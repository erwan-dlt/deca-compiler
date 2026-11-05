package fr.ensimag.deca.tree;

/**
 * Represents a print statement in the Deca programming language.
 * 
 * The {@code Print} class supports an optional hexadecimal format for floating-point numbers 
 * when {@code printHex} is set to {@code true}.
 * @author gl49
 * @version 1.0
 * @since 01/01/2025
 */
public class Print extends AbstractPrint {

    /**
     * Constructs a new {@code Print} statement with the specified arguments and formatting option.
     * 
     * @param printHex If {@code true}, floating-point numbers will be displayed in hexadecimal format (e.g., using `printx`).
     * @param arguments The list of expressions to be printed.
     */
    public Print(boolean printHex, ListExpr arguments) {
        super(printHex, arguments);
    }

    /**
     * Returns the suffix used in the generated assembly code for this print statement.
     * 
     * In the case of {@code Print}, the suffix is an empty string since it does not append a newline.
     * 
     * @return An empty string as the suffix.
     */
    @Override
    String getSuffix() {
        return "";
    }
}
