package Compiler;

import java.util.ArrayList;
import java.util.HashMap;
import Compiler.Structures.OpCode;

import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
public class CodeGenerator {
    /**
     * If you want to read the memory or instruction binary files you need to have a
     * hex editor and make sure it's reading in big endian
     * The memory is added to the instructions after the Halt instruction which is 90 00 00 00;
     * The constants will be the float32 values,
     * Any 00 00 00 00 in memory are variables that have been allocated and will have their address used in the instructions
     */
    private static String memoryFilePath = "memory.bin";
    private static String instructionFilePath = "instructions.bin";
    private static HashMap<String, Integer> labelTable = new HashMap<>();
    private static final int INSTRUCTION_SIZE = 4;
    private static int startOfMemoryOffset = 0;
    private static int variableCount = 0;

    public static File generate(ArrayList<String> atoms) {
        System.out.println("Generating code...");
        // reset files
        File instructionFile = new File(instructionFilePath);
        File memoryFile = new File(memoryFilePath);
        memoryFile.delete();
        instructionFile.delete();

        // First pass make the table and memory locations for constants and variables
        GenerateLabelTable(atoms);

        // At this point the memory has been created and will be appended after the instructions are generated
        // Second pass generate the code from atoms and fill in memory locations where needed
        GenerateMachineCode(atoms);
        printOutInstructionToFilePretty();

        // Add memory to the end of the instructions
        writeMemoryToInstructionFile();
        System.out.println("Memory starts at: 0x" + Integer.toHexString(startOfMemoryOffset * INSTRUCTION_SIZE).toUpperCase());
        return new File(instructionFilePath);
        
    }
    private static void GenerateMachineCode(ArrayList<String> atoms)
    {
        for(var atom : atoms)
        {
            String[] tempParts = atom.replace("(", "").replace(")", "").split(",");
            String[] parts = new String[tempParts.length];
            for(int i = 0; i < tempParts.length; i++)
            {
                parts[i] = tempParts[i].trim();
            }
            String instruction = parts[0];
            switch(instruction)
            {
               
                // we dont do anything for labels
                case "LBL":
                    break;
                case "ADD":
                    int LHS = labelTable.get(parts[1]);
                    int RHS = labelTable.get(parts[2]);
                    int result = labelTable.get(parts[3]);
                    generateInstruction(OpCode.LOD, 1, (LHS + startOfMemoryOffset) * INSTRUCTION_SIZE, 0);
                    generateInstruction(OpCode.ADD, 1, (RHS + startOfMemoryOffset) * INSTRUCTION_SIZE, 0);
                    generateInstruction(OpCode.STO, 1, (result + startOfMemoryOffset) * INSTRUCTION_SIZE, 0);
                    break;
                case "SUB":
                    LHS = labelTable.get(parts[1]);
                    RHS = labelTable.get(parts[2]);
                    result = labelTable.get(parts[3]);
                    generateInstruction(OpCode.LOD, 1, (LHS + startOfMemoryOffset) * INSTRUCTION_SIZE, 0);
                    generateInstruction(OpCode.SUB, 1, (RHS + startOfMemoryOffset) * INSTRUCTION_SIZE, 0);
                    generateInstruction(OpCode.STO, 1, (result + startOfMemoryOffset) * INSTRUCTION_SIZE, 0);
                    break;
                case "MUL":
                    LHS = labelTable.get(parts[1]);
                    RHS = labelTable.get(parts[2]);
                    result = labelTable.get(parts[3]);
                    generateInstruction(OpCode.LOD, 1, (LHS + startOfMemoryOffset) * INSTRUCTION_SIZE, 0);
                    generateInstruction(OpCode.MUL, 1, (RHS + startOfMemoryOffset) * INSTRUCTION_SIZE, 0);
                    generateInstruction(OpCode.STO, 1, (result + startOfMemoryOffset) * INSTRUCTION_SIZE, 0);
                    break;
                case "DIV":
                    LHS = labelTable.get(parts[1]);
                    RHS = labelTable.get(parts[2]);
                    result = labelTable.get(parts[3]);
                    generateInstruction(OpCode.LOD, 1, (LHS + startOfMemoryOffset) * INSTRUCTION_SIZE, 0);
                    generateInstruction(OpCode.DIV, 1, (RHS + startOfMemoryOffset) * INSTRUCTION_SIZE, 0);
                    generateInstruction(OpCode.STO, 1, (result + startOfMemoryOffset) * INSTRUCTION_SIZE, 0);
                    break;
                case "TST":
                    LHS = labelTable.get(parts[1]);
                    RHS = labelTable.get(parts[2]);
                    int CMP = Integer.parseInt(parts[4]);
                    int destination = labelTable.get(parts[5]);
                    generateInstruction(OpCode.LOD, 1, (LHS + startOfMemoryOffset) * INSTRUCTION_SIZE, 0);
                    generateInstruction(OpCode.CMP, 1, (RHS + startOfMemoryOffset) * INSTRUCTION_SIZE, CMP);
                    generateInstruction(OpCode.JMP, 1, (destination * INSTRUCTION_SIZE), 0);
                    break;
                case "JMP":
                    destination = labelTable.get(parts[5]);
                    generateInstruction(OpCode.CMP, 1, 0, 0);
                    generateInstruction(OpCode.JMP, 1, (destination * INSTRUCTION_SIZE), 0);
                    break;
                case "MOV":
                    LHS = labelTable.get(parts[1]);
                    destination = labelTable.get(parts[3]);
                    generateInstruction(OpCode.LOD, 1, (LHS + startOfMemoryOffset) * INSTRUCTION_SIZE, 0);
                    generateInstruction(OpCode.STO, 1, (destination + startOfMemoryOffset) * INSTRUCTION_SIZE, 0);
            }
        }
        // after all the instructions we halt
        generateInstruction(OpCode.HLT, 0, 0, 0);
    }
    
