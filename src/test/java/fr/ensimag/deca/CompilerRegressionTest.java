package fr.ensimag.deca;

import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.deca.codegen.RegisterManag;
import static org.junit.jupiter.api.Assertions.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/** End-to-end checks of the CLI, diagnostics and emitted assembly. No IMA required. */
class CompilerRegressionTest {
    @TempDir Path directory;

    private Result run(String source, String... options) throws Exception {
        Path input = directory.resolve("program.deca");
        Files.write(input, source.getBytes(StandardCharsets.UTF_8));
        List<String> command = new ArrayList<>(Arrays.asList(
                System.getProperty("java.home") + "/bin/java", "-ea", "-cp",
                System.getProperty("java.class.path"), DecacMain.class.getName()));
        command.addAll(Arrays.asList(options));
        command.add(input.toString());
        Path out = directory.resolve("stdout"), err = directory.resolve("stderr");
        Process process = new ProcessBuilder(command).redirectOutput(out.toFile()).redirectError(err.toFile()).start();
        assertTrue(process.waitFor(20, java.util.concurrent.TimeUnit.SECONDS), "Compiler timed out");
        Path assembly = directory.resolve("program.ass");
        return new Result(process.exitValue(), read(out), read(err), Files.exists(assembly) ? read(assembly) : "");
    }

    private String read(Path path) throws Exception {
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }

    private static class Result {
        final int status;
        final String out, err, assembly;
        Result(int status, String out, String err, String assembly) {
            this.status = status; this.out = out; this.err = err; this.assembly = assembly;
        }
    }

    @Test void decompileUsesStdoutAndRoundTrips() throws Exception {
        Result first = run("{ float x = 1.5; println(x); }", "-p");
        assertEquals(0, first.status, first.err);
        assertEquals("", first.err);
        assertFalse(first.out.isEmpty());
        assertEquals("", first.assembly);
        assertEquals(0, run(first.out, "-p").status);
    }

    @ParameterizedTest @ValueSource(strings={"4", "16"})
    void registerLimitsAreExclusive(String count) throws Exception {
        Result result = run("{ int x = 1; x = x + 2; println(x); }", "-r", count);
        assertEquals(0, result.status, result.err);
        assertFalse(result.assembly.contains("R" + count));
    }

    @ParameterizedTest @ValueSource(strings={"", "{}", "{ println(-5); }", "{ println((int) (2.5)); }",
        "{ float x = 1; println(x = 2); }", "{ int a = 0; int b = 0; a = b = 3; println(a + b); }",
        "{ float x = readFloat() + readFloat(); println(x); }"})
    void supportedProgramsCompile(String source) throws Exception {
        Result result = run(source);
        assertEquals(0, result.status, result.err);
        assertTrue(result.assembly.contains("HALT"));
    }

    @Test void expressionStatementsReleaseRegisters() throws Exception {
        StringBuilder source = new StringBuilder("{");
        for (int i = 0; i < 40; i++) source.append("1 + 2;");
        source.append("println(3); }");
        Result result = run(source.toString(), "-r", "4");
        assertEquals(0, result.status, result.err);
    }

    @Test void unaryMinusUsesOperandRegister() throws Exception {
        Result result = run("{ println(-5); }");
        assertEquals(0, result.status, result.err);
        assertTrue(result.assembly.contains("LOAD #5, R2"));
        assertTrue(result.assembly.contains("OPP R2, R2"));
    }

    @Test void hexadecimalPrintingEmitsWfloatx() throws Exception {
        Result result = run("{ printlnx(1.5 + 2.5); }");
        assertEquals(0, result.status, result.err);
        assertTrue(result.assembly.contains("WFLOATX"));
    }

    @Test void booleanConditionsCompareTheirResult() throws Exception {
        Result result = run("{ boolean b = true; if (!b || false) { println(1); } while (false) { b = true; } }");
        assertEquals(0, result.status, result.err);
        assertTrue(result.assembly.contains("CMP #0, R2"));
        assertTrue(result.assembly.contains("SEQ R2"));
    }

    @Test void noCheckSuppressesArithmeticGuards() throws Exception {
        Result result = run("{ float x = 4.0 / 2.0; println(x); }", "-n");
        assertEquals(0, result.status, result.err);
        assertFalse(result.assembly.contains("BOV"));
        assertFalse(result.assembly.contains("BEQ division_by_zero_error"));
    }

    @ParameterizedTest @ValueSource(strings={"{ println(true); }", "{ return 1; }", "{ int x = 999999999999999; }", "{ float x = 1.0e999; }", "{ boolean x = (boolean) (1); }"})
    void invalidProgramsHaveLocatedDiagnostics(String source) throws Exception {
        Result result = run(source);
        assertNotEquals(0, result.status);
        assertTrue(result.err.contains("program.deca:1:"), result.err);
        assertFalse(result.err.contains("Internal compiler error"), result.err);
        assertEquals("", result.assembly);
    }

    @Test void objectSyntaxCanBeDecompiled() throws Exception {
        String source = "class A { int x; int f(int y) { return y; } } {}";
        Result result = run(source, "-p");
        assertEquals(0, result.status, result.err);
        assertEquals(0, run(result.out, "-p").status);
    }

    @Test void stringEscapesRoundTrip() throws Exception {
        String source = "{ println(\"a\\\"b\\\\c\"); }";
        Result result = run(source, "-p");
        assertEquals(0, result.status, result.err);
        assertEquals(0, run(result.out).status);
    }

    @ParameterizedTest @ValueSource(strings={"3", "17", "abc"})
    void invalidRegisterCountsAreRejected(String count) throws Exception {
        Result result = run("{}", "-r", count);
        assertNotEquals(0, result.status);
        assertEquals("", result.assembly);
    }

    @Test void conflictingOptionsAreRejected() throws Exception {
        assertNotEquals(0, run("{}", "-p", "-v").status);
    }

    @Test void defaultRegisterPoolContainsFourteenRegisters() {
        RegisterManag registers = new RegisterManag(16);
        for (int i = 2; i < 16; i++) {
            assertSame(Register.getR(i), registers.allocateRegister());
        }
        assertThrows(UnsupportedOperationException.class, registers::allocateRegister);
    }

    @Test void classCodeGenerationFailsExplicitly() throws Exception {
        Result result = run("class A {} {}");
        assertNotEquals(0, result.status);
        assertTrue(result.err.contains("not supported"), result.err);
        assertEquals("", result.assembly);
    }
}
