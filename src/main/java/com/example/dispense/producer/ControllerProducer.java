package com.example.dispense.producer;

import com.example.DataForMachine;
import com.example.ListQueueAndDataForMachine;
import com.example.Result;
import com.example.dispense.aliveProcess.CheckAlive;
import com.example.dispense.distributionProcess.Distributor;
import com.example.dispense.services.Containers;
import com.example.dispense.services.DataHolder;
import com.example.dispense.services.DoPartsEntity;
import com.example.dispense.services.SubtaskDistribution;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toCollection;

@RestController
public class ControllerProducer{
    RestTemplate restTemplate=new RestTemplate();
    List<Result> resultList=new ArrayList<>();
    Integer idOfSubtask;
    String uri="http://localhost:";

    private static Logger log = Logger.getLogger(ControllerProducer.class.getName());

    SubtaskDistribution subtaskDistribution;
    Distributor distributor;
    DataHolder dataHolder;
    DoPartsEntity doPartsEntity;
    Containers containers;
    CheckAlive checkAlive;

    public ControllerProducer(CheckAlive checkAlive,Containers containers,DoPartsEntity doPartsEntity,SubtaskDistribution subtaskDistribution,Distributor distributor,DataHolder dataHolder) {
        this.checkAlive=checkAlive;
        this.containers=containers;
        this.subtaskDistribution=subtaskDistribution;
        this.distributor=distributor;
        this.dataHolder = dataHolder;
        this.doPartsEntity=doPartsEntity;
        checkAlive.startCheck();
        distributor.startDistribution();

    }


