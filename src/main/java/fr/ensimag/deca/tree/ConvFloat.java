package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.FLOAT;
import org.apache.log4j.Logger;


/**
 * Conversion of an int into a float. Used for implicit conversions.
 * 
 * @author gl49
 * @date 01/01/2025
 */
public class ConvFloat extends AbstractUnaryExpr {
    private static final Logger LOG = Logger.getLogger(ConvFloat.class);

    public ConvFloat(AbstractExpr operand) {
        super(operand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError{
        
        this.setType(compiler.environmentType.FLOAT);
        return compiler.environmentType.FLOAT;      
    }


    @Override
    protected String getOperatorName() {
        return "/* conv float */";
    }

    @Override
    public void codeGenInst(DecacCompiler compiler) {
        getOperand().codeGenInst(compiler);
        GPRegister reg = compiler.getRegisterManag().getLastUsedRegister();
        compiler.addInstruction(new FLOAT(reg, reg)); 
    }
}

