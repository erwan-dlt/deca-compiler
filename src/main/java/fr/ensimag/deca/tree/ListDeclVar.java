package fr.ensimag.deca.tree;

import java.util.Iterator;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
/**
 * List of declarations (e.g. int x; float y,z).
 *
 * @author gl49
 * @date 01/01/2025
 */
public class ListDeclVar extends TreeList<AbstractDeclVar> {

    @Override
    public void decompile(IndentPrintStream s) {
        int size = getList().size();
        int index = 0;
        for (AbstractDeclVar i : getList()) {
            i.decompile(s);
            index++;
            if (index < size) {
                s.println();
            }
        }
    }

    void verifyListDeclVariable(DecacCompiler compiler, EnvironmentExp localEnv,
        ClassDefinition currentClass) throws ContextualError {
    Iterator<AbstractDeclVar> iterator = this.iterator();
    while (iterator.hasNext()){
        AbstractDeclVar declVar = iterator.next();
        declVar.verifyDeclVar(compiler, localEnv, currentClass);
        }
    }

    public void codeGenListDeclVar(DecacCompiler compiler) {
        for (AbstractDeclVar p : getList()) {
            p.codeGenDeclVar(compiler);
        }
    }
}
