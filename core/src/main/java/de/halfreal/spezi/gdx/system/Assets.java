package de.halfreal.spezi.gdx.system;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasSprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.GdxRuntimeException;

import de.halfrel.spezi.gdx.framework.AbstractScreen;

public class Assets {

	public enum ACTION {
		attack,
		attack_short,
		idle,
		idle_short
	}

	public enum E {
		beat_enemy;

		private ParticleModifier modifier;
		private String path;
		private String secondary;

		private E() {
			this(null);
		}

		private E(String secondary) {
			this.secondary = secondary;
		}

		private void checkPath() {
			String name = name();

			path = Assets.PARTICLE_EFFECT_FOLDER + name;
		}

		public ParticleModifier getModifier() {

			if (modifier == null) {
				modifier = ParticleModifier.createParticleModifier(getPath());
			}

			return modifier;
		}

		public String getPath() {
			if (path == null) {
				checkPath();
			}

			return path;
		}

		public E getSecondary() {
			if (secondary == null) {
				return null;
			}
			return valueOf(secondary);
		}

	}

	/**
	 * All effects are ment to be shown on the left side, hence alignRigth
	 * describes to show an Effect on the right side of the Object.
	 * 
	 * valid modifieres:
	 * 
	 * righ, left, top, bottom - align the animation on those sides
	 * 
	 * x{pixel}, y{pixel} - an absolute offset
	 * 
	 * s{factor} in percent - scale, no comma values
	 * 
	 * @author Simon Joecks halfreal.de (c)
	 * 
	 */
	public static class ParticleModifier {
		public static ParticleModifier createParticleModifier(String fileName) {
			ParticleModifier modifier = new ParticleModifier();
			String[] modifiers = fileName.split("\\.");
			for (int i = 1; i < modifiers.length; i++) {
				String modifierToken = modifiers[i];
				if (modifierToken == null || modifierToken.length() == 0) {
					continue;
				}

				if (modifierToken.equals("left")) {
					modifier.alignLeft = true;
				} else

				if (modifierToken.equals("right")) {
					modifier.alignRight = true;
				} else

				if (modifierToken.equals("top")) {
					modifier.alignTop = true;
				} else

				if (modifierToken.equals("bottom")) {
					modifier.alignBottom = true;
				} else

				if (modifierToken.matches("x(\\+|-)?\\d+")) {
					modifier.offsetX = Integer.parseInt(modifierToken
							.substring(1));
				} else

				if (modifierToken.matches("y(\\+|-)?\\d+")) {
					modifier.offsetY = Integer.parseInt(modifierToken
							.substring(1));
				} else

				if (modifierToken.matches("s\\d+")) {
					modifier.scale = Integer.parseInt(modifierToken
							.substring(1)) / 100f;
				}

			}
			return modifier;
		}

		public boolean alignBottom;
		public boolean alignLeft;
		public boolean alignRight;
		public boolean alignTop;
		public int offsetX;
		public int offsetY;
		public float scale;
	}

	public enum R {
		// background_logo,
		// box,
		// halfreal_logo,
		// loading_fancy,
		pixel;
		// wait_circle;

		private Boolean isNinePatch = null;

		private String path = null;

		public String getPath() {
			if (path == null) {
				loadPath();
			}

			return path;
		}

		public boolean isNinePatch() {
			if (isNinePatch == null) {
				loadPath();
			}
			return isNinePatch;
		}

		private void loadPath() {
			String name = name() + ".png";

			path = AbstractScreen.getGFXFolder() + name;

			if (!Gdx.files.internal(path).exists()) {
				log.error("No fitting resulution for " + name
						+ ", different resulution must be loaded!");
				path = Assets.DRAWABLE_MDPI + name;
				if (Gdx.files.internal(path).exists()) {
					return;
				}
				path = Assets.DRAWABLE_HDPI + name;
				if (Gdx.files.internal(path).exists()) {
					return;
				}
				path = Assets.DRAWABLE_LDPI + name;
				if (Gdx.files.internal(path).exists()) {
					return;
				}

				path = Assets.DRAWABLE_XHDPI + name;
				if (Gdx.files.internal(path).exists()) {
					return;
				}
			} else {
				return;
			}

			// check ninepatches
			name = name() + ".9.png";
			path = AbstractScreen.getGFXFolder() + name;

			if (!Gdx.files.internal(path).exists()) {
				log.error("No fitting resulution for " + name
						+ ", different resulution must be loaded!");
				path = Assets.DRAWABLE_MDPI + name;
				if (Gdx.files.internal(path).exists()) {
					return;
				}
				path = Assets.DRAWABLE_HDPI + name;
				if (Gdx.files.internal(path).exists()) {
					return;
				}
				path = Assets.DRAWABLE_LDPI + name;
				if (Gdx.files.internal(path).exists()) {
					return;
				}

				path = Assets.DRAWABLE_XHDPI + name;
				if (Gdx.files.internal(path).exists()) {
					return;
				}
			} else {
				return;
			}

			throw new RuntimeException(
					"Cannot find any matching file for constant: " + name());
		}
	}

