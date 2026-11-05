package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.*;
import org.apache.log4j.Logger;

/**
 * Arithmetic binary operations (+, -, /, ...)
 * 
 * @author gl49
 * @date 01/01/2025
 */
public abstract class AbstractOpArith extends AbstractBinaryExpr {
    private static final Logger LOG = Logger.getLogger(AbstractOpArith.class);

    public AbstractOpArith(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
    Type leftType = this.getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
    Type rightType = this.getRightOperand().verifyExpr(compiler, localEnv, currentClass);

    if (leftType.isInt() && rightType.isInt()) {
        setType(compiler.environmentType.INT);
        return compiler.environmentType.INT;
    }

    if (leftType.isFloat() && rightType.isFloat()) {
        setType(compiler.environmentType.FLOAT);
        return compiler.environmentType.FLOAT;
    }

    if (leftType.isInt() && rightType.isFloat()) {
        ConvFloat convLeft = new ConvFloat(this.getLeftOperand());
        convLeft.setLocation(this.getLeftOperand().getLocation()); 
        convLeft.verifyExpr(compiler, localEnv, currentClass);    
        this.setLeftOperand(convLeft);                           

        setType(compiler.environmentType.FLOAT);
        return compiler.environmentType.FLOAT;
    }

    if (leftType.isFloat() && rightType.isInt()) {
        ConvFloat convRight = new ConvFloat(this.getRightOperand());
        convRight.setLocation(this.getRightOperand().getLocation()); 
        convRight.verifyExpr(compiler, localEnv, currentClass);      
        this.setRightOperand(convRight);                             

        setType(compiler.environmentType.FLOAT);
        return compiler.environmentType.FLOAT;
    }

    throw new ContextualError(
        "Incompatible types for arithmetic operation: " + leftType + " and " + rightType,
        getLocation()
    );
    }

    @Override
    public void codeGenPrint(DecacCompiler compiler) {
        codeGenInst(compiler);
        compiler.addInstruction(new LOAD(compiler.getRegisterManag().getLastUsedRegister(), Register.R1));

        if (getType().isInt()) {
            compiler.addInstruction(new WINT());
        } else if (getType().isFloat()) {
            compiler.addInstruction(new WFLOAT());
        }
        compiler.getRegisterManag().freeLastUsedRegister();
    }

    @Override
    public void codeGenInst(DecacCompiler compiler) {
        getLeftOperand().codeGenInst(compiler);
    }
}
