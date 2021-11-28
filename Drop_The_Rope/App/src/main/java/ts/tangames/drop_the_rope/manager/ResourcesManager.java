/**
 *  @author	Tanguy SOTO
 * 	@date	25/12/15
 * 
 * 	Singleton de gestion des ressources (load/unload) et contenant
 *  les références vers les objets les plus courants (camera, context, engine,
 *  ertexBufferObjectManager).	
 */
package ts.tangames.drop_the_rope.manager;

import android.graphics.Color;

import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.Engine;
import org.andengine.engine.camera.SmoothCamera;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.debug.Debug;

import java.io.IOException;

import ts.tangames.drop_the_rope.activity.GameActivity;

public class ResourcesManager {

	//---------------------------------------------
	// VARIABLES
	//---------------------------------------------

	private static final ResourcesManager INSTANCE = new ResourcesManager();

	public Engine engine;
	public PhysicsWorld physicsWorld;
	public GameActivity activity;
	public SmoothCamera camera;
	public VertexBufferObjectManager vbom;

	//---------------------------------------------
	// TEXTURES & TEXTURE REGIONS
	//---------------------------------------------

	// SplashScreen Texture et TextureRegion
	private BuildableBitmapTextureAtlas splashTextureAtlas;
	public ITextureRegion splash_region;

	// Menu Texture
	private BuildableBitmapTextureAtlas menuTextureAtlas;
	private BuildableBitmapTextureAtlas menuTextureAtlas2;

	// Menu Texture Regions
	public ITextureRegion menu_background_region;
	public ITextureRegion play_region;

	public ITextureRegion shop_region;
	public ITextureRegion shop_region2;
	public ITextureRegion sound_region;
	public ITextureRegion leaderboard_region;
	public ITextureRegion like_region;
	public ITextureRegion watch_region;
	public ITextureRegion about_region;
	public ITextureRegion how_to_play_region;
	public ITextureRegion menu_gems_region;

	public Font font;
	public Font goldFont;

	private boolean firstLoad = true;

	// Game Texture Regions
	private BuildableBitmapTextureAtlas gameTextureAtlas;
	public ITiledTextureRegion circle_region;

	public ITiledTextureRegion player_region;
	public ITiledTextureRegion player_small_region;
	public ITiledTextureRegion player_big_region;
	public ITiledTextureRegion player_big_target_region;
	public ITiledTextureRegion player_gold_region;
	public ITiledTextureRegion player_silver_region;

	public ITiledTextureRegion leaf_region;
	public ITiledTextureRegion target_region;
	public ITiledTextureRegion platform_region;
	public ITiledTextureRegion gems_region;
	public ITiledTextureRegion gems_collection_effect_region;

	public ITiledTextureRegion resume_region;
	public ITiledTextureRegion retry_region;
	public ITiledTextureRegion home_region;

	// Game HUD
	private BuildableBitmapTextureAtlas gameHUDTextureAtlas;

	// Game Background
	private BuildableBitmapTextureAtlas gameBackTextureAtlas;
	public ITextureRegion game_background;
	public ITextureRegion child_scene_background;
	public ITextureRegion child_scene_background2;

	//---------------------------------------------
	// SOUNDS
	//---------------------------------------------

	public Sound coin_sound;
	public Sound button_sound;
	public Sound game_over_sound;
	public Sound score_sound;
	public Sound throw_sound;
	public Sound fourInARow_sound;

	//---------------------------------------------
	// LOAD/UNLOAD
	//---------------------------------------------

	public void loadMenuResources() {
		loadMenuGraphics();
		loadMenuAudio();
		loadMenuFonts();
	}

	public void loadGameResources() {
		loadGameGraphics();
		loadGameFonts();
		loadGameAudio();
	}

