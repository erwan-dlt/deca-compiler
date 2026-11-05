package fr.ensimag.deca.codegen;

public class LabelManager {

    private int counterWhile = 0;
    private int counterIfThenElse = 0;
    private int counterCmp = 0;
    private int counterAnd = 0;
    private int counterOr = 0;

    public int counterWhile() {
        counterWhile++;
        return counterWhile;
    }

    public int counterIfThenElse() {
        counterIfThenElse++;
        return counterIfThenElse;
    }

    public int counterCmp() {
        counterCmp++;
        return counterCmp;
    }

    public int counterAnd() {
        counterAnd++;
        return counterAnd;
    }

    public int counterOr() {
        counterOr++;
        return counterOr;
    }
}

