package com.bonnyfone.vectalign.viewer;

import android.support.v7.graphics.drawable.PathParser;
import com.bonnyfone.vectalign.PathNodeUtils;
import com.kitfox.svg.SVGCache;
import com.kitfox.svg.app.beans.SVGIcon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.*;
import java.net.URI;
import java.util.ArrayList;

/**
 * Created by ziby on 20/09/15.
 */
public class SVGDrawingPanel extends RoundedJPanel implements ComponentListener {

    public static String TRANSPARENT_COLOR = "none";

    private SVGDrawingPanelListener listener;

    //SVG preview
    private SVGIcon svg;
    private Thread animator;

    //Data
    private String startPath;
    private String endPath;
    private PathParser.PathDataNode[] startPathNode;
    private PathParser.PathDataNode[] endPathNode;
    private float currentStep = 0.0f;

    //Color, stroke
    private String strokeColor;
    private String fillColor;
    private int strokeSize = 2;

    private int width = 512;
    private int height = 522;
    private int viewBoxWidth = width;
    private int viewBoxHeight = height;
    private float courtesyNegativeSlop = -0.01f;
    private float courtesyScaleUp = 1.2f;
    private int viewBoxNegativeSlop = (int) (courtesyNegativeSlop * viewBoxWidth);

    private long frameSeed;

    public SVGDrawingPanel() {
        super();
        svg = new SVGIcon();
        svg.setScaleToFit(true);
        addComponentListener(this);
    }

    @Override
    public void paintComponent(Graphics g) {
        try{
            super.paintComponent(g);
        }
        catch (Exception e){}

        final int width = getWidth();
        final int height = getHeight();
        g.setColor(getBackground());
        //g.fillRect(0, 0, width, height);

        svg.setAntiAlias(true);
        svg.paintIcon(this, g, 0, 0);
    }

    @Override
    public void componentResized(ComponentEvent e) {
        System.out.println(e.getComponent().getSize());
        adaptSizeWithoutStretch(e.getComponent().getSize());
    }

    private void adaptSizeWithoutStretch(Dimension newSize){
        int newW = (int) newSize.getWidth();
        int newH = (int) newSize.getHeight();

        float ratio = ((float)width)/height;
        float newRatio = ((float)newW)/newH;

        if(newRatio > ratio)
            newW = (int) (newH*ratio);
        else
            newH = (int) (newW/ratio);

        svg.setPreferredSize(new Dimension(newW, newH));
    }

    @Override
    public void componentMoved(ComponentEvent e) {}

    @Override
    public void componentShown(ComponentEvent e) {}

    @Override
    public void componentHidden(ComponentEvent e) {}

    public void setPaths(String start, String end){
        setupPaths(start, end);
    }

    public void setPath(String path){
        setupPaths(path, null);
    }

    private void setupPaths(String start, String end){
        frameSeed = System.currentTimeMillis();
        if(start != null){
            startPath = start;
            startPathNode = PathParser.createNodesFromPathData(startPath);
        }
        if(end != null){
            endPath = end;
            endPathNode = PathParser.createNodesFromPathData(endPath);
        }

        //adapt svg size and viewport
        float maxX = 0;
        float maxY = 0;
        float maxValuesStartPath[] = PathNodeUtils.getMaxValues(startPathNode);
        float maxValuesEndPath[] = PathNodeUtils.getMaxValues(endPathNode);
        if(maxValuesStartPath != null){
            maxX = Math.max(maxValuesStartPath[0], maxX);
            maxY = Math.max(maxValuesStartPath[1], maxY);
        }
        if(maxValuesEndPath != null){
            maxX = Math.max(maxValuesEndPath[0], maxX);
            maxY = Math.max(maxValuesEndPath[1], maxY);
        }

        //FIXME for now, we keep a squared aspect ratio
        int newViewBox = (int) (courtesyScaleUp * Math.max(maxX, maxY));

        viewBoxWidth = newViewBox;
        viewBoxHeight = newViewBox;
        viewBoxNegativeSlop = (int) (courtesyNegativeSlop * newViewBox);

        System.out.println("SVG Viewport: " +viewBoxWidth + " x "+viewBoxHeight);
    }

    public void redraw(){
        renderStep(currentStep);
    }

    public void reset(){
        currentStep = 0;
        redraw();
    }

