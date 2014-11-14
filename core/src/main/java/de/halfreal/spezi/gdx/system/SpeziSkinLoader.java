package de.halfreal.spezi.gdx.system;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;

public class SpeziSkinLoader extends
AsynchronousAssetLoader<SpeziSkin, SpeziSkinLoader.SkinParameter> {

	static public class SkinParameter extends AssetLoaderParameters<SpeziSkin> {
		private float scaleFactor = 1.0f;

		public final String textureAtlasPath;

		public SkinParameter(String textureAtlasPath) {
			this.textureAtlasPath = textureAtlasPath;
		}

		public SkinParameter(String textureAtlasPath, float scaleFactor) {
			this.textureAtlasPath = textureAtlasPath;
			this.scaleFactor = scaleFactor;
		}

		public float getScaleFactor() {
			return scaleFactor;
		}

	}

	public SpeziSkinLoader(FileHandleResolver resolver) {
		super(resolver);
	}

	@Override
	public Array<AssetDescriptor> getDependencies(String fileName,
			FileHandle file, SkinParameter parameter) {
		Array<AssetDescriptor> deps = new Array<AssetDescriptor>();
		if (parameter == null) {
			deps.add(new AssetDescriptor(resolve(fileName)
					.pathWithoutExtension() + ".atlas", TextureAtlas.class));
		} else {
			deps.add(new AssetDescriptor(parameter.textureAtlasPath,
					TextureAtlas.class));
		}
		return deps;
	}

	@Override
	public void loadAsync(AssetManager manager, String fileName,
			FileHandle file, SkinParameter parameter) {
		// TODO Auto-generated method stub

	}

	@Override
	public SpeziSkin loadSync(AssetManager manager, String fileName,
			FileHandle file, SkinParameter parameter) {
		String textureAtlasPath;
		if (parameter == null) {
			textureAtlasPath = resolve(fileName).pathWithoutExtension()
					+ ".atlas";
		} else {
			textureAtlasPath = parameter.textureAtlasPath;
		}
		TextureAtlas atlas = manager.get(textureAtlasPath, TextureAtlas.class);
		return new SpeziSkin(resolve(fileName), atlas,
				parameter.getScaleFactor());
	}

}
