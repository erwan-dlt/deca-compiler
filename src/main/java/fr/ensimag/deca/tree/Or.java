package fr.ensimag.deca.tree;

import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BNE;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.LOAD;

/**
 * Operator ||.
 *
 * @author gl49
 * @date 01/01/2025
 */
public class Or extends AbstractOpBool {

    public Or(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected String getOperatorName() {
        return "||";
    }

    @Override
    public void codeGenInst(DecacCompiler compiler) {
        int numberOr = compiler.getLabelManager().counterOr();
        Label trueLabel = new Label("or_true" + numberOr);
        Label endLabel = new Label("or_end" + numberOr);

        getLeftOperand().codeGenInst(compiler);
        compiler.addInstruction(new CMP(0, compiler.getRegisterManag().getLastUsedRegister()));
        compiler.addInstruction(new BNE(trueLabel));

        compiler.getRegisterManag().freeLastUsedRegister();

        getRightOperand().codeGenInst(compiler);
        compiler.addInstruction(new CMP(0, compiler.getRegisterManag().getLastUsedRegister()));
        compiler.addInstruction(new BNE(trueLabel));
        compiler.addInstruction(new BRA(endLabel));

        compiler.addLabel(trueLabel);
        compiler.addInstruction(new LOAD(1, compiler.getRegisterManag().getLastUsedRegister()));

        compiler.addLabel(endLabel);
    }
}
