package com.bonnyfone.vectalign.viewer;

import com.bonnyfone.vectalign.AnimatedVectorDrawableUtils;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;

/**
 * Created by ziby on 03/06/16.
 */
public class VectAlignExportDialog extends javax.swing.JDialog implements WindowListener{

    private static final int DEFAULT_EXPORT_SIZE = 300;

    private static File lastOuputDir;
    private static String lastDuration = "2000";
    private static String lastPrefix = "vectalign";
    private static boolean lastSupportVectorCompat;

    private final String[] solution;
    private final String strokeColor;
    private final String fillColor;
    private final int strokeWidth;
    private final boolean stroke;
    private final boolean fill;
    private final int viewPortWidth;
    private final int viewPortHeight;

    JPanel panelInput;
    JPanel panelExport;

    JLabel labelDir;
    JLabel labelPrefix;
    JLabel labelDuration;
    JLabel labelVectorCompat;

    JTextField txtDir;
    JTextField txtPrefix;
    JTextField txtDuration;
    JCheckBox checkVectorCompat;

    JButton btnOpenDir;
    JButton btnExport;

    public VectAlignExportDialog(String[] solution, String strokeColor, String fillColor, int strokeWidth, boolean stroke, boolean fill, int viewPortWidth, int viewPortHeight){
        this.solution = solution;
        this.strokeColor = strokeColor;
        this.fillColor = fillColor;
        this.strokeWidth = strokeWidth;
        this.stroke = stroke;
        this.fill = fill;
        this.viewPortWidth = viewPortWidth;
        this.viewPortHeight = viewPortHeight;
        init();
        bindListeners();
        pack();
        center();
    }

    private void center(){
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
    }

    private void init(){
        setTitle("Export options");
        setResizable(false);
        panelInput = new JPanel();
        panelExport = new JPanel();

        labelDir = new JLabel("Output dir: ");
        labelPrefix = new JLabel("Files prefix");
        labelDuration = new JLabel("Morph duration (ms):");
        labelVectorCompat = new JLabel("wnafee/vector-compat");

        txtDir = new JTextField(lastOuputDir != null ? lastOuputDir.getAbsolutePath() : "");
        txtDuration = new JTextField(lastDuration);
        txtPrefix = new JTextField(lastPrefix);
        checkVectorCompat = new JCheckBox("include vector-compat attributes", lastSupportVectorCompat);

        btnExport = new JButton("Export", new ImageIcon((new ImageIcon(this.getClass().getResource("/export-icon.png")).getImage().getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH))));
        btnOpenDir = new JButton("...");

        panelInput.setLayout(new GridBagLayout());

        //dir
        GridBagConstraints gc1 = new GridBagConstraints();
        gc1.fill = GridBagConstraints.HORIZONTAL;
        gc1.gridx = 0;
        gc1.gridy = 0;
        gc1.weightx = 0.1f;
        gc1.insets.left=10;
        gc1.insets.top=10;
        panelInput.add(labelDir, gc1);

        GridBagConstraints gc2 = new GridBagConstraints();
        gc2.fill = GridBagConstraints.HORIZONTAL;
        gc2.gridx = 1;
        gc2.gridy = 0;
        gc2.weightx = 0.85f;
        gc2.insets.top=10;
        panelInput.add(txtDir, gc2);

        GridBagConstraints gc3 = new GridBagConstraints();
        gc3.fill = GridBagConstraints.HORIZONTAL;
        gc3.gridx = 3;
        gc3.gridy = 0;
        gc3.weightx = 0.05f;
        gc3.insets.top=10;
        panelInput.add(btnOpenDir, gc3);

        //prefix
        GridBagConstraints gc4 = new GridBagConstraints();
        gc4.fill = GridBagConstraints.HORIZONTAL;
        gc4.gridx = 0;
        gc4.gridy = 1;
        gc4.insets.left=10;
        gc4.insets.top=10;
        panelInput.add(labelPrefix, gc4);

        GridBagConstraints gc5 = new GridBagConstraints();
        gc5.fill = GridBagConstraints.HORIZONTAL;
        gc5.gridx = 1;
        gc5.gridy = 1;
        gc5.gridwidth = 2;
        gc5.insets.top=10;
        panelInput.add(txtPrefix, gc5);

