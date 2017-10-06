/* ===========================================================================
 * Copyright (c) 2015 Comcast Corp. All rights reserved.
 * ===========================================================================
 *
 * Author: Alexander Ievstratiev
 * Created: 10/04/2017  4:17 PM
 */
package net.odtel.clustering;

import smile.data.AttributeDataset;
import smile.data.parser.DelimitedTextParser;
import smile.plot.ScatterPlot;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public abstract class Clustering extends JPanel implements Runnable, ActionListener, AncestorListener {
    private static final int MIN = 0;
    private static final int MAX = 1;
    private static final int LEN = 2;
    private static final int X = 0;
    private static final int Y = 1;
    private static final int RESOLUTION = 300;

    private static final String ERROR = "Error";
    static double[][][] dataset = null;
    static int datasetIndex = 0;
    static int clusterNumber = 2;
    private static String[] datasetName = {
            "Gaussian/One", "Gaussian/Two", "Gaussian/Three",
            "Gaussian/Five", "Gaussian/Six", "Gaussian/Elongate",
            "NonConvex/Cross", "NonConvex/D4", "NonConvex/Face",
            "NonConvex/Pie", "NonConvex/Ring", "NonConvex/Sincos",
            "Chameleon/t4.8k", "Chameleon/t5.8k",
            "Chameleon/t7.10k", "Chameleon/t8.8k"
    };
    private static String[] datasource = {
            "clustering/gaussian/one.txt",
            "clustering/gaussian/two.txt",
            "clustering/gaussian/three.txt",
            "clustering/gaussian/five.txt",
            "clustering/gaussian/six.txt",
            "clustering/gaussian/elongate.txt",
            "clustering/nonconvex/cross.txt",
            "clustering/nonconvex/d4.txt",
            "clustering/nonconvex/face.txt",
            "clustering/nonconvex/pie.txt",
            "clustering/nonconvex/ring.txt",
            "clustering/nonconvex/sincos.txt",
            "clustering/chameleon/t4.8k.txt",
            "clustering/chameleon/t5.8k.txt",
            "clustering/chameleon/t7.10k.txt",
            "clustering/chameleon/t8.8k.txt"
    };
    public String[] labels;
    JPanel optionPane;
    JComponent canvas;
    char pointLegend = '#';
    private JTextField clusterNumberField;
    private JButton startButton;
    private JComboBox<String> datasetBox;

    /**
     * Constructor.
     */
    public Clustering() {
        /*if (dataset == null) {
            dataset = new double[datasetName.length][][];
            DelimitedTextParser parser = new DelimitedTextParser();
            parser.setDelimiter("[\t ]+");
            try {
                AttributeDataset data = parser.parse(datasetName[datasetIndex],
                        new java.io.File("/home/acid/work/kmeans/src/main/resources/one.txt"));
                        *//*smile.data.parser.IOUtils.getTestDataFile("/home/acid/work/kmeans/src/main/resources/one.txt"));*//*
                dataset[datasetIndex] = data.toArray(new double[data.size()][]);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Failed to load dataset.", "ERROR", JOptionPane.ERROR_MESSAGE);
                System.err.println(e);
            }
        }*/

        addAncestorListener(this);

        JButton csvImportButton = new JButton();
        csvImportButton.setAction(new AbstractAction(" Import CSV ") {
            public void actionPerformed(ActionEvent ae) {
                csvImport();
            }
        });

        startButton = new JButton("Start");
        startButton.setActionCommand("startButton");
        startButton.addActionListener(this);

        datasetBox = new JComboBox<>();
        for (int i = 0; i < datasetName.length; i++) {
            datasetBox.addItem(datasetName[i]);
        }
        datasetBox.setSelectedIndex(0);
        datasetBox.setActionCommand("datasetBox");
        datasetBox.addActionListener(this);

        clusterNumberField = new JTextField(Integer.toString(clusterNumber), 5);

        optionPane = new JPanel(new FlowLayout(FlowLayout.LEFT));
        optionPane.setBorder(BorderFactory.createRaisedBevelBorder());
        optionPane.add(csvImportButton);
        optionPane.add(startButton);
/*
        optionPane.add(new JLabel("Dataset:"));
        optionPane.add(datasetBox);
*/
        optionPane.add(new JLabel("K:"));
        optionPane.add(clusterNumberField);

        setLayout(new BorderLayout());
        add(optionPane, BorderLayout.NORTH);

      /*  canvas = ScatterPlot.plot(dataset[datasetIndex], '.');
        add(canvas, BorderLayout.CENTER);*/
    }

    /**
     * Execute the clustering algorithm and return a swing JComponent representing
     * the clusters.
     */
    public abstract JComponent learn();

    @Override
    public void run() {
        startButton.setEnabled(false);
        datasetBox.setEnabled(false);

        try {
            JComponent plot = learn();
            if (plot != null) {
                remove(canvas);
                canvas = plot;
                add(canvas, BorderLayout.CENTER);
            }
            validate();
        } catch (Exception ex) {
            System.err.println(ex);
        }

        startButton.setEnabled(true);
        datasetBox.setEnabled(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ("startButton".equals(e.getActionCommand())) {
            try {
                clusterNumber = Integer.parseInt(clusterNumberField.getText().trim());
                if (clusterNumber < 2) {
                    JOptionPane.showMessageDialog(this, "Invalid K: " + clusterNumber, ERROR, JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (clusterNumber > dataset[datasetIndex].length / 2) {
                    JOptionPane.showMessageDialog(this, "Too large K: " + clusterNumber, ERROR, JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid K: " + clusterNumberField.getText(), ERROR, JOptionPane.ERROR_MESSAGE);
                return;
            }

            Thread thread = new Thread(this);
            thread.start();
        } else if ("datasetBox".equals(e.getActionCommand())) {
            datasetIndex = datasetBox.getSelectedIndex();

            if (dataset[datasetIndex] == null) {
                DelimitedTextParser parser = new DelimitedTextParser();
                parser.setDelimiter("[\t ]+");
                try {
                    AttributeDataset data = parser.parse(datasetName[datasetIndex], smile.data.parser.IOUtils.getTestDataFile(datasource[datasetIndex]));
                    dataset[datasetIndex] = data.toArray(new double[data.size()][]);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Failed to load dataset.", "ERROR", JOptionPane.ERROR_MESSAGE);
                    System.err.println(ex);
                }
            }

            remove(canvas);
            if (dataset[datasetIndex].length < 500) {
                pointLegend = 'o';
            } else {
                pointLegend = '.';
            }
            canvas = ScatterPlot.plot(dataset[datasetIndex], pointLegend);
            add(canvas, BorderLayout.CENTER);
            validate();
        }
    }

    @Override
    public void ancestorAdded(AncestorEvent event) {
        clusterNumberField.setText(Integer.toString(clusterNumber));

        if (datasetBox.getSelectedIndex() != datasetIndex) {
            datasetBox.setSelectedIndex(datasetIndex);
            remove(canvas);
            if (dataset[datasetIndex].length < 500) {
                pointLegend = 'o';
            } else {
                pointLegend = '.';
            }
            canvas = ScatterPlot.plot(dataset[datasetIndex], pointLegend);
            add(canvas, BorderLayout.CENTER);
            validate();
        }
    }

    @Override
    public void ancestorMoved(AncestorEvent event) {
    }

    @Override
    public void ancestorRemoved(AncestorEvent event) {
    }

    private void enableToolBar(boolean enabled) {
        for (Component c : optionPane.getComponents()) {
            c.setEnabled(enabled);
        }
    }

    private void csvImport() {
        enableToolBar(false);
        double[][] minmaxlens = null;

        try {
            String currentDir = System.getProperty("user.dir");
            JFileChooser chooser = new JFileChooser(currentDir);
            int returnVal = chooser.showOpenDialog(optionPane);
            if (returnVal != JFileChooser.APPROVE_OPTION) {
                return;
            }
            minmaxlens = new double[][]{
                    {Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY},
                    {Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY},
                    {0d, 0d}
            };
            java.util.List points = new ArrayList();
            java.util.List<String> l = new ArrayList<>();
            java.util.List lines = new ArrayList();
            BufferedReader reader = new BufferedReader(new FileReader(chooser.getSelectedFile()));
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
                String[] pointString = line.split("[\t ]+");
                double[] point = new double[2];
                point[X] = Double.parseDouble(pointString[X].trim());
                point[Y] = Double.parseDouble(pointString[Y].trim());
                String label = pointString[2].trim();
                System.out.println(point[X] + ", " + point[Y] + ", " + label);
                l.add(label);
                points.add(point);
                if (point[X] < minmaxlens[MIN][X]) {
                    minmaxlens[MIN][X] = point[X];
                }
                if (point[Y] < minmaxlens[MIN][Y]) {
                    minmaxlens[MIN][Y] = point[Y];
                }
                if (point[X] > minmaxlens[MAX][X]) {
                    minmaxlens[MAX][X] = point[X];
                }
                if (point[Y] > minmaxlens[MAX][Y]) {
                    minmaxlens[MAX][Y] = point[Y];
                }
            }
            dataset = new double[datasetName.length][][];
            labels = l.stream().toArray(String[]::new);
            dataset[datasetIndex] = (double[][]) points.toArray(new double[points.size()][]);
            canvas = ScatterPlot.plot(dataset[datasetIndex], '.');

            add(canvas, BorderLayout.CENTER);
            this.repaint();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        } finally {
            this.repaint();
        }

        enableToolBar(true);
    }

}

