package all.controllers.RNG;

import main.java.Car;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ListOfArrivals {

    private List<Car> carArrivalList;
    private static ListOfArrivals me;

    private ListOfArrivals(){
        carArrivalList = new ArrayList<>();
    }

    public static ListOfArrivals getMe() {
        if (me == null){
            me = new ListOfArrivals();
            return me;
        }
        return  me;
    }

    public List<Car> getCarArrivalList() {
        return carArrivalList;
    }

    public void setCarArrivalList(List<Car> carArrivalList) {
        this.carArrivalList = carArrivalList;
    }

    public void PushArrival(Car c){
        carArrivalList.add(c);
        carArrivalList.sort(new Comparator<Car>() {
            @Override
            public int compare(Car e1, Car e2) {
                return (e2.getTime() < e1.getTime()) ? 1 : -1;
            }
        });
    }

}
