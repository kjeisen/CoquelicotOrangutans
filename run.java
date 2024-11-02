
import Compiler.Parser;
import Compiler.Scanner;
import Compiler.Structures.*;

public class run {
    
    public static void main(String[] args) {
        Parser.parse(Scanner.getTokens("small_test.c"));
    }
}
