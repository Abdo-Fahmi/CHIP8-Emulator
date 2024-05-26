package org.chip8_sim.CPU;

import org.chip8_sim.Display.*;
import org.chip8_sim.Keyboard.Keypad;

import java.util.Random;

public class Chip {
    private final Stack stack = new Stack();
    private final Memory memory = new Memory();
    private final Keypad keypad = new Keypad();
    private final Screen screen = new Screen(keypad);
    private final OpCode opCode = new OpCode();
    private final char[] registers = new char[16]; // use char for most implementations as it is unsigned
    private char pc = Addresses.START_ADDRESS.address;
    private char index = 0;
    private char delayTimer = 0;
    private char soundTimer = 0; // can be byte but is char for convenience

    public void run() {
        int tickFrequency = 400;
        int soundFrequency = 60;
        long lastExecutionTime = System.currentTimeMillis();
        long lastTimerUpdate = lastExecutionTime;
        boolean skipTimer = false;

        while(true) {
            if(skipTimer || System.currentTimeMillis() - lastExecutionTime >= 1000/tickFrequency) {
                lastExecutionTime = System.currentTimeMillis();
                if(!emulationCycle()) break;
                if(System.currentTimeMillis() - lastExecutionTime >= 1000/ tickFrequency) {
                    skipTimer = true;
                    System.out.println("Skipping next instruction");
                } else skipTimer = false;
            }

            // Update timers
            if(System.currentTimeMillis() - lastTimerUpdate >= soundFrequency) {
                lastTimerUpdate = System.currentTimeMillis();

                if(delayTimer > 0) --delayTimer;
                if(soundTimer > 0) --soundTimer;
            }
        }
    }

    private boolean emulationCycle() {
        // Fetch
        char instruction = memory.readInstruction(pc);

        // Increment the pc for the next instruction
        pc += 2;

        // Decode
        decodeInstruction(instruction);
        if(opCode.getOpcodeType() == OpCodeTypes.UNSUPPORTED) {
            System.out.println("OPCODE NOT SUPPORTED: " + opCode.getOpcode());
            return false;
        }

        // Execute
        executeInstruction(opCode);
        return true;
    }

    public void decodeInstruction(char instruction) {
        // We have one opCode object that we keep modifying as the instructions change
        // We could use this method to create a new opcode each time with a constructor
        // ex. return new OpCode(instruction, OpCodeTypes);
        // but I think this is better?
        opCode.setOpcode(instruction);
        opCode.setOpcodeType(decodeInstructionType(instruction));
    }

