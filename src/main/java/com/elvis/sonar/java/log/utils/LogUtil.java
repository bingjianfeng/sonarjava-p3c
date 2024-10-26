package com.elvis.sonar.java.log.utils;

import com.elvis.sonar.java.log.formatter.LogFormatter;
import org.sonar.api.SonarProduct;

import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

/**
 * @author fengbingjian
 * @description TODO
 * @since 2024/9/26 10:50
 **/
public class LogUtil {

    private static Logger logger = null;
    private static String logPath = null;
    private static Boolean isEnable = false;
    private static SonarProduct sonarProduct = null;

    static {
        initial();
    }

    public static void initial() {
        /** 只有是SonarLint，才会执行日志登记 */
        if (sonarProduct == null || SonarProduct.SONARLINT != sonarProduct) {
            return;
        }
        if (logger == null) {
            loadProperties();
            loadFileHandler();
        }
    }

    private static void loadProperties() {
        ResourceBundle bundle = ResourceBundle.getBundle("logger");
        if (bundle == null) {
            return;
        }
        String enableStr = bundle.getString("log.enable");
        logPath = bundle.getString("log.path");
        if (enableStr == null || "".equals(enableStr)) {
            return;
        }
        if ("true".equals(enableStr.toLowerCase())) {
            isEnable = true;
        }
    }

    private static void loadFileHandler() {
        try {
            if ("".equals(logPath)) {
                return;
            }
            String dirPathStr = logPath.substring(0, logPath.lastIndexOf(File.separator));
            File dirPath = new File(dirPathStr);
            if (!dirPath.exists()) {
                dirPath.mkdirs();
            }
            FileHandler fileHandler = new FileHandler(logPath);
            LogFormatter formatter = new LogFormatter();
            fileHandler.setFormatter(formatter);
            logger = Logger.getLogger("");
            logger.setUseParentHandlers(false);
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void message(String message) {
        if (!isEnable) {
            return;
        }
        logger.info(String.format("[%s] ", getStackTraceMethodName()) + message);
    }

    public static void message(String message, Object... args) {
        if (!isEnable) {
            return;
        }
        logger.info(String.format("[%s] ", getStackTraceMethodName()) + String.format(message, args));
    }

    private static String getStackTraceMethodName() {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        StackTraceElement caller = stackTraceElements[3];
        String className = caller.getClassName();
        String methodName = caller.getMethodName();
        return String.format("%s.%s", className.substring(className.lastIndexOf(".") + 1), methodName);
    }

    public static void setProduct(SonarProduct product) {
        sonarProduct = product;
    }
}