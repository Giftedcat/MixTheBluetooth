package com.hc.mixthebluetooth.customView;

import android.app.Activity;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.core.widget.PopupWindowCompat;

import com.hc.mixthebluetooth.R;
import com.hc.mixthebluetooth.storage.Storage;

public class PopWindowFragment {

    public static final String KEY_HEX_SEND = "KEY_HEX_SEND";
    public static final String KEY_HEX_READ = "KEY_HEX_READ";
    public static final String KEY_DATA = "KEY_DATA";
    public static final String KEY_TIME = "KEY_TIME";
    public static final String KEY_CLEAR = "KEY_CLEAR_RECYCLER_";

    private Storage storage;

    private DismissListener listener;

    private CheckBoxSample checkHexSend,checkHexRead,checkTime,checkData,checkClear;

    public PopWindowFragment(View view, Activity activity, DismissListener listener){
        storage = new Storage(activity);
        this.listener = listener;
        showPopupWindow(R.layout.pop_window_message_fragment,view,activity);
    }

    private void showPopupWindow(int layout, View view, final Activity activity) {

        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(view.getContext()).inflate(
                layout, null);

        final PopupWindow popupWindow = new PopupWindow(contentView,
                activity.getWindowManager().getDefaultDisplay().getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT, true);

        checkHexRead =contentView.findViewById(R.id.pop_fragment_hex_read);
        checkHexSend = contentView.findViewById(R.id.pop_fragment_hex_send);
        checkData = contentView.findViewById(R.id.pop_fragment_data);
        checkTime = contentView.findViewById(R.id.pop_fragment_time);
        checkClear = contentView.findViewById(R.id.pop_fragment_clear_recycler);

        //需要先测量，PopupWindow还未弹出时，宽高为0
        contentView.measure(makeDropDownMeasureSpec(popupWindow.getWidth()),
                makeDropDownMeasureSpec(popupWindow.getHeight()));

        //可以退出
        popupWindow.setTouchable(true);

        //设置动画
        popupWindow.setAnimationStyle(R.style.pop_window_anim);


        int offsetX =  view.getWidth() - popupWindow.getContentView().getMeasuredWidth();
        int offsetY = 0;
        PopupWindowCompat.showAsDropDown(popupWindow, view, offsetX, offsetY, Gravity.START);

        View.OnClickListener viewListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.pop_fragment_hex_read:
                    case R.id.pop_fragment_hex_read_text:
                        checkHexRead.toggle();
                        break;
                    case R.id.pop_fragment_hex_send:
                    case R.id.pop_fragment_hex_send_text:
                        checkHexSend.toggle();
                        break;
                    case R.id.pop_fragment_data:
                    case R.id.pop_fragment_data_text:
                        checkData.toggle();
                        break;
                    case R.id.pop_fragment_time:
                    case R.id.pop_fragment_time_text:
                        checkTime.toggle();
                        break;
                    case R.id.pop_fragment_clear_recycler:
                    case R.id.pop_fragment_clear_recycler_text:
                        checkClear.toggle();
                        break;
                    case R.id.pop_fragment_clear:
                        if (listener != null)
                            listener.clearRecycler();
                        break;
                }
            }
        };

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

                if (listener != null){
                    save();
                    listener.onDismissListener();
                }
            }
        });

        setState();

        // 设置按钮的点击事件
        setItemClickListener(contentView,viewListener);

        // 设置好参数之后再show
        popupWindow.showAsDropDown(view);


    }


    private void save() {
        storage.saveData(KEY_HEX_READ,checkHexRead.isChecked());
        storage.saveData(KEY_HEX_SEND,checkHexSend.isChecked());
        storage.saveData(KEY_DATA,checkData.isChecked());
        storage.saveData(KEY_TIME,checkTime.isChecked());
        storage.saveData(KEY_CLEAR,checkClear.isChecked());
    }

    private void setState(){
        checkHexRead.setChecked(storage.getData(KEY_HEX_READ));
        checkHexSend.setChecked(storage.getData(KEY_HEX_SEND));
        checkData.setChecked(storage.getData(KEY_DATA));
        checkTime.setChecked(storage.getData(KEY_TIME));
        checkClear.setChecked(storage.getData(KEY_CLEAR));
    }



    /**
     * 设置子View的ClickListener
     */
    private void setItemClickListener(View view,View.OnClickListener listener) {
        if(view instanceof ViewGroup){
            ViewGroup viewGroup = (ViewGroup) view;
            int childCount = viewGroup.getChildCount();
            for (int i=0;i<childCount;i++){
                //不断的递归给里面所有的View设置OnClickListener
                View childView = viewGroup.getChildAt(i);
                setItemClickListener(childView,listener);
            }
        }else{
            view.setOnClickListener(listener);
        }
    }


    @SuppressWarnings("ResourceType")
    private static int makeDropDownMeasureSpec(int measureSpec) {
        int mode;
        if (measureSpec == ViewGroup.LayoutParams.WRAP_CONTENT) {
            mode = View.MeasureSpec.UNSPECIFIED;
        } else {
            mode = View.MeasureSpec.EXACTLY;
        }
        return View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(measureSpec), mode);
    }

    public interface DismissListener{
        void onDismissListener();
        void clearRecycler();
    }

}
