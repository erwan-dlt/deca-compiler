package fr.ensimag.deca.tree;

import java.io.PrintStream;

import org.apache.commons.lang.Validate;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.*;
import org.apache.log4j.Logger;
/**
 * Full if/else if/else statement.
 *
 * @author gl49
 * @date 01/01/2025
 */
public class IfThenElse extends AbstractInst {

    private static final Logger LOG = Logger.getLogger(IfThenElse.class);
    private final AbstractExpr condition;
    private final ListInst thenBranch;
    private ListInst elseBranch;

    public IfThenElse(AbstractExpr condition, ListInst thenBranch, ListInst elseBranch) {
        Validate.notNull(condition);
        Validate.notNull(thenBranch);
        Validate.notNull(elseBranch);
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    public void setElse(ListInst elseBlock) {
        Validate.notNull(elseBlock, "The else block cannot be null");
        this.elseBranch = elseBlock;
    }

    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType)
            throws ContextualError {

        this.condition.verifyCondition(compiler, localEnv, currentClass);
        this.thenBranch.verifyListInst(compiler, localEnv, currentClass, returnType);
        this.elseBranch.verifyListInst(compiler, localEnv, currentClass, returnType);

        }


    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        int numberIf = compiler.getLabelManager().counterIfThenElse();
        Label endLabel = new Label("if_end" + numberIf);
        Label ifLabel = new Label("if_cond" + numberIf);

        condition.codeGenInst(compiler);
        compiler.addInstruction(new CMP(0, compiler.getRegisterManag().getLastUsedRegister()));
        compiler.addInstruction(new BNE(ifLabel));
        compiler.getRegisterManag().freeLastUsedRegister();

        elseBranch.codeGenListInst(compiler);
        compiler.addInstruction(new BRA(endLabel));

        compiler.addLabel(ifLabel);
        thenBranch.codeGenListInst(compiler);
        compiler.addInstruction(new BRA(endLabel));

        compiler.addLabel(endLabel);
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("if (");
        condition.decompile(s);
        s.println(") {");
        s.indent();
        thenBranch.decompile(s);
        s.unindent();
        s.print("}");
        if (!elseBranch.getList().isEmpty()) {
            AbstractInst firstElse = elseBranch.getList().get(0);
            if (firstElse instanceof IfThenElse) {
                s.print(" else ");
                firstElse.decompile(s);
            } else {
                s.println(" else {");
                s.indent();
                elseBranch.decompile(s);
                s.unindent();
                s.println("}");
            }
        }
    }

    @Override
    protected
    void iterChildren(TreeFunction f) {
        condition.iter(f);
        thenBranch.iter(f);
        elseBranch.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        condition.prettyPrint(s, prefix, false);
        thenBranch.prettyPrint(s, prefix, false);
        elseBranch.prettyPrint(s, prefix, true);
    }
}

