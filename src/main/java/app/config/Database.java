package app.config;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Database {

    private String driverClass;
    private String user;
    private String password;
    private String url;

    public String getDriverClass() {
        return driverClass;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getUrl() {
        return url;
    }

}
