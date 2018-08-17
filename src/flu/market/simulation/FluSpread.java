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
    int building_id;
    float infection_rate;
    float recovery_rate;
    float time_scale;
    int population_S;
    int population_I;
    int population_R;
    int total_population;
    int total_buildings;
    float __population_S;
    float __population_I;
    float __population_R;
    float[][] contact_matrix;
    Population[] populations;

    
    public FluSpread(int _building_id, float _infection_rate, float _recovery_rate, float _time_scale,
            int _population_S, int _population_I, int _population_R, int _total_population, int _total_buildings,
            float[][] _contact_matrix, Population[] _populations){
        building_id = _building_id;
        infection_rate = _infection_rate;
        recovery_rate = _recovery_rate;
        time_scale = _time_scale;
        population_S = _population_S;
        population_I = _population_I;
        population_R = _population_R;
        total_population = _total_population;
        total_buildings = _total_buildings;
        __population_S = (float) population_S;
        __population_I = (float) population_I;
        __population_R = (float) population_R;
        contact_matrix = _contact_matrix;
        populations = _populations;
    }
    
    //update population of SIR using Euler's method
    public void update_population(){
        float change_in_S = 0;
        float change_in_I = 0;
        
        for(int i=0; i<total_buildings; i++){
            change_in_S += infection_rate * contact_matrix[building_id][i] * populations[i].population_I / populations[i].total_population * __population_S;
        }
        change_in_S *= -1;
        
        change_in_I += (-change_in_S) - (recovery_rate * __population_I); 
        
                
        float new_population_S = ( __population_S + (change_in_S * time_scale));
        float new_population_I = ( __population_I + (change_in_I * time_scale));        
        
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
    
    float get_S_rate(){
        return population_S / (float) total_population * 100;
    }
    
    float get_I_rate(){
        return population_I / (float) total_population * 100;
    }
    
    float get_R_rate(){
        return population_R / (float) total_population * 100;
    }
    
}
