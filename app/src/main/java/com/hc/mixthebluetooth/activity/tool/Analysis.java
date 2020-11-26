package com.hc.mixthebluetooth.activity.tool;

import android.animation.ValueAnimator;
import android.content.Context;
import android.location.LocationManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;

public class Analysis {

    public static String getByteToString(byte[] bytes,String code,boolean isHex){
        try {
            if (isHex)
                return bytesToHexString(bytes);
            else
                return new String(bytes,0,bytes.length,code);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    //String字符串的互转
    public static String changeHexString(boolean isChangeHex,String string){
        if (string == null||string.isEmpty()){
            return "";
        }
        if (isChangeHex) {
            try {
                return bytesToHexString(string.getBytes("GBK"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return "";
        }else {
            return hexStringToString(string);
        }
    }

    //byte数组转String
    private static String bytesToHexString(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        int length = sb.length();
        if (length == 1||length == 0){
            return sb.toString();
        }
        if (length%2==1){
            sb.insert(length-1," ");
            length= length-1;
        }
        for (int i = length;i>0;i=i-2){
            sb.insert(i," ");
        }
        return sb.toString();
    }


    public static String hexStringToString(String s) {
        if (s == null || s.equals("")) {
            return null;
        }
        s = s.replace(" ", "");
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(
                        s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            s = new String(baKeyword, "gbk");
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }

    public static boolean detection(String str){
        int number = str.length();
        String scopeStr = "0123456789AaBbCcDdEeFf ";
        for (int i= 1;i<=number;i++){
            if (!scopeStr.contains(str.substring(i-1,i))){
                return true;
            }
        }
        return false;
    }

    public static void setHex(String text, int start, int before, int count, EditText editText){
        if (before > 0)
            return;
        if (text == null || text.equals(""))
            return;
        String temp = text.substring(start,start+count);
        if (detection(temp)){
            //Log.d("AppRun","temp.size is "+temp.length()+" count is "+count);
            String newStr = text.substring(0,start)+text.substring(start+count);
            editText.setText(newStr.toUpperCase());
            editText.setSelection(newStr.length());
        }else {
            editText.setText(text.toUpperCase());
            editText.setSelection(text.length());
        }
    }

    public static byte[] getBytes(String data,String code,boolean isHex){
        byte[] buff = null;
        if (!isHex) {
            try {
                buff =data.getBytes(code);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        else {
            buff = hexString2ByteArray(data);
        }
        return buff;
    }

    /**
     * 将16进制字符串转换为byte[]
     */
    public static byte[] hexString2ByteArray(String bs) {
        if (bs == null) {
            return null;
        }
        int bsLength = bs.length();
        if (bsLength % 2 != 0) {
            bs = "0"+bs;
            bsLength = bs.length();
        }
        byte[] cs = new byte[bsLength / 2];
        String st;
        for (int i = 0; i < bsLength; i = i + 2) {
            st = bs.substring(i, i + 2);
            cs[i / 2] = (byte) Integer.parseInt(st, 16);
        }
        return cs;
    }


    //按间隔取出，例如analysis("123aa456aa789aa000",2,"aa") ->"789"
    //最多3个间隔，4个字符串，从0开始取
    public static String analysis(final String data,int number,String key){
        if (number == 0)
            return data.substring(0,data.indexOf(key));
        if (number == 1) {
            String string = data.substring(data.indexOf(key) + key.length());
            return string.substring(0,string.indexOf(key));
        }
        if (number == 2){
            int length2 = analysis(data,1,key).length();
            int length1 = analysis(data,0,key).length();
            String string = data.substring(length1+length2+key.length()*2);
            return string.substring(0,string.indexOf(key));
        }else {
            int length = analysis(data,0,key).length()+analysis(data,1,key).length()+analysis(data,2,key).length()+key.length()*3;
            return data.substring(length);
        }
    }

    //判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
    public static boolean isOpenGPS(final Context context) {
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps = true,network = true;
        // GPS定位
        if (locationManager != null)
            gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 网络服务定位
        if (locationManager != null)
            network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return gps || network;
    }

    public static String getTime(){
        //获取系统的 日期
        Calendar calendar=Calendar.getInstance();

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        String secondStr = second<10?"0"+second:second+"";
        String minuteStr = minute<10?"0"+minute:minute+"";
        String hourStr = hour<10?"0"+hour:hour+"";

        return hourStr+":"+minuteStr+":"+secondStr+" ";
    }

    /**
     * 动态改变view的高度动画效果
     * 原理:动画改变view LayoutParams.height的值
     * @param view 要进行高度改变动画的view
     * @param startHeight 动画前的view的高度
     * @param endHeight 动画后的view的高度
     */
    public static void changeViewHeightAnimatorStart(final View view, final int startHeight, final int endHeight){
        if(view!=null&&startHeight>=0&&endHeight>=0){
            ValueAnimator animator=ValueAnimator.ofInt(startHeight,endHeight);
            animator.setDuration(200);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    ViewGroup.LayoutParams params=view.getLayoutParams();
                    params.height= (int) animation.getAnimatedValue();
                    view.setLayoutParams(params);
                }
            });
            animator.start();
        }
    }

}
