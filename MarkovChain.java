import java.sql.Array;
import java.util.ArrayList;
import java.util.Random;

public class MarkovChain {

    ArrayList<State> markovChain = new ArrayList<State>();
    int[] count;
    int counter = 1;
    int currentState = 1;
    boolean check = true;
    Random random = new Random();

    final int MAX_LOOP = 1000000;

    public MarkovChain(int numOfStates){
        count = new int[numOfStates]; // Initialising Count[] all elements to 0
        for(int i = 0; i < count.length; i++){
            count[i] = 0;
        }
        for(int i = 0; i < numOfStates; i++){ // Creates and Adds State objects to MarkovChain
            markovChain.add(new State(i+1));
        }
        for(int i = 0; i < numOfStates; i++){ // Checks to add neighbour west & east
            if(Math.sqrt(markovChain.size()) * counter != i+1 ){
                if(i+1 < markovChain.size()){
                    markovChain.get(i).addNeighbour(markovChain.get(i+1));
                }
            }
            else{
                counter++;
            }
        }
        for(int i = 0; i < numOfStates; i++){ // Checks to add neighbours north & south
            if(i+Math.sqrt(markovChain.size()) < markovChain.size()) {
                markovChain.get(i).addNeighbour(markovChain.get((int) (i + Math.sqrt(markovChain.size()))));
            }
            if(i == markovChain.size()-1){ // Add the last State's neighbour as itself
                markovChain.get(i).neighbour.add(markovChain.get(numOfStates-1));
            }
        }
//       for(int i = 0; i < markovChain.get(8).neighbour.size(); i++){ // Printing neighbours of States
//           System.out.println(markovChain.get(8).neighbour.get(i).number);
//       }
    }

    public MarkovChain(int numOfStates, boolean continuously){
        count = new int[numOfStates]; // Initialising Count[] all elements to 0
        for(int i = 0; i < count.length; i++){
            count[i] = 0;
        }
        for(int i = 0; i < numOfStates; i++){ // Creates and Adds State objects to MarkovChain
            markovChain.add(new State(i+1));
        }

        markovChain.get(0).addNeighbourNoSelf(markovChain.get(1));
        markovChain.get(0).addNeighbourNoSelf(markovChain.get(2));
        markovChain.get(1).addNeighbourNoSelf(markovChain.get(0));
        markovChain.get(1).addNeighbourNoSelf(markovChain.get(2));
        markovChain.get(2).addNeighbourNoSelf(markovChain.get(0));
        markovChain.get(2).addNeighbourNoSelf(markovChain.get(1));

//       for(int i = 0; i < markovChain.get(0).neighbour.size(); i++){ // Printing neighbours of States
//           System.out.println(markovChain.get(0).neighbour.get(i).number);
//       }
//       for(int i = 0; i < markovChain.size(); i++){
//           System.out.println(markovChain.get(i).number);
//       }
    }

    public double getTransitionProbabilty(int state1, int state2, int numOfStates){
        if(markovChain.get(state1-1).neighbour.contains(markovChain.get(state2-1)) || markovChain.get(state1-1) == markovChain.get(state2-1)){ // Checks if they are neighbours
            double steadyState1Probabilty = markovChain.get(state1-1).steadyStateProbabilty/numOfStates; // Calculates Steady State Probabilty
            double steadyState2Probabilty = markovChain.get(state2-1).steadyStateProbabilty/numOfStates;

            double acceptanceProbability = steadyState2Probabilty / steadyState1Probabilty; // P acc = min (1, (SSP2) / (SSP1))
            if(acceptanceProbability > 1){
                acceptanceProbability = 1;
            }

            double proposeProbabilty = (state1 == state2) ? markovChain.get(state1-1).getSelfTransitionProbability() : 0.25;
            return acceptanceProbability * proposeProbabilty;
        }
        else {
            return 0.0;
        }
    }