    private OpCodeTypes decodeInstructionType(char code) {
        // Checking if it's the 0x00E code so that we can exit early
        if (code == 0x00E0) {
            return OpCodeTypes.OP_00E0;
        }
        if (code == 0x00EE) {
            return OpCodeTypes.OP_00EE;
        }

        // Using bitwise-AND to determine the OP code for each instruction
        // Conditions for each code subgroup shown in OpCodeTypes enum
        return switch (code & 0xF000) {
            case 0x1000 -> OpCodeTypes.OP_1nnn;
            case 0x2000 -> OpCodeTypes.OP_2nnn;
            case 0x3000 -> OpCodeTypes.OP_3xkk;
            case 0x4000 -> OpCodeTypes.OP_4xkk;
            case 0x5000 -> OpCodeTypes.OP_5xy0;
            case 0x6000 -> OpCodeTypes.OP_6xkk;
            case 0x7000 -> OpCodeTypes.OP_7xkk;
            case 0x8000 -> switch (code & 0x000F) {
                case 0x0000 -> OpCodeTypes.OP_8xy0;
                case 0x0001 -> OpCodeTypes.OP_8xy1;
                case 0x0002 -> OpCodeTypes.OP_8xy2;
                case 0x0003 -> OpCodeTypes.OP_8xy3;
                case 0x0004 -> OpCodeTypes.OP_8xy4;
                case 0x0005 -> OpCodeTypes.OP_8xy5;
                case 0x0006 -> OpCodeTypes.OP_8xy6;
                case 0x0007 -> OpCodeTypes.OP_8xy7;
                case 0x000E -> OpCodeTypes.OP_8xyE;
                default -> OpCodeTypes.UNSUPPORTED;
            };
            case 0x9000 -> OpCodeTypes.OP_9xy0;
            case 0xA000 -> OpCodeTypes.OP_Annn;
            case 0xB000 -> OpCodeTypes.OP_Bnnn;
            case 0xC000 -> OpCodeTypes.OP_Cxkk;
            case 0xD000 -> OpCodeTypes.OP_Dxyn;
            case 0xE000 -> switch (code & 0x00FF) {
                case 0X009E -> OpCodeTypes.OP_Ex9E;
                case 0X00A1 -> OpCodeTypes.OP_ExA1;
                default -> OpCodeTypes.UNSUPPORTED;
            };
            case 0xF000 -> switch (code & 0x00FF) {
                case 0x0007 -> OpCodeTypes.OP_Fx07;
                case 0x000A -> OpCodeTypes.OP_Fx0A;
                case 0x0015 -> OpCodeTypes.OP_Fx15;
                case 0x0018 -> OpCodeTypes.OP_Fx18;
                case 0X001E -> OpCodeTypes.OP_Fx1E;
                case 0X0029 -> OpCodeTypes.OP_Fx29;
                case 0X0033 -> OpCodeTypes.OP_Fx33;
                case 0X0055 -> OpCodeTypes.OP_Fx55;
                case 0X0065 -> OpCodeTypes.OP_Fx65;
                default -> OpCodeTypes.UNSUPPORTED;
            };
            default -> OpCodeTypes.UNSUPPORTED;
        };
    }

    public void executeInstruction(OpCode opcode) {
        char instruction = opcode.getOpcode();
        OpCodeTypes instructionType = opcode.getOpcodeType();

        switch (instructionType) {
            case OP_1nnn -> OP_1nnn(instruction);
            case OP_2nnn -> OP_2nnn(instruction);
            case OP_3xkk -> OP_3xkk(instruction);
            case OP_4xkk -> OP_4xkk(instruction);
            case OP_5xy0 -> OP_5xy0(instruction);
            case OP_6xkk -> OP_6xkk(instruction);
            case OP_7xkk -> OP_7xkk(instruction);
            case OP_9xy0 -> OP_9xy0(instruction);
            case OP_Annn -> OP_Annn(instruction);
            case OP_Bnnn -> OP_Bnnn(instruction);
            case OP_Cxkk -> OP_Cxkk(instruction);
            case OP_Dxyn -> OP_Dxyn(instruction);
            case OP_8xy0 -> OP_8xy0(instruction);
            case OP_8xy1 -> OP_8xy1(instruction);
            case OP_8xy2 -> OP_8xy2(instruction);
            case OP_8xy3 -> OP_8xy3(instruction);
            case OP_8xy4 -> OP_8xy4(instruction);
            case OP_8xy5 -> OP_8xy5(instruction);
            case OP_8xy6 -> OP_8xy6(instruction);
            case OP_8xy7 -> OP_8xy7(instruction);
            case OP_8xyE -> OP_8xyE(instruction);
            case OP_00E0 -> OP_00E0();
            case OP_00EE -> OP_00EE();
            case OP_ExA1 -> OP_ExA1(instruction);
            case OP_Ex9E -> OP_Ex9E(instruction);
            case OP_Fx07 -> OP_Fx07(instruction);
            case OP_Fx0A -> OP_Fx0A(instruction);
            case OP_Fx15 -> OP_Fx15(instruction);
            case OP_Fx18 -> OP_Fx18(instruction);
            case OP_Fx1E -> OP_Fx1E(instruction);
            case OP_Fx29 -> OP_Fx29(instruction);
            case OP_Fx33 -> OP_Fx33(instruction);
            case OP_Fx55 -> OP_Fx55(instruction);
            case OP_Fx65 -> OP_Fx65(instruction);
        }
    }

