package com.example.admin;

public class ModelCat {

    private String id;
    private String name;
    private String nrSets;
    private String setCounter;

    public ModelCat(String id, String name, String nrSets,String setCounter) {
        this.id = id;
        this.name = name;
        this.nrSets = nrSets;
        this.setCounter = setCounter;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNrSets() {
        return nrSets;
    }

    public void setNrSets(String nrSets) {
        this.nrSets = nrSets;
    }

    public String getSetCounter() {
        return setCounter;
    }

    public void setSetCounter(String setCounter) {
        this.setCounter = setCounter;
    }
}
