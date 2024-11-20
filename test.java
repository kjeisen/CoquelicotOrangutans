import java.util.ArrayList;

import Compiler.Parser;
import Compiler.Scanner;
import Compiler.Structures.*;

public class test {
    public static void main(String[] args) {
		var tokens = Scanner.ScanInputFileForTokens("test_input.c");

		ArrayList<String> atoms = Parser.parse(tokens);
        ArrayList<Atom> atomList = new ArrayList<>();
        for (String atomStr : atoms)
        {
            atomList.add(new Atom(atomStr));
        }

        Instructions inst = new Instructions(atomList);
        System.out.println(inst.toString());
        System.out.println(inst.varMap.toString());
    }
}
