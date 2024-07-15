package com.ese.mbt;
import com.ese.mbt.bean.Machine;
import com.ese.mbt.dao.MachineManagement;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class Run {
    public static void main(String[] args) {

        Date date = new Date();
        int dateHour = date.getHours();
        int totalDataSize = 0;

        Properties prop = new Properties();
        InputStream input = null;

        try {

            String filename = "application.properties";
            input = Run.class.getClassLoader().getResourceAsStream(filename);
            if (input == null) {
                System.out.println("Sorry, unable to find " + filename);
                return;
            }

            //load a properties file from class path, inside static method
            prop.load(input);

            //get the property value
            String apiServer = prop.getProperty("apiServer");
            String apiPath = prop.getProperty("apiPath");

            String path = prop.getProperty("path");
            String fileName = prop.getProperty("fileName");
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        MachineManagement mcm = new MachineManagement();
        List<Machine> mcList = mcm.getMachineList();

//        while (dateHour >= 7 && dateHour <= 21) {
        while(true){
            CSVReader csvReader = new CSVReader(prop, mcList);
            csvReader.ReadFile();

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            date = new Date();
            dateHour = date.getHours();

        }
    }
}
