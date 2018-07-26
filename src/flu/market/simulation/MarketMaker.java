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
    int total_buildings; //number of total_buildings
    int total_days;
    float liquidity_param;
    SecurityGroup[] security_groups; //index of security group of array is the id of the security group
    Building[] buildings;
    int total_population_per_building;
    float market_participant_rate_per_building;
    float initial_money_resident;
    float infection_rate;
    float recovery_rate;
    float time_scale;
    int initial_population_S_per_building;
    int initial_population_I_per_building;
    int initial_population_R_per_building;
    
    public MarketMaker(int _total_buildings, int _total_days, float _liquidity_param, int _total_population_per_building,
            float _market_participant_rate_per_building, float _initial_money_resident, float _infection_rate, float _recovery_rate,
            float _time_scale, int _initial_population_S_per_building, int _initial_population_I_per_building, int _initial_population_R_per_building){
        total_buildings = _total_buildings;
        total_days = _total_days;
        liquidity_param = _liquidity_param;
        security_groups = new SecurityGroup[total_days * total_buildings];
        
        //initialize security_groups
        int i = 0;
        for(int day=0; day<total_days; day++){
            for(int building=0; building<total_buildings; building++){
                security_groups[i] = new SecurityGroup(day, building, 0, 0);
                i++;
            }
        }
        
        total_population_per_building = _total_population_per_building;
        market_participant_rate_per_building = _market_participant_rate_per_building;
        initial_money_resident = _initial_money_resident;
        infection_rate = _infection_rate;
        recovery_rate = _recovery_rate;
        time_scale = _time_scale;
        initial_population_S_per_building = _initial_population_S_per_building;
        initial_population_I_per_building = _initial_population_I_per_building;
        initial_population_R_per_building = _initial_population_R_per_building;        
        
        buildings = new Building[total_buildings];
        //initialize building objects
        for (int j = 0; j < total_buildings; j++) {
            buildings[j] = new Building(j, total_population_per_building, market_participant_rate_per_building, initial_money_resident,
                    infection_rate, recovery_rate, time_scale, initial_population_S_per_building,
                    initial_population_I_per_building, initial_population_R_per_building, total_buildings);
        }
    }
    
    //get the cost to buy or sell the shares of security.
    //'market_date': market date whose security shares will be traded
    //'security_group_ids': the array of security group ids that the securities that user want to buy or sell belongs to
    //'quantity': quantity of shares of securities. If it is positive, buying shares, otherwise selling shares
    public float get_cost(int market_date, int security_group_id, int quantity){
        float prior_investment_amount;
        float posterior_investment_amount;
        
        prior_investment_amount = get_investment_amount(market_date);
        posterior_investment_amount = get_investment_amount(market_date, security_group_id, quantity);
        return Math.abs(posterior_investment_amount - prior_investment_amount); 
    }

    //get the investment amount of the market specified by the 'market_date'
    public float get_investment_amount(int market_date){
        float value_inside_log = 0;
        int idx_first_elem;
        
        idx_first_elem = get_idx_of_first_elem_of_block(market_date);
        
        for(int i=0; i<total_buildings; i++){
            value_inside_log += Math.exp(security_groups[idx_first_elem + i].shares / liquidity_param);
        }
        
        
        return (float) (liquidity_param * Math.log(value_inside_log));
    }
    
    //get the updated investment amount of the market
    public float get_investment_amount(int market_date, int security_group_id, int quantity){
        float value_inside_log = 0;
        int idx_first_elem;
        int updated_quantity;
        
        idx_first_elem = get_idx_of_first_elem_of_block(market_date);
        updated_quantity = security_groups[security_group_id].shares + quantity;
        
        for(int i=0; i<total_buildings; i++){
            if(security_group_id == (idx_first_elem + i)){
                value_inside_log += Math.exp(updated_quantity / liquidity_param);
            }
            else{
                value_inside_log += Math.exp(security_groups[idx_first_elem+i].shares / liquidity_param);
            }            
        }
                
        return (float) (liquidity_param * Math.log(value_inside_log));
    }
    
    
    
    //the array 'security_groups' can be divided into block of securiy_groups which of same market date
    //it returns to the index of the first element of the block specified by 'market_date'
    public int get_idx_of_first_elem_of_block(int market_date){
        return market_date * total_buildings;
    }

    //update price of the security groups specified by 'market_date'
    public void update_price(int market_date){
        float denominator = 0;
        float numerator;
        int idx_first_elem = get_idx_of_first_elem_of_block(market_date);
        
        //get denominator
        for(int i=0; i<total_buildings; i++){
            denominator += Math.exp(security_groups[idx_first_elem + i].shares / liquidity_param);
        }
        
        //get numerator for each security group
        for(int i=0; i<total_buildings; i++){
            numerator = (float) Math.exp(security_groups[idx_first_elem + i].shares / liquidity_param);
            security_groups[idx_first_elem + i].price = numerator / denominator;
        }
    }
    
    //buying the shares
    //return false, if the buying fails, otherwise true.
    public boolean buy_process(int security_group_id, int quantity, float flu_population_rate, int buyer_building_id, int resident_id){
        Share share = new Share(security_group_id, flu_population_rate, quantity);
        int market_date = security_groups[security_group_id].market_date;
        //if quantity is <= 0 or buyer cannot affordable, return false
        if(quantity <= 0 || buildings[buyer_building_id].residents[resident_id].money < get_cost(market_date, security_group_id, quantity))
            return false;
        //update user's current money and shares
        buildings[buyer_building_id].residents[resident_id].money -= get_cost(market_date, security_group_id, quantity);
        buildings[buyer_building_id].residents[resident_id].add_share(share);
        //update market status
        security_groups[security_group_id].shares += quantity;
        security_groups[security_group_id].add_share(share);        
        update_price(market_date);
        return true;
    }
    
    //selling the shares
    public void sell_process(int security_group_id, int quantity, float flu_population_rate, int seller_building_id, int resident_id){
        Share share = new Share(security_group_id, flu_population_rate, -quantity);
        int market_date = security_groups[security_group_id].market_date;
        
        //update user's current money and shares
        buildings[seller_building_id].residents[resident_id].money += get_cost(market_date, security_group_id, -quantity);
        buildings[seller_building_id].residents[resident_id].remove_share(share);
        //update market status
        security_groups[security_group_id].shares -= quantity;
        security_groups[security_group_id].remove_share(share);
        update_price(market_date);
    }
    
    public int get_security_group_id(int date, int building_id){
        return (date * total_buildings) + building_id;
    }    

}