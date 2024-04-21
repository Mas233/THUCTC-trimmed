package org.thunlp.text.classifiers;

import org.thunlp.text.Lexicon;

import java.io.File;

/**
 * 裁剪后的文本分类器接口，支持读入模型和分类。
 *
 * @author adam
 */
public interface TextClassifier {


    /**
     * 从文件中载入之前训练好的模型
     *
     * @param filename 模型文件
     */
    boolean loadModel(String filename);

    /**
     * 从文件中载入之前训练好的模型
     *
     * @param lexicon 字典文件
     * @param model   模型文件
     * @return 是否载入成功
     */
    boolean loadModel(File lexicon, File model);

    /**
     * 对新文本进行分类
     *
     * @param text 待分类文本
     * @return 文本的类别和分类概率
     */
    ClassifyResult classify(String text);

    /**
     * 对新文本进行分类
     *
     * @param text 待分类文本
     * @param topN 前N个结果候选
     * @return 文本的类别和分类概率
     */
    ClassifyResult[] classify(String text, int topN);


    /**
     * 将一个字符串还原为一个模型
     */
    void loadFromString(String model);

    /**
     * 返回训练字典
     *
     * @return 字典
     */
    Lexicon getLexicon();
}
