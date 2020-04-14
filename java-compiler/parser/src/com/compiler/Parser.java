package com.compiler;

import com.sun.source.tree.BlockTree;
import com.sun.tools.javac.parser.Lexer;
import com.sun.tools.javac.parser.Tokens;


import java.util.*;


import java.util.*;


/**
 * The Parser class performs recursive-descent parsing; as a by-product it will
 * build the <b>Abstract Syntax Tree</b> representation for the source
 * program<br>
 */
public class Parser {

    private Token currentToken;
    private Lexer lex;
    private EnumSet<Tokens> relationalOps
            = EnumSet.of(Tokens.Equal, Tokens.NotEqual, Tokens.Less, Tokens.LessEqual);
    private EnumSet<Tokens> addingOps
            = EnumSet.of(Tokens.Plus, Tokens.Minus, Tokens.Or);
    private EnumSet<Tokens> multiplyingOps
            = EnumSet.of(Tokens.Multiply, Tokens.Divide, Tokens.And);


    public Parser(String sourceProgram) throws Exception {
        try {
            lex = new Lexer(sourceProgram);
            scan();
        } catch (Exception e) {
            throw e;
        };
    }

    public Lexer getLex() {
        return lex;
    }

    public AST execute() throws Exception {
        try {
            return rProgram();
        } catch (SyntaxError e) {
            e.print();
            throw e;
        }
    }


    public AST rProgram() throws SyntaxError {
        // note that rProgram actually returns a ProgramTree; we use the
        // principle of substitutability to indicate it returns an AST
        AST t = new ProgramTree();
        expect(Tokens.Program);
        t.addKid(rBlock());
        return t;
    }

    public AST rBlock() throws SyntaxError {
        expect(Tokens.LeftBrace);
        AST t = new BlockTree();
        while (startingDecl()) {
            t.addKid(rDecl());
        }
        while (startingStatement()) {
            t.addKid(rStatement());
        }
        expect(Tokens.RightBrace);
        return t;
    }

    boolean startingDecl() {
        if (isNextTok(Tokens.Int) || isNextTok(Tokens.BOOLean)) {
            return true;
        }
        return false;
    }

    boolean startingStatement() {
        if (isNextTok(Tokens.If) || isNextTok(Tokens.While) || isNextTok(Tokens.Return)
                || isNextTok(Tokens.LeftBrace) || isNextTok(Tokens.Identifier)) {
            return true;
        }
        return false;
    }

    public AST rDecl() throws SyntaxError {
        AST t, t1;
        t = rType();
        t1 = rName();
        if (isNextTok(Tokens.LeftParen)) { // function
            t = (new FunctionDeclTree()).addKid(t).addKid(t1);
            t.addKid(rFunHead());
            t.addKid(rBlock());
            return t;
        }
        t = (new DeclTree()).addKid(t).addKid(t1);
        return t;
    }


    public AST rType() throws SyntaxError {
        AST t;
        if (isNextTok(Tokens.Int)) {
            t = new IntTypeTree();
            scan();
        } else {
            expect(Tokens.BOOLean);
            t = new BoolTypeTree();
        }
        return t;
    }


    public AST rFunHead() throws SyntaxError {
        AST t = new FormalsTree();
        expect(Tokens.LeftParen);
        if (!isNextTok(Tokens.RightParen)) {
            do {
                t.addKid(rDecl());
                if (isNextTok(Tokens.Comma)) {
                    scan();
                } else {
                    break;
                }
            } while (true);
        }
        expect(Tokens.RightParen);
        return t;
    }


    public AST rStatement() throws SyntaxError {
        AST t;
        if (isNextTok(Tokens.If)) {
            scan();
            t = new IfTree();
            t.addKid(rExpr());
            expect(Tokens.Then);
            t.addKid(rBlock());
            expect(Tokens.Else);
            t.addKid(rBlock());
            return t;
        }
        if (isNextTok(Tokens.While)) {
            scan();
            t = new WhileTree();
            t.addKid(rExpr());
            t.addKid(rBlock());
            return t;
        }
        if (isNextTok(Tokens.Return)) {
            scan();
            t = new ReturnTree();
            t.addKid(rExpr());
            return t;
        }
        if (isNextTok(Tokens.LeftBrace)) {
            return rBlock();
        }
        t = rName();
        t = (new AssignTree()).addKid(t);
        expect(Tokens.Assign);
        t.addKid(rExpr());
        return t;
    }


    public AST rExpr() throws SyntaxError {
        AST t, kid = rSimpleExpr();
        t = getRelationTree();
        if (t == null) {
            return kid;
        }
        t.addKid(kid);
        t.addKid(rSimpleExpr());
        return t;
    }

    public AST rSimpleExpr() throws SyntaxError {
        AST t, kid = rTerm();
        while ((t = getAddOperTree()) != null) {
            t.addKid(kid);
            t.addKid(rTerm());
            kid = t;
        }
        return kid;
    }


    public AST rTerm() throws SyntaxError {
        AST t, kid = rFactor();
        while ((t = getMultOperTree()) != null) {
            t.addKid(kid);
            t.addKid(rFactor());
            kid = t;
        }
        return kid;
    }


    public AST rFactor() throws SyntaxError {
        AST t;
        if (isNextTok(Tokens.LeftParen)) { // -> (e)
            scan();
            t = rExpr();
            expect(Tokens.RightParen);
            return t;
        }
        if (isNextTok(Tokens.INTeger)) {  //  -> <int>
            t = new IntTree(currentToken);
            scan();
            return t;
        }
        t = rName();
        if (!isNextTok(Tokens.LeftParen)) {  //  -> name
            return t;
        }
        scan();     // -> name '(' (e list ',')? ) ==> call
        t = (new CallTree()).addKid(t);
        if (!isNextTok(Tokens.RightParen)) {
            do {
                t.addKid(rExpr());
                if (isNextTok(Tokens.Comma)) {
                    scan();
                } else {
                    break;
                }
            } while (true);
        }
        expect(Tokens.RightParen);
        return t;
    }

    public AST rName() throws SyntaxError {
        AST t;
        if (isNextTok(Tokens.Identifier)) {
            t = new IdTree(currentToken);
            scan();
            return t;
        }
        throw new SyntaxError(currentToken, Tokens.Identifier);
    }

    AST getRelationTree() {  // build tree with current token's relation
        Tokens kind = currentToken.getKind();
        if (relationalOps.contains(kind)) {
            AST t = new RelOpTree(currentToken);
            scan();
            return t;
        } else {
            return null;
        }
    }

    private AST getAddOperTree() {
        Tokens kind = currentToken.getKind();
        if (addingOps.contains(kind)) {
            AST t = new AddOpTree(currentToken);
            scan();
            return t;
        } else {
            return null;
        }
    }

    private AST getMultOperTree() {
        Tokens kind = currentToken.getKind();
        if (multiplyingOps.contains(kind)) {
            AST t = new MultOpTree(currentToken);
            scan();
            return t;
        } else {
            return null;
        }
    }

    private boolean isNextTok(Tokens kind) {
        if ((currentToken == null) || (currentToken.getKind() != kind)) {
            return false;
        }
        return true;
    }

    private void expect(Tokens kind) throws SyntaxError {
        if (isNextTok(kind)) {
            scan();
            return;
        }
        throw new SyntaxError(currentToken, kind);
    }

    private void scan() {
        currentToken = lex.nextToken();
        return;
    }
}

