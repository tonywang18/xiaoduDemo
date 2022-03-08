package com.gidisoft.paul.sdk.orm;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "UserInfo")
public class UserBean {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(columnName = "name")
    public String name;

    @DatabaseField(columnName = "password")
    public String password;

    @DatabaseField(columnName = "uuid")
    public String uuid;

    @DatabaseField(columnName = "mqttclientid")
    public String mqttclientid;

    @DatabaseField(columnName = "mqttusername")
    public String mqttusername;

    @DatabaseField(columnName = "mqttuserpassword")
    public String mqttuserpassword;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getMqttclientid() {
        return mqttclientid;
    }

    public void setMqttclientid(String mqttclientid) {
        this.mqttclientid = mqttclientid;
    }

    public String getMqttusername() {
        return mqttusername;
    }

    public void setMqttusername(String mqttusername) {
        this.mqttusername = mqttusername;
    }

    public String getMqttuserpassword() {
        return mqttuserpassword;
    }

    public void setMqttuserpassword(String mqttuserpassword) {
        this.mqttuserpassword = mqttuserpassword;
    }
}
