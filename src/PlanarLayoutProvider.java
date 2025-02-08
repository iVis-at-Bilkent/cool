/*
 * KIELER - Kiel Integrated Environment for Layout Eclipse RichClient
 *
 * http://www.informatik.uni-kiel.de/rtsys/kieler/
 * 
 * Copyright 2010 by
 * + Kiel University
 *   + Department of Computer Science
 *     + Real-Time and Embedded Systems Group
 * 
 * This code is provided under the terms of the Eclipse Public License (EPL).
 * See the file epl-v10.html for the license text.
 */
//package de.cau.cs.kieler.klay.planar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Collection;
import org.json.JSONArray;

import org.eclipse.elk.core.AbstractLayoutProvider;
import org.eclipse.elk.core.options.CoreOptions;
import org.eclipse.elk.core.util.BasicProgressMonitor;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.ElkNode;

/*import de.cau.cs.kieler.klay.planar.graph.PGraph;
import de.cau.cs.kieler.klay.planar.graph.PGraphFactory;
import de.cau.cs.kieler.klay.planar.intermediate.GridRepresentation;
import de.cau.cs.kieler.klay.planar.intermediate.IntermediateProcessorStrategy;
import de.cau.cs.kieler.klay.planar.p1planar.BoyerMyrvoldPlanarSubgraphBuilder;
import de.cau.cs.kieler.klay.planar.p1planar.EdgeInsertionPlanarization;
import de.cau.cs.kieler.klay.planar.p1planar.LRPlanarSubgraphBuilder;
import de.cau.cs.kieler.klay.planar.p1planar.PlanarityTestStrategy;
import de.cau.cs.kieler.klay.planar.p2ortho.TamassiaOrthogonalizer;
import de.cau.cs.kieler.klay.planar.p3compact.TidyRectangleCompactor;
import de.cau.cs.kieler.klay.planar.properties.Properties;
import de.cau.cs.kieler.klay.planar.util.PUtil;*/

/**
 * Layout provider for an orthogonal layout.
 * 
 * @author ocl
 * @author pdo
 * @author pkl
 * @kieler.rating proposed yellow by pkl
 */
public class PlanarLayoutProvider extends AbstractLayoutProvider {

    // ======================== Variables ============================
    /** phase 1: algorithm for planar testing and building a subgraph. */
    private ILayoutPhase subgraphBuilder = new BoyerMyrvoldPlanarSubgraphBuilder();

    /** phase 2: algorithm for inserting edges that are removed in the step before. */
    private ILayoutPhase edgeInserter = new EdgeInsertionPlanarization();

    /** phase 3: algorithm for orthogonalization. */
    private ILayoutPhase orthogonalizer = new TamassiaOrthogonalizer();

    /** phase 4: algorithm for compaction. */
    private ILayoutPhase compactor = new TidyRectangleCompactor();

   // /** connected components processor. */
   // private ComponentsProcessor componentsProcessor = new ComponentsProcessor();

    /** intermediate layout processor configuration. */
    private IntermediateProcessingConfiguration intermediateProcessingConfiguration 
        = new IntermediateProcessingConfiguration();

    /** collection of instantiated intermediate modules. */
    private Map<IntermediateProcessorStrategy, ILayoutProcessor> intermediateLayoutProcessorCache 
        = new HashMap<IntermediateProcessorStrategy, ILayoutProcessor>();

    /** list of layout processors that compose the current algorithm. */
    private List<ILayoutProcessor> algorithm = new LinkedList<ILayoutProcessor>();

    /**
     * Returns an intermediate processing strategy with processors not tied to specific phases.
     * 
     * @param graph
     *            the layered graph to be processed. The strategy may vary depending on certain
     *            properties of the graph.
     * @return intermediate processing strategy. May be {@code null}.
     */
    private IntermediateProcessingConfiguration getIntermediateProcessingStrategy(final PGraph graph) {
        return null;
    }

