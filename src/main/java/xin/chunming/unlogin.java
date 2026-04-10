package xin.chunming;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import xin.chunming.bean.unloginBean;
import xin.chunming.bean.user;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class unlogin {

    unloginBean unloginBean = new unloginBean();
    public void getsessionid_unlogin(user u) throws MalformedURLException {

        caculate(unloginBean);
        try {
            String url =
                    "http://42.177.95.156:9919/adsl-api/session/school?login="+u.getName()+u.getSuffix();
            URL url2 = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) url2.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(5000);
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.0.0 Safari/537.36");
            urlConnection.setRequestProperty("Accept-Encoding", "gzip, deflate");
            urlConnection.setRequestProperty("Accept-Language", "zh-CN,zh-Hans;q=0.9");
            urlConnection.setRequestProperty("appId", "unicomportal");
            urlConnection.setRequestProperty("Connection", "keep-alive");
            urlConnection.setRequestProperty("Priority", "u=3, i");
            urlConnection.setRequestProperty("Referer", "http://42.177.95.156:9919/");
            urlConnection.setRequestProperty("nonce", this.unloginBean.getNonce());
            urlConnection.setRequestProperty("sign", this.unloginBean.getSign());
            urlConnection.setRequestProperty("timestamp", this.unloginBean.getTimestamp());
            InputStream inputStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            ObjectMapper objectMapper = new ObjectMapper();
            String s;
            while ((s = br.readLine()) != null) {
//            System.out.println(s);
                JsonNode jsonNode = objectMapper.readTree(s);
                String sessionid = jsonNode.get("data").get(0)==null?null:jsonNode.get("data").get(0).get("sessionId").asText();
                if (sessionid != null) {
                    System.out.println(sessionid);
                    unloginBean.setSessionId(sessionid);
                    this.unlogin();
                }else unloginBean.setSessionId(null);


            }
        } catch (ProtocolException e) {
            throw new RuntimeException(e);
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void caculate(unloginBean unloginBean) {
        String appId = "unicomportal";
        String secret = "gesvmkzgoijf883..";

        // 当前时间戳（秒）
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);

        // 生成 11 位随机数字 nonce
        String nonce = generateNonce(11);

        // 拼接字符串
        String raw = appId + secret + timestamp + nonce;

        // 计算 MD5 签名
        String sign = md5(raw);
        unloginBean.setNonce(nonce);
        unloginBean.setSign(sign);
        unloginBean.setTimestamp(timestamp);


        // 输出
//        System.out.println("timestamp: " + timestamp);
//        System.out.println("nonce: " + nonce);
        System.out.println("sign: " + sign);
    }

    public static String generateNonce(int length) {
        Random rand = new Random();
        StringBuilder nonce = new StringBuilder();
        for (int i = 0; i < length; i++) {
            nonce.append(rand.nextInt(10)); // 0-9
        }
        return nonce.toString();
    }

    public static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());

            // 转为十六进制字符串
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    /*{"sessionId":"SY-8651042370051000004788f5AAAM4B","type":"Stop"}*/
    public void unlogin() throws IOException {
        caculate(unloginBean);
        URL url = new URL("http://42.177.95.156:9919/adsl-api/session");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.0.0 Safari/537.36");
        urlConnection.setRequestProperty("Accept-Encoding", "gzip, deflate");
        urlConnection.setRequestProperty("Accept-Language", "zh-CN,zh-Hans;q=0.9");
        urlConnection.setRequestProperty("Content-Type", "application/json");
        urlConnection.setRequestProperty("appId", "unicomportal");
        urlConnection.setRequestProperty("Connection", "keep-alive");
        urlConnection.setRequestProperty("Priority", "u=3, i");
        urlConnection.setRequestProperty("Referer", "http://42.177.95.156:9919/");
        urlConnection.setRequestProperty("Origin", "http://42.177.95.156:9919/");
        urlConnection.setRequestProperty("nonce", this.unloginBean.getNonce());
        urlConnection.setRequestProperty("sign", this.unloginBean.getSign());
        urlConnection.setRequestProperty("timestamp", this.unloginBean.getTimestamp());

        urlConnection.setDoOutput(true);
        OutputStream outputStream = urlConnection.getOutputStream();
        DataOutputStream dos = new DataOutputStream(outputStream);
        dos.writeBytes("{\"sessionId\":\""+ unloginBean.getSessionId() +"\",\"type\":\"Stop\"}");

        dos.flush();
        dos.close();

        urlConnection.connect();
        System.out.println(urlConnection.getResponseMessage());


    }

}
