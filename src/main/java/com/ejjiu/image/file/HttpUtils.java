package com.ejjiu.image.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class HttpUtils {
    public static interface CallBack {
        void onProgress(String fileUrl, long loaded, long total);
        
        void onComplete(String fileUrl);
        
        void onError(String fileUrl, Exception exception);
    }
    
    /**
     * LOCAL_PATH 文件存储的位置
     * subFolder 子文件夹
     * httpUrl 待下载文件地址
     * @return
     */
    public static boolean downLoad(File to, String httpUrl, CallBack callBack) {
       
        int index = 1;
        while (to.exists()) {
            to = new File(FileOperator.getFileNameNoEx(to.getAbsolutePath()) + "("+index+")." + FileOperator.getExtensionName(to.getAbsolutePath()));
            index++;
        }
        File tmpFile = new File(to.getAbsoluteFile() + ".tmp");
        long finished = 0;
        if (tmpFile.exists()) {
            finished = tmpFile.length();
            System.out.printf("downLoad start finished:%s%n", finished);
        }
        InputStream in = null;
        FileOutputStream out = null;
        HttpURLConnection conn = null;
        boolean isLoaded = false;
        Exception err = null;
        try {
            //初始化连接
            URL url = new URL(httpUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(20 * 1000);
            if (finished > 0) {
                conn.setRequestProperty("Range", "bytes=" + finished + "-");
            }
            System.out.println("conn.getResponseCode():" + conn.getResponseCode());
            //读取数据
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK || conn.getResponseCode() == HttpURLConnection.HTTP_PARTIAL) {
                byte[] buffer = new byte[1024 * 10];
                in = conn.getInputStream();
                
                out = new FileOutputStream(tmpFile, true);
                
                
                long size = conn.getContentLength() + finished;
                int count;
                int addByteCount = 0;
                while ((count = in.read(buffer)) != -1) {
                    if (count != 0) {
                        out.write(buffer, 0, count);
                        finished += count;
                        if (callBack != null) {
                            callBack.onProgress(httpUrl, finished, size);
                        }
                        addByteCount += count;
                        if (addByteCount > (1 << 20)) {//>1M
                            addByteCount = 0;
                            out.getFD().sync();
                        }
                        out.flush();
                        
                        // System.out.printf("---->%1$.2f%%\n",(double)finished/size*100);
                    } else {
                        break;
                    }
                }
                Files.move(tmpFile.toPath(),to.toPath(), StandardCopyOption.COPY_ATTRIBUTES,StandardCopyOption.REPLACE_EXISTING);
                isLoaded = true;
                
            }
            
        } catch (Exception e) {
            err = e;
        } finally {
            try {
                if (out != null) {
                    
                    out.close();
                    in.close();
                    conn.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (callBack != null) {
            if (isLoaded) {
                
                callBack.onComplete(httpUrl);
            } else {
                callBack.onError(httpUrl, err);
            }
        }
        
        return isLoaded;
    }
    
    
}