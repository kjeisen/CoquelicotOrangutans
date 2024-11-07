package Compiler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import Compiler.Structures.Symbol;
import Compiler.Structures.Token;


// For the atoms I think expressions is the best to start sionce its the smallest part


public class Parser {
	static Stack<Token> stackOfTokens = new Stack<Token>();
	private static Set<String> mapOfVariables = new HashSet<String>();
	private static Stack<Token> expressionVariableStack = new Stack<Token>();
	private static String atomFile = "atom.txt";
	private static Integer tempVariableCounter = 0;
	private static Integer tempLabelCounter = 0;
	private static List<Symbol> booleanOps = List.of(Symbol.LESS_THAN, 
			Symbol.LESS_THAN_EQUAL, Symbol.GREATER_THAN, Symbol.GREATER_THAN_EQUAL,  Symbol.EQUAL, Symbol.NOT_EQUAL);
	
	private static List<Symbol> midPrecedenceOps =
			List.of(Symbol.MULTIPLY, Symbol.DIVIDE);
	
	private static List<Symbol> lowPrecedenceOps =
			List.of(Symbol.ADDITION, Symbol.SUBTRACT);
	private static List<Symbol> assignmentOps = List.of(Symbol.ASSIGNMENT, Symbol.ADDITION_ASSIGNMENT, Symbol.MULTIPLY_ASSIGNMENT, 
			Symbol.SUBTRACT_ASSIGNMENT, Symbol.MULTIPLY_ASSIGNMENT, Symbol.DIVIDE_ASSIGNMENT);
	
    public static void parse(ArrayList<Token> tokens) {
		try {
			createOrResetFile();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return;
		}
        
		stackOfTokens.push(new Token(Symbol.END_OF_INPUT, null));

        for(int i = tokens.size()-1; i >= 0; i--)
		{
			stackOfTokens.push(tokens.get(i));
		}
		System.out.println(stackOfTokens);  
		try {
		IsValidC_Code();
		System.out.println("Good Code");
		}
		catch(Error e)
		{
			System.out.println("Bad Code");
			System.out.println(e.getMessage());
		}
    }

	private static void IsValidC_Code() throws InvalidSyntaxError, EmptyInputError
	{
		Code();
		
	}
	private static void Code() throws InvalidSyntaxError, EmptyInputError 
	{
		if(accept(Symbol.IF))
		{
			If(); Code();
		} 
		else if(accept(Symbol.WHILE))
		{
			While(); Code();
		} 
		else if(accept(Symbol.FOR))
		{
			For(); Code();
		}
		else if(accept(Symbol.FLOAT)|| accept(Symbol.INT) || accept(Symbol.IDENTIFIER))
		{
			Statement();
			Code();
		}
		else if(accept(Symbol.END_OF_INPUT))
		{
			// Okay I guess you can stop there
			return;
		}
		else 
		{
			// not cool man
			throw new EmptyInputError("Either it was empty or had invalid syntax");
		}
		
		// If there was at least one match at all then we done
		
	}
	
