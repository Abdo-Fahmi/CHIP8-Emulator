package org.chip8_sim.CPU;

public class OpCode {
    private char opcode;
    private OpCodeTypes opcodeType;

    public char getOpcode() {
        return opcode;
    }

    public void setOpcode(char opcode) {
        this.opcode = opcode;
    }

    public OpCodeTypes getOpcodeType() {
        return opcodeType;
    }

    public void setOpcodeType(OpCodeTypes opcodeType) {
        this.opcodeType = opcodeType;
    }
}
