package util;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

import org.jgraph.JGraph;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphModel;

public class Graph {

    public static void main(String[] args) {

        // Construct Model and Graph
        GraphModel model = new DefaultGraphModel();
        JGraph graph = new JGraph(model);
        // Control-drag should clone selection
        graph.setCloneable(true);

        // Enable edit without final RETURN keystroke
        graph.setInvokesStopCellEditing(true);

        // When over a cell, jump to its default port (we only have one, anyway)
        graph.setJumpToDefaultPort(true);

        // Insert all three cells in one call, so we need an array to store them
        DefaultGraphCell[] cells = new DefaultGraphCell[3];

        // Create Hello Vertex
        cells[0] = createVertex("Hello", 20, 20, 40, 20, null, false );

        // Create World Vertex
        cells[1] = createVertex("World", 140, 140, 40, 20,
                Color.ORANGE, true);

        // Create Edge
        DefaultEdge edge = new DefaultEdge("foo");
        // Fetch the ports from the new vertices, and connect them with the edge
        edge.setSource(cells[0].getChildAt(0));
        edge.setTarget(cells[0].getChildAt(0));
        cells[2] = edge;

        // Create Edge
        DefaultEdge edge1 = new DefaultEdge();
        // Fetch the ports from the new vertices, and connect them with the edge
//        cells[0].addPort();
//        cells[1].addPort();
//        edge1.setSource(cells[1]);
//        edge1.setTarget(cells[0]);
//        cells[3] = edge1;

        // Set Arrow Style for edge
        int arrow = GraphConstants.ARROW_CLASSIC;
        GraphConstants.setLineEnd(edge.getAttributes(), arrow);
        GraphConstants.setEndFill(edge.getAttributes(), true);

        // Insert the cells via the cache, so they get selected
        graph.getGraphLayoutCache().insert(cells);

        // Show in Frame
        JFrame frame = new JFrame();
        frame.getContentPane().add(new JScrollPane(graph));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public static DefaultGraphCell createVertex(String name, double x,
            double y, double w, double h, Color bg, boolean raised) {

        // Create vertex with the given name
        DefaultGraphCell cell = new DefaultGraphCell(name);

        // Set bounds
        GraphConstants.setBounds(cell.getAttributes(),
                new Rectangle2D.Double(x, y, w, h));

        // Set fill color
        if (bg != null) {
            GraphConstants.setGradientColor(cell.getAttributes(), bg);
            GraphConstants.setOpaque(cell.getAttributes(), true);
        }

        // Set raised border
        if (raised) {
            GraphConstants.setBorder(cell.getAttributes(),
                    BorderFactory.createRaisedBevelBorder());
        } else // Set black border
        {
            GraphConstants.setBorderColor(cell.getAttributes(),
                    Color.black);
        }
        // Add a Floating Port
        cell.addPort();

        return cell;
    }
}