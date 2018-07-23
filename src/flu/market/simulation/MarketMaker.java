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
public class MarketMaker {
    int buildings; //number of buildings
    int days;
    SecurityGroup[] security_groups; //index of security group of array is the id of the security group
    
    public MarketMaker(int _buildings, int _days){
        buildings = _buildings;
        days = _days;
        security_groups = new SecurityGroup[days * buildings];
    }
    
    //get the cost to buy or sell the shares of security.
    //'date_market': market date whose security shares will be traded
    //'security_group_ids': the array of security group ids that the securities that user want to buy or sell belongs to
    //'quantity': quantity of shares of securities. If it is positive, buying shares, otherwise selling shares
    public float get_cost(int date_market, int[] security_group_ids, int quantity){
        float prior_investment_amount = 0;
        float posterior_investment_amount = 0;
        
        prior_investment_amount = get_investment_amount(date_market);
        posterior_investment_amount = get_investment_amount(date_market, security_group_ids, quantity);
        return Math.abs(posterior_investment_amount - prior_investment_amount); 
    }

    //get the investment amount of the market specified by the 'date_market'
    public float get_investment_amount(int date_market){
        float value_inside_log = 0;
        int idx_first_elem = 0;
        
        idx_first_elem = get_idx_of_first_elem_of_block(date_market);
        
        return 0;
    }
    
    //get the updated investment amount of the market
    public float get_investment_amount(int date_market, int[] security_group_ids, int quantity){
        return 0;
    }
    
    //the array 'security_groups' can be divided into block of securiy_groups which of same market date
    //it returns to the index of the first element of the block specified by 'date_market'
    public int get_idx_of_first_elem_of_block(int date_market){
        return 0;
    }

    public void update_price(){
        //TODO
    }
    
    public void buy_process(){
        
    }
    
    public void sell_process(){
        
    }
}
