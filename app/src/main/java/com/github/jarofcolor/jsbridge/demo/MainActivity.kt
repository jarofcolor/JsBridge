package com.github.jarofcolor.jsbridge.demo

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.github.jarofcolor.jsbridge.JsMethodHandler
import com.github.jarofcolor.jsbridge.WebViewJsBridge
import com.github.jarofcolor.jsbridge.demo.databinding.ActivityMainBinding
import org.json.JSONObject


class MainActivity : AppCompatActivity() {
    private lateinit var jsBridge: WebViewJsBridge
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.webView.webChromeClient = WebChromeClient()
        binding.webView.webViewClient = WebViewClient()

        //注册一个js对象，挂载在window上
        jsBridge = WebViewJsBridge.create(binding.webView, "bridge")

        //注册登录处理器,methodName不为空时，默认异步，否则同步
        val loginJsHandler = object : JsMethodHandler("login") {
            override fun onJsCall(
                handler: JsMethodHandler?,
                methodName: String?,
                params: String?
            ): String {
                Toast.makeText(this@MainActivity, "收到来自网页的数据：$params", Toast.LENGTH_SHORT).show()
                val data = "登录成功！token=123456"
                //这里异步同步同时返回结果，但只有一种方法能接收到，取决于methodName是否为空串
                handler?.callback(data)
                return data
            }
        }


        val payJsHandler = object : JsMethodHandler("pay") {
            override fun onJsCall(
                handler: JsMethodHandler?,
                methodName: String?,
                params: String?
            ): String {
                Toast.makeText(this@MainActivity, "收到来自网页的数据：$params", Toast.LENGTH_SHORT).show()
                //模拟一个异步操作
                goPay(handler!!)
                return ""
            }
        }

        val handlers = arrayOf(loginJsHandler, payJsHandler)

        handlers.forEach { jsBridge.registerJsMethodHandler(it) }

        lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    handlers.forEach { jsBridge.unregisterJsMethodHandler(it) }
                }
            }
        })

        binding.webView.loadUrl("file:///android_asset/index.html")
    }

    private fun goPay(handler: JsMethodHandler) {
        Handler(Looper.getMainLooper()).postDelayed({
            val json = JSONObject()
            json.put("orderId", "123456789")
            json.put("result", true)
            //这里异步同步同时返回结果，但只有一种方法能接收到，取决于methodName是否为空串
            handler.callback(json.toString())
        }, 2000)
    }
}