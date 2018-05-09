package all.entity;

import main.java.FlinkResult;
import main.java.Semaphore;

import java.util.ArrayList;

public class GeneralInfo {

    private int nOfControllers;
    private int nOfCrossroads;
    private int nOfSemaphores;
    private int nOfMalfunctions;
    private ArrayList<FlinkResult> query1;
    private ArrayList<FlinkResult> query11hour;
    private ArrayList<FlinkResult> query124hours;
    private ArrayList<FlinkResult> query2;
    private ArrayList<FlinkResult> query21hour;
    private ArrayList<FlinkResult> query224hours;
    private ArrayList<FlinkResult> query3;

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

    public ArrayList<FlinkResult> getQuery1() {
        return query1;
    }

    public void setQuery1(ArrayList<FlinkResult> query1) {
        this.query1 = query1;
    }

    public ArrayList<FlinkResult> getQuery2() {
        return query2;
    }

    public void setQuery2(ArrayList<FlinkResult> query2) {
        this.query2 = query2;
    }

    public ArrayList<FlinkResult> getQuery3() {
        return query3;
    }

    public void setQuery3(ArrayList<FlinkResult> query3) {
        this.query3 = query3;
    }

    public int getnOfMalfunctions() {
        return nOfMalfunctions;
    }

    public void setnOfMalfunctions(int nOfMalfunctions) {
        this.nOfMalfunctions = nOfMalfunctions;
    }

    public ArrayList<FlinkResult> getQuery11hour() {
        return query11hour;
    }

    public void setQuery11hour(ArrayList<FlinkResult> query11hour) {
        this.query11hour = query11hour;
    }

    public ArrayList<FlinkResult> getQuery124hours() {
        return query124hours;
    }

    public void setQuery124hours(ArrayList<FlinkResult> query124hours) {
        this.query124hours = query124hours;
    }

    public ArrayList<FlinkResult> getQuery21hour() {
        return query21hour;
    }

    public void setQuery21hour(ArrayList<FlinkResult> query21hour) {
        this.query21hour = query21hour;
    }

    public ArrayList<FlinkResult> getQuery224hours() {
        return query224hours;
    }

    public void setQuery224hours(ArrayList<FlinkResult> query224hours) {
        this.query224hours = query224hours;
    }

}
