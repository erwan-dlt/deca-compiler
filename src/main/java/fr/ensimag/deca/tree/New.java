/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.ensimag.deca.tree;

import java.io.PrintStream;

import org.apache.log4j.Logger;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;

/**
 * New expression
 * 
 * @author gl49
 * @date 01/01/2025
 */

public class New extends AbstractExpr {
    private static final Logger LOG = Logger.getLogger(DecacCompiler.class);
    
    private AbstractIdentifier ident;
    
    public New(AbstractIdentifier ident) {
        this.ident=ident;
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("new ");
        ident.decompile(s);
        s.print("()");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        ident.prettyPrint(s, prefix, true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        ident.iter(f);
    }
    
    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        Type t = ident.verifyType(compiler);
        if (! t.isClass()) {
            throw new ContextualError("identifier is not a class",this.ident.getLocation());
        }
        this.setType(t);
        return t;
    }
    
}
