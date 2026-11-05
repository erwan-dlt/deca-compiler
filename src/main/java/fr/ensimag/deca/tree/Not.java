package fr.ensimag.deca.tree;

import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.SEQ;

/**
 * Unary expression (not).
 *
 * @author gl49
 * @date 01/01/2025
 */
public class Not extends AbstractUnaryExpr {

    public Not(AbstractExpr operand) {
        super(operand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type t = this.getOperand().verifyExpr(compiler, localEnv, currentClass);
        if (!t.isBoolean()){
            throw new ContextualError("The operand type is not boolean", getLocation());
        }
        setType(compiler.environmentType.BOOLEAN);
        return compiler.environmentType.BOOLEAN;
    }


    @Override
    protected String getOperatorName() {
        return "!";
    }
    @Override
    public void codeGenInst(DecacCompiler compiler) {
        getOperand().codeGenInst(compiler);
        GPRegister reg = compiler.getRegisterManag().getLastUsedRegister();
        compiler.addInstruction(new CMP(0, reg));
        compiler.addInstruction(new SEQ(reg));
        compiler.addInstruction(new LOAD(reg, reg));
    }
}
