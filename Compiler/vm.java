package Compiler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;
public class vm {
    static int pc = 0;
    static int ir = 0;
    static boolean execute = true;
    static int memory[] = {};
    static float freg[] = new float[5];
    static int memoryLength = 0;
    public static void main(String[] args) 
    {
        File file = new File(args[0]);
        try {
            boot(file); // sets memory length too
        } catch (FileNotFoundException e) {
            execute = false;
        }
        boolean flag = false;
        //booted instructs from file
        while(execute)
        {
            ir = memory[pc++];
            int opcode = ((ir & 0xF0000000) >> 28) & 0x0F; // it was adding ones when I was bitshifting cool then &0x0F get rids of ones
            int r = ((ir & 0x00F00000) >> 20) & 0x0F;
            int cmp = ((ir & 0x07000000) >> 24) & 0x07;
            int a = (ir & 0xFFFFF);
            switch(opcode)
            {
                case 0: // clr
                    freg[r] = 0;
                    break;
                case 1: // add
                    freg[r] = freg[r] + Float.intBitsToFloat(memory[a]);
                    break;
                case 2: // subtract
                    freg[r] = freg[r] - Float.intBitsToFloat(memory[a]);
                    break;  
                case 3: // mult
                    freg[r] = freg[r] * Float.intBitsToFloat(memory[a]);
                    break;
                case 4: // div
                    freg[r] = freg[r] / Float.intBitsToFloat(memory[a]);
                    break;
                case 5: // Jump
                    if(flag)
                    {
                        flag = false;
                        pc = a;
                    }
                    break;
                case 6: // compare
                    flag = compare(cmp,r,a);
                    break;
                case 7: // load
                    freg[r] = Float.intBitsToFloat(memory[a]);
                    break;
                case 8: // Store
                    memory[a] =  Float.floatToIntBits(freg[r]);
                    break;
                case 9: // halt
                    System.out.println("halting");
                    execute = false;
                    break;
            }
        }
        writeMemoryResults();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter variable location to view its value, enter x to quit");
        boolean read = true;
        while(read)
        {
            String location = scanner.nextLine().strip();
            try {
                GetLocation(Integer.parseInt(location));
            }
            catch(NumberFormatException e) 
            {
                if(location.equals("x"))
                {
                    read = false;
                }
            }
            
        }
    }
    private static void GetLocation(int location)
    {
        File file = new File("output.bin");
        if(!file.exists()) return;
        try{
        FileInputStream is = new FileInputStream(file);
        DataInputStream di = new DataInputStream(is);
        var bytes = di.readAllBytes();
        if(location < 0 || location >= bytes.length)
        {
            di.close();
            return;
        }
        int mem = 0;
        mem |= bytes[location*4] << 24;
        mem |= (bytes[location*4+1] << 16) & 0xFF0000;
        mem |= (bytes[location*4+2] << 8) & 0xFF00;
        mem |= (bytes[location*4+3]) & 0xFF;
        System.out.println(Float.intBitsToFloat(mem));
               
        di.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }
    private static void writeMemoryResults()
    {
        File file = new File("output.bin");
        if(file.exists()) file.delete();
        try {
            file.createNewFile();
            FileOutputStream os = new FileOutputStream(file);
            DataOutputStream ds = new DataOutputStream(os);
            for(int i = 0; i < memoryLength; i++)
            {
                ds.writeInt(memory[i]);
            }
            ds.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
            memoryLength = bytes.length / 4;
            memory = new int[memoryLength];
            for(int i = 0; i < bytes.length; i = i + 4)
            {
                int mem = 0;
                mem |= bytes[i] << 24;
                mem |= (bytes[i+1] << 16) & 0xFF0000;
                mem |= (bytes[i+2] << 8) & 0xFF00;
                mem |= (bytes[i+3]) & 0xFF;
                memory[length++] = mem;
            }
            di.close();
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
            return freg[r] ==  Float.intBitsToFloat(memory[a]);
            case 2: // less
            return freg[r] < Float.intBitsToFloat(memory[a]);
            case 3: // greater
            return freg[r] > Float.intBitsToFloat(memory[a]);
            case 4: // less or equal
            return freg[r] <= Float.intBitsToFloat(memory[a]);
            case 5: //greater or equal
            return freg[r] >= Float.intBitsToFloat(memory[a]);
            case 6: // not equal
            return freg[r] != Float.intBitsToFloat(memory[a]);
        }
        execute = false;
        System.out.println("Compare not in range  0-6");
        return false;
    }
}
