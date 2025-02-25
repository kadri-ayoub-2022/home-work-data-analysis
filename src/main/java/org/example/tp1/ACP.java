package org.example.tp1;

import org.apache.commons.math3.linear.*;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.util.Arrays;

public class ACP {

    private static RealMatrix standardize(RealMatrix matrix) {
        int numRows = matrix.getRowDimension();
        int numCols = matrix.getColumnDimension();
        double[][] standardizedData = new double[numRows][numCols];

        for (int col = 0; col < numCols; col++) {
            double mean = Arrays.stream(matrix.getColumn(col)).average().orElse(0);
            double stdDev = Math.sqrt(Arrays.stream(matrix.getColumn(col))
                    .map(v -> Math.pow(v - mean, 2))
                    .sum() / numRows);

            for (int row = 0; row < numRows; row++) {
                standardizedData[row][col] = (matrix.getEntry(row, col) - mean) / stdDev;
            }
        }

        return MatrixUtils.createRealMatrix(standardizedData);
    }

    public static void main(String[] args) {



        double[][] data = {
                {5, 5, 4, 0, 1, 1},
                {4, 3, 3, 2, 2, 1},
                {2, 1, 2, 3, 2, 2},
                {5, 3, 5, 3, 4, 3},
                {4, 4, 3, 2, 3, 2},
                {2, 0, 1, 3, 1, 1},
                {3, 3, 4, 2, 4, 4},
                {1, 2, 1, 4, 3, 3},
                {0, 1, 0, 3, 1, 0},
                {2, 0, 1, 3, 1, 0},
                {1, 2, 1, 1, 0, 1},
                {4, 2, 4, 2, 1, 2},
                {3, 2, 3, 3, 2, 3},
                {1, 0, 0, 3, 2, 2},
                {2, 1, 1, 2, 3, 2}
        };


        RealMatrix matrix = MatrixUtils.createRealMatrix(data);
        RealMatrix standardizedData = standardize(matrix);



        RealMatrix correlationMatrix = new PearsonsCorrelation().computeCorrelationMatrix(standardizedData);


        EigenDecomposition eig = new EigenDecomposition(correlationMatrix);
        double[] eigenValues = eig.getRealEigenvalues();
        RealMatrix eigenVectors = eig.getV();


        int[] sortedIndices = sortIndicesDescending(eigenValues);
        Arrays.sort(eigenValues);
        reverse(eigenValues);


        double sumEigenValues = Arrays.stream(eigenValues).sum();
        double[] inertia = Arrays.stream(eigenValues).map(v -> (v / sumEigenValues) * 100).toArray();
        double[] cumulativeInertia = new double[inertia.length];
        cumulativeInertia[0] = inertia[0];
        for (int i = 1; i < inertia.length; i++) {
            cumulativeInertia[i] = cumulativeInertia[i - 1] + inertia[i];
        }


        System.out.println("Valeurs propres triées : " + Arrays.toString(eigenValues));
        System.out.println("Pourcentage d'inertie : " + Arrays.toString(inertia));
        System.out.println("Pourcentage cumulé    : " + Arrays.toString(cumulativeInertia));


        showEigenvalueHistogram(eigenValues, inertia, cumulativeInertia);
    }


    private static int[] sortIndicesDescending(double[] values) {
        Integer[] indices = new Integer[values.length];
        for (int i = 0; i < values.length; i++) {
            indices[i] = i;
        }
        Arrays.sort(indices, (i, j) -> Double.compare(values[j], values[i]));
        return Arrays.stream(indices).mapToInt(i -> i).toArray();
    }


    private static void reverse(double[] array) {
        for (int i = 0; i < array.length / 2; i++) {
            double temp = array[i];
            array[i] = array[array.length - 1 - i];
            array[array.length - 1 - i] = temp;
        }
    }


    private static void showEigenvalueHistogram(double[] eigenValues, double[] inertia, double[] cumulativeInertia) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String[] labels = {"PC1", "PC2", "PC3", "PC4", "PC5", "PC6"};

        for (int i = 0; i < eigenValues.length; i++) {
            dataset.addValue(eigenValues[i], "Valeurs propres", labels[i]);
            dataset.addValue(inertia[i], "Inertie (%)", labels[i]);
            dataset.addValue(cumulativeInertia[i], "Inertie cumulée (%)", labels[i]);
        }

        JFreeChart barChart = ChartFactory.createBarChart(
                "Histogramme des valeurs propres",
                "Composantes principales",
                "Valeurs",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new ChartPanel(barChart));
        frame.pack();
        frame.setVisible(true);
    }
}

