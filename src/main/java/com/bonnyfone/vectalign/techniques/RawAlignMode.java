package com.bonnyfone.vectalign.techniques;

import android.support.v7.graphics.drawable.PathParser;
import com.bonnyfone.vectalign.PathNodeUtils;
import com.bonnyfone.vectalign.VectAlign;

import java.util.ArrayList;

/**
 * The raw alignment technique, which uses the NWAlignment approach
 */
public class RawAlignMode extends AbstractAlignMode {

    protected  ArrayList<PathParser.PathDataNode>[] doRawAlign(PathParser.PathDataNode[] from, PathParser.PathDataNode[] to){

        //Aligning
        boolean equivalent = false;
        int extraCloneNodes = 0;
        ArrayList<PathParser.PathDataNode> alignedFrom = null;
        ArrayList<PathParser.PathDataNode> alignedTo = null;

        for(int i = 0; i < VectAlign.MAX_ALIGN_ITERATIONS && !equivalent; i++){
            System.out.println(i + ". align iteration...");
            NWAlignment nw = new NWAlignment(PathNodeUtils.transform(from, extraCloneNodes, true), PathNodeUtils.transform(to, extraCloneNodes, true));
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

        if(!equivalent)
            return null;
        else
            return new ArrayList[]{alignedFrom, alignedTo};
    }

    @Override
    public ArrayList<PathParser.PathDataNode>[] align(PathParser.PathDataNode[] from, PathParser.PathDataNode[] to) {
        return doRawAlign(from, to);
    }
}
