package de.halfreal.spezi.gdx.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasSprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.ReadOnlySerializer;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.SerializationException;

import de.halfrel.spezi.gdx.framework.AbstractScreen;

public class SpeziSkin extends Skin {

	private static Logger log = LoggerFactory.getLogger(SpeziSkin.class);
	private float scaleFactor = 1.0f;

	public SpeziSkin(FileHandle resolve, TextureAtlas atlas, float scaleFactor) {
		super(resolve, atlas);
		this.scaleFactor = scaleFactor;
	}

	/**
	 * Adds all named txeture regions from the atlas. The atlas will not be
	 * automatically disposed when the skin is disposed.
	 */
	@Override
	public void addRegions(TextureAtlas atlas) {
		Array<AtlasRegion> regions = atlas.getRegions();
		for (int i = 0, n = regions.size; i < n; i++) {
			AtlasRegion region = regions.get(i);
			if (region.name.contains("/")) {
				String[] split = region.name.split("\\/");
				region.name = split[split.length - 1];
			}
			add(region.name, region, TextureRegion.class);
		}
	}

	public int fontSize(Float size) {
		int dpSize = (int) AbstractScreen.dip(size);
		if (dpSize % 2 != 0) {
			dpSize -= 1;
		}
		return dpSize;
	}

	@Override
	public <T> T get(Class<T> type) {
		GUIProfiler.instance().assetAccess("default", type);
		return super.get(type);
	}

	@Override
	public <T> T get(String name, Class<T> type) {
		GUIProfiler.instance().assetAccess(name, type);
		return super.get(name, type);
	}

	@Override
	public Color getColor(String name) {
		GUIProfiler.instance().assetAccess(name, Color.class);
		return super.getColor(name);
	}

	/**
	 * Returns a registered drawable. If no drawable is found but a region,
	 * ninepatch, or sprite exists with the name, then the appropriate drawable
	 * is created and stored in the skin.
	 */
	@Override
	public Drawable getDrawable(String name) {
		GUIProfiler.instance().assetAccess(name, Drawable.class);
		Drawable drawable = optional(name, Drawable.class);
		if (drawable != null) {
			return drawable;
		}

		drawable = optional(name, TiledDrawable.class);
		if (drawable != null) {
			return drawable;
		}

		// Use texture or texture region. If it has splits, use ninepatch. If it
		// has rotation or whitespace stripping, use sprite.
		try {
			TextureRegion textureRegion = getRegion(name);
			if (textureRegion instanceof AtlasRegion) {
				AtlasRegion region = (AtlasRegion) textureRegion;
				if (region.splits != null) {
					drawable = new NinePatchDrawable(getPatch(name));
				} else if (region.rotate
						|| region.packedWidth != region.originalWidth
						|| region.packedHeight != region.originalHeight) {
					drawable = new SpriteDrawable(getSprite(name));
				}
			}
			if (drawable == null) {
				drawable = new TextureRegionDrawable(textureRegion);
			}
		} catch (GdxRuntimeException ignored) {
		}

		// Check for explicit registration of ninepatch, sprite, or tiled
		// drawable.
		if (drawable == null) {
			NinePatch patch = optional(name, NinePatch.class);
			if (patch != null) {
				drawable = new NinePatchDrawable(patch);
			} else {
				Sprite sprite = optional(name, Sprite.class);
				if (sprite != null) {
					drawable = new SpriteDrawable(sprite);
				} else {
					throw new GdxRuntimeException(
							"No Drawable, NinePatch, TextureRegion, Texture, or Sprite registered with name: "
									+ name);
				}
			}
		}

		if (scaleFactor != 0 && scaleFactor != 1.0f) {
			drawable.setMinHeight(drawable.getMinHeight() * scaleFactor);
			drawable.setMinWidth(drawable.getMinWidth() * scaleFactor);
			drawable.setBottomHeight(drawable.getBottomHeight() * scaleFactor);
			drawable.setLeftWidth(drawable.getLeftWidth() * scaleFactor);
			drawable.setRightWidth(drawable.getRightWidth() * scaleFactor);
			drawable.setTopHeight(drawable.getTopHeight() * scaleFactor);

		}

		add(name, drawable, Drawable.class);
		return drawable;
	}

