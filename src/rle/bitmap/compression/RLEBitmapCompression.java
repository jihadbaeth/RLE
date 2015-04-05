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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;

import org.apache.commons.io.IOUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYBarDataset;

import javax.imageio.ImageIO;

import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.SimpleHistogramBin;
import org.jfree.data.statistics.SimpleHistogramDataset;

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
    public static void main(String[] args) throws IOException {
    	long startTime = System.currentTimeMillis();
        RLEBitmapCompression rle = new RLEBitmapCompression();

        rle.oneBitsBMPs();
        //rle.fourBitsBMPs();
        //rle.eightBitsBMPs();
        rle.decompress();
        System.out.print("this operation has taken  ");
        System.out.print(System.currentTimeMillis()-startTime);
        System.out.println(" milli second");
    }	

    public void fourBitsBMPs() throws IOException {
    	
        BufferedImage in = ImageIO.read(new File("D:\\baboon_4bit.bmp"));
        String fileName = new File("D:\\baboon_4bit.bmp").getName();
        int w = in.getWidth(), h = in.getHeight();
        int counter = 0;
        int[][] array = new int[w][h];
        double[] data = new double[w*h];
        System.out.println("Size of array is: "+array.length);
        for (int j = 0; j < w; j++) {
            for (int k = 0; k < h; k++) {
                
                array[j][k] = in.getRGB(j, k);
                
                
                data[counter]=in.getRGB(j, k);
                //System.out.print("Int Array position ("+j+ "-"+k+") is:" + array[j][k]);
                //System.out.print("    Double Array: " + data[counter]);
                //System.out.println("    RGB position("+j+ "-"+k+") is:" + in.getRGB(j, k));
                counter++;

            }
        }
        System.out.println("the Four bit");
        getRunLengthByRow(array);
        getRunLengthByColumn(array);
        getRunLengthZigzag(array);
        createCompressedFile(getRunLengthByRow(array),fileName);

        //drawHistogram(data);
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
    	
    	System.out.println("Name of file is !!"+new File("D:\\baboon_BW.bmp").getName());
    	String fileName = new File("D:\\baboon_BW.bmp").getName();
        BufferedImage in = ImageIO.read(new File("D:\\baboon_BW.bmp"));
        int w = in.getWidth(), h = in.getHeight();

        int[][] array = new int[w][h];
        int [] data = new int[w*h];
        int counter =0;
        
        for (int j = 0; j < w; j++) {
            for (int k = 0; k < h; k++) {
                array[j][k] = in.getRGB(j, k);
                data[counter] = in.getRGB(j,k);
                counter++;
                //System.out.println("RGB Array" + array[j][j]);

            }
        }
        System.out.println("the one bit");
        
        createCompressedFile(getRunLengthByRow(array),fileName);
        getRunLengthByRow(array);
        getRunLengthByColumn(array);
        getRunLengthZigzag(array);
        System.out.println("finish");
        byte[] v = new byte[1 << 8];

        for (int i = 0; i < v.length; ++i) {
            v[i] = (byte) (i * 17);
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
//        byte exp [] = new byte[262144];
        int index =0;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int Pixel = array[x][y] << 16 | array[x][y] << 8 | array[x][y];
//                String pixel =Integer.toString(Pixel);
//                if(Pixel == -16777216 )
//                {
//                	exp [index]= 1;
//                }
//                else
//                {
//                	exp [index]= 0;
//                	//System.out.println("What's going on? "+Pixel);
//                }
                  index++;
                  out.setRGB(x, y, array[x][y]);
            }
                }

        
        
//        System.out.println("*****************");
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        baos.write(getRunLengthByRow(array));
//        byte[] array1 = baos.toByteArray();
//        System.out.println("rle length: " + getRunLengthByRow(array).length);
//        System.out.println("array length: " + array.length);
//        System.out.println("*****************");       
        
