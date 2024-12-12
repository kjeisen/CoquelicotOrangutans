package Compiler;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Optimizer 
{
    public static void globalOptimize(ArrayList<String> atoms)
    {
        // For now, just removes unreachable code
        for (int i = 0; i < atoms.size(); i++)
        {
            if (atoms.get(i).substring(1, 4).equals("JMP"))
            {
                i++;
                while (!atoms.get(i).substring(1, 4).equals("LBL") && i < atoms.size())
                {
                    System.out.println("Removing: " + atoms.get(i));
                    atoms.remove(i);
                    i++;
                }
            }
        }
    }

    public static void localOptimize(String filename)
    {
        try
        {
            FileInputStream fis = new FileInputStream(filename);
            byte[] instruction = new byte[4];

            ArrayList<byte[]> instructions = new ArrayList<byte[]>();

            while (fis.read(instruction) != -1)
            {
                // Simple algebraic transformation, skips unnecessary math
                if ((instruction[0] == 0x10 && instruction[2] == 0) || (instruction[0] == 0x30 && instruction[2] == 1) || (instruction[0] == 0x20 && instruction[2] == 0)
                 || (instruction[0] == 0x40 && instruction[2] == 1))
                {
                    System.out.println("Removing: " + instruction);
                    continue;
                }
                instructions.add(instruction);
            }
            fis.close();

            FileOutputStream fos = new FileOutputStream("instructions.bin");

            for (byte[] instr : instructions)
            {
                fos.write(instr);
            }

            fos.close();
        }
        catch (FileNotFoundException e)
        {
            System.out.println("File not found: " + filename);
            System.exit(1);
        } 
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
