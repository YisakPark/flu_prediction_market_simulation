package flu.market.simulation;


import javafx.application.Application; 
import javafx.scene.Group; 
import javafx.scene.Scene; 
import javafx.stage.Stage; 
import javafx.scene.chart.LineChart; 
import javafx.scene.chart.NumberAxis; 
import javafx.scene.chart.XYChart; 
         
public class PopulationLineChart extends Application {     
    static XYChart.Series S_population_rate_series;
    static XYChart.Series I_population_rate_series;
    static XYChart.Series R_population_rate_series;
    static XYChart.Series estimated_flu_population_rate_series;
    
   @Override 
   public void start(Stage stage) {
      //Defining the x axis             
      NumberAxis xAxis = new NumberAxis(1960, 2020, 10); 
      xAxis.setLabel("day"); 
        
      //Defining the y axis   
      NumberAxis yAxis = new NumberAxis   (0, 350, 50); 
      yAxis.setLabel("population rate (%)"); 
        
      //Creating the line chart 
      LineChart linechart = new LineChart(xAxis, yAxis);  
            
      //Setting the data to Line chart    
      linechart.getData().add(S_population_rate_series);        
        
      //Creating a Group object  
      Group root = new Group(linechart); 
         
      //Creating a scene object 
      Scene scene = new Scene(root, 600, 400);  
      
      //Setting title to the Stage 
      stage.setTitle(""); 
         
      //Adding scene to the stage 
      stage.setScene(scene);
	   
      //Displaying the contents of the stage 
      stage.show();         
   } 
}
