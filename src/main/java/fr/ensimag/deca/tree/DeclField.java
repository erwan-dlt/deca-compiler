package fr.ensimag.deca.tree;

import java.io.PrintStream;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.FieldDefinition;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;

/**
 * Déclaration d'un champ dans une classe.
 * @author gl49
 * @date 01/01/2025
 */
public class DeclField extends AbstractDeclField {
    private static final Logger LOG = Logger.getLogger(AbstractExpr.class);

    private AbstractIdentifier name; // Identifiant du champ
    private Visibility visibility; //Visibilité du champ
    private AbstractInitialization initialization; // Initialisation du champ
    public AbstractIdentifier type;

    /**
     * Constructeur pour un champ avec ou sans initialisation.
     * @param name Identifiant du champ.
     * @param initialization Initialisation du champ (NoInitialization ou Initialization).
     */
    public DeclField(AbstractIdentifier type, AbstractIdentifier name, AbstractInitialization initialization) {
        Validate.notNull(name, "Le nom du champ ne peut pas être nul");
        Validate.notNull(initialization, "L'initialisation ne peut pas être nulle");
        this.name = name;
        this.type = type;
        this.initialization = initialization;
        this.visibility=Visibility.PUBLIC;
    }

    public AbstractIdentifier getName() {
        return name;
    }

    public AbstractInitialization getInitialization() {
        return initialization;
    }

    public void setVisibility(Visibility v){
        this.visibility = v;
    }

    public Visibility  getVisibility(){
        return this.visibility;
    }

    @Override
    public void verifyField(DecacCompiler compiler, ClassDefinition currentClassDef) throws ContextualError {
        // Vérification que le type du champ est valide
        Type fieldType = type.verifyType(compiler);
        if (fieldType.isVoid()) {
            throw new ContextualError("Le type d'un champ ne peut pas être 'void'.", getLocation());
        }

        EnvironmentExp classEnv = currentClassDef.getMembers();
        FieldDefinition fieldDef = new FieldDefinition(fieldType, name.getLocation(), visibility, currentClassDef, currentClassDef.getNumberOfFields() + 1 );

        try {
            classEnv.declare(""+name.getName(), fieldDef);
            name.setDefinition(fieldDef);

            currentClassDef.incNumberOfFields();
        } catch (EnvironmentExp.DoubleDefException e) {
            throw new ContextualError(
                "Le champ '" + name.getName() + "' est déjà défini dans cette classe ou une classe parent.",
                getLocation()
            );
        }
    }

    @Override
    public void verifyFieldInit(DecacCompiler compiler, ClassDefinition currentClassDef, EnvironmentExp localEnv) throws ContextualError {

        Type fieldType = this.type.verifyType(compiler);
        initialization.verifyInitialization(compiler, fieldType, localEnv, currentClassDef);

    }



    @Override
    public void decompile(IndentPrintStream s) {
        if (visibility == Visibility.PROTECTED) {
            s.print("protected ");
        }
        type.decompile(s);
        s.print(" ");
        name.decompile(s);
        initialization.decompile(s);
        s.println(";");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        if (name != null) {
            name.iter(f);
        }
        if (initialization != null) {
            initialization.iter(f);
        }
    }


    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        if (name != null) {
            name.prettyPrint(s, prefix, false);
        }
        if (initialization != null) {
            initialization.prettyPrintChildren(s, prefix + "  ");
        }
        if (visibility != null) {
            s.println(prefix + "|- Visibility: " + visibility.toString());
        }
    }

    @Override
    String prettyPrintNode() {
        return "DeclField (name=" + name.getName() + ")";
    }
}
