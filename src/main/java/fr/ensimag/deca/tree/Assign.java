package fr.ensimag.deca.tree;


import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.deca.context.VariableDefinition;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.ima.pseudocode.instructions.STORE;

/**
 * Assignment, i.e. lvalue = expr.
 *
 * @author gl49
 * @date 01/01/2025
 */
public class Assign extends AbstractBinaryExpr {

    @Override
    public AbstractLValue getLeftOperand() {
        // The cast succeeds by construction, as the leftOperand has been set
        // as an AbstractLValue by the constructor.
        return (AbstractLValue)super.getLeftOperand();
    }

    public Assign(AbstractLValue leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type leftType = this.getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        Type rightType = this.getRightOperand().verifyExpr(compiler, localEnv, currentClass);

        if (leftType.isFloat() && rightType.isInt()) {
            ConvFloat convRight = new ConvFloat(this.getRightOperand());
            convRight.setLocation(this.getRightOperand().getLocation());
            convRight.verifyExpr(compiler, localEnv, currentClass);
            this.setRightOperand(convRight);

            setType(leftType);
            return leftType;
        }

        else if (!rightType.sameType(leftType)) {
            throw new ContextualError("Type mismatch in assignment: cannot assign " + rightType + " to " + leftType, this.getLocation());
        }

        this.setType(leftType);
        return leftType;
    }

    @Override
    protected String getOperatorName() {
        return "=";
    }

    @Override
    public void codeGenInst(DecacCompiler compiler) {
        getRightOperand().codeGenInst(compiler);

        GPRegister regLeft = compiler.getRegisterManag().getLastUsedRegister();

        AbstractLValue leftOp = getLeftOperand();
        if (leftOp instanceof Identifier) {
            Identifier ident = (Identifier) leftOp;

            if (ident.getDefinition() instanceof VariableDefinition) {
                VariableDefinition varDef = (VariableDefinition) ident.getDefinition();
                compiler.addInstruction(new STORE(regLeft, varDef.getOperand()));
            } else {
                throw new DecacInternalError("Unsupported lvalue type for assignment");
            }
        } else {
            throw new DecacInternalError("Left operand of assignment is not an lvalue");
        }
        // The assignment value remains available to enclosing expressions.
    }
}
