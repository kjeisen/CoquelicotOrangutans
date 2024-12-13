package Compiler;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import Compiler.Structures;
import Compiler.Structures.OpCode;

public class vm {
    static int pc = 0;
    static int ir = 0;
    static boolean execute = true;
    static int  memory[] = new int[1000];
    static float freg[] = new float[5];
    public static void main(String[] args) 
    {
        File file = new File("instructions.bin");
        try {
            boot(file);
        } catch (FileNotFoundException e) {
            execute = false;
        }

        //booted instructs from file
        while(execute)
        {
            ir = memory[pc++];
            int opcode = (ir & 0xF0000000) >> 28;
            int r = (ir & 0x00F00000) >> 20;
            int cmp = (ir & 0x07000000) >> 24;
            int a = (ir & 0xFFFFF);
            boolean flag = false;
            switch(opcode)
            {
                case 0: // clr
                freg[r] = 0;
                break;
                case 1: // add
                    freg[r] = freg[r] + (float) memory[a];
                    break;
                case 2: // subtract
                    freg[r] = freg[r] - (float) memory[a];
                    break;  
                case 3: // mult
                    freg[r] = freg[r] * (float) memory[a];
                    break;
                case 4: // div
                    freg[r] = freg[r] / (float) memory[a];
                    break;
                case 5: // Jump
                    if(flag)
                    {
                        pc = a;
                    }
                case 6: // compare
                    flag = compare(cmp,r,a);
                case 7: // load
                    freg[r] = (float) memory[a];
                case 8: // Store
                    memory[a] =  Float.floatToRawIntBits(freg[r]);
                case 9: // halt
                    execute = false;
                    break;
            }
        }

    }

    private static void boot(File file) throws FileNotFoundException
    {
        FileInputStream is = new FileInputStream(file);
        DataInputStream di = new DataInputStream(is);
        byte bytes[];
        try {
            bytes = di.readAllBytes();
            int length = 0;
            for(int i = 0; i < bytes.length; i = i + 4)
            {
                int mem = bytes[i] << 24;
                mem |= bytes[i+1] << 16;
                mem |= bytes[i+2] << 8;
                mem |= bytes[i+3];
                memory[length++] = mem;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    private static boolean compare(int op, int r, int a)
    {
        switch(op)
        {
            case 0: //always
            return true;
            case 1: // equal
            return freg[r] == (float) memory[a];
            case 2: // less
            return freg[r] < (float) memory[a];
            case 3: // greater
            return freg[r] > (float) memory[a];
            case 4: // less or equal
            return freg[r] <= (float) memory[a];
            case 5: //greater or equal
            return freg[r] >= (float) memory[a];
            case 6: // not equal
            return freg[r] != (float) memory[a];
        }
        execute = false;
        System.out.println("Compare not in range  0-6");
        return false;
    }
}
