package fr.ensimag.deca.tree;

import java.io.PrintStream;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;

/**
 * Return statement.
 *
 * @author gl49
 * @date 22/12/2024
 */

public class Return extends AbstractInst {

    private final AbstractExpr expr;

    public Return(AbstractExpr expr){
        this.expr = expr;
    }

    @Override
    protected String prettyPrintNode() {
        return "Return";
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        if (expr != null) {
            expr.iter(f);
        }
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        if (expr != null) {
            expr.prettyPrint(s, prefix, true);
        }
    }


    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType)
            throws ContextualError {

        if (returnType == null || returnType.isVoid()){
            throw new ContextualError("Return dans une methode de type void", this.getLocation());
        }
        this.expr.verifyRValue(compiler, localEnv, currentClass, returnType);

    }

    @Override
    protected void codeGenInst(DecacCompiler compiler){}

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("return ");
        expr.decompile(s);
        s.println(";");
    }

}
