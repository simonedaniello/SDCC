package algorithms;

/*
    this class measures the distance between two points on a sphere surface.
    In particular, it takes latitude and longitude
    and returns the  great-circle distance

 */


import java.io.Serializable;

public class Harvesine implements Serializable {

    public Harvesine() {};

    public final static double AVERAGE_RADIUS_OF_EARTH_KM = 6371;


    public double calculateExactDistanceInKilometer(double userLat, double userLng,
                                            double venueLat, double venueLng) {

        double latDistance = Math.toRadians(userLat - venueLat);
        double lngDistance = Math.toRadians(userLng - venueLng);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(venueLat))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return  (Math.round(AVERAGE_RADIUS_OF_EARTH_KM * c));
    }


    public double calculateApproximatedDistanceInKilometer (double userLat, double userLng,
                                                          double venueLat, double venueLng){


        return Math.sqrt((userLat-venueLat)*(userLat-venueLat) + (userLng-venueLng)*(userLng-venueLng));

    }





}
