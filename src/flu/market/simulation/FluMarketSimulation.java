/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flu.market.simulation;

import java.util.*;

/**
 *
 * @author yisak
 */
public class FluMarketSimulation {

    public static enum health_state {
        S, I, R
    }

    public static void main(String[] args) {
        int total_population_per_building = 100;
        int initial_population_S_per_building = 99;
        int initial_population_I_per_building = 1;
        int initial_population_R_per_building = 0;
        float initial_money_of_people = 100;
        float market_participant_rate_per_building = (float) 0.3;
        validate_population(total_population_per_building, initial_population_S_per_building,
                initial_population_I_per_building, initial_population_R_per_building);
        float infection_rate = (float) 0.1;
        float recovery_rate = (float) 2.14;
        float time_scale = (float) 0.14;
        int number_buildings = 10;
        Building[] buildings;
        buildings = new Building[number_buildings];
        int number_days = 20;
        float liquidity_param = (float)32.0;
        MarketMaker market_maker = new MarketMaker(number_buildings, number_days, liquidity_param);
        
        float observation_gaussian_mean = (float)0.5;
        float observation_gaussian_dev = (float) 0.2;
        


        //initialize building objects
        for (int i = 0; i < number_buildings; i++) {
            buildings[i] = new Building(i, total_population_per_building, market_participant_rate_per_building, initial_money_of_people,
                    infection_rate, recovery_rate, time_scale, initial_population_S_per_building,
                    initial_population_I_per_building, initial_population_R_per_building, number_buildings);
        }
        
        /* buying shares 
        int[] security_group_ids = {1,2};
        int quantity = 10;
        float flu_population_rate = (float) 0.4;
        buildings[0].residents[0].money -= market_maker.get_cost(0, security_group_ids, quantity);
        for(int i=0; i<security_group_ids.length; i++){
            Share share = new Share(security_group_ids[i], flu_population_rate, quantity);
            buildings[0].residents[0].add_share(share);
        }        
        market_maker.buy_process(security_group_ids, quantity, flu_population_rate);
        */
        
        /* selling shares 
        int[] security_group_ids = {1,2};
        int quantity = 10;
        float flu_population_rate = (float) 0.4;
        buildings[0].residents[0].money += market_maker.get_cost(0, security_group_ids, -quantity);
        for(int i=0; i<security_group_ids.length; i++){
            Share share = new Share(security_group_ids[i], flu_population_rate, quantity);
            buildings[0].residents[0].remove_share(share);
        }        
        market_maker.sell_process(security_group_ids, quantity, flu_population_rate);
        */

        for (int i = 0; i < number_days; i++) {
            //simulate disease spread
            for (int j = 0; j < number_buildings; j++) {
                buildings[j].update_SIR();
                System.out.println("Day: " + (i + 1) + ", S: " + buildings[j].get_population_S() + ", I: " + buildings[j].get_population_I() + ", R: "
                        + buildings[j].get_population_R());
            } 
            
            //observe flu patients
            for (int j = 0; j < number_buildings; j++) {
                //each market participant observe flu patients
                for (int k = 0; k < buildings[j].participants.length; k++){
                    int market_participant = buildings[j].participants[k]; //it is the index of market participant
                    //observe the flu population rate of each building
                    for (int l = 0; l < number_buildings; l++){
                        float observed_flu_rate = get_gaussian(observation_gaussian_mean, observation_gaussian_dev, 0, 1);
                        buildings[j].residents[market_participant].observations[l].observed_flu_rate = observed_flu_rate;
                    }
                }
            }  
/*
            //buy shares
            for(int j=0; j<number_buildings; j++){
                for(int k=0; k <buildings[j].participants.length; k++){
                    int market_participant = buildings[j].participants[k];
                    for(int l=0; l<number_buildings; l++){
                        //bet on building 'l' of date 'i'
                        float observed_flu_rate = buildings[j].residents[market_participant].observation[l].observed_flu_rate;
                        float flu_rate = get_gaussian(observed_flu_rate, security_flu_rate_gaussian_dev, 0, 1);
                        int quantity;
                        float cost;
            
                        int date = i;
                        int security_group_id = market_maker.get_security_group_id(date, l);
                        //get the quantity which is affordable by participant
                        do{
                            quantity = get_gaussian(quantity_gaussian_mean, quantity_gaussian_dev, 0, 1);
                            cost = market_maker.get_cost(date, security_group_id, quantity);
                        }while(cost > buildings[j].residents[market_participant].money);
                        //rest is buying shares with adjusted quantity and cost
                    }
                }
            }
*/
                        
            
            
/*  
            //print observation list of all market participant
            for (int j = 0; j < number_buildings; j++){
                for (int k = 0; k < buildings[j].participants.length; k++){
                    int market_participant = buildings[j].participants[k];
                    buildings[j].residents[market_participant].print_observation();
                }
            }
*/
    


            //buy shares
            

            //buy shares
//            for (int j = 0; j < number_buildings; j++) {            
//                if resident k of building[j] want to bet,
//                building[j].residents[k].money -= MarketMaker.get_cost(~);
//                Share share = new Share(~);
//                building[j].residents[k].share_list.add(share);
//                MarketMaker.buy_process(~);
//            }    
            
        }       
    }

    //checks whether the sum of population in each state is equal to the total population
    public static void validate_population(int total_population, int population_S, int population_I, int population_R) {
        if (total_population != (population_S + population_I + population_R)) {
            System.out.println("Please enter the valid population of state S, I, and R");
            System.exit(0);
        }
    }

    //get gaussian random number bounded by ['lower_bound', 'upper_bound']
    public static float get_gaussian(float mean, float dev, float lower_bound, float upper_bound){
        Random rand = new Random();
        float gaussian;
        do{
            gaussian = (float)rand.nextGaussian();
            gaussian = (gaussian * dev) + mean;
        }while(lower_bound > gaussian || gaussian > upper_bound);
        
        return gaussian;
    }
}
