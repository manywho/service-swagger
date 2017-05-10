package com.manywho.services.swagger.factories;

import com.manywho.services.swagger.ServiceConfiguration;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class HttpClientFactory {
    public CloseableHttpClient createClosableClient(ServiceConfiguration configuration) {
        if (configuration.getBasicAuthPassword() == null && configuration.getBasicAuthUserName() == null) {
            return HttpClients.createDefault();
        } else {
            CredentialsProvider credsProvider = new BasicCredentialsProvider();
            credsProvider.setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
                    new UsernamePasswordCredentials(configuration.getBasicAuthUserName(), configuration.getBasicAuthPassword()));
            return HttpClients.custom()
                    .setDefaultCredentialsProvider(credsProvider).build();
        }
    }
}
