/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flu.market.simulation;

import java.util.*;

/**
 *
 * @author YisakPark
 */
public class SecurityGroup {
    int date_market;
    int building_num;
    int shares;
    float price;
    ArrayList<Share> share_list = new ArrayList<>();
    
    public SecurityGroup(int _date_market, int _building_num, int _shares, int _price){
        date_market = _date_market;
        building_num = _building_num;
        shares = _shares;
        price = _price;
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
                share_list.get(i).quantity -= _share.quantity;
                if(share_list.get(i).quantity == 0){
                    share_list.remove(i);
                }
                return;
            }
        }
    }
}
