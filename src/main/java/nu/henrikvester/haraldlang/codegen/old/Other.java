package nu.henrikvester.haraldlang.codegen.old;

/**
 * Defines a register or memory location
 * Register allocation ties one of these to a real register or, in the case of memory location,
 * a load plus a register.
 */
class Register {
}

/**
 * Defines a location in the code, for jumps and branches.
 */
class Location {
}

record Add(Register left, Register right) {
}

record Sub(Register left, Register right) {
}

record Load(Register dest, Register address) {
}

record Store(Register src, Register address) {
}

record LoadImmediate(Register dest, int value) {
}

record ShiftLeft(Register x) {
}

record Increment(Register x) {
}

record Decrement(Register x) {
}

record And(Register left, Register right) {
}

record Or(Register left, Register right) {
}

record Xor(Register left, Register right) {
}

record JumpIfZero(Register condition, Location location) {
}

record JumpIfNotZero(Register condition, Location location) {
}

record Jump(Location location) {
}

record Halt(Location location) {
}

record Noop() {
}
