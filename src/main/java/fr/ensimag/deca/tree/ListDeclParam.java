package fr.ensimag.deca.tree;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.tools.IndentPrintStream;

/**
 * List of method parameters.
 *
 * @author gl49
 * @date 01/01/2025
 */
public class ListDeclParam extends Tree {

    private final List<AbstractDeclParam> params;

    public ListDeclParam() {
        this.params = new ArrayList<>();
    }

    public void add(AbstractDeclParam param) {
        params.add(param);
    }

    public List<AbstractDeclParam> getParams() {
        return params;
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        for (AbstractDeclParam param : params) {
            param.iter(f);
        }
    }



    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        for (int i = 0; i < params.size(); i++) {
            AbstractDeclParam param = params.get(i);
            boolean isLast = (i == params.size() - 1);
            param.prettyPrint(s, prefix, isLast);
        }
    }

    @Override
    String prettyPrintNode() {
        return "ListDeclParam (" + params.size() + " params)";
    }

    @Override
    public void decompile(IndentPrintStream s) {
        for (int i = 0; i < params.size(); i++) {
            if (i > 0) s.print(", ");
            params.get(i).decompile(s);
        }
    }

    public void verifyListParams(DecacCompiler compiler, ClassDefinition currentClass,EnvironmentExp localEnv, MethodDefinition methodDef) throws ContextualError {
        for (AbstractDeclParam param : params) {
            param.verifyParam(compiler, currentClass, localEnv, methodDef);
        }
    }

    public void codeGenParams(DecacCompiler compiler) {
        for (AbstractDeclParam param : getParams()) {
            param.codeGenParam(compiler);
        }
    }
}
