package xin.chunming.bean;

public class user {
    private String name;
    private String pwd;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public user() {
    }

    public user(String name, String pwd, String suffix) {
        this.name = name;
        this.pwd = pwd;
        this.suffix = suffix;
    }

    private String suffix;
}