//        createCompressedFile(exp,"exp");
        Graphics2D g = out.createGraphics();
        g.drawImage(out, 0, 0, null);
        g.dispose();
        ImageIO.write(out, "bmp", new File("D:\\saved1.bmp"));
    }

    public void eightBitsBMPs() throws IOException {
        //Image image = ImageIO.read(new File("D:\\baboon_8bit.bmp"));
    	String fileName = new File("D:\\baboon_8bit.bmp").getName();
        BufferedImage img = ImageIO.read(new File("D:\\baboon_8bit.bmp"));
        //Graphics g = img.createGraphics();
        //g.drawImage(image, 0, 0, null);
        //g.dispose();
        int w = img.getWidth();
        int h = img.getHeight();
        int[][] array = new int[w][h];
        int[] data = new int[w*h];
        
        int counter = 0;
        for (int j = 0; j < w; j++) {
            for (int k = 0; k < h; k++) {
                array[j][k] = img.getRGB(j, k);
                data[counter]=img.getRGB(j, k);
                //System.out.println("RGB Array"+array[j][j]);
                counter++;

            }
        }
        System.out.println("the Eight bit");
        getRunLengthByRow(array);
        getRunLengthByColumn(array);
        getRunLengthZigzag(array);
        createCompressedFile(getRunLengthByRow(array),fileName);

        //getRunLength(data);
        //drawHistogram(data);
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

        for (int i = 0; i < rhistogram.length; i++) {
            rhistogram[i] = 0;
        }
        for (int i = 0; i < ghistogram.length; i++) {
            ghistogram[i] = 0;
        }
        for (int i = 0; i < bhistogram.length; i++) {
            bhistogram[i] = 0;
        }

        for (int i = 0; i < input.getWidth(); i++) {
            for (int j = 0; j < input.getHeight(); j++) {

                int red = new Color(input.getRGB(i, j)).getRed();
                int green = new Color(input.getRGB(i, j)).getGreen();
                int blue = new Color(input.getRGB(i, j)).getBlue();

                // Increase the values of colors
                rhistogram[red]++;
                ghistogram[green]++;
                bhistogram[blue]++;

            }
        }

        ArrayList<int[]> hist = new ArrayList<int[]>();
        hist.add(rhistogram);
        hist.add(ghistogram);
        hist.add(bhistogram);

        return hist;

    }

    public void drawHistogram(double[] data) {
        //double[] dest = new double[data[0].length*data[1].length];
        //System.out.print("Source Array length" + data.length + "   while destination array length is:" + dest.length);
        SimpleHistogramDataset dataset = null;
        //int index=0;
        //for (int i = 0; i < data[0].length; i++) {
        //    for (int x = 0; x < data[1].length; x++) {
        //        dest[index] = data[x][i];
        //        index++;
                //System.out.print(index);
                //System.out.println(" and the value the holds is"+dest[index-1]);
                //dataset.addBin(new SimpleHistogramBin(dest[i][x],dest[x][i]));
        //        System.out.println("Data Array valu"+data[x][i]);
        //        System.out.println("Dest Array valu"+dest[index-1]);
        //    }
            //System.out.println("Dest Array valu"+dest[index-1]);
            
        //}
        //testing purposes
        HistogramDataset histogramdataset = new HistogramDataset();
        histogramdataset.addSeries("H1", data, 255,0,255.0);
        //end of added shit

        //double[][] valuepairs = dest;
        //DefaultXYDataset set = new DefaultXYDataset();
        //set.addSeries("Values", dest);
        //XYBarDataset barset = new XYBarDataset(set, 0.8);
        JFreeChart chart1 = ChartFactory.createHistogram(
                "Histogram", "X-Axis", "Y-Axis", histogramdataset, PlotOrientation.VERTICAL, true, true, false);
        
        //JFreeChart chart = ChartFactory.createXYBarChart(
        //        "Bars from arrays", "x", false, "y",
        //        barset, PlotOrientation.VERTICAL, true, true, false);
        JFrame frame = new JFrame("Test");
        frame.setContentPane(new ChartPanel(chart1));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
    
    public byte[] getRunLengthZigzag(int [][] imageByteArray){
    	System.out.println("Getting run lenght by Zigzag");
        ByteArrayOutputStream dest = new ByteArrayOutputStream();  
        byte lastByte = (byte) imageByteArray[0][0];
        int matchCount = 1;
        System.out.println("Length of array : "+imageByteArray[0].length * imageByteArray[1].length);
        int count =0;    	    	
    	int i=0,j=0;  
    	int r=0;
    	int c=0; 
    	int dir=0;
		int rows = imageByteArray[0].length;
		int cols = imageByteArray[1].length;
    	while(r< imageByteArray[0].length && c< imageByteArray[1].length)
    	{
    	//System.out.println( r+" -> "+c);
          byte thisByte = (byte) imageByteArray[r][c];
          if (lastByte == thisByte) {
              matchCount++;
          }
          else {
              dest.write((byte)matchCount);  
              dest.write((byte)lastByte);
              //System.out.println("Number of repetitions: "+ matchCount);
              count++;                
              matchCount=1;
              lastByte = thisByte;                
          }     
    		if (dir == 1) {
    					if (c == cols - 1) {
    						r++;
    						dir = -1;
    					} else if (r == 0) {
    						c++;
    						dir = -1;
    					} else {
    						r--;
    						c++;
    					}
    				} else {
    					if (r == rows - 1) {
    						c++;
    						dir = 1;
    					} else if (c == 0) {
    						r++;
    						dir = 1;
    					} else {
    						c--;
    						r++;
    					}
    				}
    			}	
    	    	 
//        while(i < imageByteArray.length){  
//     
//            System.out.println( i+" -> "+j);
//            byte thisByte = (byte) imageByteArray[i][j];
//            if (lastByte == thisByte) {
//                matchCount++;
//            }
//            else {
//                dest.write((byte)matchCount);  
//                dest.write((byte)lastByte);
//                //System.out.println("Number of repetitions: "+ matchCount);
//                count++;                
//                matchCount=1;
//                lastByte = thisByte;                
//            }     
//            if(i==imageByteArray.length-1){  
//                i = j+1; j = imageByteArray.length-1;  
//            }  
//            else if(j==0){  
//                j = i+1;   
//                i = 0;  
//            }  
//            else {  
//                i++;  
//                j--;  
//            }  
//        }  
        System.out.println("Number of records: "+ count );
        dest.write((byte)matchCount);  
        dest.write((byte)lastByte);
        System.out.println("**** Finished Getting run lenght by Zigzag ****");
        return dest.toByteArray();
    }
    
    
    
    public byte[] getRunLengthByRow(int [][] imageByteArray){
    	System.out.println("Getting run lenght by row");
        ByteArrayOutputStream dest = new ByteArrayOutputStream();  
        byte lastByte = (byte) imageByteArray[0][0];
        int matchCount = 1;
        System.out.println("Length of array : "+imageByteArray[0].length * imageByteArray[1].length);
        int count =0;
        //byte [][]myrle = new byte[262144][262144];
        for(int i=1; i < imageByteArray[0].length; i++){
        	for (int j = 0; j < imageByteArray[1].length; j++) {
				
			
            byte thisByte = (byte) imageByteArray[i][j];
            //System.out.print("Array value: "+imageByteArray[i][j]);
            //System.out.println("  This byte value: "+thisByte);
            if (lastByte == thisByte) {
                matchCount++;
            }
            else {
                //myrle.concat(Integer.toString(matchCount));
                //myrle.concat(Integer.toString(lastByte));
                dest.write((byte)matchCount);  
                dest.write((byte)lastByte);
                //System.out.print(lastByte);
                //System.out.println("  ---Number of repetitions: "+ matchCount);
                count++;
                
                matchCount=1;
                lastByte = thisByte;
                
            }
        	}
        	
        }
        
       
        System.out.println("Number of records: "+ count );
        dest.write((byte)matchCount);  
        dest.write((byte)lastByte);
        System.out.println("**** Finished Getting run lenght by row ****");
        return dest.toByteArray();
    }
    
    
    public byte[] getRunLengthByColumn(int [][] imageByteArray){
    	System.out.println("Getting run lenght by Column");
        ByteArrayOutputStream dest = new ByteArrayOutputStream();  
        byte lastByte = (byte) imageByteArray[0][0];
        int matchCount = 1;
        System.out.println("Length of array : "+imageByteArray[0].length * imageByteArray[1].length);
        int count =0;
        for(int i=1; i < imageByteArray[0].length; i++){
        	for (int j = 0; j < imageByteArray[1].length; j++) {
				
			
            byte thisByte = (byte) imageByteArray[j][i];
            if (lastByte == thisByte) {
                matchCount++;
            }
            else {
                dest.write((byte)matchCount);  
                dest.write((byte)lastByte);
                //System.out.println("Number of repetitions: "+ matchCount);
                count++;
                
                matchCount=1;
                lastByte = thisByte;
                
            }
        	}
        }
        System.out.println("Number of records: "+ count );
        dest.write((byte)matchCount);  
        dest.write((byte)lastByte);
        System.out.println("**** Finished Getting run lenght by Column ****");
        return dest.toByteArray();


    }

    
    
	  	public String coderRLE(String text) {
		        String res = new String();
		        char[] charArray = text.toCharArray();
		        char caractere = 0;
		        int num = 0;
		        int i = 0;
		        for (char c : charArray) {
		            if (c != caractere && i != 0) {
		                if (num >= 2) {
		                    res += num;
		                    res += caractere;
		                } else {
		                    res += caractere;
		                }
		                num = 1;
		            } else {
		                num++;
		            }
		            caractere = c;
		            i++;
		        }
		        if (num >= 2) {
		            res += num;
		            res += caractere;
		        } else {
		            res += caractere;
		        }
		        return res;
		}
		
		    
		public String decoderRLE(String text) {
		        String res = new String();
		        char[] charArray = text.toCharArray();
		        for (int i = 0;i<charArray.length-1;i++) {
		            char s = charArray[i];
		            if (!Character.isDigit(s)) {
		                res += s;
		            } else {
		                int num = Integer.parseInt(String.valueOf(s));
		                for (int j = 0; j < num - 1; j++) {
		                    res += charArray[i+1];
		                }
		            }
		        }
		        return res;
		    }
		
		public void createCompressedFile(byte [] rle, String fileName)
		{
			FileOutputStream fop = null;
			File file;
			file = new File("d:/"+fileName+".tmp");
			try {
				fop = new FileOutputStream(file);

				try {
					fop.write(rle);
					fop.flush();
					fop.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
 
		
			try
	        {
	            ObjectOutputStream myStream = new ObjectOutputStream(new FileOutputStream("d:/"+fileName+".dat"));
	            myStream.writeObject(rle);
	            myStream.close();
	        } 
	        catch (FileNotFoundException e) 
	        {
	        } 
	        catch (IOException e) 
	        {
	        }
			try {
				
				FileOutputStream output = new FileOutputStream(new File("D:\\"+ fileName+".jrle"));
				IOUtils.write(rle, output);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		public void readCompressedFile()
		{
//			try {
//				byte[] bytes = IOUtils.toByteArray();
//			} catch (FileNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
			
		}
		


	public void decompress ()
	{
        // reading of the binary file and placing it into an array 
        try
        {
            ObjectInputStream mySecondStream = new ObjectInputStream(new FileInputStream("D:\\baboon_BW.bmp.dat"));
            byte[] array = (byte[]) mySecondStream.readObject();
            for(int i=0; i<array.length; i++)
            {
                System.out.println(array[i]);
            }
        } 
        catch (FileNotFoundException e) 
        {
        } catch (IOException e) 
        {
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
 
	}
}
//public String getRunLength(int[] imageByteArray){
//  StringBuffer dest = new StringBuffer();        
//  for(int i =0; i < imageByteArray.length; i++){
//      int runlength = 1;
//      while(i+1 < imageByteArray.length && imageByteArray[i] == imageByteArray[i+1]){
//          runlength++;
//          i++;
//
//      }     
//
//      System.out.println("Number of occurance : "+runlength);
//      dest.append(runlength);  
//
//      dest.append(imageByteArray[i]);
//
//  }
//  return dest.toString();
//}

