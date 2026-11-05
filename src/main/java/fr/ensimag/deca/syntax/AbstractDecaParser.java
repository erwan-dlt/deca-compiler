package fr.ensimag.deca.syntax;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tree.AbstractProgram;
import fr.ensimag.deca.tree.Location;
import fr.ensimag.deca.tree.LocationException;
import fr.ensimag.deca.tree.Tree;

import java.io.PrintStream;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.apache.log4j.Logger;

/**
 * The super class of the generated parser. It is extended by the generated
 * code because of the superClass option in the .g file.
 *
 * @author gl49, Based on template by Jim Idle - Temporal Wave LLC - jimi@temporal-wave.com
 * @date 01/01/2025
 */
public abstract class AbstractDecaParser extends Parser {
    Logger LOG = Logger.getLogger(AbstractDecaParser.class);

    private DecacCompiler decacCompiler;

    protected DecacCompiler getDecacCompiler() {
        return decacCompiler;
    }

    public void setDecacCompiler(DecacCompiler decacCompiler) {
        this.decacCompiler = decacCompiler;
    }

    protected String decodeString(String text) {
        StringBuilder value = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '\\') c = text.charAt(++i);
            value.append(c);
        }
        return value.toString();
    }

    protected int parseIntegerLiteral(Token token) {
        try {
            return Integer.parseInt(token.getText());
        } catch (NumberFormatException e) {
            throw new ParseCancellationException(new LocationException("Integer literal out of range", tokenLocation(token)));
        }
    }

    protected float parseFloatLiteral(Token token) {
        float value = Float.parseFloat(token.getText());
        if (Float.isInfinite(value)) {
            throw new ParseCancellationException(new LocationException("Float literal out of range", tokenLocation(token)));
        }
        return value;
    }

    protected abstract AbstractProgram parseProgram();

    public AbstractProgram parseProgramAndManageErrors(PrintStream err) {
        try {
            AbstractProgram result = parseProgram();
            assert(result != null);
            return result;
        } catch (ParseCancellationException e) {
            LOG.debug("ParseCancellationException raised while compiling file:", e);
            if (e.getCause() instanceof LocationException) {
                ((LocationException)e.getCause()).display(err);
                return null;
            } else {
                throw new DecacInternalError("Parsing cancelled", e);
            }
        } catch (DecaRecognitionException e) {
            e.display(err);
            return null;
        }
    }

    /**
     * Extract the Location of a token.
     */
    protected Location tokenLocation(Token token) {
        return new Location(token.getLine(),
                token.getCharPositionInLine(),
                token.getInputStream().getSourceName());
    }

    /**
     * Sets the location of Tree to the one in Token.
     *
     * This is a simple convenience wrapper around {@link Tree#setLocation(Location)}.
     */
    protected void setLocation(Tree tree, Token token) {
        tree.setLocation(tokenLocation(token));
    }

    /**
     * Create a new parser instance, pre-supplying the input token stream.
     *
     * @param input The stream of tokens that will be pulled from the lexer
     */
    protected AbstractDecaParser(TokenStream input) {
        super(input);
        setErrorHandler(new DefaultErrorStrategy() {
            @Override
            public void reportError(Parser recognizer,
                                    RecognitionException e) {
                LOG.debug("Error found by ANTLR");
                if (e instanceof DecaRecognitionException) {
                    Token offendingToken = e.getOffendingToken();
                    if (offendingToken == null) {
                        offendingToken = recognizer.getCurrentToken();
                    }
                    recognizer.notifyErrorListeners(offendingToken, e.getMessage(), e);
                } else {
                    super.reportError(recognizer, e);
                }
            }
        });
        removeErrorListeners();
        addErrorListener(new DecacErrorListner(input));
    }
}

