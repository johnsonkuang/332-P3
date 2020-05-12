package tests.gitlab.aboveandbeyond;

import chess.board.ArrayBoard;
import chess.board.ArrayMove;

import chess.bots.JamboreeSearcher;

import org.junit.Before;
import org.junit.Test;
import tests.gitlab.SearcherTests;

public class JamboreeTests extends SearcherTests {

    @Before
    public void init() {
        STUDENT = new JamboreeSearcher<ArrayMove, ArrayBoard>();
    }


    @Test(timeout = 20000)
    public void testDepth2() {
        depth2();
    }

    @Test(timeout = 20000)
    public void testDepth3() {
        depth3();
    }

    @Test(timeout = 20000)
    public void testDepth4() {
        depth4();
    }

    @Test(timeout = 60000)
    public void testDepth5() {
        depth5();
    }

/* Optional
    @Test
    public void testDepth6() {
        depth6();
    }

 */
}
