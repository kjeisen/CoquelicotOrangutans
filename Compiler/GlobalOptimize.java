package Compiler;

import java.util.ArrayList;

public class GlobalOptimize 
{
    public static void optimize(ArrayList<String> atoms)
    {
        // For now, just removes unreachable code
        for (int i = 0; i < atoms.size(); i++)
        {
            if (atoms.get(i).substring(1, 4).equals("JMP"))
            {
                i++;
                while (!atoms.get(i).substring(1, 4).equals("LBL") && i < atoms.size())
                {
                    atoms.remove(i);
                    i++;
                }
            }
        }

    }
}
