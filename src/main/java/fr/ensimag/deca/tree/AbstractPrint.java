package fr.ensimag.deca.tree;

import fr.ensimag.ima.pseudocode.instructions.WFLOATX;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.Register;
import java.io.PrintStream;
import java.util.Iterator;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;

/**
 * Print statement (print, println, ...).
 *
 * @author gl49
 * @date 01/01/2025
 */
public abstract class AbstractPrint extends AbstractInst {
    private static final Logger LOG = Logger.getLogger(AbstractExpr.class);

    private boolean printHex;
    private ListExpr arguments = new ListExpr();

    abstract String getSuffix();

    public AbstractPrint(boolean printHex, ListExpr arguments) {
        Validate.notNull(arguments);
        this.arguments = arguments;
        this.printHex = printHex;
    }

    public ListExpr getArguments() {
        return arguments;
    }

    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType)
            throws ContextualError {
        Iterator<AbstractExpr> iterator = this.arguments.iterator();
        while (iterator.hasNext()) {

                Type t = iterator.next().verifyExpr(compiler, localEnv, currentClass);
                if ((!t.isInt())&& (!t.isFloat()) && (!t.isString())){
                    throw new ContextualError(  "Invalid argument type: " + t + ". Expected Int, Float, or String.", this.getLocation());
                }
        }
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        for (AbstractExpr a : getArguments().getList()) {
            if (printHex && a.getType().isFloat()) {
                a.codeGenInst(compiler);
                compiler.addInstruction(new LOAD(
                        compiler.getRegisterManag().getLastUsedRegister(), Register.R1));
                compiler.addInstruction(new WFLOATX());
                compiler.getRegisterManag().freeLastUsedRegister();
            } else {
                a.codeGenPrint(compiler);
            }
        }
    }

    private boolean getPrintHex() {
        return printHex;
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("print" + getSuffix());
        if (getPrintHex()) {
            s.print("x");
        }
        s.print("(");
        arguments.decompile(s);
        s.print(");");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        arguments.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        arguments.prettyPrint(s, prefix, true);
    }

}
