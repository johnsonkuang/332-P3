package experiment;

import chess.board.ArrayBoard;
import chess.board.ArrayMove;
import chess.game.SimpleEvaluator;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CountNodes {

    public static int MAX_PLY = 6;

    public static void main(String[] args) throws IOException {
        List<String> fens = loadFens("src/experiment/fens.txt");
        // countMinimax(fens);
        countParallelMinimax(fens);
    }

    public static void countParallelMinimax(List<String> fens) {
        System.out.println("testing parallelized minimax");
        for (int ply = 1; ply < MAX_PLY; ply++) {
            System.out.println("PLY = " + ply);
            long[] nodesVisitedForAllFens = new long[fens.size()];
            for (int j = 0; j < fens.size(); j++) {
                String fen = fens.get(j);
                ExperimentalParallelSearcher<ArrayMove, ArrayBoard> searcher = newParallelSearcher(ply);
                ArrayBoard board = ArrayBoard.FACTORY.create().init(fen);
                searcher.getBestMove(board, 0, 0);
                nodesVisitedForAllFens[j] = ExperimentalParallelSearcher.nodes.get();
            }
            System.out.println("average nodes visited across all fens: " + getAverage(nodesVisitedForAllFens));
        }
    }

    public static void countMinimax(List<String> fens) {
        System.out.println("testing sequential minimax");
        for (int ply = 1; ply < MAX_PLY; ply++) {
            System.out.println("PLY = " + ply);
            long[] nodesVisitedForAllFens = new long[fens.size()];
            for (int j = 0; j < fens.size(); j++) {
                String fen = fens.get(j);
                ExperimentalSimpleSearcher<ArrayMove, ArrayBoard> searcher = newSimpleSearcher(ply);
                ArrayBoard board = ArrayBoard.FACTORY.create().init(fen);
                searcher.getBestMove(board, 0, 0);
                nodesVisitedForAllFens[j] = ExperimentalSimpleSearcher.nodes;
            }
            System.out.println("average nodes visited across all fens: " + getAverage(nodesVisitedForAllFens));
        }
    }

    public static long getAverage(long[] nodesVisitedForAllFens) {
        long sum = 0;
        for (long nodesVisited : nodesVisitedForAllFens) {
            sum += nodesVisited;
        }
        return sum / nodesVisitedForAllFens.length;
    }

    public static ExperimentalSimpleSearcher<ArrayMove, ArrayBoard> newSimpleSearcher(int ply) {
        ExperimentalSimpleSearcher<ArrayMove, ArrayBoard> searcher = new ExperimentalSimpleSearcher<ArrayMove, ArrayBoard>();
        searcher.setEvaluator(new SimpleEvaluator());
        searcher.setDepth(ply);
        return searcher;
    }

    public static ExperimentalParallelSearcher<ArrayMove, ArrayBoard> newParallelSearcher(int ply) {
        ExperimentalParallelSearcher<ArrayMove, ArrayBoard> searcher = new ExperimentalParallelSearcher<>();
        searcher.setEvaluator(new SimpleEvaluator());
        searcher.setDepth(ply);
        searcher.setCutoff(ply / 2);
        return searcher;
    }

    public static List<String> loadFens(String pathName) throws IOException {
        List<String> fens = new ArrayList<String>();
        File fensFile = new File(pathName);
        BufferedReader br = new BufferedReader(new FileReader(fensFile));
        String fen;
        while ((fen = br.readLine()) != null) {
            fens.add(fen);
        }
        return fens;
    }

}
