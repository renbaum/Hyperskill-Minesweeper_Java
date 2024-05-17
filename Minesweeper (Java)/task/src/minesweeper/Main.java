package minesweeper;

import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

class Matrix {
    public char[][] matrix;

    public Matrix(char[][] matrix) {
        this.matrix = matrix;
    }

    public Matrix clone() {
        Matrix m = new Matrix(this.matrix.length, this.matrix[0].length);
        for(int i = 0; i < matrix.length; i++){
            for(int j = 0; j < matrix[i].length; j++){
                m.matrix[i][j] = this.matrix[i][j];
            }
        }
        return m;
    }

    public Matrix inverse(){
        Matrix m = new Matrix(this.matrix.length, this.matrix[0].length);
        for(int i = 0; i < matrix.length; i++){
            for(int j = 0; j < matrix[i].length; j++){
                m.matrix[i][j] = this.matrix[i][j] > 0 ? 0 : 'X';
            }
        }
        return m;
    }

    public void setMask(Matrix mask){
        for(int i = 0; i < mask.matrix.length; i++){
            for(int j = 0; j < mask.matrix[i].length; j++){
                this.matrix[i][j] = mask.matrix[i][j] != 0 ? this.matrix[i][j] : 0;
            }
        }
    }

    public boolean compare(Matrix m) {
        for(int i = 0; i < matrix.length; i++){
            for(int j = 0; j < matrix[i].length; j++){
                boolean b1 = matrix[i][j] == 0 ? false : true;
                boolean b2 = m.matrix[i][j] == 0 ? false : true;
                if(b1 ^= b2) return false;
            }
        }
        return true;
    }

    public Matrix(int x, int y){
        this.matrix = new char[x][y];
    }

    public void add(Matrix source)
    {
        for(int i = 0; i < matrix.length; i++){
            for(int j = 0; j < matrix[i].length; j++){
                if(source.matrix[i][j] != 0){
                    this.matrix[i][j] = source.matrix[i][j];
                }
            }
        }
    }

    public void addWithMask(Matrix source, Matrix mask){
        for(int i = 0; i < matrix.length; i++){
            for(int j = 0; j < matrix[i].length; j++){
                if(source.matrix[i][j] != 0){
                    if(mask.matrix[i][j] != 0){
                        this.matrix[i][j] = source.matrix[i][j];
                    }
                }
            }
        }
    }

    public void fillMatrix(char filler){
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                matrix[i][j] = filler;
            }
        }
    }

    public void printMatrix(){
        String str = " |";
        String str2 = "-|";
        for(int i = 0; i < matrix.length; i++){
            str += (i+1);
            str2 += "-";
        }
        str += "|";
        str2 += "|";
        System.out.println(str);
        System.out.println(str2);

        for (int i = 0; i < matrix.length; i++) {
            str = (i + 1) + "|";
            for (int j = 0; j < matrix[i].length; j++) {
                str += matrix[i][j];
                //System.out.printf("%c", matrix[i][j]);
            }
            str += "|";
            System.out.println(str);
        }
        System.out.println(str2);
    }

    int checkIndexY(int y){
        if(y < 0) y = 0;
        if(y >= matrix[0].length) y = matrix[0].length -1;
        return y;
    }

    int checkIndexX(int x){
        if(x < 0) x = 0;
        if(x >= matrix.length) x = matrix.length -1;
        return x;
    }

    Matrix getSubMatrix(int x1, int x2, int y1, int y2){
        x1 = checkIndexX(x1);
        x2 = checkIndexX(x2);
        y1 = checkIndexY(y1);
        y2 = checkIndexY(y2);

        Matrix subMatrix = new Matrix(x2 - x1 + 1, y2 - y1 + 1);
        for(int i = x1; i <= x2; i++){
            for(int j = y1; j <= y2; j++){
                subMatrix.matrix[i - x1][j - y1] = matrix[i][j];
            }
        }
        return subMatrix;
    }

    int countChar(char character){
        int count = 0;

        for(int i = 0; i < matrix.length; i++){
            for(int j = 0; j < matrix[i].length; j++){
                if (matrix[i][j] == character) {
                    count++;
                }
            }
        }
        return count;
    }
}

class MineField{
    private Matrix matrixMines;
    private Matrix matrixEmpty;
    private Matrix matrixCount;
    private Matrix matrixMark;
    private Matrix matrixVisible;
    private Matrix matrixFillVisible;

