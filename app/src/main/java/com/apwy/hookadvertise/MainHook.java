package com.apwy.hookadvertise;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MainHook implements IXposedHookLoadPackage {

    private XC_LoadPackage.LoadPackageParam lpparam = null;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        this.lpparam = lpparam;
        switch (lpparam.packageName) {
            case "com.baidu.netdisk":
                hookBaiduDisk();
                break;
            case "com.autonavi.minimap":
                hookMiniMap();
                break;
        }
    }

    //百度网盘
    private void hookBaiduDisk() throws Throwable {
        Class<?> aClass = lpparam.classLoader.loadClass("com.baidu.netdisk.ui.advertise.FlashAdvertiseFragment");
        XposedBridge.hookAllMethods(aClass, "onCreateView", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Object activity = XposedHelpers.callMethod(param.thisObject, "getActivity");
                XposedHelpers.callMethod(param.thisObject, "startMainActivity", activity);
            }
        });
    }

    //高德地图
    private void hookMiniMap() {
        findAndHookMethod("com.autonavi.minimap.SplashViewManager$b", lpparam.classLoader, "onResult", String.class, new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam methodHookParam) {
                callMethod(methodHookParam.thisObject, "onError");
                return null;
            }
        });
    }
}
