package Compiler;

import java.util.ArrayList;
import java.util.Stack;

import Compiler.Structures.*;

public class Parser {
    private static Stack<Pair> stack = new Stack<>();

    private static void listToStack(ArrayList<Pair> tokens) {
        for (Pair token : tokens) {
            stack.push(token);
        }
    }

    public static void parse(ArrayList<Pair> tokens) {
        listToStack(tokens);
        Code();
    }

    private static boolean accept(State token) {
        if (stack.isEmpty()) return false; // TODO: double check that we return false intsead of throwing error
        if (stack.peek().state == token) {
            return true;
        }
        return false;
    }

    private static void expect(State token) {
        if (accept(token)) {
            stack.pop();
        } else {
            throw new Error("Expected " + token + " but got " + stack.peek().state);
        }
    }

    private static void Code() {
        if(accept(State.IF_KEYWORD)) If();
        else if(accept(State.WHILE_KEYWORD)) While();
        else if(accept(State.FOR_KEYWORD)) For();
        else if(accept(null)) return;
        else {
            Statement();
            expect(State.SEMICOLON);
            Code();
        }
    }

    private static void Statement() {
        if(accept(null)) return;
        Assignment();
    }

    private static void Assignment() {
        if(accept(State.INT_KEYWORD) || accept(State.FLOAT_KEYWORD)) {
            stack.pop();
            expect(State.VARIABLE);
            expect(State.ASSIGN);
            Expression();
        } else {
            expect(State.VARIABLE);
            AssignOperator();
            Expression();
        }
    }

    private static void Expression() {
        if(accept(State.INT_VALUE) || accept(State.FLOAT_VALUE) || accept(State.VARIABLE)) {
            stack.pop();
        } else if(accept(State.OPENPARENTHESIS)) {
            stack.pop();
            Expression();
            expect(State.CLOSEDPARENTHESIS);
        } else {
            Expression();
            Operator();
            Expression();
        }
    }

    private static void If() {
        expect(State.IF_KEYWORD);
        expect(State.OPENPARENTHESIS);
        Expression();
        expect(State.CLOSEDPARENTHESIS);
        if(accept(State.OPENBRACKET)) {
            stack.pop();
            Code();
            expect(State.CLOSEDBRACKET);

            if(accept(State.ELSE_KEYWORD)) {
                stack.pop();
                if(accept(State.OPENBRACKET)) {
                    Code();
                    expect(State.CLOSEDBRACKET);
                } else {
                    If();
                }
            }
        } else {
            Code();
        }
    }  
    
    private static void For() {
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
            expect(State.CLOSEDBRACKET);
        } else {
            Statement();
            expect(State.SEMICOLON);
        }
    }

    private static void While() {
        expect(State.WHILE_KEYWORD);
        expect(State.OPENPARENTHESIS);
        Expression();
        expect(State.CLOSEDPARENTHESIS);

        if(accept(State.OPENBRACKET)) {
            stack.pop();
            Code();
            expect(State.CLOSEDBRACKET);
        } else {
            Code();
            expect(State.SEMICOLON);
        }
    }

    private static void Operator() {
        if(accept(State.ADDITION) || accept(State.SUBTRACT) || 
           accept(State.MULTIPLY) || accept(State.DIVIDE) || 
           accept(State.EQUAL) || accept(State.UNEQUAL) || 
           accept(State.GREATEROREQUAL) || accept(State.GREATER) || 
           accept(State.LESSOREQUAL) || accept(State.LESS)) {
            stack.pop();
        } else {
            throw new Error("Expected an operator but got " + stack.peek().state);
        }
    }

    private static void AssignOperator() {
        if(accept(State.ASSIGN) || accept(State.ADDITIONASSIGNMENT) || 
           accept(State.SUBTRACTIONASSIGNMENT) || accept(State.MULTIPLYASSIGNMENT) || 
           accept(State.DIVIDEASSIGNMENT)) {
            stack.pop();
        } else {
            throw new Error("Expected an assignment operator but got " + stack.peek().state);
        }
    }

}
