package nu.henrikvester.haraldlang.codegen.ir.primitives.values;

/**
 * An IR-level representation of the current stack frame that backs a local variable.
 * Codegen maps this to actual stack offsets during code generation.
 * If a promotable local gets eliminated (replaced by SSA temps), the FrameSlot loads and stores disappear.
 *
 * @param id
 */
public record IRFrameSlot(int id) implements IRValue {
}
