package com.github.jarofcolor.jsbridge;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;

class InnerJavaScriptInterface {
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private final WebViewJsBridge webViewJsBridge;

    public InnerJavaScriptInterface(WebViewJsBridge webViewJsBridge) {
        this.webViewJsBridge = webViewJsBridge;
    }

    /**
     * @param methodName 本地方法名
     * @param params     JavaScript调用本地方法时的传入参数
     * @see JsMethodHandler
     */
    @JavascriptInterface
    public String call(final String methodName, String callbackMethodName, final String params) {
        if (TextUtils.isEmpty(methodName)) return "";
        final JsMethodHandler handler = webViewJsBridge.getJsMethodHandler(methodName);
        if (handler == null)
            return "";
        handler.jsMethodName = callbackMethodName;
        if (handler.isSync()) {
            String res = handler.onJsCall(handler, methodName, params);
            return res != null ? res : "";
        }
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                handler.onJsCall(handler, methodName, params);
            }
        });
        return "";
    }
}
