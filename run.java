
import Compiler.Parser;
import Compiler.Scanner;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

import Compiler.CodeGenerator;

public class run {
    
    public static void main(String[] args) {

		if (args[0].equals("-f")) {
			String filename = "small_test.c";
			if (args[1].isEmpty()) filename = args[1];

			var tokens = Scanner.ScanInputFileForTokens(filename);
			var atoms = Parser.parse(tokens);

			printAtoms(atoms);
		}

		if (args[0].equals("-b")) {
			String filename = "atoms.txt";
			if (args[1].isEmpty()) filename = args[1];
			var atoms = readAtoms(filename);
			CodeGenerator.generate(atoms);
		}
	}
	
	public static void printAtoms(ArrayList<String> atoms) {
		printAtoms(atoms, "atoms.txt");
	}

	public static void printAtoms(ArrayList<String> atoms, String filename) {
		try {
			File output = new File(filename);
			BufferedWriter writer = new BufferedWriter(new FileWriter(output));
			for(var atom : atoms) {
				writer.write(atom);
			}
		} catch (Exception e) {
			System.out.println("Error writing atoms to file");
		}
	}

	public static ArrayList<String> readAtoms(String file) {
		ArrayList<String> atoms = new ArrayList<String>();

		try {
			File input = new File(file);
			BufferedReader reader = new BufferedReader(new FileReader(input));
			while (reader.ready()) {
				atoms.add(reader.readLine());
			}
		} catch (Exception e) {
			System.out.println("Error reading atoms from file");
		}

		return atoms;
	} 
}
