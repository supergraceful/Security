package com.example.lib.securitylibaray.general;


import android.text.TextUtils;

import com.example.lib.securitylibaray.general.core.AbstractCoder;
import com.example.lib.securitylibaray.general.core.aes.AESUtils;
import com.example.lib.securitylibaray.general.core.dsa.DSAKeyHelper;
import com.example.lib.securitylibaray.general.core.rsa.RsaKeyHelper;
import com.example.lib.securitylibaray.general.core.sm2.SM2KeyHelper;
import com.example.lib.securitylibaray.general.core.sm2.Sm2Kit;
import com.example.lib.securitylibaray.general.utils.Utils;

import javax.crypto.Cipher;

import static com.example.lib.securitylibaray.general.Encryption.EncryptType.DSA;
import static com.example.lib.securitylibaray.general.Encryption.EncryptType.RSA;
import static com.example.lib.securitylibaray.general.Encryption.EncryptType.SHA1;
import static com.example.lib.securitylibaray.general.Encryption.EncryptType.SM2;
import static com.example.lib.securitylibaray.general.Encryption.EncryptType.SM3;
import static com.example.lib.securitylibaray.general.Encryption.EncryptType.SM4;
import static com.example.lib.securitylibaray.general.Encryption.EncryptType.MD5;


public class Encryption {

    public static EncryptResult encrypt(String data, String key, EncryptType type) {
        String encryptParams=null;
        String privateKeyHex=null;
        String publicKeyHex=null;
        EncryptResult encryptResult=new EncryptResult();
        AbstractCoder abstractCoder;
        if (type.equals(SM4) &&  key.length() != 16) {
            encryptResult.setErrorMessage(type+"密钥不能为空并且长度应为16位");
            return encryptResult;
        } else if (key.isEmpty() && type.equals(DSA)) {
            encryptResult.setErrorMessage(type+"密钥不能为空且密钥长度不小于8位");
            return encryptResult;
        } else if (key.isEmpty() && !type.equals(RSA) && !type.equals(SM2)) {
            encryptResult.setErrorMessage(type+"密钥不能为空");
            return encryptResult;
        }
        switch (type) {
            case SM2:
                abstractCoder = SecurityManager.getCipher(SecurityManager.Model.SM2);
                SM2KeyHelper.KeyPair keyPair = SM2KeyHelper.generateKeyPair((Sm2Kit) abstractCoder);
                privateKeyHex = keyPair.getPrivateKey();
                publicKeyHex = keyPair.getPublicKey();
                encryptParams = abstractCoder.simpleEnCode(data, publicKeyHex);
                break;
            case SM3:
                //无法解码，单向加密
                abstractCoder = SecurityManager.getCipher(SecurityManager.Model.SM3);
                encryptParams = abstractCoder.digestSignature(data, key);
                SecurityManager.reStoreCipher();
                break;
            case SM4:
                //16位密钥
                abstractCoder = SecurityManager.getCipher(SecurityManager.Model.SM4);
                String keys = Utils.byteToHex(key.getBytes());
                encryptParams = abstractCoder.simpleEnCode(data, keys);
                break;
            case DES:
                abstractCoder = SecurityManager.getCipher(SecurityManager.Model.DES);
                encryptParams = abstractCoder.simpleEnCode(data, key);
                break;
            case DES3:
                abstractCoder = SecurityManager.getCipher(SecurityManager.Model.TRIDES);
                encryptParams = Utils.byteToHex(abstractCoder.enCode(data.getBytes(), key.getBytes()));
                break;
            case AES:
                encryptParams = AESUtils.des(data, key, Cipher.ENCRYPT_MODE);
                break;
            case MD5:
                //不能解码，单向加密
                abstractCoder = SecurityManager.getCipher(SecurityManager.Model.MD5);
                encryptParams = abstractCoder.digestSignature(data, key);
                break;
            case SHA1:
                //不能解码，单向加密
                abstractCoder = SecurityManager.getCipher(SecurityManager.Model.HMAC_SHA1);
                encryptParams = abstractCoder.digestSignature(data, key);
                break;
            case RSA:
                //公钥加密，非对称加密
                abstractCoder = SecurityManager.getCipher(SecurityManager.Model.RSA);
                RsaKeyHelper.KeyPass keyPass = RsaKeyHelper.generateKeyPair();
                //生成密钥对
                privateKeyHex = keyPass.getPrivateKeyHex();
                publicKeyHex = keyPass.getPublicKeyHex();
                encryptParams = abstractCoder.simpleEnCode(data, publicKeyHex);
                break;
            case DSA:
                //私钥加密，单向加密可验证数据一致性（数字签名）
                abstractCoder = SecurityManager.getCipher(SecurityManager.Model.DSA);
                DSAKeyHelper.KeyPass dsaKeyPass = DSAKeyHelper.genKeyPair(key);
                //生成密钥对
                privateKeyHex = dsaKeyPass.getPrivateKeyHex();
                publicKeyHex = dsaKeyPass.getPublicKeyHex();
                encryptParams = abstractCoder.digestSignature(data, privateKeyHex);
                break;
            default:
                encryptResult.setErrorMessage( "无" + type + "加密方式");
                break;
        }
        encryptResult.setEncryptParams(encryptParams);
        encryptResult.setPrivateKeyHex(privateKeyHex);
        encryptResult.setPublicKeyHex(publicKeyHex);

        return encryptResult;
    }

