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

public abstract class Clustering extends JPanel implements Runnable, ActionListener, AncestorListener {
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
    JPanel optionPane;
    JComponent canvas;
    char pointLegend = '.';
    private JTextField clusterNumberField;
    private JButton startButton;
    private JComboBox<String> datasetBox;

    /**
     * Constructor.
     */
    public Clustering() {
        if (dataset == null) {
            dataset = new double[datasetName.length][][];
            DelimitedTextParser parser = new DelimitedTextParser();
            parser.setDelimiter("[\t ]+");
            try {
                AttributeDataset data = parser.parse(datasetName[datasetIndex],
                        new java.io.File("/home/acid/work/kmeans/src/main/resources/one.txt"));
                        /*smile.data.parser.IOUtils.getTestDataFile("/home/acid/work/kmeans/src/main/resources/one.txt"));*/
                dataset[datasetIndex] = data.toArray(new double[data.size()][]);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Failed to load dataset.", "ERROR", JOptionPane.ERROR_MESSAGE);
                System.err.println(e);
            }
        }

        addAncestorListener(this);

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
        optionPane.add(startButton);
        optionPane.add(new JLabel("Dataset:"));
        optionPane.add(datasetBox);
        optionPane.add(new JLabel("K:"));
        optionPane.add(clusterNumberField);

        setLayout(new BorderLayout());
        add(optionPane, BorderLayout.NORTH);

        canvas = ScatterPlot.plot(dataset[datasetIndex], '.');
        add(canvas, BorderLayout.CENTER);
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
}