        //duration
        GridBagConstraints gc6 = new GridBagConstraints();
        gc6.fill = GridBagConstraints.HORIZONTAL;
        gc6.gridx = 0;
        gc6.gridy = 2;
        gc6.insets.left=10;
        gc6.insets.top=10;
        panelInput.add(labelDuration, gc6);

        GridBagConstraints gc7 = new GridBagConstraints();
        gc7.fill = GridBagConstraints.HORIZONTAL;
        gc7.gridx = 1;
        gc7.gridy = 2;
        gc7.gridwidth = 2;
        gc7.insets.top=10;
        panelInput.add(txtDuration, gc7);

        //support vect-compat
        GridBagConstraints gc8 = new GridBagConstraints();
        gc8.fill = GridBagConstraints.HORIZONTAL;
        gc8.gridx = 0;
        gc8.gridy = 3;
        gc8.insets.left=10;
        gc8.insets.right=10;
        gc8.insets.top=10;
        gc8.insets.bottom=20;
        panelInput.add(labelVectorCompat, gc8);

        GridBagConstraints gc9 = new GridBagConstraints();
        gc9.fill = GridBagConstraints.HORIZONTAL;
        gc9.gridx = 1;
        gc9.gridy = 3;
        gc9.gridwidth = 2;
        gc9.insets.top=10;
        gc9.insets.bottom=20;
        panelInput.add(checkVectorCompat, gc9);

        //Export
        panelExport.add(btnExport, BorderLayout.CENTER);
        panelExport.setAlignmentX(Component.CENTER_ALIGNMENT);

        //Pack
        setLayout(new BorderLayout());
        add(panelInput, BorderLayout.CENTER);
        add(panelExport, BorderLayout.SOUTH);

    }

    private void bindListeners(){
        btnExport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(export()){
                    dispose();
                }
            }
        });

        btnOpenDir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lastOuputDir = showOpenDir("Choose output directory", lastOuputDir);
                if(lastOuputDir != null)
                    txtDir.setText(lastOuputDir.getAbsolutePath());
            }
        });

    }

    private boolean export(){
        lastSupportVectorCompat = checkVectorCompat.isSelected();
        lastDuration = txtDuration.getText();
        if(lastOuputDir != null){
            //Chose prefix
            String defaultPrefix = "vectalign";
            String prefix = txtPrefix.getText();
            if(prefix == null || prefix.trim().equals("")){
                prefix = "";
            }
            lastPrefix = prefix.toLowerCase();

            //Export
            boolean success = AnimatedVectorDrawableUtils.export(lastOuputDir, lastDuration, lastSupportVectorCompat,
                    lastPrefix, solution, stroke, fill, strokeColor, strokeWidth, fillColor,
                    DEFAULT_EXPORT_SIZE, DEFAULT_EXPORT_SIZE, viewPortWidth, viewPortHeight);

            if(success)
                JOptionPane.showMessageDialog(VectAlignExportDialog.this, "Export completed ("+lastOuputDir.getAbsolutePath() +")", "VectAlign Export", JOptionPane.INFORMATION_MESSAGE);
            else
                JOptionPane.showMessageDialog(VectAlignExportDialog.this, "Unable to export files to "+lastOuputDir.getAbsolutePath() , "VectAlign Export", JOptionPane.ERROR_MESSAGE);

            return success;
        }
        else
            JOptionPane.showMessageDialog(VectAlignExportDialog.this, "Specify a valid ouput directory." , "VectAlign Export", JOptionPane.ERROR_MESSAGE);

        return false;
    }

    private File showOpenDir(String title, File lastFile){
        final JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setDialogTitle(title);
        if(lastFile != null)
            fc.setSelectedFile(lastFile);

        fc.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory())
                    return true;
                return false;
            }

            @Override
            public String getDescription() {
                return "Directory";
            }

        });

        int returnVal = fc.showOpenDialog(null);
        File file = null;
        if(returnVal == JFileChooser.APPROVE_OPTION)
            file = fc.getSelectedFile();

        return file;
    }

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }
}