    public void renderStep(float step){
        //Interpolate morphing
        currentStep = step;
        String svgFrame = null;
        if(endPathNode != null){
            ArrayList<PathParser.PathDataNode> interp = new ArrayList<>();
            PathParser.PathDataNode n;
            for(int i=0; i<startPathNode.length; i++){
                n = startPathNode[i];
                PathParser.PathDataNode newNode = new PathParser.PathDataNode(n.mType, new float[n.mParams.length]);
                newNode.interpolatePathDataNode(n, endPathNode[i], step);
                interp.add(newNode);
            }
            svgFrame = makeDynamicSVG(PathNodeUtils.pathNodesToString(interp));
        }else{
            svgFrame = makeDynamicSVG(startPath);
        }

        //Rendering step using SVGSalamander...a bit tricky, need to be improved
        StringReader reader = new StringReader(svgFrame);
        URI uri = SVGCache.getSVGUniverse().loadSVG(reader, frameSeed + "_" + this.hashCode() + "_svg_frame"+step);
        svg.setSvgURI(uri);

        if(getListener() != null)
            getListener().onMorphingChanges(currentStep);

        //refresh
        updateUI();
    }

    public synchronized void toggleAnimation(){
        if(animator != null && animator.isAlive())
            stopAnimation();
        else
            startAnimation();
    }

    public boolean isAnimating(){
        return (animator != null && animator.isAlive());
    }

    public float getCurrentStep(){
        return currentStep;
    }

    public synchronized void startAnimation(){
        animator = new Thread() {
            @Override
            public void run() {
                try {
                    if(currentStep>1)
                        currentStep = 1;
                    else if(currentStep < 0)
                        currentStep = 0;

                    float f=currentStep;
                    int baseWaitTime = 16;
                    int longWaitTime = 256;
                    float step = 0.005f;
                    while(!isInterrupted()){
                        renderStep(f);
                        sleep(f <= 0 || f >= 1 ? longWaitTime : baseWaitTime);
                        f+=step;
                        if(f > 1 || f < 0){
                            step = -step;
                        }
                    }

                } catch (InterruptedException e) {
                }
                finally{
                    System.out.println("Animation interrupted.");
                }

            }
        };
        animator.start();
    }


    public synchronized void stopAnimation(){
        if(animator != null && animator.isAlive())
            animator.interrupt();

        animator = null;
    }

    private String makeDynamicSVG(String data) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        pw.println("<svg version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\"" +
                "\t width=\"" + width + "px\" height=\"" + height + "px\" viewBox=\"" + viewBoxNegativeSlop*3 + " " + viewBoxNegativeSlop + " "+ viewBoxWidth + " " + viewBoxHeight + "\" >\n" +
                "\t\t<path fill=\"" + getFillColor() + "\" stroke=\"" + getStrokeColor() + "\" stroke-width=\"" + getStrokeSize() + "\" d=\"" + data + "\"/>\n" + //TODO https://developer.mozilla.org/en-US/docs/Web/SVG/Tutorial/Fills_and_Strokes
                "</svg>\n");
        pw.close();
        return sw.toString();
    }

    public void close() {
        stopAnimation();
    }

    public String getStrokeColor(){
        if(strokeColor != null)
            return strokeColor;

        return TRANSPARENT_COLOR;
    }

    public String getFillColor(){
        if(fillColor != null)
            return fillColor;

        return TRANSPARENT_COLOR;
    }

    public int getStrokeSize(){
        return strokeSize;
    }

    public void setStrokeSize(int size){
        strokeSize = size;
    }

    public void setStrokeColor(String strokeColor) {
        this.strokeColor = strokeColor;
    }

    public void setFillColor(String fillColor) {
        this.fillColor = fillColor;
    }

    public String getPath(){
        return startPath;
    }

    public int getSVGWidth() {
        return width;
    }

    public void setSVGWidth(int width) {
        this.width = width;
    }

    public int getSVGHeight() {
        return height;
    }

    public void setSVGHeight(int height) {
        this.height = height;
    }

    public int getSVGViewBoxWidth() {
        return viewBoxWidth;
    }

    public void setSVGViewBoxWidth(int viewBoxWidth) {
        this.viewBoxWidth = viewBoxWidth;
    }

    public int getSVGViewBoxHeight() {
        return viewBoxHeight;
    }

    public void setSVGViewBoxHeight(int viewBoxHeight) {
        this.viewBoxHeight = viewBoxHeight;
    }

    public SVGDrawingPanelListener getListener() {
        return listener;
    }

    public void setListener(SVGDrawingPanelListener listener) {
        this.listener = listener;
    }
}
