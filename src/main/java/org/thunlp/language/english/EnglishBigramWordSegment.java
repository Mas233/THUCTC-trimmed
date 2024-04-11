package org.thunlp.language.english;

import org.thunlp.language.chinese.WordSegment;

import java.util.LinkedList;

public class EnglishBigramWordSegment implements WordSegment {

    private final LinkedList<String> results;

    private final boolean withSpaceInBigram;

    public EnglishBigramWordSegment() {
        this(false);
    }

    public EnglishBigramWordSegment(boolean b) {
        results = new LinkedList<String>();
        withSpaceInBigram = b;
    }

    public boolean outputPosTag() {
        return false;
    }

    public String[] segment(String text) {
        EnglishWordSegment seg = new EnglishWordSegment();
        String[] tokens = seg.segment(text);
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
