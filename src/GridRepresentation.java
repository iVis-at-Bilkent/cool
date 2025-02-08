/*
 * KIELER - Kiel Integrated Environment for Layout Eclipse RichClient
 *
 * http://www.informatik.uni-kiel.de/rtsys/kieler/
 * 
 * Copyright 2012 by
 * + Kiel University
 *   + Department of Computer Science
 *     + Real-Time and Embedded Systems Group
 * 
 * This code is provided under the terms of the Eclipse Public License (EPL).
 * See the file epl-v10.html for the license text.
 */
//package de.cau.cs.kieler.klay.planar.intermediate;

//import de.cau.cs.kieler.klay.planar.graph.PNode;

/**
 * Represents the {@link PGraph} elements as a grid. The entries here are the graph nodes which are
 * connected by the graph edges.
 * 
 * @author pkl
 */

import org.json.JSONArray;

public class GridRepresentation {

    private PNode[][] nodePositions;

    private final int height;

    private final int width;

    /**
     * Generates a new grid with given width and height.
     * 
     * @param width
     *            of the grid.
     * @param height
     *            of the grid.
     */
    public GridRepresentation(final int width, final int height) {
        this.width = width;
        this.height = height;
        this.nodePositions = new PNode[width][height];
    }

    /**
     * Removes a node of the grid.
     * 
     * @param node
     *            , the node to remove from grid.
     */
    public void remove(final PNode node) {
        int[] coordinates = getPosition(node);
        nodePositions[coordinates[0]][coordinates[1]] = null;
    }

    /**
     * Removes a high-degree node from the grid. A high-degree node has more than one grid postion.
     * 
     * @param hdNode
     *            the removing high-degree node
     */
    public void removeHDNode(final PNode hdNode) {
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                if (get(x, y) == hdNode) {
                    nodePositions[x][y] = null;
                }
            }
        }
    }

    /**
     * Gives the entry of the grid for a position.
     * 
     * @param x
     *            position of the entry in the grid.
     * @param y
     *            position of the entry in the grid.
     * @return the found PNode.
     */
    public PNode get(final int x, final int y) {
        return this.nodePositions[x][y];
    }

    /**
     * Sets a node at the x and y position of the grid.
     * 
     * @param x
     *            coordinate of the position.
     * @param y
     *            coordinate of the position.
     * @param node
     *            {@link PNode}
     */
    public void set(final int x, final int y, final PNode node) {
        this.nodePositions[x][y] = node;
    }

    public void printGrid(boolean isPrinting){
        int[] arr = new int [2];
        arr[0] = getWidth(); 
        arr[1] = getHeight();
        JSONArray widthHeight = new JSONArray( arr );
        String [][] matrix = new String [ arr[0] ][ arr[1] ];
        /*System.out.println( getWidth() );
        System.out.println( getHeight() );
        System.out.println( widthHeight.toString() );*/

        int counter = 0;
        for( int i = 0; i  < getWidth(); i++){
            for( int j = 0; j < getHeight(); j++){
                if( this.nodePositions[i][j] == null ){
                    matrix[i][j] = null;
                    continue;
                }
                PNode node = this.nodePositions[i][j];
                /*if( node.getProperty(Properties.PLANAR_DUMMY_NODE) == true ){
                    matrix[i][j]= "pd" + (node.id);
                }
                else if( node.getProperty(Properties.RECT_SHAPE_DUMMY) == true ){
                    matrix[i][j] = "rd" + (node.id);
                }
                else if ( node.getProperty( Properties.BENDPOINT) != null ){
                    matrix[i][j] = "bd" + (node.id);
                }/ */
                matrix[i][j] = "" + node.id;
                //System.out.print( matrix[i][j] + " ");
                counter++;
            }
            //System.out.println();
        }
        JSONArray gridInfo = new JSONArray( matrix);
        //System.out.println( widthHeight.toString() );
        if( isPrinting)
        System.out.println( gridInfo.toString() );
    }

    /**
     * Searches in the grid for the given node and gives its position in the grid!
     * 
     * @throws IllegalArgumentException
     *             if no entry is found.
     * @param node
     *            , the search element.
     * @return the grid position of the node, first element is the x coordinate, second is the y
     *         coordinate.
     */
    public int[] getPosition(final PNode node) {
        int[] result = { 0, 0 };
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (nodePositions[x][y] == node) {
                    result[0] = x;
                    result[1] = y;
                    return result;
                }
            }
        }
        throw new IllegalArgumentException("GridRepresentation: the searched node does not exist!");
    }

    /**
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * @return the height
     */
    public int getHeight() {
        return height;
    }

}
