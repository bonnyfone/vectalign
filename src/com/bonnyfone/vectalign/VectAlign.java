package com.bonnyfone.vectalign;

import android.support.v7.graphics.drawable.PathParser;
import com.bonnyfone.vectalign.techniques.AbstractFillMode;
import com.bonnyfone.vectalign.techniques.BaseFillMode;
import com.bonnyfone.vectalign.techniques.NWAlignment;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Class used to align VectorDrawable sequences in order to allow morphing animations between them.
 */
public class VectAlign {

    private static final int MAX_ALIGN_ITERATIONS = 5;

    /**
     * Alignment technique
     */
    public enum Mode {
        /**
         * Inject necessary elements by repeating existing ones
         */
        BASE(1),

        /**
         * Inject necessary elements by interpolating between existing ones
         */
        LINEAR(2);


        private int mode;
        Mode(int mode){
            this.mode = mode;
        }
    }

    /**
     * @param from The source path represented in a String
     * @param to The target path represented in a String
     * @return whether the <code>from</code> can morph into <code>to</code>
     */
    public static boolean canMorph(String from, String to) {
        return PathParser.canMorph(PathParser.createNodesFromPathData(from), PathParser.createNodesFromPathData(to));
    }


    /**
     * Align two VectorDrawable sequences in order to make them <i>morphable</i>
     * @param from
     * @param to
     * @param alignMode
     * @return
     */
    public static String[] align(String from, String to, Mode alignMode){
        String result[] = null;

        //read data as nodes
        PathParser.PathDataNode[] fromList = PathParser.createNodesFromPathData(from);
        PathParser.PathDataNode[] toList = PathParser.createNodesFromPathData(to);

        System.out.println("Sequences sizes: " + fromList.length  + " / " + toList.length);

        if(PathParser.canMorph(fromList, toList)){
            result = new String[]{from, to};
        }
        else{
            //NW Align
            boolean equivalent = false;
            int extraCloneNodes = 0;
            ArrayList<PathParser.PathDataNode> alignedFrom = null;
            ArrayList<PathParser.PathDataNode> alignedTo = null;

            for(int i = 0; i < MAX_ALIGN_ITERATIONS && !equivalent; i++){
                System.out.println(i + ". align iteration...");
                NWAlignment nw = new NWAlignment(PathNodeUtils.transform(fromList, extraCloneNodes, true), PathNodeUtils.transform(toList, extraCloneNodes, true));
                nw.align();
                alignedFrom = nw.getAlignedFrom();
                alignedTo = nw.getAlignedTo();
                equivalent = PathNodeUtils.isEquivalent(nw.getOriginalFrom(), nw.getAlignedFrom()) && PathNodeUtils.isEquivalent(nw.getOriginalTo(), nw.getAlignedTo());

                if(equivalent){
                    System.out.println("Alignment found!");
                    System.out.println(PathNodeUtils.pathNodesToString(nw.getAlignedFrom(), true));
                    System.out.println(PathNodeUtils.pathNodesToString(nw.getAlignedTo(), true));
                }
                extraCloneNodes++;
            }

            if(!equivalent){
                System.err.println("Unable to NW-align lists!");
                return null;
            }
            else
                System.out.println("Sequence aligned! (" + alignedFrom.size()+ " elements)");

            AbstractFillMode fillMode = null;
            switch (alignMode){
                case BASE:
                    fillMode = new BaseFillMode();
                    break;

                case LINEAR:
                    break;
            }

            //Fill
            fillMode.fillInjectedNodes(alignedFrom, alignedTo);

            //Simplify
            PathNodeUtils.simplify(alignedFrom, alignedTo);

            result = new String[]{PathNodeUtils.pathNodesToString(alignedFrom), PathNodeUtils.pathNodesToString(alignedTo)};
        }
        return result;
    }


    // ###### DEBUG ######
    // ###################


    static void printPathData(PathParser.PathDataNode[] data){
        int i = 1;
        for(PathParser.PathDataNode node : data){
            System.out.println((i++) +".\t"+ node.mType + " " + Arrays.toString(node.mParams));
        }
    }

    static void printPathData(ArrayList<PathParser.PathDataNode> data){
        int i = 1;
        for(PathParser.PathDataNode node : data){
            System.out.println((i++) +".\t"+ node.mType + " " + Arrays.toString(node.mParams));
        }
    }
}
