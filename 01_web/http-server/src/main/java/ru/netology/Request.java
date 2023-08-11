package ru.netology;


import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.RequestContext;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;

public class Request {
    private String method;
    private String path;
    private List<String> body;

    public Request(String method, String headers) {
        this.method = method;
        this.path = headers;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getHeaders() {
        return path;
    }

    public void setHeaders(String headers) {
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
    public List<NameValuePair> getQueryParams() throws URISyntaxException {
        return URLEncodedUtils.parse(new URI(path),"UTF-8");
    }
    public NameValuePair getQueryParam(String name) throws URISyntaxException {
        List<NameValuePair> params = URLEncodedUtils.parse(new URI(path), "UTF-8");
        for(NameValuePair param : params) {
            if(param.getName().equals(name)) {
                return param;
            }
        }
        return null;
    }
    public String getPostParams() {
        if(this.method.equals("POST")){
            return this.body.get(body.size() - 1);
        } else return "No Parameters";
    }
    public String getPostParam(String name) {
        if(this.method.equals("POST")) {
            String[] parameters = body.get(body.size()-1).split("&");
            for(String s : parameters) {
                if(s.startsWith(name)) {
                    return s;
                }
            }
        }
        return "No parameters";
    }

    }
