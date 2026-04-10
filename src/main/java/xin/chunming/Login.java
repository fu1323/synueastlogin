package xin.chunming;


import xin.chunming.bean.user;
import xin.chunming.util.sm4;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Login {
    private String wanip;

    public Login(String wanip) {
        this.wanip = wanip;
    }

    public String getWanip() {
        return wanip;
    }

    public void setWanip(String wanip) {
        this.wanip = wanip;
    }



    public void login_lnuni(user u) throws MalformedURLException {
        try {
            String url = "http://42.177.95.156:9919/api/portal/auth?userName="+ sm4.parse( u.getName()).replace("+","%2B").replace("=","%3D")+"&password="+sm4.parse( u.getPwd()).replace("+","%2B").replace("=","%3D")+"&userIp=" + wanip + "&brasIp=218.25.0.169";
            URL url2 = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) url2.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setReadTimeout(5000);
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.0.0 Safari/537.36");
            urlConnection.setRequestProperty("Accept-Encoding", "gzip, deflate");
            urlConnection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9");
            InputStream inputStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String s;
            while ((s = br.readLine()) != null) {
                System.out.println(s);
                System.out.println("Success 登陆成功!");



            }

        } catch (Exception eee) {
            System.out.println(eee + "请求登录错误");
            eee.printStackTrace();
        }
    }


}
