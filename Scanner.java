import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

class Scanner {
    public static List<List<State>> array = new ArrayList<List<State>>(); 
    public static Map<Character, Integer> characterToIndex = new HashMap<>();
    public static State current_state = State.START;
    public static int[] final_states = {3, 7, 9, 11, 15, 20, 22, 23, 24, 25, 26, 27, 29, 30, 31, 32, 33, 34, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46};
    public static int[] part_states = {1, 2, 4, 5, 6, 8, 10, 12, 13, 14, 16, 17, 18, 19};
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

    public static boolean isFinal() {
        for (int i = 0; i < final_states.length; i++) {
            if (current_state.index == final_states[i]) {
                return true;
            }
        }
        return false;
    }

    public static boolean isFinalVariable(State next_state) {
        if (current_state == State.VARIABLE && next_state != State.VARIABLE) return true;
        return false;
    }

    public static boolean isPart(State next_state) {
        for (int i = 0; i < part_states.length; i++) {
            if (next_state.index == part_states[i]) {
                return true;
            }
        }
        return false;
    }

    public static boolean isTwoPiece(State state) {
        if (state == State.UNEQUAL || state == State.GREATEROREQUAL || 
            state == State.LESSOREQUAL || state == State.ADDITIONASSIGNMENT || 
            state == State.SUBTRACTIONASSIGNMENT || state == State.MULTIPLYASSIGNMENT || state == State.DIVIDEASSIGNMENT ||
            state == State.EQUAL || state == State.INCREMENT ||  state == State.DECREMENT) {
            return true;
        }
        return false;
    }

    public static void scan_input_file(String input) {
        BufferedReader br = null;
        String line = "";
        String value = "";
        ArrayList<Pair> tokens = new ArrayList<>();
        try {
            br = new BufferedReader(new FileReader(input));
            while ((line = br.readLine()) != null) {
                for (int i = 0; i < line.length(); i++) {
                    char c = line.charAt(i);
                    int index = -1;

                    try {
                        index = characterToIndex.get(c);
                    } catch (Exception e) {
                        System.out.println("Invalid character: '" + c + "'");
                        System.exit(1);
                    }

                    State next_state = array.get(current_state.index).get(index);

                    if (isPart(next_state) || next_state == State.INT_VALUE || next_state == State.FLOAT_VALUE || 
                        next_state == State.VARIABLE || c == '.') {
                        value = value + c;
                    }

                    if ((isFinal() || isFinalVariable(next_state)) && !isTwoPiece(next_state) && c != '.') {
                        if (current_state != State.VARIABLE && current_state != State.INT_VALUE && current_state != State.FLOAT_VALUE) {
                            value = "";
                        }
                        tokens.add(new Pair(current_state, value));
                        System.out.println(new Pair(current_state, value));
                        value = "";

                    }

                    current_state = next_state;
                }
            }

            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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