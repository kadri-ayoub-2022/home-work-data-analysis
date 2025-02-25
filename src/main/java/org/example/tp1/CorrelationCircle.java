package org.example.tp1;
import org.apache.commons.math3.linear.*;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultXYDataset;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class CorrelationCircle {
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
        RealMatrix eigenVectors = eig.getV();

        double[] pc1 = eigenVectors.getColumn(0);
        double[] pc2 = eigenVectors.getColumn(1);

        showCorrelationCircle(pc1, pc2);
    }

    // Function to standardize data
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

    private static void showCorrelationCircle(double[] pc1, double[] pc2) {
        DefaultXYDataset dataset = new DefaultXYDataset();

        for (int i = 0; i < pc1.length; i++) {
            double[][] vectorData = {{0, pc1[i]}, {0, pc2[i]}};
            dataset.addSeries("Var " + (i + 1), vectorData);
        }

        JFreeChart chart = ChartFactory.createScatterPlot(
                "Correlation Circle",
                "PC1", "PC2",
                dataset
        );

        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setDomainAxis(new NumberAxis("PC1"));
        plot.setRangeAxis(new NumberAxis("PC2"));

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesLinesVisible(0, true);  // Draws arrows
        renderer.setSeriesShapesVisible(0, false);
        plot.setRenderer(renderer);

        JFrame frame = new JFrame("Correlation Circle");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new ChartPanel(chart));
        frame.pack();
        frame.setVisible(true);
    }
}


