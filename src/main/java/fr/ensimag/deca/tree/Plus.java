package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.ImmediateFloat;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.ADD;
import fr.ensimag.ima.pseudocode.instructions.BOV;

/**
 * Addition
 *
 * @author gl49
 * @date 01/01/2025
 */
public class Plus extends AbstractOpArith {

    public Plus(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


    @Override
    protected String getOperatorName() {
        return "+";
    }

    @Override
    public void codeGenInst(DecacCompiler compiler) {
        super.codeGenInst(compiler);
        GPRegister leftRegister = compiler.getRegisterManag().getLastUsedRegister();
        Label overflow = new Label("overflow_error");

        if (getRightOperand() instanceof IntLiteral) {
            compiler.addInstruction(new ADD(new ImmediateInteger(((IntLiteral) getRightOperand()).getValue()), leftRegister));
        } else if (getRightOperand() instanceof FloatLiteral) {
            compiler.addInstruction(new ADD(new ImmediateFloat(((FloatLiteral) getRightOperand()).getValue()), leftRegister));
        } else {
            getRightOperand().codeGenInst(compiler);
            GPRegister rightRegister = compiler.getRegisterManag().getLastUsedRegister();
            compiler.addInstruction(new ADD(rightRegister, leftRegister));

            compiler.getRegisterManag().freeLastUsedRegister();
            compiler.getRegisterManag().setLastUsedRegister(leftRegister);
        }
         if (super.getType().isFloat() && !compiler.getCompilerOptions().getCheck()) {
            compiler.addInstruction(new BOV(overflow));
        }
    }
}
