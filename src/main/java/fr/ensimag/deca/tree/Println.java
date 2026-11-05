package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.instructions.WNL;

/**
 * Represents a print statement in the Deca programming language.
 * 
 * The {@code Println} class supports an optional hexadecimal format for floating-point numbers 
 * when {@code printHex} is set to {@code true}.
 * @author gl49
 * @version 1.0
 * @since 01/01/2025
 */
public class Println extends AbstractPrint {

    /**
     * @param arguments arguments passed to the print(...) statement.
     * @param printHex if true, then float should be displayed as hexadecimal (printlnx)
     */
    public Println(boolean printHex, ListExpr arguments) {
        super(printHex, arguments);
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        super.codeGenInst(compiler);
        compiler.addInstruction(new WNL());
    }

    @Override
    String getSuffix() {
        return "ln";
    }
}
