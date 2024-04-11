package org.thunlp.text.classifiers;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import org.apache.commons.codec.binary.Base64;
import org.thunlp.language.chinese.WordSegment;
import org.thunlp.text.*;
import org.thunlp.text.Lexicon.Word;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public abstract class AbstractTextClassifier implements TextClassifier {
    /**
     * 词典
     */
    public Lexicon lexicon;
    /**
     * 用来构造训练特征向量
     */
    private DocumentVector trainingVectorBuilder;
    /**
     * 用来构造待分类文本的特征向量
     */
    private DocumentVector testVectorBuilder;
    private WordSegment seg;
    /**
     * 训练好的模型
     */
    private svm_model model;
    /**
     * 默认的最大特征数
     */
    private int maxFeatures = 5000;
    /**
     * 类别数
     */
    private int nclasses;
    /**
     * 最长的文档向量长度，决定读取临时文件时缓冲大小
     */
    private int longestDoc;
    /**
     * 训练集的大小
     */
    private int ndocs;
    /**
     * 类别标签
     */
    public ArrayList<Integer> labelIndex = new ArrayList<Integer>();
    /**
     * 训练集的cache文件，存放在磁盘上
     */
    public File tsCacheFile;
    /**
     * 训练集的cache输出流
     */
    public DataOutputStream tsCache = null;

    public void init(int nclasses, WordSegment seg) {
        lexicon = new Lexicon();
        trainingVectorBuilder =
                new DocumentVector(lexicon, new TfOnlyTermWeighter());
        testVectorBuilder = null;
        model = null;
        this.nclasses = nclasses;
        ndocs = 0;
        this.seg = seg;

    }

    abstract protected WordSegment initWordSegment();

    public AbstractTextClassifier(int nclasses) {
        init(nclasses, initWordSegment());
    }

    /**
     * 初始化一个基于bigram和svm的中文文本分类器
     *
     * @param nclasses 类别数
     */
    public AbstractTextClassifier(int nclasses, WordSegment seg) {
        init(nclasses, seg);
    }

    /**
     * 返回字典
     */
    public Lexicon getLexicon() {
        return lexicon;
    }

    /**
     * 分类一篇文档
     *
     * @param text 待分类文档
     * @return 分类结果，其中包含分类标签和概率，对于svm分类器，概率无意义
     */
    public ClassifyResult classify(String text) {
        String[] bigrams = seg.segment(text);
        Word[] words = lexicon.convertDocument(bigrams);

        Term[] terms = testVectorBuilder.build(words, true);

        int m = terms.length;
        svm_node[] x = new svm_node[m];
        for (int j = 0; j < m; j++) {
            x[j] = new svm_node();
            x[j].index = terms[j].id + 1;
            x[j].value = terms[j].weight;
        }

        ClassifyResult cr = new ClassifyResult(-1, 0.0);
        double[] probs = new double[svm.svm_get_nr_class(model)];

        svm.svm_predict_probability(model, x, probs);
        for (int i = 0; i < probs.length; i++) {
            if (probs[i] > cr.prob) {
                cr.prob = probs[i];
                cr.label = i;
            }
        }
        return cr;
    }

    public ClassifyResult[] classify(String text, int topN) {

        String[] bigrams = seg.segment(text);
        Word[] words = lexicon.convertDocument(bigrams);

        Term[] terms = testVectorBuilder.build(words, true);

        int m = terms.length;
        svm_node[] x = new svm_node[m];
        for (int j = 0; j < m; j++) {
            x[j] = new svm_node();
            x[j].index = terms[j].id + 1;
            x[j].value = terms[j].weight;
        }

        ArrayList<ClassifyResult> cr = new ArrayList<ClassifyResult>();
        double[] probs = new double[svm.svm_get_nr_class(model)];
        svm.svm_predict_probability(model, x, probs);

        for (int i = 0; i < probs.length; i++) {
            cr.add(new ClassifyResult(i, probs[i]));
        }

        Comparator com = new Comparator() {
            public int compare(Object obj1, Object obj2) {
                ClassifyResult o1 = (ClassifyResult) obj1;
                ClassifyResult o2 = (ClassifyResult) obj2;
                if (o1.prob > o2.prob + 1e-20) return -1;
                else if (o1.prob < o2.prob - 1e-20) return 1;
                else return 0;
            }
        };

        Collections.sort(cr, com);

        ClassifyResult[] result = new ClassifyResult[Math.min(topN, probs.length)];
        for (int i = 0; i < result.length; i++) {
            result[i] = new ClassifyResult(cr.get(i).label, cr.get(i).prob);
        }
        cr.clear();
        return result;
    }

    /**
     * 从磁盘上加载训练好的模型
     *
     * @param fis 模型文件名
     * @return 加载是否成功
     */
    public boolean loadModel(String fis) {
        File modelPath = new File(fis);
        if (!modelPath.isDirectory())
            return false;

        File lexiconFile = new File(modelPath, "lexicon");
        File modelFile = new File(modelPath, "model");

        try {
            if (lexiconFile.exists()) {
                lexicon.loadFromFile(lexiconFile);
            } else {
                return false;
            }

            if (modelFile.exists()) {
                this.model = svm.svm_load_model(modelFile.getAbsolutePath());
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        lexicon.setLock(true);
        trainingVectorBuilder = null;
        testVectorBuilder =
                new DocumentVector(lexicon, new TfIdfTermWeighter(lexicon));
        return true;
    }

    private static class DataNode implements Comparable {
        int label;
        svm_node[] nodes;

        public int compareTo(Object o) {
            DataNode other = (DataNode) o;
            return label - other.label;
        }
    }

    public void loadFromString(String model) {
        ByteArrayInputStream bais =
                new ByteArrayInputStream(Base64.decodeBase64(model.getBytes()));
        ObjectInputStream ois;
        try {
            ois = new ObjectInputStream(bais);
            this.lexicon = (Lexicon) ois.readObject();
            this.model = (svm_model) ois.readObject();
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        testVectorBuilder =
                new DocumentVector(lexicon, new TfIdfTermWeighter(lexicon));
    }
}