    public static String decrypt(String data, String key, EncryptType type) {
        String decryptParams=null;
        if (TextUtils.isEmpty(data)) {
            return "解密数据为空";
        }
        if (type.equals(SM3)||type.equals(MD5)||type.equals(SHA1)){
            return key+"类型不支持解密";
        }
        if (type.equals(DSA)){
            return "请使用dsaDecrypt方法进行数字签名验证";
        }
        if (key.isEmpty()) {
            return "密钥不能为空";
        }
        AbstractCoder abstractCoder;
        switch (type) {
            case SM2:
                abstractCoder = SecurityManager.getCipher(SecurityManager.Model.SM2);
                decryptParams = abstractCoder.simpleDeCode(data, key);
                break;
            case SM4:
                abstractCoder = SecurityManager.getCipher(SecurityManager.Model.SM4);
                String keyByte = Utils.byteToHex(key.getBytes());
                decryptParams = abstractCoder.simpleDeCode(data, keyByte);
                break;
            case DES:
                //密钥长度不能小于8位
                abstractCoder = SecurityManager.getCipher(SecurityManager.Model.DES);
                decryptParams = abstractCoder.simpleDeCode(data, key);
                break;
            case DES3:
                abstractCoder = SecurityManager.getCipher(SecurityManager.Model.TRIDES);
                decryptParams = new String(abstractCoder.deCode(Utils.hexStringToBytes(data), key.getBytes()));
                break;
            case AES:
                decryptParams = AESUtils.des(data, key, Cipher.DECRYPT_MODE);
                break;
            case RSA:
                //私钥解码
                abstractCoder = SecurityManager.getCipher(SecurityManager.Model.RSA);
                decryptParams = abstractCoder.simpleDeCode(data, key);
                break;

            default:
                return type + "不能解密，或加解密类型输入有误";
        }
        return decryptParams;
    }

    public static boolean dsaDecrypt(String data, String publicKey,String digitalSignature) throws Exception {
        //验证失败
        //验证的数据+数字签名+公钥=>验证
        AbstractCoder abstractCoder = SecurityManager.getCipher(SecurityManager.Model.DSA);
        boolean status = abstractCoder.verifyWithDSA(digitalSignature, data, publicKey);
        return status;
    }
    public enum EncryptType {
        SM2, SM3, SM4, DES, DES3, MD5, AES,SHA1,RSA,DSA
    }
}
