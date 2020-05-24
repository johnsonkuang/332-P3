package chess.bots;

import cse332.chess.interfaces.AbstractSearcher;
import cse332.chess.interfaces.Board;
import cse332.chess.interfaces.Evaluator;
import cse332.chess.interfaces.Move;
import chess.bots.SimpleSearcher;
import cse332.exceptions.NotYetImplementedException;

import java.util.List;
import java.util.concurrent.RecursiveTask;


public class ParallelSearcher<M extends Move<M>, B extends Board<M, B>> extends
        AbstractSearcher<M, B> {
    public M getBestMove(B board, int myTime, int opTime) {
        throw new NotYetImplementedException();
    }

    class SearchTask extends RecursiveTask<BestMove<M>> {

        public static final int DIVIDE_CUTOFF = 3;
        private static final int CUTOFF = ParallelSearcher.;
        private int lo;
        private int hi;
        private B board;
        private Evaluator<B> evaluator;
        private List<M> moves;

        public SearchTask(List<M> moves, int lo, int hi, int cutoff, B board, Evaluator<B> evaluator){
            this.moves = moves;
            this.lo = lo;
            this.hi = hi;
            this.cutoff = cutoff;
            this.board = board;
            this.evaluator = evaluator;
        }

        @Override
        protected BestMove<M> compute() {
            if (hi - lo < DIVIDE_CUTOFF) {

            }
        }
    }
}