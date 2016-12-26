//package com.zetcode;

import java.awt.EventQueue;
import javax.swing.JFrame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.event.*;
import java.awt.Container;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.GroupLayout;
import javax.swing.JComponent;

class Gravity_Result {
    double fx;
    double fy;
    double force;
    double dist_sqr;
}

class Star {
    double pos_x, pos_y, theta;
    public static double gmass = 500;

    public Star() {
        //pos_x = (Math.random() * Board.B_WIDTH/2)+Board.B_WIDTH/4;
        //pos_y = (Math.random() * Board.B_HEIGHT/2)+Board.B_HEIGHT/4;
        pos_x = (Math.random() * Board.B_WIDTH);
        pos_y = (Math.random() * Board.B_HEIGHT);
    }

    public void f_gravity(double ast_x, double ast_y, Gravity_Result gr) {
        double dist_x = pos_x-ast_x;
        double dist_y = pos_y-ast_y;
        gr.dist_sqr = ((dist_x*dist_x)+(dist_y*dist_y));
        if (gr.dist_sqr <2) {
            gr.force = gmass;
        }
        else {
            gr.force = gmass/(gr.dist_sqr);
        }
        // Compute the direction of the force.
        theta = Math.atan2(dist_y, dist_x);
        gr.fx = Math.cos(theta) * gr.force;
        gr.fy = Math.sin(theta) * gr.force;
    }

    public void draw_star(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.YELLOW);
        g2d.fillOval((int)pos_x, (int)pos_y, 20, 20);
    }
    public static void setGravity(int new_gravity) {
        gmass=new_gravity;
    }

}

class Astroid {
    // class to create an astroid

    static double v_max = 5.0;
    double vel_x, vel_y;
    double pos_x, pos_y;
    int color;
    int size;

    public Astroid() {
        vel_x = ((Math.random() * v_max)-v_max/2);
        vel_y = ((Math.random() * v_max)-v_max/2);
        pos_x = (Math.random() * Board.B_WIDTH);
        pos_y = (Math.random() * Board.B_HEIGHT);
        color = (int)(Math.random() * 0xffffff);
        size = (int)(Math.random() * 10);
    }

    public boolean update_astroid() {
        boolean astroid_crash=false;
        int time_r = 10;

        while (time_r > 0) {
            Gravity_Result gr = new Gravity_Result();
            double max_force=0, min_dist=100, f_g_tot_x=0, f_g_tot_y=0;

            for (int i =0; i<Board.num_stars; i++) {
                Board.myStar[i].f_gravity(pos_x, pos_y, gr);
                f_g_tot_x += gr.fx;
                f_g_tot_y += gr.fy;
                max_force = Math.max(max_force, gr.force);
                min_dist = Math.min(min_dist, gr.dist_sqr);
            }

            if (min_dist < 8) {
                astroid_crash = true;
                time_r -= 10;
                //print "Astroid Crash!!!"
            }

            if (max_force < Star.gmass/500) {  //normal gravity
                vel_x += f_g_tot_x;
                vel_y += f_g_tot_y;
                //move based on position and vel'
                pos_x += vel_x;
                pos_y += vel_y;
                time_r -= 10;
            }
            else { // high gravity
                vel_x += f_g_tot_x/10;
                vel_y += f_g_tot_y/10;
                //move based on position and vel'
                pos_x += vel_x/10;
                pos_y += vel_y/10;
                time_r -= 2;
            }
            if (vel_x > 15 || vel_y > 15) {
                //print "=== dist2: {}".format(min_dist)
                //print "fg: {} fgx: {} fgy: {} max_force: {}".format(f_g,f_g_tot_x,f_g_tot_y,max_force)
                //print "velx: {} vely {}".format(self.vel_x, self.vel_y)
            }
        }
        return astroid_crash;
    }


    public void draw_astroid(Graphics g) {
        //canvas.draw_circle((self.pos_x, self.pos_y), self.size, 12, self.color,self.color)
        //canvas.create_oval(x,y,x+self.size,y+self.size,fill=self.color,outline=self.color)
        //canvas.create_oval(self.pos_x,self.pos_y,self.pos_x+10,self.pos_y+10,fill=self.color,outline=self.color)
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(new Color(color));
        //g2d.fillOval(100, 100, 10, 10);
        g2d.fillOval((int)pos_x, (int)pos_y, size, size);
    }
}

