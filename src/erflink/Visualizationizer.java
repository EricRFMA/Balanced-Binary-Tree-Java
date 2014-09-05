/**
 * Created by eric on 4/17/14.
 */

package erflink;
import prefuse.Constants;
import prefuse.action.ActionList;
import prefuse.action.assignment.FontAction;
import prefuse.action.assignment.StrokeAction;
import prefuse.action.layout.graph.NodeLinkTreeLayout;
import prefuse.activity.Activity;
import prefuse.data.Edge;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.Visualization;
import prefuse.data.expression.parser.ExpressionParser;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.EdgeRenderer;
import prefuse.render.LabelRenderer;
import prefuse.Display;
import prefuse.action.RepaintAction;
import prefuse.controls.*;
import prefuse.action.assignment.ColorAction;
import prefuse.util.ColorLib;
import prefuse.visual.VisualItem;

import javax.swing.*;
import java.awt.*;

public class Visualizationizer
{
    private final String NODELABEL = "nodeLabel";
    private final String EDGELABEL = "edgeLabel";
    private final String GRAPHNAME = "BinarySearchTree";
    private final String NODECOLOR = "nodeColor";
    private final String INVISIBLE = "@@INVISIBLE@@";  // can't be an actual node value

    // Set a font for the graph node
    private final Font theFont = new Font("Sans Serif", Font.BOLD, 18 );

    public Visualizationizer(SearchTree<StringNode> theTree)
    {
        theSearchTree = theTree;
    }

    /**
     * Takes care of building the tree graph and building the visualisation
     * scaffolding for prefuse
     */
    public void makeMagicHappen()
    {
        theTreeGraph = new Graph(true);
        theTreeGraph.addColumn(NODELABEL, String.class);
        theTreeGraph.addColumn(EDGELABEL, String.class);
        theTreeGraph.addColumn(NODECOLOR, String.class);

        buildTheTree((StringNode)theSearchTree.getRoot(),  null);

        // Here's the magical part.  First, we need a prefuse Visualization
        Visualization vis =  new Visualization();
        vis.add(GRAPHNAME, theTreeGraph);

        // Next step, create a renderer for the labels and edges
        EdgeRenderer er = new EdgeRenderer(Constants.EDGE_TYPE_LINE,
                                            Constants.EDGE_ARROW_FORWARD);
        er.setArrowHeadSize(20, 15);

        LabelRenderer lr = new LabelRenderer(NODELABEL);

        // Add a new renderer factory for the labels
        vis.setRendererFactory(new DefaultRendererFactory(lr, er));

        // Create an ActionList for an animated layout
        ActionList layout = new ActionList(Activity.INFINITY);
        NodeLinkTreeLayout nltl = new NodeLinkTreeLayout(GRAPHNAME);
        nltl.setOrientation(Constants.ORIENT_TOP_BOTTOM);
      //  nltl.setBreadthSpacing(-50.0);
     //   nltl.setDepthSpacing(5.0);
        layout.add(nltl);
        layout.add(new RepaintAction());

        // Add the action list to the visualization
        vis.putAction("layout", layout);

        // Add some color (or else it's ALL white!)

        // Make red nodes have red fill with light font, and black nodes black fill with white font
        ColorAction fill2 = new ColorAction(GRAPHNAME + ".nodes", ExpressionParser.predicate(NODECOLOR + " == \"RED\""),
                VisualItem.FILLCOLOR, ColorLib.color(new Color(255, 0, 0)));
        ColorAction fill3 = new ColorAction(GRAPHNAME + ".nodes", ExpressionParser.predicate(NODECOLOR + " == \"BLACK\""),
                VisualItem.FILLCOLOR, ColorLib.color(new Color(0, 0, 0)));

        ColorAction text2 = new ColorAction(GRAPHNAME + ".nodes", ExpressionParser.predicate(NODECOLOR + " == \"RED\""),
                VisualItem.TEXTCOLOR, ColorLib.color(new Color(255, 255, 128)));
        ColorAction text3 = new ColorAction(GRAPHNAME + ".nodes", ExpressionParser.predicate(NODECOLOR + " == \"BLACK\""),
                VisualItem.TEXTCOLOR, ColorLib.color(new Color(192, 255, 255)));

        ColorAction edges = new ColorAction(GRAPHNAME + ".edges", ExpressionParser.predicate(EDGELABEL + "!= \"" + INVISIBLE + "\""),
                VisualItem.STROKECOLOR, ColorLib.color(new Color(70, 70, 70)));
        ColorAction edgeFill = new ColorAction(GRAPHNAME + ".edges", ExpressionParser.predicate(EDGELABEL + "!= \"" + INVISIBLE + "\""),
                VisualItem.FILLCOLOR, ColorLib.color(new Color(0 ,0, 0)));

        // Make invisible nodes invisible
        ColorAction fill1 = new ColorAction(GRAPHNAME + ".nodes", ExpressionParser.predicate(NODELABEL + " == \"" + INVISIBLE + "\""),
                VisualItem.FILLCOLOR, ColorLib.color(new Color(255, 255, 255)));
        ColorAction text1 = new ColorAction(GRAPHNAME + ".nodes", ExpressionParser.predicate(NODELABEL + "== \"" + INVISIBLE + "\""),
                VisualItem.TEXTCOLOR, ColorLib.color(new Color(255, 255, 255)));

        // create an action list containing all color assignments
        ActionList color = new ActionList();
        color.add(fill2);
        color.add(fill3);
        color.add(text2);
        color.add(text3);
        color.add(fill1);
        color.add(text1);
        color.add(edges);
        color.add(edgeFill);

        vis.putAction("color", color);

        // Make larger font
        FontAction nodeFont = new FontAction(GRAPHNAME + ".nodes", theFont);

        ActionList fonts = new ActionList();
        fonts.add (nodeFont);

        vis.putAction("fonts", fonts);

        // Make edges thicker
        StrokeAction stroke = new StrokeAction(GRAPHNAME + ".edges", new BasicStroke(3));

        ActionList strokes = new ActionList();
        strokes.add(stroke);

        vis.putAction("strokes", stroke);

        // Create a new display for our visualization
        Display display = new Display(vis);
        display.setSize(720, 500); // set display size
        display.addControlListener(new DragControl()); // drag items around
        display.addControlListener(new PanControl());  // pan with background left-drag
        display.addControlListener(new ZoomControl()); // zoom with vertical right-drag

        // Now... MAKE IT GO!
        JFrame frame = new JFrame("Binary Search Tree");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(display);
        frame.pack();           // layout components in window
        frame.setVisible(true); // show the window

        vis.run("color");  // make colors!
        vis.run("fonts");
        vis.run("layout"); // start up the animated layout
        vis.run("strokes");
    }

