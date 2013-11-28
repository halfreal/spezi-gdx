package de.halfreal.spezi.gdx.system;

import java.beans.Introspector;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin.TintedDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.ReadOnlySerializer;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.SerializationException;

public class BuildST {

	public static class Pair {
		public Set<String> refs;
		public String styleName;
		public Class<?> type;

		public Pair(Class<?> type, String styleName) {
			super();
			this.type = type;
			refs = new HashSet<String>();
			this.styleName = styleName;
		}

	}

	private static String basedir;

	static Pair currentPair = null;

	public static void build(FileInputStream handle, FileInputStream packFile,
			String className) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				packFile), 64);

		StringBuilder sb = new StringBuilder();
		Json jsonReader = getJsonLoader(handle);
		List<Pair> list = jsonReader.fromJson(List.class, handle);
		HashSet<String> region = new HashSet<String>();
		int lines = 0;
		while (true) {
			String readLine = reader.readLine();
			if (readLine == null) {
				break;
			}
			if ("".equals(readLine.trim())) {
				lines = 0;
			}

			lines++;

			if (readLine.startsWith(" ") || lines <= 5) {
				continue;
			}

			String trim = readLine.trim();

			if (!region.contains(trim)) {
				list.add(new Pair(TextureRegion.class, trim));
				region.add(trim);
			}

		}

		createHeader(sb, className);
		int i = list.size();
		for (Pair pair : list) {
			i--;
			String simpleName = pair.type.getSimpleName();
			String decapitalize = Introspector.decapitalize(simpleName);
			if (pair.styleName.contains("/")) {
				String[] split = pair.styleName.split("\\/");
				pair.styleName = split[split.length - 1];
			}
			String field = (decapitalize + "_" + pair.styleName).replaceAll(
					"-", "_");
			String[] refs = pair.refs != null ? pair.refs
					.toArray(new String[pair.refs.size()]) : null;
			createConstant(pair.styleName, field, sb, refs, i < 1);
		}
		createEnd(sb, className);
		save(sb, className);
	}

	private static void createConstant(String key, String name,
			StringBuilder sb, String[] refs, boolean lastKey) {
		if (refs == null || refs.length == 0) {
			sb.append(name).append("(\"").append(key).append("\", null)");
		} else {
			sb.append(name).append("(\"").append(key)
					.append("\", new String[]{");
			for (int i = 0; i < refs.length; i++) {
				sb.append('"');
				sb.append(refs[i]);
				sb.append('"');
				if (i < refs.length - 1) {
					sb.append(",");
				}
			}
			sb.append("})");
		}
		if (!lastKey) {
			sb.append(",\n");
		} else {
			sb.append(";\n");
		}
	}

	private static void createEnd(StringBuilder sb, String className) {
		sb.append("private String key;\n");
		sb.append("private String[] refs;\n");
		sb.append(className);
		sb.append("(String key, String[] refs) {\n");
		sb.append("	this.key = key;\n");
		sb.append("	this.refs = refs;\n");
		sb.append("}\n");
		sb.append("\n");
		sb.append("public String key() {\n");
		sb.append("	return key;\n");
		sb.append("}\n");
		sb.append("public String[] refs() {\n");
		sb.append("	return refs;\n");
		sb.append("}\n");
		sb.append("\n");
		sb.append("\n}");
	}

	private static void createHeader(StringBuilder sb, String fileName) {
		sb.append("package de.halfreal.eafos.client.gen;\n\n");
		sb.append("\n/**\n Generated by BuidST \n*/\n");
		sb.append("public enum " + fileName + " {\n");
	}

	private static Json getJsonLoader(final FileInputStream skinFile) {

		final ArrayList<Pair> results = new ArrayList<BuildST.Pair>();

		final Json json = new Json() {
			private HashMap<String, String> types = new HashMap<String, String>();

			@Override
			public <T> T readValue(Class<T> type, Class elementType,
					JsonValue jsonData) {
				// If the JSON is a string but the type is not, look up the
				// actual value by name.
				if (jsonData.isString() && elementType == null
						&& !(Color.class.isAssignableFrom(type))
						&& currentPair != null) {
					if (jsonData != null) {
						types.put(currentPair.styleName, jsonData.asString());
					}
					if (types.containsKey(jsonData)) {
						Object data = jsonData;
						while (types.containsKey(data)) {
							String tempData = types.get(data);
							if (types.get(tempData) == null) {
								break;
							} else {
								data = tempData;
							}
						}
						currentPair.refs.add(types.get(data));
					} else {
						currentPair.refs.add(jsonData.asString());
					}
				}

				try {
					return super.readValue(type, elementType, jsonData);
				} catch (SerializationException se) {
					return null;
				}
			}
		};
		json.setTypeName(null);
		json.setUsePrototypes(false);

		json.setSerializer(List.class, new ReadOnlySerializer<List>() {
			@Override
			public List<BuildST.Pair> read(Json json, JsonValue jsonData,
					Class ignored) {

				for (JsonValue valueMap = jsonData.child(); valueMap != null; valueMap = valueMap
						.next()) {
					try {
						readNamedObjects(json, Class.forName(valueMap.name()),
								valueMap);
					} catch (ClassNotFoundException ex) {
						throw new SerializationException(ex);
					}
				}
				return results;
			}

			private void readNamedObjects(Json json, Class type,
					JsonValue valueMap) {
				Class addType = type == TintedDrawable.class ? Drawable.class
						: type;
				for (JsonValue valueEntry = valueMap.child(); valueEntry != null; valueEntry = valueEntry
						.next()) {
					Object object = json.readValue(type, valueEntry);
					if (object == null && type != Color.class
							&& type != BitmapFont.class) {
						continue;
					}
					try {
						System.out.println(String.format(
								"type=%s name=%s jsonData=%s", type,
								valueEntry.name(), valueEntry.name()));
						Pair pair = new Pair(type, valueEntry.name());
						results.add(pair);
						currentPair = pair;
					} catch (Exception ex) {
						throw new SerializationException("Error reading "
								+ type.getSimpleName() + ": "
								+ valueEntry.name(), ex);
					}
				}
			}

		});

		return json;
	}

	public static void main(String[] args) throws IOException {

		System.out.println(new File(".").getAbsolutePath());

		basedir = "";
		if (args.length > 0) {
			basedir = args[0] + "/";
		}

		System.out.println(basedir);
		FileInputStream handle = new FileInputStream(basedir
				+ "../eafos-client-android/assets/skins/defaultSkin.json");
		FileInputStream packFile = new FileInputStream(
				basedir
						+ "../eafos-client-android/assets/drawable-ldpi/defaultSkin.atlas");

		build(handle, packFile, "ST");

		handle = new FileInputStream(basedir
				+ "../eafos-client-android/assets/skins/splashSkin.json");
		packFile = new FileInputStream(
				basedir
						+ "../eafos-client-android/assets/drawable-ldpi/splashSkin.atlas");

		build(handle, packFile, "ST_SPLASH");

	}

	private static void save(StringBuilder sb, String fileName)
			throws IOException {

		FileWriter writer = null;
		try {
			writer = new FileWriter(
					basedir
							+ "../eafos-client3/src/main/gen/de/halfreal/eafos/client/gen/"
							+ fileName + ".java", false);
			writer.write(sb.toString());
		} catch (IOException e) {
			throw e;
		} finally {
			if (writer != null) {
				writer.close();
			}

		}
	}
}
