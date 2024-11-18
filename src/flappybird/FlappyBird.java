package flappybird;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.MouseInputListener;

public class FlappyBird extends JFrame {

    Dimension dim;
    int ticks, ymotion = 0;
    boolean start = false, lost = false;
    int score = 0;
    JLabel txt = new JLabel();
    JLabel txtI = new JLabel();
    JPanel close = new JPanel();

    public FlappyBird() {
        dim = new Dimension(400, 600);
        setSize(dim);
        setUndecorated(true);
        setPreferredSize(dim);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        add(new Back(this));

        try {
            BufferedImage ico = ImageIO.read(new File("C:\\Users\\modim\\Desktop\\logo.png"));
            setIconImage(ico);
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println(ex.getMessage());
        }
    }

    public static void main(String[] args) {
        new FlappyBird().setVisible(true);
    }

    class Back extends JPanel implements ActionListener, KeyListener {

        Block block[] = new Block[5];
        Bird bird;
        static Timer timer;
        JFrame frame;
        int consta = 0;
        Random r;
        boolean turno = true;

        public Back(JFrame frame) {
            this.frame = frame;
            r = new Random();
            Dimension tam = frame.getSize();

            this.setLayout(null);
            this.setSize(tam);
            this.setPreferredSize(tam);
            this.addKeyListener(this);
            this.setFocusable(true);
            this.setFocusTraversalKeysEnabled(true);
            this.setBackground(new Color(r.nextInt(255) + 1, r.nextInt(255) + 1, r.nextInt(255) + 1));

            this.add(txt);
            this.add(txtI);
            this.add(close);

            close.setLocation(0, 0);
            close.setSize(10, 10);
            close.setBackground(Color.red);
            close.addMouseListener(new MouseInputListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    System.exit(0);
                }

                @Override
                public void mousePressed(MouseEvent e) {
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    close.setBackground(Color.white);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    close.setBackground(Color.red);
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                }

                @Override
                public void mouseMoved(MouseEvent e) {
                }
            });

            txt.setSize(tam.width, 25);
            txt.setText("Score " + score);
            txt.setBackground(null);
            txt.setForeground(Color.white);
            txt.setLocation(175, 20);
            txt.setFont(new Font("Roboto thin", 0, 15));

            txtI.setBackground(Color.red);
            txtI.setSize(new Dimension(400, 300));
            txtI.setLocation(85, 200);
            txtI.setText("Click 'Space' to start");
            txtI.setForeground(Color.white);
            txtI.setFont(new Font("Roboto thin", 0, 25));

            generateBlocks();
            bird = new Bird(frame);

            timer = new Timer(0, this);
            timer.start();
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            ticks++;
            if (ticks % 2 == 0 && ymotion < 15) {
                ymotion += 2;
            }

            bird.y += ymotion;
            if (!start) {
                bird.y += ymotion;
                if (bird.y >= this.getHeight() / 2) {
                    ymotion -= 10;
                }
            }

            Color orgcol = this.getBackground();
            int red = orgcol.getRed(), green = orgcol.getGreen(), blue = orgcol.getBlue();

            if (red < 127) {
                if (green < 127 || blue < 127) {
                    txt.setForeground(Color.white);
                    txtI.setForeground(Color.white);
                }
            } else {
                if (green > 127 || blue > 127) {
                    txt.setForeground(Color.black);
                    txtI.setForeground(Color.black);
                }
            }

            if (red < 255 && red > 0 && turno) {
                red++;
            } else {
                turno = false;
                red--;
                if (red <= 1) {
                    turno = true;
                }
            }

