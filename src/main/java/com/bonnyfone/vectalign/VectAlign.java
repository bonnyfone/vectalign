package com.bonnyfone.vectalign;

import android.support.v7.graphics.drawable.PathParser;
import com.bonnyfone.vectalign.techniques.*;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Class used to align VectorDrawable sequences in order to allow morphing animations between them.
 */
public class VectAlign {

    public static final int MAX_ALIGN_ITERATIONS = 5;

    /**
     * Alignment technique
     */
    public enum Mode {
        /**
         * Inject necessary elements by repeating existing ones
         */
        BASE(0),

        /**
         * Inject necessary elements and interpolates coordinates where possible
         */
        LINEAR(4),

        /**
         * Use sub-aligning and inject necessary elements by repeating existing ones
         */
        SUBALIGN_BASE(1),

        /**
         * Use sub-aligning and inject necessary elements and interpolates coordinates where possible
         */
        SUBALIGN_LINEAR(5);

        //TODO more technique

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
        System.out.println("Aligning mode: "+ alignMode);

        if(PathParser.canMorph(fromList, toList)){
            result = new String[]{from, to};
            System.out.println(" >> Paths are already morphable!!! Leaving sequences untouched <<");
        }
        else{
            /* ## ALIGN TECHNIQUE ## */

            ArrayList<PathParser.PathDataNode>[] aligns = null;
            switch (alignMode){
                case BASE:
                case LINEAR:
                    aligns = new RawAlignMode().align(fromList, toList);
                    break;
                case SUBALIGN_BASE:
                case SUBALIGN_LINEAR:
                    aligns = new SubAlignMode().align(fromList, toList);
                    break;

                //TODO handle more case here if needed
            }

            if(aligns == null){
                System.err.println("Unable to NW-align lists!");
                return null;
            }
            else
                System.out.println("Sequence aligned! (" + aligns[0].size()+ " elements)");


            /* ## FILL TECHNIQUE ## */

            AbstractFillMode fillMode = null;
            switch (alignMode){
                case BASE:
                case SUBALIGN_BASE:
                    fillMode = new BaseFillMode();
                    break;
                case LINEAR:
                case SUBALIGN_LINEAR:
                    fillMode = new LinearInterpolateFillMode();
                    break;

               //TODO handle more cases here if needed
            }

            //Fill
            fillMode.fillInjectedNodes(aligns[0], aligns[1]);

            //Simplify
            PathNodeUtils.simplify(aligns[0], aligns[1]);

            result = new String[]{PathNodeUtils.pathNodesToString(aligns[0]), PathNodeUtils.pathNodesToString(aligns[1])};
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
