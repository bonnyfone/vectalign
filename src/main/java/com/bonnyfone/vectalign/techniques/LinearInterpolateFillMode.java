package com.bonnyfone.vectalign.techniques;

import android.support.v7.graphics.drawable.PathParser;
import com.bonnyfone.vectalign.PathNodeUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Fill mode which interpolates linearly over consecutive sequences of injected nodes of the same type (when possible).
 */
public class LinearInterpolateFillMode extends BaseFillMode {

    @Override
    public void fillInjectedNodes(ArrayList<PathParser.PathDataNode> from, ArrayList<PathParser.PathDataNode> to) {
        //Execute a base injection first
        super.fillInjectedNodes(from, to);

        //Interpolates
        interpolateResultList(from);
        interpolateResultList(to);

        //Check validity again
        penPosFromAfter = PathNodeUtils.calculatePenPosition(from);
        penPosToAfter = PathNodeUtils.calculatePenPosition(to);
        checkPenPosValidity();
    }

    /**
     * Apply interpolation on the result list (where possible)
     * @param list
     */
    private void interpolateResultList(ArrayList<PathParser.PathDataNode> list){
        if(list == null || list.size() <= 2)
            return;

        float[][] listPenPos = PathNodeUtils.calculatePenPosition(list);

        //find subsequence of interpolatable commands
        ArrayList<PathParser.PathDataNode> subList = new ArrayList<>();

        int size = list.size();
        PathParser.PathDataNode currentNode = null;
        int i=0;
        while(i < size-1){  //TODO O(n^2), improve this
            currentNode = list.get(i);

            if(!isInterpolatableCommand(currentNode.mType)){
                i++;
                continue;
            }

            boolean validSequence = true;
            int k = i;
            for(int j=i; j < size && validSequence; j++ ){
                if(currentNode.mType == list.get(j).mType){
                    k = j;
                    if(!Arrays.equals(currentNode.mParams, list.get(j).mParams))
                        break;
                }
                //TODO else if there's another compatible command (a sequence of L can interpolate with a V or an H
                else
                    validSequence = false;
            }

            if(k-i > 2){
                interpolateSubList(list.subList(i, k+1));
            }

            i++;
        }
    }

    /**
     * Check if a command is interpolatable
     * @param command
     * @return
     */
    public boolean isInterpolatableCommand(char command){
        if(command == 'L' || command == 'V' || command == 'H')
            return true;

        return false;
    }


    /**
     * Interpolate a list of commands using two delimiter nodes
     * @param list
     */
    private void interpolateSubList(List<PathParser.PathDataNode> list){
        if(list == null || list.size() <=2)
            return;

        int size = list.size();
        PathParser.PathDataNode nodeFrom = list.get(0);
        PathParser.PathDataNode nodeTo = list.get(size - 1);

        float step = 1.0f/(size-1);
        float fraction = 0.0f;
        for(PathParser.PathDataNode current : list){
            current.interpolatePathDataNode(nodeFrom, nodeTo, fraction);
            fraction += step;
        }
    }
}
