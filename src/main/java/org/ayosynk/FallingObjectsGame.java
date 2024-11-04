package org.ayosynk;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

public class FallingObjectsGame extends JPanel implements ActionListener {
    private Timer timer;
    private Player player;
    private ArrayList<FallingObject> fallingObjects;
    private int score;
    private int level;
    private int fallingSpeed = 5;

    public FallingObjectsGame() {
        initializeGame();
        setFocusable(true);
        setPreferredSize(new Dimension(400, 600));
        setBackground(Color.BLACK);

        timer = new Timer(20, this); // Update every 20ms
        timer.start();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                player.keyPressed(e);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                player.keyReleased(e);
            }
        });
    }

    private void initializeGame() {
        player = new Player();
        fallingObjects = new ArrayList<>();
        score = 0;
        level = 1;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw player
        g.setColor(Color.GREEN);
        g.fillRect(player.x, player.y, player.width, player.height);

        // Draw falling objects
        for (FallingObject obj : fallingObjects) {
            obj.draw(g); // Use the draw method for animations
        }

        // Draw score and level
        g.setColor(Color.WHITE);
        g.drawString("Score: " + score, 10, 10);
        g.drawString("Level: " + level, 10, 30);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        player.move();

        // Update falling objects
        for (int i = 0; i < fallingObjects.size(); i++) {
            FallingObject obj = fallingObjects.get(i);
            obj.move();

            // Check for collision
            if (player.getBounds().intersects(obj.getBounds())) {
                timer.stop();
                gameOver();
                return; // Stop processing further
            }

            // Remove object if it goes off screen
            if (obj.y > getHeight()) {
                fallingObjects.remove(i);
                score++;
                if (score % 5 == 0) { // Increase level every 5 points
                    level++;
                    fallingSpeed += 2; // Increase falling speed
                }
            }
        }

        // Add new falling object
        if (Math.random() < 0.05) {
            fallingObjects.add(new FallingObject(getWidth(), fallingSpeed));
        }

        repaint();
    }

    private void gameOver() {
        int response = JOptionPane.showOptionDialog(this,
                "Game Over! Score: " + score,
                "Game Over",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new Object[]{"Restart", "Quit"},
                JOptionPane.YES_OPTION);

        if (response == JOptionPane.YES_OPTION) {
            restartGame();
        } else {
            System.exit(0);
        }
    }

    private void restartGame() {
        timer.stop();
        initializeGame();
        fallingObjects.clear(); // Clear falling objects
        timer.start(); // Restart the timer
        repaint(); // Refresh the panel
    }

    private class Player {
        int x = 200, y = 500, width = 30, height = 30;
        int dx = 0;

        public void move() {
            x += dx;
            if (x < 0) x = 0;
            if (x > getWidth() - width) x = getWidth() - width;
        }

        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_LEFT) dx = -5;
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) dx = 5;
        }

        public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT) dx = 0;
        }

        public Rectangle getBounds() {
            return new Rectangle(x, y, width, height);
        }
    }

    private class FallingObject {
        int x, y, width, height;
        int dy;
        Color color;

        public FallingObject(int screenWidth, int fallingSpeed) {
            Random rand = new Random();
            x = rand.nextInt(screenWidth - 20);
            y = 0;
            width = 20 + rand.nextInt(30); // Random width between 20 and 50
            height = 20 + rand.nextInt(30); // Random height between 20 and 50
            dy = fallingSpeed; // Set speed based on the current falling speed
            color = new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256)); // Random color
        }

        public void move() {
            y += dy; // Move down by the speed
        }

        public void draw(Graphics g) {
            g.setColor(color);
            g.fillRect(x, y, width, height);
        }

        public Rectangle getBounds() {
            return new Rectangle(x, y, width, height);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Avoid the Falling Objects Game");
        FallingObjectsGame game = new FallingObjectsGame();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
