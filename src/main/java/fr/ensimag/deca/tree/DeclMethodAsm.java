package fr.ensimag.deca.tree;

import java.io.PrintStream;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;

/**
 * Concrete class for ASM method declaration.
 *
 * @author gl49
 * @date 01/01/2025
 */
public class DeclMethodAsm extends AbstractDeclMethod {

    private final AbstractIdentifier returnType;
    private final AbstractIdentifier methodName;
    private final ListDeclParam params;
    private final String code;

    public DeclMethodAsm(AbstractIdentifier returnType, AbstractIdentifier methodName, ListDeclParam params, String codeAsm) {
        this.returnType = returnType;
        this.methodName = methodName;
        this.params = params;
        this.code = codeAsm;
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        if (returnType != null) {
            returnType.iter(f);
        }
        if (methodName != null) {
            methodName.iter(f);
        }
        if (params != null) {
            params.iter(f);
        }
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        if (returnType != null) {
            returnType.prettyPrint(s, prefix, false);
        }
        if (methodName != null) {
            methodName.prettyPrint(s, prefix, false);
        }
        if (params != null) {
            params.prettyPrint(s, prefix, true);
        }
        if (code != null) {
            s.println(prefix + "└── ASM Code: " + code);
        }
    }


    @Override
    String prettyPrintNode() {
        String methodNameStr = (methodName != null) ? methodName.getName().getName() : "undefined";
        return "DeclMethodAsm (name=" + methodNameStr + ", asmCode=\"" + code + "\")";
    }

    @Override
    public void decompile(IndentPrintStream s) {
        returnType.decompile(s);
        s.print(" ");
        methodName.decompile(s);
        s.print("(");
        params.decompile(s);
        s.print(") asm(\"");
        s.print(code);
        s.println("\");");
    }

    @Override
    protected void verifyMethodSignature(DecacCompiler compiler, ClassDefinition currentClass, EnvironmentExp localEnv) throws ContextualError {
        throw new UnsupportedOperationException("verifyMethodSignature not yet implemented");
    }

    @Override
    protected void verifyMethodBody(DecacCompiler compiler, ClassDefinition currentClass, EnvironmentExp localEnv) throws ContextualError {
        throw new UnsupportedOperationException("verifyMethodBody not yet implemented");
    }

    @Override
    public void codeGenMethod(DecacCompiler compiler, ClassDefinition currentClass) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

}

