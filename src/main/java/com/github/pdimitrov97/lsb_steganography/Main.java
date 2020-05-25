package com.github.pdimitrov97.lsb_steganography;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Main
{
	public static void main(String[] args)
	{
		String input;
		String[] tokens;
		Scanner scanner = new Scanner(System.in);
		System.out.println("Welcome to Pavel Dimitrov's LSB Steganography project!");
		System.out.println("\nSupported commands:");
		System.out.println("encode <file-used-for-encoding> <file-to-be-encoded> <output-file>");
		System.out.println("decode <encoded-file> <output-file-name>");
		System.out.println("exit - to exit the program.\n");

		while (true)
		{
			System.out.println("Enter command:");
			input = scanner.nextLine();
			tokens = input.split(" ");

			switch (tokens[0])
			{
				case "encode":
				{
					if (tokens.length != 4)
					{
						System.out.println("Invalid number of arguments!\n");
						continue;
					}

					File target = new File(tokens[1]);
					File payload = new File(tokens[2]);
					File result = new File(tokens[3]);

					try
					{
						System.out.println("Encoding...");
						LSB.encodeFile(target, payload, result);
					}
					catch (IOException e)
					{
						System.out.println("Something went wrong with the files!\n");
					}
					catch (FileTooSmallException e)
					{
						System.out.println("Encoding failed!");
						System.out.println("Target file too small to fit payload!\n");
					}

					break;
				}
				case "decode":
				{
					if (tokens.length != 3)
					{
						System.out.println("Invalid number of arguments!\n");
						continue;
					}

					File target = new File(tokens[1]);
					String result = tokens[2];

					try
					{
						System.out.println("Decoding...");
						LSB.decodeFile(target, result);
					}
					catch (IOException e)
					{
						System.out.println("Something went wrong with the files!\n");
					}
					break;
				}
				case "exit":
				{
					System.exit(0);
				}
				default:
				{
					System.out.println("Invalid command! Supported commands:");
					System.out.println("encode <file-used-for-encoding> <file-to-be-encoded> <output-file>");
					System.out.println("decode <encoded-file> <output-file-name>");
					System.out.println("exit - to exit the program.\n");
				}
			}
		}
	}
}