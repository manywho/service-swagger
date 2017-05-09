package com.manywho.services.swagger.entities;

import com.manywho.services.swagger.entities.mapper.DataBase;

public class SwaggerMapper {
    private String manyWhoType;
    private String externalId;
    private DataBase load;
    private DataBase create;
    private DataBase update;

    public SwaggerMapper(){}

    public String getManyWhoType() {
        return manyWhoType;
    }

    public String getExternalId() {
        return externalId;
    }

    public DataBase getLoad() {
        return load;
    }

    public DataBase getCreate() {
        return create;
    }

    public DataBase getUpdate() {
        return update;
    }
}
