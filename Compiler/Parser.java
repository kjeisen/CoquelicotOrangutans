package Compiler;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Stack;

import Compiler.Structures.*;

public class Parser {
    private static Stack<Pair> stack = new Stack<>();

    private static void listToStack(ArrayList<Pair> tokens) {
        for (int i = tokens.size() - 1; i >= 0; i--) {
            stack.push(tokens.get(i));
        }
    }

    public static void parse(ArrayList<Pair> tokens) {
        System.out.println("\nInitializing Parser...");
        listToStack(tokens);
        System.out.println("Parser running...");
        while(!stack.isEmpty()) Code();
        System.out.println("Parser ran successfully!");
        // printStack();
    }

    private static void printStack() {
        for (Pair pair : stack) {
            System.out.println(pair);
        }
    }

    private static boolean accept(State token) {
        if (stack.isEmpty())
            return false;
        if (stack.peek().state == token) {
            return true;
        }
        return false;
    }

    private static void expect(State token) {
        System.out.println(token);
        if (accept(token)) {
            stack.pop();
        } else {
            printStack();
            try
            {
                throw new Error("Expected " + token + " but got " + stack.peek().state);
            }
            catch (EmptyStackException e)
            {
                throw new Error("Empty Stack");
            }
        }
    }

    private static void Code() {
        //System.out.println("CODE");
        if(accept(State.IF_KEYWORD)) If();
        else if(accept(State.WHILE_KEYWORD)) While();
        else if(accept(State.FOR_KEYWORD)) For();
        else if(!stack.isEmpty()){
            Statement();
            if(accept(State.CLOSEDBRACKET)) {
                stack.pop();
                return;
            } else if(accept(State.SEMICOLON)) {
                stack.pop();
                Code();
            }
        }
        //System.out.println("CODE-END");
    }

    private static void Statement() {
        //System.out.println("STATEMENT");
        if(stack.isEmpty()) return;
        Assignment();
    }

    private static void Assignment() {
        //System.out.println("ASSIGNMENT");
        if(accept(State.INT_KEYWORD) || accept(State.FLOAT_KEYWORD)) {
            //System.out.println("\tINT/FLOAT KEYWORD");
            stack.pop();
            expect(State.VARIABLE);
            expect(State.ASSIGN);
            Expression();
        } else if(accept(State.VARIABLE)){
            //System.out.println("\tVARIABLE");
            expect(State.VARIABLE);
            AssignOperator();
            Expression();
        } else return;
        //System.out.println("ASSIGNMENT-END");
    }

    private static void Expression() {
        //System.out.println("EXPRESSION");
        if(accept(State.CLOSEDPARENTHESIS) || accept(State.SEMICOLON)) return;
        else if(accept(State.INT_VALUE) || accept(State.FLOAT_VALUE) || accept(State.VARIABLE)) {
            //System.out.println("\tINT/FLOAT/VARIABLE");
            stack.pop();
            Expression();
        } else if(accept(State.OPENPARENTHESIS)) {
            //System.out.println("\tOPENPARENTHESIS");
            stack.pop();
            Expression();
            expect(State.CLOSEDPARENTHESIS);
        } else {
            //System.out.println("\tEXPRESSION");
            Operator();
            Expression();
        }
        //System.out.println("EXPRESSION-END");
    }

    private static void If() {
        //System.out.println("IF");
        expect(State.IF_KEYWORD);
        expect(State.OPENPARENTHESIS);
        System.out.println("reached");
        Expression();
        expect(State.CLOSEDPARENTHESIS);
        if(accept(State.OPENBRACKET)) {
            stack.pop();
            Code();
            Else();
        } else {
            Code();
            Else();
        }
    }  

    private static void Else() {
        if(accept(State.ELSE_KEYWORD)) {
            stack.pop();

            if(accept(State.OPENBRACKET)) {
                stack.pop();
                Code();
            } else if(accept(State.IF_KEYWORD)){
                If();
            } else {
                Code();
            }
        }
    }
    
    private static void For() {
        //System.out.println("FOR");
        expect(State.FOR_KEYWORD);
        expect(State.OPENPARENTHESIS);
        Assignment();
        expect(State.SEMICOLON);
        Expression();
        expect(State.SEMICOLON);
        Statement();
        expect(State.CLOSEDPARENTHESIS);

        if(accept(State.OPENBRACKET)) {
            stack.pop();
            Code();
        } else {
            Statement();
            expect(State.SEMICOLON);
        }
    }

    private static void While() {
        //System.out.println("WHILE");
        expect(State.WHILE_KEYWORD);
        expect(State.OPENPARENTHESIS);
        Expression();
        expect(State.CLOSEDPARENTHESIS);

        if(accept(State.OPENBRACKET)) {
            //System.out.println("\tOPENBRACKET");
            stack.pop();
            Code();
        } else {
            //System.out.println("\tNOT OPENBRACKET");
            Code();
        }
        //System.out.println("WHILE-END");
    }

    private static void Operator() {
        //System.out.println("OPERATOR");
        if(accept(State.ADDITION) || accept(State.SUBTRACT) || 
           accept(State.MULTIPLY) || accept(State.DIVIDE) || 
           accept(State.EQUAL) || accept(State.UNEQUAL) || 
           accept(State.GREATEROREQUAL) || accept(State.GREATER) || 
           accept(State.LESSOREQUAL) || accept(State.LESS)) {
            stack.pop();
        }  else {
            printStack();
            throw new Error("Expected an OP but got " + stack.peek().state);
        }
    }

    private static void AssignOperator() {
        //System.out.println("ASSIGNOPERATOR");
        if(accept(State.ASSIGN) || accept(State.ADDITIONASSIGNMENT) || 
           accept(State.SUBTRACTIONASSIGNMENT) || accept(State.MULTIPLYASSIGNMENT) || 
           accept(State.DIVIDEASSIGNMENT) || accept(State.INCREMENT) ||
           accept(State.DECREMENT)) {
            stack.pop();
        } else {
            throw new Error("Expected an ASSIGNOP but got " + stack.peek().state);
        }
    }


}
