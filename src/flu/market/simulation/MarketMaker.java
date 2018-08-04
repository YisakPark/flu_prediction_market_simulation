/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flu.market.simulation;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import org.jfree.data.xy.XYSeries;

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
    float[] ground_truths; //'ground_truth[x]' is the actual flu patient ratio of the building on the date specified by 'x' which is security group id.
    float[] estimated_ground_truths;
    float EGT_rate; //must be within [0,1]
    Person[] total_participants;
    
    public MarketMaker(int _total_buildings, int _total_days, float _liquidity_param, int _total_population_per_building,
            float _market_participant_rate_per_building, float _initial_money_resident, float _infection_rate, float _recovery_rate,
            float _time_scale, int _initial_population_S_per_building, int _initial_population_I_per_building, int _initial_population_R_per_building,
            float _maximum_observation_error_rate, float _minimum_observation_error_rate, float _EGT_rate){
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
                    infection_rate, recovery_rate, time_scale, initial_population_S_per_building, initial_population_I_per_building, 
                    initial_population_R_per_building, total_buildings, _maximum_observation_error_rate, _minimum_observation_error_rate, total_days);
        }

        ground_truths = new float[total_days * total_buildings];
        estimated_ground_truths = new float[total_days * total_buildings];
       
        EGT_rate = _EGT_rate;
        
        //initialize 'total_participants' array
        int total_market_participants = 0;
        for( int j=0; j<total_buildings; j++){
            total_market_participants += buildings[j].number_market_participants;
        }
        total_participants = new Person[total_market_participants];
        i = 0;
        for( int j=0; j<total_buildings; j++){
            for( int k=0; k<buildings[j].participants.length; k++){
                int participant_idx = buildings[j].participants[k];
                total_participants[i] = buildings[j].residents[participant_idx];
                i++;
            }
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
        return Math.abs(quantity * (posterior_investment_amount - prior_investment_amount)); 
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
        int market_date = security_groups[security_group_id].market_date;
        //if quantity is <= 0 or buyer cannot affordable, return false
        if(quantity <= 0 || buildings[buyer_building_id].residents[resident_id].money < get_cost(market_date, security_group_id, quantity))
            return false;
        //update user's current money and shares
        buildings[buyer_building_id].residents[resident_id].money -= get_cost(market_date, security_group_id, quantity);
        buildings[buyer_building_id].residents[resident_id].suggested_flu_population_rate[security_group_id] = flu_population_rate;
        buildings[buyer_building_id].residents[resident_id].add_share(
                new Share(security_group_id, buyer_building_id, resident_id, 
                flu_population_rate, quantity, security_groups[security_group_id].price));
        //update market status
        security_groups[security_group_id].shares += quantity;
        security_groups[security_group_id].add_share(
                new Share(security_group_id, buyer_building_id, resident_id, 
                flu_population_rate, quantity, security_groups[security_group_id].price));        
        update_price(market_date);
        return true;
    }
    
    /*
    it should return boolean value!!!!
    */    
    //selling the shares
    public void sell_process(int security_group_id, int quantity, float flu_population_rate, int seller_building_id, int resident_id){
        int market_date = security_groups[security_group_id].market_date;
        
        //update user's current money and shares
        float earned_money = get_cost(market_date, security_group_id, -quantity);
        buildings[seller_building_id].residents[resident_id].money += earned_money;
        buildings[seller_building_id].residents[resident_id].money_earned_selling += earned_money;
        buildings[seller_building_id].residents[resident_id].remove_share(
                new Share(security_group_id, seller_building_id, resident_id, 
                flu_population_rate, quantity, security_groups[security_group_id].price));
       //update market status
        security_groups[security_group_id].shares -= quantity;
        security_groups[security_group_id].remove_share(
                new Share(security_group_id, seller_building_id, resident_id, 
                flu_population_rate, quantity, security_groups[security_group_id].price));
        update_price(market_date);
    }
    
    public int get_security_group_id(int date, int building_id){
        return (date * total_buildings) + building_id;
    }    

    //determines payoff per share
    public float payoff_per_share(float truth, float predicted_truth){
        return (float) (Math.exp(-Math.abs(truth - predicted_truth)));
    }
    
    //if it is true, payoff will be determined using EGT
    boolean determine_payoff_with_EGT(){
        return Math.random() <= EGT_rate; 
    }    

    public void sort_total_participants_money_decreasing_order(){
        quickSort_person_arr(total_participants, 0, total_participants.length-1);
    }
    
    //'low' -> starting index, 'high' -> ending indx
    void quickSort_person_arr(Person[] person_arr, int low, int high){
        if(low < high){
            /* pi is partitioning index, arr[p] is now
           at right place */
            int pi = partition(person_arr, low, high);

            quickSort_person_arr(person_arr, low, pi - 1);  // Before pi
            quickSort_person_arr(person_arr, pi + 1, high); // After pi            
        }
    }
    
    /* This function takes last element as pivot, places
   the pivot element at its correct position in sorted
    array, and places all smaller (smaller than pivot)
   to left of pivot and all greater elements to right
   of pivot */
    int partition (Person[] person_arr, int low, int high){
        // pivot (Element to be placed at right position)
        float pivot = person_arr[high].money;  
 
        int i = (low - 1);  // Index of smaller element

        for (int j = low; j <= high- 1; j++){
            // If current element is greater than or
            // equal to pivot
            if (person_arr[j].money >= pivot){
                i++;    // increment index of greater element
                //swap person_arr[i] and person_arr[j]
                Person temp = person_arr[i];
                person_arr[i] = person_arr[j];
                person_arr[j] = temp;
        }
        }
        //swap person_arr[i+1] and person_arr[high]
        Person temp = person_arr[i+1];
        person_arr[i+1] = person_arr[high];
        person_arr[high] = temp;

        return (i + 1);
    }
    
    
    public void show_betting_result(){
        float dot_size = 1;
        XYSeries series_money = new XYSeries("");
        XYSeries series_price = new XYSeries("");
        XYSeries series_euclidean = new XYSeries("");
        XYSeries series_quantity = new XYSeries("");
        XYSeries series_money_selling = new XYSeries("");
        
        
        System.out.println("This is the result of the market");
        for (int i=0; i<total_participants.length; i++) {
            //get the average price of securities that user bought
            float average_price = 0;
            int quantity = 0;
            for (int j=0; j<total_participants[i].share_list.size(); j++){
                average_price += total_participants[i].share_list.get(j).price_of_security;
                quantity += total_participants[i].share_list.get(j).quantity;
            }
            average_price = average_price / total_participants[i].share_list.size();
            
            series_money.add(i+1, total_participants[i].money);
            series_price.add(i+1, average_price);
            series_euclidean.add(i+1, get_euclidean_distance(total_participants[i].suggested_flu_population_rate, ground_truths));
            series_quantity.add(i+1, quantity);   
            series_money_selling.add(i+1, total_participants[i].money_earned_selling);
        }   
        
        ScatterPlotter scatter_money = new ScatterPlotter("Money user earned",
                "rank of user","money user earned",series_money, dot_size);
        ScatterPlotter scatter_price = new ScatterPlotter("Average price of securities that user bought", 
                "rank of user","average price",series_price, dot_size);
        ScatterPlotter scatter_euclidean = new ScatterPlotter("euclidiean distance between the flu population rates that user predicted and the actual flu population rates",
                "rank of user","euclidean distance", series_euclidean, dot_size);
        ScatterPlotter scatter_quantity = new ScatterPlotter("total quantities of shares user bought",
                "rank of user","total quantities of shares",series_quantity, dot_size);
        ScatterPlotter scatter_money_selling = new ScatterPlotter("Sum of money user earned by selling shares",
                "rank of user","money earned by selling",series_money_selling, dot_size);

        scatter_money.show_scatter();
        scatter_price.show_scatter();
        scatter_euclidean.show_scatter();
        scatter_quantity.show_scatter();
        scatter_money_selling.show_scatter();

        /*
                System.out.println("Top " + (i+1) + " user information,\nmoney user earned: " + total_participants[i].money +
                    ", average price of securities that user bought: " + average_price +
                    ", euclidean distance between his predicted flu population rates and the actual flu population rates: " + 
                    get_euclidean_distance(total_participants[i].suggested_flu_population_rate, ground_truths));
 
            System.out.println("Building: " + total_participants[i].residence + 
                    ", person id: " + total_participants[i].id + 
                    ", money: " + total_participants[i].money +
                    ", euclidean distance: " + get_euclidean_distance(total_participants[i].suggested_flu_population_rate, ground_truths) +
                    ", total quantities of share user bought: " + total_participants[i].get_total_quantities_share());
            */
            /*
            System.out.println("Building: " + total_participant.residence + 
                    ", person id: " + total_participant.id + 
                    ", money: " + total_participant.money +
                    ", money earned by selling shares: " + total_participant.money_earned_selling);
            */        
        System.out.println();
    }

    //Assuming 'total_participants' are sorted according to the money each agent has,
    //this method divide the 'total_participants' from top to bottom.
    //There are 'number_people_division' people in each division
    //It shows the average observation error rate of the people in each division.
    void show_average_observation_error_rate(int number_people_division){
        float sum_observation_error_rate_division = 0;
        int count_people_division = 0;
        for (int i=0; i<total_participants.length; i++){
            sum_observation_error_rate_division += total_participants[i].observation_error_rate;
            count_people_division++;
            if(count_people_division == number_people_division){
                System.out.println("The average observation error rate of top " + (i-number_people_division+2) +
                        " ~ top " + (i+1) + " users: " + (sum_observation_error_rate_division / number_people_division));
                sum_observation_error_rate_division = 0;
                count_people_division = 0;
            }
        }
        System.out.println();
    }
    
    //Assuming 'total_participants' are sorted according to the money each agent has,
    //this method divide the 'total_participants' from top to bottom.
    //There are 'number_people_division' people in each division
    //It shows the average price of securities that user bought in each division
    void show_average_price_users_bought(int number_people_division){
        float sum_price_division = 0;
        float sum_price_user = 0;
        int count_people_division = 0;
        for (int i=0; i<total_participants.length; i++){
            for (int j=0; j<total_participants[i].share_list.size(); j++){
                sum_price_user += total_participants[i].share_list.get(j).price_of_security;
            }
            sum_price_division += sum_price_user / total_participants[i].share_list.size();
            sum_price_user = 0;
            count_people_division++;
            if(count_people_division == number_people_division){
                System.out.println("The average price of securities that top " + (i-number_people_division+2) +
                        " ~ top " + (i+1) + " users bought: " + (sum_price_division / number_people_division));
                sum_price_division = 0;
                count_people_division = 0;                
            }
        }
        System.out.println();        
    }
    
    //show ground truth and estimated ground truth
    void show_GT_EGT(){
        /*
        int size = total_days * total_buildings;
        for( int i=0; i<size; i++){
            System.out.println("On the date " + security_groups[i].market_date +
                    " in the building " + security_groups[i].building_num +
                    ", the difference between the actual flu population rate and the mean of predicted flu population rate  was " 
                    + Math.abs(ground_truths[i] - estimated_ground_truths[i]));
        }
        */
        System.out.println();
        XYSeries series = new XYSeries("");
        int size = total_days * total_buildings;
        for( int i=0; i<size; i++){
            series.add(ground_truths[i]*100, estimated_ground_truths[i]*100);
        }
        ScatterPlotter scatter = new ScatterPlotter("Comparison between the estimated flu population rate and the actual flu population rate",
                "actual flu population rate (%)","estimated flu population rate (%)",series, (float) 1);
        scatter.show_scatter(); 
        
    }
    
    //pick the share whose price is the closest to 'price' among the share list of the person specified by 'building id' and 'resident_id'
    Share pick_share(int building_id, int resident_id, float price){
        Share picked_share = null;
        float price_difference = 1;
        Iterator<Share> itr = buildings[building_id].residents[resident_id].share_list.iterator();
        
        while(itr.hasNext()){
            Share share = itr.next();
            if(price_difference >= Math.abs(security_groups[share.security_group_id].price - price)){
                price_difference = Math.abs(security_groups[share.security_group_id].price - price);
                picked_share = share;
            }
        }
        return picked_share;
    }
    
    void show_euclidean_distance_of_GT_EGT(){
        
        System.out.println("The euclidean distance between the estimated flu population rates and the actual flu population rates is " + 
                get_euclidean_distance(ground_truths, estimated_ground_truths));
        System.out.println();
    }
    
    //get euclidean distance. size of two array 'a' and 'b' must be same
    //if sizes are different it returns -1
    float get_euclidean_distance(float[] a, float[] b){
        if(a.length != b.length)
            return -1;

        int size = a.length;
        float euclidean_distance = 0;
        for( int i=0; i<size; i++){
            euclidean_distance += Math.pow(a[i] - b[i], 2);
        }
        euclidean_distance =  (float) Math.sqrt(euclidean_distance);
        return euclidean_distance;
    }
    
    boolean has_share(int building_id, int resident_id){
        return !buildings[building_id].residents[resident_id].share_list.isEmpty();     
    }
    
    void write_csv_EGT_GT(){
        String COMMA_DELIMITER = ",";
        String NEW_LINE_SEPERATOR = "\n";
        String FILE_HEADER = "Actual Flu Population,Estimated Flu Population";
        
        try{
            FileWriter fileWriter = new FileWriter("EGT_GT.csv");
            fileWriter.append(FILE_HEADER);
            int size = (ground_truths.length==estimated_ground_truths.length) ? ground_truths.length : -1;
            if(size == -1){
                System.out.println("the size of the array 'ground_truths' is not equal to 'estimated_ground_truths'");
                return;
            }               
            for(int i=0; i<size; i++){
                fileWriter.append(NEW_LINE_SEPERATOR);
                fileWriter.append(String.valueOf(ground_truths[i]*100));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(estimated_ground_truths[i]*100));
            }
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e){
            System.out.println(e.getMessage());
        }
    }
}