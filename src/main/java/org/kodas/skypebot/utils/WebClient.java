package org.kodas.skypebot.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;


/**
 * Created by Noy on 09/05/2014.
 */
public final class WebClient {

    public static String request(String urls) throws Exception{
        StringBuilder sb = new StringBuilder();
        URL url = new URL(urls.replace(" ", "%20"));
        URLConnection con = url.openConnection();
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:10.0) Gecko/20100101 Firefox/31.0");
        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
        char[] buff = new char[512];
        while (true) {
            Integer len = br.read(buff, 0, buff.length);
            if (len == -1) {
                break;
            }
            sb.append(buff, 0, len);
        }
        br.close();
        return sb.toString();
    }

    public static String post(String url, String body, List<HttpHeader> headers) throws Exception{
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("POST");
        for (HttpHeader header : headers) {
            connection.setRequestProperty(header.getName(), header.getValue());
        }
        connection.setUseCaches(false);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
        writer.write(body.getBytes());
        writer.flush();
        writer.close();
        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder sb = new StringBuilder();
        char[] buff = new char[512];
        while (true) {
            Integer len = br.read(buff, 0, buff.length);
            if (len == -1) {
                break;
            }
            sb.append(buff, 0, len);
        }
        br.close();
        br.close();
        return sb.toString();
    }
}
