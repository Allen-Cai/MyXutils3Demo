package com.yhcdhp.cai.daydays.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.yhcdhp.cai.R;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.List;


public class UiUtils {


    static Toast toast = null;

    /**
     * 动态替换文本并改变文本颜色
     * 仅限制一种颜色
     */
    public static SpannableStringBuilder getSpannableStringBuilder(Context context, int resStrId, int resColorId,
                                                                   Object... args) {
        return getSpannableStringBuilder(context, context.getString(resStrId, args), resColorId, args);
    }

    /**
     * 动态替换文本并改变文本颜色
     * 仅限制一种颜色
     */
    public static SpannableStringBuilder getSpannableStringBuilder(Context context, String resStr, int resColorId,
                                                                   Object... args) {

        SpannableStringBuilder spanStr = new SpannableStringBuilder(resStr);
        int i = -1;
        for (Object o : args) {
            if (o instanceof String) {
                String arg = (String) o;
                i = resStr.indexOf(arg, i + 1);
                spanStr.setSpan(new ForegroundColorSpan(context.getResources().getColor(resColorId)), i,
                        i + arg.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            }
        }
        return spanStr;
    }

    public static void startActivityWithAnim(Activity activity, Intent intent) {
        startActivityWithAnim(activity, intent, R.anim.slide_from_right, R.anim.slide_to_left);
    }

    public static void finishActivityWithAnim(Activity activity) {
        finishActivityWithAnim(activity, R.anim.slide_from_left, R.anim.slide_to_right);
    }

    public static void showDialog(Dialog dialog) {
        if (dialog != null && !dialog.isShowing())
            dialog.show();
    }

    public static void dismissDialog(Dialog dialog) {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

    public static boolean isActivityNormal(Activity activity) {
        if (activity != null && !activity.isFinishing())
            return true;
        return false;
    }

    /**
     * 扩大view的点击区域
     *
     * @param view
     * @param top
     * @param bottom
     * @param left
     * @param right
     */
    public static void expandViewTouchDelegate(final View view, final int top,
                                               final int bottom, final int left, final int right) {

        ((View) view.getParent()).post(new Runnable() {
            @Override
            public void run() {
                Rect bounds = new Rect();
                view.setEnabled(true);
                view.getHitRect(bounds);

                bounds.top -= top;
                bounds.bottom += bottom;
                bounds.left -= left;
                bounds.right += right;

                TouchDelegate touchDelegate = new TouchDelegate(bounds, view);

                if (View.class.isInstance(view.getParent())) {
                    ((View) view.getParent()).setTouchDelegate(touchDelegate);
                }
            }
        });
    }

    /**
     * 跳转到新activity
     *
     * @param activity  老activity，用于调起startActivity()方法
     * @param intent    跳到新activity的intent
     * @param enterAnim 新activity的进入动画
     * @param exitAnim  老activity的退出动画
     */
    public static void startActivityWithAnim(Activity activity, Intent intent, int enterAnim, int exitAnim) {
        activity.startActivity(intent);
        activity.overridePendingTransition(enterAnim, exitAnim);
        activity = null;
    }

    /**
     * 结束activity
     *
     * @param activity  老activity，用于调起finish()方法
     * @param enterAnim 新activity的进入动画
     * @param exitAnim  老activity的退出动画
     */
    public static void finishActivityWithAnim(Activity activity, int enterAnim, int exitAnim) {
        activity.finish();
        activity.overridePendingTransition(enterAnim, exitAnim);
        activity = null;
    }

    /**
     * 获取当前屏幕截图，包含状态栏
     *
     * @param activity
     * @return
     */
    public static Bitmap snapShotWithStatusBar(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        int width = getScreenWidth(activity);
        int height = getScreenHeight(activity);
        Bitmap bp = null;
        bp = Bitmap.createBitmap(bmp, 0, 0, width, height);
        view.destroyDrawingCache();
        return bp;

    }

    /**
     * 获取当前屏幕截图，不包含状态栏
     *
     * @param activity
     * @return
     */
    public static Bitmap snapShotWithoutStatusBar(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;

        int width = getScreenWidth(activity);
        int height = getScreenHeight(activity);
        Bitmap bp = null;
        bp = Bitmap.createBitmap(bmp, 0, statusBarHeight, width, height
                - statusBarHeight);
        view.destroyDrawingCache();
        return bp;

    }

    // 屏幕高度
    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public static int getScreenHeight(Activity context) {

        Display display = context.getWindowManager().getDefaultDisplay();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            display.getSize(size);
            return size.y;
        }
        return display.getHeight();
    }

    // 屏幕宽度
    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public static int getScreenWidth(Activity context) {

        Display display = context.getWindowManager().getDefaultDisplay();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            display.getSize(size);
            return size.x;
        }
        return display.getWidth();
    }

