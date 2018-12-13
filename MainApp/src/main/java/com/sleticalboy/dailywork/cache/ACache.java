/*
 * Copyright (c) 2012-2013, Michael Yang 杨福海 (www.yangfuhai.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sleticalboy.dailywork.cache;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

/**
 * @author Michael Yang（www.yangfuhai.com） update at 2013.08.07
 */
public final class ACache {

	public static final int TIME_HOUR = 60 * 60;
	public static final int TIME_DAY = TIME_HOUR * 24;
	private static final int MAX_SIZE = 1000 * 1000 * 50; // 50 mb
	private static final int MAX_COUNT = Integer.MAX_VALUE; // 不限制存放数据的数量
	private static final Map<String, ACache> CACHE_MAP = new HashMap<>();
	private ACacheManager mCacheManager;
	public static String mCacheName = "ACache";

	public static ACache get(Context ctx) {
		return get(ctx, mCacheName);
	}

	public static ACache get(Context ctx, String cacheName) {
		File f = new File(ctx.getCacheDir(), cacheName);
		return get(f, MAX_SIZE, MAX_COUNT);
	}

	public static ACache get(File cacheDir) {
		return get(cacheDir, MAX_SIZE, MAX_COUNT);
	}

	public static ACache get(Context ctx, long maxSize, int maxCount) {
		File f = new File(ctx.getCacheDir(), mCacheName);
		return get(f, maxSize, maxCount);
	}

	public static ACache get(File cacheDir, long maxSize, int maxCount) {
		ACache manager = CACHE_MAP.get(cacheDir.getAbsoluteFile() + myPid());
		if (manager == null) {
			manager = new ACache(cacheDir, maxSize, maxCount);
			CACHE_MAP.put(cacheDir.getAbsolutePath() + myPid(), manager);
		}
		return manager;
	}

	private static String myPid() {
		return "_" + android.os.Process.myPid();
	}

	private ACache(File cacheDir, long maxSize, int maxCount) {
		if (!cacheDir.exists() && !cacheDir.mkdirs()) {
			throw new RuntimeException("can't make dirs in " + cacheDir.getAbsolutePath());
		}
		mCacheManager = new ACacheManager(cacheDir, maxSize, maxCount);
	}