    public double getSejProbabilty(int state1, int state2, int numOfStates, double TS){
        //System.out.println("Starting State: " +State1);
        int index = 0;
        int countSum = 0;
        double estimatedProbability = 0.0;
        for(int i = 0; i < MAX_LOOP; i++){
            for(int x = 0; x < 1; x++){
                index = towerSampling((int) TS, state1);
            }
            count[index-1] += 1;
            currentState = 1;
        }
        for(int m = 0; m < count.length; m++){
            //System.out.println(count[m]);
        }
        for(int m = 0; m < count.length; m++){
            countSum += count[m];
        }
        estimatedProbability = ((double)count[state2-1]/countSum);
        return estimatedProbability;
    }

    public double getBiasedTransitionProbabilty(int state1, int state2, double[] ssprob){
        for(int i = 0; i < markovChain.size(); i++){
            markovChain.get(i).steadyStateProbabilty = ssprob[i]; // Assign steady state probabilities to the 9 states
        }
        double steadyState1Probabilty = markovChain.get(state1-1).steadyStateProbabilty;
        double steadyState2Probabilty = markovChain.get(state2-1).steadyStateProbabilty;
        double acceptanceProbability = steadyState2Probabilty / steadyState1Probabilty; // P acc = min (1, (SSP2) / (SSP1))

        if(acceptanceProbability > 1){
            acceptanceProbability = 1;
        }

        double proposeProbabilty = (state1 == state2) ? getSelfTransitionProbability(state1) : 0.25;
        return acceptanceProbability * proposeProbabilty;
    }

    public double getContTransProb(int state1, int state2, double[] rates){
        double ratesSum = 0;
        int counter = 0;

        for(int i = 0; i < rates.length; i++){
            ratesSum += rates[i]; // Sum rates in array
        }

        double rateProbability = getProbabilityRate(state1, state2, rates);
        //System.out.println(rateProbability + " " + getProbabilityTotal(state1,state2,rates));
        double contTransProb = rateProbability / getProbabilityTotal(state1, state2, rates);

        return contTransProb;
    }

    public double getContSejProb(int state1, int state2, double[] rates, double TSC){
        double waitingTime = 0;
        double time = 0;
        double ratesSum = 0;
        double randomNumber = 0;

        for(int i = 0; i < rates.length; i++){
            ratesSum += rates[i];
        }

        int index = 0;
        int countSum = 0;
        double estimatedProbability = 0.0;
        for(int i = 0; i < MAX_LOOP; i++){
            for(int x = 0; x < 1; x++){
                index = towerSamplingContinous(TSC, ratesSum, state1, rates);
            }
            count[index-1] += 1;
            currentState = 1;
        }
        for(int m = 0; m < count.length; m++){
            //System.out.println(count[m]);
        }
        for(int m = 0; m < count.length; m++){
            countSum += count[m];
        }
        estimatedProbability = ((double)count[state2-1]/countSum);
        return estimatedProbability;
    }

    public int towerSampling(int TS, int startState){
        for(int s = 0; s < TS; s++) {
            if(check){
                currentState = startState;
                check = false;
            }
            double randomNumber = random.nextDouble();
            double sumProbability = 0;
            double t[] = new double[markovChain.get(currentState - 1).neighbour.size()];
            for (int i = 0; i < markovChain.get(currentState - 1).neighbour.size(); i++) { // Adds the neighbours transition probability to a list t[]
                t[i] = getTransitionProbabilty(markovChain.get(currentState - 1).number, markovChain.get(currentState - 1).neighbour.get(i).number, 9);
                if (i == markovChain.get(currentState - 1).neighbour.size()) { // Adds the final neighbour as self
                    t[i] = getTransitionProbabilty(currentState, currentState, 9);
                }
                //System.out.println(t[i]);
            }
            //System.out.println("neighbours: "+markovChain.get(currentState).neighbour.get(1).number);
            for (int i = 0; i < markovChain.get(currentState - 1).neighbour.size(); i++) { // Sums the probabilties in the array
                sumProbability += t[i];
                if (randomNumber < sumProbability) { // If sumProbability is greater than randomNumber, go to that currentState
                    currentState = markovChain.get(currentState - 1).neighbour.get(i).number;
                    //System.out.println(currentState);
                    break;
                }
            }
        }
        return currentState;
    }

