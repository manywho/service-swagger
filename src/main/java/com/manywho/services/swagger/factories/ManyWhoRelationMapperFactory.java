package com.manywho.services.swagger.factories;


import com.manywho.services.swagger.ServiceConfiguration;
import com.manywho.services.swagger.services.ManyWhoRelationMapperService;

public class ManyWhoRelationMapperFactory {
    public ManyWhoRelationMapperService createManyWhoRelationMapper(ServiceConfiguration serviceConfiguration) {
        return new ManyWhoRelationMapperService(serviceConfiguration);
    }
}
