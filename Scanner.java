import java.util.*;

class Scanner {
    public static void main(String[] args) {
        make_map();
        make_array();
        System.out.println( array.get(current_state.index).get(characterToIndex.get('f')));
        current_state = array.get(current_state.index).get(characterToIndex.get('a'));
        System.out.println(current_state);
    }
    public static List<List<State>> array = new ArrayList<List<State>>(); 

    public static Map<Character, Integer> characterToIndex = new HashMap<>();;
    public static State current_state = State.start;

    public static void make_array() {
        array.add(new ArrayList<State>());
        array.get(0).addAll(Arrays.asList(State.Variable,State.Variable,
        State.Variable,State.Variable,State.e,State.f,State.Variable,
        State.Variable,State.i,State.Variable,State.Variable,State.Variable,
        State.Variable,State.Variable,State.Variable,State.Variable,State.Variable,
        State.Variable,State.Variable,State.Variable,State.Variable,State.Variable,
        State.w,State.Variable,State.Variable,State.Variable,State.Variable,State.Variable,
        State.Variable,State.Variable,State.Variable,State.Variable,State.Variable,
        State.Variable,State.Variable,State.Variable,State.Variable,State.Variable,
        State.Variable,State.Variable,State.Variable,State.Variable,State.Variable,
        State.Variable,State.Variable,State.Variable,State.Variable,State.Variable,
        State.Variable,State.Variable,State.Variable,State.Variable,State.int_value,
        State.int_value,State.int_value,State.int_value,State.int_value,State.int_value,
        State.int_value,State.int_value,State.int_value,State.int_value,State.float_value,
        State.int_value,State.Addition,State.Divide,State.Multiply,State.OpenParenthesis,
        State.ClosedParenthesis,State.OpenBracket,State.ClosedBracket,State.Less,State.Greater,State.Assign,State.Exclaim,State.Variable,State.start));
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
        start(0),
        f(1),
        fo(2),
        for_keyword(3),
        fl(4),
        flo(5),
        floa(6),
        float_keyword(7),
        i(8),
        if_keyword(9),
        in(10),
        int_keyword(11),
        e(12),
        el(13),
        els(14),
        else_keyword(15),
        w(16),
        wh(17),
        whi(18),
        whil(19),
        while_keyword(20),
        Variable(21),
        int_value(22),
        float_value(23),
        OpenBracket(24),
        ClosedBracket(25),
        OpenParenthesis(26),
        ClosedParenthesis(27),
        Exclaim(28),
        Unequal(29),
        Greater(30),
        GreaterOrEqual(31),
        Less(32),
        LessOrEqual(33),
        Assign(34),
        Equal(35),
        Addition(36),
        Increment(37),
        AdditionAssignment(38),
        Subtract(39),
        Decrement(40),
        SubtractionAssignment(41),
        Multiply(42),
        MultiplyAssignment(43),
        Divide(44),
        DivideAssignment(45);
        public final int index;
        State(int index)
        {
            this.index = index;
        }
    }
}