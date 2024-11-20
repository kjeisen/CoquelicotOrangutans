import Compiler.Structures.*;

public class test {
    public static void main(String[] args) {
        Atom atom = new Atom("(ADD, T1, T4, T5)");
        Instructions inst = new Instructions();
        inst.add(atom);
        System.out.println(inst.toString());
    }
}
