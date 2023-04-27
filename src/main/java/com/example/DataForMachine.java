package com.example;

import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataForMachine implements Serializable{
    int graph[][];
    List<ArrayList<Integer>> arrayLists=new ArrayList<>();
    Integer numberOfPassengers;
    List<Integer> taxi=new ArrayList<>();
    List<ArrayList<Integer>> arrayListQueue=new ArrayList<>();

    public DataForMachine() {
    }

    public DataForMachine(DataForMachine dataForMachine){
        graph=copy(dataForMachine.graph);
        arrayLists=new ArrayList<ArrayList<Integer>>(dataForMachine.arrayLists);
        taxi=new ArrayList<>(dataForMachine.taxi);
        arrayListQueue=new ArrayList<ArrayList<Integer>>(dataForMachine.arrayListQueue);
        numberOfPassengers=dataForMachine.numberOfPassengers;
    }

    public static int[][] copy(int[][] src) {
        if (src == null) {
            return null;
        }

        int[][] copy = new int[src.length][];

        for (int i = 0; i < src.length; i++) {
            copy[i] = new int[src[i].length];
            System.arraycopy(src[i], 0, copy[i], 0, src[i].length);
        }

        return copy;
    }

    public List<ArrayList<Integer>> getArrayListQueue() {
        return arrayListQueue;
    }

    public void setArrayListQueue(List<ArrayList<Integer>> arrayListQueue) {
        this.arrayListQueue = arrayListQueue;
    }

    public void setGraph(int[][] graph) {
        this.graph = graph;
    }

    public List<ArrayList<Integer>> getArrayLists() {
        return arrayLists;
    }

    public Integer getNumberOfPassengers() {
        return numberOfPassengers;
    }

    public List<Integer> getTaxi() {
        return taxi;
    }

    public int[][] getGraph() {
        return graph;
    }

    public void setArrayLists(List<ArrayList<Integer>> arrayLists) {
        this.arrayLists = arrayLists;
    }

    public void setNumberOfPassengers(Integer numberOfPassengers) {
        this.numberOfPassengers = numberOfPassengers;
    }

    public void setTaxi(List<Integer> taxi) {
        this.taxi = taxi;
    }

    @Override
    public String toString() {
        return "DataForMachine{" +
                "graph=" + Arrays.toString(graph) +
                ", arrayLists=" + arrayLists +
                ", numberOfPassengers=" + numberOfPassengers +
                ", taxi=" + taxi +
                ", arrayListQueue=" + arrayListQueue +
                '}';
    }
}