class Board extends JPanel
        implements ActionListener {

    public static int B_WIDTH = 500;
    public static int B_HEIGHT = 500;
    private final int INITIAL_X = -40;
    private final int INITIAL_Y = -40;
    private final int DELAY = 25;
    private static int num_astroids = 500;
    public static int num_stars = 1;
    private static int max_stars = 10;


    private Image star;
    private Timer timer;
    private int x, y;

    private Astroid[] myAstroid = new Astroid[num_astroids];
    public static Star[] myStar = new Star[max_stars];

    public Board() {

        initBoard();
    }

    private void loadImage() {

        ImageIcon ii = new ImageIcon("star.png");
        star = ii.getImage();

        for (int i =0; i<num_astroids; i++) {
            myAstroid[i] = new Astroid();
        }
        for (int i =0; i<num_stars; i++) {
            myStar[i] = new Star();
        }
    }

    private void initBoard() {

        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
        setDoubleBuffered(true);

        loadImage();

        x = INITIAL_X;
        y = INITIAL_Y;

        timer = new Timer(DELAY, this);
        timer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        //drawStar(g);
        for (int i =0; i<num_astroids; i++) {
            myAstroid[i].draw_astroid(g);
        }
        for (int i =0; i<num_stars; i++) {
            myStar[i].draw_star(g);
        }
    }

    private void drawStar(Graphics g) {

        g.drawImage(star, x, y, this);
        Toolkit.getDefaultToolkit().sync();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        for (int i =0; i<num_astroids; i++) {
            if (myAstroid[i].update_astroid() == true) {
                myAstroid[i] = null;
                myAstroid[i] = new Astroid();
            };
        }
        repaint();
    }

    public void setNumSuns(int new_stars) {

        if (new_stars > num_stars) {
            for (int i =num_stars; i<new_stars; i++) {
                myStar[i] = new Star();
            }
        }
        if (new_stars < num_stars) {
            for (int i =new_stars; i<num_stars; i++) {
                myStar[i] = null;
            }
        }
        num_stars=new_stars;
    }
}


public class Astroids extends JFrame
                        implements ChangeListener {

    private JSlider slider,g_slider;
    private JLabel lbl, g_lbl;
    private Board myBoard;

    public Astroids() {

        initUI();
    }

    private void initUI() {

        // add(new Board());
        myBoard = new Board();

        /* slider for number stars */
        slider = new JSlider(0, 10, 0);
        slider.setMinorTickSpacing(1);
        slider.setMajorTickSpacing(5);
        slider.setPaintTicks(true);
        slider.addChangeListener(this);
        lbl = new JLabel("...");

        /* slider for gravity */
        g_slider = new JSlider(0, 2000, 0);
        g_slider.setMinorTickSpacing(100);
        g_slider.setMajorTickSpacing(500);
        g_slider.setPaintTicks(true);
        g_slider.addChangeListener(this);
        g_lbl = new JLabel("...");

        createLayout(myBoard, slider, lbl, g_slider, g_lbl);
        setResizable(false);
        pack();

        setTitle("Star");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /** Listen to the slider. */
    public void stateChanged(ChangeEvent e) {
        /* update stars slider */
        int value = slider.getValue();
        lbl.setText(Integer.toString(value));
        myBoard.setNumSuns(value);
        /* update gravity slider */
        int g_value = g_slider.getValue();
        g_lbl.setText(Integer.toString(g_value));
        Star.setGravity(g_value);
        }

    private void createLayout(JComponent... arg) {

        Container pane = getContentPane();
        GroupLayout gl = new GroupLayout(pane);
        pane.setLayout(gl);

        gl.setAutoCreateContainerGaps(true);
        gl.setAutoCreateGaps(true);

        gl.setHorizontalGroup(gl.createParallelGroup()
                .addComponent(arg[0])
                .addComponent(arg[1])
                .addComponent(arg[2])
                .addComponent(arg[3])
                .addComponent(arg[4])
        );

        gl.setVerticalGroup(gl.createSequentialGroup()
                .addComponent(arg[0])
                .addComponent(arg[1])
                .addComponent(arg[2])
                .addComponent(arg[3])
                .addComponent(arg[4])
        );

        pack();
    }

    public static void main(String[] args) {

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame ex = new Astroids();
                ex.setVisible(true);
            }
        });
    }
}