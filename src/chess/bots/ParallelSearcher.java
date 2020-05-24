package chess.bots;

import cse332.chess.interfaces.AbstractSearcher;
import cse332.chess.interfaces.Board;
import cse332.chess.interfaces.Evaluator;
import cse332.chess.interfaces.Move;
import chess.bots.SimpleSearcher;
import cse332.exceptions.NotYetImplementedException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;


public class ParallelSearcher<M extends Move<M>, B extends Board<M, B>> extends
        AbstractSearcher<M, B> {
    private static final ForkJoinPool POOL = new ForkJoinPool();

    public M getBestMove(B board, int myTime, int opTime) {
        List<M> moves = board.generateMoves();
        return POOL.invoke(new SearchTask(board, this.evaluator, moves, 0, moves.size(), this.cutoff, 0)).move;
    }

    class SearchTask extends RecursiveTask<BestMove<M>> {

        public static final int DIVIDE_CUTOFF = 3;
        private int depth;
        private int cutoff;
        private int lo;
        private int hi;
        private B board;
        private Evaluator<B> evaluator;
        private List<M> moves;

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
            if (hi - lo > DIVIDE_CUTOFF) { // Divide-and-conquer list of moves
                int mid = lo + ((hi - lo) / 2);
                SearchTask left = new SearchTask(board, evaluator, moves, lo, mid, cutoff, depth);
                SearchTask right = new SearchTask(board, evaluator, moves, mid, hi, cutoff, depth);
                left.fork();
                BestMove<M> rightMove = right.compute();
                BestMove<M> leftMove = left.join();
                if (rightMove.value > leftMove.value) {
                    return rightMove;
                }
                return leftMove;
            } else if (hi - lo == 1) { // actually do the move, continue forking from new List of moves
                M move = this.moves.get(lo);
                B newBoard = this.board.copy();
                newBoard.applyMove(move);
                depth++;
                if (depth + cutoff == ply) { // pass it off to minimax if current parallized depth + cutoff = total desired ply
                    BestMove<M> bestMove = SimpleSearcher.minimax(evaluator, newBoard, cutoff);
                    return bestMove;
                }
                List<M> newMoves = newBoard.generateMoves();
                SearchTask newTask = new SearchTask(board, evaluator, newMoves, 0, newMoves.size(), cutoff, depth + 1);
                return newTask.compute();
            } else { // Sequentially fork list of moves
                List<SearchTask> tasks = new ArrayList<SearchTask>();
                List<BestMove<M>> bestMoves = new ArrayList<BestMove<M>>();

                for (int i = lo; i < hi - 1; i++) {
                    SearchTask curr = new SearchTask(board, evaluator, moves, i, i + 1, cutoff, depth);
                    tasks.add(curr);
                    curr.fork();
                }
                SearchTask last = new SearchTask(board, evaluator, moves, hi - 1, hi, cutoff, depth);
                bestMoves.add(last.compute());
                for(SearchTask task: tasks){
                    bestMoves.add(task.join());
                }

                BestMove max = bestMoves.get(0);
                for(BestMove bM : bestMoves){
                    if(bM.value > max.value){
                        max = bM;
                    }
                }
                return max;
            }
        }
    }
}