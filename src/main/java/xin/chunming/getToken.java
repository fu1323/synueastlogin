package xin.chunming;

import java.io.*;
import java.net.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class getToken {

    static String token;

    static {
        try {
             token = getToken();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getToken() throws IOException {
        String body = "{\"username\":\"admin\",\"passwd\":\"ad6226905e6bec6c0a6be3dc4afc74a5\",\"pass\":\"c2FsdF8xMUFCQ2FiYzEyMyE=\",\"remember_password\":\"\"}";
        URL url = new URL("http://192.168.9.1/Action/login");

        String tk = connect(url, body);
//        System.out.println(tk);
        Pattern pa = Pattern.compile("(=)(.*?)(;)");
        Matcher matcher = pa.matcher(tk);
        while (matcher.find()) {
           String token = matcher.group(2);
            return token;
        }return null;
    }
    private static String connect(URL url, String body) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("Content-Type", "application/json");
        urlConnection.setRequestProperty("Content-Length", "167");
        urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.0.0 Safari/537.36");
        urlConnection.setRequestProperty("Upgrade-Insecure-", "1");
//        urlConnection.setRequestProperty("Referer", "http://127.0.0.1/Main_Login.asp");
//        urlConnection.setRequestProperty("Origin", "http://127.0.0.1");
//        urlConnection.setRequestProperty("Host", "127.0.0.1");
        urlConnection.setRequestProperty("Cookie", "username=admin; sess_key=");
        urlConnection.setRequestProperty("Accept-Encoding", "gzip, deflate");
        urlConnection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9");
        urlConnection.setRequestProperty("Cache-Control", "max-age=0");
        urlConnection.setRequestProperty("Connection", "keep-alive");

        urlConnection.setDoOutput(true);
        OutputStream outputStream = urlConnection.getOutputStream();
        DataOutputStream dos = new DataOutputStream(outputStream);
        dos.writeBytes(body);

        dos.flush();
        dos.close();

        urlConnection.connect();
        return urlConnection.getHeaderField("Set-Cookie");

    }
}