	private static String DEFAULT_SKIN_ATLAS = "defaultSkin.atlas";

	private static String DEFAULT_SKIN_JSON = "defaultSkin.json";

	public static final String DRAWABLE = "drawable/";

	public static final String DRAWABLE_HDPI = "drawable-hdpi/";

	public static final String DRAWABLE_LDPI = "drawable-ldpi/";

	public static final String DRAWABLE_MDPI = "drawable-mdpi/";

	public static final String DRAWABLE_XHDPI = "drawable-xhdpi/";

	public static final String DRAWABLE_XXHDPI = "drawable-xxhdpi/";

	// public static void loadFightViewAssets() {
	// loadTextures(R.fight_panel_left, R.fight_panel_right,
	// R.background_purple, R.action_circle, R.mw_icon_truc);
	// }
	//
	// public static void loadMapAssets() {
	// loadTextures(R.circle_a, R.circle, R.icon_current_sector, R.select,
	// R.background_generic, R.lower_layer_generic,
	// R.title_line_enemy, R.division_line, R.spec_box_enemy,
	// R.title_line_me, R.spec_box_me, R.arrow_left, R.arrow_right);
	// }
	private static Logger log = LoggerFactory.getLogger(Assets.class);

	private static AssetManager manager;
	public static final String PARTICLE_EFFECT_ATLAS_NAME = "particleImages.atlas";
	public static final String PARTICLE_EFFECT_FOLDER = "particle/";
	private static Map<String, E> particleEffectMap;
	private static HashMap<String, R> resourceMap;

	public static final String STYLE_SELECTOR_BUTTON = "chooserButton";

	static {
		manager = new AssetManager();
		manager.setLoader(SpeziSkin.class, new SpeziSkinLoader(
				new InternalFileHandleResolver()));

		if (AbstractScreen.getDensity() <= 1.5f) {
			DEFAULT_SKIN_ATLAS = DRAWABLE_HDPI + DEFAULT_SKIN_ATLAS;
		}

		if (AbstractScreen.getDensity() > 2f) {
			DEFAULT_SKIN_ATLAS = DRAWABLE_XXHDPI + DEFAULT_SKIN_ATLAS;
		}

	}

	public static <T extends Actor> T color(T actor, Color color) {
		actor.setColor(color);
		return actor;
	}

	/**
	 * corrects the origin
	 * 
	 * @param image
	 */
	public static <T extends Actor> T correctOrigin(T image) {
		image.setOrigin(image.getWidth() / 2, image.getHeight() / 2);
		return image;
	}

	public static <T extends Image> T correctOrigin(T image) {
		image.setOrigin(image.getWidth() / 2, image.getHeight() / 2);
		if (image.getDrawable().getClass() == SpriteDrawable.class) {

			Sprite sprite = ((SpriteDrawable) image.getDrawable()).getSprite();
			sprite.setOrigin(sprite.getWidth() / 2, sprite.getHeight() / 2);

		}
		return image;
	}

	public static float dip(float dp) {
		return AbstractScreen.dip(dp);
	}

	public static int dip(int dp) {
		return AbstractScreen.dip(dp);
	}

	public static void dispose() {
		try {
			getManager().clear();
			// FontHelper.clear();
		} catch (RuntimeException ignored) {

		}
	}

	public static String getAnimationName(String cyborgName) {
		return "animations_" + cyborgName.toLowerCase().split(" ")[0]
				+ ".atlas";
	}

	public static String getAnimationPath(String attackBlueprint) {
		return getPath(getAnimationName(attackBlueprint));
	}

	public static AssetManager getManager() {
		return manager;
	}

