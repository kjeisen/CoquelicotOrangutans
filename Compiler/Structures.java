package Compiler;

public class Structures {
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
