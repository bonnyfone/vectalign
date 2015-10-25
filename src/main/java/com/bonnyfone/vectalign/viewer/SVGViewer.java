package com.bonnyfone.vectalign.viewer;

import com.bonnyfone.vectalign.Main;
import com.bonnyfone.vectalign.SVGParser;
import com.bonnyfone.vectalign.Utils;
import com.bonnyfone.vectalign.VectAlign;
import com.kitfox.svg.SVGDisplayPanel;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class SVGViewer extends javax.swing.JFrame implements WindowListener, SVGDrawingPanelListener {
    public static final long serialVersionUID = 273462773l;

    private int hgap = 0;
    private int vgap = 0;
    private int btnIconSize = 23;
    private Color svgPanelBackgroundColor = Color.WHITE;
    private String defaultSvgStrokeColor = "black";
    private String defaultSvgFillColor = SVGDrawingPanel.TRANSPARENT_COLOR;
    private int defaultStrokeSize = 3;

    private SVGDrawingPanel[] svgs;

    //Morphing
    private JPanel panelMorphing;
    private JSlider sliderMorphing;
    private JButton btnMorphAnimation;
    private ImageIcon icnPlay;
    private ImageIcon icnPause;
    private SVGDrawingPanel svgMorphing;

    //Input SVGs
    private JPanel panelInput;
    private JPanel panelConfig;
    private JButton btnEditFrom;
    private JButton btnEditTo;
    private JButton btnSvgFrom;
    private JButton btnSvgTo;

    //Controls
    private JPanel panelControls;

    //Output
    private JPanel panelOutput;

    private SVGDrawingPanel svgFrom;
    private SVGDrawingPanel svgTo;
    private BorderLayout mainLayout;

    private String[] result;

    public SVGViewer() {
        initIcons();
        initComponents();
        addListeners();
        pack();
        center();

        //FIXME show demo
        demo();
    }

    private void initIcons() {
        icnPlay = new ImageIcon((new ImageIcon(this.getClass().getResource("/icn_play.png")).getImage().getScaledInstance(btnIconSize, btnIconSize, java.awt.Image.SCALE_SMOOTH)));
        icnPause = new ImageIcon((new ImageIcon(this.getClass().getResource("/icn_pause.png")).getImage().getScaledInstance(btnIconSize, btnIconSize, java.awt.Image.SCALE_SMOOTH)));
    }

    private void center(){
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
    }

    private void demo() {
        String sampleA = "M 366.64407,65.08474 L 295.25723,225.79703 L 469.14417,252.02396 L 294.26984,270.55728 L 358.5001,434.26126 L 255.0126,292.0823 L 145.35594,429.55933 L 216.74277,268.84705 L 42.855843,242.62012 L 217.73016,224.08678 L 153.49991,60.38282 L 256.9874,202.56177 L 366.64407,65.08474";
        String sampleB = "M 91.09553,384.35547 L 91.09553,384.35547 L 109.221924,353.94055 L 127.34833,323.52557 L 145.47473,293.11063 L 163.60114,262.69568 L 181.72754,232.28073 L 199.85393,201.86578 L 217.98033,171.45084 L 236.10672,141.03589 L 254.23312,110.62095 L 405.71802,386.1926 L 91.09553,384.35547";

        svgFrom.setPath(sampleA);
        svgTo.setPath(sampleB);
        svgMorphing.setPaths(sampleA, sampleB);

        for (SVGDrawingPanel svgp : svgs) {
            svgp.renderStep(0.0f);
        }
    }

    private File showOpenFile(String title){
        final JFileChooser fc = new JFileChooser();
        fc.setDialogTitle(title);
        fc.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory())
                    return true;

                String extension = Utils.getExtension(f);
                if (extension != null) {
                    if (extension.equals("svg"))
                        return true;
                    else
                        return false;
                }

                return false;
            }

            @Override
            public String getDescription() {
                return "SVG images";
            }

        });

        int returnVal = fc.showOpenDialog(null);
        File file = null;
        if(returnVal == JFileChooser.APPROVE_OPTION)
            file = fc.getSelectedFile();

        return file;
    }

    private String showInputDialog(String title, String defaultText){
        JTextArea msg = new JTextArea(defaultText);
        msg.setLineWrap(true);
        msg.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(msg);
        scrollPane.setPreferredSize(new Dimension(600, 250));
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        int ris = JOptionPane.showConfirmDialog(null, scrollPane, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if(ris == JOptionPane.OK_OPTION)
            return msg.getText();
        else
            return defaultText;
    }

    private void reloadMorphing(){
        try{
            result = VectAlign.align(svgFrom.getPath(), svgTo.getPath(), VectAlign.Mode.BASE);
            svgMorphing.stopAnimation();
            svgMorphing.setPaths(result[0], result[1]);
            svgMorphing.reset();
            updateMorphingControls();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private void initComponents() {
        //Title
        setTitle(Main.NAME + " " + Main.VERSION);

        //Input SVG
        panelInput = new JPanel(new GridLayout(1, 2, 10, 0));
        JPanel panelFrom = new JPanel(new BorderLayout());
        JPanel panelTo = new JPanel(new BorderLayout());
        svgFrom = new SVGDrawingPanel();
        svgFrom.setPreferredSize(new Dimension(190, 200));
        svgTo = new SVGDrawingPanel();
        svgTo.setPreferredSize(new Dimension(190, 200));

        btnEditFrom = new JButton("Edit Path");
        btnEditTo = new JButton("Edit Path");
        btnSvgFrom = new JButton("Load SVG");
        btnSvgTo = new JButton("Load SVG");

        JPanel panelBtnFrom = new JPanel();
        JPanel panelBtnTo = new JPanel();
        panelBtnFrom.setLayout(new GridLayout(1, 2));
        panelBtnTo.setLayout(new GridLayout(1, 2));
        panelBtnFrom.add(btnEditFrom);
        panelBtnFrom.add(btnSvgFrom);
        panelBtnTo.add(btnEditTo);
        panelBtnTo.add(btnSvgTo);

        panelFrom.add(svgFrom, BorderLayout.CENTER);
        panelFrom.add(panelBtnFrom, BorderLayout.SOUTH);
        panelFrom.setBorder(new TitledBorder("Starting SVG/VD"));

        panelTo.add(svgTo, BorderLayout.CENTER);
        panelTo.add(panelBtnTo, BorderLayout.SOUTH);
        panelTo.setBorder(new TitledBorder("Ending SVG/VD"));

        //Controls
        panelControls = new JPanel(new BorderLayout(hgap, vgap));
        panelControls.setPreferredSize(new Dimension(400, 200));
        panelControls.setBorder(new TitledBorder("Configure morphing"));

        panelInput.add(panelFrom);
        panelInput.add(panelTo);

        panelConfig = new JPanel(new GridLayout(2, 1, 0, 10));
        panelConfig.add(panelInput);
        panelConfig.add(panelControls);

        //Morphing
        svgMorphing = new SVGDrawingPanel();
        svgMorphing.setListener(this);
        svgMorphing.setPreferredSize(new Dimension(400, 400));
        sliderMorphing = new JSlider(JSlider.HORIZONTAL, 0, 1000, 0);
        sliderMorphing.setPreferredSize(new Dimension(350, 25));
        btnMorphAnimation = new JButton(icnPlay);
        btnMorphAnimation.setPreferredSize(new Dimension(35,35));
        btnMorphAnimation.setBorderPainted(false);
        btnMorphAnimation.setBorder(null);
        btnMorphAnimation.setMargin(new Insets(0, 0, 0, 0));
        btnMorphAnimation.setContentAreaFilled(false);

        JPanel bottomMorphing = new JPanel(new FlowLayout());
        bottomMorphing.add(btnMorphAnimation);
        bottomMorphing.add(sliderMorphing);
        panelMorphing = new JPanel(new BorderLayout(hgap, vgap));
        panelMorphing.add(svgMorphing, BorderLayout.CENTER);
        panelMorphing.add(bottomMorphing, BorderLayout.SOUTH);
        panelMorphing.setBorder(BorderFactory.createTitledBorder("Morphing preview"));

        //Output
        panelOutput = new JPanel();
        panelOutput.setBorder(new TitledBorder("Results (aligned path sequences)"));
        panelOutput.setPreferredSize(new Dimension(800, 250));

        mainLayout = new BorderLayout(hgap, vgap);
        setLayout(mainLayout);
        getContentPane().add(panelConfig, BorderLayout.WEST);
        getContentPane().add(panelMorphing, BorderLayout.EAST);
        getContentPane().add(panelOutput, BorderLayout.SOUTH);
        setPreferredSize(new Dimension(900, 650));
        setBackground(Color.white);

        svgs = new SVGDrawingPanel[]{svgFrom, svgTo, svgMorphing};

        for (SVGDrawingPanel svgp : svgs) {
            svgp.setBackground(svgPanelBackgroundColor);
            svgp.setStrokeColor(defaultSvgStrokeColor);
            svgp.setFillColor(defaultSvgFillColor);
            svgp.setStrokeSize(defaultStrokeSize);
        }
    }

    private void handleSVGLoad(File f, SVGDrawingPanel svg){
        if(SVGParser.isSVGImage(f)) {
            svg.setPath(SVGParser.getPathDataFromSVGFile(f));
            svg.redraw();
            reloadMorphing();
        } else
            System.out.println("Error: not a valid SVG");
    }

    private void addListeners() {
        sliderMorphing.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if(sliderMorphing.isEnabled()){
                    svgMorphing.renderStep(((float)sliderMorphing.getValue())/1000.f);
                }
            }
        });

        btnMorphAnimation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleToggleAnimation();
            }
        });

        btnSvgFrom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSVGLoad(showOpenFile("Load SVG"), svgFrom);
            }
        });

        btnSvgTo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSVGLoad(showOpenFile("Load SVG"), svgTo);
            }
        });

        btnEditFrom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                svgFrom.setPath(showInputDialog("Edit STARTING path", svgFrom.getPath()));
                svgFrom.redraw();
                reloadMorphing();
            }
        });

        btnEditTo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                svgTo.setPath(showInputDialog("Edit ENDING path", svgTo.getPath()));
                svgTo.redraw();
                reloadMorphing();
            }
        });

        svgMorphing.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleToggleAnimation();
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });

        //Window
        addWindowListener(this);
    }

    private void handleToggleAnimation(){
        svgMorphing.toggleAnimation();
        updateMorphingControls();
    }

    private void updateMorphingControls(){
        if(svgMorphing.isAnimating()){
            btnMorphAnimation.setIcon(icnPause);
            sliderMorphing.setValue((int) (svgMorphing.getCurrentStep()*sliderMorphing.getMaximum()));
            sliderMorphing.setEnabled(false);
        }
        else{
            btnMorphAnimation.setIcon(icnPlay);
            sliderMorphing.setValue((int) (svgMorphing.getCurrentStep()*sliderMorphing.getMaximum()));
            sliderMorphing.setEnabled(true);
        }
    }

    @Override
    public void onMorphingChanges(float step) {
        updateMorphingControls();
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        svgMorphing.close();
        dispose();
        System.out.println("Exiting...");
    }

    @Override
    public void windowClosed(WindowEvent e) {
        System.out.println("[finish]");
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

    //MAIN
    public static void main(String args[]) {
        //Apply Nimbus LaF if possible
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        }
        catch(Exception e){}

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SVGViewer().setVisible(true);
            }
        });
    }
}
