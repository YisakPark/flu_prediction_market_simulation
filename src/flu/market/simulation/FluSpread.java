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
    float __population_S;
    float __population_I;
    float __population_R;

    
    public FluSpread(float _infection_rate, float _recovery_rate, float _time_scale,
            int _population_S, int _population_I, int _population_R, int _total_population){
        infection_rate = _infection_rate;
        recovery_rate = _recovery_rate;
        time_scale = _time_scale;
        population_S = _population_S;
        population_I = _population_I;
        population_R = _population_R;
        total_population = _total_population;
        __population_S = (float) population_S;
        __population_I = (float) population_I;
        __population_R = (float) population_R;
    }
    
    //update population of SIR using Euler's method
    public void update_population(){
        float new_population_S = ( __population_S - (infection_rate*__population_S*__population_I*time_scale));
        float new_population_I = ( __population_I + (infection_rate*__population_S*__population_I - 
                recovery_rate*__population_I)*time_scale);        
        
        if(new_population_S < 0)
            new_population_S = 0;
        else if(new_population_S > total_population)
            new_population_S = total_population;
        
        __population_S = new_population_S;
        population_S = (int) __population_S;

        if(new_population_I < 0)
            new_population_I = 0;
        else if(new_population_I > total_population)
            new_population_I = total_population;

        __population_I = new_population_I;
        population_I = (int) __population_I;
                
        population_R = total_population - population_S - population_I;                
    }
}
