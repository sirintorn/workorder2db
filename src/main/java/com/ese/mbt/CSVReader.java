package com.ese.mbt;

import com.ese.mbt.bean.Machine;
import com.ese.mbt.bean.Workorder;
import com.ese.mbt.bean.WorkorderFg;
import com.ese.mbt.bean.WorkorderMaterial;
import com.ese.mbt.dao.WorkorderManagement;

//import javax.resource.spi.work.Work;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CSVReader {

    Properties prop;
    List<Machine> machineList;
    public CSVReader(Properties prop, List<Machine> machineList) {
        super();
        this.prop = prop;
        this.machineList = machineList;
    }

    public void ReadFile(){
        System.out.println(" machine size "+machineList.size());
        for(int i = 0; i < machineList.size(); i++){
            ReadToDB(machineList.get(i));
        }

    }

    public void ReadToDB(Machine mc){

//        String csvFile = "/Users/hs5tff/Downloads/MDEXB/workorder.csv";
        String csvFile = mc.getMountPath()+"/"+prop.getProperty("fileName");
        String line = "";
        String cvsSplitBy = "\\t";
//        String regex = "\"([^\"]*)\"";
//        Pattern pattern = Pattern.compile(regex);

        Workorder workorder = new Workorder();
        WorkorderManagement wom = new WorkorderManagement();
        List<WorkorderMaterial> materialList = new ArrayList<WorkorderMaterial>();
        WorkorderMaterial woMat = new WorkorderMaterial();
        WorkorderFg woFG = new WorkorderFg();

        int state = 0;  // 0 = do nothing, 1 = set workorder and then set raw-material, 2 =  set workorder fg, 3 = stop
        int rmHeaderCount = 1;
        int rmDetailCount = 0;
        int fgHeaderCount = 1;
        int fgDetailCount = 0;

        String bufferOutput = "";
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile, StandardCharsets.UTF_8))) {

            while ((line = br.readLine()) != null) {

//                System.out.println("line "+line.toString() + " State "+state);
                String[] output = line.split(cvsSplitBy);

                for(int i = 0; i < output.length; i++){
                    String presentOutput = output[i].toString();

//                    System.out.println("presentOutput "+presentOutput + " bufferOutput "+bufferOutput+" countRmColumn "+countRmColumn);

                    workorder.setMachineId(mc.getId());
                    workorder.setStatus(1); //Just import status 1 = waiting

                    switch (presentOutput) {
                        // finish header record workorder
                        case "\"Work Order Input\"":
                            state = 1;  //Change from state 0 to 1 then insert workorder
                            int id = wom.postWorkorder(workorder);
                            workorder.setId(id);
                            woMat.setWorkorder(workorder);
                            break;

                        case "\"Work Order Output\"":
                            state = 2;
                            break;

                        case "\"Total\"":
                            state = 3;
                            deleteFile(csvFile);
                            break;
                    }

                    if(state == 0){
                        switch (bufferOutput) {
                            case "\"Work Order Report\"":
                                String woBarcode = splitDoubleQuat(presentOutput);
                                workorder.setWoBarcode(woBarcode);
                                break;

                            case "\"Work Order Date:\"":
                                String woDate = splitDoubleQuat(presentOutput);
                                woDate = dateThaiToInter(woDate);
                                workorder.setWoDate(new Date(woDate));
                                break;

                            case "\"PRQ ID:\"":
                                String prqid = splitDoubleQuat(presentOutput);
                                workorder.setPrqId(prqid);
                                break;

                            case "\"IPO:\"":
                                String ipo = splitDoubleQuat(presentOutput);
                                workorder.setIpo(ipo);
                                break;

                            case "\"PRD Completion Deadline:\"":
                                String prdCompleteDade = splitDoubleQuat(presentOutput);
                                prdCompleteDade = dateThaiToInter(prdCompleteDade);
                                workorder.setPrdCompletionDeadline(new Date(prdCompleteDade));
                                break;

                            case "\"Project: \"":
                                String project = splitDoubleQuat(presentOutput);
                                workorder.setProject(project);
                                break;

                            case "\"Work Order ID:\"":
                                String woId = splitDoubleQuat(presentOutput);
                                workorder.setWoId(woId);
                                break;
                        }
                    }

                    if(state == 1 ){
                        System.out.println("rmHeaderCount "+rmHeaderCount+" "+presentOutput);
                        presentOutput = presentOutput.replace("\"", "");
                        presentOutput = presentOutput.replace(",", "");

//                        if ((rmHeaderCount >= 0 && rmHeaderCount <= 11) || rmHeaderCount == 15) {
//                            rmHeaderCount++;
//                        }else if(rmHeaderCount == 11 ){
//                            woMat.setSpecoilBarcode(presentOutput);
//                            rmHeaderCount++;
//                        }else if(rmHeaderCount == 12 ){
//                            woMat.setSpecoilCode(presentOutput);
//                            rmHeaderCount++;
//                        }else if(rmHeaderCount == 13 ){
//                            woMat.setSpecoilDescription(presentOutput);
//                            rmHeaderCount++;
//                        }else if(rmHeaderCount >= 16){
//                            rmDetailCount++;
//                            System.out.println("rmDetailCount "+rmDetailCount);
//                            if(rmDetailCount == 2){
//                                woMat.setLot(presentOutput);
//                            }else if(rmDetailCount ==3){
//                                woMat.setCoilNo(presentOutput);
//                            }else if(rmDetailCount ==4){
//                                woMat.setCoilBarcode(presentOutput);
//                            }else if(rmDetailCount ==5){
//                                woMat.setCoilWeightStart(Double.parseDouble(presentOutput));
//                            }else if(rmDetailCount ==6){
//                                woMat.setCoilWeightEstimate(Double.parseDouble(presentOutput));
//                                wom.postWorkorderMaterial(woMat);
//                            }else if(rmDetailCount >=7){
//                                rmDetailCount = 0;
//                            }
//                            rmHeaderCount++;
//                        }
                        if ((rmHeaderCount >= 0 && rmHeaderCount <= 11) || rmHeaderCount == 15) {
                            rmHeaderCount++;
                        }else if(rmHeaderCount == 12 ){
                            woMat.setSpecoilBarcode(presentOutput);
                            rmHeaderCount++;
                        }else if(rmHeaderCount == 13 ){
                            woMat.setSpecoilCode(presentOutput);
                            rmHeaderCount++;
                        }else if(rmHeaderCount == 14 ){
                            woMat.setSpecoilDescription(presentOutput);
                            rmHeaderCount++;
                        }else if(rmHeaderCount >= 16){
                            rmDetailCount++;
                            if(rmDetailCount == 2){
                                woMat.setLot(presentOutput);
                            }else if(rmDetailCount ==3){
                                woMat.setCoilNo(presentOutput);
                            }else if(rmDetailCount ==4){
                                woMat.setCoilBarcode(presentOutput);
                            }else if(rmDetailCount ==5){
                                woMat.setCoilWeightStart(Double.parseDouble(presentOutput));
                            }else if(rmDetailCount ==6){
                                woMat.setCoilWeightEstimate(Double.parseDouble(presentOutput));
                                wom.postWorkorderMaterial(woMat);
                            }else if(rmDetailCount >=6){
                                rmDetailCount = 0;
                            }
                            rmHeaderCount++;
                        }


                    }else if(state == 2){
                        System.out.println("fgHeaderCount "+fgHeaderCount+" "+presentOutput);

                        presentOutput = presentOutput.replace("\"", "");
                        presentOutput = presentOutput.replace(",", "");

                        if((fgHeaderCount >= 0 && fgHeaderCount <= 14)){
                            fgHeaderCount++;
                        }else if(fgHeaderCount == 15){
                            woFG.setFgBarcode(presentOutput);
                            fgHeaderCount++;
                        }else if(fgHeaderCount == 16){
                            woFG.setFgSpec(presentOutput);
                            fgHeaderCount++;
                        }else if(fgHeaderCount == 17){
                            woFG.setFgDescription(presentOutput);
                            fgHeaderCount++;
                        }else if(fgHeaderCount >=20 ){
                            System.out.println("fgDetailCount "+fgDetailCount);
                            fgDetailCount++;
                            if(fgDetailCount == 1){
                                woFG.setFgLenght(Double.parseDouble(presentOutput));
                            }else if(fgDetailCount == 2){
                                woFG.setFgQty(Integer.parseInt(presentOutput));
                            }else if(fgDetailCount == 3){
                                woFG.setFgWeightEstimate(Double.parseDouble(presentOutput));
                            }else if(fgDetailCount == 4){
                                woFG.setFgArea(Double.parseDouble(presentOutput));
                                woFG.setWorkorderId(workorder.getId());
                                wom.postWorkorderFG(woFG);
                            }else if(fgDetailCount >=8){
                                fgDetailCount = 0;
                            }
                            fgHeaderCount++;
                        }else{
                            fgHeaderCount++;
                        }
                    }

                    bufferOutput = presentOutput;
                }


            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteFile(String mountPath){
        File file = new File(mountPath);

        if (file.delete()) {
            System.out.println("File deleted successfully");
        } else {
            System.out.println("Failed to delete the file");
        }
    }

    public String splitDoubleQuat(String in){
        String out = "";
        out = in.substring(1,in.length()-1);
        return out;
    }

    public String dateThaiToInter(String in){
        String out = "";

        String sub = in.substring(6,10);

        int inter = Integer.parseInt(sub) - 543;

        String sub2 = in.substring(0,6);
        
        Date dd = new Date(sub2+inter);

        System.out.println("lenght "+in.length()+" in "+in+" sub "+ sub+" sub2 "+sub2+" inter "+inter+" dd "+dd);

        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        out = format.format(dd);
        System.out.println("out "+out);
        return out;
    }

}
