package com.manywho.services.swagger;

import com.google.inject.AbstractModule;
import com.manywho.sdk.services.actions.ActionHandler;
import com.manywho.sdk.services.actions.ActionProvider;
import com.manywho.sdk.services.types.TypeProvider;
import com.manywho.services.swagger.actions.RawActionHandler;
import com.manywho.services.swagger.actions.RawActionProvider;
import com.manywho.services.swagger.managers.DescribeManager;
import com.manywho.services.swagger.types.RawTypeProvider;

public class ApplicationSwaggerModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(DescribeManager.class);
        bind(TypeProvider.class).to(RawTypeProvider.class);
        bind(ActionProvider.class).to(RawActionProvider.class);
        bind(ActionHandler.class).to(RawActionHandler.class);
    }
}
