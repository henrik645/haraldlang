package nu.henrikvester.haraldlang.codegen;

public class CPU {


    // Registers
    private final byte[] regs = new byte[4]; // r0, r1, r2, r3
    private boolean zeroFlag;

    private byte pc;
    private byte[] ram;

    private boolean halted;
    
    public CPU() {
        ram = new byte[256];
    }


    public void setRam(byte data, byte address) {
        ram[address] = data;
    }


    public void run() {
        while(!halted) {
            execute();
        }
    }


    public byte[] getRegs() {
        return regs;
    }


    public byte fetch() {
        byte result = ram[pc & 0xFF];
        pc++;
        return result;
    }



    public void execute() {
        byte instruction = fetch();
        int opCode = (instruction >> 4) & 0x0F;
        int registerAddress1 = (instruction >> 2 & 0b11);
        int registerAddress2 = (instruction & 0b11);

        byte registerA = regs[registerAddress1];
        byte registerB = regs[registerAddress2];

        boolean wroteA = false;
        
        Instruction operation = Instruction.fromCode(opCode);
        switch(operation) {
            case ADD:
                registerA = (byte) (registerA + registerB);
                wroteA = true;
                break;
            case SUB:
                registerA = (byte) (registerA - registerB);
                wroteA = true;
                break;
            case LOAD:
                registerA = ram[registerB];
                wroteA = true;
                break;
            case STORE:
                ram[registerB & 0xFF] = registerA;
                break;
            case LDI:
                byte operand = fetch();
                registerA = operand;
                wroteA = true;
                break;
            case SL:
                registerA = (byte) (registerA << 1);
                wroteA = true;
                break;
            case INC:
                registerA++;
                wroteA = true;
                break;
            case DEC:
                registerA--;
                wroteA = true;
                break;
            case OR:
                registerA = (byte) (registerA | registerB);
                wroteA = true;
                break;
            case AND:
                registerA = (byte) (registerA & registerB);
                wroteA = true;
                break;
            case XOR:
                registerA = (byte)(registerA ^ registerB);
                wroteA = true;
                break;
            case JMZ:
                if (!zeroFlag) {
                    break;
                }
                byte newAddress = fetch();
                pc = newAddress;
                break;
            case JMNZ:
                if (zeroFlag) {
                    break;
                }
                newAddress = fetch();
                pc = newAddress;
                break;
            case JMP:
                newAddress = fetch();
                pc = newAddress;
                break;
            case HLT:
                halted = true;
                break;
            default:
                break;
        }
        if (wroteA) {
            zeroFlag = registerA == 0;
            regs[registerAddress1] = registerA;
        }
    }


}