package org.thunlp.text;

import java.io.Serializable;

public abstract class TermWeighter implements Serializable {
    protected Lexicon lexicon;

    public TermWeighter(Lexicon l) {
        lexicon = l;
    }

    abstract public double weight(int id, double tf, int doclen);
}