    private void OP_00E0() {
        // This can be called from the switch statement directly but will be put here for clarity
       screen.clearDisplay();
    }

    private void OP_00EE() {
        // Return from subroutine
        pc = stack.getAddress();
    }

    private void OP_1nnn(char instruction) {
        // Jump to address nnn
        pc = (char) (instruction & 0x0FFF);
    }

    private void OP_2nnn(char instruction) {
        // Call subroutine at nnn
        char address = (char) (instruction & 0x0FFF);
        stack.setAddress(pc);
        pc = address;
    }

    private void OP_3xkk(char instruction) {
        // Skip next instruction if Vx == kk
        char x = (char) ((instruction & 0x0F00) >> 8);
        char kk = (char) (instruction & 0x00FF);

        if(registers[x] == kk) pc += 2;
    }

    private void OP_4xkk(char instruction) {
        // Skip next instruction if Vx != kk
        char x = (char) ((instruction & 0x0F00) >> 8);
        char kk = (char) (instruction & 0x00FF);

        if(registers[x] != kk) pc += 2;
    }

    private void OP_5xy0(char instruction) {
        // Skip next instruction if Vx == Vy
        char Vx = (char) ((instruction & 0x0F00) >> 8);
        char Vy = (char) ((instruction & 0x00F0) >> 4);

        if(registers[Vx] == registers[Vy]) pc+=2;
    }

    private void OP_6xkk(char instruction) {
        // Set Vx = kk
        char Vx = (char) ((instruction & 0x0F00) >> 8);
        char kk = (char) (instruction & 0x00FF);

        registers[Vx] = kk;
    }

    private void OP_7xkk(char instruction) {
        // Add kk to Vx
        char Vx = (char) ((instruction & 0x0F00) >> 8);
        char kk = (char) (instruction & 0x00FF);

        registers[Vx] += kk;
    }

    private void OP_8xy0(char instruction) {
        // Set Vx = Vy
        char Vx = (char) ((instruction & 0x0F00) >> 8);
        char Vy = (char) ((instruction & 0x00F0) >> 4);

        registers[Vx] = registers[Vy];
    }

    private void OP_8xy1(char instruction) {
        // Set Vx to Vx OR Vy
        char Vx = (char) ((instruction & 0x0F00) >> 8);
        char Vy = (char) ((instruction & 0x00F0) >> 4);

        registers[Vx] |= registers[Vy];
    }

    private void OP_8xy2(char instruction) {
        // Set Vx to Vx AND Vy
        char Vx = (char) ((instruction & 0x0F00) >> 8);
        char Vy = (char) ((instruction & 0x00F0) >> 4);

        registers[Vx] &= registers[Vy];
    }

    private void OP_8xy3(char instruction) {
        // Set Vx to Vx XOR Vy
        char Vx = (char) ((instruction & 0x0F00) >> 8);
        char Vy = (char) ((instruction & 0x00F0) >> 4);

        registers[Vx] ^= registers[Vy];
    }

    // TODO test different methods to check the sum
    private void OP_8xy4(char instruction) {
        // Set Add Vy to Vx, set VF to carry
        char Vx = (char) ((instruction & 0x0F00) >> 8);
        char Vy = (char) ((instruction & 0x00F0) >> 4);

        char sum = (char) (registers[Vx] + registers[Vy]);
        if((sum & 0xFF00) > 0) {
            registers[Addresses.VF_REGISTER.address] = 0x01;
            sum &= 0x00FF;
        } else registers[Addresses.VF_REGISTER.address] = 0x00;

        registers[Vx] = sum;
    }

