package com.example.dispense.services;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class Containers {
    private List<String> freeContainer=new ArrayList<>();
    private List<String> takenContainer=new ArrayList<>();
    private List<String> allContainer=new ArrayList<>();
    private Map<String,Integer> checkAlive=new HashMap<>();

    public void addInFreeContainer(String container){
        freeContainer.add(container);
    }
    public void addInTakenContainer(String container){
        takenContainer.add(container);
    }
    public void addInAllContainer(String container){
        allContainer.add(container);
    }
    public String deleteFromFreeContainer(int container){
        return freeContainer.remove(container);
    }
    public void deleteFromTakenContainer(String container){
        takenContainer.remove(container);
    }
    public void deleteFromAllContainer(String container){
        allContainer.remove(container);
    }

    public boolean isEmptyFreeContainer(){
        return freeContainer.isEmpty();
    }
    public boolean isEmptyAllContainer(){
        return allContainer.isEmpty();
    }
    public boolean isEmptyCheckAlive(){
        return checkAlive.isEmpty();
    }
    public Integer sizeFreeContainer(){
        return freeContainer.size();
    }
    public String getOnIndexFreeContainer(Integer index){
        return freeContainer.get(index);
    }
    public List<String> getAllContainers(){
        return allContainer;
    }

    public boolean checkAliveContainsKey(String path){
        return checkAlive.containsKey(path);
    }
    public void checkAlivePut(String path,Integer integer){
        checkAlive.put(path,integer);
    }
    public int checkAliveGet(String path){
        return checkAlive.get(path);
    }
    public Set<Map.Entry<String, Integer>> checkAliveEntrySet(){
        return checkAlive.entrySet();
    }

    public boolean freeContainerContains(Integer integer){
        return freeContainer.contains(integer);
    }


    public List<String> getFreeContainer() {
        return freeContainer;
    }

    public List<String> getTakenContainer() {
        return takenContainer;
    }

    public List<String> getAllContainer() {
        return allContainer;
    }

    public Map<String, Integer> getCheckAlive() {
        return checkAlive;
    }
}