    private PGraph createK3_3() {
        // TODO check for directed/undirected edges
        // TODO check for embedding constraints (ports)
        // TODO recurse over children in compound nodes

        // Testgraph: K3_3
        PGraph graph = new PGraph();
        // Adding Nodes
        PNode node0 = graph.addNode(NodeType.NORMAL);
        PNode node1 = graph.addNode(NodeType.NORMAL);
        PNode node2 = graph.addNode(NodeType.NORMAL);
        PNode node3 = graph.addNode(NodeType.NORMAL);
        PNode node4 = graph.addNode(NodeType.NORMAL);
        PNode node5 = graph.addNode(NodeType.NORMAL);
        /*PNode node6 = graph.addNode(NodeType.NORMAL);
        PNode node7 = graph.addNode(NodeType.NORMAL);
        PNode node8 = graph.addNode(NodeType.NORMAL);*/


        // Adding Edges
        graph.addEdge(node0, node1);
        graph.addEdge(node0, node3);
        graph.addEdge(node0, node5);
        graph.addEdge(node1, node2);
        graph.addEdge(node1, node4);
        graph.addEdge(node2, node3);
        graph.addEdge(node2, node5);
        graph.addEdge(node3, node4);
        graph.addEdge(node4, node5);
       /*  graph.addEdge(node1, node7);
        graph.addEdge(node2, node8);
        graph.addEdge(node3, node5);
        graph.addEdge(node6, node1);
        graph.addEdge(node6, node8);*/
    
        return graph;
    }
    
    public static void main(String args[]){
        PlanarLayoutProvider layoutProvider = new PlanarLayoutProvider();
        //System.out.println("dsadsasadssa");
          Scanner scanner = new Scanner(System.in);
          int numberOfNodes = Integer.parseInt(scanner.nextLine());
          PGraph graph = new PGraph();
          PNode [] nodes = new PNode[numberOfNodes];
          for( int i = 0; i  < numberOfNodes; i++){
            nodes[i] = graph.addNode(NodeType.NORMAL);
          }
          //System.out.println( numberOfNodes);
          String strArray = scanner.nextLine();
          //System.out.println( strArray);
          

        // Convert to 1D integer array
        String[] parsedArray = strArray.split(",");
        int[] oneDArray = new int[parsedArray.length];
        //System.out.println(parsedArray.length);
        //System.out.println( strArray);
        for (int i = 0; i < parsedArray.length; i+=2) {
            //System.out.println( parsedArray[i] + " " + parsedArray[i+1]);
        
             oneDArray[i] = Integer.parseInt(parsedArray[i]);
            oneDArray[i+1] = Integer.parseInt(parsedArray[i+1]);
            
            graph.addEdge(nodes[oneDArray[i]], nodes[oneDArray[i+1]]);
            
        }
        layoutProvider.layout( graph, new BasicProgressMonitor());
        
    }

    /**
     * Returns a list of layout processor instances for the given intermediate layout processing
     * slot.
     * 
     * @param slotIndex
     *            the slot index. One of the constants defined in
     *            {@link IntermediateProcessingStrategy}.
     * @return list of layout processors.
     */
    
