package fr.ensimag.deca.tree;

import java.io.PrintStream;

import org.apache.commons.lang.Validate;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.ImmediateString;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.LEA;
import fr.ensimag.ima.pseudocode.instructions.WSTR;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.deca.codegen.RegisterManag;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.DAddr;

/**
 * String literal
 *
 * @author gl49
 * @date 01/01/2025
 */
public class StringLiteral extends AbstractStringLiteral {

    @Override
    public String getValue() {
        return value;
    }

    private String value;

    public StringLiteral(String value) {
        Validate.notNull(value);
        this.value = value;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        setType(compiler.environmentType.STRING);
        return compiler.environmentType.STRING;
    }

    @Override
    protected void codeGenPrint(DecacCompiler compiler) {
        compiler.addInstruction(new WSTR(new ImmediateString(value)));
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("\"" + getValue().replace("\\", "\\\\").replace("\"", "\\\"") + "\"");
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
    String prettyPrintNode() {
        return "StringLiteral (" + value + ")";
    }



    public void codeGenInst(DecacCompiler compiler) {
        RegisterManag RegisterManag = compiler.getRegisterManag();
        GPRegister reg;
        if(RegisterManag.getAvailableRegisters().contains(RegisterManag.getLastUsedRegister()) ){
            reg = RegisterManag.getLastUsedRegister();
            RegisterManag.getAvailableRegisters().remove(reg);
        }
        else{
            reg = compiler.getRegisterManag().allocateRegister();
        }
        String labelName = "string_" + hashCode(); // on génère une étiquette unique pour la chaîne
        Label stringLabel = new Label(labelName);

        compiler.addLabel(stringLabel);
        compiler.addInstruction(new WSTR(new ImmediateString(this.getValue())));


        DAddr stringAddr = new RegisterOffset(0, Register.GB);
        compiler.addInstruction(new LEA(stringAddr, reg)); // Charger l'adresse dans le registre

        compiler.getRegisterManag().setLastUsedRegister(reg); // On met a jour le dernier registre
    }



}
