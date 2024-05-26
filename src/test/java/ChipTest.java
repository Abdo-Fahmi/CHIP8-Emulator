import org.chip8_sim.CPU.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ChipTest {
    private Chip testChip;
    private Memory testMemory;
    private Stack testStack;
    private OpCode opCode;

    @BeforeEach
    public void before() {
        this.testChip = TestData.testChip();
        this.opCode = testChip.getOpCode();
        this.testStack = testChip.getStack();
        this.testMemory = testChip.getMemory();
    }

    @AfterEach
    public void tearDown() {
       testChip = null;
       testStack = null;
       testMemory = null;
       opCode = null;
    }

    @Nested
    public class jumpAddressTest {
        char instruction = 0x1AD3;
        @Test
        public void jumpAddressDecodeTest() {
            testChip.decodeInstruction(instruction);

            assertEquals(OpCodeTypes.OP_1nnn, opCode.getOpcodeType());
        }

        @Test
        public void jumpAddressExecutionTest() {
            testChip.decodeInstruction(instruction);
            testChip.executeInstruction(opCode);

            assertEquals(0x0AD3, testChip.getProgramCounter());
        }
    }

    @Nested
    public class callAddressTest {
        char instruction = 0x2FD0;
        @Test
        public void callAddressDecodeTest() {
            testChip.decodeInstruction(instruction);

            assertEquals(OpCodeTypes.OP_2nnn, opCode.getOpcodeType());
        }

        @Test
        public void callAddressExecuteTest() {
           testChip.decodeInstruction(instruction);
           testChip.executeInstruction(opCode);

            assertEquals(0xFD0, testChip.getProgramCounter());
            assertEquals(0x200, testStack.getAddress());
        }
    }

    @Nested
    public class skipIfVxEqualsByteTest {
        char instruction = 0x3AF2;
        @Test
        public void skipIfVxEqualsByteDecodeTest() {
            testChip.decodeInstruction(instruction);

            assertEquals(OpCodeTypes.OP_3xkk, opCode.getOpcodeType());
        }

        @Test
        public void skipIfVxEqualsByteTrueExecuteTest() {
            // Setting the register to equal the Byte
            testChip.getRegisters()[0xA] = 0xF2;

            testChip.decodeInstruction(instruction);
            testChip.executeInstruction(opCode);

           assertEquals(Addresses.START_ADDRESS.address + 2, testChip.getProgramCounter());
        }

        @Test
        public void skipIfVxEqualsByteFalseExecuteTest() {
            // Setting the register to NOT equal the Byte
            testChip.getRegisters()[0xA] = 0x0;

            testChip.decodeInstruction(instruction);
            testChip.executeInstruction(opCode);

            assertEquals(Addresses.START_ADDRESS.address, testChip.getProgramCounter());
        }
    }

    @Nested
    public class skipIfVxDoesntEqualByteTest {
        char instruction = 0x4AF2;
        @Test
        public void skipVxDoesntEqualByteDecodeTest() {
            testChip.decodeInstruction(instruction);

            assertEquals(OpCodeTypes.OP_4xkk, opCode.getOpcodeType());
        }

        @Test
        public void skipIfVxDoesntEqualByteTrueExecuteTest() {
            // Setting the register to NOT equal the Byte
            testChip.getRegisters()[0xA] = 0x0;

            testChip.decodeInstruction(instruction);
            testChip.executeInstruction(opCode);

            assertEquals(Addresses.START_ADDRESS.address + 2, testChip.getProgramCounter());
        }

        @Test
        public void skipIfVxDoesntEqualByteFalseExecuteTest() {
            // Setting the register to NOT equal the Byte
            testChip.getRegisters()[0xA] = 0xF2;

            testChip.decodeInstruction(instruction);
            testChip.executeInstruction(opCode);

            assertEquals(Addresses.START_ADDRESS.address, testChip.getProgramCounter());
        }
    }

    @Nested
    public class skipIfVxEqualsVyTest {
        char instruction = 0x5CB0;
        @Test
        public void skipIfVxEqualsVyDecodeTest() {
            testChip.decodeInstruction(instruction);
            assertEquals(OpCodeTypes.OP_5xy0, opCode.getOpcodeType());
        }

        @Test
        public void skipIfVxEqualsVyTrueExecuteTest() {
            testChip.getRegisters()[0xC] = 0xFF;
            testChip.getRegisters()[0xB] = 0xFF;

            testChip.decodeInstruction(instruction);
            testChip.executeInstruction(opCode);

            assertEquals(Addresses.START_ADDRESS.address + 2, testChip.getProgramCounter());
        }

        @Test
        public void skipIfVxEqualsVyFalseExecuteTest() {
            testChip.getRegisters()[0xA] = 0x0;
            testChip.getRegisters()[0xB] = 0xFF;

            testChip.decodeInstruction(instruction);
            testChip.executeInstruction(opCode);

            assertEquals(Addresses.START_ADDRESS.address, testChip.getProgramCounter());
        }
    }

    @Nested
    public class setVxToByteTest {
        char instruction = 0x6C2D;
        @Test
        public void setVxToByteDecodeTest() {
            testChip.decodeInstruction(instruction);
            assertEquals(OpCodeTypes.OP_6xkk, opCode.getOpcodeType());
        }

        @Test
        public void setVxToByteExecuteTest() {
            testChip.decodeInstruction(instruction);
            testChip.executeInstruction(opCode);

            assertEquals(0x2D, testChip.getRegisters()[0x000C]);
        }
    }

    @Nested
    public class addByteToVxTest {
        char instruction = 0x7010;

        @Test
        public void addByteToVxDecodeTest() {
            testChip.decodeInstruction(instruction);
            assertEquals(OpCodeTypes.OP_7xkk, opCode.getOpcodeType());
        }

        @Test
        public void addByteToVxExecuteTest() {
            testChip.getRegisters()[0] = 0x10;
            testChip.decodeInstruction(instruction);
            testChip.executeInstruction(opCode);
            assertEquals(0x20, testChip.getRegisters()[0]);
        }
    }

    @Nested
    public class setVxToVyTest {
        char instruction = 0x8F30;

        @Test
        public void setVxToVyDecodeTest() {
            testChip.decodeInstruction(instruction);
            assertEquals(OpCodeTypes.OP_8xy0, opCode.getOpcodeType());
        }

        @Test
        public void setVxToVyExecuteTest() {
            testChip.getRegisters()[0xF] = 0xA2;
            testChip.getRegisters()[0x3] = 0xB2;

            testChip.decodeInstruction(instruction);
            testChip.executeInstruction(opCode);

            assertEquals(testChip.getRegisters()[0x000F], testChip.getRegisters()[0x0003]);
        }
    }

    @Nested
    public class setVxToVxORVyTest {
        char instruction =0x8A21;
        @Test
        public void setVxToVxORVyDecodeTest() {
            testChip.decodeInstruction(instruction);
            assertEquals(OpCodeTypes.OP_8xy1, opCode.getOpcodeType());
        }

        @Test
        public void setVxToVxORVyExecuteTest() {
            testChip.getRegisters()[0xA] = 0xA2;
            testChip.getRegisters()[0x2] = 0xB4;

            testChip.decodeInstruction(instruction);
            testChip.executeInstruction(opCode);

            assertEquals(0xA2 | 0xB4, testChip.getRegisters()[0xA]);
        }
    }

    @Nested
    public class setVxToVxANDVyTest {
        char instruction = 0x8DC2;
        @Test
        public void setVxToVxANDVyDecodeTest() {
            testChip.decodeInstruction(instruction);
            assertEquals(OpCodeTypes.OP_8xy2, opCode.getOpcodeType());
        }

        @Test
        public void setVxToVxANDVyExecuteTest() {
            // We can test bitwise operations this way as well
            // Both have given me the same results, so I will continue as above
            testChip.getRegisters()[0xD] = 0b00101101;
            testChip.getRegisters()[0xC] = 0b01001011;
            short ans = 0b00001001;

            testChip.decodeInstruction(instruction);
            testChip.executeInstruction(opCode);

            assertEquals(ans, testChip.getRegisters()[0xD]);
        }
    }

    @Nested
    public class setVxToVxXORVyTest {
        char instruction = 0x8AB3;
        @Test
        public void setVxToVxXORVyDecodeTest() {
            testChip.decodeInstruction(instruction);
            assertEquals(OpCodeTypes.OP_8xy3, opCode.getOpcodeType());
        }

        @Test
        public void setVxToVxANDVyExecuteTest() {
            testChip.getRegisters()[0xA] = 0xA2;
            testChip.getRegisters()[0xB] = 0xB4;

            testChip.decodeInstruction(instruction);
            testChip.executeInstruction(opCode);

            assertEquals(0xA2 ^ 0xB4, testChip.getRegisters()[0xA]);
        }
    }

    @Nested
    public class addVyToVxTest {
        char instruction = 0x8AB4;
        @Test
        public void addVyToVxDecodeTest() {
            testChip.decodeInstruction(instruction);
            assertEquals(OpCodeTypes.OP_8xy4, opCode.getOpcodeType());
        }

        @Test
        public void setVxToVxANDVyWithCarryExecuteTest() {
            testChip.getRegisters()[0xA] = 0xA2;
            testChip.getRegisters()[0xB] = 0xB4;

            testChip.decodeInstruction(instruction);
            testChip.executeInstruction(opCode);

            short ans = (0xA2 + 0xB4) & 0xFF;

            assertEquals(ans, testChip.getRegisters()[0xA]);
            assertEquals(0x01, testChip.getRegisters()[Addresses.VF_REGISTER.address]);
        }

        @Test
        public void setVxToVxANDVyWithOutCarryExecuteTest() {
            testChip.getRegisters()[0xA] = 0x2;
            testChip.getRegisters()[0xB] = 0x4;

            testChip.decodeInstruction(instruction);
            testChip.executeInstruction(opCode);

            assertEquals(0x2 + 0x4, testChip.getRegisters()[0xA]);
            assertEquals(0x00, testChip.getRegisters()[Addresses.VF_REGISTER.address]);
        }
    }

    @Nested
    public class subVyFromVxTest {
        char instruction = 0x8AB5;
        @Test
        public void subVyFromVxDecodeTest() {
            testChip.decodeInstruction(instruction);
            assertEquals(OpCodeTypes.OP_8xy5, opCode.getOpcodeType());
        }

        @Test
        public void subVyFromVxNoBorrowExecuteTest() {
            testChip.getRegisters()[0xA] = 0xB4;
            testChip.getRegisters()[0xB] = 0xA4;

            testChip.decodeInstruction(instruction);
            testChip.executeInstruction(opCode);

            assertEquals(0xB4 - 0xA4, testChip.getRegisters()[0xA]);
            assertEquals(0x01, testChip.getRegisters()[Addresses.VF_REGISTER.address]);
        }

        @Test
        public void subVyFromVxWithBorrowExecuteTest() {
            testChip.getRegisters()[0xA] = 0b00101101;
            testChip.getRegisters()[0xB] = 0b01001011;

            testChip.decodeInstruction(instruction);
            testChip.executeInstruction(opCode);

            assertEquals(0b11100010, testChip.getRegisters()[0xA]);
            assertEquals(0x00, testChip.getRegisters()[Addresses.VF_REGISTER.address]);
        }
    }

    @Nested
    public class shrVxVyTest {
        char instruction = 0x8AB6;
        @Test
        public void shrVxVyDecodeTest() {
            testChip.decodeInstruction(instruction);
            assertEquals(OpCodeTypes.OP_8xy6, opCode.getOpcodeType());
        }

        @Test
        public void shrVxVyZeroLSBExecuteTest () {
            testChip.getRegisters()[0xA] = 0b00101100;

            testChip.decodeInstruction(instruction);
            testChip.executeInstruction(opCode);

            assertEquals(0b00010110, testChip.getRegisters()[0xA]);
            assertEquals(0x00, testChip.getRegisters()[Addresses.VF_REGISTER.address]);
        }

        @Test
        public void shrVxVyOneLSBExecuteTest() {
            testChip.getRegisters()[0xA] = 0b00101101;

            testChip.decodeInstruction(instruction);
            testChip.executeInstruction(opCode);

            assertEquals(0b00010110, testChip.getRegisters()[0xA]);
            assertEquals(0x01, testChip.getRegisters()[Addresses.VF_REGISTER.address]);
        }
    }

    @Nested
    public class subNVxVyTest {
        char instruction = 0x8AD7;
        @Test
        public void subNVxVyDecodeTest() {
            testChip.decodeInstruction(instruction);
            assertEquals(OpCodeTypes.OP_8xy7, opCode.getOpcodeType());
        }

        @Test
        public void subNVxVyWithBorrowExecuteTest() {
            testChip.getRegisters()[0xA] = 0b01001011;
            testChip.getRegisters()[0xD] = 0b00101101;

            testChip.decodeInstruction(instruction);
            testChip.executeInstruction(opCode);

            assertEquals(0b11100010,testChip.getRegisters()[0xA]);
            assertEquals(0x00, testChip.getRegisters()[Addresses.VF_REGISTER.address]);
        }

        @Test
        public void subNVxVyNoBorrowExecuteTest() {
            testChip.getRegisters()[0xA] = 0xA4;
            testChip.getRegisters()[0xD] = 0xB4;

            testChip.decodeInstruction(instruction);
            testChip.executeInstruction(opCode);

            assertEquals(0xB4 - 0xA4, testChip.getRegisters()[0xA]);
            assertEquals(0x01, testChip.getRegisters()[Addresses.VF_REGISTER.address]);
        }
    }

    @Nested
    public class shlVxVyTest {
        char instruction = 0x8AEE;
        @Test
        public void shlVxVyDecodeTest() {
            testChip.decodeInstruction(instruction);
            assertEquals(OpCodeTypes.OP_8xyE, opCode.getOpcodeType());
        }

        @Test
        public void shlVxVyZeroMSBExecuteTest() {
            testChip.getRegisters()[0xA] = 0b00110110;

            testChip.decodeInstruction(instruction);
            testChip.executeInstruction(opCode);

            assertEquals(0b01101100, testChip.getRegisters()[0xA]);
            assertEquals(0x00, testChip.getRegisters()[Addresses.VF_REGISTER.address]);
        }

        @Test
        public void shlVxVyOneMSBExecuteTest() {
            testChip.getRegisters()[0x000A] = 0b10110110;

            testChip.decodeInstruction(instruction);
            testChip.executeInstruction(opCode);

            assertEquals(0b01101100, testChip.getRegisters()[0xA]);
            assertEquals(0x01, testChip.getRegisters()[Addresses.VF_REGISTER.address]);
        }
    }

    @Nested
    public class sneVxVyTest {
        char instruction = 0x97D0;
        @Test
        public void sneVxVyDecodeTest() {
            testChip.decodeInstruction(instruction);
            assertEquals(OpCodeTypes.OP_9xy0, opCode.getOpcodeType());
        }

        @Test
        public void sneVxVyTrueExecuteTest() {
           testChip.getRegisters()[0x7] = 0x5F;
           testChip.getRegisters()[0xD] = 0x0F;

           testChip.decodeInstruction(instruction);
           testChip.executeInstruction(opCode);

           assertEquals(Addresses.START_ADDRESS.address + 2, testChip.getProgramCounter());
        }

        @Test
        public void sneVxVyFalseExecuteTest() {
            testChip.getRegisters()[0x7] = 0x5F;
            testChip.getRegisters()[0xD] = 0x5F;

            testChip.decodeInstruction(instruction);
            testChip.executeInstruction(opCode);

            assertEquals(Addresses.START_ADDRESS.address, testChip.getProgramCounter());
        }
    }

    @Nested
    public class setIndexToNNNTest {
        char instruction = 0xA782;
        @Test
        public void setIndexToNNNDecodeTest() {
            testChip.decodeInstruction(instruction);
            assertEquals(OpCodeTypes.OP_Annn, opCode.getOpcodeType());
        }
        @Test
        public void setIndexToNNNExecuteTest() {
            testChip.decodeInstruction(instruction);
            testChip.executeInstruction(opCode);

            assertEquals(0x782, testChip.getIndex());
        }
    }

    @Nested
    public class jumpToNNNPlusV0Test {
        char instruction = 0xBC31;
        @Test
        public void jumpToNNNPlusV0DecodeTest() {
            testChip.decodeInstruction(instruction);
            assertEquals(OpCodeTypes.OP_Bnnn, opCode.getOpcodeType());
        }

        @Test
        public void jumpToNNNPlusV0ExecuteTest() {
           testChip.getRegisters()[0x0] = 0x1;

           testChip.decodeInstruction(instruction);
           testChip.executeInstruction(opCode);

           assertEquals(0xC31 + 0x1, testChip.getProgramCounter());
        }
    }

    @Nested
    public class setVxToRandomANDByteTest {
        char instruction = 0xC671;
        @Test
        public void setVxToRandomANDByteDecodeTest() {
            testChip.decodeInstruction(instruction);
            assertEquals(OpCodeTypes.OP_Cxkk, opCode.getOpcodeType());
        }

        @Test
        public void setVxToRandomANDByteExecuteTest() {
            testChip.decodeInstruction(instruction);
            testChip.executeInstruction(opCode);

            assertTrue(testChip.getRegisters()[0x6] >= 0 && testChip.getRegisters()[0x6] <= 255);
        }
    }

    @Nested
    public class drawNAtVxVyTest {
        @Test
        public void drawNAtVxVyDecodeTest() {
            char instruction = 0xD21F;
            testChip.decodeInstruction(instruction);
            assertEquals(OpCodeTypes.OP_Dxyn, opCode.getOpcodeType());
        }
    }

    @Nested
    public class skipIfVxValueIsPressedTest {
        @Test
        public void skipIfVxValueIsPressedDecodeTest() {
            char instruction = 0xE59E;
            testChip.decodeInstruction(instruction);
            assertEquals(OpCodeTypes.OP_Ex9E, opCode.getOpcodeType());
        }
    }

    @Nested
    public class skipIfVxValueIsNotPressedTest {
        @Test
        public void skipIfVxValueIsNotPressedDecodeTest() {
            char instruction = 0xE5A1;
            testChip.decodeInstruction(instruction);
            assertEquals(OpCodeTypes.OP_ExA1, opCode.getOpcodeType());
        }
    }

    @Nested
    public class setVxToDelayTimerValue {
        char instruction = 0xF507;
        @Test
        public void setVxToDelayTimerValueDecode() {
            testChip.decodeInstruction(instruction);
            assertEquals(OpCodeTypes.OP_Fx07, opCode.getOpcodeType());
        }

        @Test
        public void setVxToDelayTimerExecuteTest() {
            testChip.setDelayTimer((char) 0x12);

            testChip.decodeInstruction(instruction);
            testChip.executeInstruction(opCode);

            assertEquals(0x12, testChip.getRegisters()[5]);
        }
    }

    @Nested
    public class storeValueOfPressedKeyTest {
        @Test
        public void storeValueOfPressedKeyDecodeTest() {
            char instruction = 0xFC0A;
            testChip.decodeInstruction(instruction);
            assertEquals(OpCodeTypes.OP_Fx0A, opCode.getOpcodeType());
        }
    }

    @Nested
    public class setDelayTimerToVxTest {
        char instruction = 0xFD15;
        @Test
        public void setDelayTimerToVxDecodeTest() {
            testChip.decodeInstruction(instruction);
            assertEquals(OpCodeTypes.OP_Fx15, opCode.getOpcodeType());
        }

        @Test
        public void setDelayTimerToVxExecuteTest() {
            testChip.getRegisters()[0xD] = 0x1;

            testChip.decodeInstruction(instruction);
            testChip.executeInstruction(opCode);

            assertEquals(0x1, testChip.getDelayTimer());
        }
    }

    @Nested
    public class setSoundTimerToVxTest {
        char instruction = 0xFD18;
        @Test
        public void setSoundTimerToVxDecodeTest() {
            testChip.decodeInstruction(instruction);
            assertEquals(OpCodeTypes.OP_Fx18, opCode.getOpcodeType());
        }

        @Test
        public void setSoundTimerToVxExecuteTest() {
            testChip.getRegisters()[0xD] = 0xF3;

            testChip.decodeInstruction(instruction);
            testChip.executeInstruction(opCode);

            assertEquals(0xF3, testChip.getSoundTimer());
        }
    }

    @Nested
    public class addVxToIndexTest {
        char instruction = 0xFB1E;
        @Test
        public void addVxToIndexDecodeText() {
            testChip.decodeInstruction(instruction);
            assertEquals(OpCodeTypes.OP_Fx1E, opCode.getOpcodeType());
        }

        @Test
        public void addVxToIndexExecuteTest() {
            testChip.getRegisters()[0xB] = 0xAB;
            testChip.setIndex((char) 0xFC);

            testChip.decodeInstruction(instruction);
            testChip.executeInstruction(opCode);

            assertEquals( 0xAB + 0xFC, testChip.getIndex());
        }
    }

    @Nested
    public class setIToLocationOfVxTest {
        char instruction = 0xFB29;
        @Test
        public void setIToLocationOfVxDecodeTest() {
            testChip.decodeInstruction(instruction);
            assertEquals(OpCodeTypes.OP_Fx29, opCode.getOpcodeType());
        }

        @Test
        public void setIToLocationOfVxExecuteTest() {
            testChip.getRegisters()[0xB] = 0x5;

            testChip.decodeInstruction(instruction);
            testChip.executeInstruction(opCode);

            assertEquals(25, testChip.getIndex());
        }
    }

    @Nested
    public class storeBCDOfVxTest {
        char instruction = 0xFC33;
        @Test
        public void storeBCDOfVxDecodeTest() {
            testChip.decodeInstruction(instruction);
            assertEquals(OpCodeTypes.OP_Fx33, opCode.getOpcodeType());
        }

        @Test
        public void storeBCDOfVxExecuteTest() {
            testChip.getRegisters()[0xC] = 0xFF;
            // Setting an address to the index pointer
            testChip.setIndex((char) 350);

            testChip.decodeInstruction(instruction);
            testChip.executeInstruction(opCode);

            assertEquals(2, testMemory.readByte(testChip.getIndex()));
            assertEquals(5, testMemory.readByte(testChip.getIndex() + 1));
            assertEquals(5, testMemory.readByte(testChip.getIndex() + 2));
        }
    }

    @Nested
    public class storeFromV0ToVxTest {
        char instruction = 0xFB55;
        @Test
        public void storeFromV0ToVxDecodeTest() {
            testChip.decodeInstruction(instruction);
            assertEquals(OpCodeTypes.OP_Fx55, opCode.getOpcodeType());
        }

        @Test
        public void storeV0ToVxExecuteTest() {
            char[] values = {0xFF, 0xAB, 0xBC, 0xAF, 0x12, 0x83, 0x1F};
            for (int i = 0; i < values.length; i++) {
                testChip.getRegisters()[i] = values[i];
            }
            testChip.setIndex((char) 250);
            testChip.decodeInstruction(instruction);
            testChip.executeInstruction(opCode);

            for (int i = 0; i < values.length; i++) {
                assertEquals(values[i], testMemory.readByte(250 + i));
            }
        }
    }

    @Nested
    public class readFromV0ToVxTest {
        char instruction = 0xFB65;
        @Test
        public void readFromV0ToVxDecodeTest() {
            testChip.decodeInstruction(instruction);
            assertEquals(OpCodeTypes.OP_Fx65, opCode.getOpcodeType());
        }

        @Test
        public void readFromV0ToVxExecuteTest() {
            char[] values = {0xFF, 0xAB, 0xBC, 0xAF, 0x12, 0x83, 0x1F};
            testChip.setIndex((char) 250);
            char index = testChip.getIndex();

            // Writing the values into memory
            for (int i = 0; i < values.length; i++) {
                 testMemory.writeByte(index + i, values[i]);
            }

            // Reading the values into the registers and checking if they match
            testChip.decodeInstruction(instruction);
            testChip.executeInstruction(opCode);

            for (int i = 0; i < values.length; i++) {
                assertEquals(values[i], testChip.getRegisters()[i]);
            }
        }
    }

    @Nested
    public class clearDisplayTest {
        @Test
        public void clearDisplayDecodeTest() {
            char instruction = 0x00E0;
            testChip.decodeInstruction(instruction);
            assertEquals(OpCodeTypes.OP_00E0, opCode.getOpcodeType());
        }
    }

    @Nested
    public class returnFromSubroutineTest {
        char instruction = 0x00EE;
        @Test
        public void returnFromSubroutineDecodeTest() {
            testChip.decodeInstruction(instruction);

            assertEquals(OpCodeTypes.OP_00EE, opCode.getOpcodeType());
        }

        @Test
        public void returnFromSubroutineExecuteTest() {
            // Will first call 2 callAddress instructions to move the pc twice
            char address_1 = 0x2D2A;
            char address_2 = 0x2A03;
            testChip.decodeInstruction(address_1);
            testChip.executeInstruction(opCode);

            testChip.decodeInstruction(address_2);
            testChip.executeInstruction(opCode);

            // Now we call the 00EE instruction to return the pc to address_1
            testChip.decodeInstruction(instruction);
            testChip.executeInstruction(opCode);

            assertEquals(0x0D2A, testChip.getProgramCounter());
        }
    }
}
