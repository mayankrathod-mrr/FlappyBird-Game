// File: Pipe.java (Updated for Phase 4)

import java.awt.Image;

public class Pipe {
    int x;
    int y;
    int width;
    int height;
    Image image; // A single image can represent the pipe
    boolean passed = false;

    public Pipe(int x, int y, int width, int height, Image image) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.image = image;
    }
}