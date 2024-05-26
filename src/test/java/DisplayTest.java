import org.chip8_sim.CPU.Chip;
import org.chip8_sim.Display.Coordinate;
import org.chip8_sim.Display.Screen;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DisplayTest {

    private Chip testChip;
    private Screen testDisplay;

    @BeforeEach
    public void setUp() {
        testChip = TestData.testChip();
        testDisplay = testChip.getScreen();
    }

    @AfterEach
    public void tearDown() {
        testChip = null;
        testDisplay = null;
    }

    @Nested
    public class indexToPositionTest {
        @Test
        public void indexToPosTest() {
            char index = 67;

            Coordinate expected = new Coordinate((char) 3, (char) 1);
            Coordinate result = testDisplay.indexToPos(index);

            assertEquals(expected, result);
        }

        @Test
        public void indexToPosMinTest() {
            char index = 0;

            Coordinate expected = new Coordinate((char) 0, (char) 0);
            Coordinate result = testDisplay.indexToPos(index);

            assertEquals(expected, result);
        }

        @Test
        public void indexToPosMaxTest() {
            int maxHeight = testDisplay.getVideoHeight();
            int maxWidth = testDisplay.getVideoWidth();

            char index = (char) ((maxWidth * maxHeight) - 1);

            Coordinate expected = new Coordinate((char) (maxHeight - 1), (char) (maxWidth - 1));
            Coordinate result = testDisplay.indexToPos(index);

            assertEquals(expected, result);
        }

        @Test
        public void indexToPosOutOfBoundsTest() {
            char index = 32000;
            assertThrows(IndexOutOfBoundsException.class, () -> testDisplay.indexToPos(index));
        }
    }

    @Nested
    public class PositionToIndexTests {

        @Test
        public void positionToIndexTest() {
            Coordinate pos = new Coordinate((char) 5, (char) 3);
            int expectedIndex = 197;
            assertEquals(expectedIndex,testDisplay.posToIndex(pos));
        }

        @Test
        public void coordinateToIndexMinTest() {
            Coordinate pos = new Coordinate((char) 0, (char) 0);
            assertEquals(0,testDisplay.posToIndex(pos));
        }

        @Test
        public void coordinateToIndexMaxTest() {
            int maxHeight = testDisplay.getVideoHeight();
            int maxWidth = testDisplay.getVideoWidth();

            Coordinate pos = new Coordinate((char) (maxHeight - 1), (char) (maxWidth - 1));
            char expected = (char) ((maxWidth * maxHeight) - 1);

            assertEquals(expected, testDisplay.posToIndex(pos));
        }

        @Test
        public void coordinateToIndexOOBTest() {
            Coordinate pos = new Coordinate((char) -1, (char) -2);
            assertThrows(IndexOutOfBoundsException.class, () -> testDisplay.posToIndex(pos));
        }

        @Test
        public void coordinateToIndexWrapAroundTest() {
            Coordinate pos = new Coordinate((char) 65, (char) 0);
            int expectedIndex = 1;
            assertEquals(expectedIndex, testDisplay.posToIndex(pos));
        }
    }

    @Nested
    public class DrawTest {
        @Test
        public void drawSpriteTest() {
            char[] sprite = {0xf0, 0x90, 0x90, 0x90, 0xf0};
            Coordinate startPosition = new Coordinate((char) 0, (char) 0);

            boolean collision = testDisplay.drawSprite(startPosition, sprite);
            assertFalse(collision);

            short[] expected = {1, 1, 1, 1, 0, 0, 0, 0,     0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
                    1, 0, 0, 1, 0, 0, 0, 0,     0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
                    1, 0, 0, 1, 0, 0, 0, 0,     0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
                    1, 0, 0, 1, 0, 0, 0, 0,     0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
                    1, 1, 1, 1, 0, 0, 0, 0,     0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
            };

            short[] display = new short[expected.length];
            for (short i = 0; i < display.length; i++) {
                display[i] = testDisplay.isBitOn(i);
            }

            assertArrayEquals(expected, display);
        }

        @Test
        public void drawSpriteWrapTest() {
            char[] sprite = {0xf0, 0x90, 0x90, 0x90, 0xf0};
            Coordinate startPosition = new Coordinate((char) 61, (char) 1);

            boolean collision = testDisplay.drawSprite(startPosition, sprite);
            assertFalse(collision);

            short[] expected = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0, 0,0,0,
                    1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0, 1,1,1,
                    1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0, 1,0,0,
                    1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0, 1,0,0,
                    1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0, 1,0,0,
                    1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0, 1,1,1
            };

            short[] display = new short[expected.length];
            for (short i = 0; i < display.length; i++) {
                display[i] = testDisplay.isBitOn(i);
            }

            assertArrayEquals(expected, display);
        }

        @Test
        public void clearDisplayTest() {
            char[] sprite = {0xf0, 0x90, 0x90, 0x90, 0xf0};
            Coordinate startPosition = new Coordinate((char) 11, (char) 1);

            testDisplay.drawSprite(startPosition, sprite);
            testDisplay.clearDisplay();

            for (short i = 0; i < 2048; i++) {
                assertEquals(0, testDisplay.isBitOn(i));
            }
        }
    }
}
