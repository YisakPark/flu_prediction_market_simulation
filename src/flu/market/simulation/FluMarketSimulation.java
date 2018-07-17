/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flu.market.simulation;

/**
 *
 * @author yisak
 */
public class FluMarketSimulation {

    public static enum health_state {
        S, I, R
    }
    
    public static void main(String[] args) {
        int total_population_per_location = 100;
        int initial_population_S_per_location = 99;
        int initial_population_I_per_location = 1;
        int initial_population_R_per_location = 0;
        float initial_money_of_people = 100;
        float market_participant_rate_per_location = (float) 0.3;
        validate_population(total_population_per_location, initial_population_S_per_location, 
                initial_population_I_per_location, initial_population_R_per_location);
        float infection_rate = (float) 0.1;
        float recovery_rate = (float) 2.14;
        float time_scale = (float) 0.14;
        int number_locations = 1;
        Location[] locations;
        locations = new Location[number_locations];
        int number_days = 100;
        
        //initialize location objects
        for( int i=0; i<number_locations; i++){
            locations[i] = new Location(i, total_population_per_location, market_participant_rate_per_location,initial_money_of_people,
                    infection_rate, recovery_rate, time_scale, initial_population_S_per_location,
                initial_population_I_per_location, initial_population_R_per_location);
        }

        //simulate disease spread
        for( int j=0; j<number_locations; j++){
            System.out.println("Initial population, S: " + locations[j].get_population_S() + ", I: " + locations[j].get_population_I() + ", R: " 
                    + locations[j].get_population_R());
        }
        
        for( int i=0; i<number_days; i++){
            for( int j=0; j<number_locations; j++){
                locations[j].update_SIR();
                System.out.println("Day: " + (i+1) + ", S: " + locations[j].get_population_S() + ", I: " + locations[j].get_population_I() + ", R: " 
                        + locations[j].get_population_R());
            }
        }
    }
    
    //checks whether the sum of population in each state is equal to the total population
    public static void validate_population(int total_population, int population_S, int population_I, int population_R){
        if(total_population != (population_S + population_I + population_R)){
            System.out.println("Please enter the valid population of state S, I, and R");
            System.exit(0);
        }
    }
    
}
