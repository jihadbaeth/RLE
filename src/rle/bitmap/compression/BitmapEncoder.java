/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rle.bitmap.compression;

/**
 *
 * @author Mohamed Jehad Baeth <jihadbaeth@gmail.com>
 */
/**
<HTML>
<H>Java Tip 60: Saving bitmap files in Java</H>
<P>
A tutorial -- including all the code you need to write a bitmap file from an image object
<P>
<STRONG>Summary</STRONG>
Although Java provides several mechanisms for opening images,
saving them is not one of its strengths.
This tip will teach you how to save an image in a 24-bit bitmap file.
Plus: Jean-Pierre provides all the code
necessary to write a bitmap file from an image object.
<em>(1,500 words)</em>

<STRONG>By Jean-Pierre Dub&eacute;</STRONG>
<P>
<a href="http://www.javaworld.com/javaworld/javatips/jw-javatip60.html">
This tip </a>,
complements
<a href="http://www.javaworld.com/javaworld/javatips/jw-javatip43.html">Java Tip 43</a>,
which demonstrated the process of loading bitmap files in Java applications.
This month,
I follow up with a tutorial on how to save images in 24-bit bitmap files
and a code snip you can use to write a bitmap file from an image object.  
<P>
The ability to create a bitmap file opens many doors if you're working
in a Microsoft Windows environment.
On my last project,
for example,
I had to interface Java with Microsoft Access.
The Java program allowed the user to draw a map on the screen.
The map was then printed in a Microsoft Access report.
Because Java doesn't support OLE,
my only solution was to create a bitmap file of the map
and tell the Microsoft Access report where to pick it up.
If you've ever had to write an application to send an image to the clipboard,
this tip may be of use to you --
especially if this information is being passed to another Windows
application.
<P>
<FONT SIZE="+1"><STRONG>The format of a bitmap file</STRONG></FONT>
The bitmap file format supports 4-bit RLE (run length encoding),
as well as 8-bit and 24-bit encoding.
Because we're only dealing with the 24-bit format,
let's take a look at the structure of the file.
<P>
The bitmap file is divided into three sections.
I've laid them out for you below.
<P>
<FONT SIZE="+1"><STRONG>Section 1: Bitmap file header</STRONG></FONT>
This header contains information about the type size and layout of the
bitmap file.
The structure is as follows (taken from a C language structure definition):
<P>
<pre>
typedef struct tagBITMAPFILEHEADER {
   UINT bfType;
   DWORD bfSize;
   UINT bfReserved1;
   UINT bfReserved2;
   DWORD bfOffBits;
}BITMAPFILEHEADER;
</pre>
<P>
Here's a description of the code elements from the above listing:
<P>
<ul>
<li><code>bfType</code>: Indicates the type of the file and is always
set to BM.
<P>
<li><code>bfSize</code>: Specifies the size of the whole file in bytes.
<P>
<li><code>bfReserved1</code>: Reserved -- must be set to 0.
<P>
<li><code>bfReserved2</code>: Reserved -- must be set to 0.
<P>
<li><code>bfOffBits</code>: Specifies the byte offset from the
<code>BitmapFileHeader</code> to the start of the image.
</ul>
<P>
Here you've seen that the purpose of the bitmap header is to identify
the bitmap file. Every program that reads bitmap files uses the bitmap
header for file validation. 
<P>
<FONT SIZE="+1"><STRONG>Section 2: Bitmap information header</STRONG></FONT>
The next header, called the <em>information header,</em>  contains all
the properties of the image itself.
<P>
Here's how you specify information about the dimension and the color
format of a Windows 3.0 (or higher) device independent bitmap (DIB):
<P>
<pre>
typedef struct tagBITMAPINFOHEADER {
    DWORD biSize;
    LONG  biWidth;
    LONG  biHeight;
    WORD  biPlanes;
    WORD  biBitCount;
    DWORD biCompression;
    DWORD biSizeImage;
    LONG  biXPelsPerMeter;
    LONG  biYPelsPerMeter;
    DWORD biClrUsed;
    DWORD biClrImportant;
} BITMAPINFOHEADER;
</pre>
<P>
Each element of the above code listing is described below:
<P>
<ul>
<li><code>biSize</code>: Specifies the number of bytes required by the
<code>BITMAPINFOHEADER</code> structure.
<P>
<li><code>biWidth</code>: Specifies the width of the bitmap in
pixels.
<P>
<li><code>biHeight</code>: Specifies the height of the bitmap in
pixels.
<P>
<li><code>biPlanes</code>: Specifies the number of planes for the
target device. This member must be set to 1.
<P>
<li><code>biBitCount</code>: Specifies the number of bits per pixel.
This value must be 1, 4, 8, or 24.
<P>
<li><code>biCompression</code>: Specifies the type of compression for a
compressed bitmap. In a 24-bit format, the variable is set to 0.
<P>
<li>biSizeImage</code>: Specifies the size in bytes of the image. It is
valid to set this member to 0 if the bitmap is in the <code>BI_RGB</code> format.
<P>
<li>biXPelsPerMeter</code>: Specifies the horizontal resolution, in
pixels per meter, of the target device for the bitmap. An application
can use this value to select a bitmap from a resource group that best
matches the characteristics of the current device.
<P>
<li>biYPelsPerMeter</code>: Specifies the vertical resolution, in
pixels per meter, of the target device for the bitmap.
<P>
<li>biClrUsed</code>: Specifies the number of color indexes in the
color table actually used by the bitmap. If <code>biBitCount</code> is
set to 24, <code>biClrUsed</code> specifies the size of the reference
color table used to optimize performance of Windows color palettes.
<P>
<li>biClrImportant</code>: Specifies the number of color indexes considered 
important for displaying the bitmap. If this value is 0, all colors are important.
</ul>
<P>
Now all the information needed to create the image has been defined.
<P>
<FONT SIZE="+1"><STRONG>Section 3: Image</STRONG></FONT>
In the 24-bit format, each pixel in the image is represented by a
series of three bytes of RGB stored as BRG. Each scan line is padded to
an even 4-byte boundary. To complicate the process a little bit more,
the image is stored from bottom to top, meaning that the first scan line
is the last scan line in the image. The following figure shows both
headers (<code>BITMAPHEADER</code>) and (<code>BITMAPINFOHEADER</code>)
and part of the image. Each section is delimited by a vertical bar:
<P>
<pre>                 
0000000000    4D42   B536   0002   0000   0000   0036   0000 | 0028
0000000020    0000   0107   0000   00E0   0000   0001   0018   0000
0000000040    0000   B500   0002   0EC4   0000   0EC4   0000   0000
0000000060    0000   0000   0000 | FFFF   FFFF   FFFF   FFFF   FFFF
0000000100    FFFF   FFFF   FFFF   FFFF   FFFF   FFFF   FFFF   FFFF
*
</pre>
<P>
<FONT SIZE="+1"><STRONG>Now, on to the code</STRONG></FONT>
Now that we know all about the structure of a 24-bit bitmap file, here's
what you've been waiting for: the code to write a bitmap file from an image object.
<P>
First,
more about compression from this site --
<a href="http://www.webartz.com/fourcc/fccrgb.htm">
"http://www.webartz.com/fourcc/fccrgb.htm"</a>
<h1><font face="Arial">RGB Formats </font></h1>

<h3><font face="Arial">Overview </font></h3>

<p><font face="Arial">These formats are defined below. Click on
the FOURCC to be taken to its definition. </font></p>

<p><font face="Arial">Please see </font>
<a href="http://www.webartz.com/fourcc/fccbihgt.htm"><font
face="Arial">&quot;Bitmap Orientation and biHeight&quot;</font></a><font
face="Arial"> for important additional information.</font></p>

<table border="1" cellspacing="1">
    <tr>
        <td align="center"><font face="Arial"><strong>Label</strong></font></td>
        <td align="center"><font face="Arial"><strong>FOURCC in
        Hex</strong></font></td>
        <td align="center"><font face="Arial"><strong>Bits per
        pixel</strong></font></td>
        <td align="center"><font face="Arial"><strong>Description</strong></font></td>
    </tr>
    <tr>
        <td align="center"><a href="#BI_RGB"><font face="Arial">BI_RGB</font></a></td>
        <td align="center"><font face="Arial">0x00000000</font></td>
        <td align="center"><font face="Arial">1,4,8,16,24,32</font></td>
        <td><font face="Arial">Basic Windows bitmap format. 1, 4
        and 8 bpp versions are palettised. 16, 24 and 32bpp
        contain raw RGB samples.</font></td>
    </tr>
    <tr>
        <td align="center"><a href="#BI_RGB"><font face="Arial">RGB</font></a></td>
        <td align="center"><font face="Arial">0x32424752</font></td>
        <td align="center"><font face="Arial">1,4,8,16,24,32</font></td>
        <td><font face="Arial">Alias for BI_RGB</font></td>
    </tr>
    <tr>
        <td align="center"><a href="#BI_RLE8"><font face="Arial">BI_RLE8</font></a></td>
        <td align="center"><font face="Arial">0x00000001</font></td>
        <td align="center"><font face="Arial">8</font></td>
        <td><font face="Arial">Run length encoded 8bpp RGB image.</font></td>
    </tr>
    <tr>
        <td align="center"><a href="#BI_RLE8"><font face="Arial">RLE8</font></a></td>
        <td align="center"><font face="Arial">0x38454C52</font></td>
        <td align="center"><font face="Arial">8</font></td>
        <td><font face="Arial">Alias for BI_RLE8</font></td>
    </tr>
    <tr>
        <td align="center"><a href="#BI_RLE4"><font face="Arial">BI_RLE4</font></a></td>
        <td align="center"><font face="Arial">0x00000002</font></td>
        <td align="center"><font face="Arial">4</font></td>
        <td><font face="Arial">Run length encoded 4bpp RGB image.</font></td>
    </tr>
    <tr>
        <td align="center"><a href="#BI_BITFIELDS"><font
        face="Arial">BI_BITFIELDS</font></a></td>
        <td align="center"><font face="Arial">0x00000003</font></td>
        <td align="center"><font face="Arial">16,24,32</font></td>
        <td><font face="Arial">Raw RGB with arbitrary sample
        packing within a pixel. Packing and precision of R, G and
        B components is determined by bit masks for each.</font></td>
    </tr>
    <tr>
        <td align="center"><a href="#RGBA"><font face="Arial">RGBA</font></a></td>
        <td align="center"><font face="Arial">0x41424752</font></td>
        <td align="center"><font face="Arial">16,32</font></td>
        <td><font face="Arial">Raw RGB with alpha. Sample
        precision and packing is arbitrary and determined using
        bit masks for each component, as for BI_BITFIELDS.</font></td>
    </tr>
    <tr>
        <td align="center"><a href="#RGBT"><font face="Arial">RGBT</font></a></td>
        <td align="center"><font face="Arial">0x54424752</font></td>
        <td align="center"><font face="Arial">16,32</font></td>
        <td><font face="Arial">Raw RGB with a transparency field.
        Layout is as for BI_RGB at 16 and 32 bits per pixel but
        the msb in each pixel indicates whether the pixel is
        transparent or not.</font></td>
    </tr>
</table>

<h3><a name="BI_RGB"><font face="Arial">BI_RGB</font></a><font
face="Arial"> </font></h3>

<p><font face="Arial">This is the basic RBG bitmap format which
comes in all the common bits per pixel flavours. 1, 4 and 8 bpp
versions of the format are palettised and 16, 24 and 32 bpp
contain direct colour information. In all cases, the bitmap
comprises a rectangular array of packed pixels.</font></p>

<h4><font face="Arial">1bpp</font></h4>

<p><font face="Arial">Each pixel is represented by a single bit,
giving 8 pixels per BYTE in memory. A 2 entry palette defines
which colours are used to display the pixel if the bit is clear
(palette entry 0) or set (palette entry 1). Despite the fact that
this definition is apparently pretty clear, many display adapters
and graphics applications appear to reverse the definition so, in
my experience, you are never entirely sure if a 1bpp bitmap will
be displayed as a positive or negative image.</font></p>

<h4><font face="Arial">4bpp</font></h4>

<p><font face="Arial">Each pixel here is represented by a nibble,
giving 2 pixels per BYTE in memory. The 4 bits used for the pixel
give rise to 16 possible values and, hence, a 16 entry colour
palette is required to display the image.</font></p>

<h4><font face="Arial">8bpp</font></h4>

<p><font face="Arial">Each pixel here is represented by a BYTE,
giving 256 possible values per pixel and, hence, requiring a 256
entry palette.</font></p>

<h4><font face="Arial">16bpp</font></h4>

<p><font face="Arial">Things were pretty simple up to now but
some confusion is introduced by the 16bpp format. It's actually
15bpp since the default format is actually RGB 5:5:5 with the top
bit of each u_int16 being unused. In this format, each of the
red, green and blue colour components is represented by a 5 bit
number giving 32 different levels of each and 32786 possible
different colours in total (true 16bpp would be RGB 5:6:5 where
there are 65536 possible colours). No palette is used for 16bpp
RGB images - the red, green and blue values in the pixel are used
to define the colours directly.</font></p>

<p><font face="Arial">As an aside, most display drivers handle
both RGB 5:5:5 and 5:6:5 formats but several video codecs get
confused when asked to decompress to a 5:6:5 desktop so using
5:5:5 as the default is generally safer.</font></p>

<h4><font face="Arial">24bpp</font></h4>

<p><font face="Arial">We get back to predictable ground with
24bpp. Here a pixel is represented by 3 BYTES containing a red,
blue and green sample (with blue stored at the lowest address,
green next then red). No padding bytes are added between pixels.
Although I can't find the information in any spec I have on my
machine currently, I get the feeling that 24bpp images should be
stored with each line padded to a u_int32 boundary. Information
on </font><a
href="http://www.mediatel.lu/workshop/graphic/2D_fileformat/h_bmp.html"><font
face="Arial">http://www.mediatel.lu/workshop/graphic/2D_fileformat/h_bmp.html</font></a><font
face="Arial"> also suggests that this is true.</font></p>

<h4><font face="Arial">32bpp</font></h4>

<p><font face="Arial">This is another version of 24bpp where each
pixel is padded to a u_int32. Although this is inefficient from a
memory standpoint, processing u_int32s is a great deal easier
than handling byte triples and the format is used by many
graphics accelerators because of this.</font></p>

<h3><a name="BI_RLE8"><font face="Arial">BI_RLE8</font></a><font
face="Arial"> </font></h3>

<p><font face="Arial">The following definition and example are
quoted from the Windows 3.1 API online help file.</font></p>

<blockquote>
    <p><font face="Arial">&quot;<em>When the biCompression member
    is set to BI_RLE8, the bitmap is compressed using a
    run-length encoding format for an 8-bit bitmap. This format
    may be compressed in either of two modes: encoded and
    absolute. Both modes can occur anywhere throughout a single
    bitmap. </em></font></p>
    <p><font face="Arial"><em>Encoded mode consists of two bytes:
    the first byte specifies the number of consecutive pixels to
    be drawn using the color index contained in the second byte.
    In addition, the first byte of the pair can be set to zero to
    indicate an escape that denotes an end of line, end of
    bitmap, or a delta. The interpretation of the escape depends
    on the value of the second byte of the pair. The following
    list shows the meaning of the second byte: </em></font></p>
    <table border="0" cellpadding="2" cellspacing="3">
        <tr>
            <th><font face="Arial"><em>Value</em></font></th>
            <th align="left"><font face="Arial"><em>Meaning</em></font></th>
        </tr>
        <tr>
            <td align="center"><font face="Arial"><em>0</em></font></td>
            <td><font face="Arial"><em>End of line</em></font></td>
        </tr>
        <tr>
            <td align="center"><font face="Arial"><em>1</em></font></td>
            <td><font face="Arial"><em>End of bitmap</em></font></td>
        </tr>
        <tr>
            <td align="center"><font face="Arial"><em>2</em></font></td>
            <td><font face="Arial"><em>Delta. The two bytes
            following the escape contain unsigned values
            indicating the horizontal and vertical offset of the
            next pixel from the current position.</em></font></td>
        </tr>
    </table>
    <p><font face="Arial"><em>Absolute mode is signaled by the
    first byte set to zero and the second byte set to a value
    between 0x03 and 0xFF. In absolute mode, the second byte
    represents the number of bytes that follow, each of which
    contains the color index of a single pixel. When the second
    byte is set to 2 or less, the escape has the same meaning as
    in encoded mode. In absolute mode, each run must be aligned
    on a u_int16 boundary.</em></font></p>
    <p><font face="Arial"><em>The following example shows the
    hexadecimal values of an 8-bit compressed bitmap:</em></font></p>
    <p><font face="Arial"><em>03 04 05 06 00 03 45 56<br>
    67 00 02 78 00 02 05 01<br>
    02 78 00 00 09 1E 00 01</em></font></p>
    <p><font face="Arial"><em>This bitmap would expand as follows
    (two-digit values represent a color index for a single
    pixel): </em></font></p>
    <p><font face="Arial"><em>04 04 04<br>
    06 06 06 06 06<br>
    45 56 67<br>
    78 78<br>
    move current position 5 right and 1 down<br>
    78 78<br>
    end of line<br>
    1E 1E 1E 1E 1E 1E 1E 1E 1E<br>
    end of RLE bitmap&quot;</em></font></p>
</blockquote>

<h3><a name="BI_RLE4"><font face="Arial">BI_RLE4</font></a><font
face="Arial"> </font></h3>

<p><font face="Arial">The following definition and example are
quoted from the Windows 3.1 API online help file.</font></p>

<blockquote>
    <p><font face="Arial"><em>&quot;When the biCompression member
    is set to BI_RLE4, the bitmap is compressed using a
    run-length encoding (RLE) format for a 4-bit bitmap, which
    also uses encoded and absolute modes. In encoded mode, the
    first byte of the pair contains the number of pixels to be
    drawn using the color indexes in the second byte. The second
    byte contains two color indexes, one in its high-order nibble
    (that is, its low-order four bits) and one in its low-order
    nibble. The first of the pixels is drawn using the color
    specified by the high-order nibble, the second is drawn using
    the color in the low-order nibble, the third is drawn with
    the color in the high-order nibble, and so on, until all the
    pixels specified by the first byte have been drawn. </em></font></p>
    <p><font face="Arial"><em>In absolute mode, the first byte
    contains zero, the second byte contains the number of color
    indexes that follow, and subsequent bytes contain color
    indexes in their high- and low-order nibbles, one color index
    for each pixel. In absolute mode, each run must be aligned on
    a u_int16 boundary. The end-of-line, end-of-bitmap, and delta
    escapes also apply to BI_RLE4.</em></font></p>
    <p><font face="Arial"><em>The following example shows the
    hexadecimal values of a 4-bit compressed bitmap: </em></font></p>
    <p><font face="Arial"><em>03 04 05 06 00 06 45 56 67 00 04 78
    00 02 05 01<br>
    04 78 00 00 09 1E 00 01</em></font></p>
    <p><font face="Arial"><em>This bitmap would expand as follows
    (single-digit values represent a color index for a single
    pixel): </em></font></p>
    <p><font face="Arial"><em>0 4 0<br>
    0 6 0 6 0<br>
    4 5 5 6 6 7<br>
    7 8 7 8<br>
    move current position 5 right and 1 down<br>
    7 8 7 8<br>
    end of line<br>
    1 E 1 E 1 E 1 E 1<br>
    end of RLE bitmap&quot;</em></font></p>
</blockquote>

<h3><a name="BI_BITFIELDS"><font face="Arial">BI_BITFIELDS</font></a><font
face="Arial"> </font></h3>

<p><font face="Arial">To allow for arbitrarily packed RGB
samples, BI_BITFIELDS specifies a mask field for each of the red,
green and blue pixel components. These masks indicate the bit
positions occupied by each colour component in a pixel. In
general, the masks are passed to a driver or video API using
means other than a basic BITMAPINFOHEADER (such as using the
appropriate fields in a DirectDraw DDPIXELFORMAT structure) but I
have heard that it is valid to append the masks to the end of the
BITMAPINFOHEADER in much the same way that a palette is appended
for palettised formats.</font></p>

<p><font face="Arial">For example, 16 bit RGB 5:6:5 can be
described using BI_BITFIELDS and the following bitmasks:</font></p>

<table border="0" width="40%">
    <tr>
        <td width="50%"><font face="Arial">Red</font></td>
        <td width="75%"><font face="Arial">0xF800 (5 bits of red)</font></td>
    </tr>
    <tr>
        <td width="50%"><font face="Arial">Green</font></td>
        <td width="75%"><font face="Arial">0x07E0 (6 bits of
        green)</font></td>
    </tr>
    <tr>
        <td width="50%"><font face="Arial">Blue</font></td>
        <td width="75%"><font face="Arial">0x001F (5 bits of
        blue)</font></td>
    </tr>
</table>

<p><font face="Arial">In this case, if used with a
BITMAPINFOHEADER, the bitmasks are u_int16s (16 bit) since the
biBitFields field is set to 16. For a 32bpp version, the bitmasks
are each u_int32s.</font></p>

<h3><a name="RGBA"><font face="Arial">RGBA</font></a><font
face="Arial"> </font></h3>

<p><font face="Arial">This format is an extension of BI_BITFIELDS
where a fourth bitmask is used to define bits in the pixel which
correspond to an alpha channel. When displayed on top of other
images, RGBA pixels are blended with the background pixel
according to the value of this alpha component.</font></p>

<p><font face="Arial">For example, a 32bpp RGBA image would
likely use the top 8 bits of each u_int32 to store the alpha
component (the unused byte in normal 32bpp RGB). In this case,
the masks reported would be:</font></p>

<table border="0" width="30%">
    <tr>
        <td width="50%"><font face="Arial">Red</font></td>
        <td width="50%"><font face="Arial">0x00FF0000</font></td>
    </tr>
    <tr>
        <td width="50%"><font face="Arial">Green</font></td>
        <td width="50%"><font face="Arial">0x0000FF00</font></td>
    </tr>
    <tr>
        <td width="50%"><font face="Arial">Blue</font></td>
        <td width="50%"><font face="Arial">0x000000FF</font></td>
    </tr>
    <tr>
        <td width="50%"><font face="Arial">Alpha</font></td>
        <td width="50%"><font face="Arial">0xFF000000</font></td>
    </tr>
</table>

<p><font face="Arial">giving 256 levels of blending per pixel (8
bits of alpha data).</font></p>

<p><font face="Arial">In general, the masks used for this format
are passed using a means other than a BITMAPINFOHEADER (for
example, in DirectDraw, the DDPIXELFORMAT structure contains
fields specifically for R,G,B and Alpha masks) but I have also
heard that it is acceptable to append 4 u_int32s to the end of
the BITMAPINFOHEADER structure containing the mask information.</font></p>

<h3><a name="RGBT"><font face="Arial">RGBT</font></a><font
face="Arial"> </font></h3>

<p><font face="Arial">This format can be thought of as a simple
extension to the basic 16bpp and 32bpp flavours of BI_RGB. RGBT
uses the most significant bit of the pixel (unused in RGB 16bpp
and 32bpp) to indicate transparency. If the bit is set, the pixel
is visible, otherwise it is transparent. You can also think of
this as a version of RGBA where the alpha channel comprises a
single bit.</font></p>

<p align="center"><a href="http://www.webartz.com/dave"><font
face="Arial"><img
src="http://www.webartz.com/cgi-bin/countbtn.cgi?name=fourccrgb&amp;image=../public_html/images/davepgbt.gif"
align="right" border="0" hspace="0" width="80" height="32"></font></a><a
href="http://www.webartz.com/fourcc/index.htm" target="_top"><font face="Arial">Intro</font></a><font
face="Arial"> | </font><a href="http://www.webartz.com/fourcc/fccrgb.htm" target="Contents"><font
face="Arial">RGB Formats</font></a><font face="Arial"> | </font><a
href="fccyuv.htm" target="Contents"><font face="Arial">YUV
Formats</font></a><font face="Arial"> | </font><a
href="fcccodec.htm" target="Contents"><font face="Arial">Compressed
Formats</font></a><font face="Arial"> <br>
</font><a href="fccchips.htm" target="Contents"><font
face="Arial">Chips</font></a><font face="Arial"> | </font><a
href="fccsampl.htm" target="Contents"><font face="Arial">Samples</font></a><font
face="Arial"> | </font><a href="fccreg.htm" target="Contents"><font
face="Arial">Register</font></a><font face="Arial"> | </font><a
href="fcclinks.htm" target="Contents"><font face="Arial">Links</font></a><font
face="Arial"> | </font><a href="fcccredt.htm" target="Contents"><font
face="Arial">Credits</font></a><font face="Arial"> | </font><a
href="fcchelp.htm" target="Contents"><font face="Arial">Help</font></a></p>
<P>
Now at last the code..
<P>
<pre>
**/
import java.awt.*;
import java.io.*;
import java.awt.image.*;

