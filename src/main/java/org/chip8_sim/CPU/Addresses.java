package org.chip8_sim.CPU;

public enum Addresses {
    START_ADDRESS((char) 0x200),
    FONT_START((char) 0x50),
    VF_REGISTER((char) 0x0F);

    public final char address;
    Addresses(char address) {
        this.address = address;
    }
}
