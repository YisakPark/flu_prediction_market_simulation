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
    int buyer_residence;
    int buyer_resident_id;
    float flu_population_rate;
    int quantity;
    float price_of_security; //the price when user buy the shares, not current price
    
    public Share(int _security_group_id, int _buyer_residence, int _buyer_resident_id, float _flu_population_rate, int _quantity, float _price_of_security){
        security_group_id = _security_group_id;
        buyer_residence = _buyer_residence;
        buyer_resident_id = _buyer_resident_id;
        flu_population_rate = _flu_population_rate;
        quantity = _quantity;
        price_of_security = _price_of_security;
    }

}
