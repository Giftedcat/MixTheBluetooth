package com.hc.mixthebluetooth.storage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import static android.os.ParcelFileDescriptor.MODE_APPEND;

public class Storage {

    private final String widthKey = "widthKey";
    private final String firstTimeStartKey = "firstTimeStartKey";
    private final String invalidAT = "invalidAT";
    private final String codedFormatKey = "codedFormatKey";
    private SharedPreferences sp;
    @SuppressLint("WrongConstant")
    public Storage(Context context){
        sp = context.getSharedPreferences("storage",
                MODE_APPEND| Context.MODE_PRIVATE);
    }


    public void saveData(String key, boolean value){
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key,value);
        editor.apply();
    }

    public void saveData(String key, String value){
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key,value);
        editor.apply();
    }

    public void saveWidth(int value){
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(widthKey,value);
        editor.apply();
    }

    public void saveFirstTime(){
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(firstTimeStartKey,false);
        editor.apply();
    }

    public void saveInvalidAT(){
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(invalidAT,false);
        editor.apply();
    }

    public void saveCodedFormat(String code){
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(codedFormatKey,code);
        editor.apply();
    }

    public boolean getData(String key){
        return sp.getBoolean(key,false);
    }

    public String getDataString(String key){
        return sp.getString(key,null);
    }

    public int getWidth(){return sp.getInt(widthKey,-1);}

    public boolean getFirstTime(){
        return sp.getBoolean(firstTimeStartKey,true);
    }

    public boolean getInvalidAT(){ return sp.getBoolean(invalidAT,true);}

    public String getCodedFormat(){ return sp.getString(codedFormatKey,"GBK");}

}
