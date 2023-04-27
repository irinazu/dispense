package com.example;

import java.util.ArrayList;
import java.util.List;

public class ListQueueAndDataForMachine {
    DataForMachine dataForMachine=new DataForMachine();
    List<ArrayList<ArrayList<Integer>>> arrayListQueue=new ArrayList<>();

    public DataForMachine getDataForMachine() {
        return dataForMachine;
    }

    public void setDataForMachine(DataForMachine dataForMachine) {
        this.dataForMachine = dataForMachine;
    }

    public void setArrayListQueue(List<ArrayList<ArrayList<Integer>>> arrayListQueue) {
        this.arrayListQueue = arrayListQueue;
    }

    public List<ArrayList<ArrayList<Integer>>> getArrayListQueue() {
        return arrayListQueue;
    }

    @Override
    public String toString() {
        return "ListQueueAndDataForMachine{" +
                "dataForMachine=" + dataForMachine +
                ", arrayListQueue=" + arrayListQueue +
                '}';
    }
}
