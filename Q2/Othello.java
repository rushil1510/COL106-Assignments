import java.io.*;
import java.util.*;

public class Othello {
    int turn;
    int winner;
    int board[][];
    private static int xDir[] = {-1,-1,0,1,1,1,0,-1};
    private static int yDir[] = {0,1,1,1,0,-1,-1,-1};

    public Othello(String filename) throws Exception {
        File file = new File(filename);
        Scanner sc = new Scanner(file);
        turn = sc.nextInt();
        board = new int[8][8];
        for(int i = 0; i < 8; ++i) {
            for(int j = 0; j < 8; ++j){
                board[i][j] = sc.nextInt();
            }
        }
        winner = -1;
        sc.close();
    }
    public boolean is_terminal(int[][] board){
        int cnt=0;
        for(int i=0; i<8; i++){
            for(int j=0; j<8; j++){
                if(board[i][j]!=-1){cnt++;}
            }
        }
        if(cnt==64){return true;}
        else{return false;}
    }

    public int getScore(int[][] matrix){
        int num_black_tiles=0;
        int num_white_tiles=0;
        for(int i=0; i<8; i++){
            for(int j=0; j<8; j++){
                if(matrix[i][j]==0){num_black_tiles++;}
                if(matrix[i][j]==1){num_white_tiles++;};
            }
        }
        if(turn==0){
            return num_black_tiles-num_white_tiles;
        }
        else{
            return num_white_tiles-num_black_tiles;
        }
    }


    public int boardScore() {
        return getScore(board);
    }

    public int bestMove(int k) {
        int opponent=0;
        if(turn==0){opponent=1;}
        ArrayList<ArrayList<Integer>> moveList=getPossibleMoves(board, turn);
        if(is_terminal(board)|| moveList.size()==0){
            return -1;
        }
        else{
            int bestMoveval=-99999;
            int bestX=moveList.get(0).get(0); 
            int bestY=moveList.get(0).get(1);
        
            for(int i=0; i<moveList.size(); i++){
                int[][]tempboard=getBoardCopy();
                boolean a=makeMove(tempboard, turn, moveList.get(i).get(0), moveList.get(i).get(1));
                if(a){
                int val=minimaxvalue(tempboard, turn, opponent, k, 1);
                if(val>bestMoveval){
                    bestMoveval=val;
                    bestX=moveList.get(i).get(0);
                    bestY=moveList.get(i).get(1);
                }}
            }
            return 8*bestX+bestY;
        }
    }

    public ArrayList<Integer> fullGame(int k) {
        ArrayList<Integer> gameMoves=new ArrayList<>();
        while(getPossibleMoves(board, turn).size()!=0){
            gameMoves.add(bestMove(k));
            if(turn==0){turn++;}
            else{turn--;}
            if(getPossibleMoves(board, turn).size()==0){
                if(turn==0){turn++;}
                else{turn--;}
            }
        }
        winner=getWinner();
        return gameMoves;
    }

    public int[][] getBoardCopy() {
        int copy[][] = new int[8][8];
        for(int i = 0; i < 8; ++i)
            System.arraycopy(board[i], 0, copy[i], 0, 8);
        return copy;
    }

    public int getWinner() {
        int num_black=0;
        int num_white=0;
        for(int i=0; i<8; i++){
            for(int j=0; j<8; j++){
                if(board[i][j]==0){num_black++;}
                if(board[i][j]==1){num_white++;}
            }
        }
        if(num_black-num_white>0){return 0;}
        else if(num_black-num_white<0){return 1;}
        return -1;
    }

    public int getTurn() {
        return turn;
    }

    public ArrayList<ArrayList<Integer>> getPossibleMoves(int[][] board, int player){
        ArrayList<ArrayList<Integer>> moves=new ArrayList<>();
        int index = 0;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (board[row][col] == -1) {
                    if (isValidMove(board, player, row, col)) {
                        moves.add(new ArrayList<Integer>());
                        moves.get(index).add(row);
                        moves.get(index).add(col);
                        index++;
                    }
                }
            }
        }
        return moves;
    }

    public boolean isValidMove(int[][] board, int player, int x, int y) {
        if(x < 0 || x >= 8 || y < 0 || y >= 8 || board[x][y] != -1){
            return false;
        }
        boolean movePossible = false;
        for(int i = 0; i < xDir.length; i++){
            int xStep = xDir[i];
            int yStep = yDir[i];
            int currentX = x + xStep;
            int currentY = y + yStep;
            int count = 0;
            // count of opponent's pieces encountered 
            while(currentX >= 0 && currentX < 8 && currentY >= 0 && currentY < 8){
                // Empty cell 
                if(board[currentX][currentY] == -1){
                    break;
                }
                else if(board[currentX][currentY] != player){
                    currentX += xStep;
                    currentY += yStep;
                    count++;
                }
                else{
                    // conversion is possible 
                    if(count > 0){
                        movePossible = true;
                    }
                    break;
                }
            }
        }
        return movePossible;    
    }

    public boolean makeMove(int[][] board, int player, int x, int y) {
        // Place the player's piece at the specified row and column
        // if(x < 0 || x >= 8 || y < 0 || y >= 8 || board[x][y] != -1){
        //     return false;
        // }
        boolean movePossible = false;
        for(int i = 0; i < xDir.length; i++){
            int xStep = xDir[i];
            int yStep = yDir[i];
            int currentX = x + xStep;
            int currentY = y + yStep;
            int count = 0;
            // count of opponent's pieces encountered 
            while(currentX >= 0 && currentX < 8 && currentY >= 0 && currentY < 8){
                if(board[currentX][currentY] == -1){
                    break;
                }
                else if(board[currentX][currentY] != player){
                    currentX += xStep;
                    currentY += yStep;
                    count++;
                }
                else{
                    // conversion is possible 
                    if(count > 0){
                        movePossible = true;
                        int convertX = currentX - xStep;
                        int convertY = currentY - yStep;
                        while(convertX != x || convertY != y){
                            board[convertX][convertY] = player;
                            convertX = convertX - xStep;
                            convertY = convertY - yStep;
                        }
                        count=0;
                    }
                    break;
                }
            }
        }
        if(movePossible){
            board[x][y] =player;
        }
        return movePossible;
    }

    public int minimaxvalue(int[][] board, int ogplayer, int currplayer, int depth, int searchPly){
        if(searchPly==depth){
            return getScore(board);
        }
        int opponent=0;
        if(currplayer==0){
            opponent=1;
        }
        ArrayList<ArrayList<Integer>> movelist=getPossibleMoves(board, currplayer);
        if(movelist.size()==0){
            return minimaxvalue(board, ogplayer, opponent, depth, searchPly+1);
        }
        else{
            int bestMoveval=-99999;
            if(ogplayer!=currplayer){
                bestMoveval=99999;
            }
            for(int i=0; i<movelist.size(); i++){
                int[][] tempboard=getBoardCopy(board);
                int val=minimaxvalue(tempboard, ogplayer, opponent, depth, searchPly+1);
                if(ogplayer==currplayer){
                    if(val>bestMoveval){
                        bestMoveval=val;
                    }
                }
                else{
                    if(val<bestMoveval){bestMoveval=val;}
                }
            }
            return bestMoveval;
        }
    }
    public int[][] getBoardCopy(int[][] board) {
        int[][] copy = new int[board.length][];
 
        for (int i = 0; i < board.length; i++) {
            copy[i] = new int[board[i].length];
            System.arraycopy(board[i], 0, copy[i], 0, board[i].length);
        }
 
        return copy;
    }

}