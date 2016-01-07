package com.bonnyfone.vectalign.techniques;

import android.support.v7.graphics.drawable.PathParser;

import java.util.ArrayList;

/**
 * This class represents the technique used to align sequences.
 */
public abstract class AbstractAlignMode {

    public abstract  ArrayList<PathParser.PathDataNode>[] align(PathParser.PathDataNode[] from, PathParser.PathDataNode[] to);

}
