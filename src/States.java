import java.util.Arrays;

public class States {

    private static boolean isWhiteSpaceState(int state){
        return state == -2;
    }

    private static boolean isErrorState(int state){
        return state == -1;
    }

    private static boolean isDecimalState(int state){
        return state == 0 || state == 1 || state == 3;
    }

    private static boolean isFloatState(int state){
        return state == 2 || state == 4 || state == 5 || state == 6;
    }

    private static boolean isHexState(int state){
        return state == 8;
    }

    private static boolean isOctState(int state){
        return state == 10;
    }

    private static boolean isOperatorState(int state){
        return state == 11;
    }

    private static boolean isStringState(int state){
        return state == 14;
    }

    private static boolean isCommentState(int state){
        return state == 15;
    }

    private static boolean isIdentifierState(int state){
        return state == 16;
    }

    private static boolean isDelimiterState(int state){
        return state == -3;
    }

    public static String getLexemName(int state){
        if (isWhiteSpaceState(state)){ return "WHITE_SPACE"; }
        if (isErrorState(state)){ return "ERROR"; }
        if (isDecimalState(state)){ return "DECIMAL"; }
        if (isFloatState(state)){ return "FLOAT"; }
        if (isHexState(state)){ return "HEX"; }
        if (isOctState(state)){ return "OCT"; }
        if (isOperatorState(state)){ return "OPERATOR"; }
        if (isStringState(state)){ return "STRING"; }
        if (isCommentState(state)){ return "COMMENT"; }
        if (isIdentifierState(state)){ return "IDENTIFIER"; }
        if (isDelimiterState(state)){ return "DELIMITER"; }

        return "ERROR";

    }
}