public class BitmapEncoder {

  //--- Private constants
  private final static int BITMAPFILEHEADER_SIZE = 14;
  private final static int BITMAPINFOHEADER_SIZE = 40;

  //--- Private variable declaration

  //--- Bitmap file header
  private byte bitmapFileHeader [] = new byte [14];
  private byte bfType [] = {'B', 'M'};
  private int bfSize = 0;
  private int bfReserved1 = 0;
  private int bfReserved2 = 0;
  private int bfOffBits = BITMAPFILEHEADER_SIZE + BITMAPINFOHEADER_SIZE;

  //--- Bitmap info header
  private byte bitmapInfoHeader [] = new byte [40];
  private int biSize = BITMAPINFOHEADER_SIZE;
  private int biWidth = 0;
  private int biHeight = 0;
  private int biPlanes = 1;
  private int biBitCount = 24;
  private int biCompression = 0;
  private int biSizeImage = 0x030000;
  private int biXPelsPerMeter = 0x0;
  private int biYPelsPerMeter = 0x0;
  private int biClrUsed = 0;
  private int biClrImportant = 0;

  //--- Bitmap raw data
  private int bitmap [];

  //--- File
  private OutputStream fo;

  //--- Default constructor

  public BitmapEncoder () { }

/* This the main method of the process.
 * It assumes that getWidth () and getHeight () are valid!
 * convertImage convert the memory image to a byte array;
 * writeBitmapFileHeader creates and writes the bitmap file header;
 * writeBitmapInfoHeader creates the information header;
 * writeBitmap writes the image. */

