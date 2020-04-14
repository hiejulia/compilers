package com.compiler;

import jdk.nashorn.internal.ir.debug.PrintVisitor;

public class ParserTest {

    public static void main(String args[]) throws Exception {

        PrintVisitor myVisitor = new PrintVisitor();
        try {
            Parser myParser = new Parser("parseTest.txt");
            AST t = myParser.execute();
            myVisitor.print(t.getType()+ ": " + t.getSymbol(), t);
        } catch (Exception e) {
            throw e;
        }

    }
}