package de.halfreal.spezi.gdx.system;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeBitmapFontData;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

public class FontHelper {

	public static final String DEFAULT_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890/|\\-_.,;:'\"üäöÜÄÖß~=$+[] {}<>()¢%∞…?!#@*";
	private static Logger log = LoggerFactory.getLogger(FontHelper.class);
	private static Map<String, Map<Integer, BitmapFont>> maps;

	final static int STANDART_SIZE = Assets.dip(33);

	static {
		maps = new ConcurrentHashMap<String, Map<Integer, BitmapFont>>();
	}

	public static void clear() {
		maps.clear();
	}

	public static void dispose() {
		log.debug("Disposing Fonts.");
		for (Map<Integer, BitmapFont> fontCache : maps.values()) {
			if (fontCache != null) {
				for (BitmapFont font : fontCache.values()) {
					if (font != null) {
						try {
							font.dispose();

						} catch (RuntimeException re) {
							log.error("disposing font not possible: ", re);
						}
					}
				}
			}
		}
		maps.clear();
	}

	public static BitmapFont font(int size, Map<Integer, BitmapFont> map,
			String ttfpath, String staticPath, boolean justLoading) {

		if (Assets.getManager().isLoaded(staticPath)) {
			return Assets.getManager().get(staticPath, BitmapFont.class);
		}

		BitmapFont generateFont = null;
		String cachedPath = CacheHelper.getTempDirPath() + staticPath;
		FileHandle external = Gdx.files.external(cachedPath);
		FileHandle internal = Gdx.files.internal(staticPath);
		String path = staticPath;
		if (internal.exists() || external.exists()) {
			if (!internal.exists()) {
				path = cachedPath;
			}
			Assets.getManager().load(path, BitmapFont.class);
			Assets.getManager().finishLoading();
			generateFont = Assets.getManager()
					.get(staticPath, BitmapFont.class);

		} else if (!justLoading) {

			if (map.get(STANDART_SIZE) == null) {
				Gdx.app.error("FontHelper", "No static font file " + staticPath
						+ "/ or " + cachedPath);
				generateFont = generateFont(ttfpath, STANDART_SIZE, external);
				map.put(STANDART_SIZE, generateFont);
			}

			try {
				BitmapFont bitmapFont = map.get(STANDART_SIZE);
				FreeTypeBitmapFontData data = (FreeTypeBitmapFontData) bitmapFont
						.getData();
				FreeTypeFontGenerator.FreeTypeBitmapFontData newData = new FreeTypeFontGenerator.FreeTypeBitmapFontData();

				Field region = FreeTypeBitmapFontData.class
						.getDeclaredField("regions");
				region.setAccessible(true);
				region.set(newData, data.getTextureRegions());

				Field glyphs = FreeTypeBitmapFontData.class.getField("glyphs");
				glyphs.setAccessible(true);
				glyphs.set(newData, data.glyphs);

				newData.ascent = data.ascent;
				newData.capHeight = data.capHeight;
				newData.descent = data.descent;
				newData.down = data.down;
				newData.flipped = data.flipped;
				newData.fontFile = data.fontFile;
				newData.imagePaths = data.imagePaths;
				newData.lineHeight = data.lineHeight;
				newData.scaleX = data.scaleX;
				newData.scaleY = data.scaleY;
				newData.spaceWidth = data.spaceWidth;
				newData.xHeight = data.xHeight;

				BitmapFont copyOfBitmapFont = new BitmapFont(newData,
						bitmapFont.getRegion(), false);
				copyOfBitmapFont.setScale(size / (float) STANDART_SIZE);
				generateFont = copyOfBitmapFont;

				// if (map.get(size) != null && map.get(size).get() != null) {
				// generateFont = map.get(size).get();
				// } else {
				// generateFont = generateFont(ttfpath, STANDART_SIZE,
				// external);
				// map.put(STANDART_SIZE, new
				// SoftReference<BitmapFont>(generateFont));
				// }
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return generateFont;
	}

	public static BitmapFont generateFont(String fontPath, int size,
			FileHandle cachedPath) {

		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
				Gdx.files.internal(fontPath));

		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.characters = DEFAULT_CHARS;
		parameter.flip = false;
		parameter.size = size;
		BitmapFont generateFont = generator.generateFont(parameter);

		// TODO Store the Font in the temp folder for later reuse!
		// use FontGenerator, to store the data, which is still unclear if we
		// can use it on Android
		generateFont.getRegion().getTexture()
				.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		generator.dispose();
		return generateFont;
	}

	public static BitmapFont getFont(String path, int size) {
		Map<Integer, BitmapFont> map = maps.get(path);
		if (map == null) {
			maps.put(path, new HashMap<Integer, BitmapFont>());
		}
		return font(size, maps.get(path), path + ".ttf", path + "_" + size
				+ ".fnt", false);
	}

}
