package fr.ensimag.deca.tree;

import java.io.PrintStream;

import fr.ensimag.deca.codegen.RegisterManag;
import org.apache.commons.lang.Validate;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.ImmediateFloat;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.WFLOAT;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
/**
 * Single precision, floating-point literal
 *
 * @author gl49
 * @date 01/01/2025
 */
public class FloatLiteral extends AbstractExpr {

    public float getValue() {
        return value;
    }

    private float value;

    public FloatLiteral(float value) {
        Validate.isTrue(!Float.isInfinite(value),
                "literal values cannot be infinite");
        Validate.isTrue(!Float.isNaN(value),
                "literal values cannot be NaN");
        this.value = value;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {

        setType(compiler.environmentType.FLOAT);
        return compiler.environmentType.FLOAT;        
    }

    @Override
    public void decompile(IndentPrintStream s) {
        //s.print(java.lang.Float.toHexString(value));
        s.print(Float.toString(value)); 
    }

    @Override
    String prettyPrintNode() {
        return "Float (" + getValue() + ")";
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
    protected void codeGenPrint(DecacCompiler compiler) {
        ImmediateFloat immediateFloat = new ImmediateFloat(value);
        compiler.addInstruction(new LOAD(immediateFloat, Register.R1));
        compiler.addInstruction(new WFLOAT());
    }

    @Override
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
        compiler.addInstruction(new LOAD(new ImmediateFloat(this.getValue()), reg));
        compiler.getRegisterManag().setLastUsedRegister(reg);
    }


}
