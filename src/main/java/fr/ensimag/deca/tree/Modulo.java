package fr.ensimag.deca.tree;

import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.ImmediateFloat;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.REM;

/**
 * Modulo expression ("x % y")
 * This operation is restricted to integer operands, as specified by the language's type system.
 *
 * @author gl49
 * @date 01/01/2025
 */
public class Modulo extends AbstractOpArith {

    public Modulo(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type leftType = this.getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        Type rightType = this.getRightOperand().verifyExpr(compiler, localEnv, currentClass);

        if (leftType.isInt() && rightType.isInt()) {
            setType(compiler.environmentType.INT);
            return compiler.environmentType.INT;
        } else{
            throw new ContextualError("Operands must be int", getLocation());
        }

    }

    @Override
    protected String getOperatorName() {
        return "%";
    }

    @Override
    public void codeGenInst(DecacCompiler compiler) {
        super.codeGenInst(compiler);
        GPRegister leftRegister = compiler.getRegisterManag().getLastUsedRegister();

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
        compiler.addInstruction(new REM(rightRegister, leftRegister));
        if (!compiler.getCompilerOptions().getCheck()) {
            compiler.addInstruction(new BOV(new Label("overflow_error")));
        }
        compiler.getRegisterManag().freeLastUsedRegister();
        compiler.getRegisterManag().setLastUsedRegister(leftRegister);
    }
}
