package tests.gitlab;

import chess.board.ArrayBoard;
import chess.board.ArrayMove;
import chess.game.SimpleEvaluator;
import cse332.chess.interfaces.Move;
import cse332.chess.interfaces.Searcher;

import static org.junit.Assert.*;

import java.util.Arrays;

public abstract class SearcherTests {
    protected static Searcher<ArrayMove, ArrayBoard> STUDENT;

    protected ArrayMove getBestMove(String fen, Searcher<ArrayMove, ArrayBoard> searcher, int depth, int cutoff) {
        searcher.setDepth(depth);
        searcher.setCutoff(cutoff);

        return searcher.getBestMove(ArrayBoard.FACTORY.create().init(fen), 0, 0);
    }

    protected void checkResult(String fen, String[] valid, Searcher<ArrayMove, ArrayBoard> searcher, int depth, int cutoff) {
        ArrayMove result = getBestMove(fen, searcher, depth, cutoff);
        assertTrue(Arrays.asList(valid).contains(result.toString()));
    }  

    protected void depth(int d, int c) {
        STUDENT.setEvaluator(new SimpleEvaluator());
        for (Object[] input : TestingInputs.FENS_TO_TEST) { 
            checkResult((String)input[0], ((String[][])input[1])[d - 2], STUDENT, d, c);
        }
    }

    public void depth2() { depth(2, 1); }
    public void depth3() { depth(3, 1); }
    public void depth4() { depth(4, 2); }
    public void depth5() { depth(5, 3); }
    public void depth6() { depth(6, 3); }

}
