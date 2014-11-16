package de.halfreal.spezi.gdx.system;

import java.util.Arrays;
import java.util.Comparator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class CacheHelper {

	private static long allowedSize;
	private static String androidTempPath = "/sdcard/eafos/tmp/";
	private static final String ASSETS_PATH = "map/";

	private static String desktopTempPath = "/tmp/eafos/";

	/**
	 * DO NOT USE A SEPARATOR WHICH MUST BE ESCAPED IN JAVA OR CHANGE THE
	 * String.split METHOD
	 */
	private static final String SEPARATOR = "_";

	static {
		// 20MB
		allowedSize = 1024 * 1024 * 20;
		checkDirectory();
	}

	/**
	 * Renames existing files to fit the time-name pattern in order to determine
	 * last access on a file. If no file exists only absolute FilePath is
	 * returned.
	 *
	 * @param key
	 * @return
	 */
	public synchronized static FileHandle cachedFileHandle(String key) {
		if (existsInternal(key)) {
			return Gdx.files.internal(ASSETS_PATH + getRealFileName(key));
		}
		String path = getTempDirPath() + getRealFileName(key);
		return Gdx.files.absolute(path);
	}

	public static void checkDirectory() {
		FileHandle handle = new FileHandle(getTempDirPath());
		if (!handle.exists()) {
			handle.mkdirs();
		}

		if (handle.isDirectory()) {

			long size = dirSize(handle);

			if (size > allowedSize) {
				FileHandle[] list = handle.list();
				Arrays.sort(list, 0, list.length, new Comparator<FileHandle>() {

					@Override
					public int compare(FileHandle o1, FileHandle o2) {
						long thisVal = lastUsed(o1);
						long anotherVal = lastUsed(o2);
						return (thisVal < anotherVal ? -1
								: (thisVal == anotherVal ? 0 : 1));
					}
				});

				for (FileHandle fileHandle : list) {
					size -= fileHandle.length();
					fileHandle.delete();
					if (size <= allowedSize) {
						break;
					}
				}

			}
		}

	}

	public static long dirSize() {
		return dirSize(new FileHandle(getTempDirPath()));
	}

	private static long dirSize(FileHandle handle) {
		long size = 0;
		for (FileHandle file : handle.list()) {
			size += file.length();
		}
		return size;
	}

	public static boolean existsInternal(String key) {
		FileHandle internal = Gdx.files.internal(ASSETS_PATH
				+ getRealFileName(key));
		return internal.exists();
	}

	/**
	 * Checks if a file exists in the cache directory independent of its time
	 * version.
	 *
	 * @param fileName
	 * @return
	 */
	public static boolean fileExists(String key) {
		if (existsInternal(key)) {
			return true;
		}
		String path = getTempDirPath() + getRealFileName(key);
		return new FileHandle(path).exists();
	}

	/**
	 * Not renaming the file, but just returning an existing FilePath.
	 *
	 * @param key
	 * @return
	 */
	public static FileHandle findFile(String key) {
		return new FileHandle(getTempDirPath() + getRealFileName(key));
	}

	public static long getAllowedSize() {
		return allowedSize;
	}

	public static String getAndroidTempPath() {
		return androidTempPath;
	}

	public static String getDesktopTempPath() {
		return desktopTempPath;
	}

	private static String getRealFileName(String name) {
		return String.valueOf(name.hashCode());
	}

	public static String getTempDirPath() {
		return Gdx.files.getLocalStoragePath() + "/tmp/eafos/";
	}

	/**
	 * Used only with found paths by {@link CacheHelper.findFile}.
	 *
	 * @param file
	 * @return
	 */
	private static long lastUsed(FileHandle file) {
		if (file == null) {
			return -1;
		}
		String name = file.name();
		long lastUsed;
		try {
			lastUsed = Long.parseLong(name.split(SEPARATOR)[0]);
		} catch (Exception e) {
			lastUsed = 0;
		}
		return lastUsed;
	}

	/**
	 * Used only with found paths by {@link CacheHelper.findFile}.
	 *
	 * @param file
	 * @return
	 */
	public static long lastUsed(String fileName) {
		return lastUsed(findFile(fileName));
	}

	public static void setAllowedSize(long allowedSize) {
		CacheHelper.allowedSize = allowedSize;
	}

	public static void setAndroidTempPath(String androidTempPath) {
		CacheHelper.androidTempPath = androidTempPath;
	}

	public static void setDesktopTempPath(String desktopTempPath) {
		CacheHelper.desktopTempPath = desktopTempPath;
	}

}
