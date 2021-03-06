package com.js.nowakelock.xposedhook

import com.js.nowakelock.BuildConfig
import com.js.nowakelock.xposedhook.hook.AlarmHook
import com.js.nowakelock.xposedhook.hook.ServiceHook
import com.js.nowakelock.xposedhook.hook.WakelockHook
import de.robv.android.xposed.*
import de.robv.android.xposed.IXposedHookZygoteInit.StartupParam
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam


open class XposedModule : IXposedHookZygoteInit, IXposedHookLoadPackage {

    override fun initZygote(startupParam: StartupParam?) {
        XposedBridge.log(": initZygote")
    }

    @Throws(Throwable::class)
    override fun handleLoadPackage(lpparam: LoadPackageParam) {
//        val pN = lpparam.packageName
//        XposedBridge.log("$TAG $pN: handleLoadPackage ,mypid ${Process.myUid()}")

        if (lpparam.packageName == "android") {
            AlarmHook.hookAlarm(lpparam)
            ServiceHook.hookService(lpparam)
            WakelockHook.hookWakeLocks(lpparam)
        } else if (lpparam.packageName == BuildConfig.APPLICATION_ID) {

            XposedHelpers.findAndHookMethod(
                "${BuildConfig.APPLICATION_ID}.ui.mainActivity.MainActivity", lpparam.classLoader,
                "isModuleActive", XC_MethodReplacement.returnConstant(true)
            )
        }
    }
}
