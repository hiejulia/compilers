package com.compiler;

import com.sun.tools.javac.parser.Tokens;

class SyntaxError extends Exception {

    private static final long serialVersionUID = 1L;

    //private Token tokenFound;
    private Tokens kindExpected;

    public SyntaxError(Token tokenFound, Tokens kindExpected) {
        this.kindExpected = kindExpected;
    }

}