    private static void generateInstruction(OpCode op, int register, int memory, int CMP)
    {
       int instruction = 0;
       instruction |= (op.value & 0xF) << 28;
       instruction |= (CMP & 0x7) << 24;
       instruction |= (register & 0xF) << 20;
       instruction |= (memory & 0xFFFFF);
       writeInstructionToFile(instruction);
    }
    private static void GenerateLabelTable(ArrayList<String> atoms)
    {    
        int pc = 0;
        for(String atom : atoms)
        {
            String[] parts = atom.replace("(", "").replace(")", "").split(",");
            String instruction = parts[0];
            if(instruction.equals("LBL"))
            {
                labelTable.put(parts[5].trim(), pc);
            } 
            else if(instruction.equals("MOV") || instruction.equals("JMP"))
            {
                // MOV and JMP 2 instructs
                pc += 2;
                AddVariablesAndConstantsToTable(atom);

            } 
            else 
            {
                // MUL, ADD, DIV, SUB, TST all 3 instructs
                pc += 3;
                AddVariablesAndConstantsToTable(atom);
            }
        }
        // HLT CPU after end of code 1 instruct
        pc++;
        // memory starts at end of code
        startOfMemoryOffset = pc;
    }
    private static void writeInstructionToFile(int value)
    {
        File file = new File(instructionFilePath);
        try {
            FileOutputStream fOut = new FileOutputStream(file, true);
            DataOutputStream dOut = new DataOutputStream(fOut);
            dOut.writeInt(value);
            dOut.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static void writeFloatToFile(float value)
    {
        File file = new File(memoryFilePath);
        try {
            FileOutputStream fOut = new FileOutputStream(file, true);
            DataOutputStream  dOut = new DataOutputStream(fOut);
            dOut.writeFloat(value);
            dOut.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    private static void printOutInstructionToFilePretty()
    {
        File instructFile = new File(instructionFilePath);
        try {
            FileInputStream FIn = new FileInputStream(instructFile);
            DataInputStream DIn = new DataInputStream(FIn);
            var data = DIn.readAllBytes();
            for(int i = 0; i < data.length; i+=4)
            {
                StringBuilder sb = new StringBuilder();
                sb.append("Op: ");
                sb.append((Integer.toHexString((data[i] & 0xF0) >> 4)));
                sb.append(", Compare: ");
                sb.append((Integer.toHexString(data[i] & 0x0F)));
                sb.append(", Register: ");
                sb.append(Integer.toHexString((data[i+1] & 0xF0) >> 4));
                sb.append(", Memory Address: 0x");
                sb.append(String.format("%01X",data[i+1] & 0x0F));
                sb.append(String.format("%02X",data[i+2] & 0xFF));
                sb.append(String.format("%02X", data[i+3] & 0xFF));
                sb.append("\n");
                System.out.println(sb.toString());
            
            }
            DIn.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    private static void writeMemoryToInstructionFile()
    {
        File memFile = new File(memoryFilePath);
        File instructFile = new File(instructionFilePath);
        try {
            FileOutputStream FOut = new FileOutputStream(instructFile, true);
            DataOutputStream DOut = new DataOutputStream(FOut);
            FileInputStream  FIn = new FileInputStream(memFile);
            DataInputStream DIn = new DataInputStream(FIn);
            for(var _byte : DIn.readAllBytes())
            {
                DOut.writeByte(_byte);
            }
            DOut.close();
            DIn.close();
            
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    private static void AddVariablesAndConstantsToTable(String atom)
    {
        String[] cleanAtomParts = atom.replace("(", "").replace(")", "").split(",");
        for(int i = 1; i < cleanAtomParts.length; i++)
        {
            // clean string
            String part = cleanAtomParts[i];
            part = part.trim();

            // if empty just go next
            if(part.isEmpty())
            {
                continue;
            }
            // not a number, so add to variable map
            if(!Character.isDigit(part.charAt(0)))
            {
                // variable added to map and its index in memory
                if(!labelTable.containsKey(part)) {
                    labelTable.put(part, variableCount++);
                    writeFloatToFile(0);
                }
                
            }
            // its a constant or number
            else 
            {
                if(!labelTable.containsKey(part)) {
                    labelTable.put(part, variableCount++);
                    float value = Float.parseFloat(part);
                    writeFloatToFile(value);
                }
               
            }

            
        }
    }
}