  public boolean encode (Image parImage, OutputStream fos) {
     this.fo = fos;
     try {
	int parWidth = parImage.getWidth (null),
	    parHeight = parImage.getHeight (null);

        if ((parWidth < 1) || (parHeight < 1))
	    return false;

        if (convertImage (parImage, parWidth, parHeight)) {
            writeBitmapFileHeader ();
            writeBitmapInfoHeader ();
            writeBitmap ();
	    return true;
        }
     }
     catch (Exception saveEx) { saveEx.printStackTrace (); }
     return false;
  }

/* convertImage converts the memory image to the bitmap format (BRG).
 * It also computes some information for the bitmap info header.  */

  private boolean convertImage (Image parImage, int parWidth, int parHeight) {

     int pad;
     bitmap = new int [parWidth * parHeight];

     PixelGrabber pg
	= new PixelGrabber (parImage, 0, 0, parWidth, parHeight,
                             bitmap, 0, parWidth);

     try { pg.grabPixels (); }
     catch (InterruptedException e) {
        e.printStackTrace ();
        return (false);
     }

     pad = (4 - ((parWidth * 3) % 4)) * parHeight;
     biSizeImage = ((parWidth * parHeight) * 3) + pad;
     bfSize = biSizeImage + BITMAPFILEHEADER_SIZE + BITMAPINFOHEADER_SIZE;
     biWidth = parWidth;
     biHeight = parHeight;

     return (true);
  }

/* writeBitmap converts the image returned from the pixel grabber to
 * the format required. Remember: scan lines are inverted in a bitmap file!
 *
 * Each scan line must be padded to an even 4-byte boundary.  */

