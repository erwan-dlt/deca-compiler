package fr.ensimag.deca.tree;

import java.io.PrintStream;

import org.apache.log4j.Logger;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.context.ParamDefinition;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;

/**
 * Declaration of a method parameter.
 *
 * @author gl49
 * @date 01/01/2025
 */
public class DeclParam extends AbstractDeclParam {
    private static final Logger LOG = Logger.getLogger(AbstractExpr.class);
    private final AbstractIdentifier type;
    private final AbstractIdentifier name;

    public DeclParam(AbstractIdentifier type, AbstractIdentifier name) {
        if (type == null || name == null) {
            throw new IllegalArgumentException("Type and name must not be null");
        }
        this.type = type;
        this.name = name;
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        type.iter(f);
        name.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        s.println(prefix + "Type: " + type.getName().getName());
        s.println(prefix + "Name: " + name.getName().getName());
    }

    @Override
    String prettyPrintNode() {
        return "DeclParam (" + type.getName().getName() + " " + name.getName().getName() + ")";
    }

    @Override
    public void decompile(IndentPrintStream s) {
        type.decompile(s);
        s.print(" ");
        name.decompile(s);
    }

    @Override
    protected void verifyParam(DecacCompiler compiler, ClassDefinition currentClass, EnvironmentExp localEnv, MethodDefinition methodDef) throws ContextualError {

        Type paramType = type.verifyType(compiler);
        if (paramType == null) {
            throw new ContextualError("Undefined type for parameter.", type.getLocation());
        }

        if (paramType.isVoid()) {
            throw new ContextualError("Parameter cannot have type void.", type.getLocation());
        }

        try {
            ParamDefinition paramDefinition = new ParamDefinition(paramType, name.getLocation());
            if (methodDef != null) {
                methodDef.getSignature().add(paramType);
            }
            localEnv.declare(""+name.getName(), paramDefinition);
            name.setDefinition(paramDefinition);
        } catch (EnvironmentExp.DoubleDefException e) {
            throw new ContextualError("Duplicate parameter name: " + name.getName(), name.getLocation());
        }
    }

    @Override
    protected void verifyClassMembers(DecacCompiler compiler) throws ContextualError {

    }

    @Override
    protected void verifyParamConstraints(DecacCompiler compiler) throws ContextualError {

    }

    @Override
    public void codeGenParam(DecacCompiler compiler) {
        //TODO
    }


}
