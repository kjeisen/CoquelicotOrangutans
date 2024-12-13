package Compiler;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import Compiler.Structures.*;

public class Scanner {
    private static List<List<State>> array = new ArrayList<List<State>>(); 
    private static Map<Character, Integer> characterToIndex = new HashMap<>();
    private static State current_state = State.START;
    	
    public static ArrayList<Token> ScanInputFileForTokens(String fileName)
    {
        make_map();
        make_array();
        return scan_input_file(fileName);
    }
    
    // Scanning the input file
    private static ArrayList<Token> scan_input_file(String input) {
        BufferedReader br = null;
        String line = "";
        ArrayList<Token> tokens = new ArrayList<>();
        try {
            br = new BufferedReader(new FileReader(input));

            while ((line = br.readLine()) != null) {
                for (int i = 0; i < line.length(); i++) {
                    
                    int index = -1;
                    char c = line.charAt(i);
                    
                    index = GetIndex(c);

                    State next_state = array.get(current_state.index).get(index);
                   
                    checkErrorState(next_state);
                    
                    // move through whitespace
                    if(next_state == State.START)
                    {
                    	continue;
                    }
                    
                    // set the current state
                    current_state = next_state;
                    
                    // Go until reaching the start state again that always leaves current state at a final state
                    int counter = 1;
                    while(next_state != State.START)
                    {
                    	checkErrorState(next_state);
                    	current_state = next_state;
                    	
                    	// Check to see if counter still in range of line length
                    	if(i+counter < line.length()) {
                    		char temp_c = line.charAt(i+counter);
                    		// Get row index of character
                    		index = GetIndex(temp_c);
                    		next_state = array.get(current_state.index).get(index);
                    		counter++;
                    	} 
                    	// we reached the end of a line assume end no more input for this line
                    	// set next state to start, increment the counter still since it gets decremented after
                    	else
                    	{
                    		next_state = State.START;
                    		counter++;
                    	}
                    	
                    }
                    // Decrement auto-incremented counter
                    counter--;
                    
                    // Get the value of said values (don't forget i, in, f, etc)
                    Object value = null;
                    if(current_state == State.VARIABLE) {
                    	value = line.substring(i, i+counter);
                    }
                    else if(current_state == State.INT_VALUE)
                    {
                    	value = Integer.valueOf(line.substring(i, i+counter));
                    }
                    else if(current_state == State.FLOAT_VALUE)
                    {
                    	value = Float.valueOf(line.substring(i, i+counter));
                    }
                    // Picks up i,f,els, and so on
                    else if(current_state.symbol == Symbol.IDENTIFIER)
                    {
                    	value = line.substring(i, i+counter);
                    }
                    
                    Token token = new Token(current_state.symbol, value);
                    tokens.add(token);                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  
                    current_state = next_state;
                    
                    // Increase index by counter amount and decrement by one since auto of i increment
                    i += counter-1;
                }
            }

        

            br.close();

        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + input);
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
        }            
        
        return tokens;
    }

    private static int GetIndex(char c) {
    	int index = -1;
    	 try {
             index = characterToIndex.get(c);
         } catch (Exception e) {
             System.out.println("Invalid character: '" + c + "'");
             System.exit(1);
         }
    	 return index;
    }
    private static void checkErrorState(State state)
    {
    	  if(state == State.NULL)
          {
          	System.out.println("UNREADABLE TOKEN");
              System.exit(1);
          }
    }
    // Making the transition table
    private static void make_array() {
        String csvFile = "State Transition 2D Array11-3-24.csv";
        BufferedReader br = null;
        String line = "";

        try {
            br = new BufferedReader(new FileReader(csvFile));
            //Read first line
            br.readLine();
            
            while ((line = br.readLine()) != null) {
                String[] states = line.split(",");
                List<State> temp = new ArrayList<State>();

                for (int i = 1; i < states.length; i++) {
                    temp.add(State.valueOf(states[i].toUpperCase()));
                }

                array.add(temp);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Making the map from characters to indexs
    private static void make_map() {
        String tokens = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.-+/*(){}<>=!_; \t\n\f\r";
        for (int i = 0; i < tokens.length(); i++) {
            if (i >= 77) {
                characterToIndex.put(tokens.charAt(i), 77);
                continue;
            }

            characterToIndex.put(tokens.charAt(i), i);
        }
    }
}