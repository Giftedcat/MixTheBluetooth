package com.hc.mixthebluetooth.customView;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;
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

public class PopWindowMain {

    public static final String BLE_KEY = "BLE_KEY_POP_WINDOW";
    public static final String NAME_KEY = "NAME_KEY_POP_WINDOW";
    public static final String FILTER_KEY = "FILTER_KEY_POP_WINDOW";
    public static final String CUSTOM_KEY = "CUSTOM_KEY_POP_WINDOW";
    public static final String DATA_KEY = "DATA_KEY_POP_WINDOW";

    private CheckBoxSample checkBle;
    private CheckBoxSample checkMix;
    private CheckBoxSample checkName;
    private CheckBoxSample checkFilter;
    private CheckBoxSample checkCustom;
    private LinearLayout layoutFilter;
    private EditText editFilter;

    private Storage storage;

    private DismissListener listener;

    private boolean isResetEngine;//记录下是否切换搜索方式

    public PopWindowMain(View view,Activity activity,DismissListener listener){
        storage = new Storage(activity);
        this.listener = listener;
        isResetEngine = false;
        showPopupWindow(R.layout.pop_window_main,view,activity);
    }

    private void showPopupWindow(int layout, View view, final Activity activity) {

        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(view.getContext()).inflate(
                layout, null);
        checkBle = contentView.findViewById(R.id.pop_main_check_ble);
        checkMix = contentView.findViewById(R.id.pop_main_check_mix);
        checkName = contentView.findViewById(R.id.pop_main_check_name);
        checkFilter = contentView.findViewById(R.id.pop_main_check_filter);
        checkCustom = contentView.findViewById(R.id.pop_main_check_filter_custom);
        layoutFilter = contentView.findViewById(R.id.pop_main_filter);
        editFilter = contentView.findViewById(R.id.pop_main_edit_custom);

        final PopupWindow popupWindow = new PopupWindow(contentView,
                activity.getWindowManager().getDefaultDisplay().getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT, true);

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
                if (v.getId() == -1){
                    return;
                }
                switch (v.getId()){
                    case R.id.pop_main_check_ble:
                    case R.id.pop_main_check_mix:
                        checkBle.toggle();
                        checkMix.toggle();
                        isResetEngine = true;// 切换搜索引擎
                        setFilter();
                        break;
                    case R.id.pop_main_check_name:
                        checkName.toggle();
                        break;
                    case R.id.pop_main_check_filter:
                        checkFilter.toggle();
                        if (checkFilter.isChecked())
                            checkCustom.setChecked(false);
                        break;
                    case R.id.pop_main_check_filter_custom:
                        checkCustom.toggle();
                        if (checkCustom.isChecked())
                            checkFilter.setChecked(false);
                        break;
                }
            }
        };

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                storage.saveData(BLE_KEY,checkBle.isChecked());
                storage.saveData(NAME_KEY,checkName.isChecked());
                storage.saveData(FILTER_KEY,checkFilter.isChecked());
                storage.saveData(CUSTOM_KEY,checkCustom.isChecked());
                if (checkCustom.isChecked()) {
                    storage.saveData(DATA_KEY,editFilter.getText().toString().trim());
                }
                if (listener != null){
                    listener.onDismissListener(isResetEngine);
                }
            }
        });

        // 设置按钮的点击事件
        setItemClickListener(contentView,viewListener);

        // 设置好参数之后再show
        popupWindow.showAsDropDown(view);

        boolean b = storage.getData(BLE_KEY);
        checkName.setChecked(storage.getData(NAME_KEY));
        checkMix.setChecked(!b);
        checkBle.setChecked(b);
        checkFilter.setChecked(storage.getData(FILTER_KEY));
        checkCustom.setChecked(storage.getData(CUSTOM_KEY));
        editFilter.setText(storage.getDataString(DATA_KEY));
        setFilter();

    }

    private void setFilter(){
        if (checkBle.isChecked() && Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            layoutFilter.setVisibility(View.VISIBLE);
        }else {
            checkFilter.setChecked(false);
            checkCustom.setChecked(false);
            layoutFilter.setVisibility(View.GONE);
        }
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
        void onDismissListener(boolean resetEngine);
    }

}
