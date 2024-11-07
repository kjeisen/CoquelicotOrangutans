
import Compiler.Parser;
import Compiler.Scanner;
import Compiler.Scanner.Symbol;
import Compiler.Scanner.Token;

public class run {
    
    public static void main(String[] args) {
		var tokens = Scanner.ScanInputFileForTokens("small_test.c");

		Parser.parse(tokens);
	}
}
