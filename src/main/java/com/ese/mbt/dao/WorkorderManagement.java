package com.ese.mbt.dao;

import com.ese.mbt.bean.Workorder;
import com.ese.mbt.bean.WorkorderFg;
import com.ese.mbt.bean.WorkorderMaterial;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.util.List;

public class WorkorderManagement {

    Workorder workorder;


    public WorkorderManagement() {
    }

    public int postWorkorder(Workorder workorder){
        this.workorder = workorder;
        HttpClient client = HttpClient.newHttpClient();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        String woDate = format.format(workorder.getWoDate());

        String requestBody = "{\"woId\": \""+workorder.getWoId()+"\",\"woBarcode\": \""+workorder.getWoBarcode()+"\",\"woDate\": \""+woDate+"\",\"ipo\": \""+workorder.getIpo()+"\",\"prqId\": \""+workorder.getPrqId()+"\",\"project\": \""+workorder.getProject()+"\",\"prdCompletionDeadline\": \""+woDate+"\",\"machineId\": "+workorder.getTMachineId()+
                ",\"status\": 1}";


//        System.out.println("RequestBody "+requestBody);
        HttpRequest request = null;
        try {
            request = HttpRequest.newBuilder()
//                    .uri(new URI("http://192.168.10.10:8080/MBTService/api/postWorkOrder"))
                    .uri(new URI("http://18.207.187.82:8080/MBTService/api/postWorkOrder"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Status code: " + response.statusCode());
            System.out.println("Response body: " + response.body());

            if(response.statusCode() == 201){
                ObjectMapper objMapper = new ObjectMapper();
                workorder = objMapper.readValue(response.body().toString(), Workorder.class);
            }
            return workorder.getId();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

//    public void setWorkorderMaterial(){
//
//    }

    public int postWorkorderMaterial(WorkorderMaterial workorderMaterial){
        HttpClient client = HttpClient.newHttpClient();

        String requestBody = "{\"workorderId\": "+workorderMaterial.getWorkorder().getId()+",\n" +
                "    \"specoilBarcode\": \""+workorderMaterial.getSpecoilBarcode()+"\",\n" +
                "    \"specoilCode\": \""+workorderMaterial.getSpecoilCode()+"\",\n" +
                "    \"specoilDescription\": \""+workorderMaterial.getSpecoilDescription()+"\",\n" +
                "    \"lot\": \""+workorderMaterial.getLot()+"\",\n" +
                "    \"coilNo\": \""+workorderMaterial.getCoilNo()+"\",\n" +
                "    \"coilWeightStart\": "+workorderMaterial.getCoilWeightStart()+",\n" +
                "    \"coilWeightStartActual\": 0,\n" +
                "    \"coilWeightEstimate\": "+workorderMaterial.getCoilWeightEstimate()+",\n" +
                "    \"coilWeightActual\": 0,\n" +
                "    \"coilWeightRemaining\": 0,\n" +
                "    \"status\": 1,\n" +
                "    \"createBy\": 2,\n" +
//                "    \"createDatetime\": \"2024-04-05T19:25:35.621+00:00\",\n" +
//                "    \"modifyBy\": null,\n" +
//                "    \"modifyDatetime\": null,\n" +
                "    \"remark\": \"\",\n" +
                "    \"valid\": true}";

            HttpRequest request = null;
            try {
                request = HttpRequest.newBuilder()
//                        .uri(new URI("http://192.168.10.10:8080/MBTService/api/postWorkOrderMaterial"))
                        .uri(new URI("http://18.207.187.82:8080/MBTService/api/postWorkOrderMaterial"))

                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                System.out.println("Status code: " + response.statusCode());
                System.out.println("Response body: " + response.body());
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
//        }
        return 1;

    }

//    public void setWorkorderFG(){
//
//    }

    public void postWorkorderFG(WorkorderFg workorderFg){
        HttpClient client = HttpClient.newHttpClient();

        String requestBody = "{\n" +
//                "      \"id\": 27,\n" +
                "      \"workorderId\": "+ workorderFg.getWorkorderId()+",\n" +
                "      \"fgBarcode\": \""+ workorderFg.getFgBarcode()+"\",\n" +
                "      \"fgSpec\": \""+ workorderFg.getFgSpec()+"\",\n" +
                "      \"fgDescription\": \""+ workorderFg.getFgDescription()+"\",\n" +
                "      \"fgLenght\": "+ workorderFg.getFgLenght()+",\n" +
                "      \"fgQty\": "+workorderFg.getFgQty()+",\n" +
                "      \"fgWeightEstimate\": "+workorderFg.getFgWeightEstimate()+",\n" +
                "      \"fgArea\": "+workorderFg.getFgArea()+",\n" +
                "      \"fgQtyActual\": 0,\n" +
                "      \"fgWeightActual\": 0,\n" +
                "      \"fgBundle\": 0,\n" +
                "      \"status\": 0,\n" +
                "      \"createBy\": 2,\n" +
//                "      \"createDatetime\": \"2024-06-10T05:53:09.044+00:00\",\n" +
//                "      \"modifyBy\": null,\n" +
                "      \"modifyDatetime\": null,\n" +
                "      \"dataSet\": null,\n" +
                "      \"valid\": true,\n" +
                "      \"send\": false\n" +
                "    }";

        HttpRequest request = null;
        try {
            request = HttpRequest.newBuilder()
//                    .uri(new URI("http://192.168.10.10:8080/MBTService/api/postWorkOrderFG"))
                    .uri(new URI("http://18.207.187.82:8080/MBTService/api/postWorkOrderFG"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Status code: " + response.statusCode());
            System.out.println("Response body: " + response.body());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
