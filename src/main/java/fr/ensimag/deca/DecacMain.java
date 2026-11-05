package fr.ensimag.deca;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

/**
 * Main class for the command-line Deca compiler.
 *
 * @author gl49
 * @date 01/01/2025
 */
public class DecacMain {
    private static Logger LOG = Logger.getLogger(DecacMain.class);

    public static void main(String[] args) {
        // example log4j message.
        LOG.info("Decac compiler started");
        boolean error = false;
        final CompilerOptions options = new CompilerOptions();
        try {
            options.parseArgs(args);
        } catch (CLIException e) {
            System.err.println("Error during option parsing:\n"
                    + e.getMessage());
            options.displayUsage();
            System.exit(1);
        }
        if (options.getPrintBanner()) {
            displayBanner();
            System.exit(0);
        }
        if (options.getSourceFiles().isEmpty()) {
            options.displayUsage();
        }
        if (options.getParallel() && !options.getParse()) {
            ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            List<Future<Boolean>> results = new ArrayList<>();

            for (File source : options.getSourceFiles()) {
                results.add(executor.submit(() -> {
                    DecacCompiler compiler = new DecacCompiler(options, source);
                    return compiler.compile();
                }));
            }

            executor.shutdown();

            for (Future<Boolean> result : results) {
                try {
                    if (result.get()) {
                        error = true;
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    executor.shutdownNow();
                    error = true;
                    break;
                } catch (ExecutionException e) {
                    LOG.error("Error during parallel compilation", e);
                    error = true;
                }
            }
        } else {
            for (File source : options.getSourceFiles()) {
                DecacCompiler compiler = new DecacCompiler(options, source);
                if (compiler.compile()) {
                    error = true;
                }
            }
        }
        System.exit(error ? 1 : 0);
    }

    private static void displayBanner() {
        System.out.println();
        System.out.println("==========================================================");
        System.out.println("                   Projet Deca Compiler                   ");
        System.out.println("               Réalisé par l'équipe : gl49                ");
        System.out.println("         Colin Dunand / Baptiste Van der Stracten         ");
        System.out.println("  Julia Mulder / Abel Prud'Homme Lacroix / Didelot Erwan  ");
        System.out.println("                  ENSIMAG x Phelma - 2025                 ");
        System.out.println("==========================================================");
        System.out.println();
    }
}
