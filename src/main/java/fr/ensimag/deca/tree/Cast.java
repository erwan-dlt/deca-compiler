package fr.ensimag.deca.tree;

import fr.ensimag.ima.pseudocode.instructions.INT;
import fr.ensimag.ima.pseudocode.instructions.FLOAT;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.GPRegister;
import java.io.PrintStream;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;

/**
 * Cast expression.
 */
public class Cast extends AbstractExpr {

    private AbstractIdentifier type;
    private AbstractExpr expr;

    public Cast(AbstractIdentifier type, AbstractExpr expr) {
        this.type=type;
        this.expr=expr;
    }


    @Override
    public void decompile(IndentPrintStream s) {
        s.print("(");
        type.decompile(s);
        s.print(") (");
        expr.decompile(s);
        s.print(")");
    }

    @Override
    protected String prettyPrintNode() {
        return "Cast (" + type.getName().getName() + ")";
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s, prefix, false);
        expr.prettyPrint(s, prefix, true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        type.iter(f);
        expr.iter(f);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {

        Type t1 = type.verifyType(compiler);
        Type t2 = expr.verifyExpr(compiler, localEnv, currentClass);

        if (!(t1.isInt() || t1.isFloat()) || !(t2.isInt() || t2.isFloat())) {
            throw new ContextualError("Only numeric casts are supported", getLocation());
        }
        setType(t1);
        return t1;
    }

    @Override
    public void codeGenInst(DecacCompiler compiler) {
        expr.codeGenInst(compiler);
        GPRegister reg = compiler.getRegisterManag().getLastUsedRegister();
        if (getType().isFloat() && expr.getType().isInt()) {
            compiler.addInstruction(new FLOAT(reg, reg));
        } else if (getType().isInt() && expr.getType().isFloat()) {
            compiler.addInstruction(new INT(reg, reg));
            if (!compiler.getCompilerOptions().getCheck()) {
                compiler.addInstruction(new BOV(
                        new Label("overflow_error")));
            }
        }
    }
}
