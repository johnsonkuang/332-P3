package experiment;

import chess.bots.BestMove;
import cse332.chess.interfaces.AbstractSearcher;
import cse332.chess.interfaces.Board;
import cse332.chess.interfaces.Evaluator;
import cse332.chess.interfaces.Move;

import java.util.List;

/**
 * This class should implement the minimax algorithm as described in the
 * assignment handouts.
 */
public class ExperimentalSimpleSearcher<M extends Move<M>, B extends Board<M, B>> extends
        AbstractSearcher<M, B> {

    public static long nodes;
    public long instanceNodes = 0;

    public M getBestMove(B board, int myTime, int opTime) {
        /* Calculate the best move */
        nodes = 0;
        BestMove<M> best = minimax(this.evaluator, board, ply);
        return best.move;
    }

    static <M extends Move<M>, B extends Board<M, B>> BestMove<M> minimax (
            Evaluator<B> evaluator, B board, int depth) {
        if (depth == 0) {
            return new BestMove<M>(evaluator.eval(board));
        }

        List<M> moves = board.generateMoves();
        if (moves.isEmpty()) {
            return new BestMove<M>(board.inCheck() ? -evaluator.mate() - depth : -evaluator.stalemate());
        }

        BestMove<M> bestMove = new BestMove<M>(-evaluator.infty());

        for (M move : moves) {
            board.applyMove(move);
            nodes++;
            BestMove<M> currMove = minimax(evaluator, board, depth - 1).negate();
            if (currMove.value > bestMove.value) {
                bestMove = currMove;
                bestMove.move = move;
            }
            board.undoMove();
        }

        return bestMove;
    }

    <M extends Move<M>, B extends Board<M, B>> BestMove<M> countingMinimax (
            Evaluator<B> evaluator, B board, int depth) {
        if (depth == 0) {
            return new BestMove<M>(evaluator.eval(board));
        }

        List<M> moves = board.generateMoves();
        if (moves.isEmpty()) {
            return new BestMove<M>(board.inCheck() ? -evaluator.mate() - depth : -evaluator.stalemate());
        }

        BestMove<M> bestMove = new BestMove<M>(-evaluator.infty());

        for (M move : moves) {
            board.applyMove(move);
            instanceNodes++;
            BestMove<M> currMove = minimax(evaluator, board, depth - 1).negate();
            if (currMove.value > bestMove.value) {
                bestMove = currMove;
                bestMove.move = move;
            }
            board.undoMove();
        }

        return bestMove;
    }

}