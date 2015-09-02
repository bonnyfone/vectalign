package com.bonnyfone.vectalign.techniques;

import android.support.v7.graphics.drawable.PathParser;

import java.util.ArrayList;

/**
 * Created by ziby on 07/08/15.
 */
public abstract class AbstractFillMode {

    public abstract void fillInjectedNodes(ArrayList<PathParser.PathDataNode> from, ArrayList<PathParser.PathDataNode> to);

}
