/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flu.market.simulation;

/**
 *
 * @author YisakPark
 */
public class TrainingSample {
    float mean;
    float std_dev;
    float flu_population_rate;
    
    public TrainingSample(float _mean, float _std_dev, float _flu_population_rate){
        mean = _mean;
        std_dev = _std_dev;
        flu_population_rate = _flu_population_rate;
    }
}
