/**
 * Utilities used for IMA code generation.
 *
 * Register Management
 *
 * @author gl49
 * @date 10/01/2025
 */
package fr.ensimag.deca.codegen;

import java.util.LinkedList;
import java.util.List;

import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import org.apache.log4j.Logger;

public class RegisterManag {
    private static final Logger LOG = Logger.getLogger(RegisterManag.class);

    private final LinkedList<GPRegister> availableRegisters = new LinkedList<>();

    private GPRegister lastUsedRegister = null;

    public RegisterManag(int numberRegister) {
        for (int i = 2; i < numberRegister; i++) {
            availableRegisters.add(Register.getR(i));
        }
    }

    public GPRegister allocateRegister() {
        if (availableRegisters.isEmpty()) {
            throw new UnsupportedOperationException("Expression requires more registers; stack spilling is not implemented. Increase -r up to 16 or simplify the expression.");
        }
        lastUsedRegister = availableRegisters.get(0);
        return availableRegisters.remove(0);
    }

    public void freeLastUsedRegister() {
        if (lastUsedRegister == null) {
            throw new IllegalStateException("No result register to release");
        }
        freeRegister(lastUsedRegister);
        lastUsedRegister = null;
        LOG.debug(availableRegisters.toString());
    }

    public void freeRegister(GPRegister reg) {
        if (reg == null || availableRegisters.contains(reg)) {
            throw new IllegalStateException("Register is null or already free");
        }
        availableRegisters.addFirst(reg);
        if (reg == lastUsedRegister) {
            lastUsedRegister = null;
        }
        LOG.debug(availableRegisters.toString());
    }

    public List<GPRegister> getAvailableRegisters() {
        return availableRegisters;
    }

    public void setLastUsedRegister(GPRegister reg) {
        this.lastUsedRegister = reg;
    }

    public GPRegister getLastUsedRegister() {
        return lastUsedRegister;
    }
}
