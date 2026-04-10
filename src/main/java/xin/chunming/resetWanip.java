package xin.chunming;

import xin.chunming.bean.router;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class resetWanip {
    public static final String DOWN = "link_dhcp_down";
    public static final String UP = "link_dhcp_up";
    public static final String RECONNECT = "link_dhcp_reconnect";

    public resetWanip() {//重置wan口ip
    }
   // {"func_name":"wan","action":"link_dhcp_up","param":{"id":1}}
    //{"func_name":"wan","action":"link_dhcp_down","param":{"id":1}}

    public static void reConn(router r,String action) throws IOException {
        String json = "{\"func_name\":\"wan\",\"action\":\""+action+"\",\"param\":{\"id\":1}}";
        URL url2 = new URL("http://192.168.9.1/Action/call");
        HttpURLConnection urlConnection = (HttpURLConnection) url2.openConnection();
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("Cookie", "username=admin; sess_key=" + r.getToken());
        urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.0.0 Safari/537.36");
        urlConnection.setRequestProperty("Referer", "http://192.168.9.1");
        urlConnection.setRequestProperty("Accept-Encoding", "gzip, deflate");
        urlConnection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9");
        urlConnection.setDoOutput(true);
        OutputStream outputStream = urlConnection.getOutputStream();
        DataOutputStream dos = new DataOutputStream(outputStream);
        dos.writeBytes(json);
        dos.flush();
        dos.close();

        urlConnection.connect();
        InputStream inputStream = urlConnection.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String s;
        while ((s = br.readLine()) != null) {
            System.out.println(s);


        }


        }
    }



