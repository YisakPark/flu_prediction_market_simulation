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
public class Person {
    int id;
    int residence;
    boolean market_participant;
    FluMarketSimulation.health_state health_state;
    float money;
    float money_earned_selling;
    float observation_error_rate;
    Observation[] observations;
    float[] suggested_flu_population_rate;
    ArrayList<Share> share_list = new ArrayList<>();
    
    public Person(int _id, int _residence, boolean _market_participant, 
            FluMarketSimulation.health_state _health_state, float _money, 
            float _observation_error_rate, int _total_buildings, int _total_days){
        id = _id;
        residence = _residence;
        market_participant = _market_participant;
        health_state = _health_state;
        money = _money;
        money_earned_selling = (float) 0;
        observation_error_rate = _observation_error_rate;
        observations = new Observation[_total_buildings];
        suggested_flu_population_rate = new float[_total_buildings * _total_days];
        
        for(int i=0; i<_total_buildings; i++){
            observations[i] = new Observation(i, 0);
        }
    }
    
    //if there is a share in the 'share_list', which has same as arg 'share' except for quantity,
    //update quantity of the share in 'share_list'
    //otherwise, add the 'share' in the 'share_list'
    public void add_share(Share _share){
        int size = share_list.size();        
        
        for(int i=0; i<size; i++){
            Share share = share_list.get(i);
            if(share.security_group_id == _share.security_group_id && share.flu_population_rate == _share.flu_population_rate){
                share_list.get(i).quantity += _share.quantity;
                return;
            }
        }
        
        //if there is no corresponding share, just add new share
        share_list.add(_share);        
    }
    
    //update quantity of the '_share' in 'share_list'
    //if the quantity becomes 0, remove the '_share' from the 'share_list'
    public void remove_share(Share _share){
        int size = share_list.size();        
        
        for(int i=0; i<size; i++){
            Share share = share_list.get(i);
            if(share.security_group_id == _share.security_group_id && share.flu_population_rate == _share.flu_population_rate){
                share_list.get(i).quantity += _share.quantity;
                if(share_list.get(i).quantity == 0){
                    share_list.remove(i);
                }
                return;
            }
        }
    }
    
    //print observations
    public void print_observation(){
        System.out.println("building: " + residence + ", resident: " + id + ", observations will be printed as [building id : observed flu rate]");
        for (Observation observation : observations) {
            System.out.print("[" + observation.building_id + ":" + observation.observed_flu_rate + "] ");
        }
        System.out.println();
    }
    
    void give_payoff(float payoff){
        money += payoff;
    }
    
    int get_total_quantities_share(){
        int total = 0;
        Iterator<Share> itr = share_list.iterator();
        
        while(itr.hasNext()){
            Share share = itr.next();
            total += share.quantity;
        }
        return total;
    }
}
