package com.malicia.mrg.assistant.photo.pojo;

import com.malicia.mrg.assistant.photo.parameter.SeanceType;

import java.util.List;

public class ShootingParam {


    private List<List<String>> possibleValueForRepertoireName;
    private SeanceType seanceType;


    public List<List<String>> getPossibleValueForRepertoireName() {
        return possibleValueForRepertoireName;
    }

    public void setPossibleValueForRepertoireName(List<List<String>> possibleValueForRepertoireName) {
        this.possibleValueForRepertoireName = possibleValueForRepertoireName;
    }

    public SeanceType getSeanceType() {
        return seanceType;
    }

    public void setSeanceType(SeanceType seanceType) {
        this.seanceType = seanceType;
    }
}
