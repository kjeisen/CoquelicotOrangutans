
import Compiler.Parser;
import Compiler.Scanner;
import Compiler.CodeGenerator;

public class run {
    
    public static void main(String[] args) {
		var tokens = Scanner.ScanInputFileForTokens("small_test.c");

		var atoms = Parser.parse(tokens);

		for(var atom : atoms) {
			System.out.println(atom);
		}

		var instructionFile = CodeGenerator.generate(atoms);
	}
}