	/**
	 * Variant of Code() It Enforces it to be there instead of being optional
	 * This is needed for if() *ENFORCE CODE HERE* else *ENFORCE* if not using {}
	 * @throws InvalidSyntaxError
	 * @throws EmptyInputError
	 */
	private static void ExpectCode() throws InvalidSyntaxError 
	{
		if(accept(Symbol.IF))
		{
			If(); 
		} 
		else if(accept(Symbol.WHILE))
		{
			While(); 
		} 
		else if(accept(Symbol.FOR))
		{
			For(); 
		}
		else if(accept(Symbol.FLOAT)|| accept(Symbol.INT) || accept(Symbol.IDENTIFIER))
		{
			Statement();
		}
		else if(accept(Symbol.END_OF_INPUT))
		{
			// Since Code is expected in this situation we error
			throw new InvalidSyntaxError("Expected an Expression");
		}
		else 
		{
			// Since Code is expected in this situation we error
			throw new InvalidSyntaxError("Expected an Expression");
		}
		
		// If there was at least one match at all then we done
		
	}
	private static void While() throws InvalidSyntaxError
	{
		var labelStart = destLabelMaker();
		var labelEnd = destLabelMaker();
		makeAtomLabel(labelStart);
		expect(Symbol.WHILE);
		expect(Symbol.OPEN_PARENTHESIS);
		BooleanExpression(labelEnd);
		expect(Symbol.CLOSED_PARENTHESIS);
		// while() {}
		if(accept(Symbol.OPEN_BRACKET))
		{
			expect(Symbol.OPEN_BRACKET);
			try {
				Code();
			}
			catch(EmptyInputError e)
			{
				// It was an empty input which is allowed technically in this instance while(bool) {empty}
			}
			expect(Symbol.CLOSED_BRACKET);
		}
		// while() x++;
		else 
		{
			ExpectCode();
		}
		makeAtomJump(labelStart);
		makeAtomLabel(labelEnd);
	}
	private static void For() throws InvalidSyntaxError
	{
		var labelStart = destLabelMaker();
		var labelEnd = destLabelMaker();
		expect(Symbol.FOR);
		expect(Symbol.OPEN_PARENTHESIS);
		
		// At this point we can have either an assignment; or just ;
		// For loops are weird you can technically have any line of code as the first Statement()
		// Our statements are just Assignment and Reassignments so we chilling tho
		// for(Statement();
		if(accept(Symbol.SEMICOLON))
		{
			// no statement this time
			expect(Symbol.SEMICOLON);
		}
		else 
		{
			Statement();
		}
		makeAtomLabel(labelStart);
		BooleanExpression(labelEnd);
		expect(Symbol.SEMICOLON);
		// Cannot have semicolon after 
		if(!accept(Symbol.CLOSED_PARENTHESIS))
		{
			//end of loop statement but NO SEMICOLON
			Reassignment();
		}
		expect(Symbol.CLOSED_PARENTHESIS);
		if(accept(Symbol.OPEN_BRACKET))
		{
			expect(Symbol.OPEN_BRACKET);
			try {
				Code();
			} catch (EmptyInputError e) {
				// It okay to be empty cause I said so
			}
			expect(Symbol.CLOSED_BRACKET);
		}
		else 
		{
			ExpectCode();
		}
		makeAtomJump(labelStart);
		makeAtomLabel(labelEnd);
	}
	
	
	private static void If() throws InvalidSyntaxError
	{
		var labelElse = destLabelMaker();
		var labelEnd = destLabelMaker();
		expect(Symbol.IF);
		expect(Symbol.OPEN_PARENTHESIS);
		BooleanExpression(labelElse);
		expect(Symbol.CLOSED_PARENTHESIS);
		// if() {}
		if(accept(Symbol.OPEN_BRACKET))
		{
			expect(Symbol.OPEN_BRACKET);
			try {
				Code();
			} catch (EmptyInputError e) {
				// thats okay its allowed to be here
			}
			expect(Symbol.CLOSED_BRACKET);
			makeAtomJump(labelEnd);
			makeAtomLabel(labelElse);
			
			// if(){} else...
			Else();
			makeAtomLabel(labelEnd);
		}
		else 
		{
			// There needs to be an expression when not using {}
			ExpectCode();
			// if x++; else.....
			makeAtomJump(labelEnd);
			makeAtomLabel(labelElse);
			Else();
			makeAtomLabel(labelEnd);
		}
	}
	private static void Else() throws InvalidSyntaxError
	{
		if(accept(Symbol.ELSE))
		{
			expect(Symbol.ELSE);
			// if x++; else {}
			if(accept(Symbol.OPEN_BRACKET))
			{
				expect(Symbol.OPEN_BRACKET);
				try {
					Code();
				} catch (EmptyInputError e) {
					// that okay man
				}
				expect(Symbol.CLOSED_BRACKET);
			}
			// if x++; else x--;
			else 
			{
				// There needs to be an expression when not using {}
				ExpectCode();
			}
			
		}
	}
		
	private static void Statement() throws InvalidSyntaxError
	{
		if(accept(Symbol.IDENTIFIER))
		{
			Reassignment();
		} else {
			Assignment(); 
		}
		expect(Symbol.SEMICOLON);
	}
	
	private static void Reassignment() throws InvalidSyntaxError {
		var variable = GetStringValueFromToken(expect(Symbol.IDENTIFIER));
		if(!VariableAlreadyExists(variable))
		{
			throw new Error("Cannot reassign a variable that has not been made");
		}
		if(accept(Symbol.INCREMENT) || accept(Symbol.DECREMENT))
		{
			var assignmentOperator = expectOneOfThese(List.of(Symbol.INCREMENT, Symbol.DECREMENT));
			if(assignmentOperator.symbol == Symbol.INCREMENT)
			{
				makeAtomMath(Symbol.ADDITION, variable,"1", variable);
			}
			else 
			{
				makeAtomMath(Symbol.SUBTRACT, variable,"1", variable);
			}
			
		}
		else
		{
			var assignmentOperator = expectOneOfThese(assignmentOps);
			Expression();
			AddAtomFromAssignment(assignmentOperator.symbol, variable);
		}
		
			
	}
	private static void AddAtomFromAssignment(Symbol op, String variable)
	{
		var answer = expressionVariableStack.pop();
		if(op != Symbol.ASSIGNMENT)
		{
			switch(op)
			{
			case MULTIPLY_ASSIGNMENT:
				makeAtomMath(Symbol.MULTIPLY, variable,  GetStringValueFromToken(answer), variable);
				break;
			case DIVIDE_ASSIGNMENT:
				makeAtomMath(Symbol.DIVIDE, variable,  GetStringValueFromToken(answer), variable);
				break;
			case ADDITION_ASSIGNMENT:
				makeAtomMath(Symbol.ADDITION, variable,  GetStringValueFromToken(answer), variable);
				break;
			case SUBTRACT_ASSIGNMENT:
				makeAtomMath(Symbol.SUBTRACT,variable,  GetStringValueFromToken(answer), variable);
				break;
			default:
				break;
			
			}
		} 
		else
		{
			makeAtomMove(variable, GetStringValueFromToken(answer));
		}
	}
    