  private void writeBitmap () {

      int size;
      int value;
      int j;
      int i;
      int rowCount;
      int rowIndex;
      int lastRowIndex;
      int pad;
      int padCount;
      byte rgb [] = new byte [3];


      size = (biWidth * biHeight) - 1;
      pad = 4 - ((biWidth * 3) % 4);

      if (pad == 4)   // <==== Bug correction
         pad = 0;     // <==== Bug correction

      rowCount = 1;
      padCount = 0;
      rowIndex = size - biWidth;
      lastRowIndex = rowIndex;

      try {
         for (j = 0; j < size; j++) {
            value = bitmap [rowIndex];
            rgb [0] = (byte) (value & 0xFF);
            rgb [1] = (byte) ((value >> 8) & 0xFF);
            rgb [2] = (byte) ((value >> 16) & 0xFF);
            fo.write (rgb);
            if (rowCount == biWidth) {
               padCount += pad;
               for (i = 1; i <= pad; i++) {
                  fo.write (0x00);
               }
               rowCount = 1;
               rowIndex = lastRowIndex - biWidth;
               lastRowIndex = rowIndex;
            }
            else
               rowCount++;
            rowIndex++;
         }

         //--- Update the size of the file
         bfSize += padCount - pad;
         biSizeImage += padCount - pad;
      }
      catch (Exception wb) { wb.printStackTrace (); }

   }  

/* writeBitmapFileHeader writes the bitmap file header to the file.  */

