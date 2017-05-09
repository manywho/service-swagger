package com.manywho.services.swagger.entities.mapper;

public class Parameter {
    private String type;
    private String paramName;
    private String name;
    private String passedBy;

    public Parameter() {}

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassedBy() {
        return passedBy;
    }

    public void setPassedBy(String passedBy) {
        this.passedBy = passedBy;
    }
}
