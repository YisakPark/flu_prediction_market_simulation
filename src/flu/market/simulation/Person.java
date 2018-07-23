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
    ArrayList<Integer> observation_list = new ArrayList<Integer>();
    ArrayList<Share> share_list = new ArrayList<Share>();
    
    public Person(int _id, int _residence, boolean _market_participant, FluMarketSimulation.health_state _health_state, float _money){
        id = _id;
        residence = _residence;
        market_participant = _market_participant;
        health_state = _health_state;
        money = _money;
    }
}
