package fr.ensimag.deca.tree;

import java.io.PrintStream;

import org.apache.log4j.Logger;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.Definition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;





/**
 * Declaration of a class (<code>class name extends superClass {members}<code>).
 *
 * @author gl49
 * @date 01/01/2025
 */
public class DeclClass extends AbstractDeclClass {
    private static final Logger LOG = Logger.getLogger(DecacCompiler.class);

    private final AbstractIdentifier name;
    private final AbstractIdentifier superClass;
    private final ListDeclField fields;
    private final ListDeclMethod methods;

    public DeclClass(AbstractIdentifier name, AbstractIdentifier superClass, ListDeclField fields, ListDeclMethod methods) {
        this.name = name;
        this.superClass = superClass;
        this.fields = fields;
        this.methods = methods;
    }

    public DeclClass(AbstractIdentifier name, ListDeclField fields, ListDeclMethod methods) {
        this.name = name;
        this.superClass = null;
        this.fields = fields;
        this.methods = methods;
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        if (name != null) {
            name.prettyPrint(s, prefix, false);
        }
        if (superClass != null) {
            superClass.prettyPrint(s, prefix, false);
        }
        if (fields != null) {
            fields.prettyPrintChildren(s, prefix + "  ");
        }
        if (methods != null) {
            methods.prettyPrintChildren(s, prefix + "  ");
        }
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        if (name != null) {
            name.iter(f);
        }
        if (superClass != null) {
            superClass.iter(f);
        }
        if (fields != null) {
            fields.iter(f);
        }
        if (methods != null) {
            methods.iter(f);
        }
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("class ");
        name.decompile(s);
        if (superClass != null) {
            s.print(" extends ");
            superClass.decompile(s);
        }
        s.println(" {");
        s.indent();
        fields.decompile(s);
        methods.decompile(s);
        s.unindent();
        s.println("}");
    }

    @Override
    protected void verifyClass(DecacCompiler compiler) throws ContextualError {
        Symbol classSymbol = compiler.symbolTable.create(name.getName().getName());

        if (compiler.environmentType.defOfType(classSymbol) != null) {
            throw new ContextualError("Class " + classSymbol.getName() + " is already defined", this.name.getLocation());
        }

        ClassDefinition superClassDef;
        if (this.superClass != null) {
            Symbol superClassSymbol = compiler.symbolTable.create(superClass.getName().getName());
            Definition def = compiler.environmentType.defOfType(superClassSymbol);
            if (def == null || !(def instanceof ClassDefinition)) {
                throw new ContextualError("Super class " + superClassSymbol.getName() + " is not defined", this.superClass.getLocation());
            }
            superClassDef = (ClassDefinition) def;
            superClass.setDefinition(superClassDef);
            superClass.setType(superClassDef.getType());
        } else {
            superClassDef = compiler.environmentType.CLASS.getDefinition();
        }

        ClassType classType = new ClassType(classSymbol, this.name.getLocation(), superClassDef);
        ClassDefinition classDef = classType.getDefinition();



        compiler.environmentType.envTypes.put(classSymbol, classDef);
        this.name.setDefinition(classDef);
        this.name.setType(classType);
    }


    @Override
    protected void verifyClassMembers(DecacCompiler compiler) throws ContextualError {

        ClassDefinition currentClassDef = this.name.getClassDefinition();
        EnvironmentExp localEnv = currentClassDef.getMembers();
        this.fields.verifyListFields(compiler, currentClassDef);
        this.methods.verifyListMethods(compiler, currentClassDef,localEnv);

    }

    @Override
    protected void verifyClassBody(DecacCompiler compiler) throws ContextualError {
        ClassDefinition currentClassDef = this.name.getClassDefinition();
        EnvironmentExp localEnv = currentClassDef.getMembers();
        this.fields.verifyListInit(compiler,currentClassDef, localEnv);
        this.methods.verifyListMethodsBody(compiler, currentClassDef, localEnv);
    }

     @Override
     public void codeGenTableMethods(DecacCompiler compiler) {

    //     ClassDefinition classDef = this.name.getClassDefinition();// on fait la table des méthodes

    //     if (this.superClass != null) {
    //         ClassDefinition superClassDef = this.superClass.getClassDefinition(); // Ici c'est si jamais on est dans une classe extend on récupère les éthodes de la classe d'au dessus
    //         //Il faudra qu'on copie les méthodes de la super class dans cette classe
    //     }

    //     this.methods.codeGenTableMethods(compiler, classDef);
    }

    @Override
    public void codeGenClass(DecacCompiler compiler) {

        //this.fields.codeGenFieldsInit(compiler, this.name.getClassDefinition());

        this.methods.codeGenMethods(compiler, this.name.getClassDefinition());
    }

}
