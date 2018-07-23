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
public class Share {
    int security_group_id;
    float flu_population_rate;
    int quantity;
    
    public Share(int _security_group_id, float _flu_population_rate, int _quantity){
        security_group_id = _security_group_id;
        flu_population_rate = _flu_population_rate;
        quantity = _quantity;
    }
}
