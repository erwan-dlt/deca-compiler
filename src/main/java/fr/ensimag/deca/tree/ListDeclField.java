package fr.ensimag.deca.tree;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;

/**
 * List of declarations of fields.
 */
public class ListDeclField extends Tree implements Iterable<AbstractDeclField> {

    private List<AbstractDeclField> fields;

    public ListDeclField() {
        this.fields = new ArrayList<>();
    }

    public void add(AbstractDeclField field) {
        fields.add(field);
    }

    public void addAll(ListDeclField other) {
        if (other != null) {
            fields.addAll(other.getFields());
        }
    }

    public List<AbstractDeclField> getFields() {
        return fields;
    }

    @Override
    public Iterator<AbstractDeclField> iterator() {
        return fields.iterator();
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        for (AbstractDeclField field : fields) {
            if (field != null) {
                field.iter(f);
            }
        }
    }

   protected void verifyListFields(DecacCompiler compiler, ClassDefinition currentClassDef) throws ContextualError{
       Iterator<AbstractDeclField> iterator = this.iterator();
       while (iterator.hasNext()){
           iterator.next().verifyField(compiler,currentClassDef);
           }
   }

   protected void verifyListInit(DecacCompiler compiler, ClassDefinition currentClassDef, EnvironmentExp localEnv) throws ContextualError{
        Iterator<AbstractDeclField> it = this.iterator();
        while (it.hasNext()) {
                it.next().verifyFieldInit(compiler, currentClassDef, localEnv);
        }
   }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        for (AbstractDeclField field : fields) {
            if (field != null) {
                field.prettyPrint(s, prefix, false);
            }
        }
    }

    @Override
    String prettyPrintNode() {
        return "ListDeclField (" + fields.size() + " champs)";
    }

    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractDeclField member : fields) {
            member.decompile(s);
        }
    }
}
