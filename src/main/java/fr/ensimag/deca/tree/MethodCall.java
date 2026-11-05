package fr.ensimag.deca.tree;

import java.io.PrintStream;

import org.apache.log4j.Logger;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.context.Signature;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;

/**
 * Represents a method call
 */
public class MethodCall extends AbstractExpr {
    private static final Logger LOG = Logger.getLogger(AbstractExpr.class);
    private final AbstractExpr objectExpr;
    private final AbstractIdentifier methodName;
    private final ListExpr arguments;

    public MethodCall(AbstractExpr objectExpr, AbstractIdentifier methodName, ListExpr arguments) {
        this.objectExpr = objectExpr;
        this.methodName = methodName;
        this.arguments = arguments;
    }

    @Override
    protected String prettyPrintNode() {
        return "MethodCall";
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        objectExpr.prettyPrint(s, prefix + "  ", false);
        methodName.prettyPrint(s, prefix + "  ", false);
        arguments.prettyPrint(s, prefix + "  ", true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        objectExpr.iter(f);
        methodName.iter(f);
        arguments.iter(f);
    }

    @Override
    public void decompile(IndentPrintStream s) {
        objectExpr.decompile(s);
        s.print(".");
        methodName.decompile(s);
        s.print("(");
        arguments.decompile(s);
        s.print(")");
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {

        Type objectType = objectExpr.verifyExpr(compiler, localEnv, currentClass);

        if (!objectType.isClass()) {
            throw new ContextualError("Cannot call a method on a non-class type (type " + objectType + ")", this.getLocation());
        }

        MethodDefinition methodDef = compiler.environmentType.defOfMethod(""+methodName.getName());

        if (methodDef == null) {
            throw new ContextualError("Unknown method: " + methodName.getName(), getLocation());
        }
        Signature methodSignature = methodDef.getSignature();
        if (arguments.size() != methodSignature.size()) {
            throw new ContextualError("Invalid number of arguments for method " + methodName.getName() + ". Expected "
                    + methodSignature.size() + " but got " + arguments.size() + ".", this.getLocation());
        }

        for (int i = 0; i < arguments.size(); i++) {
            AbstractExpr arg = arguments.getList().get(i);
            Type expectedType = methodSignature.paramNumber(i);
            Type actualType = arg.verifyExpr(compiler, localEnv, currentClass);

            if (actualType.isClass()) {
                if (!expectedType.isClass()) {
                    throw new ContextualError("Parameter type mismatch: expected " + expectedType + " but got " + actualType + ".", arg.getLocation());
                }

                ClassType actualClassType = (ClassType) actualType;
                if (!actualClassType.isSubClassOf((ClassType) expectedType)) {
                    throw new ContextualError("Class " + actualType + " is not a subclass of " + expectedType + ".", arg.getLocation());
                }
            } else if (!actualType.sameType(expectedType)) {
                throw new ContextualError("Parameter type mismatch: expected " + expectedType + " but got " + actualType + ".", arg.getLocation());
            }
        }

    this.setType(methodDef.getType());
    return methodDef.getType();
}


    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        throw new UnsupportedOperationException("Code generation for method calls is not yet implemented.");
    }

    @Override
    protected void codeGenPrint(DecacCompiler compiler) {
        throw new UnsupportedOperationException("Code generation for printing method calls is not yet implemented.");
    }
}
