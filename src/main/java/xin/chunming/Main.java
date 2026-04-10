package xin.chunming;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import xin.chunming.bean.router;
import xin.chunming.bean.user;
import xin.chunming.ddns.Sample;

import java.io.*;
import java.util.Date;

public class Main {
    private static String userName;
    private static String password;
    private static String suffix;

    public static void main(String[] args) throws Exception {
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
            if (!userName.contains("手机号") && !password.contains("密码")) {
                login(user);
            }
        } else {
            System.out.println("配置文件有问题!");
            boolean delete = configFile.delete();
            if (delete) {
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(configFile));
                bufferedWriter.write("{\n" +
                        "  \"phone_num\": \"手机号\",\n" +
                        "  \"password\": \"密码\",\n" +
                        "  \"num_suffix\": \"@edu.sysfdx\"\n" +
                        "}");
                bufferedWriter.flush();
                bufferedWriter.close();
            } else {
                System.out.println("配置文件不存在,已生成,请填写srun_config.json! ");
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(configFile));
                bufferedWriter.write("{\n" +
                        "  \"phone_num\": \"手机号\",\n" +
                        "  \"password\": \"密码\",\n" +
                        "  \"num_suffix\": \"@sysfdx.edu\"\n" +
                        "}");
                bufferedWriter.flush();
                bufferedWriter.close();
            }
        }
    }

    private static void login(user user) throws Exception {
        Date date = new Date();
        System.out.println(date);
        System.out.println("尝试下线登录.....");
//  unlogin unlogin = new unlogin();
//   unlogin.unlogin_lnuni();
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
            if (user != null) {
                login.login_lnuni(user);
                Sample sample = new Sample(r.getWanip());
                sample. reg();
            } else System.out.println("发生问题!");
        } else System.out.println("发生问题!");
    }
}
