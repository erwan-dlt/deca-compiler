package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;

/**
 * Represents an abstract binary operation for boolean expressions in the Deca language.
 * 
 * This class serves as a base for logical operations like AND (&&) or OR (||).
 * It ensures that both operands are of boolean type during the contextual verification phase
 * and sets the resulting type to boolean.
 * 
 * @author gl49
 * @date 01/01/2025
 */
public abstract class AbstractOpBool extends AbstractBinaryExpr {

    public AbstractOpBool(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {

        Type t1 = this.getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        Type t2 = this.getRightOperand().verifyExpr(compiler, localEnv, currentClass);
        
        if ((!t1.isBoolean()) || (!t2.isBoolean())){
            throw new ContextualError("Un ou les deux cotés n'est pas un bool", this.getLocation());
        }
        setType(compiler.environmentType.BOOLEAN);
        return compiler.environmentType.BOOLEAN;
    }

}
