package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.OPP;

/**
 * Unary minus expression.
 *
 * @author gl49
 */
public class UnaryMinus extends AbstractUnaryExpr {

    /**
     * Constructs a new UnaryMinus operation node with the given operand.
     *
     * @param operand The operand for the unary minus operation.
     */
    public UnaryMinus(AbstractExpr operand) {
        super(operand);
    }

    /**
     * Verifies the type of the operand for the unary minus operation.
     * The operand must be of type {@code int} or {@code float}.
     *
     * @param compiler The compiler instance.
     * @param localEnv The local environment containing variable definitions.
     * @param currentClass The current class context, if applicable.
     * @return The type of the result, which is the same as the operand's type.
     * @throws ContextualError if the operand is not of type {@code int} or {@code float}.
     */
    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type t = this.getOperand().verifyExpr(compiler, localEnv, currentClass);
        if (t.isInt()) {
            setType(compiler.environmentType.INT);
            return compiler.environmentType.INT;
        }
        if (t.isFloat()) {
            setType(compiler.environmentType.FLOAT);
            return compiler.environmentType.FLOAT;
        }
        throw new ContextualError("Unary minus can only be applied to int or float types", getLocation());
    }

    /**
     * Returns the symbol representing the unary minus operator.
     *
     * @return The string "-".
     */
    @Override
    protected String getOperatorName() {
        return "-";
    }

    /**
     * Generates the assembly code for the unary minus operation.
     *
     * It negates the value of the operand and stores the result in a register.
     *
     * @param compiler The compiler instance.
     */
    @Override
    public void codeGenInst(DecacCompiler compiler) {
        getOperand().codeGenInst(compiler);
        GPRegister reg = compiler.getRegisterManag().getLastUsedRegister();
        compiler.addInstruction(new OPP(reg, reg));

    }
}
