/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rle.bitmap.compression;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import me.lemire.integercompression.Composition;
import me.lemire.integercompression.FastPFOR;
import me.lemire.integercompression.IntWrapper;
import me.lemire.integercompression.IntegerCODEC;
import me.lemire.integercompression.VariableByte;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;

import com.rapplogic.xbee.util.IntArrayOutputStream;

/**
 *
 * @author Mohamed Jehad Baeth <jihadbaeth@gmail.com>
 */
public class RLEBitmapCompression {

	/**
	 * @param args
	 *            the command line arguments
	 *
	 */
	// @throws java.io.IOException
	public static void main(String[] args) throws IOException {
		long startTime = System.currentTimeMillis();
		RLEBitmapCompression rle = new RLEBitmapCompression();
		System.out.println("Input data:");
		File path = new File("./4bits/");
		File[] files = path.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".bmp");
			}
		});
		Arrays.sort(files);

		for (File file : files) {
			System.out.println("Executing " + file.getName() + " ...");
			file.getAbsolutePath();
			rle.fourBitsBMPsColumn(file.getAbsolutePath());
			rle.fourBitsBMPsRowByRow(file.getAbsolutePath());
			rle.fourBitsBMPsZigzag(file.getAbsolutePath());

		}
		path = new File("./8bits/");
		files = path.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".bmp");
			}
		});
		Arrays.sort(files);

		for (File file : files) {
			System.out.println("Executing " + file.getName() + " ...");
			file.getAbsolutePath();
			rle.eightBitsBMPsColumn(file.getAbsolutePath());
			rle.eightBitsBMPsZigzag(file.getAbsolutePath());
			rle.eightBitsBMPsRowByRow(file.getAbsolutePath());

		}
		path = new File("./1bit/");
		files = path.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".bmp");
			}
		});
		Arrays.sort(files);

		for (File file : files) {
			System.out.println("Executing " + file.getName() + " ...");
			file.getAbsolutePath();
			rle.oneBitsBMPsRowByRow(file.getAbsolutePath());
			rle.oneBitsBMPsColumn(file.getAbsolutePath());
			rle.oneBitsBMPsZigzag(file.getAbsolutePath());
		}

		System.out.print("this operation has taken  ");
		System.out.print(System.currentTimeMillis() - startTime);
		System.out.println(" milli second");
	}

	public void fourBitsBMPsZigzag(String path) throws IOException {

		BufferedImage in = ImageIO.read(new File(path));
		String fileName = new File(path).getName();
		int w = in.getWidth(), h = in.getHeight();
		int[][] array = new int[w][h];
		double[] data = new double[w * h];
		int counter = 0;
		for (int j = 0; j < w; j++) {
			for (int k = 0; k < h; k++) {
				array[j][k] = in.getRGB(j, k);
				data[counter] = in.getRGB(j, k);
				counter++;
			}
		}
		System.out.println("___****the Four bit By Zigzag****___");
		System.out.println("File Name: " + fileName);
		createCompressedFile(getRunLengthZigzag(array), path);
		drawHistogram(data, fileName);
		byte[] v = new byte[1 << 4];
		for (int i = 0; i < v.length; ++i) {
			v[i] = (byte) (i * 4);
		}
		ColorModel cm = new IndexColorModel(4, v.length, v, v, v);
		WritableRaster wr = cm.createCompatibleWritableRaster(w, h);
		BufferedImage out = new BufferedImage(cm, wr, false, null);
		int[][] tempo = zigzagReorder(decompress(getRunLengthZigzag(array),
				path));

		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				int Pixel = array[x][y] << 16 | array[x][y] << 8 | array[x][y];
				out.setRGB(x, y, tempo[x][y]);
			}

		}

		Graphics2D g = out.createGraphics();
		g.drawImage(out, 0, 0, null);
		g.dispose();
		ImageIO.write(in, "bmp", new File("./4bits/4bitZigzag" + fileName));
	}

	public void fourBitsBMPsColumn(String path) throws IOException {

		BufferedImage in = ImageIO.read(new File(path));
		String fileName = new File(path).getName();
		int w = in.getWidth(), h = in.getHeight();
		int[][] array = new int[w][h];
		double[] data = new double[w * h];
		int counter = 0;
		for (int j = 0; j < w; j++) {
			for (int k = 0; k < h; k++) {
				array[j][k] = in.getRGB(j, k);
				data[counter] = in.getRGB(j, k);
				counter++;
			}
		}
		System.out.println("___**** The Four bit By Column****___");
		createCompressedFile(getRunLengthByColumn(array), path);
		drawHistogram(data, fileName);
		byte[] v = new byte[1 << 4];
		for (int i = 0; i < v.length; ++i) {
			v[i] = (byte) (i * 4);
		}
		ColorModel cm = new IndexColorModel(4, v.length, v, v, v);
		WritableRaster wr = cm.createCompatibleWritableRaster(w, h);
		BufferedImage out = new BufferedImage(cm, wr, false, null);
		int[][] tempo = columnByColumnOdrder(decompress(
				getRunLengthByColumn(array), path));

		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				int Pixel = array[x][y] << 16 | array[x][y] << 8 | array[x][y];
				out.setRGB(x, y, tempo[x][y]);
			}

		}

		Graphics2D g = out.createGraphics();
		g.drawImage(out, 0, 0, null);
		g.dispose();
		ImageIO.write(in, "bmp", new File("./4bits/4bitColumn" + fileName));
	}

	public void fourBitsBMPsRowByRow(String path) throws IOException {

		BufferedImage in = ImageIO.read(new File(path));
		String fileName = new File(path).getName();
		int w = in.getWidth(), h = in.getHeight();
		int[][] array = new int[w][h];
		double[] data = new double[w * h];
		int counter = 0;
		for (int j = 0; j < w; j++) {
			for (int k = 0; k < h; k++) {
				array[j][k] = in.getRGB(j, k);
				data[counter] = in.getRGB(j, k);
				counter++;
			}
		}
		System.out.println("___****the Four bit By Row****___");
		createCompressedFile(getRunLengthByRow(array), path);
		drawHistogram(data, fileName);
		byte[] v = new byte[1 << 4];
		for (int i = 0; i < v.length; ++i) {
			v[i] = (byte) (i * 4);
		}
		ColorModel cm = new IndexColorModel(4, v.length, v, v, v);
		WritableRaster wr = cm.createCompatibleWritableRaster(w, h);
		BufferedImage out = new BufferedImage(cm, wr, false, null);
		int[][] tempo = rowByRowReOdrder(decompress(getRunLengthByRow(array),
				path));

		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				int Pixel = array[x][y] << 16 | array[x][y] << 8 | array[x][y];
				out.setRGB(x, y, tempo[x][y]);
			}

		}

		Graphics2D g = out.createGraphics();
		g.drawImage(out, 0, 0, null);
		g.dispose();
		ImageIO.write(in, "bmp", new File("./4bits/4bitByRow" + fileName));
	}

	public void oneBitsBMPsZigzag(String path) throws IOException {

		System.out.println("Name of file is !!" + new File(path).getName());
		String fileName = new File(path).getName();
		BufferedImage in = ImageIO.read(new File(path));
		int w = in.getWidth(), h = in.getHeight();
		int[][] array = new int[w][h];
		double[] data = new double[w * h];
		int counter = 0;
		for (int j = 0; j < w; j++) {
			for (int k = 0; k < h; k++) {
				array[j][k] = in.getRGB(j, k);
				data[counter] = in.getRGB(j, k);
				counter++;
			}
		}
		System.out.println("___****The One bit Zigzag****___");
		createCompressedFile(getRunLengthZigzag(array), path);
		drawHistogram(data, fileName);

		System.out.println("finish");
		byte[] v = new byte[1 << 8];

		for (int i = 0; i < v.length; ++i) {
			v[i] = (byte) (i * 17);
		}
		Color[] colors = { Color.red, Color.green, Color.yellow, Color.black };
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
		int index = 0;
		int[][] tempo = zigzagReorder(decompress(getRunLengthZigzag(array),
				path));
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				int Pixel = array[x][y] << 16 | array[x][y] << 8 | array[x][y];
				index++;
				out.setRGB(x, y, array[x][y]);
			}
		}

		Graphics2D g = out.createGraphics();
		g.drawImage(out, 0, 0, null);
		g.dispose();
		ImageIO.write(out, "bmp", new File("./1bit/1bitZigzag" + fileName));
	}

	public void oneBitsBMPsColumn(String path) throws IOException {

		System.out.println("Name of file is !!" + new File(path).getName());
		String fileName = new File(path).getName();
		BufferedImage in = ImageIO.read(new File(path));
		int w = in.getWidth(), h = in.getHeight();

		int[][] array = new int[w][h];
		double[] data = new double[w * h];
		int counter = 0;
		for (int j = 0; j < w; j++) {
			for (int k = 0; k < h; k++) {
				array[j][k] = in.getRGB(j, k);
				data[counter] = in.getRGB(j, k);
				counter++;
			}
		}
		System.out.println("___****The One bit By Column****____");

		createCompressedFile(getRunLengthByColumn(array), path);
		drawHistogram(data, fileName);

		System.out.println("finish");
		byte[] v = new byte[1 << 8];

		for (int i = 0; i < v.length; ++i) {
			v[i] = (byte) (i * 17);
		}
		Color[] colors = { Color.red, Color.green, Color.yellow, Color.black };
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
		int index = 0;
		// int[][] tempo =
		// rowByRowReOdrder(decompress(getRunLengthByRow(array),fileName));
		int[][] tempo = columnByColumnOdrder(decompress(
				getRunLengthByColumn(array), path));
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				int Pixel = array[x][y] << 16 | array[x][y] << 8 | array[x][y];
				index++;
				out.setRGB(x, y, tempo[x][y]);
			}
		}

		Graphics2D g = out.createGraphics();
		g.drawImage(out, 0, 0, null);
		g.dispose();
		ImageIO.write(out, "bmp", new File("./1bit/1bitColumn" + fileName));
	}

	public void oneBitsBMPsRowByRow(String path) throws IOException {

		System.out.println("Name of file is !!"
				+ new File("D:\\baboon_BW.bmp").getName());
		String fileName = new File("D:\\baboon_BW.bmp").getName();
		BufferedImage in = ImageIO.read(new File(path));
		int w = in.getWidth(), h = in.getHeight();

		int[][] array = new int[w][h];
		int counter = 0;
		double[] data = new double[w * h];
		for (int j = 0; j < w; j++) {
			for (int k = 0; k < h; k++) {
				array[j][k] = in.getRGB(j, k);
				data[counter] = in.getRGB(j, k);
				counter++;

			}
		}
		System.out.println("___****The One bit by Row****___");

		createCompressedFile(getRunLengthByRow(array), path);
		drawHistogram(data, fileName);

		System.out.println("finish");
		byte[] v = new byte[1 << 8];

		for (int i = 0; i < v.length; ++i) {
			v[i] = (byte) (i * 17);
			// System.out.println(v[i]);
		}
		Color[] colors = { Color.red, Color.green, Color.yellow, Color.black };
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
		int index = 0;
		int[][] tempo = rowByRowReOdrder(decompress(getRunLengthByRow(array),
				path));
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				int Pixel = array[x][y] << 16 | array[x][y] << 8 | array[x][y];
				index++;
				out.setRGB(x, y, tempo[x][y]);
			}
		}
		Graphics2D g = out.createGraphics();
		g.drawImage(out, 0, 0, null);
		g.dispose();
		ImageIO.write(out, "bmp", new File("./1bit/1bitRow" + fileName));
	}

	public void eightBitsBMPsZigzag(String path) throws IOException {
		String fileName = new File(path).getName();
		BufferedImage img = ImageIO.read(new File(path));
		int w = img.getWidth();
		int h = img.getHeight();
		int[][] array = new int[w][h];
		double[] data = new double[w * h];
		int counter = 0;
		for (int j = 0; j < w; j++) {
			for (int k = 0; k < h; k++) {
				array[j][k] = img.getRGB(j, k);
				data[counter] = img.getRGB(j, k);
				counter++;

			}
		}
		System.out.println("___****the Eight bit Zigzag****___");
		System.out.println("File Name: " + fileName);
		createCompressedFile(getRunLengthZigzag(array), path);
		drawHistogram(data, fileName);
		try {
			BufferedImage bufferImage2 = new BufferedImage(w, h,
					BufferedImage.TYPE_BYTE_GRAY);
			int[][] tempo = zigzagReorder(decompress(getRunLengthZigzag(array),
					path));
			for (int y = 0; y < h; y++) {
				for (int x = 0; x < w; x++) {
					int Pixel = array[x][y] << 16 | array[x][y] << 8
							| array[x][y];
					bufferImage2.setRGB(x, y, array[x][y]);
				}
			}
			Graphics g = bufferImage2.getGraphics();
			g.drawImage(bufferImage2, h, h, null);
			g.dispose();
			ImageIO.write(bufferImage2, "bmp", new File("./8bits/8bitZigzag"
					+ fileName));
		} catch (Exception ee) {
			ee.printStackTrace();
		}

	}

	public void eightBitsBMPsColumn(String path) throws IOException {
		String fileName = new File(path).getName();
		BufferedImage img = ImageIO.read(new File(path));
		int w = img.getWidth();
		int h = img.getHeight();
		int[][] array = new int[w][h];
		double[] data = new double[w * h];
		int counter = 0;
		for (int j = 0; j < w; j++) {
			for (int k = 0; k < h; k++) {
				array[j][k] = img.getRGB(j, k);
				data[counter] = img.getRGB(j, k);
				// System.out.println("RGB Array"+array[j][j]);
				counter++;

			}
		}
		System.out.println("___****the Eight bit by Column****___");
		System.out.println("File Name: " + fileName);
		createCompressedFile(getRunLengthByColumn(array), path);
		drawHistogram(data, fileName);
		try {
			BufferedImage bufferImage2 = new BufferedImage(w, h,
					BufferedImage.TYPE_BYTE_GRAY);
			int[][] tempo = columnByColumnOdrder(decompress(
					getRunLengthByColumn(array), path));
			for (int y = 0; y < h; y++) {
				for (int x = 0; x < w; x++) {
					int Pixel = array[x][y] << 16 | array[x][y] << 8
							| array[x][y];
					bufferImage2.setRGB(x, y, tempo[x][y]);
				}
			}
			Graphics g = bufferImage2.getGraphics();
			g.drawImage(bufferImage2, h, h, null);
			g.dispose();
			ImageIO.write(bufferImage2, "bmp", new File("./8bits/8bitColumn"
					+ fileName));
		} catch (Exception ee) {
			ee.printStackTrace();
		}

	}

	public void eightBitsBMPsRowByRow(String path) throws IOException {
		String fileName = new File(path).getName();
		BufferedImage img = ImageIO.read(new File(path));
		int w = img.getWidth();
		int h = img.getHeight();
		int[][] array = new int[w][h];
		double[] data = new double[w * h];
		int counter = 0;
		for (int j = 0; j < w; j++) {
			for (int k = 0; k < h; k++) {
				array[j][k] = img.getRGB(j, k);
				data[counter] = img.getRGB(j, k);
				counter++;

			}
		}
		System.out.println("___****the Eight bit By Row****___");
		System.out.println("File Name: " + fileName);
		createCompressedFile(getRunLengthByRow(array), path);
		drawHistogram(data, fileName);
		try {
			BufferedImage bufferImage2 = new BufferedImage(w, h,
					BufferedImage.TYPE_BYTE_GRAY);
			int[][] tempo = rowByRowReOdrder(decompress(
					getRunLengthByRow(array), path));
			for (int y = 0; y < h; y++) {
				for (int x = 0; x < w; x++) {
					int Pixel = array[x][y] << 16 | array[x][y] << 8
							| array[x][y];
					bufferImage2.setRGB(x, y, tempo[x][y]);
				}
			}
			Graphics g = bufferImage2.getGraphics();
			g.drawImage(bufferImage2, h, h, null);
			g.dispose();
			ImageIO.write(bufferImage2, "bmp", new File("./8bits/8bitRow"
					+ fileName));
		} catch (Exception ee) {
			ee.printStackTrace();
		}

	}

	public void drawHistogram(double[] data, String name) {

		HistogramDataset histogramdataset = new HistogramDataset();
		histogramdataset.setType(HistogramType.FREQUENCY);
		histogramdataset.addSeries(name, data, 17);
		JFreeChart chart1 = ChartFactory.createHistogram(name, "X-Axis",
				"Y-Axis", histogramdataset, PlotOrientation.VERTICAL, true,
				true, false);
		JFrame frame = new JFrame(name);
		frame.setContentPane(new ChartPanel(chart1));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}

	public int[] getRunLengthZigzag(int[][] imageByteArray) {
		// System.out.println("Getting run lenght by Zigzag");
		IntArrayOutputStream dest = new IntArrayOutputStream();
		int lastByte = imageByteArray[0][0];
		int matchCount = 1;
		int count = 0;
		int r = 0;
		int c = 0;
		int dir = 0;
		int rows = imageByteArray[0].length;
		int cols = imageByteArray[1].length;
		while (r < imageByteArray[0].length && c < imageByteArray[1].length) {
			// System.out.println( r+" -> "+c);
			int thisByte = imageByteArray[r][c];
			if (lastByte == thisByte) {
				matchCount++;
			} else {
				dest.write(matchCount);
				dest.write(lastByte);
				count++;
				matchCount = 1;
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
		System.out.println("Compression Ratio =  " + -1
				* (((count * 100) / 262144) - 100));
		// System.out.println("Number of records: " + count);
		dest.write(matchCount);
		dest.write(lastByte);
		// System.out.println("**** Finished Getting run lenght by Zigzag ****");
		return dest.getIntArray();
	}

	public int[] getRunLengthByRow(int[][] imageByteArray) {
		// System.out.println("Getting run lenght by row");
		IntArrayOutputStream dest = new IntArrayOutputStream();
		int lastByte = imageByteArray[0][0];
		int matchCount = 1;

		int count = 0;
		for (int i = 0; i < imageByteArray[0].length; i++) {
			for (int j = 0; j < imageByteArray[0].length; j++) {

				int thisByte = imageByteArray[i][j];
				// System.out.print("Array value: "+imageByteArray[i][j]);
				// System.out.println("  This byte value: "+thisByte);
				if (lastByte == thisByte) {
					matchCount++;
				} else {
					dest.write(matchCount);
					dest.write(lastByte);

					count++;

					matchCount = 1;
					lastByte = thisByte;

				}
			}

		}
		System.out.println("Compression Ratio =  " + -1
				* (((count * 100) / 262144) - 100));
		// System.out.println("Number of records: " + count);
		dest.write(matchCount);
		dest.write(lastByte);
		int[] rl = new int[dest.getIntArray().length];
		rl = dest.getIntArray();
		int check = 0;
		for (int i = 0; i < rl.length; i = i + 2) {
			check += rl[i];
			// System.out.println(rl[i]);

		}

		// System.out.println("**** Finished Getting run lenght by row ****");
		return dest.getIntArray();

	}

	public int[] getRunLengthByColumn(int[][] imageByteArray) {
		// System.out.println("Getting run lenght by Column");
		IntArrayOutputStream dest = new IntArrayOutputStream();
		int lastByte = imageByteArray[0][0];
		int matchCount = 1;
		int count = 0;
		for (int i = 1; i < imageByteArray[0].length; i++) {
			for (int j = 0; j < imageByteArray[1].length; j++) {

				int thisByte = imageByteArray[j][i];
				if (lastByte == thisByte) {
					matchCount++;
				} else {
					dest.write(matchCount);
					dest.write(lastByte);
					// System.out.println("Number of repetitions: "+
					// matchCount);
					count++;

					matchCount = 1;
					lastByte = thisByte;

				}
			}
		}
		// System.out.println("Number of records: " + count);
		System.out.println("Compression Ratio =  " + -1
				* (((count * 100) / 262144) - 100));
		dest.write(matchCount);
		dest.write(lastByte);
		// System.out.println("**** Finished Getting run lenght by Column ****");
		return dest.getIntArray();

	}

	public void createCompressedFile(int[] rle, String path) {
		int check = 0;
		for (int i = 0; i < rle.length; i++) {
			check += rle[i];
			i++;
		}
		int[] compressed = new int[rle.length + 1024];// could need more
		IntegerCODEC codec = new Composition(new FastPFOR(), new VariableByte());
		// compressing
		IntWrapper inputoffset = new IntWrapper(0);
		IntWrapper outputoffset = new IntWrapper(0);
		codec.compress(rle, inputoffset, rle.length, compressed, outputoffset);
		// System.out.println("compressed unsorted integers from " + rle.length
		// * 4 / 1024 + "KB to " + outputoffset.intValue() * 4 / 1024
		// + "KB");
		compressed = Arrays.copyOf(compressed, outputoffset.intValue());
		// System.out.println("compressed array size" + compressed.length);

		try {
			ObjectOutputStream myStream = new ObjectOutputStream(
					new FileOutputStream(path + ".dat"));
			myStream.writeObject(compressed);
			myStream.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}

	}

	public int[] decompress(int[] rle, String path) {
		int[] recovered = new int[rle.length];
		// reading of the binary file and placing it into an array
		try {
			ObjectInputStream mySecondStream = new ObjectInputStream(
					new FileInputStream(path + ".dat"));
			int[] array = (int[]) mySecondStream.readObject();
			for (int i = 0; i < array.length; i++) {
				// System.out.println(array[i]);
			}
			mySecondStream.close();
			IntegerCODEC codec = new Composition(new FastPFOR(),
					new VariableByte());

			IntWrapper recoffset = new IntWrapper(0);
			codec.uncompress(array, new IntWrapper(0), array.length, recovered,
					recoffset);

			if (Arrays.equals(rle, recovered))
				System.out.println("data is recovered without loss");
			else
				throw new RuntimeException("bug"); // could use assert
			System.out.println();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int x = 0; x < recovered.length; x++) {
			// System.out.println("Value of X: "+recovered[x]);
		}
		// rowByRowReOdrder(recovered);
		return recovered;

	}

	public int[][] rowByRowReOdrder(int[] rle) {
		int[] temp = new int[262145];
		int x = 0;
		int counter = 0;
		int check = 0;
		while (x < rle.length) {
			for (int y = 0; y < rle[x]; y++) {
				temp[counter] = rle[x + 1];

				// System.out.println("Counter val = "+counter);
				counter++;
			}
			x = x + 2;
		}

		int index = 0;
		int[][] array = new int[512][512];
		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < array.length; j++) {
				array[i][j] = temp[index];
				index++;
			}
		}
		return array;
	}

	public int[][] columnByColumnOdrder(int[] rle) {
		int[] temp = new int[262145];
		int x = 0;
		int counter = 0;
		int check = 0;
		while (x < rle.length) {
			for (int y = 0; y < rle[x]; y++) {
				temp[counter] = rle[x + 1];

				// System.out.println("Counter val = "+counter);
				counter++;
			}
			x = x + 2;
		}

		int index = 0;
		int[][] array = new int[512][512];
		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < array.length; j++) {
				array[j][i] = temp[index];
				index++;
			}
		}
		return array;
	}

	public int[][] zigzagReorder(int[] rle) {
		int[] temp = new int[262145];
		int x = 0;
		int counter = 0;
		while (x < rle.length) {
			for (int y = 0; y < rle[x]; y++) {
				temp[counter] = rle[x + 1];

				// System.out.println("Counter val = "+counter);
				counter++;
			}
			x = x + 2;
		}

		int index = 0;
		int[][] array = new int[512][512];
		int r = 0;
		int c = 0;
		int dir = 0;
		int rows = 512;
		int cols = 512;
		while (r < 512 && c < 512) {
			// System.out.println( r+" -> "+c);
			array[r][c] = temp[index];
			index++;

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
		return array;

	}
}
