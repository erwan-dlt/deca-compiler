package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;

/**
 * Represents an "e1 instanceof SomeClass" expression in Deca.
 */
public class InstanceOf extends AbstractExpr {

    private AbstractExpr expression;
    private AbstractIdentifier classType;

    public InstanceOf(AbstractExpr expression, AbstractIdentifier classType) {
        this.expression = expression;
        this.classType = classType;
    }

    @Override
    protected String prettyPrintNode() {
        return "InstanceOf";
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        expression.prettyPrint(s, prefix + "  ", false);
        classType.prettyPrint(s, prefix + "  ", true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        expression.iter(f);
        classType.iter(f);
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("(");
        expression.decompile(s);
        s.print(" instanceof ");
        classType.decompile(s);
        s.print(")");
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler,
                           EnvironmentExp localEnv,
                           ClassDefinition currentClass)
            throws ContextualError {
        throw new UnsupportedOperationException("Code generation for instanceof not implemented yet.");
    }

    @Override
    protected void codeGenInst(fr.ensimag.deca.DecacCompiler compiler) {
        throw new UnsupportedOperationException("Code generation for instanceof not implemented yet.");
    }
}
