import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

class Scanner {
    public static List<List<State>> array = new ArrayList<List<State>>(); 
    public static Map<Character, Integer> characterToIndex = new HashMap<>();
    public static State current_state = State.START;
    public static int[] final_states = {3, 7, 9, 11, 15, 20, 21, 22, 23, 24, 25, 26, 27, 29, 30, 31, 32, 33, 34, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45};

    public static void main(String[] args) {
        make_map();
        make_array();
        scan_input_file("test.c");
    }

    public static boolean isFinal(State state) {
        for (int i = 0; i < final_states.length; i++) {
            if (state.index == final_states[i]) {
                return true;
            }
        }
        return false;
    }

    public static void scan_input_file(String input) {
        BufferedReader br = null;
        String line = "";
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
                        return;
                    }
                    State next_state = array.get(current_state.index).get(index);
                    // System.out.println(c + " " + next_state + " " + current_state + " " + isFinal(current_state));

                    if (isFinal(current_state)) {
                        System.out.println(current_state);
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
        NULL(46);
        public final int index;
        State(int index) {
            this.index = index;
        }
    }
}