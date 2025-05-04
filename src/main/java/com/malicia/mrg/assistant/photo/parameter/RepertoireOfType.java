package com.malicia.mrg.assistant.photo.parameter;

import com.malicia.mrg.assistant.photo.repertoire.SeanceRepertoire;

import java.util.List;

public class RepertoireOfType {

    private SeanceTypeEnum seanceType;
    private List<SeanceRepertoire> repertoire;

    // Getters and Setters
    public SeanceTypeEnum getSeanceType() {
        return seanceType;
    }

    public void setSeanceType(SeanceTypeEnum seanceType) {
        this.seanceType = seanceType;
    }

    public List<SeanceRepertoire> getRepertoire() {
        return repertoire;
    }

    public void setRepertoire(List<SeanceRepertoire> repertoire) {
        this.repertoire = repertoire;
    }

}