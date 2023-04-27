package com.example.dispense.distributionProcess;

import com.example.DataForMachine;
import com.example.dispense.producer.ControllerProducer;
import com.example.dispense.services.Containers;
import com.example.dispense.services.DataHolder;
import com.example.dispense.services.DoPartsEntity;
import com.example.dispense.services.SubtaskDistribution;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toCollection;

@Service
public class Distributor {

    Containers containers;
    SubtaskDistribution subtaskDistribution;
    DataHolder dataHolder;
    DoPartsEntity doPartsEntity;
    String uri="http://localhost:";
    String fileNameWithQueue,fileNameGeneralData;
    Boolean flag=true;
    private static Logger log = Logger.getLogger(Distributor.class.getName());

    public Distributor(DoPartsEntity doPartsEntity,Containers containers, SubtaskDistribution subtaskDistribution,DataHolder dataHolder) {
        this.containers = containers;
        this.subtaskDistribution = subtaskDistribution;
        this.dataHolder=dataHolder;
        this.doPartsEntity=doPartsEntity;
    }
    public void startDistribution(/*String fileNameWithQueue,String fileNameGeneralData*/){
        //this.fileNameWithQueue=fileNameWithQueue;
        //this.fileNameGeneralData=fileNameGeneralData;
        distribution.start();
    }
    public void setName(String fileNameWithQueue,String fileNameGeneralData){
        this.fileNameWithQueue=fileNameWithQueue;
        this.fileNameGeneralData=fileNameGeneralData;
    }

    Thread distribution = new Thread(new Runnable() {
        @Override
        public void run() {
            while(flag){
                //try {
                    if(!containers.isEmptyFreeContainer()&&!subtaskDistribution.isEmptyFreeSubtask()){
                        int flag=0;
                        int index=0;

                        try (FileInputStream fis = new FileInputStream(fileNameWithQueue); InputStreamReader isr = new InputStreamReader(fis); BufferedReader br = new BufferedReader(isr)) {
                            StringBuilder forOneSubtask=new StringBuilder();
                            String line;
                            while ((line = br.readLine()) != null&&index<subtaskDistribution.sizeFreeSubtask()&&index<containers.sizeFreeContainer()) {
                                if(line.contains("<"+subtaskDistribution.getOnIndexFreeSubtask(index)+">")){
                                    flag=1;
                                    forOneSubtask.append(line);
                                }

                                if(line.contains("<")&&flag==1){
                                    String k=subtaskDistribution.getOnIndexFreeSubtask(index).toString();
                                    forOneSubtask.delete(0,4+k.length());
                                    forOneSubtask.delete(forOneSubtask.length()-2,forOneSubtask.length());
                                    String oneSubtask=forOneSubtask.toString();
                                    oneSubtask=oneSubtask.replace(" ","");
                                    String[] words_2 = oneSubtask.split("],\\[");

                                    List<ArrayList<Integer>> arrayLists=new ArrayList<>();
                                    for (String s : words_2) {
                                        arrayLists.add(Arrays.stream(s.split(",")).map(Integer::parseInt).collect(toCollection(ArrayList::new)));
                                    }
                                    forOneSubtask.setLength(0);
                                    flag=0;

                                    Integer subtask=subtaskDistribution.getOnIndexFreeSubtask(index);
                                    String path=containers.getOnIndexFreeContainer(index);
                                    subtaskDistribution.addInDistributedMap(path,subtask);
                                    subtaskDistribution.addInTakenSubtask(subtask);
                                    containers.addInTakenContainer(path);

                                    log.info("[Подзадача " + subtask+" распределяется на вычислительный узел с портом: "+path);
                                    dataHolder.getGeneralDataForMachine().setArrayListQueue(arrayLists);

                                    subtaskDistribution.deleteFromFreeSubtask(0);
                                    containers.deleteFromFreeContainer(0);

                                    sendConfirm(path,dataHolder.getGeneralDataForMachine());

                                    ++index;

                                }  else if(flag==1){
                                    forOneSubtask.append(line);
                                }
                            }

                        } catch (IOException ex) {
                            ex.printStackTrace(System.out);
                        }
                    }
                    /*Thread.sleep(1000*30); //1000 - 1 сек
                } catch (InterruptedException ex) {
                }*/
            }
        }
    });

    public void sendConfirm(String path, DataForMachine dataForMachine) throws IOException {

        RestTemplate restTemplate = new RestTemplate();
        try (final FileOutputStream fout = new FileOutputStream(fileNameGeneralData);
             final ObjectOutputStream out = new ObjectOutputStream(fout)) {
            out.writeObject(dataForMachine);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File file1=new File(fileNameGeneralData);
        byte[] bytes_2= Files.readAllBytes(Paths.get(file1.getAbsolutePath()));
        final ByteArrayResource byteArrayResource_2 = new ByteArrayResource(bytes_2) {
            @Override
            public String getFilename() {
                return file1.getName();
            }
        };
        HttpHeaders parts_2 = new HttpHeaders();
        final HttpEntity<ByteArrayResource> dataForMachineHttpEntity = new HttpEntity<>(byteArrayResource_2, parts_2);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap<>();
        requestMap.add("jar", doPartsEntity.getPartsEntity());
        requestMap.add("objectForMachine",dataForMachineHttpEntity);


        try {
            restTemplate.exchange(uri+path+"/getData", HttpMethod.POST, new HttpEntity<>(requestMap, headers), String.class);
        }catch (Exception e){
            Integer lostSubtask=subtaskDistribution.getValue(path);
            log.info("[Узел с IP: "+path+"недоступен, подзадача "+lostSubtask+"перераспределяется снова]");
            updateListOfSubtaskAndContainer(path);
            System.out.println(e);
        }

    }

    public void updateListOfSubtaskAndContainer(String path){
        //обновляем список подзадач и контейнеров в случае недостижимости узла
        containers.deleteFromAllContainer(path);
        containers.deleteFromTakenContainer(path);
        subtaskDistribution.getValue(path);
        Integer lostSubtask=subtaskDistribution.getValue(path);
        subtaskDistribution.deleteFromDistributedMap(path);
        subtaskDistribution.deleteFromTakenSubtask(lostSubtask);

        int indexReplace=0;
        boolean flag=true;
        if(!subtaskDistribution.isEmptyFreeSubtask()){
            for(int i=0;i<subtaskDistribution.sizeFreeSubtask();i++){
                if(lostSubtask<subtaskDistribution.getOnIndexFreeSubtask(i)&& flag){
                    flag=false;
                    indexReplace=i;
                }
            }
            subtaskDistribution.addInFreeSubtask(indexReplace,lostSubtask);

        }else {
            subtaskDistribution.addInFreeSubtask(lostSubtask);
        }
    }
}
