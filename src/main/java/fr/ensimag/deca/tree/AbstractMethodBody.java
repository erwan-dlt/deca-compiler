package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.tools.IndentPrintStream;

import java.io.PrintStream;

/**
 * Abstract class representing the body of a method in the Deca compiler.
 *
 * @author gl49
 * @date 01/01/2025
 */
public abstract class AbstractMethodBody extends Tree {
    
    /**
     * Pass 3 of [SyntaxeContextuelle]. Verify that instructions and expressions
     * in the method body are correct.
     */
    protected abstract void verifyMethodBody(DecacCompiler compiler) throws ContextualError;
    
    /**
     * Generate assembly code for the method body.
     */
    protected abstract void codeGenMethodBody(DecacCompiler compiler);
    
    /**
     * Decompile the method body.
     */
    public abstract void decompile(IndentPrintStream s);
    
    @Override
    protected abstract void prettyPrintChildren(PrintStream s, String prefix);
    
    @Override
    protected abstract void iterChildren(TreeFunction f);
}
