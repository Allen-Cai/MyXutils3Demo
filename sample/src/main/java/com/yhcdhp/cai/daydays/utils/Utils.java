package com.yhcdhp.cai.daydays.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;
import com.yhcdhp.cai.MyApplication;
import com.yhcdhp.cai.daydays.config.AppEnv;

import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 通用的工具类
 * Created by Administrator on 2015/4/30.
 */
public class Utils {

    /**
     * ******************* get Device ID *****************************
     */
    /*
     * 获取设别的唯一标志
     * 1）该唯一标志生成后需要存在本地文件中，以便后续获取使用
     * 2）以如下顺序来获取唯一标志
     * IMEI，缺点：对于PAD设备可能取不到
     * CPU序列号，缺点：该值需要从系统文件/proc/cpuinfo中获取，某些系统不存在该文件
     * Android2.3版本以上可以使用android.os.Build.SERIAL，缺点：有版本限制
     * Android2.2版本以上系统可以使用ANDROID_ID，注意“9774d56d682e549c”是无效ID，缺点：有版本限制
     * IMSI，缺点：必须插入SIM卡；对于CDMA设备，返回的是一个空值
     * MAC地址，缺点：用户如果未打开WIFI则获取不到
     * UUID，缺点：与设备无关，每次生成都不同，且长度为32位
     * 注意： 1. 需要过滤无效的ID，这些ID保存在assets/invalid-imei.idx中，支持正则表达式
     *       2. 除了IMEI号，其他方式取出的都用特殊前缀标明，例如：CPU添加C; SERIAL添加S; ANDROID_ID添加A; IMSI添加I; MAC添加M; UUID添加U
     */
    private final static String INVALID_IMEI_FILENAME = "invalid-imei.idx";
    private final static String DEVICE_ID_FILENAME_NEW = "DEV";
    private final static String ANDROID_ID_FILENAME = "ANDROID_ID"; //保存手机的android_id，用来校验DEVICE_ID是否需要重新获取
    private static String sDeviceId = null;
    private static String sNewDeviceId = null;

    public static Context getAppContext() {
        if (MyApplication.mApplication != null)
            return MyApplication.mApplication.getApplicationContext();
        return null;
    }

    /**
     * 获得DeviceId
     *
     * @param context
     * @return
     */
    public synchronized static String getDeviceId(Context context) {
        if (sDeviceId == null) {
            File newFile = new File(context.getFilesDir(), DEVICE_ID_FILENAME_NEW);
            if (newFile.exists()) {
                if (AppEnv.DEBUG) {
                    LogUtil.d(AppEnv.TAG + "getDeviceId(), newFile exist.");
                }
                //新文件存在，直接读取
                sDeviceId = readIdFile(context, newFile, true);
                if (TextUtils.isEmpty(sDeviceId)) {
                    //可能读取失败，则重新生成
                    createDeviceId(context, newFile, true);
                }
            } else {
                if (AppEnv.DEBUG) {
                    LogUtil.d(AppEnv.TAG + "getDeviceId(), newFile not exist.");
                }
                createDeviceId(context, newFile, true);
            }
        }
        if (AppEnv.DEBUG) {
            Log.d(AppEnv.TAG, "getDeviceId(), sDeviceId=" + sDeviceId);
        }
        return (sDeviceId == null) ? "" : sDeviceId;
    }


