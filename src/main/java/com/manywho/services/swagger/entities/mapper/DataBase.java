package com.manywho.services.swagger.entities.mapper;

import java.util.List;

public class DataBase {
    private String verb;
    private String url;

    private List<Parameter> parameters;

    public DataBase() {}

    public String getVerb() {
        return verb;
    }

    public String getUrl() {
        return url;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }
}
