package experiment;

import chess.board.ArrayBoard;
import chess.board.ArrayMove;
import chess.bots.BestMove;
import chess.bots.SimpleSearcher;
import cse332.chess.interfaces.AbstractSearcher;
import cse332.chess.interfaces.Board;
import cse332.chess.interfaces.Evaluator;
import cse332.chess.interfaces.Move;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;


public class ExperimentalParallelSearcher<M extends Move<M>, B extends Board<M, B>> extends
        AbstractSearcher<M, B> {
    public static AtomicLong nodes = new AtomicLong(0);
    private static final ForkJoinPool POOL = new ForkJoinPool();

    public M getBestMove(B board, int myTime, int opTime) {
        nodes.set(0);
        List<M> moves = board.generateMoves();
        SearchTask searchTask = new SearchTask(board, this.evaluator, moves, 0, moves.size(), this.cutoff, 0);
        BestMove<M> bestMove = POOL.invoke(searchTask);
        return bestMove.move;
    }

    class SearchTask extends RecursiveTask<BestMove<M>> {
        public static final int DIVIDE_CUTOFF = 50;
        private final int depth;
        private final int cutoff;
        private final int lo;
        private final int hi;
        private final B board;
        private final Evaluator<B> evaluator;
        private final List<M> moves;

        public SearchTask(B board, Evaluator<B> evaluator, List<M> moves, int lo, int hi, int cutoff, int depth) {
            this.moves = moves;
            this.lo = lo;
            this.hi = hi;
            this.depth = depth;
            this.cutoff = cutoff;
            this.board = board;
            this.evaluator = evaluator;
        }

        @Override
        protected BestMove<M> compute() {
            if (hi - lo > DIVIDE_CUTOFF) { // Divide-and-conquer on the list of moves
                int mid = lo + ((hi - lo) / 2);
                SearchTask left = new SearchTask(board, evaluator, moves, lo, mid, cutoff, depth);
                SearchTask right = new SearchTask(board, evaluator, moves, mid, hi, cutoff, depth);
                left.fork();
                BestMove<M> rightMove = right.compute();
                BestMove<M> leftMove = left.join();
                return rightMove.value > leftMove.value ? rightMove : leftMove;
            } else if (hi - lo == 1) { // Now we only have one move left. Apply the move, so depth increases by 1.
                M move = moves.get(lo);
                B newBoard = board.copy();
                newBoard.applyMove(move);
                nodes.incrementAndGet();
                // Execute sequentially using SimpleSearcher's minimax iff newDepth + cutoff == ply
                if (depth + cutoff + 1 == ply) {
                    ExperimentalSimpleSearcher<ArrayMove, ArrayBoard> simpleSearcher = CountNodes.newSimpleSearcher(ply);
                    BestMove<M> bestMove = simpleSearcher.countingMinimax(evaluator, newBoard, cutoff).negate();
                    bestMove.move = move;
                    nodes.getAndAdd(simpleSearcher.instanceNodes);
                    return bestMove;
                }
                // Continue forking in parallel iff newDepth + cutoff != ply
                List<M> newMoves = newBoard.generateMoves();
                SearchTask newTask = new SearchTask(
                        newBoard, evaluator, newMoves, 0, newMoves.size(), cutoff, depth + 1
                );
                BestMove<M> bestMove = newTask.compute().negate();
                bestMove.move = move;
                return bestMove;
            } else { // Sequentially fork over the remaining list of moves if below DIVIDE_CUTOFF
                if (moves.isEmpty()) {
                    return new BestMove<M>(board.inCheck() ? -evaluator.mate() + depth : -evaluator.stalemate());
                }

                List<SearchTask> tasks = new ArrayList<SearchTask>();
                for (int i = 0; i < hi - lo - 1; i++) {
                    SearchTask newTask = new SearchTask(
                            board, evaluator, moves, lo + i, lo + i + 1, cutoff, depth
                    );
                    tasks.add(newTask);
                    newTask.fork();
                }
                SearchTask last = new SearchTask(board, evaluator, moves, hi - 1, hi, cutoff, depth);
                BestMove<M> bestMove = last.compute();

                for (SearchTask task : tasks) {
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