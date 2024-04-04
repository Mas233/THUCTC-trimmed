package org.thunlp;

import org.thunlp.text.classifiers.BasicTextClassifier;
import org.thunlp.text.classifiers.LinearBigramChineseTextClassifier;

import java.util.Arrays;
import java.util.List;

/**
 * 基于THUCTC的中文新闻分类器
 *
 * @author wang mingsong
 * @date 2024.04.04
 */
public class NewsClassifier {
    public static final String MODEL_PATH = "news_model";
    public static final String CATEGORY_PATH = "category";
    private BasicTextClassifier classifier = null;

    public NewsClassifier() {
        setClassifier();
    }

    /**
     * 返回新闻的topN分类结果
     *
     * @param text 新闻文本
     * @param topN topN个数
     * @return 分类结果
     */
    public NewsClassifyResult[] topNClassify(String text, int topN) {
        return Arrays.stream(classifier.classifyText(text, topN)).map(
                result -> new NewsClassifyResult(classifier.getCategoryName(result.label), result.prob)
        ).toArray(NewsClassifyResult[]::new);
    }

    /**
     * 获取新闻分类
     *
     * @return 分类列表
     */
    public List<String> getCategoryList() {
        return classifier.getCategoryList();
    }

    /**
     * 设置分类器
     */
    private void setClassifier() {
        if (this.classifier == null) {
            this.classifier = new BasicTextClassifier();
            String modelPath = NewsClassifier.class.getClassLoader().getResource(MODEL_PATH).getPath();

            // 设置分类种类，并读取模型
            classifier.loadCategoryListFromFile(modelPath + CATEGORY_PATH);
            classifier.setTextClassifier(new LinearBigramChineseTextClassifier(classifier.getCategorySize()));
            classifier.getTextClassifier().loadModel(modelPath);
        }
    }

    /**
     * 新闻分类结果类
     */
    public class NewsClassifyResult {
        private final String category;
        private final double prob;

        public NewsClassifyResult(String category, double prob) {
            this.category = category;
            this.prob = prob;
        }

        public String getCategory() {
            return category;
        }

        public double getProb() {
            return prob;
        }
    }
}
