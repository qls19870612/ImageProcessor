package com.ejjiu.image.utils;

import com.ejjiu.image.controllers.Controller;
import com.ejjiu.image.componet.AlertBox;
import com.ejjiu.image.config.AppConfig;
import com.ejjiu.image.enums.ConfigType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;


/**
 *
 * 创建人  liangsong
 * 创建时间 2021/07/15 14:25
 */
public class ShellProcessor {
    private static final Logger logger = LoggerFactory.getLogger(ShellProcessor.class);
    public static void exe(String shellFile) {
        String config = AppConfig.getConfigRepository().getConfig(ConfigType.GLOBAL_GIT_BASH_PATH);
        if (StringUtils.isEmpty(config)) {
            AlertBox.showAlert("未配置正确的git-bash.exe路径");
            return;
        }
        File file = new File(config);
        if (!file.exists() || file.isDirectory()) {
            AlertBox.showAlert("未配置正确的git-bash.exe路径");
            return;
        }
        if (StringUtils.isEmpty(shellFile)) {
            AlertBox.showAlert("不正确的shell脚本文件路径");
            return;
        }
        File shFile = new File(shellFile);
        if (!shFile.exists() || shFile.isDirectory()) {
            AlertBox.showAlert("不正确的shell脚本文件路径");
            return;
        }
        ProcessBuilder pb = new ProcessBuilder(file.getAbsolutePath(), shellFile );

        StringWriter sw = new StringWriter();
        PrintWriter printWriter = new PrintWriter(sw);
        pb.directory(shFile.getParentFile());
        int runningStatus = 0;
        pb.redirectErrorStream(false);
        try {
            Process p = pb.start();


            new Thread(new Runnable() {
                @Override
                public void run() {
                    BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

                    BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

                    String s = null;
                    try {
                        while ((s = stdInput.readLine()) != null) {
                          logger.debug("bash INFO:{}", s);
                          Controller.log("bash INFO:" + s);
                        }
                        while ((s = stdError.readLine()) != null) {
                          logger.debug("bash ERROR:{}", s);
                            Controller.log("bash ERROR:" + s);
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                }
            },"shell").start();

            runningStatus = p.waitFor();

        } catch (IOException | InterruptedException e) {

            e.printStackTrace(printWriter);
            AlertBox.showAlert(sw.getBuffer().toString());

        }
        if (runningStatus != 0) {
            AlertBox.showAlert("执行发生错误");
        }

    }

}
