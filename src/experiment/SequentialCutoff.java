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
 * Results are saved in SeqCutoff.txt
 */
public class SequentialCutoff {
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

    public static ArrayMove getBestMove(String fen, Searcher<ArrayMove, ArrayBoard> searcher, int depth, int cutoff) {
        searcher.setDepth(depth);
        searcher.setCutoff(cutoff);
        searcher.setEvaluator(new SimpleEvaluator());

        return searcher.getBestMove(ArrayBoard.FACTORY.create().init(fen), 0, 0);
    }

    public static void printMove(String fen, Searcher<ArrayMove, ArrayBoard> searcher, int depth, int cutoff, int trial, String name) {
        long startTime = System.nanoTime();
        String botName = searcher.getClass().toString().split(" ")[1].replace("chess.bots.", "");
        long endTime = System.nanoTime();

        double totalTime = (endTime - startTime);
        //time in nanoseconds
        System.out.println("\t" + name + " board: " + getBestMove(fen, searcher, depth, cutoff) + " in " + totalTime);
    }
    public static void main(String[] args) {
        for(int cutoff = 0; cutoff < 5; cutoff++){
            for(int trial = 1; trial < 5; trial++){
                System.out.println("Trial " + trial + " for cutoff " + cutoff);
                for(Board b : Board.values()){
                    Searcher<ArrayMove, ArrayBoard> searcher = new ParallelSearcher<>();
                    printMove(b.getBoard(), searcher, 5, cutoff, trial, b.name());
                }
            }
        }
    }


}
