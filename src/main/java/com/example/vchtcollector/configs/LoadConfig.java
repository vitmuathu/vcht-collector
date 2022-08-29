package com.example.vchtcollector.configs;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.example.vchtcollector.utils.YamlUtil;
import org.yaml.snakeyaml.Yaml;

public class LoadConfig {
	private ConcurrentHashMap<Object, Object> mapConfig = new ConcurrentHashMap<Object, Object>();

	private String url;
	private String user;
	private String password;
	private String file;

	public LoadConfig(String file) {
		this.file = file;
	}

	@SuppressWarnings("unchecked")
	public boolean loadConfig() {
		Yaml yaml = new Yaml();
		Map<Object, Object> map;
		try {
			map = (Map<Object, Object>) yaml.load(new FileReader(file));
			mapConfig.putAll(map);
			url = (String) YamlUtil.take(mapConfig, "file.url");
			user = (String) YamlUtil.take(mapConfig, "file.user");
			password = (String) YamlUtil.take(mapConfig, "file.password");
			return true;

		} catch (FileNotFoundException e) {
			// TODO: handle exception
		}
		return false;
	}

	public ConcurrentHashMap<Object, Object> getMapConfig() {
		return mapConfig;
	}

	public void setMapConfig(ConcurrentHashMap<Object, Object> mapConfig) {
		this.mapConfig = mapConfig;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "LoadConfig [mapConfig=" + mapConfig + ", url=" + url + ", user=" + user + ", password=" + password
				+ "]";
	}

	public static void main(String[] args) {
		LoadConfig conf = new LoadConfig("file-config/info.yml");
		conf.loadConfig();
		System.out.println(conf.getUrl());
		System.out.println(conf.getUser());
		System.out.println(conf.getPassword());

	}
}
