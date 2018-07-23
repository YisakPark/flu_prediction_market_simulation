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
public class SecurityGroup {
    int date_market;
    int building_num;
    int shares;
    int price;
    
    public SecurityGroup(int _date_market, int _building_num, int _shares, int _price){
        date_market = _date_market;
        building_num = _building_num;
        shares = _shares;
        price = _price;
    }

}
