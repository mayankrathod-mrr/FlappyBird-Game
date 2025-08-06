// File: GamePanel.java (Updated for Phase 4 - Final Version)

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.*;

public class GamePanel extends JPanel implements ActionListener {

    // --- Constants ---
    public static final int BOARD_WIDTH = 360;
    public static final int BOARD_HEIGHT = 640;

    // --- Images ---
    private Image backgroundImg;
    private Image birdImg;
    private Image topPipeImg;
    private Image bottomPipeImg;

    // --- Bird ---
    private int birdX = BOARD_WIDTH / 460;
    private int birdY = BOARD_HEIGHT / 640;
    private int birdWidth = 34;
    private int birdHeight = 24;
    private Rectangle birdRect;

    // --- Bird Physics ---
    private double velocityY = 0;
    private double gravity = 0.4;

    // --- Pipes ---
    private ArrayList<Pipe> pipes;
    private Random random = new Random();
    private int pipeX = BOARD_WIDTH;
    private int pipeY = 0;
    private int pipeWidth = 64;  // Adjusted to match common sprite widths
    private int pipeHeight = 512;
    private int pipeGap = 140;

    // --- Game Logic ---
    private Timer gameLoop;
    private Timer placePipesTimer;
    private int scrollSpeed = 3;
    private boolean gameOver = false;
    private double score = 0;

    public GamePanel() {
        setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    if (!gameOver) {
                        velocityY = -7;
                    }
                }
                // Restart the game if 'R' is pressed after game over
                if (e.getKeyCode() == KeyEvent.VK_R && gameOver) {
                    restartGame();
                }
            }
        });

        // Load images
        try {
            backgroundImg = ImageIO.read(new File("images/background.jpg"));
            birdImg = ImageIO.read(new File("images/bird2.png"));
            topPipeImg = ImageIO.read(new File("images/pipeimg.png"));
            bottomPipeImg = ImageIO.read(new File("images/pipeb.png"));
        } catch (IOException e) {
            System.out.println("Error loading images: " + e.getMessage());
            e.printStackTrace();
        }

        // Initialize game state
        pipes = new ArrayList<>();
        birdRect = new Rectangle(birdX, birdY, birdWidth, birdHeight);

        // Timers
        placePipesTimer = new Timer(2000, e -> placePipes());
        placePipesTimer.start();
        gameLoop = new Timer(16, this);
        gameLoop.start();
    }

    private void restartGame() {
        // Reset all game variables to their initial states
        birdY = BOARD_HEIGHT / 2;
        velocityY = 0;
        pipes.clear();
        score = 0;
        gameOver = false;
        
        // Restart the timers
        gameLoop.start();
        placePipesTimer.start();
    }

    public void placePipes() {
        if (gameOver) return;
        int randomPipeY = (int) (pipeY - pipeHeight / 4 - random.nextInt(pipeHeight / 2));
        
        // Add a top and bottom pipe pair
        Pipe topPipe = new Pipe(pipeX, randomPipeY, pipeWidth, pipeHeight, topPipeImg);
        pipes.add(topPipe);
        
        // The bottom pipe's Y is calculated from the top pipe's position and the gap
        int bottomPipeY = randomPipeY + pipeHeight + pipeGap;
        Pipe bottomPipe = new Pipe(pipeX, bottomPipeY, pipeWidth, pipeHeight, bottomPipeImg);
        pipes.add(bottomPipe);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            placePipesTimer.stop();
            gameLoop.stop();
        }
    }

    private void move() {
        if (gameOver) return;

        velocityY += gravity;
        birdY += velocityY;
        birdY = Math.max(birdY, 0);
        birdRect.y = birdY;

        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.x -= scrollSpeed;

            if (!pipe.passed && birdX > pipe.x + pipe.width) {
                pipe.passed = true;
                score += 0.5; // 0.5 for each pipe = 1 point per pair
            }
            
            if (checkCollision(pipe)) {
                gameOver = true;
            }
        }
        
        if (birdY >= BOARD_HEIGHT - birdHeight) {
            gameOver = true;
        }
    }

    private boolean checkCollision(Pipe pipe) {
        // We only need to check collision for the bird's rectangle
        Rectangle pipeRect = new Rectangle(pipe.x, pipe.y, pipe.width, pipe.height);
        return birdRect.intersects(pipeRect);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    private void draw(Graphics g) {
        // Background
        g.drawImage(backgroundImg, 0, 0, BOARD_WIDTH, BOARD_HEIGHT, null);

        // Pipes
        for (Pipe pipe : pipes) {
            g.drawImage(pipe.image, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        // Bird (with rotation)
        Graphics2D g2d = (Graphics2D) g;
        double rotation = 0.0;
        // Rotate based on velocity, but cap the rotation angle
        if (velocityY < 0) { // Going up
            rotation = Math.max(-Math.PI / 4, velocityY * 0.1); // Max -45 degrees
        } else { // Falling
            rotation = Math.min(Math.PI / 2, velocityY * 0.05); // Max 90 degrees
        }
        g2d.rotate(rotation, birdX + birdWidth / 2, birdY + birdHeight / 2);
        g.drawImage(birdImg, birdX, birdY, birdWidth, birdHeight, null);
        // It's important to "un-rotate" the graphics context afterwards
        g2d.rotate(-rotation, birdX + birdWidth / 2, birdY + birdHeight / 2);

        // Score and Game Over message
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 32));
        if (gameOver) {
            g.drawString("Game Over", 100, 300);
            g.drawString("Score: " + (int) score, 115, 340);
            g.setFont(new Font("Arial", Font.PLAIN, 16));
            g.drawString("Press 'R' to Restart", 110, 370);
        } else {
            g.drawString(String.valueOf((int) score), 10, 35);
        }
    }
}
