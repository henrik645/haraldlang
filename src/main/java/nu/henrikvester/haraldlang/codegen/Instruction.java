package nu.henrikvester.haraldlang.codegen;

public enum Instruction {
    
    NOP (0b0000),
    ADD (0b0001), // 
    SUB (0b0010),
    LOAD(0b0011),
    STORE(0b0100),
    LDI (0b0101),
    SL  (0b0110),
    INC (0b0111),
    DEC (0b1000),
    OR  (0b1001),
    AND (0b1010),
    XOR (0b1011),
    JMZ (0b1100),
    JMNZ(0b1101),
    JMP (0b1110),
    HLT (0b1111);
    
    private final int code;
    
    Instruction(int code) {
        this.code = code;
    }


    public int code() {
        return code;
    }


    public static Instruction fromCode(int code) {
        for (Instruction instr : values()) {
            if (instr.code == code) {
                return instr;
            }
        }
        throw new IllegalArgumentException("Unknown opcode: " + code);
    }
}