    private List<ILayoutProcessor> getIntermediateProcessorList(final int slotIndex) {
        // fetch the set of layout processors configured for the given slot
        Set<IntermediateProcessorStrategy> processors = intermediateProcessingConfiguration
                .getProcessors(slotIndex);
        List<ILayoutProcessor> result = new ArrayList<ILayoutProcessor>(processors.size());

        // iterate through the layout processors and add them to the result list
        for (IntermediateProcessorStrategy processor : processors) {
            // check if an instance of the given layout processor is already in the cache
            ILayoutProcessor processorImpl = intermediateLayoutProcessorCache.get(processor);

            if (processorImpl == null) {
                // It's not in the cache, so create it and put it in the cache
                processorImpl = processor.create();
                intermediateLayoutProcessorCache.put(processor, processorImpl);
            }

            // add the layout processor to the list of processors for this slot
            result.add(processorImpl);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void layout(final ElkNode kgraph, final IElkProgressMonitor progressMonitor) {
        progressMonitor.begin("Orthogonal Layout", 1);

        // KGraph -> PGraph conversion
        PGraph pgraph = PGraphFactory.createGraphFromKGraph(kgraph);

        // split the input graph into components and perform the layout for each component
        //List<PGraph> components = componentsProcessor.split(pgraph);
        //for (PGraph comp : components) {

            // update the modules

            // Exception handling for graphs with only one node and without an edge.
            // Create a 1x1 grid and set the graph node to position (0,0).
        // // A node with no edges causes problems everytime the edge angles of a node a needed.
        // if (pgraph.getAdjacentEdgeCount() == 0 && pgraph.getNodeCount() == 1) {
        // GridRepresentation grid = new GridRepresentation(1, 1);
        // grid.set(0, 0, pgraph.getNodes().iterator().next());
        // pgraph.setProperty(Properties.GRID_REPRESENTATION, grid);
        // } else {
        // layout(comp, null);
        // }
        // }

        // for testing without components processor.
        updateModules(pgraph);
        if (pgraph.getAdjacentEdgeCount() == 0 && pgraph.getNodeCount() == 1) {
            GridRepresentation grid = new GridRepresentation(1, 1);
            grid.set(0, 0, pgraph.getNodes().iterator().next());
            pgraph.setProperty(Properties.GRID_REPRESENTATION, grid);
        } else {
            layout(pgraph, null);
        }
        
        // pack the components back into one graph
       // pgraph = componentsProcessor.pack(components);

        // apply the layout results to the original graph
        PGraphFactory.applyLayout(pgraph);

        progressMonitor.done();
    }

    /**
     * @param graph
     */
    private void updateModules(final PGraph graph) {

        // check which planarity test algorithm should be used
        if (graph.getProperty(Properties.PLANAR_TESTING_ALGORITHM)
                == PlanarityTestStrategy.BOYER_MYRVOLD_ALGORITHM) {
            if (!(this.subgraphBuilder instanceof BoyerMyrvoldPlanarSubgraphBuilder)) {
                this.subgraphBuilder = new BoyerMyrvoldPlanarSubgraphBuilder();
            }
        } else {
            if (!(this.subgraphBuilder instanceof LRPlanarSubgraphBuilder)) {
                this.subgraphBuilder = new LRPlanarSubgraphBuilder();
            }
        }

        // update intermediate processor strategy
        intermediateProcessingConfiguration.clear();
        intermediateProcessingConfiguration
                .addAll(subgraphBuilder.getIntermediateProcessingStrategy(graph))
                .addAll(edgeInserter.getIntermediateProcessingStrategy(graph))
                .addAll(orthogonalizer.getIntermediateProcessingStrategy(graph))
                .addAll(compactor.getIntermediateProcessingStrategy(graph))
                .addAll(this.getIntermediateProcessingStrategy(graph));

        // construct the list of processors that make up the algorithm
        algorithm.clear();
        algorithm.addAll(getIntermediateProcessorList(
                IntermediateProcessingConfiguration.BEFORE_PHASE_1));
        algorithm.add(subgraphBuilder);
        algorithm.addAll(getIntermediateProcessorList(
                IntermediateProcessingConfiguration.BEFORE_PHASE_2));
        algorithm.add(edgeInserter);
        algorithm.addAll(getIntermediateProcessorList(
                IntermediateProcessingConfiguration.BEFORE_PHASE_3));
        algorithm.add(orthogonalizer);
        algorithm.addAll(getIntermediateProcessorList(
                IntermediateProcessingConfiguration.BEFORE_PHASE_4));
        algorithm.add(compactor);
        algorithm.addAll(getIntermediateProcessorList(
                IntermediateProcessingConfiguration.AFTER_PHASE_4));
    }

    // ======================================= Layout ===============================

    /**
     * Perform the four phases of the topology-shape-metrics layouter.
     * 
     * @param graph
     *            the graph that is to be laid out
     * @param themonitor
     *            a progress monitor, or {@code null}
     */
    public void layout(final PGraph graph, final IElkProgressMonitor themonitor) {
        updateModules(graph);
        //System.out.println("dsadsdsadsadssadsad");
        IElkProgressMonitor monitor = themonitor;
        if (monitor == null) {
            monitor = new BasicProgressMonitor();
        }
        monitor.begin("Component Layout", algorithm.size());

        if (graph.getProperty(CoreOptions.DEBUG_MODE)) {
            PUtil.clearTmpDir();
            // Debug Mode!
            // Prints the algorithm configuration and outputs the whole graph to a file
            // before each slot execution

            System.out.println("KLay planar uses the following " + algorithm.size() + " modules:");
            for (int i = 0; i < algorithm.size(); i++) {
                System.out.println("   Slot " + String.format("%1$02d", i) + ": "
                        + algorithm.get(i).getClass().getName());
            }

            // invoke each layout processor
            int slotIndex = 0;
            //System.out.println("graph:\n" + graph.toString());
            for (ILayoutProcessor processor : algorithm) {
                if (monitor.isCanceled()) {
                    return;
                }
                processor.process(graph, monitor.subTask(1));
                // graph debug output
                PUtil.storeGraph(graph, slotIndex++, false);
            }

        } else {
            // invoke each layout processor
            //System.out.println( "else processor");
            for (ILayoutProcessor processor : algorithm) {
                /*if (monitor.isCanceled()) {
                    return;
                }*/
                processor.process(graph, monitor.subTask(1));

                
                if( processor.toString().contains("GridDrawing")){
                    Collection < PEdge > edges = graph.getEdges();
                    String [] edgeId = new String [edges.size() * 2];
                    int counter = 0; 
                    //System.out.println( edges.size());
                    for( PEdge edge : edges){
                        /*System.out.print( edge.getSource().id + " ");
                        System.out.print( edge.getTarget().id + " ");*/
                        String sourceString = "";
                        if( edge.getSource().id >=0 && edge.getSource().getProperty(Properties.PLANAR_DUMMY_NODE) != null && 
                         edge.getSource().getProperty( Properties.PLANAR_DUMMY_NODE) == true){
                            sourceString = "pd";
                        }
                        else if( edge.getSource().getProperty( Properties.BENDPOINT) != null ){
                            sourceString = "bd";
                        }
                        else if( edge.getSource().getProperty(Properties.RECT_SHAPE_DUMMY) != null &&
                         edge.getSource().getProperty( Properties.RECT_SHAPE_DUMMY) == true){
                            sourceString = "rd";
                        }
                        sourceString += edge.getSource().id;
                        edgeId[counter] = sourceString;
                        sourceString = "";
                        if( edge.getTarget().getProperty(Properties.PLANAR_DUMMY_NODE) != null &&
                         edge.getTarget().getProperty( Properties.PLANAR_DUMMY_NODE) == true){
                            sourceString = "pd";
                        }
                        else if( edge.getTarget().getProperty( Properties.BENDPOINT) != null ){
                            sourceString = "bd";
                        }
                        else if( edge.getTarget().getProperty(Properties.RECT_SHAPE_DUMMY) != null &&
                         edge.getTarget().getProperty( Properties.RECT_SHAPE_DUMMY) == true){
                            sourceString = "rd";
                        }
                        sourceString += edge.getTarget().id;
                        edgeId[counter+1] = sourceString;
                        counter += 2;
                    }
                    JSONArray arr = new JSONArray( edgeId );
                    System.out.println(arr.toString());
                    if( graph.getProperty(Properties.GRID_REPRESENTATION) != null)
                    graph.getProperty( Properties.GRID_REPRESENTATION).printGrid(true);
                   
                   // System.out.println(edgeId.toString()); // JSON Output
                }
                
                /*if( graph.getProperty(Properties.GRID_REPRESENTATION) != null)
                graph.getProperty( Properties.GRID_REPRESENTATION).printGrid();*/
            }
        }
        //PGraphFactory.applyLayout(graph);

        monitor.done();
    }

}
