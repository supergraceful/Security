package com.example.lib.securitylibaray.general.utils;

import android.content.Context;
import android.os.Environment;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static android.content.Context.MODE_PRIVATE;

public class FileUtil {

    public static void getFile() {

    }

    public static JSONObject getStorePath(Context context) {
        return getStorePath(context, null);
    }

    public static JSONObject getStorePath(Context context, String fileName) {
        // /data目录下的文件物理上存放在我们通常所说的内部存储里面
        // /storage目录下的文件物理上存放在我们通常所说的外部存储里面
        // /system用于存放系统文件，/cache用于存放一些缓存文件，物理上它们也是存放在内部存储里面的
        JSONObject jsonObject = new JSONObject();
        try {
            if (context != null) {
                String interiorFilesDir = context.getFilesDir().getAbsolutePath();
                String exteriorFilesDir = context.getExternalFilesDir("").getAbsolutePath();
                String exteriorCacheDir = context.getExternalCacheDir().getAbsolutePath();
                if (fileName != null) {
                    String interiorDir = context.getDir(fileName, MODE_PRIVATE).getAbsolutePath();
                    jsonObject.put("interiorDir", interiorDir); //某个应用在内部存储中的自定义路径  /data/user/0/packname/app_myFile
                }
                jsonObject.put("interiorFilesDir", interiorFilesDir);//获取某个应用在内部存储中的files路径    /data/user/0/{packname}/cache
                jsonObject.put("exteriorFilesDir", exteriorFilesDir);  //获取某个应用在外部存储中的files路径  /storage/emulated/0/Android/data/packname/files
                jsonObject.put("exteriorCacheDir", exteriorCacheDir);  //获取某个应用在外部存储中的cache路径 /storage/emulated/0/Android/data/packname/cache
            }
            jsonObject.put("interiorDirectory", Environment.getDataDirectory());//   获取内部存储的根路径:/data
            jsonObject.put("exteriorDirectory", Environment.getExternalStorageDirectory().getAbsolutePath());  //获取外部存储的根路径   /storage/emulated/0
            jsonObject.put("downloadCacheDirectory", Environment.getDownloadCacheDirectory());  //获取缓存根路径   /cache
            jsonObject.put("rootDirectory", Environment.getRootDirectory());  //获取系统根路径   /system

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }


    public byte[] getFileStream(String path) throws IOException{
        FileInputStream inputStream=new FileInputStream(path);
        return readInputStream(inputStream);
    }

    public byte[] readInputStream(InputStream inputStream){
        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
        byte []buffer=new byte[1024];
        int len=-1;
        try{
            if (inputStream!=null){
                while ((len=inputStream.read(buffer))!=-1){
                    outputStream.write(buffer,0,len);
                }
            }
            outputStream.close();
            inputStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return outputStream.toByteArray();
    }
}
