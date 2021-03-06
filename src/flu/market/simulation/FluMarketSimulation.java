/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flu.market.simulation;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import org.jfree.data.xy.XYSeries;
import java.lang.Object;
import javafx.application.Application;
import javafx.scene.chart.XYChart;
/**
 *
 * @author yisak
 */
public class FluMarketSimulation {

    public static enum health_state {
        S, I, R
    }
    
    public static enum market_participant_type{
        N, ACCURATE, INACCURATE, PAST_RECORD
    } //N: not participant, ACCURATE: accurate observation participant,
      //INACCURATE: inaccruate observation participant, PAST_RECORD: participant using past record

    public static void main(String[] args) {
        int total_population_per_building = 400;
        int initial_population_S_per_building = 390;
        int initial_population_I_per_building = 10;
        int initial_population_R_per_building = 0;
        float initial_money_resident = 100;
        float market_participant_rate_per_building = (float) 0.3;
        float accurate_observation_participant_rate_per_building = (float) 0.1;
        float inaccurate_observation_participant_rate_per_building = (float) 0.1;
        float past_record_participant_rate_per_building = (float) 0.1;
        validate_population(total_population_per_building, initial_population_S_per_building,
                initial_population_I_per_building, initial_population_R_per_building);
        float infection_rate = (float) 3;
        float recovery_rate = (float) 2.14;//2.14
        float time_scale = (float) 0.14;
        int total_buildings = 20;
        int total_days = 30;
        float liquidity_param = (float)32.0;
        MarketMaker market_maker;
        MarketMakerFlatPrice market_maker_flat_price;
        //the lower the observation error rate, the better agent observe flu population rate close to the actual rate
        float maximum_observation_error_rate = (float) 15;
        float minimum_observation_error_rate = (float) 0; //must be greater than 0 
        float EGT_rate = (float) 0.1; //must be [0,1], We need to determine the actual flu population rate of each building. 
                                    //But the estimated flu population rate will be determined instead for 
                                    //the buildings whose number is set by 'EGT_rate' * 'total_buildings' will 
        float price_per_share_flat_price_market = (float) 0.1;
                                    
        float accurate_observation_gaussian_std_dev = (float) 0;      
        float inaccurate_observation_gaussian_std_dev = (float) 5;      
        float past_record_gaussian_std_dev = (float) 5;              
        float security_flu_rate_gaussian_std_dev = (float) 3;
        float sell_price_gaussian_std_dev = (float) 0.1;
        float quantity_gaussian_mean = (float) 10; 
        float quantity_gaussian_std_dev = (float) 2.4;
        float date_gaussian_std_dev = (float) 0;

        market_maker = new MarketMaker(total_buildings, total_days, liquidity_param, total_population_per_building, 
        accurate_observation_participant_rate_per_building, inaccurate_observation_participant_rate_per_building, 
        past_record_participant_rate_per_building, initial_money_resident, infection_rate, recovery_rate, time_scale, 
        initial_population_S_per_building, initial_population_I_per_building, initial_population_R_per_building,
        maximum_observation_error_rate, minimum_observation_error_rate, EGT_rate);

        market_maker_flat_price = new MarketMakerFlatPrice(total_buildings, total_days, liquidity_param, total_population_per_building, 
        accurate_observation_participant_rate_per_building, inaccurate_observation_participant_rate_per_building, 
        past_record_participant_rate_per_building, initial_money_resident, infection_rate, recovery_rate, time_scale, 
        initial_population_S_per_building, initial_population_I_per_building, initial_population_R_per_building,
        maximum_observation_error_rate, minimum_observation_error_rate, EGT_rate, price_per_share_flat_price_market);
        
        for (int i = 0; i < total_days; i++) {
            //initialize money tracers of each user
            market_maker.initialize_money_tracer();
            market_maker_flat_price.initialize_money_tracer();
                        
            //simulate disease spread
            for (int j = 0; j < total_buildings; j++) {
                market_maker.buildings[j].update_SIR();
                market_maker_flat_price.buildings[j].update_SIR();
            } 

            //observe flu patients
            for (int j = 0; j < total_buildings; j++) {
                //each market participant whose residence is building 'j' observe flu patients (LMSR market)
                for (int k = 0; k < market_maker.buildings[j].participants.length; k++){
                    int market_participant = market_maker.buildings[j].participants[k]; //it is the index of market participant
                    //observe the flu patients of each building
                    for (int l = 0; l < total_buildings; l++){
                        float observed_flu_rate = 0;
                        switch(market_maker.buildings[j].residents[market_participant].market_participant_type){
                            case ACCURATE:
                                observed_flu_rate = get_gaussian(market_maker.buildings[l].get_flu_population_rate(), 
                                                                 accurate_observation_gaussian_std_dev, 0, 100);                                                     
                                break;
                            case INACCURATE:
                                observed_flu_rate = get_gaussian(market_maker.buildings[l].get_flu_population_rate(), 
                                                                 inaccurate_observation_gaussian_std_dev, 0, 100);                                                     
                                break;
                            case PAST_RECORD:
                                //if the day is first day, skip observation
                                if(i == 0) continue;
              
                                float yesterday_flu_rate;
                                int security_id = market_maker.get_security_group_id(i-1, l);
                                yesterday_flu_rate = market_maker.ground_truths[security_id];                                
                                observed_flu_rate = get_gaussian(yesterday_flu_rate, past_record_gaussian_std_dev, 0, 100);                                                     
                                break;                           
                                
                        }
                        market_maker.buildings[j].residents[market_participant].observations[l].observed_flu_rate = observed_flu_rate;
                    }
                }
                //each market participant whose residence is building 'j' observe flu patients (flat price market)
                for (int k = 0; k < market_maker_flat_price.buildings[j].participants.length; k++){
                    int market_participant = market_maker_flat_price.buildings[j].participants[k]; //it is the index of market participant
                    //observe the flu patients of each building
                    for (int l = 0; l < total_buildings; l++){
                        float observed_flu_rate = 0;
                        switch(market_maker_flat_price.buildings[j].residents[market_participant].market_participant_type){
                            case ACCURATE:
                                observed_flu_rate = get_gaussian(market_maker_flat_price.buildings[l].get_flu_population_rate(), 
                                                                 accurate_observation_gaussian_std_dev, 0, 100);                                                     
                                break;
                            case INACCURATE:
                                observed_flu_rate = get_gaussian(market_maker_flat_price.buildings[l].get_flu_population_rate(), 
                                                                 inaccurate_observation_gaussian_std_dev, 0, 100);                                                     
                                break;
                            case PAST_RECORD:
                                //if the day is first day, skip observation
                                if(i == 0) continue;
              
                                float yesterday_flu_rate;
                                int security_id = market_maker_flat_price.get_security_group_id(i-1, l);
                                yesterday_flu_rate = market_maker_flat_price.ground_truths[security_id];                                
                                observed_flu_rate = get_gaussian(yesterday_flu_rate, past_record_gaussian_std_dev, 0, 100);                                                     
                                break;                           
                                
                        }
                        market_maker_flat_price.buildings[j].residents[market_participant].observations[l].observed_flu_rate = observed_flu_rate;
                    }
                }
            }
/*            
            //print observation list of all market participant
            for (int j = 0; j < total_buildings; j++){
                for (int k = 0; k < market_maker.buildings[j].participants.length; k++){
                    int market_participant = market_maker.buildings[j].participants[k];
                    market_maker.buildings[j].residents[market_participant].print_observation();
                }
            }
*/

            //buy shares
            for(int j=0; j<total_buildings; j++){
                //in LMSR market
                for(int k=0; k<market_maker.buildings[j].participants.length; k++){
                    //this loop iterates through market participants whose residence is building 'j'
                    int market_participant = market_maker.buildings[j].participants[k];
                    
                    //if the day is first day and the type of market participant is 'PAST_RECORD', they don't buy anything
                    if(market_maker.buildings[j].residents[market_participant].market_participant_type == market_participant_type.PAST_RECORD &&
                       i == 0)
                        continue;
                    
                    for(int l=0; l<total_buildings; l++){
                        //bet on building 'l' of date 'date'
                        float observed_flu_rate = market_maker.buildings[j].residents[market_participant].observations[l].observed_flu_rate;
//                        float flu_rate = get_gaussian(observed_flu_rate, security_flu_rate_gaussian_std_dev, 0, 100);
                        float flu_rate = get_gaussian(observed_flu_rate, security_flu_rate_gaussian_std_dev, 0, 100);
                        int quantity;
                        int date = (int) Math.floor(get_gaussian(i, date_gaussian_std_dev, i, total_days));
                        int security_group_id = market_maker.get_security_group_id(date, l);
                        
                        
                        
                        quantity = (int) get_gaussian(quantity_gaussian_mean, quantity_gaussian_std_dev);
                        market_maker.record_suggested_flu_population_rate(j, market_participant, l, flu_rate);
                        market_maker.record_actual_flu_population_rate(l, flu_rate);
                        market_maker.buy_process(security_group_id, quantity, flu_rate, j, market_participant);
                    }
                }
                //in flat price market
                for(int k=0; k<market_maker_flat_price.buildings[j].participants.length; k++){
                    //this loop iterates through market participants whose residence is building 'j'
                    int market_participant = market_maker_flat_price.buildings[j].participants[k];
                    
                    //if the day is first day and the type of market participant is 'PAST_RECORD', they don't buy anything
                    if(market_maker_flat_price.buildings[j].residents[market_participant].market_participant_type == market_participant_type.PAST_RECORD &&
                       i == 0)
                        continue;
                    
                    for(int l=0; l<total_buildings; l++){
                        //bet on building 'l' of date 'date'
                        float observed_flu_rate = market_maker_flat_price.buildings[j].residents[market_participant].observations[l].observed_flu_rate;
//                        float flu_rate = get_gaussian(observed_flu_rate, security_flu_rate_gaussian_std_dev, 0, 100);
                        float flu_rate = get_gaussian(observed_flu_rate, security_flu_rate_gaussian_std_dev, 0, 100);
                        int quantity;
                        int date = (int) Math.floor(get_gaussian(i, date_gaussian_std_dev, i, total_days));
                        int security_group_id = market_maker_flat_price.get_security_group_id(date, l);
                        
                        
                        
                        quantity = (int) get_gaussian(quantity_gaussian_mean, quantity_gaussian_std_dev);
                        market_maker_flat_price.record_suggested_flu_population_rate(j, market_participant, l, flu_rate);
                        market_maker_flat_price.record_actual_flu_population_rate(l, flu_rate);
                        market_maker_flat_price.buy_process(security_group_id, quantity, flu_rate, j, market_participant);                      
                    }
                }                
            }

/*            
            //selling shares
            for(int j=0; j<total_buildings; j++){
                for(int k=0; k<market_maker.buildings[j].participants.length; k++){
                    //this loop iterates through market participants whose residence is building 'j'
                    int market_participant = market_maker.buildings[j].participants[k];
                    float price = get_gaussian(1, sell_price_gaussian_std_dev, 0, 1);
                    //pick the share whose security's price is the cloeset to 'price'
                    if(market_maker.has_share(j, market_participant)){
                        Share share = market_maker.pick_share(i, j, market_participant, price);
                        if(share != null){
                            int quantity = get_rand_int(0, share.quantity);
                            if(quantity != 0){
                                market_maker.sell_process(share.security_group_id, quantity, share.flu_population_rate,
                                    share.buyer_residence, share.buyer_resident_id);                                            
                            }
                        }
                    }
                }
            }
*/            
            
            //payoff in LMSR market
            for(int j=0; j<total_buildings; j++){
                float payoff;
                int security_group_id = market_maker.get_idx_of_first_elem_of_block(i) + j;
                float estimated_ground_truth = market_maker.security_groups[security_group_id].get_mean();                    
                float ground_truth = market_maker.buildings[j].get_flu_population_rate();                    
                Iterator<Share> itr = market_maker.security_groups[security_group_id].share_list.iterator();

                while(itr.hasNext()){
                    Share share = itr.next();
                    if(market_maker.determine_payoff_with_EGT()){
                        payoff = share.quantity * market_maker.payoff_per_share(estimated_ground_truth, share.flu_population_rate);
                    }
                    else{
                        payoff = share.quantity * market_maker.payoff_per_share(ground_truth, share.flu_population_rate);
                    }
                    market_maker.buildings[share.buyer_residence].residents[share.buyer_resident_id].give_payoff(payoff);
                }
                
                market_maker.estimated_ground_truths[security_group_id] = estimated_ground_truth;
                market_maker.ground_truths[security_group_id] = ground_truth;            
            }
            //payoff in flat price market
            for(int j=0; j<total_buildings; j++){
                float payoff;
                int security_group_id = market_maker_flat_price.get_idx_of_first_elem_of_block(i) + j;
                float estimated_ground_truth = market_maker_flat_price.security_groups[security_group_id].get_mean();                    
                float ground_truth = market_maker_flat_price.buildings[j].get_flu_population_rate();                    
                Iterator<Share> itr = market_maker_flat_price.security_groups[security_group_id].share_list.iterator();

                while(itr.hasNext()){
                    Share share = itr.next();
                    if(market_maker_flat_price.determine_payoff_with_EGT()){
                        payoff = share.quantity * market_maker_flat_price.payoff_per_share(estimated_ground_truth, share.flu_population_rate);
                    }
                    else{
                        payoff = share.quantity * market_maker_flat_price.payoff_per_share(ground_truth, share.flu_population_rate);
                    }
                    market_maker_flat_price.buildings[share.buyer_residence].residents[share.buyer_resident_id].give_payoff(payoff);
                }
                
                market_maker_flat_price.estimated_ground_truths[security_group_id] = estimated_ground_truth;
                market_maker_flat_price.ground_truths[security_group_id] = ground_truth;            
            } 
            
            market_maker.sort_total_participants_money_decreasing_order();
            market_maker.write_csv_user_performance(i);
         
            market_maker_flat_price.sort_total_participants_money_decreasing_order();
            market_maker_flat_price.write_csv_user_performance(i);
            
            //record the population information of each building (LMSR market)
            for(int j=0; j<total_buildings; j++){
                int security_group_id = market_maker.get_idx_of_first_elem_of_block(i) + j;
                float estimated_ground_truth = market_maker.estimated_ground_truths[security_group_id];                   
                market_maker.buildings[j].record_population(i, estimated_ground_truth);
            }
            
            //record the population information of each building (flat price market)
            for(int j=0; j<total_buildings; j++){
                int security_group_id = market_maker_flat_price.get_idx_of_first_elem_of_block(i) + j;
                float estimated_ground_truth = market_maker_flat_price.estimated_ground_truths[security_group_id];                   
                market_maker_flat_price.buildings[j].record_population(i, estimated_ground_truth);
            }
        }  
        
        market_maker.get_MSE();
        market_maker_flat_price.get_MSE();
        market_maker.write_csv_population_rates();
        market_maker_flat_price.write_csv_population_rates();
        market_maker.write_csv_EGT_GT();
        market_maker_flat_price.write_csv_EGT_GT();
        market_maker.show_GT_EGT();        
        market_maker_flat_price.show_GT_EGT();      
    }

    //checks whether the sum of population in each state is equal to the total population
    public static void validate_population(int total_population, int population_S, int population_I, int population_R) {
        if (total_population != (population_S + population_I + population_R)) {
            System.out.println("Please enter the valid population of state S, I, and R");
            System.exit(0);
        }
    }
    
    //get gaussian random number
    public static float get_gaussian(float mean, float std_dev){
        Random rand = new Random();
        float gaussian;
        gaussian = (float)rand.nextGaussian();
        gaussian = (gaussian * std_dev) + mean;
        
        return gaussian;
    }
/*
    public static float truncated_normal(float mean, float std_dev, float lower_bound, float upper_bound){
        TruncatedNormalDistribution a;
    }
 */   
    //get gaussian random number bounded by ['lower_bound', 'upper_bound']
    public static float get_gaussian(float mean, float std_dev, float lower_bound, float upper_bound){
        Random rand = new Random();
        float gaussian;
        do{
            gaussian = (float)rand.nextGaussian();
            gaussian = (gaussian * std_dev) + mean;
        }while(lower_bound > gaussian || gaussian > upper_bound);
        
        return gaussian;
    }
    
    public static int get_rand_int(int min, int max){
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
    

}