	public void loadSplashResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		splashTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 512, 512,
				TextureOptions.BILINEAR);

		splash_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(splashTextureAtlas,
				activity, "splash.png");

		try {
			splashTextureAtlas
					.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(
							1, 1, 1));
			splashTextureAtlas.load();
		} catch (final TextureAtlasBuilderException e) {
			Debug.e(e);
		}
	}

	//---------------------------------------------
	// SPLASH

	public void unloadSplashResources() {
		splashTextureAtlas.unload();
		splash_region = null;
	}

	//---------------------------------------------
	// MENU

	private void loadMenuGraphics() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/");
		menuTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024,
				1024, TextureOptions.BILINEAR);
		menuTextureAtlas2 = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 760,
				760, TextureOptions.BILINEAR);
		menu_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				menuTextureAtlas, activity, "background.png");
		play_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas,
				activity, "play2.png");
		shop_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas,
				activity, "cart.png");
		shop_region2 = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas,
				activity, "purchase.png");
		sound_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas,
				activity, "sound.png");
		leaderboard_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas,
				activity, "reward.png");
		like_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas,
				activity, "like.png");
		watch_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas,
				activity, "fastforward.png");
		about_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas2,
				activity, "unknown3.png");
		menu_gems_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas,
				activity, "coin.png");
		how_to_play_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas,
				activity, "how_to_play_button.png");
		child_scene_background = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas2,
				activity, "background_game_over_child_scene.png");

		try {
			this.menuTextureAtlas
					.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(
							1, 1, 1));
			this.menuTextureAtlas.load();
			this.menuTextureAtlas2
					.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(
							1, 1, 1));
			this.menuTextureAtlas2.load();
		} catch (final TextureAtlasBuilderException e) {
			Debug.e(e);
		}
	}

	private void loadMenuFonts() {
		FontFactory.setAssetBasePath("fonts/");
		final ITexture mainFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 450,
				450, TextureOptions.BILINEAR);

		font = FontFactory.createStrokeFromAsset(activity.getFontManager(), mainFontTexture,
				activity.getAssets(), "libel-suit-rg.ttf", 80, true,Color.BLACK, 1f,Color.BLACK);
		font.load();

		final ITexture goldFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 350,
				350, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

		goldFont = FontFactory.createStrokeFromAsset(activity.getFontManager(), goldFontTexture,
				activity.getAssets(), "libel-suit-rg.ttf", 80, true,Color.YELLOW, 1.5f,Color.YELLOW);
		goldFont.load();
	}

	private void loadMenuAudio() {
		SoundFactory.setAssetBasePath("sounds/");

		try{
			coin_sound = SoundFactory.createSoundFromAsset(engine.getSoundManager(), activity, "coin.mp3");
			game_over_sound = SoundFactory.createSoundFromAsset(engine.getSoundManager(), activity, "game_over.mp3");
			button_sound = SoundFactory.createSoundFromAsset(engine.getSoundManager(), activity, "button.mp3");
			score_sound = SoundFactory.createSoundFromAsset(engine.getSoundManager(), activity, "rar_deleted.mp3");
			throw_sound = SoundFactory.createSoundFromAsset(engine.getSoundManager(), activity, "throw.mp3");
			fourInARow_sound = SoundFactory.createSoundFromAsset(engine.getSoundManager(), activity, "zip_touched.mp3");
		}
		catch (IOException e){
			e.printStackTrace();
		}

		setVolume();
	}

	public void setVolume(){
		coin_sound.setVolume(0.9f);
		game_over_sound.setVolume(0.15f);
		button_sound.setVolume(0.5f);
		score_sound.setVolume(0.8f);
		throw_sound.setVolume(0.47f);
		fourInARow_sound.setVolume(0.4f);
	}

	public void mute(){
		coin_sound.setVolume(0);
		game_over_sound.setVolume(0);
		button_sound.setVolume(0);
		score_sound.setVolume(0);
		throw_sound.setVolume(0);
		fourInARow_sound.setVolume(0);
	}

	public void loadMenuTextures() {
		menuTextureAtlas.load();
	}

	public void unloadMenuTextures() {
		menuTextureAtlas.unload();
	}

	public void unloadMenuResources() {
		unloadMenuTextures();
		menu_background_region = null;
		play_region = null;
		shop_region = null;
	}

	//---------------------------------------------
	// GAME

	private void loadGameGraphics() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/");

		// object
		gameTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024,
				1024, TextureOptions.BILINEAR);

		circle_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas,
				activity, "circle.png",1,1);

		player_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
				gameTextureAtlas, activity, "player_3.png", 2, 1);
		player_small_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
				gameTextureAtlas, activity, "player_small_3.png", 2, 1);
		player_big_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
				gameTextureAtlas, activity, "player_big_3.png", 2, 1);
		player_big_target_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
				gameTextureAtlas, activity, "player_big_target_3.png", 2, 1);
		player_gold_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
				gameTextureAtlas, activity, "player_gold_3.png", 2, 1);
		player_silver_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
				gameTextureAtlas, activity, "player_silver_3.png", 2, 1);

		leaf_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
				gameTextureAtlas, activity, "leaf.png", 1, 1);
		target_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
				gameTextureAtlas, activity, "target.png", 1, 1);
		platform_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
				gameTextureAtlas, activity, "platform.png", 1, 1);
		gems_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas,
				activity, "coin.png", 1, 1);
		gems_collection_effect_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas,
				activity, "coin_collection_effect.png", 1, 1);

		resume_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
				gameTextureAtlas, activity, "next.png", 1, 1);
		retry_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas,
				activity, "refresh3.png", 1, 1);
		home_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas,
				activity, "home.png", 1, 1);

		child_scene_background2 = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas,
				activity, "background_pause_child_scene.png", 1, 1);

		try {
			this.gameTextureAtlas
					.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(
							1, 1, 1));
			this.gameTextureAtlas.load();
		} catch (final TextureAtlasBuilderException e) {
			Debug.e(e);
		}

		// background
		gameBackTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 900,
				500, TextureOptions.DEFAULT	);
		game_background = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameBackTextureAtlas,
				activity, "background.png");

		try {
			this.gameBackTextureAtlas
					.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(
							0, 1, 0));
			this.gameBackTextureAtlas.load();
		} catch (final TextureAtlasBuilderException e) {
			Debug.e(e);
		}

		// hud
		gameHUDTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024,
				1024, TextureOptions.DEFAULT);
		try {
			this.gameHUDTextureAtlas
					.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(
							0, 1, 0));
			this.gameHUDTextureAtlas.load();
		} catch (final TextureAtlasBuilderException e) {
			Debug.e(e);
		}

		firstLoad = false;
	}

	private void loadGameFonts() {

	}

	private void loadGameAudio() {

	}

	public void loadGameTextures() {
		gameTextureAtlas.load();
		gameBackTextureAtlas.load();
		gameHUDTextureAtlas.load();
	}

	public void unloadGameTextures() {
		gameTextureAtlas.unload();
		gameBackTextureAtlas.unload();
		gameHUDTextureAtlas.unload();
	}

	// pas implémenté car les TextureRegions sont souvent réutilisées
	public void unloadGameResources() {

	}

	/**
	 * @param engine
	 * @param activity
	 * @param camera
	 * @param vbom
	 * <br><br>
	 * We use this method at beginning of game loading, to prepare Resources Manager properly,
	 * setting all needed parameters, so we can latter access them from different classes (eg. scenes)
	 */
	public static void prepareManager(Engine engine, GameActivity activity, SmoothCamera camera,
			VertexBufferObjectManager vbom) {
		getInstance().engine = engine;
		getInstance().activity = activity;
		getInstance().camera = camera;
		getInstance().vbom = vbom;
	}

	//---------------------------------------------
	// GETTERS AND SETTERS
	//---------------------------------------------

	public static ResourcesManager getInstance() {
		return INSTANCE;
	}

	public boolean isFirstLoad() {
		return firstLoad;
	}
}
