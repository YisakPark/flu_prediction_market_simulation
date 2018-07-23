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
    float liquidity_param;
    SecurityGroup[] security_groups; //index of security group of array is the id of the security group
    
    public MarketMaker(int _buildings, int _days, float _liquidity_param){
        buildings = _buildings;
        days = _days;
        liquidity_param = _liquidity_param;
        security_groups = new SecurityGroup[days * buildings];
        
        //initialize security_groups
        int i = 0;
        for(int day=0; day<days; day++){
            for(int building=0; building<buildings; building++){
                security_groups[i] = new SecurityGroup(day, building, 0, 0);
                i++;
            }
        }
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
        int idx_first_elem;
        
        idx_first_elem = get_idx_of_first_elem_of_block(date_market);
        
        for(int i=0; i<buildings; i++){
            value_inside_log += Math.exp(security_groups[idx_first_elem + i].shares / liquidity_param);
        }
        
        
        return (float) (liquidity_param * Math.log(value_inside_log));
    }
    
    //get the updated investment amount of the market
    public float get_investment_amount(int date_market, int[] security_group_ids, int quantity){
        float value_inside_log = 0;
        int idx_first_elem;
        int[] updated_quantity_arr = new int[security_group_ids.length]; //updated_quantity_arr[x] is the updated quantity of security group security_group_ids[x]        
        
        idx_first_elem = get_idx_of_first_elem_of_block(date_market);
        
        //initialize updated_quantity_arr
        for(int i=0; i<security_group_ids.length; i++){
            int shares_before_update = security_groups[security_group_ids[i]].shares;
            updated_quantity_arr[i] = shares_before_update + quantity;
        }
        
        for(int i=0; i<buildings; i++){
            int j = doesContain(i + idx_first_elem, security_group_ids);
            
            if(j != -1){
                value_inside_log += Math.exp(updated_quantity_arr[j] / liquidity_param);
            }
            else{
                value_inside_log += Math.exp(security_groups[idx_first_elem+i].shares / liquidity_param);
            }            
        }
        
        
        return (float) (liquidity_param * Math.log(value_inside_log));
    }
    
    //check whether 'security_group_ids' contain 'security_group_id'
    //if it contains, return the index of 'security_group_ids' which points 'security_group_id'
    //otherwise, return -1
    int doesContain(int security_group_id, int[] security_group_ids){
        for(int i=0; i<security_group_ids.length; i++){
            if(security_group_ids[i] == security_group_id)
                return i;
        }
        return -1;
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
