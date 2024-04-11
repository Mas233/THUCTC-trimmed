package org.thunlp.text.classifiers;

import org.thunlp.io.TextFileReader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class BasicTextClassifier {

    /**
     * 词典大小
     */
    private int lexiconSize = 0;
    /**
     * 返回的可能分类数目
     */
    protected int resultNum = 1;
    /**
     * 分类器接口
     */
    protected TextClassifier classifier;
    /**
     * 类别列表
     */
    protected final ArrayList<String> categoryList = new ArrayList<>();


    /**
     * 文件编码
     */
    protected String encoding = "utf-8";

    /**
     * 分类编号索引
     */
    protected Hashtable<String, Integer> categoryToInt = new Hashtable<>();


    public int getLexiconSize() {
        return lexiconSize;
    }

    public TextClassifier getTextClassifier() {
        return classifier;
    }

    public void setTextClassifier(TextClassifier tc) {
        classifier = tc;
    }

    public String getCategoryName(int id) {
        return categoryList.get(id);
    }

    public int getCategorySize() {
        return categoryList.size();
    }

    public List<String> getCategoryList() {
        return categoryList;
    }

    /**
     * 从文件中获取分类列表
     */
    public boolean loadCategoryListFromFile(String filePath) {
        File f;
        if (filePath == null || !(f = new File(filePath)).exists() || !f.isFile()) {
            System.err.println("load categoryListFromFile failed");
            return false;
        }
        categoryList.clear();
        String s;
        TextFileReader tfr;
        try {
            tfr = new TextFileReader(filePath, encoding);
            while ((s = tfr.readLine()) != null) {
                categoryList.add(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        categoryToInt.clear();
        for (int i = 0; i < categoryList.size(); ++i) {
            categoryToInt.put(categoryList.get(i), i);
        }

        return true;
    }


    /**
     * 对一个文本进行分类，返回前topN个分类结果
     */
    public ClassifyResult[] classifyText(String text, int topN) {
        if (topN > categoryList.size()) {
            topN = categoryList.size();
        }
        return classifier.classify(text, topN);
    }

}
