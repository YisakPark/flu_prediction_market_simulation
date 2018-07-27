/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flu.market.simulation;

import java.util.*;
import org.jfree.data.xy.XYSeries;
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
        float initial_money_resident = 100;
        float market_participant_rate_per_building = (float) 0.3;
        validate_population(total_population_per_building, initial_population_S_per_building,
                initial_population_I_per_building, initial_population_R_per_building);
        float infection_rate = (float) 0.1;
        float recovery_rate = (float) 2.14;
        float time_scale = (float) 0.14;
        int total_buildings = 10;
        int total_days = 20;
        float liquidity_param = (float)32.0;
        MarketMaker market_maker;
      
        float observation_gaussian_std_dev = (float) 0.2;     
        float security_flu_rate_gaussian_std_dev = (float) 0.2;
        float quantity_gaussian_mean = (float) 10;
        float quantity_gaussian_std_dev = (float) 2.4;

        market_maker = new MarketMaker(total_buildings, total_days, liquidity_param, total_population_per_building,
        market_participant_rate_per_building, initial_money_resident, infection_rate, recovery_rate, time_scale, 
        initial_population_S_per_building, initial_population_I_per_building, initial_population_R_per_building);
        
        
        for (int i = 0; i < total_days; i++) {
            //simulate disease spread
            for (int j = 0; j < total_buildings; j++) {
                market_maker.buildings[j].update_SIR();
                System.out.println("Day: " + (i + 1) + ". Building: " +  j + ", S: " + market_maker.buildings[j].get_population_S() + ", I: " 
                        + market_maker.buildings[j].get_population_I() + ", R: "
                        + market_maker.buildings[j].get_population_R());
            } 

//XYSeries series = new XYSeries("observation");
            //observe flu patients
            for (int j = 0; j < total_buildings; j++) {
                //each market participant whose residence is building 'j' observe flu patients
                for (int k = 0; k < market_maker.buildings[j].participants.length; k++){
                    int market_participant = market_maker.buildings[j].participants[k]; //it is the index of market participant
                    //observe the flu population rate of each building
                    for (int l = 0; l < total_buildings; l++){
                        float observed_flu_rate = get_gaussian(market_maker.buildings[l].get_flu_population_rate(), observation_gaussian_std_dev, 0, 1);
                        market_maker.buildings[j].residents[market_participant].observations[l].observed_flu_rate = observed_flu_rate;
//series.add(observed_flu_rate, 0);
                    }
                }
            }  
//ScatterPlotter scatter = new ScatterPlotter("x","y",series,0.1);
//scatter.show_scatter();
  
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
                for(int k=0; k<market_maker.buildings[j].participants.length; k++){
                    //this loop iterates through market participants whose residence is building 'j'
                    int market_participant = market_maker.buildings[j].participants[k];
                    for(int l=0; l<total_buildings; l++){
                        //bet on building 'l' of date 'i'
                        float observed_flu_rate = market_maker.buildings[j].residents[market_participant].observations[l].observed_flu_rate;
                        float flu_rate = get_gaussian(observed_flu_rate, security_flu_rate_gaussian_std_dev, 0, 1);
                        int quantity;
                        float cost;
                        int date = i;
                        int security_group_id = market_maker.get_security_group_id(date, l);
                        
                        quantity = (int) get_gaussian(quantity_gaussian_mean, quantity_gaussian_std_dev);
                        market_maker.buy_process(security_group_id, quantity, flu_rate, j, market_participant);                      
                    }
                }
            }
/*            
//XYSeries series = new XYSeries("training sample");
            //generate estimated ground truth and training sample
            for(int j=0; j<total_buildings; j++){
                int security_group_id = market_maker.get_idx_of_first_elem_of_block(i) + j;
                float mean = market_maker.security_groups[security_group_id].get_mean();
                float std_var = market_maker.security_groups[security_group_id].get_std_var(mean);
                float actual_flu_rate = market_maker.buildings[j].get_flu_population_rate();
                market_maker.generate_EGT(security_group_id, mean, std_var);
                market_maker.training_samples[security_group_id] = new TrainingSample(mean, std_var, actual_flu_rate);
                makret_maker.ground_truths[security_group_id] = actual_flu_rate;
//series.add(mean, actual_flu_rate);
            }
//ScatterPlotter scatter = new ScatterPlotter("mean","actual_flu_rate",series,1);
//scatter.show_scatter(); 
            */
            
            /*
                market_maker.train_model();
                market_maker.compare_EGT_GT();
            */
        }   
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
}