	public static E getParticleEffect(ProgramPhase phase) {

		// for (E value : E.values()) {
		// if (value.name().contains(phase.getProgram())) {
		// return value;
		// }
		// }

		String key = phase.getProgram() + "_"
				+ phase.getType().name().toLowerCase();

		E effect = particleEffectMap.get(key);
		if (effect != null) {
			return effect;
		}

		return E.valueOf(key);

	}

	public static String getPath(String name) {

		String path = AbstractScreen.getGFXFolder() + name;
		if (!Gdx.files.internal(path).exists()) {
			log.error("No fitting resulution for " + name
					+ ", different resulution must be loaded!");
			path = Assets.DRAWABLE_MDPI + name;
			if (Gdx.files.internal(path).exists()) {
				return path;
			}
			path = Assets.DRAWABLE_HDPI + name;
			if (Gdx.files.internal(path).exists()) {
				return path;
			}
			path = Assets.DRAWABLE_LDPI + name;
			if (Gdx.files.internal(path).exists()) {
				return path;
			}
			path = Assets.DRAWABLE_XHDPI + name;
			if (Gdx.files.internal(path).exists()) {
				return path;
			}
			throw new RuntimeException(
					"Cannot find any matching file for constant: " + name);
		}
		return path;
	}

	public static float getRelativeScaleFactor(String name) {
		float factor = AbstractScreen.getRoundDensity();
		String path = AbstractScreen.getGFXFolder() + name;
		if (!Gdx.files.internal(path).exists()) {
			path = Assets.DRAWABLE_MDPI + name;
			if (Gdx.files.internal(path).exists()) {
				return factor / 1f;
			}
			path = Assets.DRAWABLE_HDPI + name;
			if (Gdx.files.internal(path).exists()) {
				return factor / 1.5f;
			}
			path = Assets.DRAWABLE_LDPI + name;
			if (Gdx.files.internal(path).exists()) {
				return factor / 0.75f;
			}
			path = Assets.DRAWABLE_XHDPI + name;
			if (Gdx.files.internal(path).exists()) {
				return factor / 2f;
			}

		}
		return 1.0f;

	}

	public static R getResource(String key) {
		if (resourceMap.containsKey(key)) {
			return resourceMap.get(key);
		}

		try {
			return R.valueOf(key);
		} catch (RuntimeException e) {
			Gdx.app.error("ResourceHelper", "no resource named '" + key
					+ "' found. Forgot to put it in the resource map?");
		}
		throw new RuntimeException("R MUST BE IMPLEMENTED AGAIN");
		// return R.cpu_osi1;
	}

	public static Skin getSkin() {
		manager.finishLoading();
		return manager.get(DEFAULT_SKIN_JSON, SpeziSkin.class);
	}

	public static Skin getSkin(Class<?> class1) {
		// if (class1.equals(ST.class)) {
		// return getSkin();
		// } else if (class1.equals(ST_SPLASH.class)) {
		//
		// if (skinSplash == null) {
		// manager.finishLoading();
		// skinSplash = manager.get("skins/splashSkin.json",
		// EafosSkin.class);
		// skinSplash.add("pixel", texture(R.pixel), Texture.class);
		// }
		// return skinSplash;
		//
		// }
		// TODO
		return getSkin();
	}

	public static Image image(R texture) {
		return new Image(texture(texture));
	}

	public static boolean isLoading() {
		return manager.getProgress() < 1;
	}

	private static void loadNinepatch(R ninepatch) {
		loadTexture(ninepatch);
	}

	public static void loadParticleEffectImages() {
		manager.load(getPath(PARTICLE_EFFECT_ATLAS_NAME), TextureAtlas.class);
	}

	public static void loadSkin() {
		AssetLoaderParameters<SpeziSkin> skinParameter = new SpeziSkinLoader.SkinParameter(
				DEFAULT_SKIN_ATLAS, 1.0f);

		Assets.getManager().load(DEFAULT_SKIN_JSON, SpeziSkin.class,
				skinParameter);
	}

	private static void loadTexture(R texture) {
		manager.load(texture.getPath(), Texture.class);
	}

	public static void loadTextureAtlas(String path) {
		manager.load(path, TextureAtlas.class);
	}

	public static void loadTextures(R... names) {
		for (R name : names) {
			loadTexture(name);
		}
	}

	public static Sprite newSprite(AtlasRegion region) {
		if (region.packedWidth == region.originalWidth
				&& region.packedHeight == region.originalHeight) {
			if (region.rotate) {
				Sprite sprite = new Sprite(region);
				sprite.setBounds(0, 0, region.getRegionHeight(),
						region.getRegionWidth());
				sprite.rotate90(true);
				return sprite;
			}
			return new Sprite(region);
		}
		return new AtlasSprite(region);
	}

