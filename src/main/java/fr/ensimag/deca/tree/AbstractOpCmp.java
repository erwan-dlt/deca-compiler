package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.*;


/**
 *
 * @author gl49
 * @date 01/01/2025
 */
public abstract class AbstractOpCmp extends AbstractBinaryExpr {

    public AbstractOpCmp(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type leftType = this.getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        Type rightType = this.getRightOperand().verifyExpr(compiler, localEnv, currentClass);
    
        if (leftType.isInt() && rightType.isInt()) {
            setType(compiler.environmentType.BOOLEAN);
            return compiler.environmentType.BOOLEAN;
        }
    
        if (leftType.isFloat() && rightType.isFloat()) {
            setType(compiler.environmentType.BOOLEAN);
            return compiler.environmentType.BOOLEAN;
        }
    
        if (leftType.isInt() && rightType.isFloat()) {
            ConvFloat convLeft = new ConvFloat(this.getLeftOperand());
            convLeft.setLocation(this.getLeftOperand().getLocation()); 
            convLeft.verifyExpr(compiler, localEnv, currentClass);    
            this.setLeftOperand(convLeft);                           
    
            setType(compiler.environmentType.BOOLEAN);
            return compiler.environmentType.BOOLEAN;
        }
    
        if (leftType.isFloat() && rightType.isInt()) {
            ConvFloat convRight = new ConvFloat(this.getRightOperand());
            convRight.setLocation(this.getRightOperand().getLocation()); 
            convRight.verifyExpr(compiler, localEnv, currentClass);      
            this.setRightOperand(convRight);                             
    
            setType(compiler.environmentType.BOOLEAN);
            return compiler.environmentType.BOOLEAN;
        }

        else {
            if (leftType.isBoolean() && rightType.isBoolean()){
                if (this.getOperatorName().equals("==") || this.getOperatorName().equals("!=")){
                    setType(compiler.environmentType.BOOLEAN);
                    return compiler.environmentType.BOOLEAN;
                }
                throw new ContextualError(
                    "Incompatible comparison operator for type boolean",
                    getLocation()
                );
            }
        }
    
        throw new ContextualError(
            "Incompatible types for comparison operation: " + leftType + " and " + rightType,
            getLocation()
        );
    
        }

    @Override
    public void codeGenInst(DecacCompiler compiler) {
        int numberCmp = compiler.getLabelManager().counterCmp();
        Label cmpLabel = new Label("cmp_label" + numberCmp);
        Label cmpEnd = new Label("cmp_end" + numberCmp);

        if (getRightOperand() instanceof IntLiteral) {
            getLeftOperand().codeGenInst(compiler);
            compiler.addInstruction(new CMP(((IntLiteral) getRightOperand()).getValue(), compiler.getRegisterManag().getLastUsedRegister()));

        } else {
            getLeftOperand().codeGenInst(compiler);
            GPRegister leftRegister = compiler.getRegisterManag().getLastUsedRegister();
            getRightOperand().codeGenInst(compiler);
            compiler.addInstruction(new CMP(compiler.getRegisterManag().getLastUsedRegister(), leftRegister));

            compiler.getRegisterManag().freeLastUsedRegister();
            compiler.getRegisterManag().setLastUsedRegister(leftRegister);
        }

        switch (getOperatorName()) {
            case "==":
                compiler.addInstruction(new BEQ(cmpLabel));
                break;
            case "!=":
                compiler.addInstruction(new BNE(cmpLabel));
                break;
            case ">=":
                compiler.addInstruction(new BGE(cmpLabel));
                break;
            case "<=":
                compiler.addInstruction(new BLE(cmpLabel));
                break;
            case ">":
                compiler.addInstruction(new BGT(cmpLabel));
                break;
            case "<":
                compiler.addInstruction(new BLT(cmpLabel));
                break;
            default:
                throw new UnsupportedOperationException("Unsupported operator: " + getOperatorName());
        }
        compiler.addInstruction(new LOAD(0, compiler.getRegisterManag().getLastUsedRegister()));
        compiler.addInstruction(new BRA(cmpEnd));

        compiler.addLabel(cmpLabel);
        compiler.addInstruction(new LOAD(1, compiler.getRegisterManag().getLastUsedRegister()));
        compiler.addLabel(cmpEnd);
    }
}
