
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
		int backendidx = argFind(args, "-b");
		int frontendidx = argFind(args, "-f");


		if (frontendidx >= 0) {
			String filename = "test_input.c";
			if (args.length > frontendidx + 1 && frontendidx + 1 != backendidx) filename = args[frontendidx + 1];

			var tokens = Scanner.ScanInputFileForTokens(filename);
			var atoms = Parser.parse(tokens);

			printAtoms(atoms);
		}

		if (backendidx >= 0) {
			System.out.println(backendidx);
			String filename = "atoms.txt";
			if (args.length > backendidx + 1 && backendidx + 1 != frontendidx) filename = args[backendidx + 1];
			var atoms = readAtoms(filename);
			CodeGenerator.generate(atoms);
		}
	}

	public static int argFind(String[] args, String arg) {
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals(arg)) {
				return i;
			}
		}
		return -1;
	}
	
	public static void printAtoms(ArrayList<String> atoms) {
		printAtoms(atoms, "atoms.txt");
	}

	public static void printAtoms(ArrayList<String> atoms, String filename) {
		try {
			File output = new File(filename);
			BufferedWriter writer = new BufferedWriter(new FileWriter(output));
			for(var atom : atoms) {
				writer.write(atom + "\n");
			}
			writer.close();
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
			reader.close();
		} catch (Exception e) {
			System.out.println("Error reading atoms from file");
		}

		return atoms;
	} 
}
