import java.io.IOException;

public final class Variant10 {
    static final int DFA_STATES = 15;
    static final int NFA_STATES = 6;


    static final int[][] DFA = {
        {1, 2},    // q0
        {3, 6},    // q1  - прочитано "0"
        {9, 12},   // q2  - прочитано "1"
        {3, 5},    // q3  - первая пара 00, окончание 00
        {3, 5},    // q4  - первая пара 00, окончание 10
        {4, 5},    // q5  - первая пара 00, окончание на 1
        {7, 8},    // q6  - первая пара 01, окончание 01
        {7, 6},    // q7  - первая пара 01, окончание на 0
        {7, 8},    // q8  - первая пара 01, окончание 11
        {11, 10},  // q9  - первая пара 10, окончание 10
        {9, 10},   // q10 - первая пара 10, окончание на 1
        {11, 10},  // q11 - первая пара 10, окончание 00
        {14, 12},  // q12 - первая пара 11, окончание 11
        {14, 12},  // q13 - первая пара 11, окончание 01
        {14, 13},  // q14 - первая пара 11, окончание на 0
    };
    static final boolean[] DFA_FINAL = {
        false, false, false, true, false, false, true, false,
        false, true, false, false, true, false, false
    };

    static final boolean[][][] NFA = new boolean[NFA_STATES][2][NFA_STATES];
    static final boolean[] NFA_FINAL = {false, true, true, true, true, true};
    static {
        NFA[0][0][0] = true;
        NFA[0][1][0] = true;
        NFA[0][1][1] = true; 
        for (int from = 1; from < 5; from++) {
            NFA[from][0][from + 1] = true;
            NFA[from][1][from + 1] = true;
        }
    }

    static int symbolIndex(int code) {
        if (code == '0') return 0;
        if (code == '1') return 1;
        return -1;
    }

    static int stepDfa(int state, int symbol) {
        if (state < 0 || symbol < 0) return -1;
        return DFA[state][symbol];
    }

    static boolean acceptDfa(int state) {
        return state >= 0 && DFA_FINAL[state];
    }

    static boolean[] stepNfa(boolean[] current, int symbol) {
        boolean[] next = new boolean[NFA_STATES];
        if (symbol < 0) return next;
        for (int from = 0; from < NFA_STATES; from++) {
            if (!current[from]) continue;
            for (int to = 0; to < NFA_STATES; to++) {
                if (NFA[from][symbol][to]) next[to] = true;
            }
        }
        return next;
    }

    static boolean acceptNfa(boolean[] states) {
        for (int i = 0; i < NFA_STATES; i++) {
            if (states[i] && NFA_FINAL[i]) return true;
        }
        return false;
    }

    static boolean[] nfaStart() {
        boolean[] start = new boolean[NFA_STATES];
        start[0] = true;
        return start;
    }

    private static void printStates(boolean[] states) {
        System.out.print('{');
        boolean first = true;
        for (int i = 0; i < NFA_STATES; i++) {
            if (states[i]) {
                if (!first) System.out.print(", ");
                System.out.print('q');
                System.out.print(i);
                first = false;
            }
        }
        System.out.print('}');
    }

    private static void printStep(int step, int code, int dfa, boolean[] nfa) {
        System.out.print(step);
        System.out.print('\t');
        if (step == 0) System.out.print('-');
        else System.out.print((char) code);
        System.out.print('\t');
        if (dfa < 0) System.out.print("{}");
        else { System.out.print('q'); System.out.print(dfa); }
        System.out.print('\t');
        printStates(nfa);
        System.out.println();
    }

    private static void printResult(int dfa, boolean[] nfa, boolean valid) {
        System.out.print("DFA: ");
        System.out.println(valid && acceptDfa(dfa) ? "Accept" : "Reject");
        System.out.print("NFA: ");
        System.out.println(valid && acceptNfa(nfa) ? "Accept" : "Reject");
        if (!valid) System.out.println("Invalid symbol: alphabet is {0, 1}.");
        System.out.println();
    }


    public static void main(String[] args) throws IOException {
        System.out.println("Variant 10 | DFA and NFA | alphabet {0, 1}");
        System.out.println("One input per line. Empty line = epsilon. EOF = exit.");
        int dfa = 0, step = 0, code;
        boolean[] nfa = nfaStart();
        boolean valid = true, started = false, afterCR = false;
        while ((code = System.in.read()) != -1) {
            if (afterCR && code == '\n') { afterCR = false; continue; }
            afterCR = false;
            if (!started) {
                System.out.println("Step\tRead\tDFA\tNFA");
                printStep(0, '-', dfa, nfa);
                started = true;
            }
            if (code == '\r' || code == '\n') {
                printResult(dfa, nfa, valid);
                dfa = 0;
                step = 0;
                nfa = nfaStart();
                valid = true;
                started = false;
                afterCR = code == '\r';
                continue;
            }
            int symbol = symbolIndex(code);
            if (symbol < 0) valid = false;
            dfa = stepDfa(dfa, symbol);
            nfa = stepNfa(nfa, symbol);
            printStep(++step, code, dfa, nfa);
        }
        if (started) printResult(dfa, nfa, valid);
    }
}
