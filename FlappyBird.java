import javax.swing.JFrame;

public class FlappyBird extends JFrame {

    public FlappyBird() {
        GamePanel gamePanel = new GamePanel();
        add(gamePanel);

        setTitle("Flappy Bird");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        new FlappyBird();
    }
}