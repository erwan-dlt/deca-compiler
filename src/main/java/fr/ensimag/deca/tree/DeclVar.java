package fr.ensimag.deca.tree;

import java.io.PrintStream;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.RegisterManag;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.VariableDefinition;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.STORE;
/**
 * Declaration of a variable.
 *
 * @author gl49
 * @date 01/01/2025
 */
public class DeclVar extends AbstractDeclVar {
    private static final Logger LOG = Logger.getLogger(DeclVar.class);

    final private AbstractIdentifier type;
    final private AbstractIdentifier varName;
    final private AbstractInitialization initialization;


    public DeclVar(AbstractIdentifier type, AbstractIdentifier varName, AbstractInitialization initialization) {
        Validate.notNull(type);
        Validate.notNull(varName);
        Validate.notNull(initialization);
        this.type = type;
        this.varName = varName;
        this.initialization = initialization;
    }

    @Override
    protected void verifyDeclVar(DecacCompiler compiler, EnvironmentExp localEnv,
                                ClassDefinition currentClass) throws ContextualError {
        // Vérifie le type de la variable
        Type t = this.type.verifyType(compiler);

        if (t.isVoid()) {
            throw new ContextualError("Variable cannot have type void", this.type.getLocation());
        }

        // Crée la définition de la variable
        VariableDefinition varDef = new VariableDefinition(t, varName.getLocation());
        DAddr varAddress = (currentClass == null)
                ? new RegisterOffset(compiler.allocateGlobalOffset(), Register.GB)
                : new RegisterOffset(compiler.allocateLocalOffset(), Register.LB);

        varDef.setOperand(varAddress);

        // Ajoute la variable à l'environnement
        Symbol symbol = compiler.symbolTable.create(varName.getName().getName());
        try {
            localEnv.declare(""+symbol, varDef);
        } catch (EnvironmentExp.DoubleDefException e) {
            throw new ContextualError("Variable '" + symbol.getName() + "' is already declared in this scope", varName.getLocation());
        }

        varName.setDefinition(varDef);

        // Vérifie l'initialisation
        this.initialization.verifyInitialization(compiler, t, localEnv, currentClass);
    }


    @Override
    public void codeGenDeclVar(DecacCompiler compiler) {
        RegisterManag registerManag = compiler.getRegisterManag();

        DAddr varAddress = varName.getExpDefinition().getOperand();// Récupère l'adresse de la variable
        if (varAddress == null) {
            throw new IllegalStateException("La variable '" + varName.getName() + "' est null");
        }

        initialization.codeGenInit(compiler, type.getType());

        GPRegister reg = registerManag.getLastUsedRegister();

        compiler.addInstruction(new STORE(reg, varAddress));

        registerManag.freeLastUsedRegister();
    }


    @Override
    public void decompile(IndentPrintStream s) {
        type.decompile(s);
        s.print(" ");
        varName.decompile(s);
        initialization.decompile(s);
        s.println(";");
    }

    @Override
    protected
    void iterChildren(TreeFunction f) {
        type.iter(f);
        varName.iter(f);
        initialization.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s, prefix, false);
        varName.prettyPrint(s, prefix, false);
        initialization.prettyPrint(s, prefix, true);
    }
}
