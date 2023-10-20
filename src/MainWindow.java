package com.company;

import java.awt.Container;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.MouseInfo;
import java.awt.PointerInfo;
import java.awt.image.DataBufferByte;
import java.util.List;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;


import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.opencv.core.CvType;


import static org.bytedeco.opencv.global.opencv_imgproc.LINE_8;
import static org.bytedeco.opencv.global.opencv_imgproc.rectangle;

public class MainWindow extends JFrame {

    ImageIcon image;
    YOLONet yolo;
    public MainWindow() {

        initUI();

        yolo = new YOLONet(
                "yolov4.cfg",
                "yolov4.weights",
                "coco.names",
                608, 608);
        yolo.setup();
    }

    private void initUI() {

        image = loadImage();

        JLabel label = new JLabel(image);

        createLayout(label);

        setTitle("Image");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
    public static Mat bufferToMartix(BufferedImage image) {
        OpenCVFrameConverter.ToMat cv = new OpenCVFrameConverter.ToMat() ;
        return cv.convertToMat( new Java2DFrameConverter().convert(image)) ;
//        Mat mat = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
//        byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
//        if (mat != null) {
//            try {
//                mat.put( 0,0,data);
//            } catch (Exception e) {
//                return null;
//            }
//        }
       // return mat;
    }
    public void updateImage(Screenshot st) {
        BufferedImage bimg = st.capture();
        bimg = process(bimg);
        image.setImage(bimg);
        this.repaint();
    }

    private ImageIcon loadImage() {

        ImageIcon ii = new ImageIcon("Screenshot.jpg");

        return ii;
    }

    private void createLayout(JComponent... arg) {

        Container pane = getContentPane();
        GroupLayout gl = new GroupLayout(pane);
        pane.setLayout(gl);

        gl.setAutoCreateContainerGaps(true);

        gl.setHorizontalGroup(gl.createSequentialGroup()
                .addComponent(arg[0]));

        gl.setVerticalGroup(gl.createParallelGroup()
                .addComponent(arg[0]));

        pack();
    }

    public boolean isGrey(int r, int g, int b, int range) {

        if (Math.abs(r - g) > range) {
            return false;
        }
        if (Math.abs(g - b) > range) {
            return false;
        }
        if (Math.abs(b - r) > range) {
            return false;
        }

        return true;
    }

    public BufferedImage process(BufferedImage img) {

        Mat mImg = bufferToMartix(img) ;

        // Creating the empty destination matrix
        Mat dst_mat = new Mat();

// Converting the image from BGRA to BGR and saving it in the dst_mat matrix
        opencv_imgproc.cvtColor(mImg, dst_mat, opencv_imgproc.COLOR_BGRA2BGR);




        List<YOLONet.ObjectDetectionResult> results = yolo.predict(dst_mat);

        System.out.printf("Detected %d objects:\n", results.size());
        for(YOLONet.ObjectDetectionResult result : results) {
            System.out.printf("\t%s - %.2f%%\n", result.className, result.confidence * 100f);

            // annotate on image
//            rectangle(image,
//                    new org.bytedeco.opencv.opencv_core.Point(result.x, result.y),
//                    new Point(result.x + result.width, result.y + result.height),
//                    Scalar.MAGENTA, 2, LINE_8, 0);
        }

        // test red square
        // for (int i = 0; i < 50; i++) {
        // for (int j = 0; j < 50; j++) {
        // img.setRGB(j, i, 16711680);
        // }
        // }

        //checkAimPoint(30, img);

        // PointerInfo inf = MouseInfo.getPointerInfo();
        // Point p = inf.getLocation();
        // System.out.println(p.toString());

        return img;
    }

    void checkAimPoint(int range, BufferedImage img) {

        int centerX = img.getWidth() / 2;
        int centerY = img.getHeight() / 2;

        // tracking aim point( small red pointer)
        int count = 0;
        for (int y = centerY - range; y <= centerY + range; y++) {
            for (int x = centerX - range; x <= centerX + range; x++) {
                int color = img.getRGB(x, y);
                int[] argb = pixelToARGB(color);
                int[] hsv = rgb_to_hsv(argb[1], argb[2], argb[3]);
                if (isGrey(argb[1], argb[2], argb[3], 5) == false) {
                    // the color is close to red
                    if (hsv[0] <= 15 || hsv[0] >= 330) {
                        count++;
                    }
                }
                if (y == centerY - range || x == centerX - range) {
                    img.setRGB(x, y, 0xff0000);
                }
            }
        }
        System.out.println("Red pixels : " + count);

        // set
        if (count > 100) {
            // exp red pixel : 171
            for (int i = 0; i < 50; i++) {
                for (int j = 0; j < 50; j++) {
                    img.setRGB(i, j, 0xff0000);
                }
            }
        }

    }

    // private void marchThroughImage(BufferedImage image) {
    // int w = image.getWidth();
    // int h = image.getHeight();
    // System.out.println("width, height: " + w + ", " + h);

    // for (int i = 0; i < h; i++) {
    // for (int j = 0; j < w; j++) {
    // System.out.println("x,y: " + j + ", " + i);
    // int pixel = image.getRGB(j, i);
    // printPixelARGB(pixel);
    // System.out.println("");
    // }
    // }
    // }

    public int[] pixelToARGB(int pixel) {
        int alpha = (pixel >> 24) & 0xff;
        int red = (pixel >> 16) & 0xff;
        int green = (pixel >> 8) & 0xff;
        int blue = (pixel) & 0xff;
        // System.out.println("argb: " + alpha + ", " + red + ", " + green + ", " +
        // blue);
        return new int[] { alpha, red, green, blue };
    }

    public void printPixelARGB(int pixel) {
        int alpha = (pixel >> 24) & 0xff;
        int red = (pixel >> 16) & 0xff;
        int green = (pixel >> 8) & 0xff;
        int blue = (pixel) & 0xff;
        System.out.println("argb: " + alpha + ", " + red + ", " + green + ", " + blue);
    }

    /**
     * 
     * @param r
     * @param g
     * @param b
     * @return int[] {h,s,v}
     */
    static int[] rgb_to_hsv(double r, double g, double b) {

        // R, G, B values are divided by 255
        // to change the range from 0..255 to 0..1
        r = r / 255.0;
        g = g / 255.0;
        b = b / 255.0;

        // h, s, v = hue, saturation, value
        double cmax = Math.max(r, Math.max(g, b)); // maximum of r, g, b
        double cmin = Math.min(r, Math.min(g, b)); // minimum of r, g, b
        double diff = cmax - cmin; // diff of cmax and cmin.
        double h = -1, s = -1;

        // if cmax and cmax are equal then h = 0
        if (cmax == cmin)
            h = 0;

        // if cmax equal r then compute h
        else if (cmax == r)
            h = (60 * ((g - b) / diff) + 360) % 360;

        // if cmax equal g then compute h
        else if (cmax == g)
            h = (60 * ((b - r) / diff) + 120) % 360;

        // if cmax equal b then compute h
        else if (cmax == b)
            h = (60 * ((r - g) / diff) + 240) % 360;

        // if cmax equal zero
        if (cmax == 0)
            s = 0;
        else
            s = (diff / cmax) * 100;

        // compute v
        double v = cmax * 100;
        // System.out.println("(" + h + " " + s + " " + v + ")");
        return new int[] { (int) h, (int) s, (int) v };

    }

    public static void main(String[] args) throws Exception {

        MainWindow window = new MainWindow();
        window.setVisible(true);

        // set a specific region
        Screenshot screen = new Screenshot(0.5f);
        while (true) {
            window.updateImage(screen);
            Thread.sleep(100); // 1000/30 = 33.33 fps
        }
    }
}