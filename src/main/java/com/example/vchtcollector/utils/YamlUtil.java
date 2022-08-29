package com.example.vchtcollector.utils;

import java.util.HashMap;
import java.util.Map;

public class YamlUtil {

	@SuppressWarnings("unchecked")
	public static Object take(Map<Object, Object> map, String key) {
		String[] split = key.split("\\.");
		Map<Object, Object> tmp = new HashMap<Object, Object>();
		tmp.putAll(map);
		int i = 0;
		for (i = 0; i < split.length - 1; i++) {
			tmp = (Map<Object, Object>) tmp.get(split[i]);
		}
		return tmp.get(split[i]);
	}
}
