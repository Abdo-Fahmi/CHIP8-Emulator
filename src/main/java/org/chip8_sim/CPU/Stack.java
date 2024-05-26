package org.chip8_sim.CPU;

public class Stack {
    private byte sp = -1;
    private final char[] stack = new char[16];

    public byte getSp() {
        return sp;
    }

    public void setSp(byte sp) {
        this.sp = sp;
    }

    public void setAddress(char address) {
       stack[++sp] = address;
    }

    public char getAddress() {
        return stack[sp--];
    }
}
