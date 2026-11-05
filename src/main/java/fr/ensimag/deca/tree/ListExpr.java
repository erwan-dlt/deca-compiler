package fr.ensimag.deca.tree;

import fr.ensimag.deca.tools.IndentPrintStream;

/**
 * List of expressions (eg list of parameters).
 *
 * @author gl49
 * @date 01/01/2025
 */
public class ListExpr extends TreeList<AbstractExpr> {


    @Override
    public void decompile(IndentPrintStream s) {
        int size = getList().size();
        int index = 0;
        for (AbstractExpr i : getList()) {
            i.decompile(s);
            index++;
            if (index < size) {
                s.print(", ");
            }
        }
    }
}
