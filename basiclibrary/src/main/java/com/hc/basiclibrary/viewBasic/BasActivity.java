package com.hc.basiclibrary.viewBasic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.hc.basiclibrary.ioc.ViewUtils;
import com.hc.basiclibrary.viewBasic.manage.BasFragmentManage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Calendar;

public abstract class BasActivity extends AppCompatActivity {

    private Context context;
    private Class mClass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.currentThread().setUncaughtExceptionHandler(new MyUncaughtExceptionHandler());
    }

    private class MyUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
        //当有未捕获的异常的时候会调用
        //Throwable : 其实Exception和Errorfu父类
        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            //将异常保存到文件中
            try {
                //异常文件log.txt，可以判断返回给我们的服务器
                ex.printStackTrace(new PrintStream(new File("log.txt")));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            Writer w = new StringWriter();
            ex.printStackTrace(new PrintWriter(w));
            String smsg = w.toString();
            saveNew(smsg);
            smsg += "     -------这是一处错误(%&*@#$)";
            String ERROR = "exception";
            Log.e(ERROR, smsg);
            save(smsg);
            //保存文件之后，自杀,myPid() : 获取自己的pid
            android.os.Process.killProcess(android.os.Process.myPid());
        }

        public void save(String inputText){
            FileOutputStream out = null;
            BufferedWriter writer = null;
            try {
                out = openFileOutput("errlog",MODE_APPEND| Context.MODE_PRIVATE);
                writer = new BufferedWriter(new OutputStreamWriter(out));
                writer.write(inputText);
                out.write("\r\n".getBytes());
            }catch (IOException e){
                e.printStackTrace();
            }finally {
                try {
                    if (writer != null){
                        writer.close();
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }

        public void saveNew(String inputText){
            FileOutputStream out ;
            BufferedWriter writer = null;
            try {
                out = openFileOutput("errNewLog",Context.MODE_PRIVATE);
                writer = new BufferedWriter(new OutputStreamWriter(out));
                out.write("\r\n".getBytes());
                writer.write(inputText+"------最新异常,时间："+getTime());
                out.write("\r\n".getBytes());
            }catch (IOException e){
                e.printStackTrace();
            }finally {
                try {
                    if (writer != null){
                        writer.close();
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }

        /*public String load(){
            FileInputStream in = null;
            BufferedReader reader = null;
            StringBuilder content = new StringBuilder();
            try{
                in = openFileInput("errlog");
                reader = new BufferedReader(new InputStreamReader(in));
                String line = "";
                while ((line = reader.readLine()) != null){
                    content.append(line);
                }
            }catch (IOException e){
                e.printStackTrace();
            }finally {
                if(reader != null){
                    try{
                        reader.close();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
            return  content.toString();
        }*/

    }

    public void setContext(Context context){
        ViewUtils.inject((Activity)context);
        this.context = context;
        mClass = context.getClass();
        initAll();
    }

    public void log(String str){
        if (mClass == null)
            mClass = getClass();
        Log.d("AppRun"+mClass.getSimpleName(),str);
    }

    public void log(String str,String lv){
        if (mClass == null)
            mClass = getClass();
        if (lv.equals("e")){
            Log.e("AppRun"+mClass.getSimpleName(),str);
        }else {
            Log.w("AppRun"+mClass.getCanonicalName(),str);
        }
    }

    public void toast(final String s,final int state){
        if (context != null)
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, s, state).show();
                }
            });
    }

    public void startActivity(Class clazz){
        if (context == null){
            throw new SecurityException("请重载setContext(Context context)方法");
        }
        startActivity(new Intent(context,clazz));
    }

    public BasFragmentManage setFragment(int viewId, FragmentActivity activity){
        return new BasFragmentManage(viewId,activity);
    }

    public abstract void initAll();

    private String getTime(){
        //获取系统的 日期
        Calendar calendar=Calendar.getInstance();

        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        String secondStr = second<10?"0"+second:second+"";
        String minuteStr = minute<10?"0"+minute:minute+"";
        String hourStr = hour<10?"0"+hour:hour+"";
        String dayStr = day<10?"0"+day:day+"";
        String monthStr = month<10?"0"+month:month+"";

        return monthStr+"月"+dayStr+"日 "+hourStr+":"+minuteStr+":"+secondStr;
    }

}
