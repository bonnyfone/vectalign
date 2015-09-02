package com.bonnyfone.vectalign.techniques;

import android.support.v7.graphics.drawable.PathParser;
import com.bonnyfone.vectalign.PathNodeUtils;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Needleman–Wunsch algorithm re-interpreted to align VectorDrawable sequences.
 */
public class NWAlignment {
    private ArrayList<PathParser.PathDataNode> mListA;
    private ArrayList<PathParser.PathDataNode> mListB;
    private ArrayList<PathParser.PathDataNode> mOriginalListA;
    private ArrayList<PathParser.PathDataNode> mOriginalListB;
    private ArrayList<PathParser.PathDataNode> mAlignedListA;
    private ArrayList<PathParser.PathDataNode> mAlignedListB;
    private int[][] mD;

    private int match = 1;
    private int missmatch = -1;
    private int indel = 0;

    public NWAlignment(ArrayList<PathParser.PathDataNode> from, ArrayList<PathParser.PathDataNode> to){
        mOriginalListA = from;
        mOriginalListB = to;

        mListA = new ArrayList<>();
        mListA.addAll(mOriginalListA);

        mListB = new ArrayList<>();
        mListB.addAll(mOriginalListB);

        //Add dumb command at start
        PathParser.PathDataNode dumbNode = new PathParser.PathDataNode('D', new float[]{});
        mListA.add(0, dumbNode);
        mListB.add(0, dumbNode);
    }

    private void initMatrixD(){
        mD = new int[mListA.size() + 1][mListB.size() + 1];
        for (int i = 0; i <= mListA.size(); i++) {
            for (int j = 0; j <= mListB.size(); j++) {
                if (i == 0)
                    mD[i][j] = -j;
                else if (j == 0)
                    mD[i][j] = -i;
                else
                    mD[i][j] = 0;
            }
        }
    }

    public void align(){
        initMatrixD();
        mAlignedListA = new ArrayList<>();
        mAlignedListB = new ArrayList<>();

        //process matrix D
        for (int i = 1; i <= mListA.size(); i++) {
            for (int j = 1; j <= mListB.size(); j++) {
                int scoreDiag = mD[i-1][j-1] + getScore(i, j);
                int scoreLeft = mD[i][j-1] + indel;
                int scoreUp = mD[i-1][j] + indel;
                mD[i][j] = Math.max(Math.max(scoreDiag, scoreLeft), scoreUp);
            }
        }

        //backtracking
        int i = mListA.size();
        int j = mListB.size();
        float[] arrayType = new float[]{};
        while (i > 0 && j > 0) {

            PathParser.PathDataNode newNodeA = null;
            PathParser.PathDataNode newNodeB = null;
            if (mD[i][j] == mD[i-1][j-1] + getScore(i, j)) {
                newNodeA = mListA.get(i-1);
                newNodeB = mListB.get(j-1);
                i--;
                j--;
            } else if (mD[i][j] == mD[i][j-1] + indel) {
                newNodeA = new PathParser.PathDataNode(PathNodeUtils.CMD_PLACEHOLDER, arrayType);
                newNodeB = mListB.get(j-1);
                j--;
            } else {
                newNodeA = mListA.get(i-1);
                newNodeB = new PathParser.PathDataNode(PathNodeUtils.CMD_PLACEHOLDER, arrayType);
                i--;
            }

            //insert new nodes in reverse order
            mAlignedListA.add(0, newNodeA);
            mAlignedListB.add(0, newNodeB);
        }

        //Remove dumbs node
        Iterator iterators[] = new Iterator[]{mAlignedListA.iterator(), mAlignedListB.iterator()};
        for(Iterator it : iterators){
            while(it.hasNext()){
                PathParser.PathDataNode node = (PathParser.PathDataNode) it.next();
                if(node.mType == PathNodeUtils.CMD_DUMB)
                    it.remove();
            }
        }

    }

    private int getScore(int i, int j) {
        if (mListA.get(i - 1).mType == mListB.get(j - 1).mType){
            return match; //Arbitrary positive score
        } else {
            return missmatch; //Arbitrary negative score
        }
    }


    public ArrayList<PathParser.PathDataNode> getAlignedFrom() {
        return mAlignedListA;
    }

    public ArrayList<PathParser.PathDataNode> getAlignedTo() {
        return mAlignedListB;
    }

    public ArrayList<PathParser.PathDataNode> getOriginalFrom() {
        return mOriginalListA;
    }

    public ArrayList<PathParser.PathDataNode> getOriginalTo() {
        return mOriginalListB;
    }

    void printMatrix() {
        System.out.println("D =");
        for (int i = 0; i < mListA.size() + 1; i++) {
            for (int j = 0; j < mListB.size() + 1; j++) {
                System.out.print(String.format("%4d ", mD[i][j]));
            }
            System.out.println();
        }
        System.out.println();
    }
}