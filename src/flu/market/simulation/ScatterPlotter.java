/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flu.market.simulation;


import java.awt.Color;
import java.awt.Shape;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.util.ShapeUtils;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * @author imssbora
 */
public class ScatterPlotter extends JFrame {
  private static final long serialVersionUID = 6294689542092367723L;

  public ScatterPlotter(String title, String x_axis, String y_axis, XYSeries series, float point_size) {
    super(title);
    
    XYSeriesCollection dataset = new XYSeriesCollection();
    dataset.addSeries(series);


    // Create chart
    JFreeChart chart = ChartFactory.createScatterPlot(
        title, 
        x_axis, y_axis, dataset);

    
    //Changes background color
    XYPlot plot = (XYPlot)chart.getPlot();
    plot.setBackgroundPaint(new Color(255,228,196));
    
    //Change dot on the plot
    Shape diamond = ShapeUtils.createDiamond(point_size);
    XYItemRenderer renderer = plot.getRenderer();
    renderer.setSeriesShape(0, diamond);
    
    // Create Panel
    ChartPanel panel = new ChartPanel(chart);
    setContentPane(panel);
  }
  
    public void show_scatter(){
        this.setSize(800, 400);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setVisible(true);
    }
}
