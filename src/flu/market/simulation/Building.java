/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flu.market.simulation;

import flu.market.simulation.FluMarketSimulation.health_state;
import flu.market.simulation.FluMarketSimulation.market_participant_type;
import java.util.*;
import javafx.scene.chart.XYChart;
import org.jfree.data.xy.XYSeries;

/**
 *
 * @author yisak
 */
public class Building {
    int id;
    int total_population;
    float initial_money_resident;
//    float market_participant_rate;
    float accurate_observation_participant_rate;
    float inaccurate_observation_participant_rate;
    float past_record_participant_rate;
    int number_accurate_participants;
    int number_inaccurate_participants;
    int number_past_record_participants;
    Person[] residents;
    FluSpread flu_spread;
    int total_buildings;
    int total_days;
    float maximum_observation_error_rate;
    float minimum_observation_error_rate;
    int number_market_participants;
    int[] participants; //the element of this array is the index of element in 'residents', pointing market participant
    int total_shares_sold;
    
    //In residents array, from Person[0] to Person[end_of_S] are in state S
    //from residents[end_of_S+1] to Person[end_of_I] are in state I
    //from residents[end_of_I+1] to Person[total_population-1] are in state R
    //if the population of S state is 0, end_of_S will be -1
    int end_of_S;
    int end_of_I;
    
    float[][] contact_matrix;
    Population[] populations;
    PopulationRate[] population_rates; //it records the population rates of S,I,R of everyday
    
    public Building(int _id, int _total_population, float _accurate_observation_participant_rate, 
            float _inaccurate_observation_participant_rate, float _past_record_participant_rate,
            float _initial_money_resident, float _infection_rate, float _recovery_rate,
            float _time_scale, int _population_S, int _population_I, int _population_R, int _total_buildings,
            float _maximum_observation_error_rate, float _minimum_observation_error_rate, int _total_days, 
            float[][] _contact_matrix, Population[] _populations){
        id = _id;
        total_population = _total_population;
//        market_participant_rate = _market_participant_rate;
        accurate_observation_participant_rate = _accurate_observation_participant_rate;
        inaccurate_observation_participant_rate = _inaccurate_observation_participant_rate;
        past_record_participant_rate = _past_record_participant_rate;
        initial_money_resident = _initial_money_resident;
        contact_matrix = _contact_matrix;
        populations = _populations;
        total_buildings = _total_buildings;
        flu_spread = new FluSpread(_id, _infection_rate, _recovery_rate, _time_scale, _population_S, _population_I, _population_R, total_population,
                total_buildings, contact_matrix, populations);
        total_days = _total_days;
        maximum_observation_error_rate = _maximum_observation_error_rate;
        minimum_observation_error_rate = _minimum_observation_error_rate;
//        number_market_participants = (int)(total_population*market_participant_rate);
        number_accurate_participants = (int)(total_population*accurate_observation_participant_rate);
        number_inaccurate_participants = (int)(total_population*inaccurate_observation_participant_rate);
        number_past_record_participants = (int)(total_population*past_record_participant_rate);
        number_market_participants = number_accurate_participants +
                                     number_inaccurate_participants +
                                     number_past_record_participants;
        initialize_residents();
        population_rates = new PopulationRate[total_days];
        total_shares_sold = 0;
    }

    
    private void initialize_residents(){
        residents = new Person[total_population];
        for(int i=0; i < total_population; i++){
            float observation_accuracy = get_random_within_range(minimum_observation_error_rate, maximum_observation_error_rate);
            residents[i] = new Person(i, id, false, health_state.S, market_participant_type.N, initial_money_resident, 
                    observation_accuracy, total_buildings, total_days);
        }
        set_market_participants();
        set_end_index();
        spread_disease();
    }
    
    private float get_random_within_range(float min, float max){
        return (float) ((Math.random() * ((max - min) + 1)) + min);
    }
    
    //set 'end_of_S' and 'end_of_I'
    private void set_end_index(){
        end_of_S = flu_spread.population_S - 1;
        end_of_I = end_of_S + flu_spread.population_I;
    }
    
