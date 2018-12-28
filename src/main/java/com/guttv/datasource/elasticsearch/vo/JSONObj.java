package com.guttv.datasource.elasticsearch.vo;


import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class JSONObj {

    private JsonObject jsonObject = new JsonObject();

    public JSONObj() {

    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this.jsonObject);
    }

    public JSONObj(JSONObj json) {
        this.jsonObject = json.jsonObject;
    }

    public JsonObject get() {
        return jsonObject;
    }

    public JSONObj add(String key, JSONObj value) {
        jsonObject.add(key, value.get());
        return this;
    }


    public JSONObj add(String key, String value) {
        jsonObject.addProperty(key, value);
        return this;
    }

    public JSONObj add(String key, Number value) {
        jsonObject.addProperty(key, value);
        return this;
    }

    public JSONObj add(String key, Boolean value) {
        jsonObject.addProperty(key, value);
        return this;
    }

    public JSONObj add(String key, Character value) {
        jsonObject.addProperty(key, value);
        return this;
    }
    public JSONObj add(String key, Integer value) {
        jsonObject.addProperty(key, value);
        return this;
    }

    public JSONObj add(String key,JSONArr value){
        jsonObject.add(key,value.get());
        return this;
    }



    public static void main(String[] args) throws Exception{
//        JSONObj json = new JSONObj("query",new JSONObj("match",new JSONObj("message","xxxx")));



//        JSONObj json = new JSONObj()
//                .add("query", new JSONObj()
//                        .add("match", new JSONObj()
//                                .add("message", "xxxxx")
//                                .add("message2", "xxxx")
//                        ));
        JSONObj json = new JSONObj()
                .add("query", new JSONObj()
                        .add("match", new JSONArr()
                                .add("message","xxxx")
                                .add("message2","xxxx")));
//        System.out.println(JsonUtil.GSON.toJson(json.get()));

    }
}
