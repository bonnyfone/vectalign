package com.bonnyfone.vectalign;

import android.support.v7.graphics.drawable.PathParser;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Created by ziby on 07/08/15.
 */
public class PathNodeUtils {

    public static final char CMD_PLACEHOLDER = '#';
    public static final char CMD_DUMB = 'D';


    /**
     * Return the number of arguments expected for a specific command
     * @param type
     * @return
     */
    static int commandArguments(char type){
        switch (type) {
            case CMD_PLACEHOLDER:
            case 'z':
            case 'Z':
                return 0;
            case 'm':
            case 'M':
            case 'l':
            case 'L':
            case 't':
            case 'T':
                return 2;
            case 'h':
            case 'H':
            case 'v':
            case 'V':
                return 1;
            case 'c':
            case 'C':
                return 6;
            case 's':
            case 'S':
            case 'q':
            case 'Q':
                return 4;
            case 'a':
            case 'A':
                return 7;

        }
        return -1;
    }


    /**
     * Check if the given node sequences are equivalent (in terms of graphic commands)
     * @param original
     * @param alternative
     * @return
     */
    static boolean isEquivalent(ArrayList<PathParser.PathDataNode> original, ArrayList<PathParser.PathDataNode> alternative){
        int innerStart = 0;
        for(PathParser.PathDataNode o : original){
            boolean found = false;
            for(int i = innerStart; i < alternative.size() && !found; i++ ){
                PathParser.PathDataNode n = alternative.get(i);
                if(o.mType == n.mType && Arrays.equals(o.mParams, n.mParams)){
                    found = true;
                    innerStart = i+1;
                }
            }

            if(!found)
                return false;
        }

        return true;
    }

    static ArrayList<PathParser.PathDataNode> transform(PathParser.PathDataNode[] elements){
        return transform(elements, 0);
    }

    /**
     * Transform the input list. Expand (eventually) compressed element and create extra copy if needed.
     * @param elements
     * @param extraCopy
     * @return
     */
    static ArrayList<PathParser.PathDataNode> transform(PathParser.PathDataNode[] elements, int extraCopy){
        if(elements == null)
            return null;

        ArrayList<PathParser.PathDataNode> transformed = new ArrayList<>();
        for(PathParser.PathDataNode node : elements){
            int cmdArgs = commandArguments(node.mType);
            int argsProvided = node.mParams.length;
            if(cmdArgs == -1){
                System.err.println("Command not supported! " + node.mType);
            }
            else if(argsProvided < cmdArgs){
                System.err.println("Command " + node.mType + " requires " + cmdArgs + " params! Passing only " + argsProvided);
            }
            else if(cmdArgs == node.mParams.length){
                //Normal command with the exact number of params
                transformed.add(node);
                if(extraCopy > 0 && node.mType != 'z' && node.mType != 'Z' && node.mType != PathNodeUtils.CMD_DUMB){ //never add extra z/Z or dumb commands
                    PathParser.PathDataNode extraNodes = new PathParser.PathDataNode(node);
                    if(Character.isLowerCase(node.mType)){ // this is a relative movement. If we want to create extra nodes, we need to create neutral relative commands
                        Arrays.fill(extraNodes.mParams, 0.0f); //FIXME is good?
                    }

                    for(int j=0; j<extraCopy; j++)
                        transformed.add(extraNodes);
                }
            }
            else{
                //Multiple groups of params, verify consistency
                int mod = (argsProvided % cmdArgs);
                if(mod != 0){
                    System.err.println("Providing multiple groups of params for command " + node.mType + ", but in wrong number (missing " + mod +" args)" );
                }
                else{
                    //Create multiple nodes
                    int iter = argsProvided / cmdArgs;
                    for(int i=0; i< iter; i++){
                        PathParser.PathDataNode newNode = new PathParser.PathDataNode(node.mType, PathParser.copyOfRange(node.mParams, i*cmdArgs, (i+1) *cmdArgs));
                        transformed.add(newNode);

                        if(extraCopy > 0){
                            PathParser.PathDataNode extraNodes = new PathParser.PathDataNode(newNode);;
                            if(Character.isLowerCase(newNode.mType)){ // this is a relative movement. If we want to create extra nodes, we need to create neutral relative commands
                                Arrays.fill(extraNodes.mParams, 0.0f); //FIXME is good?
                            }

                            for(int j=0; j<extraCopy; j++)
                                transformed.add(extraNodes);
                        }
                    }
                }
            }
        }
        return transformed;
    }


    /**
     * Simplify eventually useless nodes
     * @param from
     * @param to
     */
    static void simplify(ArrayList<PathParser.PathDataNode> from, ArrayList<PathParser.PathDataNode> to){
        if(from.size() != to.size()){
            System.err.println("Cannot simplify lists of nodes of different sizes");
            return;
        }

        System.out.println("Simplify lists with size "+from.size());

        boolean removeIndexes[] = new boolean[from.size()];
        int last = from.size()-1; //avoid last

        for(int i=0; i<last; i++){
            if(from.get(i).isEqual(from.get(i+1)) && to.get(i).isEqual(to.get(i+1))){
                removeIndexes[i] = true;
            }
        }

        Iterator iterators[] = new Iterator[]{from.iterator(), to.iterator()};
        for(Iterator it : iterators){
            int i = 0;
            while(it.hasNext()){
                it.next();
                if(removeIndexes[i++])
                    it.remove();
            }
        }

        System.out.println("Final size after simplify is "+from.size());
    }


    /**
     * Create a VectorDrawable sequence from a list of nodes
     * @param nodes
     * @return
     */
    static String pathNodesToString(ArrayList<PathParser.PathDataNode> nodes, boolean onlyCommands){
        StringBuilder sb = new StringBuilder();
        for(PathParser.PathDataNode n : nodes){
            sb.append(n.mType);
            sb.append(' ');
            if(!onlyCommands){
                for(float p : n.mParams){
                    sb.append(p);
                    sb.append(',');
                }
                sb.replace(sb.length()-1, sb.length()," ");
            }
        }
        return sb.toString();
    }

    static String pathNodesToString(ArrayList<PathParser.PathDataNode> nodes){
        return pathNodesToString(nodes, false);
    }
}
