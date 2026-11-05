package fr.ensimag.deca.tree;

import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.ImmediateFloat;
import fr.ensimag.ima.pseudocode.instructions.*;
import fr.ensimag.ima.pseudocode.Label;
import org.apache.log4j.Logger;

/**
 * Division.
 *
 * @author gl49
 * @date 01/01/2025
 */
public class Divide extends AbstractOpArith {
    private static final Logger LOG = Logger.getLogger(Divide.class);

    public Divide(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


    @Override
    protected String getOperatorName() {
        return "/";
    }

    @Override
    public void codeGenInst(DecacCompiler compiler) {
        super.codeGenInst(compiler);
        GPRegister leftRegister = compiler.getRegisterManag().getLastUsedRegister();
        Label division_zero = new Label("division_by_zero_error");
        Label overflow = new Label("overflow_error");

        getRightOperand().codeGenInst(compiler);
        GPRegister rightRegister = compiler.getRegisterManag().getLastUsedRegister();
        if (!compiler.getCompilerOptions().getCheck()) {
            if (getType().isFloat()) {
                compiler.addInstruction(new CMP(new ImmediateFloat(0.0f), rightRegister));
            } else {
                compiler.addInstruction(new CMP(0, rightRegister));
            }
            compiler.addInstruction(new BEQ(new Label("division_by_zero_error")));
        }
        if (getType().isFloat()) {
            compiler.addInstruction(new DIV(rightRegister, leftRegister));
        } else {
            compiler.addInstruction(new QUO(rightRegister, leftRegister));
        }
        if (!compiler.getCompilerOptions().getCheck()) {
            compiler.addInstruction(new BOV(new Label("overflow_error")));
        }
        compiler.getRegisterManag().freeLastUsedRegister();
        compiler.getRegisterManag().setLastUsedRegister(leftRegister);
    }
}
