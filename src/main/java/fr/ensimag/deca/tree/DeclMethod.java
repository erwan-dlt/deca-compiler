package fr.ensimag.deca.tree;

import java.io.PrintStream;

import org.apache.log4j.Logger;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.context.Signature;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Label;

/**
 * Concrete class for method declaration.
 *
 * @author gl49
 * @date 01/01/2025
 */
public class DeclMethod extends AbstractDeclMethod {
    private static final Logger LOG = Logger.getLogger(AbstractExpr.class);
    
    private final AbstractIdentifier returnType;
    private final AbstractIdentifier methodName;
    private final ListDeclParam params;
    private final ListDeclVar var;
    private final ListInst inst;

    public DeclMethod(AbstractIdentifier returnType, AbstractIdentifier methodName, 
                      ListDeclParam params, ListDeclVar var, ListInst inst) {
        this.returnType = returnType;
        this.methodName = methodName;
        this.params = params;
        this.var=var;
        this.inst=inst;
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        if (returnType != null) returnType.iter(f);
        if (methodName != null) methodName.iter(f);
        if (params != null) params.iter(f);
        if (var != null) var.iter(f);
        if (inst != null) inst.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        if (returnType != null) {
            returnType.prettyPrint(s, prefix, true);
        }
        if (methodName != null) {
            methodName.prettyPrint(s, prefix, true);
        }
        if (params != null) {
            params.prettyPrint(s, prefix, true);
        }
        if (var != null) {
            var.prettyPrint(s, prefix, true);
        }
        if (inst != null) {
            inst.prettyPrint(s, prefix, true);
        }
    }

    @Override
    String prettyPrintNode() {
        return "DeclMethod (name=" + methodName.getName() + ", returnType=" + returnType.getName() + ")";
    }

    @Override
    protected void verifyMethodSignature(DecacCompiler compiler, ClassDefinition currentClass, EnvironmentExp localEnv) throws ContextualError {

        Type t = this.returnType.verifyType(compiler);


        ExpDefinition existingDef = localEnv.get(""+methodName.getName());
        MethodDefinition methodDef;

        if (existingDef != null) {
            if (!existingDef.isMethod()) {
                throw new ContextualError("The name " + methodName.getName() + " is already used by a non-method element.", methodName.getLocation());
            }

            MethodDefinition superMethodDef = (MethodDefinition) existingDef;
            if (!superMethodDef.getType().sameType(t)) {
                throw new ContextualError("Return type mismatch for method " + methodName.getName(), methodName.getLocation());
            }
            methodDef = new MethodDefinition(t, methodName.getLocation(), superMethodDef.getSignature(), superMethodDef.getIndex());
        } else {

            int methodIndex = currentClass.incNumberOfMethods();
            methodDef = new MethodDefinition(t, methodName.getLocation(), new Signature(), methodIndex);
        }

        methodName.setDefinition(methodDef);
        try {
            localEnv.declare(""+methodName.getName(), methodDef);
        } catch (EnvironmentExp.DoubleDefException e) {
            throw new ContextualError("Method " + methodName.getName() + " is already defined.", methodName.getLocation());
        }
        methodName.setDefinition(methodDef);
        compiler.environmentType.envMethods.put(""+methodName.getName(), methodDef);
        methodDef.setLabel(new Label(methodName.getName().getName() + "_" + currentClass.getType().getName().getName()));

    }

    @Override
    protected void verifyMethodBody(DecacCompiler compiler, ClassDefinition currentClass, EnvironmentExp localEnv) throws ContextualError {
        EnvironmentExp localEnv2 = new EnvironmentExp();
        this.params.verifyListParams(compiler, currentClass, localEnv2, compiler.environmentType.envMethods.get(""+methodName.getName()));
        Type methodReturnType = this.returnType.verifyType(compiler);

        if (var != null) {
            var.verifyListDeclVariable(compiler, localEnv2, currentClass);
        }

        if (inst != null) {
            inst.verifyListInst(compiler, localEnv2, currentClass, methodReturnType);
        }
    }


    @Override
    public void decompile(IndentPrintStream s) {
        returnType.decompile(s);
        s.print(" ");
        methodName.decompile(s);
        s.print("(");
        params.decompile(s);
        s.println(") {");
        s.indent();
        var.decompile(s);
        inst.decompile(s);
        s.unindent();
        s.println("}");
    }

    public void codeGenMethod(DecacCompiler compiler, ClassDefinition currentClass) {
//        Label methodLabel = new Label("code." + currentClass.getType().getName() + "." + this.getName().getName());
//        compiler.addLabel(methodLabel);
//
//        this.getListDeclVar().codeGenListDeclVar(compiler);
//        this.getListInst().codeGenListInst(compiler);
//        //this.getListParam().codeGenParams(compiler);
    }
}
