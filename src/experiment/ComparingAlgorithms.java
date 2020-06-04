package experiment;

import chess.board.ArrayBoard;
import chess.board.ArrayMove;
import chess.game.SimpleEvaluator;
import cse332.chess.interfaces.Searcher;


/**
 * measures the efficiencies of different sequential cutoffs for parallelSearcher
 *  time measured in nanoseconds
 *
 * Board: defines the board states that the searcher will be searching through
 * 4 trials are run for each cutoff
 *
 * Results are saved in ComparingAlgorithms.txt
 */
public class ComparingAlgorithms {
    public enum Board {
        START("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq -"),
        MIDDLE("r2qkb1r/pp4pp/2n1p3/2pp1p1Q/5B2/2NP4/PPP2PPP/R4RK1 b kq -"),
        END("2k3r1/p6p/2n5/3pp3/1pp5/2qPP3/P1P1K2P/R1R5 w - -");

        private final String board;

        Board(final String board){
            this.board = board;
        }

        public String getBoard() {return board;}
    }

    public static final int PARALLEL_CUTOFF = 3;

    public static void printMove(String fen, Searcher<ArrayMove, ArrayBoard> searcher, int depth, int cutoff, String name) {
        searcher.setDepth(depth);
        searcher.setCutoff(cutoff);
        searcher.setEvaluator(new SimpleEvaluator());

        long startTime = System.nanoTime();
        ArrayMove move = searcher.getBestMove(ArrayBoard.FACTORY.create().init(fen), 0, 0);
        long endTime = System.nanoTime();

        double totalTime = (endTime - startTime) / 1_000_000.0;
        //time in nanoseconds
        System.out.println("\t" + name + " board: " + move + " in " + totalTime);
    }
    public static void main(String[] args) {
        System.out.println("Parallel Searcher:");
        for(int trial = 1; trial < 5; trial++){
            System.out.println("Trial " + trial);
            for(Board b : Board.values()){
                Searcher<ArrayMove, ArrayBoard> searcher = new ExperimentalParallelSearcher<>();
                printMove(b.getBoard(), searcher, 5, PARALLEL_CUTOFF, b.name());
            }
        }
        System.out.println("Sequential Searcher:");
        for(int trial = 1; trial < 5; trial++){
            System.out.println("Trial " + trial);
            for(Board b : Board.values()){
                Searcher<ArrayMove, ArrayBoard> searcher = new ExperimentalParallelSearcher<>();
                printMove(b.getBoard(), searcher, 5, PARALLEL_CUTOFF, b.name());
            }
        }
    }


}
