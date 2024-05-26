package org.chip8_sim.Display;

import org.chip8_sim.Keyboard.Keypad;

import java.awt.*;
import javax.swing.*;

public class Screen extends JFrame{
    private final int videoHeight = 32;
    private final int videoWidth = 64;
    private final short[] screen = new short[videoHeight * videoWidth];
    private final int size;
    private final int multiplier;

    private static final Color spriteColor = new Color(255, 255, 255);
    private static final Color backgroundColor = new Color(0, 0, 0);

    public Screen(Keypad keypad) {
        // Setting the key listener for the key inputs
        this.addKeyListener(keypad);

        this.multiplier = 15;
        this.size = multiplier - multiplier/15;

        this.setName("Chip8 Emulator");
        this.setTitle("Chip8 Emulator");

        // Setting up the window for display
        this.setBackground(backgroundColor);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setBackground(backgroundColor);
        this.setVisible(true);

        int width = 64 * multiplier + this.getInsets().left + this.getInsets().right;
        int height = 32 * multiplier + this.getInsets().top + this.getInsets().bottom;
        this.setSize(width, height);
    }

    @Override
    public void paint(Graphics graphics) {
        Graphics2D graphics2D = (Graphics2D) graphics;

        for (char i = 0; i < videoWidth; i++) {
            for (char j = 0; j < videoHeight; j++) {
                Coordinate position = new Coordinate(i, j);
                if(isBitOn(position) == 1) drawAt(position, graphics2D);
                else drawBackground(position, graphics2D);
            }
        }
    }

    public boolean drawSprite(Coordinate position, char[] sprite) {
        boolean collision = false;
        for (int i = 0; i < sprite.length; i++) {
            int y = position.getY() + i;
            if (y > 31 || y < 0) break;
            collision = drawByte(new Coordinate(position.getX(), (char) y), sprite[i]);
        }
        repaint();
        return collision;
    }

    private boolean drawByte(Coordinate position, char sprite) {
        sprite = (char) (sprite & 0x00FF);

        boolean collision = false;
        for (short i = 0; i < 8; i++) {
            Coordinate tempPosition = new Coordinate(position.getX(), position.getY());
            tempPosition.setX((char) (position.getX() + i));
            short index = posToIndex(tempPosition);
            char currentBit = (char) ((sprite >> (7-i)) & 0x0001);
            short displayBit = screen[index];

            // Checking for collision
            if (displayBit == 1 && currentBit == 1) collision = true;
            screen[index] = (short) (currentBit ^ displayBit);
        }
        return collision;
    }

    public void clearDisplay() {
        for (short i = 0; i < videoHeight * videoWidth; i++) {
            screen[i] = 0;
        }
    }

    private void drawBackground(Coordinate position, Graphics2D graphics2D) {
        graphics2D.setPaint(backgroundColor);
        graphics2D.fillRect(position.getX() * multiplier + this.getInsets().left,
                            position.getY() * multiplier + this.getInsets().top,
                            size, size);
    }

    private void drawAt(Coordinate position, Graphics2D graphics2D) {
        graphics2D.setPaint(spriteColor);
        graphics2D.fillRect(position.getX() * multiplier + this.getInsets().left,
                            position.getY() * multiplier + this.getInsets().top,
                            size, size);
    }

    private short isBitOn(Coordinate position) {
        return screen[posToIndex(position)];
    }

    public short posToIndex(Coordinate position) {
        int x = position.getX() % 64;
        int y = position.getY();

        if(x < 0 || y > 31 || y < 0) throw new IndexOutOfBoundsException("Coordinates" + position + " are invalid");

        return (short) ((y * videoWidth) + x);
    }

    public Coordinate indexToPos(char index) {
        if(index < 0 || index >= (videoWidth * videoHeight))
            throw new IndexOutOfBoundsException("Index " + index + " is out of range");

        char X = (char) (index % 64);
        char Y = (char) (index / 64);

        return new Coordinate(X, Y);
    }

    // ------------------ Used for testing ------------------

    public short isBitOn(short index) {
        return screen[index];
    }

    public int getVideoHeight() {
        return videoHeight;
    }

    public int getVideoWidth() {
        return videoWidth;
    }
}
