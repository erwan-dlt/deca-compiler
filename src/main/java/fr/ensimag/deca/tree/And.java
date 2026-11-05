package fr.ensimag.deca.tree;

import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.LOAD;

/**
 * Represents a logical "AND" in the Deca language.
 *
 * This class models the behavior of the "AND" operator between two boolean expressions
 * as part of the abstract syntax tree (AST). It handles the short-circuit evaluation,
 * ensuring that the second operand is only evaluated if the first operand is true.
 *
 * @author gl49
 * @date 01/01/2025
 */
public class And extends AbstractOpBool {

    public And(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected String getOperatorName() {
        return "&&";
    }

    @Override
    public void codeGenInst(DecacCompiler compiler) {
        int numberAnd = compiler.getLabelManager().counterAnd();
        Label falseLabel = new Label("and_false" + numberAnd);
        Label endLabel = new Label("and_end" + numberAnd);

        getLeftOperand().codeGenInst(compiler);
        compiler.addInstruction(new CMP(0, compiler.getRegisterManag().getLastUsedRegister()));
        compiler.addInstruction(new BEQ(falseLabel));

        compiler.getRegisterManag().freeLastUsedRegister();

        getRightOperand().codeGenInst(compiler);
        compiler.addInstruction(new CMP(0, compiler.getRegisterManag().getLastUsedRegister()));
        compiler.addInstruction(new BEQ(falseLabel));
        compiler.addInstruction(new BRA(endLabel));

        compiler.addLabel(falseLabel);
        compiler.addInstruction(new LOAD(0, compiler.getRegisterManag().getLastUsedRegister()));

        compiler.addLabel(endLabel);
    }
}
