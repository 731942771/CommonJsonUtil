package com.cuiweiyou.xianke.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * JSON数据解析
 * @author cuiweiyou
 */
public class CommonJsonUtil {
	
	/**
	 * 解析json返回自动匹配的bean封装好的list集合<br/>
	 * 一旦上线此方法不再更改
	 * @param json json字串
	 * @return list集合
	 */
	private List<Object> analyzeJson2List(String json){

		if (json == null || "".equals(json))
			return null;
		
		List<Object> result = new ArrayList<Object>();
		
		
		return result;
	}
	
	/**
	 * 自动匹配json数据返回对应的bean<br/>
	 * 一旦上线，此方法不能再有任何修改
	 * @param json json字串
	 * @return 对应的bean封装体
	 */
	public Object analyzeJson2Bean(String json) {

		if (json == null || "".equals(json))
			return null;
		
		String beanName = null; // Bean的类名，即JSON串中JSONObject的Key
		
		JSONObject jobj = null;
		
		try {
			jobj = new JSONObject(json);
			Iterator<String> keys = jobj.keys(); // 获取全部的key
			while (keys.hasNext()) { // 遍历key
				String key = keys.next(); // json串格式固定，得到key
				beanName = key;
				break;
			}
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		
		if (beanName == null || "".equals(beanName))
			return null;
		
		try {
			/** 反射，得到本条json串对应的Bean封装体 */
			Class<?> clazz = Class.forName("com.cuiweiyou.analyze." + beanName);
			/** 反射，得到Bean的全部成员属性 */
			Field[] fields = clazz.getDeclaredFields(); // 公有私有属性都拿来

			/** 构建Bean实例时用到的参数的类型集 */
			Class[] types = new Class[fields.length];
			/** 构建Bean实例时用到的参数集 */
			Object[] parms = new Object[fields.length];

			// json串中bean体对应的数据
			JSONObject jcobj = jobj.getJSONObject(beanName);

			// 提取Bean属性的值
			for (int i = 0; i < types.length; i++) {
				Class<?> type = fields[i].getType(); // 成员属性的类型，String、int、...
				String fieldName = fields[i].getName(); // 成员属性的名称，name、age、...

				Object fieldValue = jcobj.get(fieldName); // 根据成员名称，从json中提取对应的值。"北京"、23、...
				String fieldType = type.getSimpleName();

				System.out.println("变量类型：" + fieldType + "，变量名：" + fieldName + "， 变量值：" + fieldValue);

				types[i] = type;

				// 判断反射到的类的成员是什么类型。非集合类型的变量都好处理
				if ("int".equals(type.getSimpleName()) || "Integer".equals(type.getSimpleName())) {
					parms[i] = Integer.parseInt(fieldValue.toString());
				} else if ("float".equals(type.getSimpleName()) || "Float".equals(type.getSimpleName())) {
					parms[i] = Float.parseFloat(fieldValue.toString());
				} else if ("short".equals(type.getSimpleName()) || "Short".equals(type.getSimpleName())) {
					parms[i] = Short.parseShort(fieldValue.toString());
				} else if ("long".equals(type.getSimpleName()) || "Long".equals(type.getSimpleName())) {
					parms[i] = Long.parseLong(fieldValue.toString());
				} else if ("double".equals(type.getSimpleName()) || "Double".equals(type.getSimpleName())) {
					parms[i] = Double.parseDouble(fieldValue.toString());
				} else if ("boolean".equals(type.getSimpleName()) || "Boolean".equals(type.getSimpleName())) {
					parms[i] = Boolean.parseBoolean(fieldValue.toString());
				} else {
					parms[i] = fieldValue;
				}
			}

			/** 发射，得到对应数量参数的构造方法 */
			Constructor cstor = clazz.getDeclaredConstructor(types);
			cstor.setAccessible(true);// 取消 Java 语言访问安全检查。此时可操作私有成员
			/** 反射，通过构造方法创建Bean实例 */
			return cstor.newInstance(parms);

		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}
}
