package com.manywho.services.swagger.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.manywho.services.swagger.ServiceConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import java.io.IOException;
import java.util.HashMap;

public class HttpClientSwagger {
    private CloseableHttpClient closeableHttpClient;

    public HttpClientSwagger(ServiceConfiguration configuration) {
        if (configuration.getBasicAuthPassword() == null && configuration.getBasicAuthUserName() == null) {

            this.closeableHttpClient = HttpClients.createDefault();
        } else {
            CredentialsProvider credsProvider = new BasicCredentialsProvider();
            credsProvider.setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
                    new UsernamePasswordCredentials(configuration.getBasicAuthUserName(), configuration.getBasicAuthPassword()));

            this.closeableHttpClient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
        }
    }

    public HashMap<String, Object> executeOperationHashMap(HttpRequestBase httpClient) {

        try {
            String responseBody = closeableHttpClient.execute(httpClient, new ClientResponseHandler());
            ObjectMapper mapper = new ObjectMapper();
            if (StringUtils.isEmpty(responseBody)) {
                return new HashMap<>();
            }

            return mapper.readValue(responseBody, HashMap.class);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            try {
                closeableHttpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Object executeOperationObject(HttpRequestBase httpClient) {
        try {
            String responseBody = closeableHttpClient.execute(httpClient, new ClientResponseHandler());
            ObjectMapper mapper = new ObjectMapper();
            if (StringUtils.isEmpty(responseBody)) {
                return new Object();
            }
            return mapper.readValue(responseBody, Object.class);

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            try {
                closeableHttpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
