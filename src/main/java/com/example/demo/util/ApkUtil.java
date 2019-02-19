package com.example.demo.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import com.example.demo.entity.AnalysisApp;

import java.io.File;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * <p>Title: ApkUtil</p>
 * <p>Description: APK解析工具类</p>
 *
 * @author Adam
 * @date 2018年4月24日
 */
@Slf4j
public class ApkUtil {

    /**
     * 解析apk
     * @author Adam
     * @date 12:33 2018/12/18
     * @param path
     * @param appName
     * @return com.hangzhi.bean.AnalysisApp
     **/
    public static AnalysisApp analysisAPK(String path, String appName) throws Exception {
        boolean isLinux = OsUtil.isOSLinux();
        AnalysisApp ret = new AnalysisApp();
        Runtime r = Runtime.getRuntime();
        Process p = r.exec("aapt d --values badging " + path + appName, null, new File(path));
        InputStream is = p.getInputStream();
        DynamicBuffer buffer = new DynamicBuffer();
        while (true) {
            byte[] ab = new byte[1024];
            int i = is.read(ab);
            if (i == -1) {
                break;
            }
            buffer.append(ab, 0, i);
        }
        byte[] output = buffer.toByteArray();
        String strOutput = new String(output, "UTF-8");
        String[] lines;
        if (isLinux) {
            lines = strOutput.split("\n");
        } else {
            lines = strOutput.split("\r\n");
        }
        for (String line : lines) {
            String[] keyValue = line.split(":");
            if (keyValue.length < 2) {
                continue;
            }
            String key = keyValue[0];
            String value = keyValue[1].trim();
            if ("package".equals(key)) {
                List<String> properties = new LinkedList<>();
                int ptr = 0;
                while (true) {
                    int propertyValueBegin = value.indexOf("'", ptr);
                    if (propertyValueBegin == -1) {
                        break;
                    }
                    int propertyValueEnd = value.indexOf("'", propertyValueBegin + 1);
                    properties.add(value.substring(ptr, propertyValueEnd + 1));
                    ptr = propertyValueEnd + 2;
                }
                for (String property : properties) {
                    String[] propertyKeyValue = property.split("=");
                    String propertyKey = propertyKeyValue[0];
                    String propertyValue = propertyKeyValue[1].substring(1, propertyKeyValue[1].length() - 1);
                    if ("name".equals(propertyKey)) {
                        ret.setPackageName(propertyValue);
                    } else if ("versionName".equals(propertyKey)) {
                        ret.setVersion(propertyValue);
                    }
                }
            } else if ("application".equals(key)) {
                String[] properties = new String[2];
                int space = value.lastIndexOf(" ");
                properties[0] = value.substring(0, space);
                properties[1] = value.substring(space + 1);
                for (String property : properties) {
                    String[] propertyKeyValue = property.split("=");
                    String propertyKey = propertyKeyValue[0];
                    String propertyValue = propertyKeyValue[1].substring(1, propertyKeyValue[1].length() - 1);
                    if ("label".equals(propertyKey)) {
                        ret.setName(propertyValue);
                    } else if ("icon".equals(propertyKey)) {
                        if (!StringUtils.isEmpty(propertyValue)) {
                            ret = unzipApp(propertyValue, path, appName, r, p, isLinux, ret);
                        }
                    }
                }
            }
        }
        return ret;
    }

    /**
     * 解压apk 获取图标
     * @author Adam
     * @date 12:34 2018/12/18
     * @param propertyValue
     * @param path
     * @param appName
     * @param r
     * @param p
     * @param isLinux
     * @param ret
     * @return com.hangzhi.bean.AnalysisApp
     **/
    public static AnalysisApp unzipApp(String propertyValue, String path, String appName, Runtime r, Process p, boolean isLinux, AnalysisApp ret) throws Exception {

        String iconPath = propertyValue;
        //获取图标文件名
        int index = iconPath.lastIndexOf("/") + 1;
        String iconFileName = iconPath.substring(index);
        //取后缀
        index = iconFileName.lastIndexOf(".");
        String ext = iconFileName.substring(index);
        //获得app的图标
        p = r.exec("7za e -y " + path + appName + " " + iconPath, null, new File(path));
        p.waitFor();
        File icon = new File(path + iconFileName);
        //图标名称改为     包名+随机数
        String appIconName = ret.getPackageName() + "." + new Random().nextInt(100) + ext;
        String destFilePath = path + appIconName;
        try {
            icon.renameTo(new File(destFilePath));
        } catch (Exception e) {
            e.printStackTrace();
        }
        ret.setIconName(appIconName);
        return ret;
    }
}
