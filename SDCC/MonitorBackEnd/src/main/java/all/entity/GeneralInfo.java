package all.entity;

import java.util.ArrayList;

public class GeneralInfo {

    private int nOfControllers;
    private int nOfCrossroads;
    private int nOfSemaphores;
    private int nOfMalfunctions;
    private ArrayList<String> query1;
    private ArrayList<String> query2;
    private ArrayList<String> query3;

    public int getnOfControllers() {
        return nOfControllers;
    }

    public void setnOfControllers(int nOfControllers) {
        this.nOfControllers = nOfControllers;
    }

    public int getnOfCrossroads() {
        return nOfCrossroads;
    }

    public void setnOfCrossroads(int nOfCrossroads) {
        this.nOfCrossroads = nOfCrossroads;
    }

    public int getnOfSemaphores() {
        return nOfSemaphores;
    }

    public void setnOfSemaphores(int nOfSemaphores) {
        this.nOfSemaphores = nOfSemaphores;
    }

    public ArrayList<String> getQuery1() {
        return query1;
    }

    public void setQuery1(ArrayList<String> query1) {
        this.query1 = query1;
    }

    public ArrayList<String> getQuery2() {
        return query2;
    }

    public void setQuery2(ArrayList<String> query2) {
        this.query2 = query2;
    }

    public ArrayList<String> getQuery3() {
        return query3;
    }

    public void setQuery3(ArrayList<String> query3) {
        this.query3 = query3;
    }

    public int getnOfMalfunctions() {
        return nOfMalfunctions;
    }

    public void setnOfMalfunctions(int nOfMalfunctions) {
        this.nOfMalfunctions = nOfMalfunctions;
    }
}
