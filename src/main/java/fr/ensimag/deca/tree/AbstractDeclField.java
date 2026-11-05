package fr.ensimag.deca.tree;

import java.io.PrintStream;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;

/**
 * Classe abstraite pour les déclarations de champs.
 *
 * @author gl49
 * @date 01/01/2025
 */
public abstract class AbstractDeclField extends Tree {

    public abstract void setVisibility(Visibility v);

    public abstract Visibility getVisibility();

    public abstract void verifyField(DecacCompiler compiler, ClassDefinition currentClassDef) throws ContextualError;

    public abstract void verifyFieldInit(DecacCompiler compiler, ClassDefinition currentClassDef, EnvironmentExp localEnv) throws ContextualError;

    @Override
    public abstract void decompile(IndentPrintStream s);

    @Override
    protected abstract void iterChildren(TreeFunction f);

    @Override
    protected abstract void prettyPrintChildren(PrintStream s, String prefix);

    @Override
    abstract String prettyPrintNode();
}
