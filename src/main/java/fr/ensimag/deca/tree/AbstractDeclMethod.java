package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

/**
 * Abstract class for method declarations.
 *
 * @author gl49
 * @date 01/01/2025
 */
public abstract class AbstractDeclMethod extends Tree {

    /**
     * Pass 1: Verify the method signature.
     */
    protected abstract void verifyMethodSignature(DecacCompiler compiler, ClassDefinition currentClass, EnvironmentExp localEnv)
            throws ContextualError;

    /**
     * Pass 2: Verify the method body.
     */
    protected abstract void verifyMethodBody(DecacCompiler compiler, ClassDefinition currentClass, EnvironmentExp localEnv)
            throws ContextualError;
            
    public abstract void codeGenMethod(DecacCompiler compiler, ClassDefinition currentClass);
}