            this.setBackground(new Color(red, green, blue));
            repaint();
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            bird.paintBird(g);
            if (start) {
                paintBlocks(g);
            }
        }

        public void paintBlocks(Graphics g) {
            Block bloque;
            g.setColor(Color.WHITE);
            for (int i = 0; i < block.length; i++) {
                bloque = block[i];
                bloque.paintBlock(g);
                bloque.moveBlock();

                checkColitions(i);
                if (block[i].poc + 60 < 0) {
                    updateBlocks(i);
                }
                if (block[i].poc + 60 == bird.x) {
                    score++;
                    txt.setText("Score: " + score);
                }
            }
        }

        public void generateBlocks() {
            for (int i = 0; i < block.length; i++) {
                block[i] = new Block(consta + 600, 0, r.nextInt(201) + 100, frame);
                consta += 300;
            }
        }

        public void updateBlocks(int poc) {
            block[poc] = new Block(consta, 0, r.nextInt(201) + 100, frame);
        }

        public void checkColitions(int i) {
            Block bloque = block[i];
            bloque.checkBounds(bird);

            if (bird.y + bird.size > this.getSize().getHeight()) {
                gameOver();
            }
        }

        public void gameOver() {
            txtI.setVisible(true);
            txtI.setText("Score: " + score + ", Enter to restart");
            score = 0;
            txt.setText("Score: " + score);
            start = false;
            lost = true;
        }

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (KeyEvent.VK_SPACE == e.getKeyCode()) {
                if (ymotion > 0) {
                    ymotion = 0;
                }
                if (start) {
                    ymotion -= 10;
                }

                if (!start && !lost) {
                    start = true;
                    txtI.setVisible(false);
                }
            } else if (KeyEvent.VK_ENTER == e.getKeyCode()) {
                start = true;
                txtI.setVisible(false);
                lost = false;
                consta = 0;
                generateBlocks();
                this.setBackground(new Color(r.nextInt(255) + 1, r.nextInt(255) + 1, r.nextInt(255) + 1));
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }
    }

    class Block {
        private int base = 40, hight = 60, hightY, s = 20, max = 80;
        private int x[], y[];
        private int points = 8;
        private int poc, pocY, pocYs, pocs;
        private final int velocity = 4;
        private Graphics g;
        private Polygon[] figs = new Polygon[2];

        public Block(int poc, int pocY, int hight, JFrame frame) {
            this.poc = poc;
            this.pocY = pocY;
            this.hight = hight;
            pocs = poc - s;
            hightY = 400 - hight;
            pocYs = frame.getHeight() - hightY;
        }

        public void checkBounds(Bird bird) {
            if (figs[0].contains(bird.x, bird.y) || figs[0].contains(bird.x + bird.size, bird.y)) {
                loose();
            }
            if (figs[1].contains(bird.x, bird.y + bird.size) || figs[1].contains(bird.x + bird.size, bird.y)) {
                loose();
            }
        }

        public void loose() {
            txtI.setVisible(true);
            txtI.setText("Score: " + score + ", Enter to restart");
            score = 0;
            txt.setText("Score: " + score);
            start = false;
            lost = true;
        }

        public void generateBlocks(Graphics g) {
            x = new int[]{poc, poc + base, poc + base, poc + base + s, poc + base + s, poc - s, poc - s, poc, poc};
            y = new int[]{pocY, pocY, pocY + hight, pocY + hight, pocY + hight + s, pocY + hight + s, pocY + hight, pocY + hight, pocY};

            figs[0] = new Polygon(x, y, points);
            g.fillPolygon(figs[0]);

            x = new int[]{pocs, pocs + max, pocs + max, pocs + max - s, pocs + max - s, pocs + s, pocs + s, pocs, pocs};
            y = new int[]{pocYs, pocYs, pocYs + s, pocYs + s, pocYs + s + hightY, pocYs + s + hightY, pocYs + s, pocYs + s, pocYs};

            figs[1] = new Polygon(x, y, points);
            g.fillPolygon(figs[1]);
        }

        public void moveBlock() {
            poc -= velocity;
            pocs -= velocity;
        }

        public void paintBlock(Graphics g) {
            generateBlocks(g);
        }
    }

    class Bird {
        int y = 300;
        int x = 100;
        final int size = 20;
        private JFrame jf;

        public Bird(JFrame frame) {
            this.jf = frame;
        }

        public void paintBird(Graphics g) {
            g.setColor(Color.YELLOW);
            g.fillOval(x, y, size + 10, size + 10);
        }
    }
}