    /**
     * 检查android_id是否变化，来判断是否需要重新生成DeviceId。
     * 在application启动的时候调下
     *
     * @return
     */
    public static boolean checkAndroidId(Context context) {
        String androidId = getAndroidId(context);
        if (!TextUtils.isEmpty(androidId)) {
            File file = new File(context.getFilesDir(), ANDROID_ID_FILENAME);
            if (file.exists()) {
                if (AppEnv.DEBUG) {
                    Log.d(AppEnv.TAG, "checkAndroidId(), ANDROID_ID file exist.");
                }
                //文件存在，直接读取
                String savedAndroidId = readIdFile(context, file, true);
                if (!androidId.equals(savedAndroidId)) {
                    if (AppEnv.DEBUG) {
                        Log.d(AppEnv.TAG, "checkAndroidId(), android_id isn't match, call createDeviceId()");
                    }
                    //如果android_id改变了，可能是换机或者重刷了ROM，就重新生成DEVICE_ID
                    File devFile = new File(context.getFilesDir(), DEVICE_ID_FILENAME_NEW);
                    createDeviceId(context, devFile, true);
                    //将当前的android_id保存到文件中
                    writeIdFile(context, androidId, file, true);
                    return false;
                } else {
                    if (AppEnv.DEBUG) {
                        Log.d(AppEnv.TAG, "checkAndroidId(), android_id is match.");
                    }
                }
            } else {
                if (AppEnv.DEBUG) {
                    Log.d(AppEnv.TAG, "checkAndroidId(), ANDROID_ID file not exist, create it.");
                }
                writeIdFile(context, androidId, file, true);
            }
        }
        return true;
    }

    private static void createDeviceId(Context context, File file, boolean encode) {
        try {
            String deviceId = null;
            // IMEI
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(
                    Context.TELEPHONY_SERVICE);
            deviceId = telephonyManager.getDeviceId();
            if (AppEnv.DEBUG) {
                Log.d(AppEnv.TAG, "imei=" + deviceId);
            }
            if (invalidDeviceId(context, deviceId)) {
                // CPU序列号
                deviceId = getCPUSerial();
                if (deviceId != null) {
                    deviceId = deviceId.toLowerCase();
                }
                if (invalidDeviceId(context, deviceId)) {
                    // android.os.Build.SERIAL
                    deviceId = getSerial();
                    if (invalidDeviceId(context, deviceId)) {
                        // ANDROID_ID
                        deviceId = getAndroidId(context);
                        if (invalidDeviceId(context, deviceId)) {
//                            // IMSI
//                            deviceId = getIMSI(context, 0);
                            if (invalidDeviceId(context, deviceId)) {
                                // MAC地址
                                deviceId = getMacAddress(context);
                                if (invalidDeviceId(context, deviceId)) {
                                    // UUID
                                    deviceId = "U" + getUUID();
                                } else {
                                    deviceId = "M" + deviceId;
                                }
                            } else {
                                deviceId = "I" + deviceId;
                            }
                        } else {
                            deviceId = "A" + deviceId;
                        }
                    } else {
                        deviceId = "S" + deviceId;
                    }
                } else {
                    deviceId = "C" + deviceId;
                }
            }
            sDeviceId = deviceId;
            writeIdFile(context, sDeviceId, file, encode);
        } catch (Exception ex) {
        }
    }


    private static void createNewDeviceId(Context context, File file, boolean encode) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = tm.getDeviceId();
        String AndroidID = android.provider.Settings.System.getString(context.getContentResolver(), "android_id");
        String serialNo = getSerial();
        sNewDeviceId = MD5Util.encode("" + imei + AndroidID + serialNo);

