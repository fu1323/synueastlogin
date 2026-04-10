package xin.chunming;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import xin.chunming.bean.router;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class getWanip {
    private router r;

    public getWanip(router r) {
        this.r = r;
    }

    public void get() throws IOException {
        String json = "{\"func_name\":\"wan\",\"action\":\"show\",\"param\":{\"id\":\"1\",\"TYPE\":\"support_wisp,total,data,lte_antenna_support\"}}";
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
        ObjectMapper objectMapper = new ObjectMapper();
        dos.flush();
        dos.close();

        urlConnection.connect();
        InputStream inputStream = urlConnection.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String s;
//        ArrayList<String> arr = new ArrayList<>();
        while ((s = br.readLine()) != null) {
//            System.out.println(s);
            JsonNode jsonNode = objectMapper.readTree(s);
            String wanip = jsonNode.get("Data").get("data").get(0).get("dhcp_ip_addr").asText();
            if (wanip != null) {
                System.out.println(wanip);
                r.setWanip(wanip);
            }

        }
    }



}
