package fr.ensimag.deca.tree;

import java.util.Iterator;

import org.apache.log4j.Logger;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.tools.IndentPrintStream;

/**
 *  List of declarations of classes.
 *
 * @author gl49
 * @date 01/01/2025
 */
public class ListDeclClass extends TreeList<AbstractDeclClass> {
    private static final Logger LOG = Logger.getLogger(ListDeclClass.class);
    
    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractDeclClass c : getList()) {
            c.decompile(s);
            s.println();
        }
    }

    /**
     * Pass 1 of [SyntaxeContextuelle]
     */
    void verifyListClass(DecacCompiler compiler) throws ContextualError {
        LOG.debug("verify listClass: start");
        Iterator<AbstractDeclClass> iterator = this.iterator();
        while (iterator.hasNext()){
            iterator.next().verifyClass(compiler);
            }
        this.verifyListClassMembers(compiler);
        LOG.debug("verify listClass: end");
    }

    /**
     * Pass 2 of [SyntaxeContextuelle]
     */
    public void verifyListClassMembers(DecacCompiler compiler) throws ContextualError {
        Iterator<AbstractDeclClass> iterator = this.iterator();
        while (iterator.hasNext()){
            iterator.next().verifyClassMembers(compiler);
            }
        this.verifyListClassBody(compiler);
    }
    
    /**
     * Pass 3 of [SyntaxeContextuelle]
     */
    public void verifyListClassBody(DecacCompiler compiler) throws ContextualError {
        Iterator<AbstractDeclClass> iterator = this.iterator();
        while (iterator.hasNext()){
            iterator.next().verifyClassBody(compiler);
            }
    }

    public void codeGenListClass(DecacCompiler compiler) {
        
        // for (AbstractDeclClass declClass : getList()) { // On construit d'abord les méthodes
        //     declClass.codeGenTableMethods(compiler);
        // }

       
        for (AbstractDeclClass declClass : getList()) { // génération du code d'initialisation et des méthodes
            declClass.codeGenClass(compiler);
        }
    }
}
