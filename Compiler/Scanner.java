package Compiler;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

class Scanner {
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
            e.printStackTrace();
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
   
    public static class Token
    {
    	Symbol symbol;
    	Object value;
    	Token(Symbol s, Object value)
    	{
    		this.symbol = s;
    		this.value = value;
    	}
    	public String toString()
    	{
			 return (this.value == null) ? symbol.toString() : symbol.toString() + ": " + value.toString();
    		
    	}
    }
    public enum Symbol 
    {
    	FOR,
    	ELSE,
    	WHILE,
    	IF,
    	INT,
    	FLOAT,
    	INT_VALUE,
    	FLOAT_VALUE,
    	IDENTIFIER,
    	OPEN_BRACKET,
    	CLOSED_BRACKET,
    	OPEN_PARENTHESIS,
    	CLOSED_PARENTHESIS,
    	NOT_EQUAL,
    	GREATER_THAN,
    	GREATER_THAN_EQUAL,
    	LESS_THAN,
    	LESS_THAN_EQUAL,
    	ASSIGNMENT,
    	EQUAL,
    	ADDITION,
    	INCREMENT,
    	ADDITION_ASSIGNMENT,
    	SUBTRACT,
    	DECREMENT,
    	SUBTRACT_ASSIGNMENT,
    	MULTIPLY,
    	MULTIPLY_ASSIGNMENT,
    	DIVIDE,
    	DIVIDE_ASSIGNMENT,
    	SEMICOLON,
    	END_OF_INPUT
    }
    // Enums for all the states with index associated and optional symbol 
    public enum State {
        START(0),
        F(1,Symbol.IDENTIFIER),
        FO(2,Symbol.IDENTIFIER),
        FOR_KEYWORD(3,Symbol.FOR),
        FL(4,Symbol.IDENTIFIER),
        FLO(5,Symbol.IDENTIFIER),
        FLOA(6,Symbol.IDENTIFIER),
        FLOAT_KEYWORD(7,Symbol.FLOAT),
        I(8,Symbol.IDENTIFIER),
        IF_KEYWORD(9,Symbol.IF),
        IN(10,Symbol.IDENTIFIER),
        INT_KEYWORD(11,Symbol.INT),
        E(12,Symbol.IDENTIFIER),
        EL(13,Symbol.IDENTIFIER),
        ELS(14,Symbol.IDENTIFIER),
        ELSE_KEYWORD(15,Symbol.ELSE),
        W(16,Symbol.IDENTIFIER),
        WH(17,Symbol.IDENTIFIER),
        WHI(18,Symbol.IDENTIFIER),
        WHIL(19,Symbol.IDENTIFIER),
        WHILE_KEYWORD(20,Symbol.WHILE),
        VARIABLE(21, Symbol.IDENTIFIER),
        INT_VALUE(22, Symbol.INT_VALUE),
        FLOAT_VALUE(23, Symbol.FLOAT_VALUE),
        OPENBRACKET(24, Symbol.OPEN_BRACKET),
        CLOSEDBRACKET(25,Symbol.CLOSED_BRACKET),
        OPENPARENTHESIS(26,Symbol.OPEN_PARENTHESIS),
        CLOSEDPARENTHESIS(27,Symbol.CLOSED_PARENTHESIS),
        EXCLAIM(28),
        UNEQUAL(29,Symbol.NOT_EQUAL),
        GREATER(30,Symbol.GREATER_THAN),
        GREATEROREQUAL(31,Symbol.GREATER_THAN_EQUAL),
        LESS(32,Symbol.LESS_THAN),
        LESSOREQUAL(33, Symbol.LESS_THAN_EQUAL),
        ASSIGN(34, Symbol.ASSIGNMENT),
        EQUAL(35, Symbol.EQUAL),
        ADDITION(36, Symbol.ADDITION),
        INCREMENT(37, Symbol.INCREMENT),
        ADDITIONASSIGNMENT(38, Symbol.ADDITION_ASSIGNMENT),
        SUBTRACT(39, Symbol.SUBTRACT),
        DECREMENT(40, Symbol.DECREMENT),
        SUBTRACTIONASSIGNMENT(41, Symbol.SUBTRACT_ASSIGNMENT),
        MULTIPLY(42, Symbol.MULTIPLY),
        MULTIPLYASSIGNMENT(43, Symbol.MULTIPLY_ASSIGNMENT),
        DIVIDE(44, Symbol.DIVIDE),
        DIVIDEASSIGNMENT(45, Symbol.DIVIDE_ASSIGNMENT),
        SEMICOLON(46, Symbol.SEMICOLON),
        NULL(47);
        public final int index;
        public final Symbol symbol;
        State(int index, Symbol s) {
            this.index = index;
            this.symbol = s;
        }
        State(int index) {
            this.index = index;
            this.symbol = null;
        }
    }
}