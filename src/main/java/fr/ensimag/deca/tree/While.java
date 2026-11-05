package fr.ensimag.deca.tree;

import java.io.PrintStream;

import org.apache.commons.lang.Validate;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.instructions.*;
import fr.ensimag.ima.pseudocode.*;

/**
 * While instruction in the Deca language.
 *
 * @author gl49
 * @date 01/01/2025
 */
public class While extends AbstractInst {
    private AbstractExpr condition;
    private ListInst body;

    public AbstractExpr getCondition() {
        return condition;
    }

    public ListInst getBody() {
        return body;
    }

    public While(AbstractExpr condition, ListInst body) {
        Validate.notNull(condition);
        Validate.notNull(body);
        this.condition = condition;
        this.body = body;
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        int numberWhile = compiler.getLabelManager().counterWhile();
        Label startLabel = new Label("while_start" + numberWhile);
        Label conditionLabel = new Label("while_cond" + numberWhile);

        compiler.addInstruction(new BRA(conditionLabel));

        compiler.addLabel(startLabel);
        body.codeGenListInst(compiler);

        compiler.addLabel(conditionLabel);
        condition.codeGenInst(compiler);
        compiler.addInstruction(new CMP(0, compiler.getRegisterManag().getLastUsedRegister()));
        compiler.addInstruction(new BNE(startLabel));

        compiler.getRegisterManag().freeLastUsedRegister();
    }

    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType)
            throws ContextualError {

            this.condition.verifyCondition(compiler, localEnv, currentClass);
            this.body.verifyListInst(compiler, localEnv, currentClass, returnType);
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("while (");
        getCondition().decompile(s);
        s.println(") {");
        s.indent();
        getBody().decompile(s);
        s.unindent();
        s.print("}");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        condition.iter(f);
        body.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        condition.prettyPrint(s, prefix, false);
        body.prettyPrint(s, prefix, true);
    }

}
