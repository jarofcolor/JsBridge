# 简介

一个Android网页交互工具库。

## 如何使用

增加

``` gradle
 repositories {
    maven { url "https://jitpack.io" }
 }
```

引入

``` gradle
implementation 'com.github.jarofcolor:JsBridge:0.0.1'
```

## 使用

Android端

``` kotlin
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
            //异步通知
            handler.callback(json.toString())
        }, 2000)
    }
}
```

html端

``` html
<!DOCTYPE html>
<html lang="">

<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width,initial-scale=1.0">
    <title>交互测试</title>
</head>

<script type="text/javascript">

  window.clientcall = {}

  //异步消息回调
  window.clientcall.pay = function (res) {
    alert("来自客户端返回的异步数据：" + res)
  }

  //同步消息
  function login() {
    var params = document.getElementById("input1").value;
    var res = window.bridge.call("login", "", params)
    alert("来自客户端返回的数据：" + res)
  }

  //异步消息
  function pay() {
    var params = document.getElementById("input2").value;
    //传入回调函数。就异步处理
    var res = window.bridge.call("pay", "window.clientcall.pay", params)
  }
</script>

<body>
<div>

    <div>
        <input placeholder="输入数据" id="input1" /><button onclick="login()">发送消息，同步接收数据</button>
    </div>
    <div>
        <input placeholder="输入数据" id="input2"/><button onclick="pay()">发送消息，异步接收数据</button>
    </div>
</div>
</body>

</html>
```