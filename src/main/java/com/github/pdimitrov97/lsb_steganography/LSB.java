package com.github.pdimitrov97.lsb_steganography;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

class LSB
{
	/**
	 * Encode payload into the target image using the LSB algorithm and write it to
	 * an output file
	 * 
	 * @param target  - file to be used for encoding the payload into
	 * @param payload - file to be encoded
	 * @param output  - resulting file
	 * @throws IOException
	 */
	public static void encodeFile(File target, File payload, File output) throws IOException, FileTooSmallException
	{
		// Read all bytes of payload
		byte[] payloadBytes = Files.readAllBytes(payload.toPath());

		FileOutputStream fileOutputStream = new FileOutputStream(output);
		FileInputStream fileInputStream = new FileInputStream(target);

		// The following if statement check if the target file is large enough to fit
		// the payload using the LSB algorithm
		// 150 is 54 bytes header, 32 bytes size and 64 bytes file extension
		if ((long) payloadBytes.length * 8 > (long) fileInputStream.available() - 150)
			throw new FileTooSmallException();

		// Read and copy to output the target header
		byte[] targetHeader = new byte[54];
		// Read header
		fileInputStream.read(targetHeader);
		// Write header
		fileOutputStream.write(targetHeader);
		// Write size of payload
		encodeBytes(fileOutputStream, fileSizeToByteArray(payloadBytes.length), fileInputStream);
		// Write extension of payload
		encodeBytes(fileOutputStream, fileExtensionToByteArray(getFileExtension(payload)), fileInputStream);
		// Write payload
		encodeBytes(fileOutputStream, payloadBytes, fileInputStream);

		// Write what's left of file
		byte[] buffer = new byte[1024];
		int length;

		while ((length = fileInputStream.read(buffer)) > 0)

			fileOutputStream.write(buffer, 0, length);

		fileOutputStream.close();
		fileInputStream.close();
	}

	/**
	 * Decode the specified target file and write the resulting output file
	 * 
	 * @param target         - file to be decoded
	 * @param outputFileName - resulting file name without extension
	 * @throws IOException
	 */
	public static void decodeFile(File target, String outputFileName) throws IOException
	{
		FileInputStream fileInputStream = new FileInputStream(target);

		// Skip file header bytes
		fileInputStream.skip(54);
		// Get the file size
		byte[] sizeBytes = decodeBytes(fileInputStream, 4);
		// Get the file extension
		byte[] fileExtensionBytes = decodeBytes(fileInputStream, 8);

		// Convert the size byte array to integer
		int size = byteArrayToInt(sizeBytes);
		// Convert the file extension byte array to String
		String fileExtension = new String(fileExtensionBytes, StandardCharsets.UTF_8);

		File output = new File(outputFileName + "." + fileExtension.trim());
		FileOutputStream fileOutputStream = new FileOutputStream(output);

		byte[] outputBytes = decodeBytes(fileInputStream, size);
		fileOutputStream.write(outputBytes);
	}

	/**
	 * Decode a specified number of bytes from a stego image
	 * 
	 * @param fileInputStream - file input stream representing the stego image
	 * @param numberOfBytes   - number of bytes to be read and decoded
	 * @return decoded byte array
	 * @throws IOException
	 */
	private static byte[] decodeBytes(FileInputStream fileInputStream, int numberOfBytes) throws IOException
	{
		byte[] result = new byte[numberOfBytes];
		int readByte;
		int writeByte;

		for (int i = 0; i < numberOfBytes; i++)
		{
			writeByte = 0;

			for (int j = 0; j < 8; j++)
			{
				readByte = fileInputStream.read();
				writeByte = writeByte + (readByte & 0x1);

				if (j < 7)
					writeByte = writeByte << 1;
			}

			result[i] = (byte) writeByte;
		}
		return result;
	}

	/**
	 * Convert byte array to int
	 * 
	 * @param input - byte array for conversion
	 * @return - converted number
	 */
	private static int byteArrayToInt(byte[] input)
	{
		int result = 0;

		for (int i = 0; i < input.length; i++)
		{
			result = result | (0x000000FF & input[i]);

			if (i < input.length - 1)
			{
				result = result << 8;
			}
		}
		return result;
	}

	/**
	 * Convert specified file size from int to byte array
	 * 
	 * @param fileSize - file size as int
	 * @return file size as byte array
	 */
	private static byte[] fileSizeToByteArray(int fileSize)
	{
		byte[] newPayloadSizeBytes = new byte[4];

		for (int i = 0; i < 4; i++)
			newPayloadSizeBytes[i] = (byte) ((fileSize >>> ((3 - i) * 8)) & 0x000000ff);

		return newPayloadSizeBytes;
	}

	/**
	 * Convert specified file extension to an 8-byte array
	 * 
	 * @param fileExtension
	 * @return byte array with the converted file extension
	 */
	private static byte[] fileExtensionToByteArray(String fileExtension)
	{
		byte[] payloadFileExtensionBytes = fileExtension.getBytes(StandardCharsets.UTF_8);
		byte[] newPayloadFileExtensionBytes = new byte[8];

		for (int i = 7; i >= 0; i--)
		{
			if (7 - i <= payloadFileExtensionBytes.length - 1)
				newPayloadFileExtensionBytes[i] = payloadFileExtensionBytes[payloadFileExtensionBytes.length - 1 - (7 - i)];
			else
				break;
		}

		return newPayloadFileExtensionBytes;
	}

	/**
	 * Reads from the specified file input stream and encodes the payload with the
	 * LSB algorithm and writes it in file output stream
	 * 
	 * @param fileOutputStream
	 * @param payload
	 * @param fileInputStream
	 * @throws IOException
	 */
	private static void encodeBytes(FileOutputStream fileOutputStream, byte[] payload, FileInputStream fileInputStream) throws IOException
	{
		for (byte b : payload)
		{
			// Go through every byte
			for (int i = 0; i < 8; i++)
			{
				// Go through every bit
				int bit = b >>> (7 - i) & 0x1; // Get current left most bit
				int originalByte = fileInputStream.read(); // Get current byte from file input stream

				if (bit == 1)
					fileOutputStream.write(originalByte | 0x1); // Change last bit of original byte to 1
				else
					fileOutputStream.write(originalByte & ~0x1); // Change last bit of original byte to 0
			}
		}
	}

	/**
	 * Accepts a file and returns the file extension
	 * 
	 * @param file - specified file
	 * @return file extension excluding '.'
	 */
	private static String getFileExtension(File file)
	{
		String name = file.getName();

		int lastIndexOf = name.lastIndexOf(".");

		if (lastIndexOf == -1)
			return ""; // missing extension

		lastIndexOf++;

		return name.substring(lastIndexOf);
	}
}