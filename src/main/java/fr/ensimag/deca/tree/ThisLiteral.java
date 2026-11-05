package fr.ensimag.deca.tree;

import java.io.PrintStream;

import org.apache.log4j.Logger;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;

/**
 * 'this' keyword.
 */
public class ThisLiteral extends AbstractExpr {
    private static final Logger LOG = Logger.getLogger(AbstractExpr.class);    

    @Override
    String prettyPrintNode() {
        return "This";
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("this");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // leaf node => nothing to do
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // leaf node => nothing to do
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        if (currentClass == null ){
            throw new ContextualError("cannot call this in main",this.getLocation());
        }
        ClassType t = currentClass.getType();
        this.setType(t);
        return t;
    }

    @Override
    public void codeGenInst(DecacCompiler compiler) {
        throw new UnsupportedOperationException("not yet implemented");   
    }

}
