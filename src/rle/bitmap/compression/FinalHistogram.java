/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.D:\\Photos\\Omrah Photos\\20130803_061322.jpg
 */
package rle.bitmap.compression;

/**
 *
 * @author Mohamed Jehad Baeth <jihadbaeth@gmail.com>
 */
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class FinalHistogram extends JPanel {

    int[] bins = new int[256];

    FinalHistogram(int[] pbins) {
        bins = pbins;
        repaint();
}

@Override
protected void paintComponent(Graphics g) {

        for (int i = 0; i < 256; i++) {

            System.out.println("bin[" + i + "]===" + bins[i]);
            g.drawLine(200 + i, 300, 200 + i, 300 - (bins[i]) / 70);
        }

}

public static void main(String[] args) throws IOException {
        JFrame frame = new JFrame();
        frame.setSize(500, 500);
        int[] pbins = new int[256];
        int[] sbins = new int[256];

        PlanarImage image = JAI.create("fileload", "D:\\Photos\\\\Omrah Photos\\20130803_061322.jpg");

        BufferedImage bi = image.getAsBufferedImage();
        System.out.println("tipe is          " + bi.getType());
        int[] pixel = new int[3];

        int k = 0;
        Color c = new Color(k);
        Double d = 0.0;
        Double d1;
        for (int x = 0; x < bi.getWidth(); x++) {
            for (int y = 0; y < bi.getHeight(); y++) {
                pixel = bi.getRaster().getPixel(x, y, new int[3]);
                k = (int) ((pixel[0]) + ( pixel[1]) + (pixel[2]));
                k=k/3;
                sbins[k]++;

            }

        }
        System.out.println("copleted" + d + "--" + k);
        frame.add(new FinalHistogram(sbins));
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
}