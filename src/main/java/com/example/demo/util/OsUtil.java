package com.example.demo.util;

import java.util.Properties;

/**
 * @author Adam
 * @Description: 判断服务器的系统类型，linux or windows
 * @Title: OsUtil
 * @ProjectName wanmo
 * @date 2018/8/17 12:51
 */
public class OsUtil {

    private static final String LINUX_OS_STR = "linux";

    /**
     * 判断是否是linux
     * @return ${return_type}
     * @author Adam
     * @date
     */
    public static boolean isOSLinux() {
        Properties prop = System.getProperties();

        String os = prop.getProperty("os.name");
        if (os != null && os.toLowerCase().indexOf(LINUX_OS_STR) > -1) {
            return true;
        } else {
            return false;
        }
    }
}
