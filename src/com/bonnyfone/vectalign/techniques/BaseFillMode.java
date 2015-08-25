package com.bonnyfone.vectalign.techniques;

import android.support.v7.graphics.drawable.PathParser;
import com.bonnyfone.vectalign.PathNodeUtils;
import junit.framework.Assert;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by ziby on 07/08/15.
 */
public class BaseFillMode extends AbstractFillMode{

    @Override
    public void fillInjectedNodes(ArrayList<PathParser.PathDataNode> from, ArrayList<PathParser.PathDataNode> to) {
        PathParser.PathDataNode nodePlaceholder, nodeMaster;

        float[][] penPosFrom = PathNodeUtils.calculatePenPosition(from);
        float[][] penPosTo = PathNodeUtils.calculatePenPosition(to);
        float[][] penPosPlaceholder = null;
        float[][] penPosMaster = null;
        ArrayList<PathParser.PathDataNode> listMaster = null;
        ArrayList<PathParser.PathDataNode> listPlaceholder = null;

        for(int i=0; i < from.size(); i++){
            if(from.get(i).mType == PathNodeUtils.CMD_PLACEHOLDER){
                nodePlaceholder = from.get(i);
                nodeMaster = to.get(i);
                penPosPlaceholder = penPosFrom;
                penPosMaster = penPosTo;
                listMaster = to;
                listPlaceholder = from;
            }
            else if(to.get(i).mType == PathNodeUtils.CMD_PLACEHOLDER){
                nodePlaceholder = to.get(i);
                nodeMaster = from.get(i);
                penPosPlaceholder = penPosTo;
                penPosMaster = penPosFrom;
                listMaster = from;
                listPlaceholder = to;
            }
            else{
                nodeMaster = null;
                nodePlaceholder = null;
            }

            if(nodeMaster == null || nodePlaceholder == null)
                continue;


            nodePlaceholder.mType = nodeMaster.mType;
            nodePlaceholder.mParams = PathParser.copyOfRange(nodeMaster.mParams, 0, nodeMaster.mParams.length);

            float lastPlaceholderX, lastPlaceholderY, lastMasterX, lastMasterY;
            if(i>0){
                lastPlaceholderX = penPosPlaceholder[i-1][0];
                lastPlaceholderY = penPosPlaceholder[i-1][1];
                lastMasterX = penPosMaster[i-1][0];
                lastMasterY = penPosMaster[i-1][1];
            }
            else{
                lastPlaceholderX = 0;
                lastPlaceholderY = 0;
                lastMasterX = 0;
                lastMasterY = 0;
            }

            if(Character.toLowerCase(nodeMaster.mType) == 'z'){
                //Injecting a 'z' means we need to counterbalance the last path position with an extra 'm'
                PathParser.PathDataNode extraMoveNodePlaceholder = new PathParser.PathDataNode('M', new float[]{lastPlaceholderX, lastPlaceholderY});
                PathParser.PathDataNode extraMoveNodeMaster = new PathParser.PathDataNode('M', new float[]{lastMasterX, lastMasterY});

                listMaster.add(i, extraMoveNodeMaster);
                listPlaceholder.add(i, extraMoveNodePlaceholder);
                i++; //next item is already filled, we just added it

                //Recalculate penpos arrays //TODO do this more efficiently
                penPosFrom = PathNodeUtils.calculatePenPosition(from);
                penPosTo = PathNodeUtils.calculatePenPosition(to);

            }
            else if(Character.isLowerCase(nodePlaceholder.mType)){ // this is a relative movement. If we want to create extra nodes, we need to create neutral relative commands
                Arrays.fill(nodePlaceholder.mParams, 0.0f); //FIXME is good?
            }
            else{
                if(nodePlaceholder.mType == 'V'){
                    nodePlaceholder.mParams[0] =  lastPlaceholderY;
                }
                else if (nodePlaceholder.mType == 'H') {
                    nodePlaceholder.mParams[0] =  lastPlaceholderX;
                }
                else{
                    for(int j = 0; j< nodePlaceholder.mParams.length; j++){
                        nodePlaceholder.mParams[j++] = lastPlaceholderX;
                        nodePlaceholder.mParams[j] = lastPlaceholderY;
                    }
                }
            }

        }

        float[][] penPosFromAfter = PathNodeUtils.calculatePenPosition(from);
        float[][] penPosToAfter = PathNodeUtils.calculatePenPosition(to);

        if(        (penPosFromAfter[penPosFromAfter.length-1][0] == penPosFrom[penPosFrom.length-1][0])
                && (penPosFromAfter[penPosFromAfter.length-1][1] == penPosFrom[penPosFrom.length-1][1])
                && (penPosToAfter[penPosToAfter.length-1][0] == penPosTo[penPosTo.length-1][0])
                && (penPosToAfter[penPosToAfter.length-1][1] == penPosTo[penPosTo.length-1][1])){

            System.out.println("Injection completed correctly!");
        }
        else{
            System.out.println("PROBLEM during injection!");
            System.out.println("PenPos from");
            StringBuffer sb = new StringBuffer();
            int i = 0;
            for(float[] coord : penPosFrom){
                sb.append((++i)+"p. " +coord[0]+" , "+coord[1] +"\n") ;
            }
            System.out.println(sb.toString());


            System.out.println("PenPos fromAfter");
            sb = new StringBuffer();
            i = 0;
            for(float[] coord : penPosFromAfter){
                sb.append((++i)+"p. " +coord[0]+" , "+coord[1] +"\n") ;
            }
            System.out.println(sb.toString());


            System.out.println("PenPos to");
            sb = new StringBuffer();
            i = 0;
            for(float[] coord : penPosTo){
                sb.append((++i)+"p. " +coord[0]+" , "+coord[1] +"\n") ;
            }
            System.out.println(sb.toString());


            System.out.println("PenPos toAfter");
            sb = new StringBuffer();
            i = 0;
            for(float[] coord : penPosToAfter){
                sb.append((++i)+"p. " +coord[0]+" , "+coord[1] +"\n") ;
            }
            System.out.println(sb.toString());


        }

    }
}
