
import Compiler.Parser;
import Compiler.Scanner;
import Compiler.GlobalOptimize;

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
	private static String validArgs[] = {"-b", "-f", "-o", "-l"};
    
    public static void main(String[] args) {
		int backendidx = argFind(args, "-b");
		int frontendidx = argFind(args, "-f");
		int globaloptidx = argFind(args, "-o");
		int localoptidx = argFind(args, "-l");

		if (frontendidx != -1) {
			String filename = "test_input.c";
			if (frontendidx + 1 < args.length && argFind(validArgs, args[frontendidx + 1]) == -1) filename = args[frontendidx + 1];
			
			var tokens = Scanner.ScanInputFileForTokens(filename);
			var atoms = Parser.parse(tokens);

			if (globaloptidx != -1) GlobalOptimize.optimize(atoms);
			
			printAtoms(atoms);
		}

		if (backendidx != -1) {
			String filename = "atoms.txt";
			if (backendidx + 1 < args.length && argFind(validArgs, args[backendidx + 1]) == -1) filename = args[backendidx + 1];

			var atoms = readAtoms(filename);

			File instr = new File("instructions.bin");
			File memory = new File("memory.bin");

			try {
				if (instr.exists()) instr.createNewFile();
				if (memory.exists()) memory.createNewFile();
			} catch (Exception e) {
				System.out.println("Error creating binary files");
			}

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

	public static void printAtoms(ArrayList<String> atoms, String file) {
		try {
			File output = new File(file);
			BufferedWriter writer = new BufferedWriter(new FileWriter(output));
			for(var atom : atoms) {
				writer.write(atom + "\n");
			}
			writer.close();
		} catch (Exception e) {
			System.out.println("Error writing atoms to file: " + file);
			System.exit(1);
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
			System.out.println("Error reading atoms from file: " + file);
			System.exit(1);
		}

		return atoms;
	} 
}
