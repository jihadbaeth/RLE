/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rle.bitmap.compression;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.Window;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYBarDataset;
import javax.imageio.ImageIO;

/**
 *
 * @author Mohamed Jehad Baeth <jihadbaeth@gmail.com>
 */
public class RLEBitmapCompression {

    /**
     * @param args the command line arguments
     * 
     */
    
    //@throws java.io.IOException
    public static void main(String[] args) throws IOException  {
        RLEBitmapCompression rle = new RLEBitmapCompression();

        rle.oneBitsBMPs();
        rle.fourBitsBMPs();
        rle.eightBitsBMPs();
   }

    public void fourBitsBMPs() throws IOException {
        BufferedImage in = ImageIO.read(new File("D:\\baboon_4bit.bmp"));
        int w = in.getWidth(), h = in.getHeight();

        int[][] array = new int[w][h];
        for (int j = 0; j < w; j++) {
            for (int k = 0; k < h; k++) {
                array[j][k] = in.getRGB(j, k);
                //System.out.println("RGB Array" + array[j][j]);

            }
        }
        byte[] v = new byte[1 << 4];
        for (int i = 0; i < v.length; ++i) {
            v[i] = (byte) (i * 4);
            //System.out.println(v[i]);
        }

        ColorModel cm = new IndexColorModel(4, v.length, v, v, v);
        WritableRaster wr = cm.createCompatibleWritableRaster(w, h);
        BufferedImage out = new BufferedImage(cm, wr, false, null);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int Pixel = array[x][y] << 16 | array[x][y] << 8 | array[x][y];
                out.setRGB(x, y, array[x][y]);
            }

        }
        Graphics2D g = out.createGraphics();
        g.drawImage(out, 0, 0, null);
        g.dispose();
        ImageIO.write(in, "bmp", new File("D:\\saved4.bmp"));
    }

    public void oneBitsBMPs() throws IOException {
        BufferedImage in = ImageIO.read(new File("D:\\baboon_BW.bmp"));
        int w = in.getWidth(), h = in.getHeight();

        int[][] array = new int[w][h];
        for (int j = 0; j < w; j++) {
            for (int k = 0; k < h; k++) {
                array[j][k] = in.getRGB(j, k);
                //System.out.println("RGB Array" + array[j][j]);

            }
        }
        byte[] v = new byte[1 << 8];
 
        for (int i = 0; i < v.length; ++i) {
            v[i] = (byte) (i*17);
            //System.out.println(v[i]);
        }
        Color[] colors = {Color.red, Color.green, Color.yellow,  
                Color.black};  
        byte[] reds = new byte[4];  
        byte[] greens = new byte[4];  
        byte[] blues = new byte[4];  
        for (int i = 0; i < colors.length; i++) {  
             reds[i] = (byte) colors[i].getRed();  
             greens[i] = (byte) colors[i].getGreen();  
             blues[i] = (byte) colors[i].getBlue();  
        } 
        ColorModel cm = new IndexColorModel(1, 2, reds, reds, reds);
        WritableRaster wr = cm.createCompatibleWritableRaster(w, h);
        BufferedImage out = new BufferedImage(cm, wr, false, null);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int Pixel = array[x][y] << 16 | array[x][y] << 8 | array[x][y];
                out.setRGB(x, y, array[x][y]);
            }

        }
        Graphics2D g = out.createGraphics();
        g.drawImage(out, 0, 0, null);
        g.dispose();
        ImageIO.write(out, "bmp", new File("D:\\saved1.bmp"));
    }

    public void eightBitsBMPs() throws IOException {
        //Image image = ImageIO.read(new File("D:\\baboon_8bit.bmp"));
        BufferedImage img = ImageIO.read(new File("D:\\baboon_8bit.bmp"));
        //Graphics g = img.createGraphics();
        //g.drawImage(image, 0, 0, null);
        //g.dispose();
        int w = img.getWidth();
        int h = img.getHeight();
        int[][] array = new int[w][h];
 
        for (int j = 0; j < w; j++) {
            for (int k = 0; k < h; k++) {
                array[j][k] = img.getRGB(j, k);
                //System.out.println("RGB Array"+array[j][j]);


            }
        }

        try {
             BufferedImage bufferImage2 = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
             for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    int Pixel = array[x][y] << 16 | array[x][y] << 8 | array[x][y];
                    bufferImage2.setRGB(x, y, array[x][y]);

                }
             }
             Graphics g = bufferImage2.getGraphics();
             g.drawImage(bufferImage2, h, h, null);
             g.dispose();
             ImageIO.write(bufferImage2, "bmp", new File("D:\\saved8.bmp"));
             } catch (Exception ee) {
             ee.printStackTrace();
             }
          
    }
    public static ArrayList<int[]> imageHistogram(BufferedImage input) {
 
    int[] rhistogram = new int[256];
    int[] ghistogram = new int[256];
    int[] bhistogram = new int[256];
 
    for(int i=0; i<rhistogram.length; i++) rhistogram[i] = 0;
    for(int i=0; i<ghistogram.length; i++) ghistogram[i] = 0;
    for(int i=0; i<bhistogram.length; i++) bhistogram[i] = 0;
 
    for(int i=0; i<input.getWidth(); i++) {
        for(int j=0; j<input.getHeight(); j++) {
 
            int red = new Color(input.getRGB (i, j)).getRed();
            int green = new Color(input.getRGB (i, j)).getGreen();
            int blue = new Color(input.getRGB (i, j)).getBlue();
 
            // Increase the values of colors
            rhistogram[red]++; ghistogram[green]++; bhistogram[blue]++;
 
        }
    }
 
    ArrayList<int[]> hist = new ArrayList<int[]>();
    hist.add(rhistogram);
    hist.add(ghistogram);
    hist.add(bhistogram);
 
    return hist;
 
}
    public void drawHistogram(double[][] data)
    {

      double[][] valuepairs = data;
 
      DefaultXYDataset set = new DefaultXYDataset();
      set.addSeries("Values",valuepairs);      
      XYBarDataset barset = new XYBarDataset(set,0.8);
      JFreeChart chart = ChartFactory.createXYBarChart(
         "Bars from arrays","x",false,"y",
         barset,PlotOrientation.VERTICAL,true, true, false);
      JFrame frame = new JFrame("Test");
      frame.setContentPane(new ChartPanel(chart));
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.pack();
      frame.setVisible(true);
    }
    

    

}
