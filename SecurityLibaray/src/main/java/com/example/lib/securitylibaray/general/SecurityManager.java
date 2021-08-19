package com.example.lib.securitylibaray.general;


import com.example.lib.securitylibaray.general.core.AbstractCoder;
import com.example.lib.securitylibaray.general.core.aes.AESKit;
import com.example.lib.securitylibaray.general.core.des.OneDesKit;
import com.example.lib.securitylibaray.general.core.des.TriDesKit;
import com.example.lib.securitylibaray.general.core.dsa.DSAKit;
import com.example.lib.securitylibaray.general.core.md5.MD5Kit;
import com.example.lib.securitylibaray.general.core.rsa.RsaKit;
import com.example.lib.securitylibaray.general.core.sha1.HMacSha1Kit;
import com.example.lib.securitylibaray.general.core.sm2.Sm2Kit;
import com.example.lib.securitylibaray.general.core.sm3.Sm3Kit;
import com.example.lib.securitylibaray.general.core.sm4.Sm4Kit;

/**
 * Created by fplei on 2018/9/26.
 */
public class SecurityManager {
    public enum Model{
        AES,RSA,TRIDES,DES,HMAC_SHA1,DSA,SM2,SM3,SM4,MD5
    }
    private static AbstractCoder abstractCoder=null;
    private static Object object=new Object();
    private static Model currentModel=null;

    /**
     * 获取加密器
     * @param model 加密器模式
     * @return 返回加密器
     */
    public static AbstractCoder getCipher(Model model){
        if(abstractCoder==null||
                (currentModel==null||currentModel!=model)){
            synchronized (object){
                if(abstractCoder==null||
                        (currentModel==null||currentModel!=model)){
                    currentModel=model;
                    switch (model){
                        case AES:
                            abstractCoder=new AESKit();
                            break;
                        case RSA:
                            abstractCoder=new RsaKit();
                            break;
                        case TRIDES:
                            abstractCoder=new TriDesKit();
                            break;
                        case SM2:
                            abstractCoder=new Sm2Kit();
                            break;
                        case SM3:
                            abstractCoder=new Sm3Kit();
                            break;
                        case DES:
                            abstractCoder=new OneDesKit();
                            break;
                        case DSA:
                            abstractCoder=new DSAKit();
                            break;
                        case MD5:
                            abstractCoder=new MD5Kit();
                            break;
                        case HMAC_SHA1:
                            abstractCoder=new HMacSha1Kit();
                            break;
                        case SM4:
                            abstractCoder=new Sm4Kit();
                            break;
                    }
                }
            }
        }

        return abstractCoder;
    }

    /**
     * 重置
     */
    public static void reStoreCipher(){
        abstractCoder=null;
        currentModel=null;
    }
}
