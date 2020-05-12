package tests.gitlab.ckpt1;

import chess.board.ArrayBoard;
import chess.board.ArrayMove;

import chess.bots.ParallelSearcher;

import org.junit.Before;
import org.junit.Test;
import tests.gitlab.SearcherTests;
import tests.gitlab.TestingInputs;

public class ParallelMinimaxTests extends SearcherTests {

    @Before
    public void init() {
        STUDENT = new ParallelSearcher<ArrayMove, ArrayBoard>();
    }


	@Test(timeout = 30000)
    public void testDepth2() {
        depth2();
    }

    @Test(timeout = 30000)
    public void testDepth3() {
        depth3();
    }

    @Test(timeout = 30000)
    public void testDepth4() {
        depth4();
    }
}
