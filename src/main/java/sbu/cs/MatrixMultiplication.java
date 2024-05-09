package sbu.cs;

import java.util.ArrayList;
import java.util.List;

public class MatrixMultiplication {

    // You are allowed to change all code in the BlockMultiplier class
    public static class BlockMultiplier implements Runnable {
        List<List<Integer>> tempMatrixProduct;
        List<List<Integer>> matrix_A;
        List<List<Integer>> matrix_B;

        int startingRow;
        int startingColumn;
        int p;
        int q;
        int r;

        public BlockMultiplier(List<List<Integer>> matrix_A, List<List<Integer>> matrix_B, int startingRow,int startingColumn) {
            this.matrix_A = matrix_A;
            this.matrix_B = matrix_B;
            this.startingColumn = startingColumn;
            this.startingRow = startingRow;
            this.p = matrix_A.size();
            this.q = matrix_B.size();
            this.r = matrix_B.getFirst().size();
            this.tempMatrixProduct = new ArrayList<>();
            for (int i = 0; i < p / 2; i++) {
                tempMatrixProduct.add(new ArrayList<Integer>());
            }
        }

        @Override
        public void run() {

            int temp = 0;

            for (int i = startingRow; i < startingRow + (p / 2); i++) {
                for (int j = startingColumn; j < startingColumn + (r / 2); j++) {
                    int sum = 0;
                    for (int k = 0; k < q; k++) {
                        sum += matrix_A.get(i).get(k) * matrix_B.get(k).get(j);
                    }
                    tempMatrixProduct.get(temp).add(sum);
                }
                temp++;
            }

        }
    }

    /*
    Matrix A is of the form p x q
    Matrix B is of the form q x r
    both p and r are even numbers
    */
    public static List<List<Integer>> ParallelizeMatMul(List<List<Integer>> matrix_A, List<List<Integer>> matrix_B) {

        List<List<Integer>> matrix_C = new ArrayList<>();
        for (int i = 0; i < matrix_A.size(); i++) {
            matrix_C.add(new ArrayList<Integer>());
        }

        BlockMultiplier firstQuarter = new BlockMultiplier(matrix_A, matrix_B, 0, 0);
        BlockMultiplier secondQuarter = new BlockMultiplier(matrix_A, matrix_B, 0, matrix_B.getFirst().size() / 2);
        BlockMultiplier thirdQuarter = new BlockMultiplier(matrix_A, matrix_B, matrix_A.size() / 2, 0);
        BlockMultiplier fourthQuarter = new BlockMultiplier(matrix_A, matrix_B, matrix_A.size() / 2, matrix_B.getFirst().size() / 2);

        Thread thread1 = new Thread(firstQuarter);
        Thread thread2 = new Thread(secondQuarter);
        Thread thread3 = new Thread(thirdQuarter);
        Thread thread4 = new Thread(fourthQuarter);

        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();

        try {
            thread1.join();
            thread2.join();
            thread3.join();
            thread4.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        for (int i = 0; i < matrix_A.size()/2; i++) {
            firstQuarter.tempMatrixProduct.get(i).addAll(secondQuarter.tempMatrixProduct.get(i));
            thirdQuarter.tempMatrixProduct.get(i).addAll(fourthQuarter.tempMatrixProduct.get(i));
        }
        firstQuarter.tempMatrixProduct.addAll(thirdQuarter.tempMatrixProduct);

        matrix_C = firstQuarter.tempMatrixProduct;

        return matrix_C;
    }

    public static void main(String[] args) {
        // Test your code here

    }
}