    private void OP_8xy5(char instruction) {
        // Sub Vy from Vx, VF = 1 if Vx < Vy, else VF = 0
        char Vx = (char) ((instruction & 0x0F00) >> 8);
        char Vy = (char) ((instruction & 0x00F0) >> 4);
        char result = (char) (registers[Vx] - registers[Vy]);

        if (registers[Vx] > registers[Vy]) {
            registers[Addresses.VF_REGISTER.address] = 0x01;
        } else {
            registers[Addresses.VF_REGISTER.address] = 0x00;
            result &= 0x00FF;
        }
        registers[Vx] = result;
    }

    private void OP_8xy6(char instruction) {
        // Divide Vx by 2
        char Vx = (char) ((instruction & 0x0F00) >> 8);

        // Save LSB in Vf
        registers[Addresses.VF_REGISTER.address] = (char) (registers[Vx] & 0x1);

        // Register is right-shifted, result is equivalent to division
        registers[Vx] >>= 1;
    }

    private void OP_8xy7(char instruction) {
        // Sub Vx = Vy - Vx, VF = 0 if Vx < Vy, else VF = 1
        char Vx = (char) ((instruction & 0x0F00) >> 8);
        char Vy = (char) ((instruction & 0x00F0) >> 4);
        char result = (char) (registers[Vy] - registers[Vx]);
        if (registers[Vy] > registers[Vx]) {
            registers[Addresses.VF_REGISTER.address] = 0x01;
        } else {
            registers[Addresses.VF_REGISTER.address] = 0x00;
            result &= 0x00FF;
        }
        registers[Vx] = result;
    }

    private void OP_8xyE(char instruction) {
        // If MSD of Vx is 1, VF = 0, else VF = 1, then multiply by 2
        char Vx = (char) ((instruction & 0x0F00) >> 8);

        // Save MSB to VF
        registers[Addresses.VF_REGISTER.address] = (char) ((registers[Vx] & 0x80) >> 7);

        // registers[Vx] <<= 1; gives a wrong answer , so we use this method instead to correct it.
        registers[Vx] = (char) ((registers[Vx] << 1) & 0x00FF);
    }

    private void OP_9xy0(char instruction) {
        // Skip next instruction if Vx != Vy
        char Vx = (char) ((instruction & 0x0F00) >> 8);
        char Vy = (char) ((instruction & 0x00F0) >> 4);

        if(registers[Vx] != registers[Vy]) pc+=2;
    }

    private void OP_Annn(char instruction) {
        // Set Index to nnn
        index = (char) (instruction & 0x0FFF);
    }

    private void OP_Bnnn(char instruction) {
        // Jump to location nnn + V0
        char address = (char) (instruction & 0x0FFF);
        pc = (char) (address + registers[0]);
    }

    private void OP_Cxkk(char instruction) {
        // Set Vx to random byte AND kk
        char Vx = (char) ((instruction & 0x0F00) >> 8);
        char b = (char) (instruction & 0x00FF);

        registers[Vx] = (char) (new Random().nextInt(256) & b);
    }

    private void OP_Dxyn(char instruction) {
        // Display n-byte sprite starting at memory location I at (Vx, Vy), set VF = collision.
        char Vx = (char) ((instruction & 0x0F00) >> 8);
        char Vy = (char) ((instruction & 0x00F0) >> 4);
        char nibble = (char) (instruction & 0x000F);

        Coordinate position = new Coordinate(registers[Vx], registers[Vy]);
        char[] sprite = new char[nibble];
        for (int i = 0; i < nibble; i++) {
            sprite[i] = memory.readByte(i + index);
        }
        boolean collision = screen.drawSprite(position, sprite);
        if (collision) registers[Addresses.VF_REGISTER.address] = 0x1;
        else registers[Addresses.VF_REGISTER.address] = 0x0;
    }

    private void OP_Ex9E(char instruction) {
        // Skip next instruction if key of value Vx is pressed
        char Vx = (char) ((instruction & 0x0F00) >> 8);
        char key = registers[Vx];

        if(keypad.getPressedKey() == key) {
            pc += 2;
        }
    }

