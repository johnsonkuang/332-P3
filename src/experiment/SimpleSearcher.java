package experiment;

import chess.bots.BestMove;
import cse332.chess.interfaces.AbstractSearcher;
import cse332.chess.interfaces.Board;
import cse332.chess.interfaces.Evaluator;
import cse332.chess.interfaces.Move;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class should implement the minimax algorithm as described in the
 * assignment handouts.
 */
public class SimpleSearcher<M extends Move<M>, B extends Board<M, B>> extends
        AbstractSearcher<M, B> {

    public M getBestMove(B board, int myTime, int opTime) {
        /* Calculate the best move */
        BestMove<M> best = minimax(this.evaluator, board, ply);
        return best.move;
    }

    static <M extends Move<M>, B extends Board<M, B>> BestMove<M> minimax(
            Evaluator<B> evaluator, B board, int ply
    ) {
        Set<Board> seen = new HashSet<Board>();
        return minimax(evaluator, board, ply, seen);
    }

    static <M extends Move<M>, B extends Board<M, B>> BestMove<M> minimax (
            Evaluator<B> evaluator, B board, int depthLeft, Set<Board> seen) {
        if (depthLeft == 0) { // || seen.contains(board)
            return new BestMove<M>(evaluator.eval(board));
        }
        seen.add(board);

        List<M> moves = board.generateMoves();
        if (moves.isEmpty()) {
            return new BestMove<M>(board.inCheck() ? -evaluator.mate() + depthLeft : -evaluator.stalemate());
        }

        BestMove<M> bestMove = new BestMove<M>(-evaluator.infty());

        for (M move : moves) {
            board.applyMove(move);
            BestMove<M> currMove = minimax(evaluator, board, depthLeft - 1, seen).negate();
            board.undoMove();
            if (currMove.value > bestMove.value) {
                bestMove = currMove;
                bestMove.move = move;
            }
        }

        return bestMove;
    }

}