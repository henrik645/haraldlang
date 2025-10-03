package nu.henrikvester.haraldlang.codegen.ir;

/**
 * A compile-time id for a local variable. Used in name resolution during code generation.
 *
 * @param id
 */
public record VarSlot(int id, String name) {
}
