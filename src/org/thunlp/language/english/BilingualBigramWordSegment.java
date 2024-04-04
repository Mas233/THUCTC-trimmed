package org.thunlp.language.english;

import java.util.LinkedList;

import org.thunlp.language.chinese.LangUtils;
import org.thunlp.language.chinese.WordSegment;

public class BilingualBigramWordSegment implements WordSegment {

    private LinkedList<String> results = null;

    private boolean withSpaceInBigram;

    public BilingualBigramWordSegment() {
        this(false);
    }

    public BilingualBigramWordSegment(boolean b) {
        results = new LinkedList<String>();
        withSpaceInBigram = b;
    }

    public boolean outputPosTag() {
        return false;
    }

    public String splitChineseCharactersWithSpace(String text) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); ++i) {
            if (LangUtils.isChinese(text.codePointAt(i))) {
                if (i > 0 && sb.charAt(sb.length() - 1) != ' ')
                    sb.append(' ');
                sb.append(text.charAt(i));
                if (i + 1 < text.length())
                    sb.append(' ');
            } else {
                sb.append(text.charAt(i));
            }
        }
        return sb.toString();
    }

    public String[] segment(String text) {
        EnglishWordSegment seg = new EnglishWordSegment();
        String[] tokens = seg.segment(splitChineseCharactersWithSpace(text));
        int len = tokens.length;
        results.clear();
        if (len > 1) {
            for (int i = 0; i < len - 1; ++i)
                if (withSpaceInBigram)
                    results.add(tokens[i] + " " + tokens[i + 1]);
                else
                    results.add(tokens[i] + tokens[i + 1]);
            return results.toArray(new String[results.size()]);
        } else if (len < 1) {
            return new String[]{""};
        } else {
            return tokens;
        }
    }
}
