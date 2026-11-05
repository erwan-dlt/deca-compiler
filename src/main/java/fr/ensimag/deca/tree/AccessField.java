package fr.ensimag.deca.tree;

import java.io.PrintStream;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.tools.IndentPrintStream;

/**
 * Represents the access to a field (obj.field) in Deca.
 */
public class AccessField extends AbstractLValue {
    private AbstractExpr objectExpr; // The object or class on the left side of the dot
    private AbstractIdentifier fieldName; // The field being accessed

    public AccessField(AbstractExpr objectExpr, AbstractIdentifier fieldName) {
        this.objectExpr = objectExpr;
        this.fieldName = fieldName;
    }

    public AbstractExpr getObjectExpr() {
        return objectExpr;
    }

    public AbstractIdentifier getFieldName() {
        return fieldName;
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        objectExpr.prettyPrint(s, prefix, false); // Affiche l'objet sur la même ligne
        fieldName.prettyPrint(s, prefix, true); // Affiche l'attribut sur la même ligne
    }


    @Override
    protected void iterChildren(TreeFunction f) {
        objectExpr.iter(f);
        fieldName.iter(f);
    }

    @Override
    protected String prettyPrintNode() {
        return "AccessField (" + fieldName.getName() + ")";
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print(objectExpr.decompile() + "." + fieldName.decompile());
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError {
        throw new UnsupportedOperationException("Verification of field access is not yet implemented.");
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        throw new UnsupportedOperationException("Code generation for field access is not yet implemented.");
    }

    @Override
    protected void codeGenPrint(DecacCompiler compiler) {
        throw new UnsupportedOperationException("Code generation for field printing is not yet implemented.");
    }
}
