package nu.henrikvester.haraldlang.codegen;

public enum Op {
    CONST,
    ADD,
    SUB,
    SHL,
    AND,
    XOR,
    OR,
    MOV, // mem or const
    LOAD, // mem
    BRNZ, // branch on zero -- arguments: cond, labelTrue, labelFalse
    JMP,
    PHI, // SSA join [ L1: v1, L2: v2, ... ] -- phi aware coalescing?
}

// not included:
// INC => add 1
// DEC => sub 1
// LDI => mov <const>
// HLT => jmp exit
// peephole optimization reintroduces these

/*

block entry:
t0 = load a // symbol lowered later?
t1 = add t1, 1
brz t1, then, else

block then:
t2 = shl t1, 1
jmp join

block else:
t3 = sub t1, 1
jmp join

block join:
t4 = phi [ then: t2, else: t3 ]
store a, t4
jmp exit

 */
