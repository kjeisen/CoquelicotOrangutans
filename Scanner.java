import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

class Scanner {
    public static void main(String[] args) {
        // get int value of char
        make_map();
        make_array();
        System.out.println( array.get(current_state.index).get(characterToIndex.get('f')));
        current_state = array.get(current_state.index).get(characterToIndex.get('a'));
        System.out.println(current_state);
    }
    public static List<List<State>> array = new ArrayList<List<State>>(); 

    public static Map<Character, Integer> characterToIndex = new HashMap<>();
    public static State current_state = State.START;

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
        characterToIndex.put('a',0);
        characterToIndex.put('b',1);
        characterToIndex.put('c',2);
        characterToIndex.put('d',3);
        characterToIndex.put('e',4);
        characterToIndex.put('f',5);
        characterToIndex.put('g',6);
        characterToIndex.put('h',7);
        characterToIndex.put('i',8);
        characterToIndex.put('j',9);
        characterToIndex.put('k',10);
        characterToIndex.put('l',11);
        characterToIndex.put('m',12);
        characterToIndex.put('n',13);
        characterToIndex.put('o',14);
        characterToIndex.put('p',15);
        characterToIndex.put('q',16);
        characterToIndex.put('r',17);
        characterToIndex.put('s',18);
        characterToIndex.put('t',19);
        characterToIndex.put('u',20);
        characterToIndex.put('v',21);
        characterToIndex.put('w',22);
        characterToIndex.put('x',23);
        characterToIndex.put('y',24);
        characterToIndex.put('z',25);
        characterToIndex.put('A',26);
        characterToIndex.put('B',27);
        characterToIndex.put('C',28);
        characterToIndex.put('D',29);
        characterToIndex.put('E',30);
        characterToIndex.put('F',31);
        characterToIndex.put('G',32);
        characterToIndex.put('H',33);
        characterToIndex.put('I',34);
        characterToIndex.put('J',35);
        characterToIndex.put('K',36);
        characterToIndex.put('L',37);
        characterToIndex.put('M',38);
        characterToIndex.put('N',39);
        characterToIndex.put('O',40);
        characterToIndex.put('P',41);
        characterToIndex.put('Q',42);
        characterToIndex.put('R',43);
        characterToIndex.put('S',44);
        characterToIndex.put('T',45);
        characterToIndex.put('U',46);
        characterToIndex.put('V',47);
        characterToIndex.put('W',48);
        characterToIndex.put('X',49);
        characterToIndex.put('Y',50);
        characterToIndex.put('Z',51);
        characterToIndex.put('0',52);
        characterToIndex.put('1',53);
        characterToIndex.put('2',54);
        characterToIndex.put('3',55);
        characterToIndex.put('4',56);
        characterToIndex.put('5',57);
        characterToIndex.put('6',58);
        characterToIndex.put('7',59);
        characterToIndex.put('8',60);
        characterToIndex.put('9',61);
        characterToIndex.put('.',62);
        characterToIndex.put('-',63);
        characterToIndex.put('+',64);
        characterToIndex.put('/',65);
        characterToIndex.put('*',66);
        characterToIndex.put('(',67);
        characterToIndex.put(')',68);
        characterToIndex.put('{',69);
        characterToIndex.put('}',70);
        characterToIndex.put('<',71);
        characterToIndex.put('>',72);
        characterToIndex.put('=',73);
        characterToIndex.put('!',74);
        characterToIndex.put('_',75);
    // All whitespace characters go to same place
        characterToIndex.put(' ',76);
        characterToIndex.put('\t',76);
        characterToIndex.put('\n',76);
        characterToIndex.put('\f',76);
        characterToIndex.put('\r',76);
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
        State(int index)
        {
            this.index = index;
        }
    }
}