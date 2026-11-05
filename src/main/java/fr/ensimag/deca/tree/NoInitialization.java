package fr.ensimag.deca.tree;

import java.io.PrintStream;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.RegisterManag;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.LEA;
import fr.ensimag.ima.pseudocode.*;
/**
 * Absence of initialization (e.g. "int x;" as opposed to "int x =
 * 42;").
 *
 * @author gl49
 * @date 01/01/2025
 */
public class NoInitialization extends AbstractInitialization {

    @Override
    protected void verifyInitialization(DecacCompiler compiler, Type t,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError {
        //nothing to do
    }


    /**
     * Node contains no real information, nothing to check.
     */
    @Override
    protected void checkLocation() {
        // nothing
    }

    @Override
    public void decompile(IndentPrintStream s) {
        // nothing
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // leaf node => nothing to do
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // leaf node => nothing to do
    }

    @Override
    public void codeGenInit(DecacCompiler compiler, Type t) {
        RegisterManag RegisterManag = compiler.getRegisterManag();
        GPRegister reg;
        if(RegisterManag.getAvailableRegisters().contains(RegisterManag.getLastUsedRegister()) ){
            reg = RegisterManag.getLastUsedRegister();
            RegisterManag.getAvailableRegisters().remove(reg);
        }
        else{
            reg = compiler.getRegisterManag().allocateRegister();
        }

        if (t.isFloat()) {
            compiler.addInstruction(new LOAD(new ImmediateFloat(0.0f), reg));
        } else if (t.isInt()) { // En fonction du type on choisit ce qu'on initialise
            compiler.addInstruction(new LOAD(new ImmediateInteger(0), reg));
        } else if (t.isBoolean()) {
            compiler.addInstruction(new LOAD(new ImmediateInteger(0), reg));
        } else if (t.isString()) {
            String labelName = "string_default_empty";
            Label stringLabel = new Label(labelName);
            compiler.addLabel(stringLabel);
            compiler.addInstruction(new LEA(new RegisterOffset(compiler.allocateGlobalOffset(), Register.GB), reg));
        } else if (t.isClassOrNull()) {
            compiler.addInstruction(new LOAD(new NullOperand(), reg));
        } else {
            throw new UnsupportedOperationException("Unsupported type for no initialization: " + t);
        }


        compiler.getRegisterManag().setLastUsedRegister(reg);
    }


}


