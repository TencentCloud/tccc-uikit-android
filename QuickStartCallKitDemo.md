## 快速跑通腾讯云联络中心 Android Call Kit Demo

腾讯云联络中心提供了 Android Call Kit SDK，可以让座席实现拨打电话、手机等功能。也可以通过我们提供的 SDK 来实现在手机端、PC 端外呼、呼入来电接听等场景。该解决方案包含了UI界面，并且提供源码给开发者自行修改UI以及业务逻辑。

## 快速跑通腾讯云联络中心 Android Call Kit Demo
本文主要介绍如何快速跑通腾讯云联络中心 Call Kit Demo，只要按照如下步骤进行配置，就可以跑通基于腾讯云联络中心相关功能。

## 开发环境要求
- Android Studio 3.5+。
- Android 4.1（SDK API 16）及以上系统。

## 前提条件

- 您已 [注册腾讯云](https://cloud.tencent.com/document/product/378/17985) 账号，并完成 [实名认证](https://cloud.tencent.com/document/product/378/3629) 。
- 您已 [开通云联络中心](https://cloud.tencent.com/document/product/679/48028#.E6.AD.A5.E9.AA.A41.EF.BC.9A.E5.87.86.E5.A4.87.E5.B7.A5.E4.BD.9C) 服务，并创建了 [云联络中心实例](https://cloud.tencent.com/document/product/679/48028#.E6.AD.A5.E9.AA.A42.EF.BC.9A.E5.88.9B.E5.BB.BA.E4.BA.91.E5.91.BC.E5.8F.AB.E4.B8.AD.E5.BF.83.E5.AE.9E.E4.BE.8B) 。
- 您已购买了号码，[查看购买指南](https://cloud.tencent.com/document/product/679/73526)。并且完成了对应的[IVR配置](https://cloud.tencent.com/document/product/679/73549)

## 关键概念

1. **SdkAppId**：是用户在 [腾讯云联络中心控制台](https://console.cloud.tencent.com/ccc) 上创建的应用 ID，称之为 SdkAppId，一个腾讯云账号最多可以创建20个腾讯联络中心应用，通常为140开头。
   [](id:SdkAppId)


2. **UserID** ：座席或管理员在腾讯云联络中心内配置的账号，通常为邮箱格式，首次创建应用，主账号可前往 [站内信](https://console.cloud.tencent.com/message)（子账号需订阅云联络中心产品消息） 查看联络中心管理员账号和密码。一个 SDKAppID 下可以配置多个 UserID，如果超出配置数量限制，需到 [座席购买页](https://buy.cloud.tencent.com/ccc_seat) 购买更多座席数量。
   [](id:UserID)


3. **SecretId 和 SecretKey**：开发者调用云 API 所需凭证，通过 [腾讯云控制台](https://console.cloud.tencent.com/cam/capi) 创建。
   [](id:SecretId)


4. **token**: 登录票据，需要调用云API接口[**CreateSDKLoginToken**](https://cloud.tencent.com/document/api/679/49227)来获取。正确的做法是将 Token 的计算代码和加密密钥放在您的业务服务器上，然后由 App 按需向您的服务器获取实时算出的 Token。
   [](id:token)



## 操作步骤

### 步骤1：下载 tccc-uikit-android 源码
根据实际业务需求 [tccc-uikit-android](https://github.com/TencentCloud/tccc-uikit-android) 源码。

[](id:step2)
### 步骤2：配置 tccc-uikit-android 工程文件
1. 找到并打开 debug/src/main/java/com/tencent/qcloud/debug/GenerateTestUserToken.java 文件。

2. 设置 GenerateTestUserToken.java 文件中的相关参数：
<ul>
  <li/>USERID：座席账号，格式为 ： xxx@qq.com
  <li/>SDKAPPID：腾讯云联络中心 SDKAppId，需要替换为您自己账号下的 SDKAppId
	<li/>SECRETID：计算签名用的加密密钥ID。
  <li/>SECRETKEY：计算签名用的加密密钥Key。
</ul>


![](https://tccc.qcloud.com/assets/doc/Agent/ios_image/uicallkit_demo.png)


> ! 请不要将如下代码发布到您的线上正式版本的 App 中，原因如下：
- 本文件中的代码虽然能够正确计算出 Token，但仅适合快速调通 SDK 的基本功能，**不适合线上产品**，这是因为客户端代码中的 SECRETKEY 很容易被反编译逆向破解，尤其是 Web 端的代码被破解的难度几乎为零。一旦您的密钥泄露，攻击者就可以计算出正确的 Token 来盗用您的腾讯云流量。
- 正确的做法是将 Token 的计算代码和加密密钥放在您的业务服务器上，然后由 App 按需向您的服务器获取实时算出的 Token。由于破解服务器的成本要高于破解客户端 App，所以服务器计算的方案能够更好地保护您的加密密钥。更多详情请参见[创建 SDK 登录 Token](https://cloud.tencent.com/document/product/679/49227)


### 步骤3：编译运行
使用 Android Studio（3.5及以上的版本）打开源码工程 `tccc-uikit-android`，单击**运行**即可。

1. 点击登录，
2. 登录成功后输入需要拨打的手机号即可完成拨打功能。


### 运行效果

基本功能如下图所示

| 外呼效果 | 呼入效果 |
|-----|------|
|![](https://tccc.qcloud.com/assets/doc/Agent/ios_image/callout.png)| ![](https://tccc.qcloud.com/assets/doc/Agent/ios_image/callIn.png) |   

## 交流与反馈
   
   如果您在使用过程中，有什么建议或者意见，可以在这里反馈。点此进入 [TCCC 社群](https://zhiliao.qq.com/)，享有专业工程师的支持，解决您的难题。