    public int towerSamplingContinous(double TSC, double rateTotal, int startState, double[] rates){
        double waitingTime = 0;
        double time = 0;
        double ratesSum = rateTotal;
        double randomNumber = 0;

        //System.out.println(ratesSum);
         while(time < TSC) {
            //System.out.println(time);
            randomNumber = random.nextDouble();
            waitingTime = -(1/ratesSum)*Math.log(randomNumber);
            time = time + waitingTime;
            if(time > TSC){
                return currentState;
            }
            if (check) {
                currentState = startState;
                check = false;
            }
            double sumProbability = 0;
            double t[] = new double[markovChain.get(currentState-1).neighbour.size()];
            for (int i = 0; i < markovChain.get(currentState-1).neighbour.size(); i++) { // Adds the neighbours transition probability to a list t[]
                t[i] = getContTransProb(markovChain.get(currentState-1).number, markovChain.get(currentState-1).neighbour.get(i).number, rates);
                //System.out.println(t[i]);
            }
            //System.out.println("neighbours: "+markovChain.get(currentState).neighbour.get(1).number);
            for (int i = 0; i < markovChain.get(currentState - 1).neighbour.size(); i++) { // Sums the probabilties in the array
                sumProbability += t[i];
                if (randomNumber < sumProbability) { // If sumProbability is greater than randomNumber, go to that currentState
                    currentState = markovChain.get(currentState - 1).neighbour.get(i).number;
                    //System.out.println(currentState);
                    break;
                }
            }
        }
        return currentState;
    }

    public double getSelfTransitionProbability(int state){
        double[] transitionProbabilityArray = new double[markovChain.get(state-1).neighbour.size()-1];
        double transitionProbability = 0;
        for(int i = 0; i < markovChain.get(state-1).neighbour.size()-1; i++){
            transitionProbabilityArray[i] = getTransitionProbabilty(markovChain.get(state-1).number,markovChain.get(state-1).neighbour.get(i).number,9);
            //System.out.println(transitionProbabilityArray[i]);
        }
        for(int i = 0; i < markovChain.get(state-1).neighbour.size()-1; i++){
            transitionProbability = transitionProbability + transitionProbabilityArray[i];
        }
        return (1.0 - transitionProbability);
    }

    public double getProbabilityRate(int state1, int state2, double[] rates){
        if(markovChain.get(state1-1).number == 1 && markovChain.get(state2-1).number == 2){
            return rates[0];
        }
        else if(markovChain.get(state1-1).number == 1 && markovChain.get(state2-1).number == 3){
            return rates[1];
        }
        else if(markovChain.get(state1-1).number == 2 && markovChain.get(state2-1).number == 1){
            return rates[2];
        }
        else if(markovChain.get(state1-1).number == 2 && markovChain.get(state2-1).number == 3){
            return rates[3];
        }
        else if(markovChain.get(state1-1).number == 3 && markovChain.get(state2-1).number == 1){
            return rates[4];
        }
        else if(markovChain.get(state1-1).number == 3 && markovChain.get(state2-1).number == 2){
            return rates[5];
        }
        return 0.0;
    }

    public double getProbabilityTotal(int state1, int state2, double[] rates){
        if(markovChain.get(state1-1).number == 1 && markovChain.get(state2-1).number == 2){
            return rates[0]+rates[1];
        }
        else if(markovChain.get(state1-1).number == 1 && markovChain.get(state2-1).number == 3){
            return rates[1]+rates[0];
        }
        else if(markovChain.get(state1-1).number == 2 && markovChain.get(state2-1).number == 1){
            return rates[2]+rates[3];
        }
        else if(markovChain.get(state1-1).number == 2 && markovChain.get(state2-1).number == 3){
            return rates[3]+rates[2];
        }
        else if(markovChain.get(state1-1).number == 3 && markovChain.get(state2-1).number == 1){
            return rates[4]+rates[5];
        }
        else if(markovChain.get(state1-1).number == 3 && markovChain.get(state2-1).number == 2){
            return rates[5]+rates[4];
        }
        return 0.0;
    }
}