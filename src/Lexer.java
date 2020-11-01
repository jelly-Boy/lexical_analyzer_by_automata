import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Lexer {

    private int state = 0;
    private StringBuilder tmp;

    ArrayList<Pair> ans = new ArrayList<>();

    private String ops = new String("+-*/%=<>!&|^~");
    private String[] operators = {"+", "-", "*", "/", "%", "=", "<", ">", "+=", "-=",
            "*=", "**", "**=", "/=", "//", "//=", "%=", "==", "<<", "<=", "<>", ">>",
            ">=", "!=", "&", "|", "^", "~"};
    private char quotation1 = '\'';
    private char quotation2 = '"';
    private char sharp = '#';
    private char newLine = '\n';
    private char underscore = '_';
    private String[] reservedWords = {"False", "None", "True", "and", "as", "assert",
            "async", "await", "break", "class", "continue", "def", "del", "elif",
            "else", "except", "finally", "for", "from", "global", "if", "import", "in",
            "is", "lambda", "nonlocal", "not", "or", "pass", "raise", "return", "try",
            "while", "with", "yield"};
    private String delimiters = new String("()[]{}:,");


    private class Pair{
        private final String lexem;
        private final String value;

        public Pair(String lexem , String value){
            this.lexem = lexem;
            this.value = value;
        }

        public String toString(){
            return value + " - <" + lexem + ">";
        }
    }

    public void write_ans(String fileOutName){
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileOutName));
            for (int i = 0; i < ans.size(); i++) {
                writer.write(ans.get(i).toString() + "\n");
                writer.flush();
            }
        } catch (IOException e) {
            System.out.println("File not found");
            e.printStackTrace();
        }

    }

    private boolean isZero(char ch){
        return ch == '0';
    }

    private boolean isDigit(char ch){
        return ch >= '0' && ch <= '9';
    }

    private boolean isDot(char ch){
        return ch == '.';
    }

    private boolean isStandart(char ch){
        return ch == 'e' || ch == 'E';
    }

    private boolean isOct(char ch){
        return ch == 'o' || ch == 'O';
    }

    private boolean isHex(char ch){
        return ch == 'x' || ch == 'X';
    }

    private boolean isOctSymbols(char ch){
        return ch >= '0' && ch <= '7';
    }

    private boolean isHexSymbols(char ch){
        return ch >= '0' && ch <= '9' ||
                ch >= 'a' && ch <= 'f' ||
                ch >= 'A' && ch <= 'F';
    }

    private boolean isWhiteSpace(char ch){
        return ch == ' ' || ch == '\n' || ch == '\r' || ch == '\t';
    }

    private boolean isOp(char ch){
        return ops.indexOf(ch) != -1;
    }

    private boolean isQuotation1(char ch){
        return quotation1 == ch;
    }

    private boolean isQuotation2(char ch){
        return quotation2 == ch;
    }

    private boolean isSharp(char ch){
        return sharp == ch;
    }

    private boolean isNewLine(char ch){
        return newLine == ch;
    }

    private boolean isUnderscore(char ch){
        return underscore == ch;
    }

    private boolean isLowerCase(char ch){
        return ch >= 'a' && ch <= 'z';
    }

    private boolean isUpperCase(char ch){
        return ch >= 'A' && ch <= 'Z';
    }

    private boolean isBeginOfIdentier(char ch){
        return isUnderscore(ch) || isLowerCase(ch) || isUpperCase(ch);
    }

    private boolean isSymbolOfIdentifier(char ch){
        return isBeginOfIdentier(ch) || isDigit(ch);
    }

    private boolean isDelimiter(char ch){
        return delimiters.indexOf(ch) != -1;
    }


    private boolean isCorrectOperator(String s){
        return Arrays.asList(operators).contains(s);
    }

    private boolean isReservedWord(String s){
        return Arrays.asList(reservedWords).contains(s);
    }

    private void push(){
        if (tmp.toString().isEmpty()){ return; }
        String lexem = States.getLexemName(state);
        if (lexem == "COMMENT") { return; }

        if (lexem == "OPERATOR" && !isCorrectOperator(tmp.toString())){ lexem = "ERROR"; }
        if (lexem == "IDENTIFIER" && isReservedWord(tmp.toString())){ lexem = "RESERVED_WORD"; }
        ans.add(new Pair(lexem, tmp.toString()));
    }


    public void process(String line){
        state = 0;
        tmp = new StringBuilder();

        for (int i = 0; i < line.length(); i ++) {
            char ch = line.charAt(i);

            if (state == -1){
                if (isWhiteSpace(ch)){
                    push();
                    state = -2; // wh
                    tmp = new StringBuilder();
                    tmp.append(ch);
                    push();
                    tmp = new StringBuilder();
                    state = 0;
                } else if (isDelimiter(ch)){
                    push();
                    state = -3; // delimiter
                    tmp = new StringBuilder();
                    tmp.append(ch);
                    push();
                    tmp = new StringBuilder();
                    state = 0;
                } else if (isOp(ch)){
                    push();
                    tmp = new StringBuilder();
                    tmp.append(ch);
                    state = 11;
                } else if (isSharp(ch)){
                    push();
                    tmp = new StringBuilder();
                    tmp.append(ch);
                    state = 15;
                } else {
                    tmp.append(ch);
                }
                continue;
            }
            if (state == 11){ // op
                if (isOp(ch)){
                    tmp.append(ch);
                    state = 11;
                    continue;
                } else {
                    push();
                    tmp = new StringBuilder();
                    state = 0;
                }
            }
            if (state == 14){//string const
                push();
                tmp = new StringBuilder();
                state = 0;
            }
            if (state == 16){//identifier
                if (isSymbolOfIdentifier(ch)){
                    tmp.append(ch);
                    state = 16;
                    continue;
                } else {
                    push();
                    tmp = new StringBuilder();
                    state = 0;
                }
            }
            if (state == 0){ // init state
                if (isZero(ch)){
                    tmp.append(ch);
                    state = 3;
                } else if (isDigit(ch)) {
                    tmp.append(ch);
                    state = 1;
                } else if (isWhiteSpace(ch)){
                    tmp.append(ch);
                    state = -2; // wh
                    push();
                    tmp = new StringBuilder();
                    state = 0;
                } else if (isDelimiter(ch)){
                    tmp.append(ch);
                    state = -3; // delimiter
                    push();
                    tmp = new StringBuilder();
                    state = 0;
                } else if (isOp(ch)){
                    tmp.append(ch);
                    state = 11;
                } else if (isQuotation1(ch)){
                    tmp.append(ch);
                    state = 12;
                } else if (isQuotation2(ch)){
                    tmp.append(ch);
                    state = 13;
                } else if (isSharp(ch)){
                    tmp.append(ch);
                    state = 15;
                } else if (isBeginOfIdentier(ch)){
                    tmp.append(ch);
                    state = 16;
                } else {
                    tmp.append(ch);
                    state = -1;
                }
                continue;
            }
            if (state == 1){ // decimal
                if (isDigit(ch)){
                    tmp.append(ch);
                    state = 1;
                } else if (isDot(ch)){
                    tmp.append(ch);
                    state = 2;
                } else if (isWhiteSpace(ch)){
                    push();
                    tmp = new StringBuilder();
                    tmp.append(ch);
                    state = -2; // wh
                    push();
                    tmp = new StringBuilder();
                    state = 0;
                } else if (isDelimiter(ch)){
                    push();
                    tmp = new StringBuilder();
                    tmp.append(ch);
                    state = -3; // delimiter
                    push();
                    tmp = new StringBuilder();
                    state = 0;
                } else if (isOp(ch)){
                    push();
                    tmp = new StringBuilder();
                    tmp.append(ch);
                    state = 11;
                } else if (isSharp(ch)){
                    push();
                    tmp = new StringBuilder();
                    tmp.append(ch);
                    state = 15;
                } else {
                    tmp.append(ch);
                    state = -1;
                }
                continue;
            }
            if (state == 2){ // double
                if (isDigit(ch)){
                    tmp.append(ch);
                    state = 2;
                } else if (isStandart(ch)){
                    tmp.append(ch);
                    state = 4;
                } else if (isWhiteSpace(ch)){
                    push();
                    tmp = new StringBuilder();
                    tmp.append(ch);
                    state = -2; // wh
                    push();
                    tmp = new StringBuilder();
                    state = 0;
                } else if (isDelimiter(ch)){
                    push();
                    tmp = new StringBuilder();
                    tmp.append(ch);
                    state = -3; // delimiter
                    push();
                    tmp = new StringBuilder();
                    state = 0;
                } else if (isOp(ch)){
                    push();
                    tmp = new StringBuilder();
                    tmp.append(ch);
                    state = 11;
                } else if (isSharp(ch)){
                    push();
                    tmp = new StringBuilder();
                    tmp.append(ch);
                    state = 15;
                } else {
                    tmp.append(ch);
                    state = -1;
                }
                continue;
            }
            if (state == 3){ // zero
                if (isOct(ch)){
                    tmp.append(ch);
                    state = 9;
                } else if (isHex(ch)){
                    tmp.append(ch);
                    state = 7;
                } else if (isWhiteSpace(ch)){
                    push();
                    tmp = new StringBuilder();
                    tmp.append(ch);
                    state = -2; // wh
                    push();
                    tmp = new StringBuilder();
                    state = 0;
                } else if (isDelimiter(ch)){
                    push();
                    tmp = new StringBuilder();
                    tmp.append(ch);
                    state = -3; // delimiter
                    push();
                    tmp = new StringBuilder();
                    state = 0;
                } else if (isOp(ch)){
                    push();
                    tmp = new StringBuilder();
                    tmp.append(ch);
                    state = 11;
                } else if (isSharp(ch)){
                    push();
                    tmp = new StringBuilder();
                    tmp.append(ch);
                    state = 15;
                } else {
                    tmp.append(ch);
                    state = -1;
                }
                continue;
            }
            if (state == 4){ // standart
                if (ch == '+' || ch == '-'){
                    tmp.append(ch);
                    state = 5;
                } else if (isDigit(ch)){
                    tmp.append(ch);
                    state = 6;
                } else if (isWhiteSpace(ch)){
                    push();
                    tmp = new StringBuilder();
                    tmp.append(ch);
                    state = -2; // wh
                    push();
                    tmp = new StringBuilder();
                    state = 0;
                } else if (isDelimiter(ch)){
                    push();
                    tmp = new StringBuilder();
                    tmp.append(ch);
                    state = -3; // delimiter
                    push();
                    tmp = new StringBuilder();
                    state = 0;
                } else {
                    tmp.append(ch);
                    state = -1;
                }
                continue;
            }
            if (state == 5){ // standart
                if (isDigit(ch)){
                    tmp.append(ch);
                    state = 6;
                } else if (isWhiteSpace(ch)){
                    push();
                    tmp = new StringBuilder();
                    tmp.append(ch);
                    state = -2; // wh
                    push();
                    tmp = new StringBuilder();
                    state = 0;
                } else if (isDelimiter(ch)){
                    push();
                    tmp = new StringBuilder();
                    tmp.append(ch);
                    state = -3; // delimiter
                    push();
                    tmp = new StringBuilder();
                    state = 0;
                } else {
                    tmp.append(ch);
                    state = -1;
                }
                continue;
            }
            if (state == 6){ // standart
                if (isDigit(ch)){
                    tmp.append(ch);
                    state = 6;
                } else if (isWhiteSpace(ch)){
                    push();
                    tmp = new StringBuilder();
                    tmp.append(ch);
                    state = -2; // wh
                    push();
                    tmp = new StringBuilder();
                    state = 0;
                } else if (isDelimiter(ch)){
                    push();
                    tmp = new StringBuilder();
                    tmp.append(ch);
                    state = -3; // delimiter
                    push();
                    tmp = new StringBuilder();
                    state = 0;
                } else if (isOp(ch)){
                    push();
                    tmp = new StringBuilder();
                    tmp.append(ch);
                    state = 11;
                } else if (isSharp(ch)){
                    push();
                    tmp = new StringBuilder();
                    tmp.append(ch);
                    state = 15;
                } else {
                    tmp.append(ch);
                    state = -1;
                }
                continue;
            }
            if (state == 7){ // hex
                if (isHexSymbols(ch)){
                    tmp.append(ch);
                    state = 8;
                } else if (isWhiteSpace(ch)){
                    push();
                    tmp = new StringBuilder();
                    tmp.append(ch);
                    state = -2; // wh
                    push();
                    tmp = new StringBuilder();
                    state = 0;
                } else if (isDelimiter(ch)){
                    push();
                    tmp = new StringBuilder();
                    tmp.append(ch);
                    state = -3; // delimiter
                    push();
                    tmp = new StringBuilder();
                    state = 0;
                } else {
                    tmp.append(ch);
                    state = -1;
                }
                continue;
            }
            if (state == 8){ // hex
                if (isHexSymbols(ch)){
                    tmp.append(ch);
                    state = 8;
                } else if (isWhiteSpace(ch)){
                    push();
                    tmp = new StringBuilder();
                    tmp.append(ch);
                    state = -2; // wh
                    push();
                    tmp = new StringBuilder();
                    state = 0;
                } else if (isDelimiter(ch)){
                    push();
                    tmp = new StringBuilder();
                    tmp.append(ch);
                    state = -3; // delimiter
                    push();
                    tmp = new StringBuilder();
                    state = 0;
                } else if (isOp(ch)){
                    push();
                    tmp = new StringBuilder();
                    tmp.append(ch);
                    state = 11;
                } else if (isSharp(ch)){
                    push();
                    tmp = new StringBuilder();
                    tmp.append(ch);
                    state = 15;
                } else {
                    tmp.append(ch);
                    state = -1;
                }
                continue;
            }
            if (state == 9){ // oct
                if (isOctSymbols(ch)){
                    tmp.append(ch);
                    state = 10;
                } else if (isWhiteSpace(ch)){
                    push();
                    tmp = new StringBuilder();
                    tmp.append(ch);
                    state = -2; // wh
                    push();
                    tmp = new StringBuilder();
                    state = 0;
                } else if (isDelimiter(ch)){
                    push();
                    tmp = new StringBuilder();
                    tmp.append(ch);
                    state = -3; // delimiter
                    push();
                    tmp = new StringBuilder();
                    state = 0;
                } else {
                    tmp.append(ch);
                    state = -1;
                }
                continue;
            }
            if (state == 10){ // oct
                if (isOctSymbols(ch)){
                    tmp.append(ch);
                    state = 10;
                } else if (isWhiteSpace(ch)){
                    push();
                    tmp = new StringBuilder();
                    tmp.append(ch);
                    state = -2; // wh
                    push();
                    tmp = new StringBuilder();
                    state = 0;
                } else if (isDelimiter(ch)){
                    push();
                    tmp = new StringBuilder();
                    tmp.append(ch);
                    state = -3; // delimiter
                    push();
                    tmp = new StringBuilder();
                    state = 0;
                } else if (isOp(ch)){
                    push();
                    tmp = new StringBuilder();
                    tmp.append(ch);
                    state = 11;
                } else if (isSharp(ch)){
                    push();
                    tmp = new StringBuilder();
                    tmp.append(ch);
                    state = 15;
                } else {
                    tmp.append(ch);
                    state = -1;
                }
                continue;
            }
            if (state == 12){ // '...
                if (isQuotation1(ch)){
                    tmp.append(ch);
                    state = 14;
                } else {
                    tmp.append(ch);
                    state = 12;
                }
                continue;
            }
            if (state == 13){ // "...
                if (isQuotation2(ch)){
                    tmp.append(ch);
                    state = 14;
                } else {
                    tmp.append(ch);
                    state = 13;
                }
                continue;
            }
            if (state == 15){ // #...
                if (isNewLine(ch)){
                    push();
                    tmp = new StringBuilder();
                    tmp.append(ch);
                    state = -2; // wh
                    push();
                    tmp = new StringBuilder();
                    state = 0;
                } else {
                    tmp.append(ch);
                    state = 15;
                }
            }

        }

        push();
        tmp = new StringBuilder();
        tmp.append('\n');
        state = -2;
        push();
        tmp = new StringBuilder();
        state = 0;

    }


}