    //set market participant according to participant rate
    private void set_market_participants(){
        //get random number without duplication
        int count = 0;
        
        participants = new int[number_market_participants];
        
        Random ran = new Random();
        
        //assign accurate observation participants
        for(int i=0; i<number_accurate_participants; i++){
            int nxt = ran.nextInt(total_population-1);
            while(residents[nxt].market_participant_type != market_participant_type.N){
                nxt = ran.nextInt(total_population-1);
            }
            residents[nxt].market_participant_type = market_participant_type.ACCURATE;
            participants[count] = nxt;
            count++;
        }
        
        //assign accurate observation participants
        for(int i=0; i<number_inaccurate_participants; i++){
            int nxt = ran.nextInt(total_population-1);
            while(residents[nxt].market_participant_type != market_participant_type.N){
                nxt = ran.nextInt(total_population-1);
            }
            residents[nxt].market_participant_type = market_participant_type.INACCURATE;
            participants[count] = nxt;
            count++;
        }
   
        //assign accurate observation participants
        for(int i=0; i<number_past_record_participants; i++){
            int nxt = ran.nextInt(total_population-1);
            while(residents[nxt].market_participant_type != market_participant_type.N){
                nxt = ran.nextInt(total_population-1);
            }
            residents[nxt].market_participant_type = market_participant_type.PAST_RECORD;
            participants[count] = nxt;
            count++;
        }
        /*
        while(count < number_market_participants){
            //get random number [0, total_population-1]
            int nxt = ran.nextInt(total_population-1);
            //set nxt to indicate the person who is not market_participant
            while(residents[nxt].market_participant != false)
                nxt = ran.nextInt(total_population-1);
            residents[nxt].market_participant = true;
            //add index which indicates market participant to the 'participants'
            participants[count] = nxt;
            count++;
        }
 */
    }
    
    //change the state of people following the parameters
    private void spread_disease(){
        for( int i=0; i<end_of_S; i++){
            residents[i].health_state = health_state.S;
        }
        for( int i=end_of_S+1; i<end_of_I; i++){
            residents[i].health_state = health_state.I;
        }
        for( int i=end_of_I+1; i<total_population; i++){
            residents[i].health_state = health_state.R;
        }
    }
    
    //update SIR population and update health state of people
    public void update_SIR(){
        flu_spread.update_population();
        set_end_index();
        spread_disease();
    }

    public int get_population_S(){
        return flu_spread.population_S;
    }
    
    public int get_population_I(){
        return flu_spread.population_I;
    }

    public int get_population_R(){
        return flu_spread.population_R;
    }    
    
    public float get_flu_population_rate(){
        //those are integers
        return flu_spread.population_I / (float) total_population * 100;
    }
    
    void record_population(int day, float estimated_flu_population_rate){
        population_rates[day] = new PopulationRate(day, flu_spread.get_S_rate(),
                flu_spread.get_I_rate(), flu_spread.get_R_rate(), estimated_flu_population_rate);
    }
/*
    XYChart.Series get_S_series(){
        XYChart.Series series = new XYChart.Series(); 
        series.setName("population rate of 'S' group"); 
        for(int i=0; i<total_days; i++){
            if(population_rates[i] == null){
                System.out.println("Population rates are not recorded");
                System.exit(0);
            }
            series.getData().add(new XYChart.Data(population_rates[i].S_population_rate, population_rates[i].day));    
        }
        return series;
    }

    XYChart.Series get_I_series(){
        XYChart.Series series = new XYChart.Series(); 
        series.setName("population rate of 'I' group"); 
        for(int i=0; i<total_days; i++){
            if(population_rates[i] == null){
                System.out.println("Population rates are not recorded");
                System.exit(0);
            }
            series.getData().add(new XYChart.Data(population_rates[i].I_population_rate, population_rates[i].day));    
        }
        return series;
    }
    
    XYChart.Series get_R_series(){
        XYChart.Series series = new XYChart.Series(); 
        series.setName("population rate of 'R' group"); 
        for(int i=0; i<total_days; i++){
            if(population_rates[i] == null){
                System.out.println("Population rates are not recorded");
                System.exit(0);
            }
            series.getData().add(new XYChart.Data(population_rates[i].R_population_rate, population_rates[i].day));    
        }
        return series;
    }

    XYChart.Series get_estimated_flu_rate_series(){
        XYChart.Series series = new XYChart.Series(); 
        series.setName("estimated flu population rate"); 
        for(int i=0; i<total_days; i++){
            if(population_rates[i] == null){
                System.out.println("Population rates are not recorded");
                System.exit(0);
            }
            series.getData().add(new XYChart.Data(population_rates[i].estimated_flu_population_rate, population_rates[i].day));    
        }
        return series;
    }    
*/
}
