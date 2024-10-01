package com.example.messagesfx.utils;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class SolicitudAPI extends Service<String> {
    private String uri;
    private String json;
    private String method;

    public SolicitudAPI(String uri, String json, String method){
        this.uri = uri;
        this.json = json;
        this.method = method;
    }

    @Override
    protected Task<String> createTask() {
        return new Task<String>() {
            @Override
            protected String call() throws Exception {
                return ServiceUtils.getResponse(uri, json, method);
            }
        };
    }
}