package fr.ensimag.deca.tree;

import fr.ensimag.ima.pseudocode.instructions.ERROR;
import java.io.PrintStream;

import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.WNL;
import fr.ensimag.ima.pseudocode.instructions.WSTR;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.instructions.HALT;

/**
 * Deca complete program (class definition plus main block)
 *
 * @author gl49
 * @date 01/01/2025
 */
public class Program extends AbstractProgram {
    private static final Logger LOG = Logger.getLogger(Program.class);

    public Program(ListDeclClass classes, AbstractMain main) {
        Validate.notNull(classes);
        Validate.notNull(main);
        this.classes = classes;
        this.main = main;
    }
    public ListDeclClass getClasses() {
        return classes;
    }
    public AbstractMain getMain() {
        return main;
    }
    private ListDeclClass classes;
    private AbstractMain main;

    @Override
    public void verifyProgram(DecacCompiler compiler) throws ContextualError {
        LOG.debug("verify program: start");
        this.classes.verifyListClass(compiler);
        this.main.verifyMain(compiler);
        LOG.debug("verify program: end");
    }

    @Override
    public void codeGenProgram(DecacCompiler compiler) {
        compiler.addComment("Main program");

        // Génération de code pour les déclarations de classes (si applicable)
        // classes.codeGenListClass(compiler); // Quand on sera avec objet

        main.codeGenMain(compiler);
        compiler.addInstruction(new HALT());

        compiler.addLabel(new Label("input_error"));
        compiler.addInstruction(new WSTR("Error: invalid input"));
        compiler.addInstruction(new WNL());
        compiler.addInstruction(new ERROR());
        compiler.addLabel(new Label("stack_overflow_error"));
        compiler.addInstruction(new WSTR("Error: stack overflow"));
        compiler.addInstruction(new WNL());
        compiler.addInstruction(new ERROR());

        compiler.addComment("Error");
        compiler.addLabel(new Label("division_by_zero_error"));
        compiler.addInstruction(new WSTR("Division by zero"));
        compiler.addInstruction(new WNL());
        compiler.addInstruction(new ERROR());

        compiler.addComment("Error");
        compiler.addLabel(new Label("overflow_error"));
        compiler.addInstruction(new WSTR("Error: Overflow during arithmetic operation"));
        compiler.addInstruction(new WNL());
        compiler.addInstruction(new ERROR());
    }

    @Override
    public void decompile(IndentPrintStream s) {
        getClasses().decompile(s);
        getMain().decompile(s);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        classes.iter(f);
        main.iter(f);
    }
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        classes.prettyPrint(s, prefix, false);
        main.prettyPrint(s, prefix, true);
    }
}
