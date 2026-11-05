package fr.ensimag.deca.tree;

import java.io.PrintStream;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.tools.IndentPrintStream;

/**
 * Abstract class representing a parameter declaration in a method.
 *
 * @author gl49
 * @date 01/01/2025
 */
public abstract class AbstractDeclParam extends Tree {
    
    protected abstract void verifyParam(DecacCompiler compiler, ClassDefinition currentClass, EnvironmentExp localEnv, MethodDefinition methodDef) throws ContextualError;

    protected abstract void verifyClassMembers(DecacCompiler compiler) throws ContextualError;

    protected abstract void verifyParamConstraints(DecacCompiler compiler) throws ContextualError;

    public abstract void decompile(IndentPrintStream s);

    @Override
    protected abstract void prettyPrintChildren(PrintStream s, String prefix);

    @Override
    protected abstract void iterChildren(TreeFunction f);

    @Override
    String prettyPrintNode() {
        return "AbstractDeclParam";
    }
        
    public abstract void codeGenParam(DecacCompiler compiler);
}