    // dp to px
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    // px to dp
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 获得最小移动距离 后控件移动方向
     *
     * @param context
     * @return
     */
    public static int getTouchSlop(Context context) {
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        return configuration.getScaledTouchSlop();
    }

//    /**
//     * 创建一个在下面显示的toast 需要再onPause调cancelToast
//     *
//     * @param context
//     * @param text
//     * @param style
//     * @return
//     */
//    public static AppMsg makeToast(Activity context, CharSequence text, AppMsg.Style style) {
//        AppMsg appMsg = AppMsg.makeText(context, text, AppMsg.STYLE_CONFIRM);
//        appMsg.setLayoutGravity(BOTTOM);
//        return appMsg;
//    }

//    public static void cancelToast(Activity context) {
//        if (context == null)
//            return;
//        if (SDK_INT < ICE_CREAM_SANDWICH) {
//            AppMsg.cancelAll(context);
//        }
//    }
//    private static Handler mHandler = new Handler();
//    private static Runnable r = new Runnable() {
//        public void run() {
//            if (mtoast != null) {
//                mtoast.cancel();
//                mtoast = null;//toast隐藏后，将其置为null
//            }
//
//        }
//    };
//
//    public static void showShortToast(Context context, int stringID) {
//        try {
//            mHandler.removeCallbacks(r);
//            LayoutInflater inflater = LayoutInflater.from(context);
//            View view = inflater.inflate(R.layout.custom_toast_layout, null);
//            TextView toastContent = (TextView) view.findViewById(R.id.tvTextToast);
//            if (!TextUtils.isEmpty(String.valueOf(stringID))) {
//                toastContent.setText(context.getResources().getString(stringID));
//            } else {
//                return;
//            }
//            if (mtoast == null) {
//                mtoast = new Toast(context);
//            }
//            int marginBotton = getScreenHeight((Activity) context) / 4;
//            mtoast.setGravity(Gravity.BOTTOM, 0, marginBotton);
//            mtoast.setDuration(Toast.LENGTH_SHORT);
//            mtoast.setView(view);
//            mHandler.postDelayed(r, 1000);
//            mtoast.show();
//        } catch (Exception e) {
//            if (mtoast != null) {
//                mtoast.cancel();
//                mtoast = null;
//            }
//        }
//
//
//    }

