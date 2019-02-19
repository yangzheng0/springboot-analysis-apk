package com.example.demo.entity;

import lombok.Data;

/**
 * 解析应用实体类
 * @author Adam
 */
@Data
public class AnalysisApp {
	private String packageName;
	private String version;
	private String name;
	private String iconName;
}
