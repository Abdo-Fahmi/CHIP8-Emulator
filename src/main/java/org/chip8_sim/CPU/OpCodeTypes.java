package org.chip8_sim.CPU;

public enum OpCodeTypes {
    OP_1nnn, OP_2nnn, // The entire code is unique
    OP_3xkk, OP_4xkk,
    OP_5xy0, OP_6xkk,
    OP_7xkk, OP_9xy0,
    OP_Annn, OP_Bnnn,
    OP_Cxkk, OP_Dxyn,

    OP_8xy0, OP_8xy1, // Table 8 codes, first digit is 8, last digit is unique
    OP_8xy2, OP_8xy3,
    OP_8xy4, OP_8xy5,
    OP_8xy6, OP_8xy7,
    OP_8xyE,

    OP_00E0, OP_00EE, // 00E but the last digit is unique

    OP_ExA1, OP_Ex9E, // First 2 digits are unique but the last 2 are different
    OP_Fx07, OP_Fx0A,
    OP_Fx15, OP_Fx18,
    OP_Fx1E, OP_Fx29,
    OP_Fx33, OP_Fx55,
    OP_Fx65,

    UNSUPPORTED;      // For errors when decoding the instruction
}
