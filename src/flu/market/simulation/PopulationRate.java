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
public class PopulationRate {
    int day;
    float S_population_rate;
    float I_population_rate;
    float R_population_rate;
    float estimated_flu_population_rate;
    
    public PopulationRate(int _day, float _S_population_rate, float _I_population_rate, float _R_population_rate, 
            float _estimated_flu_population_rate){
        day = _day;
        S_population_rate = _S_population_rate;
        I_population_rate = _I_population_rate;
        R_population_rate = _R_population_rate;
        estimated_flu_population_rate = _estimated_flu_population_rate;
    }
}
