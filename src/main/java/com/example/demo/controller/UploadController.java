package com.example.demo.controller;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.entity.AnalysisApp;
import com.example.demo.util.ApkUtil;
import com.example.demo.util.OsUtil;

@RestController
public class UploadController {

	public final static String windowsFilePath = "D:\\file\\";
	public final static String linuxFilePath = "/usr/local/uploadApk/";

	@RequestMapping("/uploadApk")
	public AnalysisApp uploadApk(MultipartFile file) throws Exception {
		AnalysisApp analysisApp = null;
		if (file != null && !file.isEmpty()) {
			String path;
			if (OsUtil.isOSLinux()) {
				path = linuxFilePath;
			} else {
				path = windowsFilePath;
			}
			//1.接收上传的应用，写入到硬盘
            String appName = UUID.randomUUID().toString().replace("-", "").toLowerCase()+".apk";
            File dirFile = new File(path + appName);
            file.transferTo(dirFile);
            //调用工具类解析apk
            analysisApp = ApkUtil.analysisAPK(path, appName);
		}
		return analysisApp;
	}
}
