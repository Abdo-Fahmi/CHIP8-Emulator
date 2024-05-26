package org.chip8_sim.Keyboard;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Keypad implements KeyListener {
    private char pressedKey = 0xFFFF;

    public char getPressedKey() {
        return pressedKey;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        keyPressed(e);
    }

    @Override
    public void keyPressed(KeyEvent e) {
       pressedKey = convertToHex(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (convertToHex(e) == pressedKey)
            pressedKey = 0xFFFF;
    }

    private char convertToHex(KeyEvent e) {
        return switch (e.getKeyChar()) {
            case '1' -> 0x1;
            case '2' -> 0x2;
            case '3' -> 0x3;
            case '4' -> 0xC;
            case 'q' -> 0x4;
            case 'w' -> 0x5;
            case 'e' -> 0x6;
            case 'r' -> 0xD;
            case 'a' -> 0x7;
            case 's' -> 0x8;
            case 'd' -> 0x9;
            case 'f' -> 0xE;
            case 'z' -> 0xA;
            case 'x' -> 0x0;
            case 'c' -> 0xB;
            case 'v' -> 0xF;
            default -> 0xFFFF;
        };
    }
}
