import java.util.ArrayList;

import Compiler.Parser;
import Compiler.Scanner;

public class test {
    public static void main(String[] args) {
		var tokens = Scanner.ScanInputFileForTokens("test_input.c");

		ArrayList<String> atoms = Parser.parse(tokens);
      
    }
}
