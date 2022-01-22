import java.lang.Math;
import java.util.*;
import java.io.PrintWriter;
class Markov{

    public static  double getTransProb(int i,int j,int k){
        MarkovChain grid = new MarkovChain(k);
        return grid.getTransitionProbabilty(i, j, k);
    }

    public static double getSejProb(int s1,int s2,int numStates,double TS){
        MarkovChain grid = new MarkovChain(numStates);
        return grid.getSejProbabilty(s1, s2, numStates, TS);
    }

    public static double getBiasTransProb(int s1, int s2,double[] ssprob) {
        MarkovChain grid = new MarkovChain(9);
        return grid.getBiasedTransitionProbabilty(s1, s2, ssprob);
    }

    public static double  getContTransProb(int s1,int s2,double[] rates){
        MarkovChain grid = new MarkovChain(3, true);
        return grid.getContTransProb(s1, s2, rates);
    }

    public static double getContSejProb(int s1,int s2,double[] rates,double TSC){
        MarkovChain grid = new MarkovChain(3, true);
        return grid.getContSejProb(s1, s2, rates, TSC);
    }
}//end class