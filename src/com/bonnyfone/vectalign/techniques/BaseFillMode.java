package com.bonnyfone.vectalign.techniques;

import android.support.v7.graphics.drawable.PathParser;
import com.bonnyfone.vectalign.PathNodeUtils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by ziby on 07/08/15.
 */
public class BaseFillMode extends AbstractFillMode{

    @Override
    public void fillInjectedNodes(ArrayList<PathParser.PathDataNode> from, ArrayList<PathParser.PathDataNode> to) {

        PathParser.PathDataNode nodePlaceholder, nodeMaster;
        ArrayList<PathParser.PathDataNode> listPlaceholder = null;
        ArrayList<PathParser.PathDataNode> listMaster = null;
        for(int i=0; i < from.size(); i++){
            if(from.get(i).mType == PathNodeUtils.CMD_PLACEHOLDER){
                nodePlaceholder = from.get(i);
                nodeMaster = to.get(i);
                listPlaceholder = from;
                listMaster = to;
            }
            else if(to.get(i).mType == PathNodeUtils.CMD_PLACEHOLDER){
                nodePlaceholder = to.get(i);
                nodeMaster = from.get(i);
                listPlaceholder = to;
                listMaster = from;
            }
            else{
                nodeMaster = null;
                nodePlaceholder = null;
            }

            if(nodeMaster == null || nodePlaceholder == null)
                continue;

            nodePlaceholder.mType = nodeMaster.mType;
            nodePlaceholder.mParams = PathParser.copyOfRange(nodeMaster.mParams, 0, nodeMaster.mParams.length);

            if(Character.isLowerCase(nodePlaceholder.mType)){ // this is a relative movement. If we want to create extra nodes, we need to create neutral relative commands
                Arrays.fill(nodePlaceholder.mParams, 0.0f); //FIXME is good?
            }
            else{ //TODO calcolare spostamenti esatti nel caso in cui ci siano movimenti relativi! va calcolato lo spostamento dall'inizio
                float lastX, lastY;
                int index = i-1;
                if(index<0){
                    index = i+1;
                    while(index < listPlaceholder.size() && listPlaceholder.get(index).mType == PathNodeUtils.CMD_PLACEHOLDER)
                        index++;
                }

                //TODO gestire comandi con un solo parametro! tipo 'v'
                if(listPlaceholder.get(index).mParams.length >= 2){

                    lastX = listPlaceholder.get(index).mParams[ listPlaceholder.get(index).mParams.length-2];
                    lastY = listPlaceholder.get(index).mParams[ listPlaceholder.get(index).mParams.length-1];

                    for(int j = 0; j< nodePlaceholder.mParams.length; j++){
                        nodePlaceholder.mParams[j++] = lastX;
                        nodePlaceholder.mParams[j] = lastY;
                    }
                }
            }

        }
    }
}
