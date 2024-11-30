
import Compiler.Parser;
import Compiler.Scanner;
import Compiler.CodeGenerator;

public class run {
    
    public static void main(String[] args) {
		var tokens = Scanner.ScanInputFileForTokens("test_input.c");

		var atoms = Parser.parse(tokens);

		var instructionFile = CodeGenerator.generate(atoms);
	}
}
