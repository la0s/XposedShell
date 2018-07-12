package com.example.la0s.xposedshell;

import android.content.pm.ApplicationInfo;

import com.example.la0s.xposedshell.extend.Timing;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
/*
    注意！
	你需要关掉Instant Run才能在Android Studio里使用“运行App”，不然Xposed会出现找不到类的错误。
	Be Awared!
	You should disable Instant Run if you want to use 'Run App' from Android Studio, or Xposed Framework will not find module class from base.apk.
	https://developer.android.com/studio/run/#disable-ir
*/

public class MyXposed implements IXposedHookLoadPackage {

    String targetApp = "cn.thecover.www.covermedia";
    String packageName;
    Boolean isFirstApplication;
    ClassLoader classLoader;
    String processName;
    ApplicationInfo appInfo;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if (!loadPackageParam.packageName.equals(targetApp)) return;
        gatherInfo(loadPackageParam);
        //Write your code here.
        new Timing(loadPackageParam,true){
            @Override
            protected void onNewActivity(XC_MethodHook.MethodHookParam param) {
                super.onNewActivity(param);
                try {
                    Class clz = Class.forName("cn.thecover.www.covermedia.data.entity.HttpRequestEntity", false, classLoader);
                    new MyXposedHook().hook(clz, "getSign");
                }catch (Exception e){
                    XposedBridge.log(e.getLocalizedMessage());
                }
            }
        };

    }

    private void gatherInfo(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        packageName = loadPackageParam.packageName;
        isFirstApplication = loadPackageParam.isFirstApplication;
        classLoader = loadPackageParam.classLoader;
        processName = loadPackageParam.processName;
        appInfo = loadPackageParam.appInfo;
    }
}
