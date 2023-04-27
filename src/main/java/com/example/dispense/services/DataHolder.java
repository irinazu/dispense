package com.example.dispense.services;

import com.example.DataForMachine;
import org.springframework.stereotype.Service;

@Service
public class DataHolder {
    DataForMachine generalDataForMachine;

    public void setGeneralDataForMachine(DataForMachine generalDataForMachine) {
        this.generalDataForMachine = generalDataForMachine;
    }

    public DataForMachine getGeneralDataForMachine() {
        return generalDataForMachine;
    }
}
