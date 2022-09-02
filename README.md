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

### 1、创建一个WebViewJsBridge

``` kotlin
val jsBridge = WebViewJsBridge.create(webView, "bridge")
```

### 2、注册一个JS方法处理器

``` kotlin
val loginJsHandler = object : JsMethodHandler("login") {
    override fun onJsCall(
        handler: JsMethodHandler?,
        methodName: String?,
        params: String?
    ): String {
        return ""
    }
}

jsBridge.registerJsMethodHandler(loginJsHandler)       
```

### 3、异步回调结果给网页端

除了立即返回，可以在任何地方通过handler回调

``` kotlin
 loginJsHandler.callback(data)
```

html端

### 1、同步请求

``` javascript
//创建一个同步请求，直接返回结果
var res = window.bridge.call("login", "", params)
```

### 2、异步请求

```  javascript
window.call = {}
//定义回调函数，注意不要用内部函数，否则从客户端可能无法找到函数
window.call.onLoginResult = function(data){console.log(data)}

//创建一个异步请求，只要传入回调函数就是异步回调，结果通过该函数回调
window.bridge.call("login", "window.call.onLoginResult", params)
```

demo中有完整示例可以参阅。
