    
    //文件加密
    @JavaScriptApi
    public void encryptFile(AgreeSDKWebView webView, JSONArray array, CallbackContext callbackContext) {
        //只需传递本地文件路径  /storage/emulated/0/DCIM/1615969362315.jpg
       try {
            String inputResult = array.getJSONObject(0).getString("inputData");
            AbstractCoder cipher = SecurityManager.getCipher(SecurityManager.Model.SM3);
            String encryResult = cipher.digestSignature(inputResult);
            callbackContext.success(encryResult);
        } catch (Exception e) {
            e.printStackTrace();
            callbackContext.error("加密失败：" + e);
        }
    }
    
    
    ```java
    //加密用法
    @JavaScriptApi
    public void encrypt(AgreeSDKWebView webView, JSONArray array, CallbackContext callbackContext) {
        try {
            JSONObject res = array.getJSONObject(0);
            //获取输入数据,以及密钥,和加密类型
            String inputResult = res.getString("data");
            String keyData = res.getString("key");
            String type = res.getString("type");
            Log.e("encrypt", res.toString());
            if (TextUtils.isEmpty(inputResult)) {
                callbackContext.error("加密数据为空");
                return;
            }

            //根据不同类型的加密方式，判定当密钥为空或者不合法时的修改办法
            if (type.equals("SM4") && (keyData.isEmpty() || keyData.length() < 16)) {
                keyData = UUIDUtils.genRandomNum();
            } else if (keyData.isEmpty() && type.equals("DSA")) {
                keyData = UUIDUtils.genRandomNum(32);
            } else if (keyData.isEmpty() && !type.equals("RSA") && !type.equals("SM2")) {
                keyData = UUIDUtils.getUUID();
            }
            AbstractCoder abstractCoder;
            String encryResult = "";
            String privateKeyHex;
            String publicKeyHex;
            JSONObject jsonObject = new JSONObject();
            switch (type) {
                case "SM2":
                    //公钥加密，非对称加密
                    abstractCoder = SecurityManager.getCipher(SecurityManager.Model.SM2);
                    SM2KeyHelper.KeyPair keyPair = SM2KeyHelper.generateKeyPair((Sm2Kit) abstractCoder);
                    privateKeyHex = keyPair.getPrivateKey();
                    publicKeyHex = keyPair.getPublicKey();
                    encryResult = abstractCoder.simpleEnCode(inputResult, publicKeyHex);

                    jsonObject.put("privateKeyHex", privateKeyHex);
                    jsonObject.put("publicKeyHex", publicKeyHex);
                    break;
                case "SM3":
                    //无法解码，单向加密
                    abstractCoder = SecurityManager.getCipher(SecurityManager.Model.SM3);
                    encryResult = abstractCoder.digestSignature(inputResult, keyData);
                    SecurityManager.reStoreCipher();
                    break;
                case "SM4":
                    abstractCoder = SecurityManager.getCipher(SecurityManager.Model.SM4);
                    String key = Utils.byteToHex(keyData.getBytes());
                    encryResult = abstractCoder.simpleEnCode(inputResult, key);
                    break;
                case "DES":
                    abstractCoder = SecurityManager.getCipher(SecurityManager.Model.DES);
                    encryResult = abstractCoder.simpleEnCode(inputResult, keyData);
                    break;
                case "DES3":
                    abstractCoder = SecurityManager.getCipher(SecurityManager.Model.TRIDES);
                    encryResult = Utils.byteToHex(abstractCoder.enCode(inputResult.getBytes(), keyData.getBytes()));
                    break;
                case "AES":
                    encryResult = AESUtils.des(inputResult, keyData, Cipher.ENCRYPT_MODE);
                    break;
                case "MD5":
                    //不能解码，单向加密
                    abstractCoder = SecurityManager.getCipher(SecurityManager.Model.MD5);
                    encryResult = abstractCoder.digestSignature(inputResult, keyData);
                    break;
                case "SHA1":
                    //不能解码，单向加密
                    abstractCoder = SecurityManager.getCipher(SecurityManager.Model.HMAC_SHA1);
                    encryResult = abstractCoder.digestSignature(inputResult, keyData);
                    break;
                case "RSA":
                    //公钥加密，非对称加密
                    abstractCoder = SecurityManager.getCipher(SecurityManager.Model.RSA);
                    RsaKeyHelper.KeyPass keyPass = RsaKeyHelper.generateKeyPair();
                    //生成密钥对
                    privateKeyHex = keyPass.getPrivateKeyHex();
                    publicKeyHex = keyPass.getPublicKeyHex();
                    encryResult = abstractCoder.simpleEnCode(inputResult, publicKeyHex);
                    jsonObject.put("privateKeyHex", privateKeyHex);
                    jsonObject.put("publicKeyHex", publicKeyHex);
                    break;
                case "DSA":
                    //私钥加密，单向加密可验证数据一致性（数字签名）
                    abstractCoder = SecurityManager.getCipher(SecurityManager.Model.DSA);
                    DSAKeyHelper.KeyPass dsaKeyPass = DSAKeyHelper.genKeyPair(keyData);
                    //生成密钥对
                    privateKeyHex = dsaKeyPass.getPrivateKeyHex();
                    publicKeyHex = dsaKeyPass.getPublicKeyHex();
                    encryResult = abstractCoder.digestSignature(inputResult, privateKeyHex);
                    jsonObject.put("privateKeyHex", privateKeyHex);
                    jsonObject.put("publicKeyHex", publicKeyHex);
                    break;
                default:
                    callbackContext.error("无" + type + "加密方式");
                    return;
            }
            jsonObject.put("encryptData", encryResult);
            jsonObject.put("key", keyData);

            callbackContext.success(jsonObject);
        } catch (JSONException e) {
            callbackContext.error("加密失败:" + e);
            e.printStackTrace();
        }

    }
```



    //解密用法
    @JavaScriptApi
    public void decrypt(AgreeSDKWebView webView, JSONArray array, CallbackContext callbackContext) {
        try {
            JSONObject res = array.getJSONObject(0);
            String encryResult = res.getString("data");
            String keyData = res.getString("key");
            String type = res.getString("type");
            Log.e("decrypt", res.toString());
            if (TextUtils.isEmpty(encryResult)) {
                callbackContext.error("解密数据为空");
                return;
            }
            if (keyData.equals("SM3")||keyData.equals("MD5")||keyData.equals("SHA1")){
                callbackContext.error(keyData+"类型不支持解密");
                return;
            }
            if (keyData.isEmpty()) {
                callbackContext.error("密钥不能为空");
                return;
            }
            AbstractCoder abstractCoder;
            String decodeResult = "";
            JSONObject jsonObject = new JSONObject();
            switch (type) {
                case "SM2":
                    abstractCoder = SecurityManager.getCipher(SecurityManager.Model.SM2);
                    decodeResult = abstractCoder.simpleDeCode(encryResult, keyData);
                    break;
                case "SM4":
                    abstractCoder = SecurityManager.getCipher(SecurityManager.Model.SM4);
                    String key = Utils.byteToHex(keyData.getBytes());
                    decodeResult = abstractCoder.simpleDeCode(encryResult, key);
                    break;
                case "DES":
                    //密钥长度不能小于8位
                    abstractCoder = SecurityManager.getCipher(SecurityManager.Model.DES);
                    decodeResult = abstractCoder.simpleDeCode(encryResult, keyData);
                    break;
                case "DES3":
                    abstractCoder = SecurityManager.getCipher(SecurityManager.Model.TRIDES);
                    decodeResult = new String(abstractCoder.deCode(Utils.hexStringToBytes(encryResult), keyData.getBytes()));
                    break;
                case "AES":
                    decodeResult = AESUtils.des(encryResult, keyData, Cipher.DECRYPT_MODE);
                    break;
//                case "MD5":
//                    //不能解码
//
//                    abstractCoder = SecurityManager.getCipher(SecurityManager.Model.MD5);
//                    decodeResult = abstractCoder.digestSignature(encryResult, keyData);
//                    break;
//                case "SHA1":
//                    //不能解码
//                    abstractCoder = SecurityManager.getCipher(SecurityManager.Model.HMAC_SHA1);
//                    decodeResult = abstractCoder.simpleDeCode(encryResult, keyData);
//                    break;
                case "RSA":
                    //私钥解码
                    abstractCoder = SecurityManager.getCipher(SecurityManager.Model.RSA);
                    decodeResult = abstractCoder.simpleDeCode(encryResult, keyData);
                    break;
                case "DSA":
                    //验证失败
                    //验证的数据+数字签名+公钥=>验证
                    String inputResult = res.getString("inputData");
                    abstractCoder = SecurityManager.getCipher(SecurityManager.Model.DSA);
//                    boolean status=abstractCoder.verifyWithDSA(Utils.hexStringToBytes(inputResult), encryResult, Utils.hexStringToBytes(dsaKeyHex));
                    boolean status = abstractCoder.verifyWithDSA(inputResult, encryResult, keyData);
                    jsonObject.put("status", status);
                    break;
                default:
                    callbackContext.error(type + "不能解密，或加解密类型输入有误");
                    return;
            }
            jsonObject.put("decodeResult", decodeResult);
            callbackContext.success(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
            callbackContext.error("解密失败：" + e);
        }
    }
