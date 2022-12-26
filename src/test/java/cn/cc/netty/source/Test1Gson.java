package cn.cc.netty.source;

import com.google.gson.Gson;

public class Test1Gson {
    public static void main(String[] args) {

        // java.lang.UnsupportedOperationException: Attempted to serialize java.lang.Class: java.lang.String. Forgot to register a type adapter?
        System.out.println(new Gson().toJson(String.class));

        // 类型转换器
        //Gson gson = new GsonBuilder().registerTypeAdapter(Class.class, new Serializer.ClassCodec()).create();
        //System.out.println(gson.toJson(String.class));

    }
}
