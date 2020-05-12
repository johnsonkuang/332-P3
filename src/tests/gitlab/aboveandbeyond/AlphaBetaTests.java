package tests.gitlab.aboveandbeyond;

import chess.board.ArrayBoard;
import chess.board.ArrayMove;

import chess.bots.AlphaBetaSearcher;

import org.junit.Before;
import org.junit.Test;
import tests.gitlab.SearcherTests;
import tests.gitlab.TestingInputs;

public class AlphaBetaTests extends SearcherTests {

	@Before
    public void init() {
	    STUDENT = new AlphaBetaSearcher<ArrayMove, ArrayBoard>();
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
