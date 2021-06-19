package com.example.hw06;

import java.util.HashMap;

public class DataHolder { //Класс для хранения данных
    private static DataHolder moInstance;

    private HashMap<String, Object> moHashMap;

    private DataHolder(){
        moHashMap = new HashMap<>();
    }

    public static DataHolder getInstance() {
        if (moInstance == null) moInstance = new DataHolder();
        return moInstance;
    }

    public void putData(String ivKey, Object ioValue) {
        moHashMap.put(ivKey, ioValue);
    }

    public Object getData(String ivKey) {
        return moHashMap.get(ivKey);
    }

    public void  deleteData(String ivKey) { moHashMap.remove(ivKey); }

}
