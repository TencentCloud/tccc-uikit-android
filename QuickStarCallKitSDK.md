本文将介绍如何用最短的时间完成 TCCCCallKit 组件的接入，您将在十分钟内完成如下几个关键步骤，并最终得到一个包含完备 UI 界面的电话外呼、接听功能。

## 环境准备
- Android Studio 3.5+。
- Android 4.1（SDK API 16）及以上系统。

## 前提条件
- 您已 [注册腾讯云](https://cloud.tencent.com/document/product/378/17985) 账号，并完成 [实名认证](https://cloud.tencent.com/document/product/378/3629) 。
- 您已 [开通云联络中心](https://cloud.tencent.com/document/product/679/48028#.E6.AD.A5.E9.AA.A41.EF.BC.9A.E5.87.86.E5.A4.87.E5.B7.A5.E4.BD.9C) 服务，并创建了 [云联络中心实例](https://cloud.tencent.com/document/product/679/48028#.E6.AD.A5.E9.AA.A42.EF.BC.9A.E5.88.9B.E5.BB.BA.E4.BA.91.E5.91.BC.E5.8F.AB.E4.B8.AD.E5.BF.83.E5.AE.9E.E4.BE.8B) 。
- 您已购买了号码，[查看购买指南](https://cloud.tencent.com/document/product/679/73526)。并且完成了对应的[IVR配置](https://cloud.tencent.com/document/product/679/73549)

[](id:step1)
## 步骤一：下载并导入组件

在 [Github](https://github.com/TencentCloud/tccc-uikit-android) 中克隆/下载代码，然后拷贝该库目录下的 TCCCCallKit 子目录到您当前工程中的 app 同一级目录中，如下图：

![](https://tccc.qcloud.com/assets/doc/Agent/ios_image/GitCallKitDemo.png)

[](id:step2)
## 步骤二：完成工程配置
1. 在工程根目录下找到 `settings.gradle` 文件，并在其中增加如下代码，它的作用是将 [步骤一](id:step1) 中下载的 TCCCCallKit 组件导入到您当前的项目中：

``` java
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // 需要引入TCCCCallKit目录下的aar文件
        flatDir {
            dirs 'TCCCCallKit/libs'
        }
    }
}

include ':TCCCCallKit'
```
2. 在 app 目录下找到 `build.gradle` 文件，并在其中增加如下代码，它的作用是声明当前 app 对新加入的 TCCCCallKit 组件的依赖：

``` java
api project(':TCCCCallKit')
```

3. 由于我们在 SDK 内部使用了Java 的反射特性，需要将 SDK 中的部分类加入不混淆名单，因此需要您在 `proguard-rules.pro` 文件中添加如下代码：

``` bash
-keep class com.tencent.** { *; }
```

> **注意**
> 

> TCCCCallKit 会在内部帮助您动态申请麦克风、蓝牙、读取存储权限等，如果因为您的业务问题需要删减，可以请修改`TCCCCallKit/src/main/AndroidManifest.xml`。
> 

[](id:step3)
## 步骤三：登录 TCCC

在您的项目中添加如下代码，它的作用是通过调用 TCCCCallKit 中的相关接口完成 TCCCCallKit 组件的登录。这个步骤异常关键，因为只有在登录成功后才能正常使用 TCCCCallKit 的各项功能，故请您耐心检查相关参数是否配置正确：

``` java
// 登录
TCCCCallKit.createInstance(getApplicationContext()).login(
    "denny@qq.com", // 请替换为您的 UserID，通常是一个邮箱地址
    1400000001,     // 请替换为步骤一取到的 SDKAppID
    "xxxxxxxxxxx",  // 您可以在你的后台计算一个 token 并填在这个位置
    new TUICommonDefine.Callback() {
    @Override
    public void onSuccess() {
        Log.i(TAG, "login success");
    }

    @Override
    public void onError(int errorCode, String errorMessage) {
        Log.e(TAG, "login failed, errorCode: " + errorCode + " msg:" + errorMessage);
    }
});

// 退出登录
TCCCCallKit.createInstance(getApplicationContext()).logout(
    new TUICommonDefine.Callback() {
    @Override
    public void onSuccess() {
        Log.i(TAG, "logout success");
    }

    @Override
    public void onError(int errorCode, String errorMessage) {
        Log.e(TAG, "logout failed, errorCode: " + errorCode + " msg:" + errorMessage);
    }
});
```

## 步骤四：拨打电话

通过调用 TCCCCallKit 的 call 函数并指定需要拨打的电话号码，就可以发起外呼。

``` java
TCCCCallKit.createInstance(getApplicationContext()).call(
    "13430689561", // 需要呼叫的电话号码,
    "134*****561", // 在通话条中会替代号码显示,填写为null或者为空字符串将显示电话号码
    null,         // 号码备注，在通话记录中将会保存显示该备注
    new TUICommonDefine.Callback() {
    @Override
    public void onSuccess() {
        Log.i(TAG, "call success");
    }

    @Override
    public void onError(int errorCode, String errorMessage) {
        Log.e(TAG, "call failed, errorCode: " + errorCode + " msg:" + errorMessage);
    }
);
```

## 步骤五：接听来电

收到来电请求后，TCCCCallKit 组件会自动唤起来电提醒的接听界面，不过因为 Android 系统权限的原因，分为如下几种情况：
- 您的 App 在前台时，当收到邀请时会自动弹出呼叫界面并播放来电铃音。
- 您的 App 在后台，但是有授予`悬浮窗权限`或`后台弹出应用`等权限时，仍然会自动弹出呼叫界面并播放来电铃音。
- 您的 App 在后台，且没有授予`悬浮窗权限`或`后台弹出应用`等权限时，TCCCCallKit 会播放来电铃音，提示用户接听或挂断。
- 您的 App 进程已经被销毁或者冻结了，可以通过开通手机接听来处理接听来电。

    ![](https://tccc.qcloud.com/assets/doc/Agent/ios_image/callInByPhone.png)

## 步骤六：更多特性

### 一、悬浮窗功能

如果您的业务需要开启悬浮窗功能，您可以在 TCCCCallKit 组件初始化时调用以下接口开启该功能：

``` java
TCCCCallKit.createInstance(getApplicationContext()).enableFloatWindow(true);
```

### 二、通话状态监听

如果您的业务需要 **监听通话的状态**，例如通话开始、结束，以及通话过程中的网络质量等等（详见[CallStatusListener](https://github.com/TencentCloud/tccc-uikit-android/blob/main/TCCCCallKit/src/main/java/com/tencent/qcloud/tccccallkit/base/TUICommonDefine.java)），可以监听以下事件：

``` java
TCCCCallKit.createInstance(getApplicationContext()).setCallStatusListener(new TUICommonDefine.CallStatusListener() {
    @Override
    public void onNewSession(TUICommonDefine.SessionInfo info) {
        // 通话开始，包括呼入和呼出
    }

    @Override
    public void onEnded(TUICommonDefine.EndedReason reason, String reasonMessage, String sessionId) {
        // 通话结束
    }

    @Override
    public void onAccepted(String sessionId) {
        // 对端已接听
    }

    @Override
    public void onAudioVolume(TUICommonDefine.VolumeInfo volumeInfo) {
        // 音量大小变化
    }

    @Override
    public void onNetworkQuality(TUICommonDefine.QualityInfo localQuality, TUICommonDefine.QualityInfo remoteQuality) {
        // 网络情况变化
    }
});
```

### 三、其他功能说明

- 如果您的业务需要 **用户状态监听**，可以监听以下事件：

``` java
TCCCCallKit.createInstance(getApplicationContext()).setUserStatusListener(new TUICommonDefine.UserStatusListener() {
    @Override
    public void onKickedOffline() {
        // 被T，用户可能在其他地方登录了。
    }
});
```

- 实时判断坐席是否登录

``` java
TCCCCallKit.createInstance(getApplicationContext()).isUserLogin(new TUICommonDefine.Callback() {
    @Override
    public void onSuccess() {
        // 已登录
    }

    @Override
    public void onError(int errCode, String errMsg) {
        // 未登录，或者被T了。
    }
});
```

- 默认自带 简体中文、英语 语言包，作为界面展示语言。组件内部语言会跟随系统语言,无需额外步骤。如需要实时动态修改语言可参考

``` java
// 实时变更为英文
TUIThemeManager.getInstance().changeLanguage(getApplicationContext(),TUIThemeManager.LANGUAGE_EN);
```

### 四、自定义铃音

如果您需要自定义来电铃音，可以通过如下接口进行设置：

``` java
TCCCCallKit.createInstance(getApplicationContext()).setCallingBell(filePath);
```


## 交流与反馈
   
   如果您在使用过程中，有什么建议或者意见，可以在这里反馈。点此进入 [TCCC 社群](https://zhiliao.qq.com/)，享有专业工程师的支持，解决您的难题。
