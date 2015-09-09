import android.support.v7.graphics.drawable.PathParser;
import com.bonnyfone.vectalign.PathNodeUtils;
import com.bonnyfone.vectalign.VectAlign;
import com.bonnyfone.vectalign.techniques.BaseFillMode;
import com.bonnyfone.vectalign.techniques.NWAlignment;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import static org.junit.Assert.*;

/**
 * Created by ziby on 11/08/15.
 */
public class VectAlignTest {


    String star = "M 48,54 L 31,42 15,54 21,35 6,23 25,23 32,4 40,23 58,23 42,35 z";
    String pentagon = "M 48,54 L 31,54 15,54 10,35 6,23 25,10 32,4 40,10 58,23 54,35 z";
    String square = "M 10,10 L 10,10 10,10 50,10 50,10 50,10 50,50 50,50 50,50 10,50 10,50 10,50 z";
    String starEquivalent = "M 48,54 M 48,54 M 48,54 L 31,42 L 31,42 L 31,42 15,54 21,35 6,23 25,23 32,4 40,23 40,23 40,23 58,23 42,35 z z";
    String complex = "M32.8897783,115.66321 C26.50733,119.454449 21.3333333,116.509494 21.3333333,109.083329 L21.3333333,20.2501707 C21.3333333,12.8249638 26.5016153,9.87565618 32.8897783,13.6702897 L107.184497,57.8021044 C113.566945,61.5933434 113.57266,67.7367615 107.184497,71.5313951 L32.8897783,115.66321 z";
    String megaStar = "M 18.363636,11.818181 l 6.780273,10.042729 5.875151,-10.597692 0.27442,12.114173 10.672036,-5.738999 -6.31856,10.339453 12.080626,0.941786 -10.905441,5.28203 9.653701,7.323559 L 34.44592,40.07282 38.607715,51.452976 29.272727,43.727272 26.621276,55.550904 22.945015,44.004753 14.322134,52.517942 17.471787,40.817166 5.6151801,43.317035 14.590755,35.176546 3.2648108,30.869404 15.216625,28.873751 8.0172516,19.127085 19.150689,23.909874 z";
    String pentagonAlter = "M 48,54 L 31,54 15,54 10,35 6,23 C 25,10 25,10 25,10 L 32,4 40,10 58,23 54,35 z";
    final String supportSample ="m20,200l100,90l180-180l-35-35l-145,145l-60-60l-40,40z V 10 h 11 l 2,2 v 5";
    String arrow = "M 12, 4 L 10.59,5.41 L 16.17,11 L 18.99,11 L 12,4 z M 4, 11 L 4, 13 L 18.99, 13 L 20, 12 L 18.99, 11 L 4, 11 z M 12,20 L 10.59, 18.59 L 16.17, 13 L 18.99, 13 L 12, 20z";
    String rombo = "M -6.5454547,11.454545 L 16.23507,17.929393 38.909091,11.090909 32.434243,33.871434 39.272727,56.545455 16.492202,50.070607 -6.1818193,56.909091 0.29302906,34.128566 z";

    PathParser.PathDataNode[] pentagonData = PathParser.createNodesFromPathData(pentagon);
    PathParser.PathDataNode[] pentagonAlterData = PathParser.createNodesFromPathData(pentagonAlter);
    PathParser.PathDataNode[] supportData = PathParser.createNodesFromPathData(supportSample);
    PathParser.PathDataNode[] starData = PathParser.createNodesFromPathData(star);
    PathParser.PathDataNode[] arrowData = PathParser.createNodesFromPathData(arrow);
    PathParser.PathDataNode[] complexData = PathParser.createNodesFromPathData(complex);
    PathParser.PathDataNode[] megaStarData = PathParser.createNodesFromPathData(megaStar);
    PathParser.PathDataNode[] romboData = PathParser.createNodesFromPathData(rombo);
    PathParser.PathDataNode[] squareData = PathParser.createNodesFromPathData(square);