    /**
     * 自定义toast(3000毫秒显示)
     */
    public static void showLongCustomToast(Context context, String msg) {
        try {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.custom_toast, null);
            TextView toastContent = (TextView) view.findViewById(R.id.tvTextToast);
            if (!TextUtils.isEmpty(msg)) {
                toastContent.setText(msg);
            } else {
                return;
            }
            if (toast == null) {
                toast = new Toast(context);
            }
            int marginBotton = getScreenHeight((Activity) context) / 4;
            toast.setGravity(Gravity.BOTTOM, 0, marginBotton);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(view);
            toast.show();
        } catch (Exception e) {
            if (toast != null) {
                toast.cancel();
            }
        }
    }

    /**
     * 自定义toast(1500毫秒显示 传String)
     */
    public static void showShortCustomToast(Context context, String msg) {

        try {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.custom_toast, null);
            TextView toastContent = (TextView) view.findViewById(R.id.tvTextToast);
            if (!TextUtils.isEmpty(msg)) {
                toastContent.setText(msg);
            } else {
                return;
            }
            if (toast == null) {
                toast = new Toast(context);
            }
            int marginBotton = getScreenHeight((Activity) context) / 4;
            toast.setGravity(Gravity.BOTTOM, 0, marginBotton);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(view);
            toast.show();
        } catch (Exception e) {
            if (toast != null) {
                toast.cancel();
            }
        }
    }

    /**
     * 自定义toast(1500毫秒显示,传String的ID)
     */
    public static void showShortCustomToast(Context context, int stringID) {
        try {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.custom_toast, null);
            TextView toastContent = (TextView) view.findViewById(R.id.tvTextToast);
            if (!TextUtils.isEmpty(String.valueOf(stringID))) {
                toastContent.setText(context.getResources().getString(stringID));
            } else {
                return;
            }
            if (toast == null) {
                toast = new Toast(context);
            }
            int marginBotton = getScreenHeight((Activity) context) / 4;
            toast.setGravity(Gravity.BOTTOM, 0, marginBotton);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(view);
            toast.show();
        } catch (Exception e) {
            if (toast != null) {
                toast.cancel();
            }
        }
    }

    /**
     * 自定义toast(1500毫秒显示,传String的ID)
     */
    public static void showShortSpecialToast(Context context, String msg) {
        Toast sToast = null;
        try {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.custom_toast, null);
            TextView toastContent = (TextView) view.findViewById(R.id.tvTextToast);
            if (!TextUtil.checkString(msg)) {
                toastContent.setText(msg);
            } else {
                return;
            }
            if (sToast == null) {
                sToast = new Toast(context);
            }
            int marginBotton = getScreenHeight((Activity) context) / 4;
            sToast.setGravity(Gravity.BOTTOM, 0, marginBotton);
            sToast.setDuration(Toast.LENGTH_SHORT);
            sToast.setView(view);
            sToast.show();
        } catch (Exception e) {
            if (sToast != null) {
                sToast.cancel();
            }
        }
    }

    /**
     * 销毁toast
     */
    public static void cancelToast() {
        if (toast != null) {
            toast.cancel();
            toast = null;
        }
    }

    /**
     * 带List选项的Dialog;
     * LYL
     *
     * @param context  上下文；
     * @param title    标题；
     * @param dataList list数据；
     * @param callBack 回调函数 （点击的是第几个）；
     */
    public static void showListDialog(final Context context, String title, final List<String> dataList, final ListDialogCallBack callBack) {
        View mListDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_list, null);
        TextView tv_title = (TextView) mListDialogView.findViewById(R.id.mTV_listDialog_title);
        ListView lv_content = (ListView) mListDialogView.findViewById(R.id.mList_listDialog_Content);
        lv_content.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return dataList.size();
            }

            @Override
            public Object getItem(int position) {
                return dataList.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View mListDialogItem = LayoutInflater.from(context).inflate(R.layout.item_listdialog, null);
                TextView tv = (TextView) mListDialogItem.findViewById(R.id.mTV_listDialog_item);
                tv.setText(dataList.get(position));
                return mListDialogItem;
            }
        });
        tv_title.setText(title);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(mListDialogView);
        final Dialog dialog = builder.show();
        lv_content.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                callBack.choosedItem(position);
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }

            }
        });

    }

    /**
     * 带一个输入框的dialog；
     * LYL
     *
     * @param context
     * @param title
     * @param callBack
     */
    public static void showDialogWithOneEdit(final Context context, String title, final OneEditDialogCallBack callBack) {
        View mOneEditDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_one_edit, null);
        TextView tv_title = (TextView) mOneEditDialogView.findViewById(R.id.mTV_OneEditDialog_title);
        final EditText editText = (EditText) mOneEditDialogView.findViewById(R.id.mEdit_OneEditDialog);
        tv_title.setText(title);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setView(mOneEditDialogView)
                .setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                callBack.inputString(editText.getText().toString().trim());
            }
        });
        Dialog dialog = builder.create();
        dialog.show();
        editText.requestFocus();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS); //


    }

    /**
     * 获取一个自定义的DatePickerDialog；
     * LYL
     *
     * @param context
     * @param listener
     * @param cal
     * @return
     */
    public static DatePickerDialog makeDatePicker(Context context, long startTime, long endTime, Calendar cal, DatePickerDialog.OnDateSetListener listener) {
        Calendar c;
        if (cal == null) {
            c = Calendar.getInstance();
        } else {
            c = cal;
        }
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int[] time = new int[]{year, month, day};
        DatePickerDialog newFragment = new DatePickerDialog(context, listener, year, month, day);
        DatePicker dpView = newFragment.getDatePicker();
        newFragment.setCanceledOnTouchOutside(true);
        //设置开始、结束时间；（如果放到newFragment.setTitle("");的下边：会出现原来的title样式）
        if (startTime != 0l) {
            dpView.setMinDate(startTime);
        }
        if (endTime != 0l) {
            dpView.setMaxDate(endTime);
        }
        newFragment.setTitle("");
        LinearLayout llFirst = (LinearLayout) dpView.getChildAt(0);
        if (!(llFirst.getChildAt(0) instanceof ViewGroup)) {
            return newFragment;
        }
        ViewGroup llSecond = (LinearLayout) llFirst.getChildAt(0);
        for (int i = 0; i < llSecond.getChildCount(); i++) {
            // 日期分割线；（5.0以上系统里边包含TextView，不去掉会崩溃。）
            if (llSecond.getChildAt(i) instanceof TextView) {
                break;
            }
            NumberPicker picker = (NumberPicker) llSecond.getChildAt(i);
            //设置年、月、日显示；
            picker.setValue(time[i]);
            // reflection - picker.setDividerDrawable(divider); << didn't seem to work.
            Field[] pickerFields = NumberPicker.class.getDeclaredFields();
            for (Field pf : pickerFields) {
                if (pf.getName().equals("mSelectionDivider")) {
                    pf.setAccessible(true);
                    try {
                        pf.set(picker, context.getResources().getDrawable(R.mipmap.title_background));
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (Resources.NotFoundException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
        // New top:
        int titleHeight = 100;
        // Container:
        LinearLayout llTitleBar = new LinearLayout(context);
        llTitleBar.setOrientation(LinearLayout.VERTICAL);
        llTitleBar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, titleHeight));

        // TextView Title:
        TextView tvTitle = new TextView(context);
        tvTitle.setText("选择生日");
        tvTitle.setGravity(Gravity.CENTER);
//        tvTitle.setPadding(10, 30, 10, 30);
        tvTitle.setTextSize(17);
        tvTitle.setTextColor(context.getResources().getColor(R.color.titleAndButton_background_color));
        tvTitle.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, titleHeight - 2));
        llTitleBar.addView(tvTitle);

        // View line:
        View vTitleDivider = new View(context);
        vTitleDivider.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2));
        vTitleDivider.setBackgroundColor(context.getResources().getColor(R.color.light_silver));
        llTitleBar.addView(vTitleDivider);
        dpView.addView(llTitleBar);
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) llFirst.getLayoutParams();
        lp.setMargins(0, titleHeight, 0, 0);
        return newFragment;
    }

    /**
     * 带选项的Dialog回调接口；
     * LYL
     */
    public interface ListDialogCallBack {
        public void choosedItem(int item);
    }

    /**
     * 带一个输入框的Dialog回调接口；
     * LYL
     */
    public interface OneEditDialogCallBack {
        public void inputString(String str);
    }
}