        writeIdFile(context, sNewDeviceId, file, encode);
    }

    private static String readIdFile(Context context, File idFile, boolean decode) {
        RandomAccessFile f = null;
        String deviceId = null;
        try {
            f = new RandomAccessFile(idFile, "r");
            byte[] bytes = new byte[(int) f.length()];
            f.readFully(bytes);
            if (decode) {
                deviceId = AESUtils.AESDecrypt(new String(bytes) + context.getPackageName());
            } else {
                deviceId = new String(bytes);
            }
        } catch (Exception ex) {
        } finally {
            if (f != null) {
                try {
                    f.close();
                } catch (Exception ex) {
                }
            }
        }
        return deviceId;
    }

    private static void writeIdFile(Context context, String id, File idFile, boolean encode) {
        if (TextUtils.isEmpty(id))
            return;

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(idFile, false);
            if (encode) {
                id = AESUtils.AESEncrypt(id + context.getPackageName());
            }
            out.write(id.getBytes());
        } catch (Exception ex) {
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (Exception ex) {
                }
            }
        }
    }

    private static boolean invalidDeviceId(Context context, String str) {
        if (TextUtils.isEmpty(str)) {
            return true;
        }

        InputStream is = null;
        try {
            is = context.getAssets().open(INVALID_IMEI_FILENAME);
            if (is != null) {
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String regexp = null;
                while ((regexp = br.readLine()) != null) {
                    try {
                        Pattern pattern = Pattern.compile(regexp);
                        Matcher match = pattern.matcher(str);
                        if (match.matches()) {
                            return true;
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                if (br != null)
                    br.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception ex) {
                }
            }
        }
        return false;
    }

    /**
     * 获取UUID
     *
     * @return
     */
    private static String getUUID() {
        String id = null;
        try {
            id = UUID.randomUUID().toString();
            id = id.replaceAll("-", "").replace(":", "").toLowerCase();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return id;
    }

    /**
     * 获取网卡的MAC地址
     *
     * @param context
     * @return
     */
    private static String getMacAddress(Context context) {
        String macAddress = null;
        try {
            WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifi.getConnectionInfo();
            if (info != null) {
                macAddress = info.getMacAddress();
                if (macAddress != null) {
                    macAddress = macAddress.replaceAll("-", "").replaceAll(":", "").toLowerCase();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (AppEnv.DEBUG) {
            Log.d(AppEnv.TAG, "macAddress=" + macAddress);
        }
        return macAddress;
    }

    /**
     * 获取ANDROID_ID号，Android2.2版本以上系统有效
     *
     * @return
     */
    private static String getAndroidId(Context context) {
        String android_id = null;
        try {
            if (Build.VERSION.SDK_INT >= 8) {
                android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                if (android_id != null) {
                    android_id = android_id.toLowerCase();
                }
            }
        } catch (Throwable ex) {
            if (AppEnv.DEBUG) {
                Log.e(AppEnv.TAG, ex.toString());
            }
        }
        if (AppEnv.DEBUG) {
            Log.d(AppEnv.TAG, "android_id=" + android_id);
        }
        return android_id;
    }

    /*
     * 获取机器Serial号，Android2.3版本以上有效
     */
    private static String getSerial() {
        String serial = null;
        try {
            if (Build.VERSION.SDK_INT >= 9) {
                Class<Build> clazz = Build.class;
                Field field = clazz.getField("SERIAL");
                serial = (String) field.get(null);
                if (serial != null) {
                    serial = serial.toLowerCase();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (AppEnv.DEBUG) {
            Log.d(AppEnv.TAG, "serial=" + serial);
        }
        return serial;
    }

    /**
     * 获取CPU序列号
     *
     * @return CPU序列号(16位) 读取失败为null
     */
    private static String getCPUSerial() {
        String line = "";
        String cpuAddress = null;
        try {
            // 读取CPU信息
            Process pp = Runtime.getRuntime().exec("cat /proc/cpuinfo");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            // 查找CPU序列号
            for (int i = 1; i < 100; i++) {
                line = input.readLine();
                if (line != null) {
                    // 查找到序列号所在行
                    line = line.toLowerCase();
                    int p1 = line.indexOf("serial");
                    int p2 = line.indexOf(":");
                    if (p1 > -1 && p2 > 0) {
                        // 提取序列号
                        cpuAddress = line.substring(p2 + 1);
                        // 去空格
                        cpuAddress = cpuAddress.trim();
                        break;
                    }
                } else {
                    // 文件结尾
                    break;
                }
            }
            if (ir != null) {
                ir.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        if (AppEnv.DEBUG) {
            Log.d(AppEnv.TAG, "cpuAddress=" + cpuAddress);
        }
        return cpuAddress;
    }


    /**
     * 将多个String拼接
     *
     * @param strings
     * @return
     */
    public static String stringAddString(String... strings) {
        StringBuffer sbBuffer = new StringBuffer();
        for (int i = 0; i < strings.length; i++) {
            if (strings[i] == null) {
                strings[i] = "null";
            }
            sbBuffer.append(strings[i]);
        }
        return sbBuffer.toString();
    }

    public static String stringAdd(String... strings) {
        StringBuffer sbBuffer = new StringBuffer();
        for (int i = 0; i < strings.length; i++) {
            if (!TextUtil.checkString(strings[i]))
                sbBuffer.append(strings[i]);
        }
        return sbBuffer.toString();
    }

    /**
     * 获得当前格式化以后的时间
     *
     * @return
     */
    public static String getTime() {
        return getTimeBylong(System.currentTimeMillis());
    }

    public static String getTime(long time) {
        return getTimeBylong(time);
    }

    private static String getTimeBylong(long timelong) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = null;
        try {
            Timestamp now = new Timestamp(timelong);//获取系统当前时间
            String str = sdf.format(now);
            time = str.substring(11, 16);
            String month = str.substring(5, 7);
            String day = str.substring(8, 10);
            time = getDate(month, day) + time;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return time;
    }


    public static String getDate(String month, String day) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//24小时制
        java.util.Date d = new java.util.Date();
        ;
        String str = sdf.format(d);
//        String nowmonth = str.substring(5, 7);
        String nowday = str.substring(8, 10);
        String result = null;
        int temp = Integer.parseInt(nowday) - Integer.parseInt(day);
        switch (temp) {
            case 0:
                result = "今天";
                break;
            case 1:
                result = "昨天";
                break;
            case 2:
                result = "前天";
                break;
            default:
                StringBuilder sb = new StringBuilder();
                sb.append(Integer.parseInt(month) + "月");
                sb.append(Integer.parseInt(day) + "日");
                result = sb.toString();
                break;
        }
        return result;
    }

    /**
     * 获得渠道号
     *
     * @param context
     * @return
     */
    public static String getChannel(Context context) {
//        AnalyticsConfig.getChannel(context);
//        ApplicationInfo appInfo = null;
//        try {
//            appInfo = context.getPackageManager()
//                    .getApplicationInfo(context.getPackageName(),
//                            PackageManager.GET_META_DATA);
//            String msg = appInfo.metaData.getString("UMENG_CHANNEL");
//            return msg;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return AnalyticsConfig.getChannel(context);
    }

    //获取系统当前时间；
    public static String getDataNow() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        return df.format(new Date());
    }

    /**
     * 为get请求增加通用字段
     * source(来源 20 android 30 ios), sourceApp app来源:不传默认为天天象上 (0 网络教辅 1 天天象上 2 智能教辅 3 名师辅导 4
     *
     * @param url
     * @param context
     * @return
     */
    public static String urlAddVerandDevByget(String url, Context context) {
        return stringAddString(url,
                "&versionCode=", AppUtils.getVersionCode(context) + "",
                "&deviceId=", getDeviceId(context),
//                "&province=", UserManager.getInstance(context).getUser().getCity() + "",
                "&channel=" + getChannel(context),
                "&source=20&sourceApp=1",
                "&sysVersion=" + Build.VERSION.RELEASE,
                "&deviceName=" + getDeviceName() + "",
                "&token=" + "custom_token_1234567890");
//                "&token=" + UserManager.getInstance(context).getUser().getAuthorkey()).replace(" ", "");
    }

    /**
     * 为post请求增加通用字段
     * source(来源 20 android 30 ios), sourceApp app来源:不传默认为天天象上 (0 网络教辅 1 天天象上 2 智能教辅 3 名师辅导 4
     *
     * @param params
     * @param context
     * @return
     */
    public static RequestParams urlAddVerandDevBypost(RequestParams params, Context context) {
        if (params == null) {
            params = new RequestParams();
        }
//        if (!TextUtil.checkString(UserManager.getInstance(context).getUser().getAuthorkey())) {
//            params.addQueryStringParameter("token", UserManager.getInstance(context).getUser().getAuthorkey());
//        }
        params.addQueryStringParameter("versionCode", "" + AppUtils.getVersionCode(context));
        params.addQueryStringParameter("deviceId", getDeviceId(context));
        params.addQueryStringParameter("channel", getChannel(context));
//        params.addQueryStringParameter("province", UserManager.getInstance(context).getUser().getCity() + "");
        params.addQueryStringParameter("source", "20");
        params.addQueryStringParameter("sourceApp", "1");
        params.addQueryStringParameter("sysVersion", Build.VERSION.RELEASE);
        params.addQueryStringParameter("deviceName", getDeviceName());

        return params;
    }

    /**
     * byte(字节)根据长度转成kb(千字节)和mb(兆字节)
     *
     * @param bytes
     * @return
     */
    public static String bytes2kb(long bytes) {
        BigDecimal filesize = new BigDecimal(bytes);
        BigDecimal megabyte = new BigDecimal(1024 * 1024);
        float returnValue = filesize.divide(megabyte, 2, BigDecimal.ROUND_UP)
                .floatValue();
        if (returnValue > 1)
            return (returnValue + "MB");
        BigDecimal kilobyte = new BigDecimal(1024);
        returnValue = filesize.divide(kilobyte, 2, BigDecimal.ROUND_UP)
                .floatValue();
        return (returnValue + "KB");
    }

    /**
     * 友盟统计事件
     *
     * @param context
     * @param eventID
     */
    public static void setUMengEvent(Context context, String eventID) {
        if (context != null) {
            MobclickAgent.onEvent(context, eventID);
        }

    }

    /**
     * 获取本应用相关市场的intent
     *
     * @param paramContext
     * @return
     */
    public static Intent getIntent(Context paramContext, String packageName) {
        StringBuilder localStringBuilder = new StringBuilder().append("market://details?id=");
        if (TextUtils.isEmpty(packageName)) {
            String str = paramContext.getPackageName();
            localStringBuilder.append(str);
        } else {
            localStringBuilder.append(packageName);
        }
        Uri localUri = Uri.parse(localStringBuilder.toString());
        return new Intent("android.intent.action.VIEW", localUri);
    }

    /**
     * 增加跳转到应用市场或者浏览器的方法
     *
     * @param context
     * @param url
     */
    public static void downLoadApkFromBrowse(Context context, String url) {
        Uri uri = Uri.parse(url);
        Intent it = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(it);
    }

    /**
     * 判断是否可以跳转
     *
     * @param paramContext
     * @param paramIntent
     * @return
     */
    public static boolean judge(Context paramContext, Intent paramIntent) {
        List<ResolveInfo> localList = paramContext.getPackageManager().queryIntentActivities(paramIntent, PackageManager.GET_INTENT_FILTERS);
        if ((localList != null) && (localList.size() > 0)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 判断应用是否安装
     *
     * @param context
     * @param packagename
     * @return
     */
    public static boolean isAppInstalled(Context context, String packagename) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }
        if (packageInfo == null) {//没有安装
            return false;
        } else {//已经安装
            return true;
        }
    }

    /**
     * 根据包名启动app
     *
     * @param context
     * @param packageName
     */
    public static void startOtherApp(Activity context, String packageName) throws Exception {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (intent != null) {
            context.startActivity(intent);
        } else {
            UiUtils.showShortCustomToast(context, "无法启动该应用");
        }
    }

    /**
     * 开启一个定时任务；
     *
     * @param callBack
     * @time 时长；
     */
    public static void makeTimer(long time, final TimerCallBack callBack) {
        Timer mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                callBack.timerEnd();
            }
        }, time);
    }


    /**
     * 活取
     */
    public static String getDeviceName() {
        String deviceName = (android.os.Build.BRAND + "_" + android.os.Build.MODEL).replace(" ", "");
        try {
            deviceName = URLDecoder.decode(deviceName, "UTF-8");
        } catch (Exception e) {
        }
        return deviceName.replace(" ", "");

    }

    /**
     * 设置view的shape
     *
     * @param context
     * @param view
     * @param colorId
     */
    public static void setViewShape(Context context, View view, int colorId) {
        if (view != null) {
            GradientDrawable myGrad = (GradientDrawable) view.getBackground();
            if (myGrad != null) {
                myGrad.setColor(context.getApplicationContext().getResources().getColor(colorId));
            }
        }
    }

    /**
     * 设置view的stoken颜色
     *
     * @param view
     * @param colorId
     * @param width
     */
    public static void setViewShapeStokenColor(View view, int colorId, int width) {
        if (view != null) {
            GradientDrawable myGrad = (GradientDrawable) view.getBackground();
            if (myGrad != null) {
                myGrad.setStroke(width, colorId);
            }
        }
    }

    /*
    * 定时任务回调；
     */
    public interface TimerCallBack {
        public void timerEnd();
    }
}
