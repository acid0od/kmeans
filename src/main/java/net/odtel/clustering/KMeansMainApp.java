/* ===========================================================================
 * Copyright (c) 2015 Comcast Corp. All rights reserved.
 * ===========================================================================
 *
 * Author: Alexander Ievstratiev
 * Created: 10/04/2017  4:14 PM
 */
package net.odtel.clustering;

import smile.clustering.KMeans;
import smile.plot.Label;
import smile.plot.*;

import javax.swing.*;
import java.awt.*;

public class KMeansMainApp extends Clustering {

    public KMeansMainApp() {
    }

    public static void main(String argv[]) {
        try {
            // Set System L&F
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException e) {
            // handle exception
        } catch (ClassNotFoundException e) {
            // handle exception
        } catch (InstantiationException e) {
            // handle exception
        } catch (IllegalAccessException e) {
            // handle exception
        }

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
        KMeans kmeans = new KMeans(dataset[datasetIndex], clusterNumber, 100, 10);
        System.out.format("K-Means clusterings %d samples in %dms\n", dataset[datasetIndex].length, System.currentTimeMillis() - clock);

        PlotPanel panel = new PlotPanel();

        PlotCanvas plot = ScatterPlot.plot(dataset[datasetIndex], kmeans.getClusterLabel(), pointLegend, Palette.COLORS);

        for (int i = 0; i < labels.length; i++) {

            Label label = new Label(Integer.toString(i + 1), 0, 1, 0, dataset[datasetIndex][i]);
            plot.add(label);
        }

        plot.points(kmeans.centroids(), 'S');
        panel.add(plot);

        return panel;
    }

    @Override
    public String toString() {
        return "K-Means";
    }

}
