package chess.bots;

import cse332.chess.interfaces.AbstractSearcher;
import cse332.chess.interfaces.Board;
import cse332.chess.interfaces.Evaluator;
import cse332.chess.interfaces.Move;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;


public class ParallelSearcher<M extends Move<M>, B extends Board<M, B>> extends
        AbstractSearcher<M, B> {
    private static final ForkJoinPool POOL = new ForkJoinPool();
    private static final int DIVIDE_CUTOFF = 100;

    public M getBestMove(B board, int myTime, int opTime) {
        List<M> moves = board.generateMoves();
        SearchTask<M, B> searchTask = new SearchTask<>(board, this.evaluator, moves, 0, moves.size(), this.cutoff, 0, this.ply);
        BestMove<M> bestMove = POOL.invoke(searchTask);
        return bestMove.move;
    }

    private static class SearchTask<M extends Move<M>, B extends Board<M, B>> extends RecursiveTask<BestMove<M>> {
        private static final int LAST_MOVE = 1;
        private final int depth;
        private final int cutoff;
        private final int lo;
        private final int hi;
        private final B board;
        private final Evaluator<B> evaluator;
        private final List<M> moves;
        private final int ply;

        public SearchTask(B board, Evaluator<B> evaluator, List<M> moves, int lo, int hi, int cutoff, int depth, int ply) {
            this.moves = moves;
            this.lo = lo;
            this.hi = hi;
            this.depth = depth;
            this.cutoff = cutoff;
            this.board = board;
            this.evaluator = evaluator;
            this.ply = ply;
        }

        @Override
        protected BestMove<M> compute() {
            if (hi - lo > DIVIDE_CUTOFF) { // Divide-and-conquer on the list of moves
                int mid = lo + ((hi - lo) / 2);
                SearchTask<M, B> left = new SearchTask<>(board, evaluator, moves, lo, mid, cutoff, depth, ply);
                SearchTask<M, B> right = new SearchTask<>(board, evaluator, moves, mid, hi, cutoff, depth, ply);
                left.fork();
                BestMove<M> rightMove = right.compute();
                BestMove<M> leftMove = left.join();
                return rightMove.value > leftMove.value ? rightMove : leftMove;
            } else if (hi - lo == LAST_MOVE) { // Now we only have one move left. Apply the move, so depth increases by 1.
                M move = moves.get(lo);
                B newBoard = board.copy();
                newBoard.applyMove(move);
                // Execute sequentially using SimpleSearcher's minimax iff newDepth + cutoff == ply
                if (depth + cutoff + 1 == ply) {
                    BestMove<M> bestMove = SimpleSearcher.minimax(evaluator, newBoard, cutoff).negate();
                    bestMove.move = move;
                    return bestMove;
                }
                // Continue forking in parallel iff newDepth + cutoff != ply
                List<M> newMoves = newBoard.generateMoves();
                SearchTask newTask = new SearchTask(
                        newBoard, evaluator, newMoves, 0, newMoves.size(), cutoff, depth + 1, ply
                );
                BestMove<M> bestMove = newTask.compute().negate();
                bestMove.move = move;
                return bestMove;
            } else { // Sequentially fork over the remaining list of moves if below DIVIDE_CUTOFF
                if (moves.isEmpty()) {
                    return new BestMove<>(board.inCheck() ? -evaluator.mate() + depth : -evaluator.stalemate());
                }

                List<SearchTask<M, B>> tasks = new ArrayList<>();
                for (int i = 0; i < hi - lo - 1; i++) {
                    SearchTask<M, B> newTask = new SearchTask<>(
                            board, evaluator, moves, lo + i, lo + i + 1, cutoff, depth, ply
                    );
                    tasks.add(newTask);
                    newTask.fork();
                }
                SearchTask<M, B> last = new SearchTask(board, evaluator, moves, hi - 1, hi, cutoff, depth, ply);
                BestMove<M> bestMove = last.compute();

                for (SearchTask<M, B> task : tasks) {
                    BestMove<M> currMove = task.join();
                    if (currMove.value > bestMove.value) {
                        bestMove = currMove;
                    }
                }

                return bestMove;
            }
        }
    }
}