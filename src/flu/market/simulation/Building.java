/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flu.market.simulation;

import flu.market.simulation.FluMarketSimulation.health_state;
import java.util.*;
/**
 *
 * @author yisak
 */
public class Building {
    int id;
    int total_population;
    float initial_money_resident;
    float market_participant_rate;
    Person[] residents;
    FluSpread flu_spread;
    
    //In residents array, from Person[0] to Person[end_of_S] are in state S
    //from residents[end_of_S+1] to Person[end_of_I] are in state I
    //from residents[end_of_I+1] to Person[total_population-1] are in state R
    //if the population of S state is 0, end_of_S will be -1
    int end_of_S;
    int end_of_I;
    
    public Building(int _id, int _total_population, float _market_participant_rate, float _initial_money_resident,
            float _infection_rate, float _recovery_rate, float _time_scale, int _population_S, int _population_I, int _population_R){
        id = _id;
        total_population = _total_population;
        market_participant_rate = _market_participant_rate;
        initial_money_resident = _initial_money_resident;
        flu_spread = new FluSpread(_infection_rate, _recovery_rate, _time_scale, _population_S, _population_I, _population_R, total_population);
        
        initialize_residents();
    }
    
    private void initialize_residents(){
        residents = new Person[total_population];
        for(int i=0; i < total_population; i++){
            residents[i] = new Person(i, id, false, health_state.S, initial_money_resident);
        }
        set_market_participants();
        set_end_index();
        spread_disease();
    }
    
    //set 'end_of_S' and 'end_of_I'
    private void set_end_index(){
        end_of_S = flu_spread.population_S - 1;
        end_of_I = end_of_S + flu_spread.population_I;
    }
    
    //set market participant according to participant rate
    private void set_market_participants(){
        //get random number without duplication
        int number_market_participants = (int)(total_population*market_participant_rate);
        int count_market_participants = 0;
        Random ran = new Random();
        
        while(count_market_participants <= number_market_participants){
            //get random number [0, total_population-1]
            int nxt = ran.nextInt(total_population-1);
            //set nxt to indicate the person who is not market_participant
            while(residents[nxt].market_participant != false)
                nxt = ran.nextInt(total_population-1);
            residents[nxt].market_participant = true;
            count_market_participants++;
        }
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
}