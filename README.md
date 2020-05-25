# LSB Steganography Encoder and Decoder
This is a Java program that encodes files into 24-bit BMP images using the LSB (Least Significant Bit) algorithm.
Furthermore, the program can decode hidden files from the same image format.

## Description
The program can encode any file format into 24-bit BMP image.
The only constraint is that the image to be used for hiding must be at least 8 times bigger in size than the file to be hidden.

## How to use it
The program supports the following commands:
- <b>encode &lt;file-used-for-encoding&gt; &lt;file-to-be-encoded&gt; &lt;output-file&gt;</b>
  - <b>&lt;file-used-for-encoding&gt;</b> is the name of the file used for cover image including its extension (e.g. picture.bmp).
  - <b>&lt;file-to-be-encoded&gt;</b> is the name of the file to be hidden including its extension (e.g. secret.txt).
  - <b>&lt;output-file&gt;</b> is the name of the output file including its extension (e.g. output.bmp).
- <b>decode &lt;encoded-file&gt; &lt;output-file-name&gt;</b>
  - <b>&lt;encoded-file&gt;</b> is the name of the file to be decoded including its extension (e.g. output.bmp).
  - <b>&lt;output-file-name&gt;</b> is the name of the file containing the extracted information excluding extension (e.g. secret).
- <b>exit</b> - to exit the program.

## Requirements:
- JDK 8
- Maven

## Build:
````
mvn clean install
````
