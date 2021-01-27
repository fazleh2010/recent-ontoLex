/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.parameters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;


/**
 *
 * @author elahi
 */
public interface ThresoldConstants {
    
    
    public static final String supA = "supA";
    public static final String supB = "supB";
    public static final String conAB = "conAB";
    public static final String conBA = "conBA";
    public static final String NUMBER_OF_RULES = "numberOfRules";
    
    public static final String AllConf = "AllConf";
    public static final String MaxConf = "MaxConf";
    public static final String IR = "IR";
    public static final String Kulczynski = "Kulczynski";
    public static final String Cosine = "Cosine";
    public static final String Coherence = "Coherence";
    
   
    public static final String linguisticRule = "linguisticRule";
    public static final String kbRule = "linguisticRule";

    public static final String predict_l_for_s_given_p = "predict_l_for_s_given_p";
    public static final String predict_l_for_s_given_o = "predict_l_for_s_given_o";
    public static final String predict_l_for_o_given_p = "predict_l_for_o_given_p";
    public static final String predict_l_for_s_given_po = "predict_l_for_s_given_po";

    public static final String predict_p_for_s_given_l = "predict_p_for_s_given_l";
    public static final String predict_o_for_s_given_l = "predict_o_for_s_given_l";
    public static final String predict_p_for_o_given_l = "predict_p_for_o_given_l";
    public static final String predict_po_for_s_given_l = "predict_po_for_s_given_l";

    public static final List<String> predicateRules = new ArrayList<String>(Arrays.asList(
            predict_l_for_s_given_p,
            predict_l_for_s_given_o,
            predict_l_for_o_given_p,
            predict_l_for_s_given_po,
            predict_p_for_s_given_l,
            predict_o_for_s_given_l,
            predict_p_for_o_given_l,
            predict_po_for_s_given_l));

    public static final LinkedHashSet<String> interestingness = new LinkedHashSet(new ArrayList<String>(Arrays.asList(Cosine,Coherence,AllConf,MaxConf, IR, Kulczynski)));

}
