/**
 *  @author	Tanguy SOTO
 * 	@date	25/12/15
 * 
 * 	Singleton de gestion des scènes du jeu
 * 	(changements de scènes, scène courante, type de scènes).
 */

package ts.tangames.drop_the_rope.manager;

import android.app.Activity;
import android.view.View;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import org.andengine.engine.Engine;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.ui.IGameInterface.OnCreateSceneCallback;

import ts.tangames.drop_the_rope.activity.GameActivity;
import ts.tangames.drop_the_rope.base.BaseScene;
import ts.tangames.drop_the_rope.scene.GameScene;
import ts.tangames.drop_the_rope.scene.LoadingScene;
import ts.tangames.drop_the_rope.scene.MainMenuScene;
import ts.tangames.drop_the_rope.scene.SplashScene;

public class SceneManager {
	//---------------------------------------------
	// SCENES
	//---------------------------------------------

	private BaseScene splashScene;
	private BaseScene menuScene;

	private BaseScene gameScene;

	private BaseScene loadingScene;

	private int n=2;
	private InterstitialAd mInterstitialAd;
	private AdView mAdView;

	//---------------------------------------------
	// VARIABLES
	//---------------------------------------------

	private static final SceneManager INSTANCE = new SceneManager();

	private SceneType currentSceneType = SceneType.SCENE_SPLASH;

	private BaseScene currentScene;

	private Engine engine;

	public enum SceneType {
		SCENE_SPLASH, SCENE_MENU, SCENE_GAME, SCENE_LOADING,SCENE_PAUSING
	}

	//---------------------------------------------
	// CHANGEMENT DE SCENE
	//---------------------------------------------

	public void setScene(BaseScene scene) {
		engine.setScene(scene);
		currentScene = scene;
		currentSceneType = scene.getSceneType();
	}

	public void setScene(SceneType sceneType) {
		switch (sceneType) {
		case SCENE_MENU:
			setScene(menuScene);
			break;
		case SCENE_GAME:
			setScene(gameScene);
			break;
		case SCENE_SPLASH:
			setScene(splashScene);
			break;
		case SCENE_LOADING:
			setScene(loadingScene);
			break;
		default:
			break;
		}
	}

	//---------------------------------------------
	// CREATION, CHANGEMENT ET SUPPRESSION DE SCENE
	//---------------------------------------------

	public void createSplashScene(OnCreateSceneCallback pOnCreateSceneCallback) {
		engine = ResourcesManager.getInstance().engine;
		ResourcesManager.getInstance().loadSplashResources();
		splashScene = new SplashScene();
		currentScene = splashScene;
		pOnCreateSceneCallback.onCreateSceneFinished(splashScene);
	}

	private void disposeSplashScene() {
		ResourcesManager.getInstance().unloadSplashResources();
		splashScene.disposeScene();
		splashScene = null;
	}

	public void createMenuScene() {
		ResourcesManager.getInstance().loadMenuResources();
		menuScene = new MainMenuScene();
		loadingScene = new LoadingScene();
		setScene(menuScene);
		((MainMenuScene)menuScene).animateIn();
		disposeSplashScene();

	}

	public void loadMenuScene(final Engine mEngine) {
		// must be done before setting loading scene
		ResourcesManager.getInstance().camera.setChaseEntity(null);
		ResourcesManager.getInstance().camera.setHUD(null);
		ResourcesManager.getInstance().camera.setBoundsEnabled(false);
		ResourcesManager.getInstance().camera.setZoomFactorDirect(1);
		ResourcesManager.getInstance().camera.setCenterDirect(GameActivity.getWidth() / 2,
				GameActivity.getHeight() / 2);
		
		setScene(loadingScene);
		gameScene.disposeScene();
		ResourcesManager.getInstance().unloadGameTextures();
		mEngine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback() {
			public void onTimePassed(final TimerHandler pTimerHandler) {
				mEngine.unregisterUpdateHandler(pTimerHandler);
				ResourcesManager.getInstance().loadMenuTextures();
				MainMenuScene scene = (MainMenuScene)menuScene;
				scene.majDisplayedGems();
				setScene(menuScene);
				scene.animateIn();
			}
		}));
	}

	public void disposeMenuScene() {
		ResourcesManager.getInstance().unloadMenuResources();
		menuScene.disposeScene();
		menuScene = null;
	}

	public void loadGameScene(final Engine mEngine) {
		MainMenuScene menu = ((MainMenuScene)menuScene);
		menu.animateOut();

		mEngine.registerUpdateHandler(new TimerHandler(((MainMenuScene)menuScene).getAnimateOutDuration(),
				new ITimerCallback() {
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				setScene(loadingScene);
				ResourcesManager.getInstance().unloadMenuTextures();
				mEngine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback() {
					public void onTimePassed(final TimerHandler pTimerHandler) {
						mEngine.unregisterUpdateHandler(pTimerHandler);
						if (ResourcesManager.getInstance().isFirstLoad()) {
							ResourcesManager.getInstance().loadGameResources();
						} else {
							ResourcesManager.getInstance().loadGameTextures();
						}
						gameScene = new GameScene();
						setScene(gameScene);
					}
				}));
			}
		}));

	}

	// Utilisée quand on clique sur le bouton rejouer
	public void reloadGameScene(final Engine mEngine){
		// must be done before setting loading scene
		ResourcesManager.getInstance().camera.setChaseEntity(null);
		ResourcesManager.getInstance().camera.setHUD(null);
		ResourcesManager.getInstance().camera.setBoundsEnabled(false);
		ResourcesManager.getInstance().camera.setZoomFactorDirect(1);
		ResourcesManager.getInstance().camera.setCenterDirect(GameActivity.getWidth() / 2,
				GameActivity.getHeight() / 2);

		setScene(loadingScene);
		gameScene.disposeScene();

		mEngine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback() {
			public void onTimePassed(final TimerHandler pTimerHandler) {
				mEngine.unregisterUpdateHandler(pTimerHandler);
				gameScene = new GameScene();
				setScene(gameScene);
			}
		}));
	}

	//---------------------------------------------
	// GETTERS AND SETTERS
	//---------------------------------------------

	public static SceneManager getInstance() {
		return INSTANCE;
	}

	public SceneType getCurrentSceneType() {
		return currentSceneType;
	}

	public BaseScene getCurrentScene() {
		return currentScene;
	}

	public MainMenuScene getMenuScene(){
		if(menuScene==null){
			return null;
		}
		return (MainMenuScene)menuScene;
	}

	public AdView getmAdView() {
		return mAdView;
	}

	public void setmAdView(AdView mAdView) {
		this.mAdView = mAdView;
	}

	public void showAdView(Activity a){
		a.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mAdView.setVisibility(View.VISIBLE);
			}
		});
	}

	public void hideAdView(Activity a){
		a.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mAdView.setVisibility(View.GONE);
			}
		});
	}

	public InterstitialAd getmInterstitialAd() {
		return mInterstitialAd;
	}

	public void setmInterstitialAd(InterstitialAd mInterstitialAd) {
		this.mInterstitialAd = mInterstitialAd;
	}

	public void showInterstitial(Activity a){
		n++;
		a.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(mInterstitialAd.isLoaded()){
					if(n%6==0) {
						mInterstitialAd.show();
					}
				}
			}
		});
	}
}