	private static void Assignment() throws InvalidSyntaxError
	{
		
			expectOneOfThese(List.of(Symbol.FLOAT, Symbol.INT));
			var variable = GetStringValueFromToken(expect(Symbol.IDENTIFIER));
			createVariable(variable);
			var assignmentOperator = expectOneOfThese(assignmentOps);
			Expression();
			AddAtomFromAssignment(assignmentOperator.symbol, variable);
		
			
	}
	// Alright so theres actually no way to evaluate booleans in the atoms provided by the assignment
	// which are add, mul,sub, div, (tst,,,Dest), JMP, so I have to rework expression 
	// vs a boolean expression cause there is no less_than greater_than ops only jumps with a comparison
	
	private static void BooleanExpression(String destination) throws InvalidSyntaxError
	{
			Expression();
			var LHS = GetStringValueFromToken(expressionVariableStack.pop());
			var operator = expectOneOfThese(booleanOps);
			Expression();
			var RHS = GetStringValueFromToken(expressionVariableStack.pop());
			makeAtomTestJump(LHS, RHS, operator.symbol, destination);
	}
	/**
	 * Gets the mathematical expression
	 * I found that you can create precedence by how you do the parsing so Ill be able to do it this way
	 * I'll sort them by how they get solved somehow
	 * @throws InvalidSyntaxError
	 */
	private static void Expression() throws InvalidSyntaxError
	{
		LowestPrecedence();
	}
	
	
	private static void LowestPrecedence() throws InvalidSyntaxError
	{
		MidPrecedence();
		Token op = null;
		if(accept(lowPrecedenceOps))
		{
			op = expectOneOfThese(lowPrecedenceOps);
			LowestPrecedence();
		}
		if(op != null)
		{
		System.out.println(op);
		String RHS = GetStringValueFromToken(expressionVariableStack.pop());
		String LHS = GetStringValueFromToken(expressionVariableStack.pop());
		var tempName = tempVariableMaker();
		makeAtomMath(op.symbol, LHS, RHS, tempName);
		expressionVariableStack.push(new Token(Symbol.IDENTIFIER, tempName));
		}
		
		
	}
	
	private static void MidPrecedence() throws InvalidSyntaxError
	{
		Token op = null;
		HighestPrecedence();
		if(accept(midPrecedenceOps))
		{
			op = expectOneOfThese(midPrecedenceOps);
			MidPrecedence();
		}
		if(op != null)
		{
			System.out.println(op);
			String RHS = GetStringValueFromToken(expressionVariableStack.pop());
			String LHS = GetStringValueFromToken(expressionVariableStack.pop());
			var tempName = tempVariableMaker();
			makeAtomMath(op.symbol, LHS, RHS, tempName);
			expressionVariableStack.push(new Token(Symbol.IDENTIFIER, tempName));
		}
		
		
	}
	private static void  HighestPrecedence() throws InvalidSyntaxError
	{
		Token value = null;
		if(accept(Symbol.OPEN_PARENTHESIS)) {
			expect(Symbol.OPEN_PARENTHESIS);
			Expression();
			expect(Symbol.CLOSED_PARENTHESIS);
		}
		else 
		{
			value = expectOneOfThese(List.of(Symbol.IDENTIFIER, Symbol.FLOAT_VALUE, Symbol.INT_VALUE));
			if(value.symbol == Symbol.IDENTIFIER)
			{
				if(!VariableAlreadyExists(value.value.toString()))
				{
					throw new InvalidSyntaxError("Variable not created "+ value.value.toString());
				}
				
			}
		}
		if(value != null)
		{
			System.out.println(value);
			expressionVariableStack.push(value);
		}
		
	}
	
	
	private static boolean VariableAlreadyExists(String variableName) 
	{
		return mapOfVariables.contains(variableName);
	}
	private static void createVariable(String variableName) throws InvalidSyntaxError
	{
		if(VariableAlreadyExists(variableName)) throw new Error("Variable already exists " + variableName); 
		mapOfVariables.add(variableName);
	}
	private static boolean accept(Symbol symbol)
	{
		boolean result = stackOfTokens.peek().symbol == symbol;
		return result;
	}
	