    @Test
    public void testCanMorph() throws Exception {
        System.out.println("Testing canMorph()...");
        boolean shouldMorph = VectAlign.canMorph(star, pentagon);
        assertTrue(shouldMorph);

        boolean shouldNotMorph = VectAlign.canMorph(square, pentagon);
        assertFalse(shouldNotMorph);
    }

    @Test
    public void testIsEquivalent() throws Exception {
        System.out.println("Testing isEquivalent()...");
        assertTrue(PathNodeUtils.isEquivalent(PathNodeUtils.transform(PathParser.createNodesFromPathData(star)), PathNodeUtils.transform(PathParser.createNodesFromPathData(starEquivalent))));
        assertTrue(PathNodeUtils.isEquivalent(PathNodeUtils.transform(PathParser.createNodesFromPathData(star)), PathNodeUtils.transform(PathParser.createNodesFromPathData(star), 2, true)));
        assertTrue(PathNodeUtils.isEquivalent(PathNodeUtils.transform(PathParser.createNodesFromPathData(megaStar)), PathNodeUtils.transform(PathParser.createNodesFromPathData(megaStar), 2, true)));
        assertTrue(PathNodeUtils.isEquivalent(PathNodeUtils.transform(PathParser.createNodesFromPathData(megaStar)), PathNodeUtils.transform(PathParser.createNodesFromPathData(megaStar), 5, true)));

        assertFalse(PathNodeUtils.isEquivalent(PathNodeUtils.transform(PathParser.createNodesFromPathData(megaStar)), PathNodeUtils.transform(PathParser.createNodesFromPathData(star))));
        assertFalse(PathNodeUtils.isEquivalent(PathNodeUtils.transform(PathParser.createNodesFromPathData(pentagonAlter)), PathNodeUtils.transform(PathParser.createNodesFromPathData(star))));
        assertFalse(PathNodeUtils.isEquivalent(PathNodeUtils.transform(PathParser.createNodesFromPathData(complex)), PathNodeUtils.transform(PathParser.createNodesFromPathData(supportSample))));
        assertFalse(PathNodeUtils.isEquivalent(PathNodeUtils.transform(PathParser.createNodesFromPathData(arrow)), PathNodeUtils.transform(PathParser.createNodesFromPathData(square))));
    }

    @Test
    public void testTransformations() throws Exception {
        System.out.println("Testing transformations...");

        PathParser.PathDataNode[] nodesFromPathData = PathParser.createNodesFromPathData(complex);
        ArrayList<PathParser.PathDataNode> transform = PathNodeUtils.transform(nodesFromPathData);
        String s = PathNodeUtils.pathNodesToString(transform);


        PathParser.PathDataNode[] nodesFromPathData2 = PathParser.createNodesFromPathData(s);
        ArrayList<PathParser.PathDataNode> transform2 = PathNodeUtils.transform(nodesFromPathData2);

        assertTrue(transform != null && transform2 != null && transform.size() == transform2.size());
        for(int i=0; i<transform.size(); i++)
            assertTrue(transform.get(i).isEqual(transform2.get(i)));

    }

    @Test
    public void testCalculatePenPosition() throws Exception {
        System.out.println("Testing calculatePenPosition()...");
        float[][] penPos = PathNodeUtils.calculatePenPosition(PathNodeUtils.transform(supportData));
        assertTrue(penPos[penPos.length-1][0] == 33.0f);
        assertTrue(penPos[penPos.length-1][1] == 17.0f);
    }