  private void writeBitmapFileHeader () {

     try {
        fo.write (bfType);
        fo.write (intToDWord (bfSize));
        fo.write (intToWord (bfReserved1));
        fo.write (intToWord (bfReserved2));
        fo.write (intToDWord (bfOffBits));

     }
     catch (Exception wbfh) {
        wbfh.printStackTrace ();
     }
  }

/* writeBitmapInfoHeader writes the bitmap information header to the file.  */

  private void writeBitmapInfoHeader () {

     try {
        fo.write (intToDWord (biSize));
        fo.write (intToDWord (biWidth));
        fo.write (intToDWord (biHeight));
        fo.write (intToWord (biPlanes));
        fo.write (intToWord (biBitCount));
        fo.write (intToDWord (biCompression));
        fo.write (intToDWord (biSizeImage));
        fo.write (intToDWord (biXPelsPerMeter));
        fo.write (intToDWord (biYPelsPerMeter));
        fo.write (intToDWord (biClrUsed));
        fo.write (intToDWord (biClrImportant));
     }
     catch (Exception wbih) {
        wbih.printStackTrace ();
     }
  }

/* intToWord converts an int to a word, where the return
 * value is stored in a 2-byte [least, most] array.  */

  private byte [] intToWord (int parValue) {

     byte retValue [] = new byte [2];

     retValue [0] = (byte) (parValue & 0x00FF);
     retValue [1] = (byte) ((parValue >> 8) & 0x00FF);

     return (retValue);
  }

/* intToDWord converts an int to a double word, where the return
 * value is stored in a 4-byte [least ... most] array.  */

  private byte [] intToDWord (int parValue) {

     byte retValue [] = new byte [4];

     retValue [0] = (byte) (parValue & 0x00FF);
     retValue [1] = (byte) ((parValue >> 8) & 0x000000FF);
     retValue [2] = (byte) ((parValue >> 16) & 0x000000FF);
     retValue [3] = (byte) ((parValue >> 24) & 0x000000FF);

     return (retValue);
  }

}
/* <IMG SRC="/cgi-bin/counter">*/
/*
</pre> </HTML>
*/