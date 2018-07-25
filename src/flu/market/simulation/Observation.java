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
public class Observation {
    int building_id;
    float observed_flu_rate;
    
    public Observation (int _building_id, float _observed_flu_rate){
        building_id = _building_id;
        observed_flu_rate = _observed_flu_rate;
    }
}
