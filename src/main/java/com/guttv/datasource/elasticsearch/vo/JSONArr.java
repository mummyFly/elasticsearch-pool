package com.guttv.datasource.elasticsearch.vo;

import com.google.gson.JsonArray;

public class JSONArr {
    private static JsonArray jsonArray = new JsonArray();

    public JSONArr() {

    }

    public JSONArr add(String key, JSONObj value) {
        this.jsonArray.add(new JSONObj().add(key, value).get());
        return this;
    }

    public JSONArr add(String key, String value) {
        this.jsonArray.add(new JSONObj().add(key, value).get());
        return this;
    }


    public JSONArr add(String key, Number value) {
        this.jsonArray.add(new JSONObj().add(key, value).get());
        return this;
    }

    public JSONArr add(String key, Character value) {
        this.jsonArray.add(new JSONObj().add(key, value).get());
        return this;
    }

    public JSONArr add(String key, Boolean value) {
        this.jsonArray.add(new JSONObj().add(key, value).get());
        return this;
    }

    public JSONArr add(String key, JSONArr value) {
        this.jsonArray.add(new JSONObj().add(key, value).get());
        return this;
    }

    public JsonArray get() {
        return this.jsonArray;
    }

    public static void main(String[] args) {

    }
}
