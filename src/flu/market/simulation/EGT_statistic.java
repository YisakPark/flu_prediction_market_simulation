/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flu.market.simulation;

/**
 *
 * @author YisakPark
 */
public class EGT_statistic {
    float ground_truth;
    float estimated_ground_truth_mean;
    float margin_of_error;
    
    public EGT_statistic(float _ground_truth, float _estimated_ground_truth_mean,
            float _margin_of_error){
        ground_truth = _ground_truth;
        estimated_ground_truth_mean = _estimated_ground_truth_mean;
        margin_of_error = _margin_of_error;
    }
}
