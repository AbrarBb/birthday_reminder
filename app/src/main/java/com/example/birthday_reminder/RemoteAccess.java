package com.example.birthday_reminder;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.NameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.utils.URLEncodedUtils;
import java.io.*;
import java.util.List;
import java.net.*;

public class RemoteAccess {
    private RemoteAccess(){}
    private static RemoteAccess instance = new RemoteAccess();

    public static RemoteAccess getInstance(){
        return instance;
    }

    public String makeHttpRequest(String url, String method, List<NameValuePair> params) {
        HttpURLConnection http = null;
        try {
            String paramString = "";
            if (params != null) {
                paramString = URLEncodedUtils.format(params, "utf-8");
            }

            // For GET, append params to URL
            if (method.equalsIgnoreCase("GET") && !paramString.isEmpty()) {
                url += "?" + paramString;
            }

            URL urlc = new URL(url);
            http = (HttpURLConnection) urlc.openConnection();
            http.setRequestMethod(method);
            http.setConnectTimeout(15000);
            http.setReadTimeout(15000);

            // For POST, send params in the request body
            if (method.equalsIgnoreCase("POST")) {
                http.setDoOutput(true);
                http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                OutputStream os = http.getOutputStream();
                os.write(paramString.getBytes("UTF-8"));
                os.flush();
                os.close();
            }

            http.connect();
            
            int responseCode = http.getResponseCode();
            InputStream is = (responseCode == HttpURLConnection.HTTP_OK) 
                             ? http.getInputStream() 
                             : http.getErrorStream();

            if (is != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                is.close();
                return sb.toString().trim();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (http != null) http.disconnect();
        }
        return null;
    }
}
