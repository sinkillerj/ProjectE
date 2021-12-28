package moze_intel.projecte.api.imc;

import moze_intel.projecte.api.nss.NormalizedSimpleStack;

/**
 * @param stack Defines the stack to set the EMC for.
 * @param value The EMC value to register for the given stack.
 */
public record CustomEMCRegistration(NormalizedSimpleStack stack, long value) {
}