    public MineField(int sizeX, int sizeY, int numMines){
        matrixMines = new Matrix(sizeX, sizeY);
        matrixEmpty = new Matrix(sizeX, sizeY);
        matrixCount = new Matrix(sizeX, sizeY);
        matrixMark = new Matrix(sizeX, sizeY);
        matrixVisible = new Matrix(sizeX, sizeY);
        matrixFillVisible = new Matrix(sizeX, sizeY);

        matrixEmpty.fillMatrix('.');
        matrixFillVisible.fillMatrix('/');
        deployMines(numMines);
        countMines();
    }

    private void deployMines(int numMines){
        Random rand  = new Random(System.currentTimeMillis());


        for(int i = 0; i < numMines; i++){
            int x = rand.nextInt(9);
            int y = rand.nextInt(9);
            if (matrixMines.matrix[x][y] == 'X') {
                i--;
                continue;
            }
            matrixMines.matrix[x][y] = 'X';
        }

    }

    public boolean setMine(int x, int y){
        /*if(matrixCount.matrix[x][y] > '0'){
            System.out.println("There is a number here!");
            return false;
        }*/
        if(matrixMark.matrix[x][y] == 0){
            matrixMark.matrix[x][y] = '*';
        }else{
            matrixMark.matrix[x][y] = 0;
        }
        return true;
    }

    static public int level = 0;

    public boolean setVisible(int x, int y){
        if(matrixMines.matrix[x][y] == 'X') return true;
        if(matrixVisible.matrix[x][y] > 0) return true;
        matrixVisible.matrix[x][y] = '/';
        if(matrixCount.matrix[x][y] != 0) return true;

        for(int i = x -1; i <= x + 1; i++){
            if(i < 0 || i >= matrixCount.matrix.length) continue;
            for(int j = y -1; j <= y + 1; j++){
                if(i == x && j == y) continue;
                if(j < 0 || j >= matrixCount.matrix[0].length) continue;
                setVisible(i, j);
            }
        }
        return true;
    }

    private void countMines(){
        for (int i = 0; i < matrixMines.matrix.length; i++) {
            for (int j = 0; j < matrixMines.matrix[i].length; j++) {
                Matrix subMatrix = matrixMines.getSubMatrix(i - 1, i + 1, j - 1, j + 1);
                int count = subMatrix.countChar('X');
                if(count > 0 && matrixMines.matrix[i][j] != 'X') matrixCount.matrix[i][j] = (char)(count + '0');
            }
        }
    }

    public void Print(boolean showMines){
        Matrix newMatrix = (Matrix)matrixEmpty.clone();
        newMatrix.addWithMask(matrixFillVisible, matrixVisible);
        newMatrix.addWithMask(matrixCount, matrixVisible);
        matrixMark.setMask(matrixVisible.inverse());
        newMatrix.add(matrixMark);
        if(showMines){
            newMatrix.add(matrixMines);
        }
        newMatrix.printMatrix();
    }

    public boolean checkWin(){
        return matrixMark.compare(matrixMines);
    }

    public boolean checkAllFree(){
        Matrix newMatrix = matrixVisible.inverse();
        return newMatrix.compare(matrixMines);
    }

    public boolean checkMine(int x, int y){
        if (matrixMines.matrix[x][y] == 'X') return true;
        return false;
    }
}

public class Main {
    public static void main(String[] args) {
        // write your code here

        Scanner scan = new Scanner(System.in);
        System.out.print("How many mines do you want on the field?");
        int numMines = scan.nextInt();

        MineField mineField = new MineField(9, 9, numMines);
        boolean dead = false;
        boolean win = false;

        do {
            mineField.Print(false);
            boolean ok = false;
            do {
                System.out.print("Set/unset mines marks or claim a cell as free:");
                int y = scan.nextInt();
                int x = scan.nextInt();
                String option = scan.nextLine();
                option = option.trim();
                switch(option){
                    case "mine":
                        ok = mineField.setMine(x-1, y-1);
                        break;
                    case "free":
                        ok = mineField.setVisible(x-1, y-1);
                        dead = mineField.checkMine(x-1, y-1);
                        break;
                }
            }while(!ok);
            System.out.println();
            win = mineField.checkWin() || mineField.checkAllFree();
        }while(!win && !dead);
        mineField.Print(true);
        if(dead)
        {
            System.out.println("You stepped on a mine and failed!");
        }else {
            System.out.println("Congratulations! You found all mines!");
        }
    }
}
