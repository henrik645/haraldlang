package nu.henrikvester.haraldlang.codegen.ir;

import nu.henrikvester.haraldlang.ast.types.HLType;

/**
 * A compile-time id for a local variable. Used in name resolution during code generation.
 *
 * @param id
 */
// TODO should this keep type information?
public record VarSlot(int id, String name, HLType type) {
}
