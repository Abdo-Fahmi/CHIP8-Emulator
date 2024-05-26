import org.chip8_sim.CPU.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class StackTest {
    private Stack testStack;

    @BeforeEach
    public void beforeEach() {
        testStack = TestData.testStack();
    }

    @AfterEach
    public void tearDown() {
        testStack = null;
    }

    @Test
    public void firstSetAddress() {
       testStack.setAddress((char) 0x0CD8);

       assertEquals(0, testStack.getSp());
    }

    @Nested
    public class AddressAccessTest {
        @Test
        public void multipleSetAddressTest() {
            testStack.setAddress((char) 0x0CD8);
            testStack.setAddress((char) 0x0B0F);
            testStack.setAddress((char) 0x0ABC);
            testStack.setAddress((char) 0x0F0F);

            assertEquals(3, testStack.getSp());
        }

        @Test
        public void multipleGetAddressTest() {
            testStack.setAddress((char) 0x0F0F);
            testStack.setAddress((char) 0x0ABC);
            testStack.setAddress((char) 0x0B0F);
            testStack.setAddress((char) 0x0CD8);

            assertEquals((char) 0x0CD8, testStack.getAddress());
            assertEquals((char) 0x0B0F, testStack.getAddress());
            assertEquals((char) 0x0ABC, testStack.getAddress());
            assertEquals((char) 0x0F0F, testStack.getAddress());
        }

        @Test
        public void emptyStackAccessTest() {
            assertThrows(ArrayIndexOutOfBoundsException.class, () -> testStack.getAddress());
        }
    }
}
