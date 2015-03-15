/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rle.bitmap.compression;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Transparency;
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
import javax.imageio.ImageIO;

/**
 *
 * @author Mohamed Jehad Baeth <jihadbaeth@gmail.com>
 */
public class RLEBitmapCompression {

    /**
     * @param args the command line arguments
     */
    
    
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
        byte[] v = new byte[2];
        for (int i = 0; i < v.length; ++i) {
            v[i] = (byte) (i );
            System.out.println(v[i]);
        }

        ColorModel cm = new IndexColorModel(1, v.length, v, v, v);
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

    

}
