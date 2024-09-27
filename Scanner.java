import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Authors: Blake Wagner, Andrew Sarver, George Harmon, Kolby Eisenhauer
 * Reviewers: Meagan Geer, Levi Frashure
 */

class Scanner {
    private static List<List<State>> array = new ArrayList<List<State>>(); 
    private static Map<Character, Integer> characterToIndex = new HashMap<>();
    private static State current_state = State.START;
    private static final int[] final_states = {3, 7, 9, 11, 15, 20, 21, 22, 23, 24, 25, 26, 27, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46};
    private static final int[] part_states = {1, 2, 4, 5, 6, 8, 10, 12, 13, 14, 16, 17, 18, 19};
    private static final int[] part_keyword_states = {8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20};
    private static final char[] invalid_c_values = {'=', ';', '+', '-', '*', '/', '(', ')', '{', '}', '<', '>', '!', ' ', '\t', '\n', '\f', '\r'};
    public static ArrayList<Pair> tokens = new ArrayList<>(); // List of all tokens + values

    // Pair class to store the state and the value of the token
    public static class Pair {
        State state;
        String value;
        Pair(State state, String value) {
            this.state = state;
            this.value = value;
        }

        public String toString() {
            String stateStr = state.toString();
            if (value.length() > 0) stateStr = stateStr + " : " + value;
            return stateStr;
        }
    }
    public static void main(String[] args) {
        make_map();
        make_array();
        scan_input_file("test_input.c");
    }

    // Check if the state is a final state
    public static boolean isFinal() {
        for (int i = 0; i < final_states.length; i++) {
            if (current_state.index == final_states[i]) {
                return true;
            }
        }
        return false;
    }

    // Check if the state is a part of a bigger token (I, W, WHI, ...)
    public static boolean isPart(State next_state) {
        for (int i = 0; i < part_states.length; i++) {
            if (next_state.index == part_states[i]) {
                return true;
            }
        }
        return false;
    }

    // Check if the state COULD apart two piece token
    public static boolean isTwoPiece(State state) {
        if (state == State.UNEQUAL || state == State.GREATEROREQUAL || 
            state == State.LESSOREQUAL || state == State.ADDITIONASSIGNMENT || 
            state == State.SUBTRACTIONASSIGNMENT || state == State.MULTIPLYASSIGNMENT || state == State.DIVIDEASSIGNMENT ||
            state == State.EQUAL || state == State.INCREMENT ||  state == State.DECREMENT) {
            return true;
        }
        return false;
    }

    // Check if the state needs a value
    public static boolean needsValue(State state) {
        if (state == State.VARIABLE || state == State.INT_VALUE || state == State.FLOAT_VALUE) {
            return true;
        }
        return false;
    }

    // Add the a token to the list of tokens
    public static String addFinal(String value) {
        if (isFinal()) {
            tokens.add(new Pair(current_state, needsValue(current_state) ? value : ""));
            System.out.println(new Pair(current_state, needsValue(current_state) ? value : ""));
            if (needsValue(current_state) || (!needsValue(current_state) && value.length() != 0 && isKeyword(current_state))) value = "";

            current_state = State.START;
        }
        return value;
    }

    // Check if the state is a part of a keyword
    public static boolean isKeywordPart(State state) {
        for (int i = 0; i < part_keyword_states.length; i++) {
            if (state.index == part_keyword_states[i]) {
                return true;
            }
        }
        return false;
    }

    // Check if the state is a keyword
    public static boolean isKeyword(State state) {
        if (state == State.FOR_KEYWORD || state == State.FLOAT_KEYWORD || 
            state == State.IF_KEYWORD || state == State.INT_KEYWORD || 
            state == State.ELSE_KEYWORD || state == State.WHILE_KEYWORD) {
            return true;
        }
        return false;
    }

    // Check if the character is valid for states that need a value
    public static boolean isValidC(char c) {
        for (int i = 0; i < invalid_c_values.length; i++) {
            if (c == invalid_c_values[i]) {
                return false;
            }
        }
        return true;
    }

    // Checks if the current token is unfinished
    public static boolean isUnfinished(State original, State state) {
        if (isKeywordPart(original) && !isKeywordPart(state) && !isFinal()) current_state = State.VARIABLE;
        if (state == State.FLOAT_VALUE && original == State.INT_VALUE) return true;
        else if(isPart(original) && state == State.VARIABLE) return true;
        else if ((state == original || isPart(state)) && !isTwoPiece(original) && !isKeywordPart(state)) return true;
        else if (isTwoPiece(state)) return true;
        else if (isKeywordPart(original) && (state == State.VARIABLE)) return true; // I dont think this is ever used, but im keeping it here just in case

        return false;
    }

    // Scanning the input file
    public static ArrayList<Pair> scan_input_file(String input) {
        BufferedReader br = null;
        String line = "";
        String value = "";

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

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }            
        
        return tokens;
    }

    // Making the transition table
    public static void make_array() {
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
    public static void make_map() {
        String tokens = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.-+/*(){}<>=!_; \t\n\f\r";
        for (int i = 0; i < tokens.length(); i++) {
            if (i >= 77) {
                characterToIndex.put(tokens.charAt(i), 77);
                continue;
            }

            characterToIndex.put(tokens.charAt(i), i);
        }
    }

    // Enums for all the states with and index associated
    public enum State {
        START(0),
        F(1),
        FO(2),
        FOR_KEYWORD(3),
        FL(4),
        FLO(5),
        FLOA(6),
        FLOAT_KEYWORD(7),
        I(8),
        IF_KEYWORD(9),
        IN(10),
        INT_KEYWORD(11),
        E(12),
        EL(13),
        ELS(14),
        ELSE_KEYWORD(15),
        W(16),
        WH(17),
        WHI(18),
        WHIL(19),
        WHILE_KEYWORD(20),
        VARIABLE(21),
        INT_VALUE(22),
        FLOAT_VALUE(23),
        OPENBRACKET(24),
        CLOSEDBRACKET(25),
        OPENPARENTHESIS(26),
        CLOSEDPARENTHESIS(27),
        EXCLAIM(28),
        UNEQUAL(29),
        GREATER(30),
        GREATEROREQUAL(31),
        LESS(32),
        LESSOREQUAL(33),
        ASSIGN(34),
        EQUAL(35),
        ADDITION(36),
        INCREMENT(37),
        ADDITIONASSIGNMENT(38),
        SUBTRACT(39),
        DECREMENT(40),
        SUBTRACTIONASSIGNMENT(41),
        MULTIPLY(42),
        MULTIPLYASSIGNMENT(43),
        DIVIDE(44),
        DIVIDEASSIGNMENT(45),
        SEMICOLON(46),
        NULL(47);
        public final int index;
        State(int index) {
            this.index = index;
        }
    }
}