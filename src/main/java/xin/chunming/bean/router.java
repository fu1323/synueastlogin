package xin.chunming.bean;

public class router {
    private String token;
    private String wanip;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getWanip() {
        return wanip;
    }

    public void setWanip(String wanip) {
        this.wanip = wanip;
    }

    public router() {
    }

    public router(String token, String wanip) {
        this.token = token;
        this.wanip = wanip;
    }
}
