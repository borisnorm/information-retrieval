import java.awt.Frame;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.math.plot.Plot2DPanel;


/**
 * Helper class to plot graph in a JFrame
 * 
 * @author Adib
 */
public class PlotGraph {

	/**
	 * Plot a 2D graph for the given axis
	 * 
	 * @param x X-axis
	 * @param y Y-axis
	 */
	public static void plotGraph(double[] x, double[] y) {
		// Create plot panel
		Plot2DPanel plot = new Plot2DPanel();
		
		// Minimum bound is X:0 Y:0
		double[] minBound = {0, 0};
		// Maximum bound is X:Max Rank Y:Max Probability
		double[] maxBound = {x.length, y[0]};

		// Add line
		plot.addLinePlot("Zipfian Plot", x, y);
		plot.setFixedBounds(minBound, maxBound);
		plot.setAxisLabels("Rank (by decreasing frequency)", "Probability (of occurrence)");
		
		// Put the PlotPanel in a JFrame, as a JPanel
		JFrame frame = new JFrame("Zipfian Curve");
		frame.setContentPane(plot);
		frame.setVisible(true);
		
		// Maximize frame
		frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		
		// Exit on close
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}
	
}
