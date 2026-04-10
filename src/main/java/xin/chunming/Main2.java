package xin.chunming;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import xin.chunming.bean.router;
import xin.chunming.bean.user;
import xin.chunming.ddns.Sample;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.SocketTimeoutException;
import java.util.Date;

public class Main2 {
    private static String userName;
    private static String password;
    private static String suffix;

    public static void main(String[] args) throws Exception {


        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://223.5.5.5")  // 目标 URL
                .build();
        Response execute = null;
        try {
            execute = client.newCall(request).execute();
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            relogin();
        }
        if (execute.body().string().contains("统一接入认证")) {
            relogin();


        } else System.out.println("正常");
    }

    private static void relogin() throws Exception {
        System.out.println(new Date());
        router r = new router();
        System.out.println("获取token...");
        r.setToken(getToken.token);
        System.out.println(r.getToken());

        System.out.println("重置wanip");

        resetWanip.reConn(r, resetWanip.DOWN);
        Thread.sleep(2000);
        resetWanip.reConn(r, resetWanip.UP);
        Thread.sleep(2000);
        resetWanip.reConn(r, resetWanip.RECONNECT);
        Thread.sleep(3500);
        System.out.println("获取wanip...");
        getWanip getWanip = new getWanip(r);

        getWanip.get();
        if (r.getWanip() != null && !r.getWanip().startsWith("192") && !r.getWanip().startsWith("10")) {
            System.out.println(r.getWanip());
            System.out.println("请求portal认证...");
            Login login = new Login(r.getWanip());
            String path = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            // 2. 处理路径（如果是 JAR 运行，获取其父目录）
            File jarFile = new File(path);
            String jarDir = jarFile.getParentFile().getAbsolutePath();
            user user = null;
            // 3. 拼接配置文件的完整路径
            File configFile = new File(jarDir, "lnlogin_config.json");
            if (configFile.exists()) {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(configFile));
                StringBuilder configJson = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    configJson.append(line);
                }
                ObjectMapper objectMapper = new ObjectMapper();
                bufferedReader.close();
                JsonNode jsonNode = objectMapper.readTree(configJson.toString());
                userName = jsonNode.get("phone_num").asText().strip();
                password = jsonNode.get("password").asText().strip();
                suffix = jsonNode.get("num_suffix").asText().strip();
                user = new user(userName, password, suffix);
                login.login_lnuni(user);
                Sample sample = new Sample(r.getWanip());
                sample.reg();
            } else System.out.println("发生问题!");
        }


    }
}