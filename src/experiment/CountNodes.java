package experiment;

import chess.board.ArrayBoard;
import cse332.chess.interfaces.Board;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CountNodes {

    public static void main(String[] args) throws IOException {
        List<String> fens = loadFens("src/experiment/fens.txt");
        for (String fen : fens) {

        }
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
