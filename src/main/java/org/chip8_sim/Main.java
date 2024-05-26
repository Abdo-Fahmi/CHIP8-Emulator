package org.chip8_sim;

import org.chip8_sim.CPU.Chip;
import org.chip8_sim.CPU.Memory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {

        Chip chip = new Chip();
        Memory memory = chip.getMemory();
        try {
            URL res = Main.class.getClassLoader().getResource("test_opcode.ch8");
            File ROM;
            if (res != null) {
                ROM = Paths.get(res.toURI()).toFile();
            } else return;
            String fileName = ROM.getAbsolutePath();
            memory.loadROM(fileName);
        } catch (IOException | URISyntaxException e) {
            System.out.println("No game was found");
            System.exit(1);
        }

        chip.run();
    }
}