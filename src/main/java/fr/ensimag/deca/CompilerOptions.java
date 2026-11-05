package fr.ensimag.deca;

import java.io.File;
import java.util.*;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * User-specified options influencing the compilation.
 *
 * @author gl49
 * @date 01/01/2025
 */
public class CompilerOptions {
    // Public variables
    public static final int QUIET = 0;
    public static final int INFO  = 1;
    public static final int DEBUG = 2;
    public static final int TRACE = 3;

    // Private variables
    private int debug = 0;
    private int register = 16;
    private boolean parallel = false;
    private boolean printBanner = false;
    private boolean parse = false ;
    private boolean verification = false;
    private boolean check = false;
    private List<File> sourceFiles = new ArrayList<File>();

    // Getters
    public int getDebug() { return debug; }
    public int getRegister() { return register; }
    public boolean getParallel() { return parallel; }
    public boolean getPrintBanner() { return printBanner; }
    public boolean getParse() { return parse; }
    public boolean getVerification() { return verification; }
    public boolean getCheck() { return check; }
    public List<File> getSourceFiles() { return Collections.unmodifiableList(sourceFiles); }


    public void parseArgs(String[] args) throws CLIException {
        Logger logger = Logger.getRootLogger();

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            switch (arg) {
                case "-b":
                    if (args.length > 1) {
                        throw new CLIException("L’option '-b' doit être utilisée seule.");
                    }
                    printBanner = true;
                    break;
                case "-d":
                    debug++;
                    break;
                case "-P":
                    parallel = true;
                    break;
                case "-p":
                    if (verification) {
                        throw new CLIException("L'option '-p' ne peut pas être utilisée avec '-v'.");
                    }
                    parse = true;
                    break;
                case "-v":
                    if (parse) {
                        throw new CLIException("L'option '-v' ne peut pas être utilisée avec '-p'.");
                    }
                    verification = true;
                    break;
                case "-n":
                    check = true;
                    break;
                case "-r":
                    if (i + 1 >= args.length) {
                        throw new CLIException("L'option '-r' doit être suivie d'une valeur pour X.");
                    }
                    try {
                        int x = Integer.parseInt(args[++i]);
                        if (x < 4 || x > 16) {
                            throw new CLIException("La valeur pour '-r' doit être un entier entre 4 et 16.");
                        }
                        register = x;
                    } catch (NumberFormatException e) {
                        throw new CLIException("L'option' '-r' doit être suivie d'un entier valide.");
                    }
                    break;
                default:
                    if (arg.endsWith(".deca")) {
                        File source = new File(arg);
                        if (!sourceFiles.contains(source)) {
                            sourceFiles.add(source);
                        }
                    } else {
                        throw new CLIException("Option inconnue ou fichier non reconnu : " + arg);
                    }
                    break;
            }
        }

        // map command-line debug option to log4j's level.
        switch (getDebug()) {
            case QUIET: break; // keep default
            case INFO:
                logger.setLevel(Level.INFO); break;
            case DEBUG:
                logger.setLevel(Level.DEBUG); break;
            case TRACE:
                logger.setLevel(Level.TRACE); break;
            default:
                logger.setLevel(Level.ALL); break;
        }
        logger.info("Application-wide trace level set to " + logger.getLevel());

        boolean assertsEnabled = false;
        assert assertsEnabled = true; // Intentional side effect!!!
        if (assertsEnabled) {
            logger.info("Java assertions enabled");
        } else {
            logger.info("Java assertions disabled");
        }
    }

    protected void displayUsage() {
        System.out.println();
        System.out.println("Usage: decac [[-p | -v] [-n] [-r X] [-d]* [-P] <fichiers_deca>] | [-b]");
        System.out.println("Options:");
        System.out.println("  -b   : affiche une bannière indiquant le nom de l'équipe");
        System.out.println("  -p   : arrête decac après l'étape de construction de\n" +
                           "         l'arbre, et affiche la décompilation de ce dernier\n" +
                           "         (i.e. s'il n'y a qu'un fichier source à\n" +
                           "         compiler, la sortie doit être un programme\n" +
                           "         deca syntaxiquement correct)");
        System.out.println("  -v   : arrête decac après l'étape de vérifications\n" +
                           "         (ne produit aucune sortie en l'absence d'erreur)");
        System.out.println("  -n   : supprime les tests à l'exécution spécifiés dans\n" +
                           "         les points 11.1 et 11.3 de la sémantique de Deca");
        System.out.println("  -r X : limite les registres banalisés disponibles à\n" +
                           "         R0 ... R{X-1}, avec 4 <= X <= 16");
        System.out.println("  -d   : active les traces de debug. Répéter l'option\n" +
                           "         plusieurs fois pour avoir plus de traces.");
        System.out.println("  -P   : s'il y a plusieurs fichiers sources,\n" +
                           "         lance la compilation des fichiers en\n" +
                           "         parallèle (pour accélérer la compilation)");
        System.out.println();
    }
}