	@Override
	public BitmapFont getFont(String name) {
		GUIProfiler.instance().assetAccess(name, BitmapFont.class);
		return super.getFont(name);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Json getJsonLoader(final FileHandle skinFile) {
		final Skin skin = this;

		Json json = super.getJsonLoader(skinFile);

		json.setSerializer(Color.class, new ReadOnlySerializer<Color>() {

			@Override
			public Color read(Json json, JsonValue jsonData, Class type) {
				if (jsonData.isString()) {
					return get(jsonData.asString(), Color.class);
				}
				String hexa = jsonData.getString("hex", null);
				if (hexa != null) {
					return Color.valueOf(hexa);
				} else {
					float r = json.readValue("r", float.class, 0f, jsonData);
					float g = json.readValue("g", float.class, 0f, jsonData);
					float b = json.readValue("b", float.class, 0f, jsonData);
					float a = json.readValue("a", float.class, 1f, jsonData);
					return new Color(r, g, b, a);
				}
			}
		});

		json.setSerializer(Skin.class, new ReadOnlySerializer<Skin>() {

			@Override
			public Skin read(Json json, JsonValue typeToValueMap, Class ignored) {
				for (JsonValue valueMap = typeToValueMap.child(); valueMap != null; valueMap = valueMap
						.next()) {
					try {
						readNamedObjects(json, Class.forName(valueMap.name()),
								valueMap);
					} catch (ClassNotFoundException ex) {
						throw new SerializationException(ex);
					}
				}
				return skin;
			}

			private void readNamedObjects(Json json, Class type, JsonValue data) {
				Class addType = type.equals(TintedDrawable.class)
						|| type.equals(LayeredNinePatch.class) ? Drawable.class
						: type;

				for (JsonValue valueEntry = data.child(); valueEntry != null; valueEntry = valueEntry
						.next()) {
					Object object = json.readValue(type, valueEntry);
					if (object == null) {
						continue;
					}
					try {
						add(valueEntry.name(), object, addType);
					} catch (Exception ex) {
						throw new SerializationException("Error reading "
								+ type.getSimpleName() + ": "
								+ valueEntry.name(), ex);
					}
				}
			}
		});

		json.setSerializer(BitmapFont.class,
				new ReadOnlySerializer<BitmapFont>() {

					@Override
					public BitmapFont read(Json json, JsonValue data, Class type) {

						boolean noFontFile = false;
						String path = data.getString("file");
						// DP Value of the font
						Float size = data.getFloat("size");
						FileHandle fontFile = skinFile.parent().child(path);

						if (!fontFile.exists()) {
							int dpSize = fontSize(size);
							fontFile = Gdx.files.internal(path + "_" + dpSize
									+ ".fnt");
						}

						if (!fontFile.exists()) {
							noFontFile = true;
							log.error("Font file not found: " + fontFile
									+ ", must generate one");

							// throw new
							// RuntimeException("Font file not found: "
							// + fontFile + ", must generate one");
						}

						if (!noFontFile) {
							// Use a region with the same name as the font, else
							// use
							// a PNG file in the same directory as the FNT file.
							String regionName = fontFile.nameWithoutExtension();
							try {
								TextureRegion region = skin.optional(
										regionName, TextureRegion.class);
								if (region != null) {
									return new BitmapFont(fontFile, region,
											false);
								} else {
									FileHandle imageFile = fontFile.parent()
											.child(regionName + ".png");
									if (imageFile.exists()) {
										return new BitmapFont(fontFile,
												imageFile, false);
									} else {
										return new BitmapFont(fontFile, false);
									}
								}
							} catch (RuntimeException ex) {
								throw new SerializationException(
										"Error loading bitmap font: "
												+ fontFile, ex);
							}
						} else {
							// return FontHelper.getFont(path, fontSize(size));
							return FontHelper.generateFont(path + ".ttf",
									fontSize(size), null);
						}
					}
				});

		json.setSerializer(LayeredNinePatch.class, new ReadOnlySerializer() {
			@Override
			public Object read(Json json, JsonValue jsonData, Class type) {

				String name = json.readValue("base", String.class, jsonData);
				Integer cornerId = json.readValue("cornerId", Integer.class,
						jsonData);
				String corner = json
						.readValue("corner", String.class, jsonData);

				Drawable baseDrawable = newDrawable(name);
				Drawable cornerDrawable = newDrawable(corner);

				return new LayeredNinePatchDrawable(
						(NinePatchDrawable) baseDrawable, cornerDrawable,
						cornerId);
			}
		});

		return json;
	}

	@Override
	public NinePatch getPatch(String name) {
		GUIProfiler.instance().assetAccess(name, NinePatch.class);
		NinePatch patch = super.getPatch(name);
		return patch;
	}

	@Override
	public TextureRegion getRegion(String name) {
		GUIProfiler.instance().assetAccess(name, TextureRegion.class);
		return super.getRegion(name);
	}

	public float getScaleFactor() {
		return scaleFactor;
	}

	@Override
	public Sprite getSprite(String name) {
		GUIProfiler.instance().assetAccess(name, Sprite.class);
		return super.getSprite(name);
	}

	@Override
	public TiledDrawable getTiledDrawable(String name) {
		GUIProfiler.instance().assetAccess(name, TiledDrawable.class);
		return super.getTiledDrawable(name);
	}

	@Override
	public Drawable newDrawable(Drawable drawable, Color tint) {
		return newDrawable(drawable, tint, false, false);
	}

	/**
	 * Returns a tinted copy of a drawable found in the skin via
	 * {@link #getDrawable(String)}.
	 */
	public Drawable newDrawable(Drawable drawable, Color tint, boolean flipX,
			boolean flipY) {
		if (drawable instanceof TextureRegionDrawable) {
			TextureRegion region = ((TextureRegionDrawable) drawable)
					.getRegion();
			Sprite sprite;
			if (region instanceof AtlasRegion) {
				sprite = new AtlasSprite((AtlasRegion) region);
			} else {
				sprite = new Sprite(region);
			}
			sprite.setColor(tint);
			sprite.flip(flipX, flipY);

			if (scaleFactor != 1.0f && scaleFactor != 0) {
				sprite.setScale(scaleFactor);
			}

			return new SpriteDrawable(sprite);
		}
		if (drawable instanceof NinePatchDrawable) {
			NinePatch patch = ((NinePatchDrawable) drawable).getPatch();
			float padLeft = patch.getPadLeft();
			float padRight = patch.getPadRight();
			float padTop = patch.getPadTop();
			float padBottom = patch.getPadBottom();
			// float bottomHeight = patch.getBottomHeight();
			// float topHeight = patch.getTopHeight();
			// float leftWidth = patch.getLeftWidth();
			// float rightWidth = patch.getRightWidth();
			//
			// // TODO commit fixes to libgdx github
			// // Skin.newDrawabe(drawabe, tint) does not work correctly
			// // PatchDrawable is not correct
			NinePatch newPatch = new NinePatch(patch, tint);
			newPatch.setPadding((int) padLeft, (int) padRight, (int) padTop,
					(int) padBottom);
			// patchDrawable.getPatch().setBottomHeight(bottomHeight);
			// patchDrawable.getPatch().setTopHeight(topHeight);
			// patchDrawable.getPatch().setLeftWidth(leftWidth);
			// patchDrawable.getPatch().setRightWidth(rightWidth);
			return new NinePatchDrawable(newPatch);
		}
		if (drawable instanceof SpriteDrawable) {
			SpriteDrawable spriteDrawable = new SpriteDrawable(
					(SpriteDrawable) drawable);
			Sprite sprite = spriteDrawable.getSprite();
			if (sprite instanceof AtlasSprite) {
				sprite = new AtlasSprite((AtlasSprite) sprite);
			} else {
				sprite = new Sprite(sprite);
			}
			sprite.flip(flipX, flipY);
			sprite.setColor(tint);
			spriteDrawable.setSprite(sprite);
			if (scaleFactor != 1.0f && scaleFactor != 0) {
				sprite.setScale(scaleFactor);
			}
			return spriteDrawable;
		}
		throw new GdxRuntimeException("Unable to copy, unknown drawable type: "
				+ drawable.getClass());
	}

	private void scale(TextureRegion region) {
		// TODO Auto-generated method stub

	}
}
