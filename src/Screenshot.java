package com.company;

// Java Program to Capture full
// Image of Screen

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.plaf.DimensionUIResource;

public class Screenshot {
    public static final long serialVersionUID = 1L;
    // Used to get ScreenSize and capture image
    Rectangle capture;
    Robot r;

    Screenshot(int x, int y, int w, int h) throws Exception {
        if (w == -1 || h == -1) {
            // set screen full size
            capture = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        } else {
            capture = new Rectangle(x, y, w, h);
        }
        r = new Robot();
    }

    Screenshot(float ratio) throws Exception {

        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        double w = size.getWidth() * ratio;
        double h = size.getHeight() * ratio;
        double x = (size.getWidth() - w) / 2;
        double y = (size.getHeight() - h) / 2;

        capture = new Rectangle((int) x, (int) y, (int) w, (int) h);
        r = new Robot();
    }

    BufferedImage capture() {

        return r.createScreenCapture(capture);
    }

    public static void main(String[] args) {
        try {
            Thread.sleep(1000);

            // // It saves screenshot to desired path
            // String path =
            // "G://我的雲端硬碟//教學//上課資料//Java_Evan//Project_AimBot//code//Screenshot.jpg";

            Screenshot sc = new Screenshot(0.5f);

            BufferedImage Image = sc.capture();
            ImageIO.write(Image, "jpg", new File("test.jpg"));
            // System.out.println("Screenshot saved");
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
}