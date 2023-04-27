package com.example.dispense.services;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SubtaskDistribution {
    private List<Integer> freeSubtask=new ArrayList<>();
    private List<Integer> takenSubtask=new ArrayList<>();
    private Map<String,Integer> distributedMap=new HashMap<>();

    public void addInFreeSubtask(Integer subtaskId){
        freeSubtask.add(subtaskId);
    }
    public void addInFreeSubtask(Integer index,Integer subtaskId){
        freeSubtask.add(index,subtaskId);
    }
    public void addInTakenSubtask(Integer subtaskId){
        takenSubtask.add(subtaskId);
    }
    public void addInDistributedMap(String container,Integer subtaskId){
        distributedMap.put(container,subtaskId);
    }
    public void deleteFromFreeSubtask(int subtaskId){
        freeSubtask.remove(subtaskId);
    }
    public void deleteFromTakenSubtask(int subtaskId){
        takenSubtask.remove(subtaskId);
    }
    public void deleteFromDistributedMap(String container){
        distributedMap.remove(container);
    }
    public boolean isEmptyFreeSubtask(){
        return freeSubtask.isEmpty();
    }

    public Integer getOnIndexFreeSubtask(Integer index){
        return freeSubtask.get(index);
    }
    public Integer sizeFreeSubtask(){
        return freeSubtask.size();
    }

    public Integer getValue(String key){
        return distributedMap.get(key);
    }
    public void createFreeSubtasks(Integer integer){
        for(int i=1;i<integer+1;i++) freeSubtask.add(i);
    }

    public List<Integer> getFreeSubtask() {
        return freeSubtask;
    }

    public List<Integer> getTakenSubtask() {
        return takenSubtask;
    }

    public Map<String, Integer> getDistributedMap() {
        return distributedMap;
    }
}
