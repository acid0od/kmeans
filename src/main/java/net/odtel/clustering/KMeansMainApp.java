/* ===========================================================================
 * Copyright (c) 2015 Comcast Corp. All rights reserved.
 * ===========================================================================
 *
 * Author: Alexander Ievstratiev
 * Created: 10/04/2017  4:14 PM
 */
package net.odtel.clustering;

import smile.clustering.KMeans;
import smile.plot.*;

import javax.swing.*;
import java.awt.*;

public class KMeansMainApp extends Clustering {

    public KMeansMainApp() {
    }

    public static void main(String argv[]) {
        Clustering demo = new KMeansMainApp();
        JFrame f = new JFrame("K-Means");
        f.setSize(new Dimension(1000, 1000));
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(demo);

        f.setVisible(true);
    }

    @Override
    public JComponent learn() {
        long clock = System.currentTimeMillis();
        KMeans kmeans = new KMeans(dataset[datasetIndex], clusterNumber, 100, 4);
        System.out.format("K-Means clusterings %d samples in %dms\n", dataset[datasetIndex].length, System.currentTimeMillis() - clock);

        PlotPanel panel = new PlotPanel();

        PlotCanvas plot = ScatterPlot.plot(dataset[datasetIndex], kmeans.getClusterLabel(), pointLegend, Palette.COLORS);

/*
        double[] x  = new double[5000];
        for (int j = 0; j < x.length; j++) {
            x[j] = Math.random();
        }

        double[] y  = new double[500];
        for (int j = 0; j < y.length; j++) {
            y[j] = j / 500.0;
}

        PlotCanvas plot = QQPlot.plot(x, y */
/*, kmeans.getClusterLabel(), pointLegend, Palette.COLORS*//*
);
*/

        plot.points(kmeans.centroids(), '@');
        panel.add(plot);

/*        double[][] data = new double[100][3];
        for (int j = 0; j < data.length; j++) {
            data[j][0] = Math.random();
            data[j][1] = Math.random();
            data[j][2] = Math.random();
        }

        PlotCanvas canvas3d = ScatterPlot.plot(data);
        canvas3d.setTitle("3D Scatter Plot");
        add(canvas3d);

        panel.add(canvas3d);*/
        return panel;
    }

    @Override
    public String toString() {
        return "K-Means";
    }

}
