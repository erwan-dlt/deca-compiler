package fr.ensimag.deca.tree;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.codegen.*;

/**
 * List of method declarations in a class.
 *
 * @author gl49
 * @date 01/01/2025
 */
public class ListDeclMethod extends Tree {

    private final List<AbstractDeclMethod> methods;

    public ListDeclMethod() {
        this.methods = new ArrayList<>();
    }

    public void add(AbstractDeclMethod method) {
        methods.add(method);
    }

    public List<AbstractDeclMethod> getMethods() {
        return methods;
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        for (AbstractDeclMethod method : methods) {
            if (method != null) {
                method.iter(f);
            }
        }
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        for (AbstractDeclMethod method : methods) {
            if (method != null) {
                method.prettyPrint(s, prefix, false);
            }
        }
    }

    @Override
    String prettyPrintNode() {
        return "ListDeclMethod (" + methods.size() + " methods)";
    }

    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractDeclMethod member : methods) {
            member.decompile(s);
        }
    }

    public void verifyListMethods(DecacCompiler compiler, ClassDefinition currentClass, EnvironmentExp localEnv) throws ContextualError {
        for (AbstractDeclMethod method : methods) {
            method.verifyMethodSignature(compiler, currentClass, localEnv);
        }
    }


    public void verifyListMethodsBody(DecacCompiler compiler, ClassDefinition currentClass, EnvironmentExp localEnv) throws ContextualError {
        for (AbstractDeclMethod method : methods) {
            method.verifyMethodBody(compiler, currentClass, localEnv);
        }
    }

    public void codeGenMethods(DecacCompiler compiler, ClassDefinition currentClass) {
        for (AbstractDeclMethod method : getMethods()) {
            method.codeGenMethod(compiler, currentClass);
        }
    }

}
