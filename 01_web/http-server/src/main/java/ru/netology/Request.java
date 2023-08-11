package ru.netology;


import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Request {
    private String method;
    private String path;
    private String httpVersion;
    private List<String> body;
    private List<NameValuePair> queryParams;

    public Request(String method, String path, String httpVersion) {
        this.method = method;
        this.path = path;
        this.httpVersion = httpVersion;
        try {
            this.queryParams = URLEncodedUtils.parse(new URI(path),"UTF-8");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String headers) {
        this.path = headers;
    }

    public List<String> getBody() {
        return body;
    }

    public void setBody(List<String> body) {
        this.body = body;
    }
    public void setBodyFromInput(BufferedReader in) {
        final int limit = 4096;
        final var buffer = new char[limit];
        final int read;
        try {
            read = in.read(buffer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int i = 0;
        StringBuilder sb = new StringBuilder();
        final var body = new String(Arrays.copyOf(buffer, read));
        final var headers = Arrays.asList(body.split("\r\n"));
        this.body = headers;
    }


    public List<NameValuePair> getQueryParams() {
        return queryParams;
    }
    public NameValuePair getQueryParam(String name) {
        for(NameValuePair param : queryParams) {
            if(param.getName().equals(name)) {
                return param;
            }
        }
        return null;
    }
    public List<NameValuePair> getPostParams()   {
        if(this.method.equals("POST")){
        String params= this.body.get(body.size() - 1);
        List<NameValuePair> postParams = new ArrayList<>();
        String[] parameters = params.split("&");
        for(String s : parameters) {
            postParams.add(new BasicNameValuePair(s.substring(0, s.indexOf("=")),s.substring(s.indexOf("="))));
        }
        return postParams;
    } else return null;
}
    public NameValuePair getPostParam(String name) {
        if(this.method.equals("POST")) {
            String[] parameters = body.get(body.size()-1).split("&");
            for(String s : parameters) {
                if(s.startsWith(name)) {
                    return new BasicNameValuePair(s.substring(0, s.indexOf("=")),s.substring(s.indexOf("=")));
                }
            }
        }
        return null;
    }

    }
