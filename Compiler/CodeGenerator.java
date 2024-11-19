package Compiler;

import java.util.ArrayList;
import Compiler.Structures.Atom;

public class CodeGenerator {
    public static void generate(ArrayList<String> atoms) {
        System.out.println("Generating code...");

        // Create list for atom data structures and initialize using output from Parser
        ArrayList<Atom> atomList = new ArrayList<>();
        
        for (String atomStr : atoms)
        {
            atomList.add(new Atom(atomStr));
        }
    }
}
