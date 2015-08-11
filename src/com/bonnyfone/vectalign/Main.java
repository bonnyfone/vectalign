package com.bonnyfone.vectalign;

import android.support.v7.graphics.drawable.PathParser;
import com.bonnyfone.vectalign.techniques.BaseFillMode;
import com.bonnyfone.vectalign.techniques.NWAlignment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Created by ziby on 11/08/15.
 */
public class Main {

    public static void main(String args[]){
        System.out.println("-------- start ---------");

        //Input
        String star = "M 48,54 L 31,42 15,54 21,35 6,23 25,23 25,23 25,23 25,23 32,4 40,23 58,23 42,35 z";
        String square = "M 10,10 L 10,10 10,10 50,10 50,10 50,10 50,50 50,50 50,50 10,50 10,50 10,50 z";
        String pentagon = "M 48,54 L 31,54 15,54 10,35 6,23 25,10 32,4 40,10 58,23 54,35 z";
        String pentagonAlter = "M 48,54 L 31,54 15,54 10,35 6,23 C 25,10 25,10 25,10 L 32,4 40,10 58,23 54,35 z";
        String supportSample="m20,200l100,90l180-180l-35-35l-145,145l-60-60l-40,40z";
        String arrow = "M 12, 4 L 10.59,5.41 L 16.17,11 L 18.99,11 L 12,4 z M 4, 11 L 4, 13 L 18.99, 13 L 20, 12 L 18.99, 11 L 4, 11 z M 12,20 L 10.59, 18.59 L 16.17, 13 L 18.99, 13 L 12, 20z";
        String complex ="M32.8897783,115.66321 C26.50733,119.454449 21.3333333,116.509494 21.3333333,109.083329 L21.3333333,20.2501707 C21.3333333,12.8249638 26.5016153,9.87565618 32.8897783,13.6702897 L107.184497,57.8021044 C113.566945,61.5933434 113.57266,67.7367615 107.184497,71.5313951 L32.8897783,115.66321 z";
        String megaStar = "M 18.363636,11.818181 l 6.780273,10.042729 5.875151,-10.597692 0.27442,12.114173 10.672036,-5.738999 -6.31856,10.339453 12.080626,0.941786 -10.905441,5.28203 9.653701,7.323559 L 34.44592,40.07282 38.607715,51.452976 29.272727,43.727272 26.621276,55.550904 22.945015,44.004753 14.322134,52.517942 17.471787,40.817166 5.6151801,43.317035 14.590755,35.176546 3.2648108,30.869404 15.216625,28.873751 8.0172516,19.127085 19.150689,23.909874 z";
        String rombo= "M -6.5454547,11.454545 L 16.23507,17.929393 38.909091,11.090909 32.434243,33.871434 39.272727,56.545455 16.492202,50.070607 -6.1818193,56.909091 0.29302906,34.128566 z";
        /*
        //TEST canMorph
        System.out.println("Can morph star to pentagon? " + VectAlign.canMorph(star, pentagon));
        System.out.println("Can morph star to supportSample? " + VectAlign.canMorph(star, supportSample));

        //TEST explode
        System.out.println("\nExploded string: ");
        PathParser.PathDataNode[] nodesFromPathData = PathParser.createNodesFromPathData(supportSample);
        printPathData(nodesFromPathData);*/

        //TEST transform
        PathParser.PathDataNode[] pentagonData = PathParser.createNodesFromPathData(pentagon);
        PathParser.PathDataNode[] pentagonAlterData = PathParser.createNodesFromPathData(pentagonAlter);
        PathParser.PathDataNode[] supportData = PathParser.createNodesFromPathData(supportSample);
        PathParser.PathDataNode[] starData = PathParser.createNodesFromPathData(star);
        PathParser.PathDataNode[] arrowData = PathParser.createNodesFromPathData(arrow);
        PathParser.PathDataNode[] complexData = PathParser.createNodesFromPathData(complex);
        PathParser.PathDataNode[] megaStarData = PathParser.createNodesFromPathData(megaStar);
        PathParser.PathDataNode[] romboData = PathParser.createNodesFromPathData(rombo);
        PathParser.PathDataNode[] squareData = PathParser.createNodesFromPathData(square);


        PathParser.PathDataNode[] data1 = arrowData;
        PathParser.PathDataNode[] data2 = starData;

        System.out.println("TRANSFORMED 1: ");
        VectAlign.printPathData(PathNodeUtils.transform(data1));

        System.out.println("TRANSFORMED 2: ");
        VectAlign.printPathData(PathNodeUtils.transform(data2));

        System.out.println("NW ALIGNING");
        NWAlignment nw = new NWAlignment(PathNodeUtils.transform(data1), PathNodeUtils.transform(data2));
        nw.align();
        //nw.printMatrix();

        System.out.println("new From");
        VectAlign.printPathData(nw.getAlignedFrom());
        System.out.println("new To");
        VectAlign.printPathData(nw.getAlignedTo());

        System.out.println("original 1 is equivalent to aligned1? " + PathNodeUtils.isEquivalent(nw.getOriginalFrom(), nw.getAlignedFrom()));
        System.out.println("original 2 is equivalent to aligned2? " + PathNodeUtils.isEquivalent(nw.getOriginalTo(), nw.getAlignedTo()));

        String[] align = VectAlign.align(rombo, megaStar, VectAlign.Mode.BASE);
        System.out.println(rombo);
        System.out.println(align[0]);
        System.out.println();
        System.out.println(megaStar);
        System.out.println(align[1]);

        System.out.println("Aligned seq can morph?? " + VectAlign.canMorph(align[0], align[1]));

        //System.out.println("aligned lists can morph? " + PathParser.canMorph(nw.getAlignedFrom().toArray(new PathParser.PathDataNode[]{}), nw.getAlignedTo().toArray(new PathParser.PathDataNode[]{})));


        System.out.println("-------- end ---------");
    }
}
