package org.chip8_sim.CPU;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.chip8_sim.CPU.Addresses.START_ADDRESS;

public class Memory {
    private final char[] memory = new char[4096];

    public Memory() {
        char[] sprites = {
                0xf0, 0x90, 0x90, 0x90, 0xf0,
                0x20, 0x60, 0x20, 0x20, 0x70,
                0xf0, 0x10, 0xf0, 0x80, 0xf0,
                0xf0, 0x10, 0xf0, 0x10, 0xf0,
                0x90, 0x90, 0xf0, 0x10, 0x10,
                0xf0, 0x80, 0xf0, 0x10, 0xf0,
                0xf0, 0x80, 0xf0, 0x90, 0xf0,
                0xf0, 0x10, 0x20, 0x40, 0x40,
                0xf0, 0x90, 0xf0, 0x90, 0xf0,
                0xf0, 0x90, 0xf0, 0x10, 0xf0,
                0xf0, 0x90, 0xf0, 0x90, 0xf0,
                0xe0, 0x90, 0xe0, 0x90, 0xe0,
                0xf0, 0x80, 0x80, 0x80, 0xf0,
                0xe0, 0x90, 0x90, 0x90, 0xe0,
                0xf0, 0x80, 0xf0, 0x80, 0xf0,
                0xf0, 0x80, 0xf0, 0x80, 0x80
        };
        System.arraycopy(sprites, 0, memory, 0, sprites.length);
    }

    public void loadROM (String filePath) throws IOException {
        // Reading the ROM data from the file
        File ROM = new File(filePath);
        try (FileInputStream inputStream = new FileInputStream(ROM)) {
            byte[] arr = inputStream.readAllBytes();

            // Writing the data into memory
            for (int i = 0; i < arr.length; i++) {
                memory[i + START_ADDRESS.address] = (char) (arr[i] & 0xFF);
            }
        }
    }

    public char readInstruction(int index) {
        // Getting the next 2 instructions and combining them to make up the opcode
        char p1 = memory[index];
        char p2 = memory[index + 1];
        return (char) ((p1 << 8) | p2);
    }

    public void writeByte(int index, char b) {
        memory[index] = b;
    }

    public char readByte(int index) {
        return memory[index];
    }
}
