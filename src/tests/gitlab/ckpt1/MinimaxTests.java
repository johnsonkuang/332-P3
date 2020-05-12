package tests.gitlab.ckpt1;

import chess.board.ArrayBoard;
import chess.board.ArrayMove;

import chess.bots.SimpleSearcher;

import org.junit.Before;
import org.junit.Test;
import tests.gitlab.SearcherTests;
import tests.gitlab.TestingInputs;

public class MinimaxTests extends SearcherTests {

    @Before
    public void init() {
        STUDENT = new SimpleSearcher<ArrayMove, ArrayBoard>();
    }

    @Test(timeout = 20000)
    public void testDepth2() {
        depth2();
    }

    @Test(timeout = 20000)
    public void testDepth3() {
        depth3();
    }
}