    @Test
    public void testPenPositionInvariant() throws Exception {
        System.out.println("Testing final pen position...");
        float[][] penPos1 = PathNodeUtils.calculatePenPosition(PathNodeUtils.transform(supportData));
        float[][] penPos2 = PathNodeUtils.calculatePenPosition(PathNodeUtils.transform(starData));
        ArrayList<PathParser.PathDataNode> transform1 = PathNodeUtils.transform(supportData, 2, true);
        ArrayList<PathParser.PathDataNode> transform2 = PathNodeUtils.transform(starData, 2, true);
        NWAlignment nwexp = new NWAlignment(transform1, transform2);
        nwexp.align();
        BaseFillMode fillMode = new BaseFillMode();
        fillMode.fillInjectedNodes(nwexp.getAlignedFrom(), nwexp.getAlignedTo());

        float[][] penPos1b = PathNodeUtils.calculatePenPosition(nwexp.getAlignedFrom());
        float[][] penPos2b = PathNodeUtils.calculatePenPosition(nwexp.getAlignedTo());


        if(        (penPos1[penPos1.length-1][0] == penPos1b[penPos1b.length-1][0])
                && (penPos1[penPos1.length-1][1] == penPos1b[penPos1b.length-1][1])
                && (penPos2[penPos2.length-1][0] == penPos2b[penPos2b.length-1][0])
                && (penPos2[penPos2.length-1][1] == penPos2b[penPos2b.length-1][1])){

            assertTrue(true);
        }
        else{
            System.out.println("PROBLEM during injection!");
            System.out.println("PenPos from");
            StringBuffer sb = new StringBuffer();
            int i = 0;
            for(float[] coord : penPos1){
                sb.append((++i)+"p. " +coord[0]+" , "+coord[1] +"\n") ;
            }
            System.out.println(sb.toString());


            System.out.println("PenPos fromAfter");
            sb = new StringBuffer();
            i = 0;
            for(float[] coord : penPos1b){
                sb.append((++i)+"p. " +coord[0]+" , "+coord[1] +"\n") ;
            }
            System.out.println(sb.toString());


            System.out.println("PenPos to");
            sb = new StringBuffer();
            i = 0;
            for(float[] coord : penPos2){
                sb.append((++i)+"p. " +coord[0]+" , "+coord[1] +"\n") ;
            }
            System.out.println(sb.toString());


            System.out.println("PenPos toAfter");
            sb = new StringBuffer();
            i = 0;
            for(float[] coord : penPos2b){
                sb.append((++i)+"p. " +coord[0]+" , "+coord[1] +"\n") ;
            }
            System.out.println(sb.toString());
            assertTrue(false);

        }
    }

    @Test
    public void testRandomBaseAligns() throws Exception {
        testRandomAligns(VectAlign.Mode.BASE);
    }

    @Test
    public void testRandomLinearInterpolateAligns() throws Exception {
        testRandomAligns(VectAlign.Mode.LINEAR);
    }

