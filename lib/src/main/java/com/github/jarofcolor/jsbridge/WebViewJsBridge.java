package com.github.jarofcolor.jsbridge;

import android.annotation.SuppressLint;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.util.HashMap;

public class WebViewJsBridge {
    private final HashMap<String, JsMethodHandler> jsMethodHandles = new HashMap<>();
    private final WebView webView;

    private WebViewJsBridge(WebView webView) {
        this.webView = webView;
    }


    /**
     * @param webView ...
     * @param name    挂载在window上的对象名称
     * @return WebViewJsBridge
     */
    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    public static WebViewJsBridge create(WebView webView, String name) {
        WebSettings webSetting = webView.getSettings();
        webSetting.setJavaScriptEnabled(true);

        WebViewJsBridge bridge = new WebViewJsBridge(webView);
        webView.addJavascriptInterface(new InnerJavaScriptInterface(bridge), name);
        return bridge;
    }


    public synchronized void registerJsMethodHandler(JsMethodHandler handler) {
        if (!jsMethodHandles.containsKey(handler.nativeMethodName)) {
            jsMethodHandles.put(handler.nativeMethodName, handler);
            handler.webView = webView;
        }
    }

    public synchronized void unregisterJsMethodHandler(JsMethodHandler handler) {
        jsMethodHandles.remove(handler.nativeMethodName);
    }

    synchronized JsMethodHandler getJsMethodHandler(String nativeMethodName) {
        return jsMethodHandles.get(nativeMethodName);
    }
}
