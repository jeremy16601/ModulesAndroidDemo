package com.appinfo.lixueqing.myapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import com.uzmap.pkg.uzcore.UZWebView;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 该类映射至Javascript中moduleDemo对象<br><br>
 * <strong>Js Example:</strong><br>
 * var module = api.require('moduleDemo');<br>
 * module.xxx();
 *
 * @author APICloud
 */
public class PackagesModule extends UZModule {

    static final int ACTIVITY_REQUEST_CODE_A = 100;

    private AlertDialog.Builder mAlert;
    private Vibrator mVibrator;
    private UZModuleContext mJsCallback;

    public PackagesModule(UZWebView webView) {
        super(webView);
    }

    /**
     * <strong>函数</strong><br><br>
     * 该函数映射至Javascript中moduleDemo对象的showAlert函数<br><br>
     * <strong>JS Example：</strong><br>
     * moduleDemo.showAlert(argument);
     *
     * @param moduleContext (Required)
     */
    public void jsmethod_getAppList(final UZModuleContext moduleContext) {
//        if (null != mAlert) {
//            return;
//        }
//        String showMsg = moduleContext.optString("msg");
//        mAlert = new AlertDialog.Builder(mContext);
//        mAlert.setTitle("test");
//        mAlert.setMessage(showMsg);
//        mAlert.setCancelable(false);
//        mAlert.setPositiveButton("确定", new OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//                mAlert = null;
        JSONObject ret = new JSONObject();
        try {
            ret.put("packages", getAllApps(moduleContext.getContext()));
            Log.e("tres", getAllApps(moduleContext.getContext()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        moduleContext.success(ret, true);
//            }
//        });
//        mAlert.show();
    }

    /**
     * 查询手机内非系统应用--包名
     *
     * @param context
     * @return
     */
    public static String getAllApps(Context context) {
        List<String> packages = new ArrayList<>();
        PackageManager pManager = context.getPackageManager();
        List<PackageInfo> paklist = pManager.getInstalledPackages(0);
        JSONArray jsonArray = new JSONArray();
        JSONObject object = new JSONObject();
        for (int i = 0; i < paklist.size(); i++) {
            PackageInfo pak = paklist.get(i);
            if ((pak.applicationInfo.flags & pak.applicationInfo.FLAG_SYSTEM) <= 0) {
                packages.add(pak.packageName);
            }
        }
        for (String s : packages) {
            jsonArray.put(s);
        }
//        try {
//            object.put("packagelist",jsonArray);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        return jsonArray.toString();
    }


    /**
     * <strong>函数</strong><br><br>
     * 该函数映射至Javascript中moduleDemo对象的vibrate函数<br><br>
     * <strong>JS Example：</strong><br>
     * moduleDemo.vibrate(argument);
     *
     * @param moduleContext (Required)
     */
    public void jsmethod_vibrate(UZModuleContext moduleContext) {
        try {
            if (null == mVibrator) {
                mVibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
            }
            mVibrator.vibrate(moduleContext.optLong("milliseconds"));
        } catch (SecurityException e) {
            Toast.makeText(mContext, "no vibrate permisson declare", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * <strong>函数</strong><br><br>
     * 该函数映射至Javascript中moduleDemo对象的stopVibrate函数<br><br>
     * <strong>JS Example：</strong><br>
     * moduleDemo.stopVibrate(argument);
     *
     * @param moduleContext (Required)
     */
    public void jsmethod_stopVibrate(UZModuleContext moduleContext) {
        if (null != mVibrator) {
            try {
                mVibrator.cancel();
                mVibrator = null;
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == ACTIVITY_REQUEST_CODE_A) {
            String result = data.getStringExtra("result");
            if (null != result && null != mJsCallback) {
                try {
                    JSONObject ret = new JSONObject(result);
                    mJsCallback.success(ret, true);
                    mJsCallback = null;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onClean() {
        if (null != mAlert) {
            mAlert = null;
        }
        if (null != mJsCallback) {
            mJsCallback = null;
        }
    }

}
