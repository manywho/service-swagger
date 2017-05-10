package com.manywho.services.swagger.database.factory;

import com.manywho.services.swagger.ServiceConfiguration;
import com.manywho.services.swagger.database.services.ManyWhoRelationMapperService;

public class ManyWhoRelationMapperFactory {
    public ManyWhoRelationMapperService createManyWhoRelationMapper(ServiceConfiguration serviceConfiguration) {
        return new ManyWhoRelationMapperService(serviceConfiguration);
    }
}
