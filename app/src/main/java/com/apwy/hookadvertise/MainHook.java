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
            case "com.kugou.android":
                hookKuGou();
                break;
            case "ctrip.android.view":
                hookCtrip();
                break;
            case "com.qiyi.video":
                hookIQiYi();
                break;
            case "com.tencent.qqmusic":
                hookQQMusic();
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

    //酷狗音乐
    private void hookKuGou() {
        //冷启动广告
        findAndHookMethod("com.kugou.android.app.splash.SplashActivity", lpparam.classLoader, "x", new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam methodHookParam) {
                callMethod(methodHookParam.thisObject, "z");
                return null;
            }
        });
        findAndHookMethod("com.kugou.framework.musicfees.exemptionad.c", lpparam.classLoader, "a", XC_MethodReplacement.returnConstant(true));
        //热启动广告
        findAndHookMethod("com.kugou.android.app.splash.foresplash.b", lpparam.classLoader, "k", XC_MethodReplacement.returnConstant(true));
    }

    //携程旅行
    private void hookCtrip() {
        findAndHookMethod("ctrip.business.splash.CtripSplashActivity", lpparam.classLoader, "prepareSplashAd", new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) {
                callMethod(param.thisObject, "nextStep");
                return null;
            }
        });
    }

    //爱奇艺
    private void hookIQiYi() {
        findAndHookMethod("org.qiyi.android.video.MainActivity", lpparam.classLoader, "x9", XC_MethodReplacement.returnConstant(false));
    }

    //qq音乐
    private void hookQQMusic() {
        findAndHookMethod("com.tencent.qqmusic.business.splash.thirdpartsplash.i", lpparam.classLoader, "c", XC_MethodReplacement.returnConstant(null));
    }
}