    public void doFile(File file,MultipartFile multipartFile){
        try (OutputStream os = new FileOutputStream(file)) {
            os.write(multipartFile.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/registration")
    public void registration(@RequestBody String addressForSendData) {
        log.info("Зарегистрирован вычеслительный узел: "+addressForSendData);
        containers.addInFreeContainer(addressForSendData);
        containers.addInAllContainer(addressForSendData);
    }

    @PostMapping(value = "/getAllData/{idOfSubtask}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public void takeAllData(@PathVariable("idOfSubtask") Integer idOfSubtask,
                            @RequestParam("jar") MultipartFile jarMultipart,
                            @RequestParam("allQueue") MultipartFile allQueueMultipart,
                            @RequestParam("paths") MultipartFile pathsMultipart) {

        subtaskDistribution.getDistributedMap().clear();
        subtaskDistribution.getTakenSubtask().clear();
        subtaskDistribution.getFreeSubtask().clear();

        log.info("[Данные на раздаватель получены]");
        this.idOfSubtask=idOfSubtask;

        File jarFile = new File(jarMultipart.getOriginalFilename());
        doFile(jarFile,jarMultipart);
        doPartsEntity.generateFileWithJar(jarMultipart.getOriginalFilename());

        File allQueueFile = new File(allQueueMultipart.getOriginalFilename());
        doFile(allQueueFile,allQueueMultipart);

        File pathsFile = new File(pathsMultipart.getOriginalFilename());
        doFile(pathsFile,pathsMultipart);

        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(pathsFile)))
        {
            dataHolder.setGeneralDataForMachine((DataForMachine) ois.readObject());
        }
        catch(Exception ex){System.out.println(ex.getMessage());}
        subtaskDistribution.createFreeSubtasks(idOfSubtask);
        //distributor.startDistribution(allQueueMultipart.getOriginalFilename(),pathsMultipart.getOriginalFilename());
        distributor.setName(allQueueMultipart.getOriginalFilename(),pathsMultipart.getOriginalFilename());
    }


    @PostMapping("/getResult")
    public void takeAllData(@RequestBody Result result) {
        log.info("[Результат пришел с:"+result+"]");
        subtaskDistribution.getDistributedMap().remove(result.getPort());
        //distributedMap.remove(result.getPort());
        containers.getTakenContainer().remove(result.getPort());
        //takenContainer.remove(result.getPort());
        containers.getFreeContainer().add(result.getPort());
        //freeContainer.add(result.getPort());
        //distributedMap.remove(result.getPort());
        /*resultList.add(result);
        System.out.println(resultList);
        if(resultList.size()==idOfSubtask){
            System.out.println("ALL");
        }*/
        restTemplate.postForEntity(uri+8080+"/shape/getResult",result,Result.class);
    }

   /* Thread aliveChecker = new Thread(new Runnable() {
        @Override
        public void run() {
            while(true){
                try {
                    if(!allContainer.isEmpty()){
                        for (String path:allContainer) {
                            try {
                                ResponseEntity<String> response = restTemplate.getForEntity(uri+path+"/alive", String.class);
                                System.out.println(response.getBody() + "noexeption");

                            } catch (Exception e) {
                                if(checkAlive.containsKey(path)){
                                    checkAlive.put(path,checkAlive.get(path)+1);
                                }else {
                                    checkAlive.put(path,1);
                                }

                                System.out.println(e.getMessage());
                            }
                        }

                        if(!checkAlive.isEmpty()){
                            updateDataAndContainer(checkAlive);
                        }
                    }
                    Thread.sleep(1000*30); //1000 - 1 сек
                } catch (InterruptedException ex) {
                }
            }
        }
    });*/

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

    /*public void updateDataAndContainer(Map<String,Integer> checkAlive){
        List<String> forDelete=new ArrayList<>();
        for (Map.Entry<String, Integer> entry : checkAlive.entrySet()){
            if(entry.getValue()==2){
                forDelete.add(entry.getKey());
                allContainer.remove(entry.getKey());
                if(freeContainer.contains(entry.getKey())){
                    freeContainer.remove(entry.getKey());
                }else {
                    takenContainer.remove(entry.getKey());
                    Integer lostSubtask=distributedMap.get(entry.getKey());
                    distributedMap.remove(entry.getKey());
                    takenSubtask.remove(lostSubtask);

                    int indexReplace=0;
                    for(int i=0;i<freeSubtask.size();i++){
                        if(lostSubtask<freeSubtask.get(i)){
                            indexReplace=i;
                            return;
                        }
                    }
                    freeSubtask.add(indexReplace,lostSubtask);
                }
            }
        }
        if(!forDelete.isEmpty()){
            for (String s : forDelete) {
                checkAlive.remove(s);
            }
        }

    }*/

    /*public void distribute(){
        if(!freeContainer.isEmpty()&&!freeSubtask.isEmpty()){
            List<Integer> forDelete=new ArrayList<>();
            int flag=0;
            int index=0;

            try (FileInputStream fis = new FileInputStream("newTest.txt"); InputStreamReader isr = new InputStreamReader(fis); BufferedReader br = new BufferedReader(isr)) {
                StringBuilder forOneSubtask=new StringBuilder();
                String line;
                while ((line = br.readLine()) != null&&index<freeSubtask.size()&&index<freeContainer.size()) {
                    if(line.contains("<"+freeSubtask.get(index)+">")){
                        flag=1;
                        forOneSubtask.append(line);
                    }
                    if(line.contains("<")&&flag==1){
                        String k=freeSubtask.get(index).toString();
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


                        distributedMap.put(freeContainer.get(index),freeSubtask.get(index));
                        takenSubtask.add(freeSubtask.get(index));
                        takenContainer.add(freeContainer.get(index));
                        forDelete.add(index);

                        generalDataForMachine.setArrayListQueue(arrayLists);
                        sendConfirm(freeContainer.get(index),generalDataForMachine);

                        ++index;

                    }  else if(flag==1){
                        forOneSubtask.append(line);
                    }
                }

            } catch (IOException ex) {
                ex.printStackTrace(System.out);
            }

            if(!forDelete.isEmpty()){
                for (int i=0;i<forDelete.size();i++) {
                    freeSubtask.remove(freeSubtask.get(i));
                    freeContainer.remove(freeContainer.get(i));
                }
            }
        }
    }*/

    /*Thread distribution = new Thread(new Runnable() {
        @Override
        public void run() {
            while(true){
                try {
                    if(!freeContainer.isEmpty()&&!freeSubtask.isEmpty()){
                        List<Integer> forDelete=new ArrayList<>();
                        int flag=0;
                        int index=0;

                        try (FileInputStream fis = new FileInputStream("newTest.txt"); InputStreamReader isr = new InputStreamReader(fis); BufferedReader br = new BufferedReader(isr)) {
                            StringBuilder forOneSubtask=new StringBuilder();
                            String line;
                            while ((line = br.readLine()) != null&&index<freeSubtask.size()&&index<freeContainer.size()) {
                                if(line.contains("<"+freeSubtask.get(index)+">")){
                                    flag=1;
                                    forOneSubtask.append(line);
                                }
                                if(line.contains("<")&&flag==1){
                                    String k=freeSubtask.get(index).toString();
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


                                    distributedMap.put(freeContainer.get(index),freeSubtask.get(index));
                                    takenSubtask.add(freeSubtask.get(index));
                                    takenContainer.add(freeContainer.get(index));
                                    forDelete.add(index);

                                    generalDataForMachine.setArrayListQueue(arrayLists);

                                    freeSubtask.remove(0);
                                    String path=freeContainer.remove(0);

                                    sendConfirm(path,generalDataForMachine);

                                    ++index;

                                }  else if(flag==1){
                                    forOneSubtask.append(line);
                                }
                            }

                        } catch (IOException ex) {
                            ex.printStackTrace(System.out);
                        }


                    }
                    Thread.sleep(1000*5); //1000 - 1 сек
                } catch (InterruptedException ex) {
                }
            }
        }
    });*/

    /*public void sendConfirm(String path,DataForMachine dataForMachine) throws IOException {
        RestTemplate restTemplate = new RestTemplate();

        try (final FileOutputStream fout = new FileOutputStream("dataForMachine.txt");
             final ObjectOutputStream out = new ObjectOutputStream(fout)) {
                out.writeObject(dataForMachine);
                out.flush();
        } catch (IOException e) {
                e.printStackTrace();
        }
        File file1=new File("dataForMachine.txt");
        byte[] bytes_2= Files.readAllBytes(Paths.get(file1.getAbsolutePath()));
        final ByteArrayResource byteArrayResource_2 = new ByteArrayResource(bytes_2) {
            @Override
            public String getFilename() {
                return file1.getName();
            }
        };
        HttpHeaders parts_2 = new HttpHeaders();
        //parts.setContentType(MediaType.MULTIPART_FORM_DATA);
        final HttpEntity<ByteArrayResource> dataForMachineHttpEntity = new HttpEntity<>(byteArrayResource_2, parts_2);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap<>();
        requestMap.add("jar", partsEntity);
        requestMap.add("objectForMachine",dataForMachineHttpEntity);


        try {
            restTemplate.exchange(uri+path+"/getData", HttpMethod.POST, new HttpEntity<>(requestMap, headers), String.class);
        }catch (Exception e){
            try {
                allContainer.remove(path);
                takenContainer.remove(path);
                Integer lostSubtask=distributedMap.get(path);
                distributedMap.remove(path);
                takenSubtask.remove(lostSubtask);

                int indexReplace=0;
                boolean flag=true;
                if(!freeSubtask.isEmpty()){
                    for(int i=0;i<freeSubtask.size();i++){
                        if(lostSubtask<freeSubtask.get(i)&& flag){
                            flag=false;
                            indexReplace=i;
                        }
                    }
                    freeSubtask.add(indexReplace,lostSubtask);

                }else {
                    freeSubtask.add(lostSubtask);
                }
            }catch (Exception exception){}

            System.out.println(e);
        }

        }*/


}
