import java.util.ArrayList;

public class State {

    int number;
    double steadyStateProbabilty = 1.0;
    double transitionRate = 0.0;
    ArrayList<State> neighbour = new ArrayList<State>();

    public State(int number){
        this.number = number;
    }

    public void addNeighbour(State state){
        if(!neighbour.contains(state)){
            neighbour.add(state);
            state.neighbour.add(this);
        }
        if(!neighbour.contains(this)){
            neighbour.add(this);
        }
    }

    public void addNeighbourNoSelf(State state){
        if(!neighbour.contains(state)){
            neighbour.add(state);
            state.neighbour.add(this);
        }
    }

    public int getNumber(){
        return number;
    }

    public ArrayList<State> getNeighbours(){
        return neighbour;
    }

    public double getSteadyStateProbability(){
        return steadyStateProbabilty;
    }

    public double getSelfTransitionProbability(){
        double selfTransitionProbabilty = 1.25;
        for(int i = 0; i < (neighbour.size()); i++){
            selfTransitionProbabilty -= 0.25;
        }
        return selfTransitionProbabilty;
    }
}