	private static Token expect(Symbol symbol) throws InvalidSyntaxError 
	{
	
		if(stackOfTokens.peek().symbol != symbol) {
			
			throw new InvalidSyntaxError("Expected a "+ symbol + " got " + stackOfTokens.peek().symbol + " instead");
		}
		
		return stackOfTokens.pop();
	}
	private static Token expectOneOfThese(List<Symbol> symbol) throws InvalidSyntaxError 
	{
		boolean flag = symbol.stream().anyMatch(e -> e == stackOfTokens.peek().symbol);
		
		if(!flag)
		{
			throw new InvalidSyntaxError("Expected a "+ symbol + " got " + stackOfTokens.peek().symbol + " instead");
		}
		return stackOfTokens.pop();
	}
	private static boolean accept(List<Symbol> symbol) throws InvalidSyntaxError 
	{
		boolean flag = symbol.stream().anyMatch(e -> e == stackOfTokens.peek().symbol);
		return flag;
	}
	public static void createOrResetFile() throws IOException
	{
		File file = new File(atomFile);
			if(!file.createNewFile())
			{
			// file exists 
				file.delete();
				if(!file.createNewFile())
				{
					// it needs to be made so error
					throw new IOException();
				}
			}
			// yay file made
		
	}
	public static void writeAtomToFile(String atom) throws IOException
	{
		FileWriter fw = new FileWriter(atomFile,true);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.append(atom);
		bw.append('\n');
		bw.close();
		fw.close();

		
	}
	public static String destLabelMaker()
	{
		String dest = "Dest" + tempLabelCounter.toString();
		tempLabelCounter++;
		return dest;
	}
	public static String tempVariableMaker()
	{
		String name = "T" + tempVariableCounter.toString();
		tempVariableCounter++;
		return name;
	}
	public static String GetStringValueFromToken(Token LHS)
	{
		return LHS.value.toString();
	}
	public static void makeAtomMath(Symbol operator, String LHS, String RHS, String result)
	{
		
		StringBuilder sb = new StringBuilder();
		sb.append('(');
		switch(operator)
		{
		case MULTIPLY:
			sb.append("MUL, ");
			break;
		case ADDITION:
			sb.append("ADD, ");
			break;
		case DIVIDE:
			sb.append("DIV, ");
			break;
		case SUBTRACT:
			sb.append("SUB, ");
			break;
		
		default:
			System.out.println("You shouldnt have called this");
			break;
		}
		sb.append(LHS.toString()+", ");
		sb.append(RHS.toString()+", ");
		sb.append(result + ")");
		try {
			writeAtomToFile(sb.toString());
		} catch (IOException e) {
			throw new Error("Failed to write atom to file");
		}
	}
	public static void makeAtomJump(String dest)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("(JMP, , , , , ");
		sb.append(dest);
		sb.append(')');
		try {
			writeAtomToFile(sb.toString());
		} catch (IOException e) {
			throw new Error("Failed to write atom to file");
		}
	}
	public static void makeAtomLabel(String dest)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("(LBL");
		sb.append(", , , , , ");
		sb.append(dest);
		sb.append(')');
		try {
			writeAtomToFile(sb.toString());
		} catch (IOException e) {
			throw new Error("Failed to write atom to file");
		}
	}
	public static void makeAtomTestJump(String LHS, String RHS, Symbol op, String labelDest)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("(TST, ");
		sb.append(LHS.toString() + ", ");
		sb.append(RHS.toString() + ", ");
		sb.append(", ");
		sb.append(getCompareNumber(op).toString() + ", ");
		sb.append(labelDest + ")");
		try {
			writeAtomToFile(sb.toString());
		} catch (IOException e) {
			throw new Error("Failed to write atom to file");
		}
	}
	public static Integer getCompareNumber(Symbol op)
	{
		int res = 7;
		switch(op)
		{
		case EQUAL:
			return res-1;
		case LESS_THAN:
			return res-2;
		case GREATER_THAN:
			return res-3;
		case LESS_THAN_EQUAL:
			return res-4;
		case GREATER_THAN_EQUAL:
			return res-5;
		case NOT_EQUAL:
			return res-6;
		default:
			throw new Error("Tried to find comparision number for symbol that not a comparison");
		}
	}
	public static void makeAtomMove(String LHS, String RHS)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("(MOV, ");
		sb.append(RHS.toString() + ", ");
		sb.append(", " + LHS + ")");
		try {
			writeAtomToFile(sb.toString());
		} catch (IOException e) {
			throw new Error("Failed to write atom to file");
		}
	}

}
class InvalidSyntaxError extends Error{
	public InvalidSyntaxError(String errMessage)
	{
		super(errMessage);
	}
}
class EmptyInputError extends Error{
	public EmptyInputError(String errMessage)
	{
		super(errMessage);
	}
}