    private void OP_ExA1(char instruction) {
        // Skip next instruction if key of value Vx is NOT pressed
        char Vx = (char) ((instruction & 0x0F00) >> 8);
        char key = registers[Vx];

        if(keypad.getPressedKey() != key) {
            pc += 2;
        }
    }

    private void OP_Fx07(char instruction) {
        // Set Vx to delay timer value
        char Vx = (char) ((instruction & 0x0F00) >> 8);
        registers[Vx] = delayTimer;
    }

    private void OP_Fx0A(char instruction) {
        // Wait until the next key is pressed, store its value in Vx
        char Vx = (char) ((instruction & 0x0F00) >> 8);
        boolean keySet = false;

        // We could also decrement pc by 2 (repeat current instruction) as an alternative to wait
        while(!keySet) {
            char currentKey = keypad.getPressedKey();
            if(currentKey != 0xFFFF) {
                registers[Vx] = currentKey;
                keySet = true;
            } else {
                try {
                    Thread.sleep(150);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void OP_Fx15(char instruction) {
        // Set delay timer to Vx
        char Vx = (char) ((instruction & 0x0F00) >> 8);

        delayTimer = registers[Vx];
    }

    private void OP_Fx18(char instruction) {
        // Set sound timer to Vx
        char Vx = (char) ((instruction & 0x0F00) >> 8);

        soundTimer = registers[Vx];
    }

    private void OP_Fx1E(char instruction) {
        // Add Vx to index
        char Vx = (char) ((instruction & 0x0F00) >> 8);

        index += registers[Vx];
    }

    // TODO Check if starting from FONT_START address is necessary
    private void OP_Fx29(char instruction) {
        // Set I to location of sprite for digit Vx
        char Vx = (char) ((instruction & 0x0F00) >> 8);
        char digit = registers[Vx];

        index = (char) ((digit * 5));
    }

    private void OP_Fx33(char instruction) {
        // Store BCD representation of Vx in memory locations I, I+1, and I+2.
        // Hundreds in I, tens in I+1, ones in I+3.
        char Vx = (char) ((instruction & 0x0F00) >> 8);
        char value = registers[Vx];

        // Placing ones
        memory.writeByte(index + 2, (char) (value%10));
        value /= 10;

        // Placing tens
        memory.writeByte(index + 1, (char) (value%10));
        value /= 10;

        // Placing hundreds
        memory.writeByte(index, (char) (value%10));
    }

    private void OP_Fx55(char instruction) {
        // Store registers V0 -> Vx in memory, starting at I
        char Vx = (char) ((instruction & 0x0F00) >> 8);

        for (int i = 0; i < Vx; i++) {
            memory.writeByte(index + i, registers[i]);
        }
    }

    private void OP_Fx65(char instruction) {
        // Read registers V0 -> Vx from memory, starting at I
        char Vx = (char) ((instruction & 0x0F00) >> 8);

        for (int i = 0; i < Vx; i++) {
            registers[i] = memory.readByte(index + i);
        }
    }

    // ------------------ Used for testing ------------------

    public char getIndex() {
        return index;
    }

    public void setIndex(char val) {
        this.index = val;
    }

    public char[] getRegisters() {
        return registers;
    }

    public OpCode getOpCode() {
        return opCode;
    }

    public char getProgramCounter() {
        return pc;
    }

    public void setDelayTimer(char val) {
        this.delayTimer = val;
    }

    public char getSoundTimer() {
        return soundTimer;
    }

    public void setSoundTimer(char soundTimer) {
        this.soundTimer = soundTimer;
    }

    public char getDelayTimer() {
        return this.delayTimer;
    }

    public Stack getStack() {
        return stack;
    }

    public Memory getMemory() {
        return memory;
    }

    public Screen getScreen() {
        return screen;
    }
}