	public static NinePatch ninepatch(R name) {

		Texture texture = texture(name);
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		String[] patches = name.getPath().split("\\.");
		NinePatch ninePatch;
		// nine patch : name.9.left.right.top.bottom.png
		if (patches.length == 7) {
			int left = Integer.parseInt(patches[2]);
			int right = Integer.parseInt(patches[3]);
			int top = Integer.parseInt(patches[4]);
			int bottom = Integer.parseInt(patches[5]);
			ninePatch = new NinePatch(texture, left, right, top, bottom);
		} else {
			ninePatch = new NinePatch(texture);
		}

		return ninePatch;
	}

	public static NinePatchDrawable ninepatchDrawable(R name) {
		return new NinePatchDrawable(ninepatch(name));
	}

	public static ParticleEffect particleEffect(E effect) {

		ParticleEffect particleEffect = new ParticleEffect();

		particleEffect.load(Gdx.files.internal(effect.getPath()), manager.get(
				getPath(PARTICLE_EFFECT_ATLAS_NAME), TextureAtlas.class));

		return particleEffect;

	}

	public static void resume() {
		Texture.setAssetManager(manager);

	}

	public static void scale(Drawable drawable, Skin skin) {
		if (drawable != null && skin instanceof SpeziSkin) {
			float scaleFactor = ((SpeziSkin) skin).getScaleFactor();
			if (scaleFactor != 0 && scaleFactor != 1.0f) {

				drawable.setMinHeight(drawable.getMinHeight() * scaleFactor);
				drawable.setMinWidth(drawable.getMinWidth() * scaleFactor);
				drawable.setBottomHeight(drawable.getBottomHeight()
						* scaleFactor);
				drawable.setLeftWidth(drawable.getLeftWidth() * scaleFactor);
				drawable.setRightWidth(drawable.getRightWidth() * scaleFactor);
				drawable.setTopHeight(drawable.getTopHeight() * scaleFactor);
			}
		}
	}

	public static void scale(Sprite sprite, Skin skin) {

		if (sprite != null && skin instanceof SpeziSkin) {
			float scaleFactor = ((SpeziSkin) skin).getScaleFactor();
			if (scaleFactor != 0 && scaleFactor != 1.0f) {
				sprite.setScale(scaleFactor);
			}
		}
	}

	public static float scaleFactor(Skin skin) {

		if (skin != null && skin instanceof SpeziSkin) {
			float scaleFactor = ((SpeziSkin) skin).getScaleFactor();
			if (scaleFactor != 0 && scaleFactor != 1.0f) {
				return scaleFactor;
			}
		}
		return 1f;
	}

	public static <T extends Actor> T scaleImageH(T image, float height) {

		float originalHeight = image.getHeight();
		float originalWidth = image.getWidth();

		image.setHeight(height);
		image.setWidth(height * (originalWidth / originalHeight));
		return image;
	}

	public static <T extends Actor> T scaleImageW(T image, float width) {

		float originalHeight = image.getHeight();
		float originalWidth = image.getWidth();

		image.setWidth(width);
		image.setHeight(width * (originalHeight / originalWidth));
		return image;
	}

	public static Texture texture(R name) {
		return texture(name, true);
	}

	public static Texture texture(R name, boolean linearFilter) {
		return texture(name, linearFilter, true);
	}

	public static Texture texture(R name, boolean linearFilter, boolean loading) {

		if (!manager.isLoaded(name.getPath(), Texture.class)) {
			Gdx.app.error("ResourceHelper", "loadTexture(R." + name.name()
					+ ")");
			TextureParameter param = new TextureParameter();
			if (linearFilter) {
				param.magFilter = TextureFilter.Linear;
				param.minFilter = TextureFilter.Linear;
			}
			manager.load(name.getPath(), Texture.class, param);
			manager.finishLoading();
		}

		Texture texture = manager.get(name.getPath(), Texture.class);
		if (linearFilter && texture != null) {
			texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		}
		return texture;
	}

	public static void unloadSkin() {
		try {
			getManager().unload(DEFAULT_SKIN_JSON);
		} catch (GdxRuntimeException ignored) {

		}
	}

	public static boolean update() {
		return manager.update();
	}
}
