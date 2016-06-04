package com.bonnyfone.vectalign.techniques;

import android.support.v7.graphics.drawable.PathParser;
import com.bonnyfone.vectalign.PathNodeUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Advanced technique which uses a "sub-aligning" approach
 */
public class SubAlignMode extends RawAlignMode {

    @Override
    public  ArrayList<PathParser.PathDataNode>[] align(PathParser.PathDataNode[] from, PathParser.PathDataNode[] to) {
        //Remove 'Z'
        ArrayList<PathParser.PathDataNode> transformFrom = PathNodeUtils.transform(from, 0, true);
        ArrayList<PathParser.PathDataNode> transformTo = PathNodeUtils.transform(to, 0, true);

        int fromSize = transformFrom.size();
        int toSize = transformTo.size();
        int min = Math.min(fromSize, toSize);
        int numberOfSubAligns = (int) Math.max(1, min/2);
        System.out.println("Estimated number of sub-alignments: "+numberOfSubAligns);
        int fromChunkSize = fromSize / numberOfSubAligns;
        int toChunkSize = toSize / numberOfSubAligns;

        ArrayList<PathParser.PathDataNode> finalFrom = new ArrayList<>();
        ArrayList<PathParser.PathDataNode> finalTo = new ArrayList<>();
        int startFromIndex, startToIndex, endFromIndex, endToIndex;
        for(int i=0; i < numberOfSubAligns; i++){
            if(i == numberOfSubAligns-1){
                endFromIndex = fromSize;
                endToIndex = toSize;
            }
            else{
                endFromIndex = Math.min(fromSize, (i + 1) * (fromChunkSize));
                endToIndex = Math.min(toSize, (i + 1) * (toChunkSize));
            }
            startFromIndex = i * fromChunkSize;
            startToIndex = i * toChunkSize;
            System.out.println("Subaligning FROM("+startFromIndex +","+endFromIndex+") - TO("+startToIndex +","+endToIndex+")");
            ArrayList<PathParser.PathDataNode>[] subAlign = doRawAlign(transformFrom.subList(startFromIndex, endFromIndex).toArray(new PathParser.PathDataNode[1]), transformTo.subList(startToIndex, endToIndex).toArray(new PathParser.PathDataNode[1]));
            finalFrom.addAll(subAlign[0]);
            finalTo.addAll(subAlign[1]);
        }

        return new ArrayList[]{finalFrom, finalTo};
    }

}
