package Compiler;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import Compiler.Structures.*;

/**
 * Authors: Blake Wagner, Andrew Sarver, George Harmon, Kolby Eisenhauer
 * Reviewers: Meagan Geer, Levi Frashure
 */

public class Scanner {
    private static List<List<State>> array = new ArrayList<List<State>>(); 
    private static Map<Character, Integer> characterToIndex = new HashMap<>();
    private static State current_state = State.START;
    private static final int[] final_states = {3, 7, 9, 11, 15, 20, 21, 22, 23, 24, 25, 26, 27, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46};
    private static final int[] part_states = {1, 2, 4, 5, 6, 8, 10, 12, 13, 14, 16, 17, 18, 19}; // States that are part of a keyword, without the keyword itself
    private static final int[] part_keyword_states = {4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20}; // States that are part of a keyword, including the keyword itself
    private static final char[] invalid_c_values = {'=', ';', '+', '-', '*', '/', '(', ')', '{', '}', '<', '>', '!', ' ', '\t', '\n', '\f', '\r'}; // Characters that should never show up as a value
    private static final int[] always_final_states = {25, 26, 27, 34, 46};
    private static ArrayList<Pair> tokens = new ArrayList<>(); // List of all tokens + values

    public static void init() {
        System.out.println("Initializing Scanner...");
        if(characterToIndex.isEmpty()) make_map();
        if(array.isEmpty()) make_array();
        System.out.println("Scanner initialized successfully!");
    }

    // Returns tokens, if there are none, generate them using test_input.c
    public static ArrayList<Pair> getTokens() {
        init();
        if(tokens.isEmpty()) scan_input_file("test_input.c");

        return tokens;
    }

    // Returns tokens, if there are none, generate them using custom input
    public static ArrayList<Pair> getTokens(String input) {
        init();
        if(tokens.isEmpty()) scan_input_file(input);

        return tokens;
    }

    // Check if the state is a final state
    private static boolean isFinal() {
        for (int i = 0; i < final_states.length; i++) {
            if (current_state.index == final_states[i]) {
                return true;
            }
        }
        return false;
    }

    // Check if the state is a part of a bigger token (I, W, WHI, ...)
    private static boolean isPart(State next_state) {
        for (int i = 0; i < part_states.length; i++) {
            if (next_state.index == part_states[i]) {
                return true;
            }
        }
        return false;
    }

    // Check that a state cannot be turned into a variable/number keyword
    private static boolean validFinishedState(State state) {
        for (int i = 0; i < always_final_states.length; i++) {
            if (state.index == always_final_states[i]) {
                return true;
            }
        }
        return false;
    }

    // Check if the state COULD be apart two piece token
    private static boolean isTwoPiece(State state) {
        if (state == State.UNEQUAL || state == State.GREATEROREQUAL || 
            state == State.LESSOREQUAL || state == State.ADDITIONASSIGNMENT || 
            state == State.SUBTRACTIONASSIGNMENT || state == State.MULTIPLYASSIGNMENT || state == State.DIVIDEASSIGNMENT ||
            state == State.EQUAL || state == State.INCREMENT ||  state == State.DECREMENT) {
            return true;
        }
        return false;
    }

    // Check if the state needs a value
    private static boolean needsValue(State state) {
        if (state == State.VARIABLE || state == State.INT_VALUE || state == State.FLOAT_VALUE) {
            return true;
        }
        return false;
    }

    // Add the a token to the list of tokens
    private static String addFinal(String value) {
        if (isFinal()) {
            tokens.add(new Pair(current_state, needsValue(current_state) ? value : ""));
            // System.out.println(new Pair(current_state, needsValue(current_state) ? value : ""));
            if (needsValue(current_state) || (!needsValue(current_state) && value.length() != 0 && isKeyword(current_state))) value = "";

            current_state = State.START;
        }
        return value;
    }

    // Check if the state is a part of a keyword
    private static boolean isKeywordPart(State state) {
        for (int i = 0; i < part_keyword_states.length; i++) {
            if (state.index == part_keyword_states[i]) {
                return true;
            }
        }
        return false;
    }

    // Check if the state is a keyword
    private static boolean isKeyword(State state) {
        if (state == State.FOR_KEYWORD || state == State.FLOAT_KEYWORD || 
            state == State.IF_KEYWORD || state == State.INT_KEYWORD || 
            state == State.ELSE_KEYWORD || state == State.WHILE_KEYWORD) {
            return true;
        }
        return false;
    }

    // Check if the character is valid for states that need a value
    private static boolean isValidC(char c) {
        for (int i = 0; i < invalid_c_values.length; i++) {
            if (c == invalid_c_values[i]) {
                return false;
            }
        }
        return true;
    }

    // Checks if the current token is unfinished
    private static boolean isUnfinished(State original, State state) {
        if (isKeywordPart(original) && !isKeywordPart(state) && !isFinal()) current_state = State.VARIABLE;
        
        if (isTwoPiece(state)) return true;
        else if (validFinishedState(original)) return false;
        else if (state == State.FLOAT_VALUE && original == State.INT_VALUE) return true;        
        else if(isPart(original) && state == State.VARIABLE) return true;
        else if ((state == original || isPart(state)) && !isTwoPiece(original) && !isKeywordPart(state)) return true;
        else if (isKeywordPart(original) && (state == State.VARIABLE)) return true; // This is ever used in ours tests, can't remember why I wrote it so I'm leaving it here just in case

        return false;
    }

    // Scanning the input file
    private static ArrayList<Pair> scan_input_file(String input) {
        BufferedReader br = null;
        String line = "";
        String value = "";
        String inputSplit[] = input.split("/");
        System.out.println("Scanning input file \"" + inputSplit[inputSplit.length - 1] + "\"...");

        try {
            br = new BufferedReader(new FileReader(input));

            while ((line = br.readLine()) != null) {
                for (int i = 0; i < line.length(); i++) {
                    char c = line.charAt(i);
                    int index = -1;

                    // Check if the character is in the map
                    try {
                        index = characterToIndex.get(c);
                    } catch (Exception e) {
                        System.out.println("Invalid character: '" + c + "'");
                        System.exit(1);
                    }

                    // Get the next state
                    State next_state = array.get(current_state.index).get(index);
                    if(isValidC(c)) value += c;

                    // If the current state is a final state, add state + token (if any) to the list of tokens
                    if(!isUnfinished(current_state, next_state)) value = addFinal(value);
                    current_state = next_state;
                }
            }
            // End of file, print final token + value and add to list of tokens
            addFinal(value);

            br.close();

            System.out.println("Scanner ran successfully!\nTokens created successfully!");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }            
        
        return tokens;
    }

    // Making the transition table
    private static void make_array() {
        String csvFile = "transition_table.csv";
        BufferedReader br = null;
        String line = "";

        try {
            br = new BufferedReader(new FileReader(csvFile));
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