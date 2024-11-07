package Compiler;
public class Structures {

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
