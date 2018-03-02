package Model;

import java.util.ArrayList;

public class SemaphoreRepository {
    private static SemaphoreRepository ourInstance = new SemaphoreRepository();
    private ArrayList<Semaphore> list = new ArrayList<>();

    public ArrayList<Semaphore> getList() {
        return list;
    }

    public void setList(ArrayList<Semaphore> list) {
        this.list = list;
    }

    public static SemaphoreRepository getInstance() {
        return ourInstance;
    }

    private SemaphoreRepository() {
    }




}
