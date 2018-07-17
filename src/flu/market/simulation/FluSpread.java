/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flu.market.simulation;

/**
 *
 * @author yisak
 */
public class FluSpread {
    float infection_rate;
    float recovery_rate;
    float time_scale;
    int population_S;
    int population_I;
    int population_R;
    int total_population;
    
    public FluSpread(float _infection_rate, float _recovery_rate, float _time_scale,
            int _population_S, int _population_I, int _population_R, int _total_population){
        infection_rate = _infection_rate;
        recovery_rate = _recovery_rate;
        time_scale = _time_scale;
        population_S = _population_S;
        population_I = _population_I;
        population_R = _population_R;
        total_population = _total_population;
    }
    
    //update population of SIR using Euler's method
    public void update_population(){
        int new_population_S = (int)( population_S - (infection_rate*population_S*population_I*time_scale));
        int new_population_I = (int)( population_I + (infection_rate*population_S*population_I - recovery_rate*population_I)*time_scale);        
        
        if(new_population_S < 0)
            population_S = 0;
        else if(new_population_S > total_population)
            population_S = total_population;
        else
            population_S = new_population_S;

        if(new_population_I < 0)
            population_I = 0;
        else if(new_population_I > total_population)
            population_I = total_population;
        else
            population_I = new_population_I;
        
        population_R = total_population - population_S - population_I;
    }
}
