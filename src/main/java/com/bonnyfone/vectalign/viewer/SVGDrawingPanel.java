package com.bonnyfone.vectalign.viewer;

import android.support.v7.graphics.drawable.PathParser;
import com.bonnyfone.vectalign.PathNodeUtils;
import com.kitfox.svg.SVGCache;
import com.kitfox.svg.app.beans.SVGIcon;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URI;
import java.util.ArrayList;

/**
 * Created by ziby on 20/09/15.
 */
public class SVGDrawingPanel extends JPanel {

    private SVGIcon svg;
    private Thread animator;

    private String startPath;
    private String endPath;
    private PathParser.PathDataNode[] startPathNode;
    private PathParser.PathDataNode[] endPathNode;


    public SVGDrawingPanel() {
        super(true);
        svg = new SVGIcon();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        final int width = getWidth();
        final int height = getHeight();
        g.setColor(getBackground());
        g.fillRect(0, 0, width, height);
        svg.setAntiAlias(true);
        svg.paintIcon(this, g, 0, 0);
    }

    public void setPaths(String start, String end){
        startPath = start;
        endPath = end;
        startPathNode = PathParser.createNodesFromPathData(startPath);
        endPathNode = PathParser.createNodesFromPathData(endPath);
    }

    public void renderStep(float step){
        ArrayList<PathParser.PathDataNode> interp = new ArrayList<>();
        interp.clear();
        for(int i=0; i<startPathNode.length;i++){
            PathParser.PathDataNode n = startPathNode[i];
            PathParser.PathDataNode newNode = new PathParser.PathDataNode(n.mType, new float[n.mParams.length]);
            newNode.interpolatePathDataNode(n, endPathNode[i], step);
            interp.add(newNode);
        }
        String svgFrame = makeDynamicSVG(PathNodeUtils.pathNodesToString(interp));
        StringReader reader = new StringReader(svgFrame);
        final URI uri = SVGCache.getSVGUniverse().loadSVG(reader, "myImage"+step);
        //System.out.println("Updating with step " + f);
        svg.setSvgURI(uri);
        updateUI();
    }

    public void startAnimation(){
        animator = new Thread() {
            @Override
            public void run() {
                try {
                    float f=0;
                    int waitTime = 16;
                    float step = 0.005f;
                    while(!isInterrupted()){
                        renderStep(f);
                        sleep(waitTime);
                        f+=step;
                        if(f>1 || f < 0)
                            step = -step;
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


    public void stopAnimation(){
        if(animator != null & animator.isAlive())
            animator.interrupt();

        animator = null;
    }

    private String makeDynamicSVG(String data) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        pw.println("<svg version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\"" +
                "\t width=\"512px\" height=\"512px\" viewBox=\"0 0 512 512\" >\n" +
                "\t\t<path stroke=\"red\" stroke-width=\"2\" d=\"" + data + "\"/>\n" + //TODO https://developer.mozilla.org/en-US/docs/Web/SVG/Tutorial/Fills_and_Strokes
                "</svg>\n");
        pw.close();
        return sw.toString();
    }


    public void close() {
        if(animator != null && animator.isAlive())
            animator.interrupt();
    }
}
