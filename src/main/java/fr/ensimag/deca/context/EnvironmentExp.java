package fr.ensimag.deca.context;

import java.util.HashMap;
import java.util.Map;

/**
 * Dictionary associating identifier's ExpDefinition to their names.
 *
 * This is actually a linked list of dictionaries: each EnvironmentExp has a
 * pointer to a parentEnvironment, corresponding to superblock (eg superclass).
 *
 * The dictionary at the head of this list thus corresponds to the "current"
 * block (eg class).
 *
 * Searching a definition (through method get) is done in the "current"
 * dictionary and in the parentEnvironment if it fails.
 *
 * Insertion (through method declare) is always done in the "current" dictionary.
 *
 * @author gl49
 * @date 01/01/2025
 */
public class EnvironmentExp {
    private Map<String, ExpDefinition> env;

    EnvironmentExp parentEnvironment;

    public EnvironmentExp(EnvironmentExp parentEnvironment) {
        this.parentEnvironment = parentEnvironment;
        this.env = new HashMap<String, ExpDefinition>();
    }

    //si l'environnement n'a pas de parent
    public EnvironmentExp() {
        this.parentEnvironment = null;
        this.env = new HashMap<String, ExpDefinition>();
    }

    @Override
    public String toString(){
        String m = "  ";
        for (String e : env.keySet()){
            m += "  " + e+" : "+env.get(e)+"\n";
        }
        return m;
    }

    public static class DoubleDefException extends Exception {
        private static final long serialVersionUID = -2733379901827316441L;
    }
    /**
     * Return the definition of the symbol in the environment, or null if the
     * symbol is undefined.
     */
    public ExpDefinition get(String symbol) {
        if (env.containsKey(symbol)) {
            return env.get(symbol);
        } else if (parentEnvironment != null) {
            return parentEnvironment.get(symbol);
        } else {
            return null;
        }
    }

    /**
     * Add the definition def associated to the symbol name in the environment.
     *
     * Adding a symbol which is already defined in the environment,
     * - throws DoubleDefException if the symbol is in the "current" dictionary
     * - or, hides the previous declaration otherwise.
     *
     * @param name
     *            Name of the symbol to define
     * @param def
     *            Definition of the symbol
     * @throws DoubleDefException
     *             if the symbol is already defined at the "current" dictionary
     *
     */
    public void declare(String name, ExpDefinition def) throws DoubleDefException {
        if (env.containsKey(name)){
            throw new DoubleDefException();
        } else {
            env.put(name, def);
        }
    }
}
