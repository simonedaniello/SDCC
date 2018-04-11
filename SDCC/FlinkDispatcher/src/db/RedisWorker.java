package db;

import main.java.system.Printer;
import redis.clients.jedis.Jedis;

import java.util.Set;

public class RedisWorker {

    private static Jedis jedis;

    public static void main(String[] args) {
        //Connecting to Redis server on localhost
        connectToDB("localhost");

        addToDB("keyTest1", "valueTest1");
        addToDB("keyTest2", "valueTest2");
        addToDB("keyTest3", "valueTest3");

        addToDB("keyTest4", "valueTest4");

        getFromDB("*");


    }

    private static void connectToDB(String host){
        jedis = new Jedis(host);
        System.out.println("Connection to server sucessfully");
        //check whether server is running or not
//        System.out.println("Server is running: "+jedis.ping());
    }


    public static void addToDB(String key, String value){
        jedis.set(key, value);
    }

    public static void getFromDB(String key){
        Set<String> keys = jedis.keys(key);
        Printer.getInstance().print("getting key names with values", "cyan");
        for (String aList : keys) {
            Printer.getInstance().print("key: " + aList + ", value: " + jedis.get(aList), "yellow");
        }

    }
}