    /**
     *
     * @param theNode The SearchTree node we're building from
     * @param theTreeGraphNode The prefuse node that corresponds to theNode.  Null if the root.
     */
    private void buildTheTree(StringNode theNode, Node theTreeGraphNode)
    {
        assert theNode != null : "buildTheTree: theNode is null";

        // Add the node itself if it's the root
        if (theTreeGraphNode == null)
        {
            theTreeGraphNode = theTreeGraph.addNode();

            theTreeGraphNode.setString(NODELABEL, theNode.getStringValue());

            if (theNode.isRed())
                theTreeGraphNode.setString(NODECOLOR, "RED");
            else
                theTreeGraphNode.setString(NODECOLOR, "BLACK");
        }


        // Now add the children

        // Create the left node... even if there isn't one.
        // Create an invisible left node if there's no left branch, so the
        // generated graph shows reasonable left/right links
        Node newLeftNode = theTreeGraph.addNode();
        boolean invisible = false;

        if (theNode.leftNode != null)
        {
            newLeftNode.setString(NODELABEL, ((StringNode) (theNode.leftNode)).getStringValue());
            if (theNode.isRed())
                newLeftNode.setString(NODECOLOR, "RED");
            else
                newLeftNode.setString(NODECOLOR, "BLACK");
        }
        else
        {
            newLeftNode.setString(NODELABEL, INVISIBLE);
            newLeftNode.setString(NODECOLOR, INVISIBLE);
            invisible = true;
        }

        // Add edges
        Edge anEdge = theTreeGraph.addEdge(theTreeGraphNode, newLeftNode);
        if (invisible)
            anEdge.set(EDGELABEL, INVISIBLE);

        // Do the rest of the left subgraph

        if (theNode.leftNode != null)
            buildTheTree((StringNode) theNode.leftNode, newLeftNode);

        // Ditto for the right branch/node
        Node newRightNode = theTreeGraph.addNode();

        invisible = false;
        if (theNode.rightNode != null)
        {
            newRightNode.setString(NODELABEL, ((StringNode) (theNode.rightNode)).getStringValue());

            if (theNode.isRed())
                newRightNode.setString(NODECOLOR, "RED");
            else
                newRightNode.setString(NODECOLOR, "BLACK");
        }
        else
        {
            newRightNode.setString(NODELABEL, INVISIBLE);
            newRightNode.setString(NODECOLOR, INVISIBLE);
            invisible = true;
        }

        // Add edges
        anEdge = theTreeGraph.addEdge(theTreeGraphNode, newRightNode);

        if (invisible)
            anEdge.set(EDGELABEL, INVISIBLE);

        // Do the rest of the right subgraph
        if (theNode.rightNode != null)
            buildTheTree((StringNode)theNode.rightNode, newRightNode);
            

    }

    Graph theTreeGraph;
    SearchTree theSearchTree;
}
