# 加密

## 基座集成

1.将android包下的aar复制到项目下app/lib下，

2.添加依赖工程

```java
implementation files('libs/api-security-1.0.0.aar')
```
3.在assets/data/config.xml文件中，添加security插件配置

```java
    <feature name="Security">
        <param name="android-package" value="cn.com.agree.abc.sdk.api.security.SecurityImpl" />
    </feature>
```
## 前端集成

在前端工程的lib目录下，添加AgreeSDK.Security.js文件，并在main.js中import该文件

```javascript
import "../lib/AgreeSDK.Security";
```
## API

 ### AgreeSDK.secure.encrypt（object）加密

| 属性     | 类型     | 默认值 | 必填 | 说明                                             |
| -------- | -------- | ------ | ---- | ------------------------------------------------ |
| success  | Function |        | 否   | 接口调用成功的回调函数                           |
| error    | Function |        | 否   | 接口调用失败的回调函数                           |
| complete | Function |        | 否   | 接口调用结束的回调函数（调用成功、失败都会执行） |

 ***params***

| 属性 | 必填 | 属性   | 说明                                                         |
| ---- | ---- | ------ | ------------------------------------------------------------ |
| data | 是   | String | 要加密的数据                                                 |
| key  | 否   | String | 密钥（如未上传将随机生成密钥，用于对称加密,非对称加密不需要上传） |
| type | 是   | String | 加密类型                                                     |

 ***示例代码***

```javascript
AgreeSDK.security.encrypt({
    data: "",
    key: "",
    type: "SHA1",
    success: (data) => {
        this.outData = data;
    },
    fail: (e) => {
        this.outData = "加密失败：" + e + "\n";
    },
    complete() {},
});

```
 ***回调***

```javascript
{
	encryptData:HGIG21G87987IBU243HBHU,
    key:12345678,
	privateKeyHex:"",
    publicKeyHex:""
}
```

encryptData:加密后生成的加密数据

key：对称加密时，回传的密钥，当不传入密钥时将自动生成密钥

privateKeyHex：非对称加密时，生成的私钥

publicKeyHex：非对称加密时，生成的公钥



 ### AgreeSDK.secure.decrypt（object）解密

| 属性     | 类型     | 默认值 | 必填 | 说明                                             |
| -------- | -------- | ------ | ---- | ------------------------------------------------ |
| success  | Function |        | 否   | 接口调用成功的回调函数                           |
| error    | Function |        | 否   | 接口调用失败的回调函数                           |
| complete | Function |        | 否   | 接口调用结束的回调函数（调用成功、失败都会执行） |

 ***params***

| 属性 | 必填 | 属性   | 说明                                     |
| ---- | ---- | ------ | ---------------------------------------- |
| data | 是   | String | 要加密的数据                             |
| key  | 否   | String | 密钥（当为非对称加加密时传入公钥或私钥） |
| type | 是   | String | 加密类型解密类型                         |

 ***示例代码***

```javascript
AgreeSDK.security.decrypt({
    data: "",
    key: "",
    type: "SHA1",
    success: (data) => {
        this.outData = data;
    },
    fail: (e) => {
        this.outData = "解密失败：" + e + "\n";
    },
    complete() {},
});

```
### AgreeSDK.security.getRandomKey（object）获取随机密钥

 ***示例代码***

```javascript
AgreeSDK.security.decrypt({
    length: 8,
    success: (data) => {
        this.outData = data;
    },
    fail: (e) => {
        this.outData = e
    },
    complete() {},
});

```
length：所需获取的密钥长度


## **type**

| 类型 | 加密类型   | 加密方式 | 解密方式 | 说明     |
| ---- | ---------- | -------- | -------- | -------- |
| SM2  | 非对称加密 | 公钥加密 | 私钥解密 |          |
| SM3  | 单向加密   | 密钥加密 |          |          |
| SM4  | 对称加密   | 密钥加密 | 密钥解密 | 16位密钥 |
| DES  | 对称加密   | 密钥加密 | 密钥解密 | 不小于8位密钥  |
| DES3 | 对称加密   | 密钥加密 | 密钥解密 |          |
| AES  | 对称加密   | 密钥加密 | 密钥加密 |          |
| MD5  | 单向加密   | 密钥加密 |          |          |
| RSA  | 非对称加密 | 公钥加密 | 私钥解   |          |
| DSA  | 验证性加密 | 私钥加密 | 公钥验证 |          |

**DSA**：加密时生成的加密数据作为**数字签名**，不能解密只能作为数据验证使用，验证时传入加密前数据、数字签名以及公钥进行验证

