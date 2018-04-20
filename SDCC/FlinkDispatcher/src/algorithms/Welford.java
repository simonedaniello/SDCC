package algorithms;

public class Welford {

    private double current_mean;
    private double current_var;
    private double current_index;


    public double getCurrent_mean() {
        return current_mean;
    }

    public void setCurrent_mean(double current_mean) {
        this.current_mean = current_mean;
    }

    public double getCurrent_var() {
        return current_var;
    }

    public void setCurrent_var(double current_var) {
        this.current_var = current_var;
    }

    public Welford() {

        current_mean = 0.0;
        current_var = 0.0;
        current_index = 0.0;


    }

    /**
     * This method takes in input a double element like the previous one but uses indexes that are never reset
     *
     * @param elem
     */


    public void addElement(double elem) {

        current_index++;

        double old_mean = current_mean;
        double old_var = current_var;

        current_mean = old_mean + (1 / current_index) * (elem - old_mean);
        current_var = old_var + ((current_index - 1.0) / current_index) * ((elem - old_mean) * (elem - old_mean));

        return;
    }


    /**
     * This method reset all indexes
     */

    public void resetIndexes() {

        current_mean = 0.0;
        current_var = 0.0;
        current_index = 0.0;

    }



}