    public void testRandomAligns(VectAlign.Mode mode) throws Exception {
        System.out.println("Testing random sequences alignment "+ mode.toString() + "...");
        ArrayList<PathParser.PathDataNode> all = new ArrayList<>();
        all.addAll(PathNodeUtils.transform(pentagonData));
        all.addAll(PathNodeUtils.transform(pentagonAlterData));
        all.addAll(PathNodeUtils.transform(supportData));
        all.addAll(PathNodeUtils.transform(starData));
        all.addAll(PathNodeUtils.transform(arrowData));
        all.addAll(PathNodeUtils.transform(complexData));
        all.addAll(PathNodeUtils.transform(megaStarData));
        all.addAll(PathNodeUtils.transform(romboData));
        all.addAll(PathNodeUtils.transform(squareData));

        int experiments = 500;
        int extraCopyInTransform = 2;
        Random r = new Random(System.currentTimeMillis());
        ArrayList<PathParser.PathDataNode> l1 = new ArrayList<>();
        ArrayList<PathParser.PathDataNode> l2 = new ArrayList<>();
        ArrayList<PathParser.PathDataNode>[] lists = new ArrayList[2];
        lists[0]= l1;
        lists[1]= l2;

        int success=0;
        int fail=0;

        for(int i=0; i< experiments; i++){
            System.out.println("\nRunning experiment " + (i + 1));
            for(int j=0; j<lists.length; j++){
                Collections.shuffle(all);

                int n1 = r.nextInt(all.size());
                int n2 = r.nextInt(all.size());
                while(n1==n2)
                    n2 = r.nextInt(all.size());
                lists[j].clear();
                if(n1 < n2)
                    lists[j].addAll(all.subList(n1, n2));
                else
                    lists[j].addAll(all.subList(n2, n1));
            }
            String[] ris = VectAlign.align(PathNodeUtils.pathNodesToString(l1), PathNodeUtils.pathNodesToString(l2), mode);

            if(VectAlign.canMorph(ris[0], ris[1])){
                success++;
                System.out.println("Ok morphable");
            }
            else{
                fail++;
                System.out.println("CAN'T MORPH");

                ArrayList<PathParser.PathDataNode> transform1 = PathNodeUtils.transform(l1.toArray(new PathParser.PathDataNode[0]), extraCopyInTransform, true);
                ArrayList<PathParser.PathDataNode> transform2 = PathNodeUtils.transform(l2.toArray(new PathParser.PathDataNode[0]), extraCopyInTransform, true);
                NWAlignment nwexp = new NWAlignment(transform1, transform2);
                nwexp.align();
                if(!PathNodeUtils.isEquivalent(nwexp.getOriginalFrom(), nwexp.getAlignedFrom()) || !PathNodeUtils.isEquivalent(nwexp.getOriginalTo(), nwexp.getAlignedTo())){
                    System.out.println("----NOT EQUIVALENT! "+ nwexp.getOriginalFrom().size() + " to " +nwexp.getOriginalTo().size() + "  -> " +nwexp.getAlignedFrom().size() +" to "+nwexp.getAlignedTo().size());
                }
                else{
                    System.out.println("OK! " + nwexp.getOriginalFrom().size() + " to " +nwexp.getOriginalTo().size()  + "  -> " +nwexp.getAlignedFrom().size() +" to "+nwexp.getAlignedTo().size());
                }

                BaseFillMode fillMode = new BaseFillMode();
                fillMode.fillInjectedNodes(nwexp.getAlignedFrom(), nwexp.getAlignedTo());
                boolean internalMorp = VectAlign.canMorph(PathNodeUtils.pathNodesToString(nwexp.getAlignedFrom()), PathNodeUtils.pathNodesToString(nwexp.getAlignedTo()));
                if(internalMorp){
                    System.err.println("SOMETHING STRANGE");

                }
                System.out.println("Original");
                System.out.println(PathNodeUtils.pathNodesToString(nwexp.getOriginalFrom(), true));
                System.out.println(PathNodeUtils.pathNodesToString(nwexp.getOriginalTo(), true));
                System.out.println("Aligned");
                //PathNodeUtils.simplify(nwexp.getAlignedFrom(), nwexp.getAlignedTo());
                System.out.println(PathNodeUtils.pathNodesToString(nwexp.getAlignedFrom(), true));
                System.out.println(PathNodeUtils.pathNodesToString(nwexp.getAlignedTo(), true));
            }
            System.out.println("___________________________________________________________________________________________________________________________________________________________________________________________________________________________________________");

            /*
            ArrayList<PathParser.PathDataNode> transform1 = PathNodeUtils.transform(l1.toArray(new PathParser.PathDataNode[0]), extraCopyInTransform);
            ArrayList<PathParser.PathDataNode> transform2 = PathNodeUtils.transform(l2.toArray(new PathParser.PathDataNode[0]), extraCopyInTransform);
            NWAlignment nwexp = new NWAlignment(transform1, transform2);
            nwexp.align();
            if(!PathNodeUtils.isEquivalent(nwexp.getOriginalFrom(), nwexp.getAlignedFrom()) || !PathNodeUtils.isEquivalent(nwexp.getOriginalTo(), nwexp.getAlignedTo())){
                System.out.println("----NOT EQUIVALENT! "+ nwexp.getOriginalFrom().size() + " to " +nwexp.getOriginalTo().size() + "  -> " +nwexp.getAlignedFrom().size() +" to "+nwexp.getAlignedTo().size());
                fail++;
            }
            else{
                System.out.println("OK! " + nwexp.getOriginalFrom().size() + " to " +nwexp.getOriginalTo().size()  + "  -> " +nwexp.getAlignedFrom().size() +" to "+nwexp.getAlignedTo().size());
                success++;
            }*/
        }

        System.out.println("FINISH: success("+success+"), fail("+fail+") --> success rate " +((float)success / (success+fail))*100.0f+"%");
        assertTrue(success == experiments);

    }
}