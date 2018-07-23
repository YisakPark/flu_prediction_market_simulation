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
        int number_days = 100;
        float liquidity_param = (float)32.0;
        MarketMaker market_maker = new MarketMaker(number_buildings, number_days, liquidity_param);
        
        int[] security_ids = {1,2};
        System.out.println(market_maker.get_cost(0, security_ids, 10));
/*
        //initialize building objects
        for (int i = 0; i < number_buildings; i++) {
            buildings[i] = new Building(i, total_population_per_building, market_participant_rate_per_building, initial_money_of_people,
                    infection_rate, recovery_rate, time_scale, initial_population_S_per_building,
                    initial_population_I_per_building, initial_population_R_per_building);
        }

        for (int i = 0; i < number_days; i++) {
            //simulate disease spread
            for (int j = 0; j < number_buildings; j++) {
                buildings[j].update_SIR();
                System.out.println("Day: " + (i + 1) + ", S: " + buildings[j].get_population_S() + ", I: " + buildings[j].get_population_I() + ", R: "
                        + buildings[j].get_population_R());
            }          
            
        }
        */
        
    }

    //checks whether the sum of population in each state is equal to the total population
    public static void validate_population(int total_population, int population_S, int population_I, int population_R) {
        if (total_population != (population_S + population_I + population_R)) {
            System.out.println("Please enter the valid population of state S, I, and R");
            System.exit(0);
        }
    }

}
