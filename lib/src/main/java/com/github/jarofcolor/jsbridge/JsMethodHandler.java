package com.github.jarofcolor.jsbridge;

import android.os.Build;
import android.text.TextUtils;
import android.webkit.ValueCallback;
import android.webkit.WebView;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class JsMethodHandler {
    WebView webView;
    String jsMethodName;
    String nativeMethodName;

    public JsMethodHandler(String nativeMethodName) {
        this.nativeMethodName = nativeMethodName;
    }

    protected abstract String onJsCall(JsMethodHandler handler, String methodName, String params);

    public void callback(String params) {
        if (TextUtils.isEmpty(jsMethodName))
            return;

        String script = "javascript:" + jsMethodName + "('" + params + "')";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.evaluateJavascript(script, new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String s) {
                    //ignored
                }
            });
        } else {
            webView.loadUrl(script);
        }
    }

    protected boolean isSync() {
        return TextUtils.isEmpty(jsMethodName);
    }
}