	// =======================================
	// ============ String数据 读写 ==============
	// =======================================
	/**
	 * 保存 String数据 到 缓存中
	 * 
	 * @param key
	 *            保存的key
	 * @param value
	 *            保存的String数据
	 */
	public void put(String key, String value) {
		File file = mCacheManager.newFile(key);
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter(file), 1024);
			out.write(value);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.flush();
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			mCacheManager.put(file);
		}
	}

	/**
	 * 保存 String数据 到 缓存中
	 * 
	 * @param key
	 *            保存的key
	 * @param value
	 *            保存的String数据
	 * @param saveTime
	 *            保存的时间，单位：秒
	 */
	public void put(String key, String value, int saveTime) {
		put(key, Utils.newStringWithDateInfo(saveTime, value));
	}

	/**
	 * 读取 String数据
	 * 
	 * @param key
	 * @return String 数据
	 */
	public String getAsString(String key) {
		File file = mCacheManager.get(key);
		if (!file.exists())
			return null;
		boolean removeFile = false;
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(file));
			String readString = "";
			String currentLine;
			while ((currentLine = in.readLine()) != null) {
				readString += currentLine;
			}
			if (!Utils.isDue(readString)) {
				return Utils.clearDateInfo(readString);
			} else {
				removeFile = true;
				return null;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (removeFile)
				remove(key);
		}
	}

	// =======================================
	// ============= JSONObject 数据 读写 ==============
	// =======================================
	/**
	 * 保存 JSONObject数据 到 缓存中
	 * 
	 * @param key
	 *            保存的key
	 * @param value
	 *            保存的JSON数据
	 */
	public void put(String key, JSONObject value) {
		put(key, value.toString());
	}

	/**
	 * 保存 JSONObject数据 到 缓存中
	 * 
	 * @param key
	 *            保存的key
	 * @param value
	 *            保存的JSONObject数据
	 * @param saveTime
	 *            保存的时间，单位：秒
	 */
	public void put(String key, JSONObject value, int saveTime) {
		put(key, value.toString(), saveTime);
	}

	/**
	 * 读取JSONObject数据
	 * 
	 * @param key
	 * @return JSONObject数据
	 */
	public JSONObject getAsJSONObject(String key) {
		String JSONString = getAsString(key);
		try {
			JSONObject obj = new JSONObject(JSONString);
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// =======================================
	// ============ JSONArray 数据 读写 =============
	// =======================================
	/**
	 * 保存 JSONArray数据 到 缓存中
	 * 
	 * @param key
	 *            保存的key
	 * @param value
	 *            保存的JSONArray数据
	 */
	public void put(String key, JSONArray value) {
		put(key, value.toString());
	}

	/**
	 * 保存 JSONArray数据 到 缓存中
	 * 
	 * @param key
	 *            保存的key
	 * @param value
	 *            保存的JSONArray数据
	 * @param saveTime
	 *            保存的时间，单位：秒
	 */
	public void put(String key, JSONArray value, int saveTime) {
		put(key, value.toString(), saveTime);
	}

	/**
	 * 读取JSONArray数据
	 * 
	 * @param key
	 * @return JSONArray数据
	 */
	public JSONArray getAsJSONArray(String key) {
		String JSONString = getAsString(key);
		try {
			JSONArray obj = new JSONArray(JSONString);
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// =======================================
	// ============== byte 数据 读写 =============
	// =======================================
	/**
	 * 保存 byte数据 到 缓存中
	 * 
	 * @param key
	 *            保存的key
	 * @param value
	 *            保存的数据
	 */
	public void put(String key, byte[] value) {
		File file = mCacheManager.newFile(key);
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
			out.write(value);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.flush();
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			mCacheManager.put(file);
		}
	}

	/**
	 * 保存 byte数据 到 缓存中
	 * 
	 * @param key
	 *            保存的key
	 * @param value
	 *            保存的数据
	 * @param saveTime
	 *            保存的时间，单位：秒
	 */
	public void put(String key, byte[] value, int saveTime) {
		put(key, Utils.newByteArrayWithDateInfo(saveTime, value));
	}

	/**
	 * 获取 byte 数据
	 * 
	 * @param key
	 * @return byte 数据
	 */
	public byte[] getAsBinary(String key) {
		RandomAccessFile RAFile = null;
		boolean removeFile = false;
		try {
			File file = mCacheManager.get(key);
			if (!file.exists())
				return null;
			RAFile = new RandomAccessFile(file, "r");
			byte[] byteArray = new byte[(int) RAFile.length()];
			RAFile.read(byteArray);
			if (!Utils.isDue(byteArray)) {
				return Utils.clearDateInfo(byteArray);
			} else {
				removeFile = true;
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (RAFile != null) {
				try {
					RAFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (removeFile)
				remove(key);
		}
	}

	// =======================================
	// ============= 序列化 数据 读写 ===============
	// =======================================
	/**
	 * 保存 Serializable数据 到 缓存中
	 * 
	 * @param key
	 *            保存的key
	 * @param value
	 *            保存的value
	 */
	public void put(String key, Serializable value) {
		put(key, value, -1);
	}

	/**
	 * 保存 Serializable数据到 缓存中
	 * 
	 * @param key
	 *            保存的key
	 * @param value
	 *            保存的value
	 * @param saveTime
	 *            保存的时间，单位：秒
	 */
	public void put(String key, Serializable value, int saveTime) {
		ByteArrayOutputStream baos = null;
		ObjectOutputStream oos = null;
		try {
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(value);
			byte[] data = baos.toByteArray();
			if (saveTime != -1) {
				put(key, data, saveTime);
			} else {
				put(key, data);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				oos.close();
			} catch (IOException e) {
			}
		}
	}

	/**
	 * 读取 Serializable数据
	 * 
	 * @param key
	 * @return Serializable 数据
	 */
	public Object getAsObject(String key) {
		byte[] data = getAsBinary(key);
		if (data != null) {
			ByteArrayInputStream bais = null;
			ObjectInputStream ois = null;
			try {
				bais = new ByteArrayInputStream(data);
				ois = new ObjectInputStream(bais);
                return ois.readObject();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			} finally {
				try {
					if (bais != null)
						bais.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					if (ois != null)
						ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	// =======================================
	// ============== bitmap 数据 读写 =============
	// =======================================
	/**
	 * 保存 bitmap 到 缓存中
	 * 
	 * @param key
	 *            保存的key
	 * @param value
	 *            保存的bitmap数据
	 */
	public void put(String key, Bitmap value) {
		put(key, Utils.bitmap2Bytes(value));
	}

	/**
	 * 保存 bitmap 到 缓存中
	 * 
	 * @param key
	 *            保存的key
	 * @param value
	 *            保存的 bitmap 数据
	 * @param saveTime
	 *            保存的时间，单位：秒
	 */
	public void put(String key, Bitmap value, int saveTime) {
		put(key, Utils.bitmap2Bytes(value), saveTime);
	}

	/**
	 * 读取 bitmap 数据
	 * 
	 * @param key
	 * @return bitmap 数据
	 */
	public Bitmap getAsBitmap(String key) {
		if (getAsBinary(key) == null) {
			return null;
		}
		return Utils.bytes2Bitmap(getAsBinary(key));
	}

	// =======================================
	// ============= drawable 数据 读写 =============
	// =======================================
	/**
	 * 保存 drawable 到 缓存中
	 * 
	 * @param key
	 *            保存的key
	 * @param value
	 *            保存的drawable数据
	 */
	public void put(String key, Drawable value) {
		put(key, Utils.drawable2Bitmap(value));
	}

	/**
	 * 保存 drawable 到 缓存中
	 * 
	 * @param key
	 *            保存的key
	 * @param value
	 *            保存的 drawable 数据
	 * @param saveTime
	 *            保存的时间，单位：秒
	 */
	public void put(String key, Drawable value, int saveTime) {
		put(key, Utils.drawable2Bitmap(value), saveTime);
	}

	/**
	 * 读取 Drawable 数据
	 * 
	 * @param key
	 * @return Drawable 数据
	 */
	public Drawable getAsDrawable(String key) {
		if (getAsBinary(key) == null) {
			return null;
		}
		return Utils.bitmap2Drawable(Utils.bytes2Bitmap(getAsBinary(key)));
	}

	/**
	 * 获取缓存文件
	 * 
	 * @param key
	 * @return value 缓存的文件
	 */
	public File file(String key) {
		File f = mCacheManager.newFile(key);
		if (f.exists())
			return f;
		return null;
	}

	/**
	 * 移除某个key
	 * 
	 * @param key
	 * @return 是否移除成功
	 */
	public boolean remove(String key) {
		return mCacheManager.remove(key);
	}

	/**
	 * 清除所有数据
	 */
	public void clear() {
		mCacheManager.clear();
	}

}
