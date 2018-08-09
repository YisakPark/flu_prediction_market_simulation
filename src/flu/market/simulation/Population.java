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
public class Population {
    int building_id;
    int population_I;
    int total_population;
    
    public Population(int _building_id, int _population_I, int _total_population){
        building_id = _building_id;
        population_I = _population_I;
        total_population = _total_population;
    }
}
