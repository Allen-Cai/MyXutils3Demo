# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/wyouflf/develop/android-sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

#忽略警告
-ignorewarning

#umeng统计混淆
-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

################### region for xUtils
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,*Annotation*,Synthetic,EnclosingMethod

-keep public class org.xutils.** {
    public protected *;
}
-keep public interface org.xutils.** {
    public protected *;
}
-keepclassmembers class * extends org.xutils.** {
    public protected *;
}
-keepclassmembers class * extends org.xutils.http.RequestParams {*;}
-keepclassmembers class * {
   void *(android.view.View);
   *** *Click(...);
   *** *Event(...);
}

#百度地图sdk
-keep class com.baidu.** {*;}
-keep class vi.com.** {*;}
-dontwarn com.baidu.**



#保持 JavaScripct 方法不被混淆
-keepattributes *JavascriptInterface*

#本地javaScript
-keep class com.yhcdhp.cai.daydays.fragment.ChannelFragment$PlayViewInterface{*;}

#保持 fastjson
-keep class com.alibaba.fastjson.** { *; }

-keep class com.yhcdhp.cai.daydays.entity.CityEntity{*;}

#################### end region