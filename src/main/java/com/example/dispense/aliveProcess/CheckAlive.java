package com.example.dispense.aliveProcess;

import com.example.dispense.distributionProcess.Distributor;
import com.example.dispense.services.Containers;
import com.example.dispense.services.SubtaskDistribution;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Service
public class CheckAlive {
    RestTemplate restTemplate = new RestTemplate();
    String uri="http://localhost:";
    Containers containers;
    SubtaskDistribution subtaskDistribution;
    private static Logger log = Logger.getLogger(Distributor.class.getName());

    public CheckAlive(Containers containers, SubtaskDistribution subtaskDistribution) {
        this.containers = containers;
        this.subtaskDistribution = subtaskDistribution;
    }
    public void startCheck(){
        aliveChecker.start();
    }

    Thread aliveChecker = new Thread(new Runnable() {
        @Override
        public void run() {
            while(true){
                try {
                    if(!containers.isEmptyAllContainer()){
                        for (String path:containers.getAllContainers()) {
                            try {
                                ResponseEntity<String> response = restTemplate.getForEntity(uri+path+"/alive", String.class);
                                System.out.println(response.getBody() + "noexeption");
                            } catch (Exception e) {
                                if(containers.checkAliveContainsKey(path)){
                                    containers.checkAlivePut(path,containers.checkAliveGet(path)+1);
                                }else {
                                    containers.checkAlivePut(path,1);
                                }
                                System.out.println(e.getMessage());
                            }
                        }

                        if(!containers.isEmptyCheckAlive()){
                            updateDataAndContainer();
                        }
                    }
                    Thread.sleep(1000*30); //1000 - 1 сек
                } catch (InterruptedException ex) {
                }
            }
        }
    });

    /*@Override
    public void run() {
        if(!takenContainer.isEmpty()){
            List<String> forDelete=new ArrayList<>();

            for (String path:takenContainer) {
                try {
                    ResponseEntity<String> response = restTemplate.getForEntity(uri+path, String.class);
                    System.out.println(response.getBody());
                } catch (Exception e) {
                    forDelete.add(path);
                    System.out.println(e.getMessage());
                }
            }

            if(!forDelete.isEmpty()){
                updateDataAndContainer(forDelete);
            }
        }
    }*/

    public void updateDataAndContainer(){
        List<String> forDelete=new ArrayList<>();
        for (Map.Entry<String, Integer> entry : containers.checkAliveEntrySet()){
            if(entry.getValue()==2){
                forDelete.add(entry.getKey());
                containers.deleteFromAllContainer(entry.getKey());
                if(containers.getFreeContainer().contains(entry.getKey())){
                    log.info("Узел с портом: "+entry.getKey()+" не отвечает, удаляем узел");
                    containers.getFreeContainer().remove(entry.getKey());
                }else {
                    containers.getTakenContainer().remove(entry.getKey());
                    Integer lostSubtask=subtaskDistribution.getDistributedMap().get(entry.getKey());
                    log.info("Узел с портом: "+entry.getKey()+" не отвечает, удаляем узел, перераспределяем задачу: "+lostSubtask);
                    subtaskDistribution.getDistributedMap().remove(entry.getKey());
                    subtaskDistribution.getTakenSubtask().remove(lostSubtask);

                    int indexReplace=0;
                    for(int i=0;i<subtaskDistribution.getFreeSubtask().size();i++){
                        if(lostSubtask<subtaskDistribution.getFreeSubtask().get(i)){
                            indexReplace=i;
                            return;
                        }
                    }
                    subtaskDistribution.getFreeSubtask().add(indexReplace,lostSubtask);
                }
            }
        }
        if(!forDelete.isEmpty()){
            for (String s : forDelete) {
                containers.getCheckAlive().remove(s);
            }
        }

    }
}
