/**
 *  @author	Tanguy SOTO
 * 	@date	25/12/15
 * 
 * 	Scène affichant le menu (scène avec fond + MenuScene en child)
 */

package ts.tangames.drop_the_rope.scene;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.jirbo.adcolony.AdColony;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.MoveYModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.TextMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.TextOptions;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.modifier.ease.EaseSineInOut;
import org.andengine.util.modifier.ease.EaseSineOut;

import ts.tangames.drop_the_rope.activity.GameActivity;
import ts.tangames.drop_the_rope.activity.PlayerActivity;
import ts.tangames.drop_the_rope.activity.RopeActivity;
import ts.tangames.drop_the_rope.base.BaseScene;
import ts.tangames.drop_the_rope.factory.PlayerType;
import ts.tangames.drop_the_rope.factory.RopeType;
import ts.tangames.drop_the_rope.manager.ConfigManager;
import ts.tangames.drop_the_rope.manager.ResourcesManager;
import ts.tangames.drop_the_rope.manager.SceneManager;
import ts.tangames.drop_the_rope.manager.SceneManager.SceneType;
import ts.tangames.ventix.R;

public class MainMenuScene extends BaseScene implements IOnMenuItemClickListener {


	// ---------------------------------
	// VARIABLES
	// ---------------------------------

	private MenuScene menuChildScene;

	// ITEMS DU MENU
	protected IMenuItem playMenuItem;
	protected IMenuItem chooseRopeMenuItem;
	protected IMenuItem choosePlayerMenuItem;
	protected IMenuItem likeMenuItem;
	protected IMenuItem soundMenuItem;
	protected IMenuItem leaderBoardMenuItem;
	protected IMenuItem gemsImageMenuItem;
	protected IMenuItem aboutMenuItem;

	protected TextMenuItem gemsMenuItem;

	protected ScaleMenuItemDecorator watchVideoMenuItem;
	protected TextMenuItem watchVideoTextMenuItem;
	protected SpriteMenuItem watchVideoDecoratorMenuItem;

	protected TextMenuItem bestScoreMenuItem;

	private final int MENU_PLAY = 0;
	private final int MENU_CHOOSE_ROPE = 1;
	private final int MENU_CHOOSE_PLAYER = 2;
	private final int MENU_LIKE = 3;
	private final int MENU_ENABLE_SOUND = 4;
	private final int MENU_LEADERBOARD = 5;
	private final int MENU_GEMS = 6;
	private final int MENU_WATCH_VIDEO = 7;
	private final int MENU_BEST_SCORE = 8;
	private final int MENU_ABOUT = 9;

	private final float animateInDuration = 0.45f;
	private final float animateOutDuration = 0.45f;

	protected boolean isInGrandChildScene =false;


	// ---------------------------------
	// CREATION METHODS
	// ---------------------------------

	@Override
	public void createScene() {
		createBackground();
		createMenuChildScene();

		Log.d("menu",""+getActivity().getResources().getDisplayMetrics().density);
	}

	private void createBackground() {
		attachChild(new Sprite(GameActivity.getWidth() / 2, GameActivity.getHeight() / 2,
				resourcesManager.menu_background_region, vbom));
	}

	// utilise MenuScene de AndEngine
	private void createMenuChildScene() {
		menuChildScene = new MenuScene(camera);
		menuChildScene.setPosition(0, 0);

		if(ConfigManager.getInstance().getParameterInt(ConfigManager.INT_ROPE_TYPE)==-1){
			ConfigManager.getInstance().setParameter(ConfigManager.INT_ROPE_TYPE, RopeType.NORMAL_ROPE.ordinal());
			ConfigManager.getInstance().setParameter(ConfigManager.INT_PLAYER_TYPE, PlayerType.BASIC_PLAYER.ordinal());
			ConfigManager.getInstance().setParameter(ConfigManager.INT_TOTAL_GEM,0);
			ConfigManager.getInstance().setParameter(ConfigManager.INT_BEST_SCORE,0);
			ConfigManager.getInstance().setParameter(ConfigManager.STR_SOUND_ENABLED,"true");

		}

		// Boutons
		playMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_PLAY,
				resourcesManager.play_region, vbom), 1f, 1.1f);
		chooseRopeMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(
				MENU_CHOOSE_ROPE, resourcesManager.shop_region, vbom), 0.30f, 0.35f);
		choosePlayerMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(
				MENU_CHOOSE_PLAYER, resourcesManager.shop_region2, vbom), 0.30f, 0.35f);
		likeMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(
				MENU_LIKE, resourcesManager.like_region, vbom), 0.30f, 0.35f);
		soundMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(
				MENU_ENABLE_SOUND, resourcesManager.sound_region, vbom), 0.30f, 0.35f);
		leaderBoardMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(
				MENU_LEADERBOARD, resourcesManager.leaderboard_region, vbom), 0.30f, 0.35f);
		gemsImageMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(
				MENU_GEMS,resourcesManager.menu_gems_region,vbom),0.35f,0.35f);
		aboutMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(
				MENU_ABOUT,resourcesManager.about_region,vbom),0.18f,0.22f);

		watchVideoMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(
				MENU_WATCH_VIDEO, resourcesManager.watch_region, vbom), 0.30f, 0.30f);
		watchVideoTextMenuItem = new TextMenuItem(MENU_WATCH_VIDEO,resourcesManager.font,
				"+"+getActivity().getResources().getInteger(R.integer.adcolony_gems_reward),vbom);
		watchVideoTextMenuItem.setScale(0.25f);
		watchVideoDecoratorMenuItem = new SpriteMenuItem(MENU_WATCH_VIDEO,
				ResourcesManager.getInstance().menu_gems_region, vbom);
		watchVideoDecoratorMenuItem.setScale(0.2f);

		int gems = ConfigManager.getInstance().getParameterInt(ConfigManager.INT_TOTAL_GEM);
		gemsMenuItem = new TextMenuItem(MENU_GEMS, ResourcesManager.getInstance().font,
				"0123456789",new TextOptions(HorizontalAlign.RIGHT),vbom);
		gemsMenuItem.setText(""+gems);
		gemsMenuItem.setAnchorCenter(0,0);
		gemsMenuItem.setScale(0.53f);

		int best = ConfigManager.getInstance().getParameterInt(ConfigManager.INT_BEST_SCORE);
		bestScoreMenuItem = new TextMenuItem(MENU_BEST_SCORE,ResourcesManager.getInstance().font,": 01234456789   ",new TextOptions(HorizontalAlign.LEFT),
				vbom);
		bestScoreMenuItem.setText(getActivity().getString(R.string.best_score)+" : "+best);
		bestScoreMenuItem.setAnchorCenter(0,0);
		bestScoreMenuItem.setScale(0.43f);

		menuChildScene.addMenuItem(playMenuItem);
		menuChildScene.addMenuItem(chooseRopeMenuItem);
		menuChildScene.addMenuItem(choosePlayerMenuItem);
		menuChildScene.addMenuItem(likeMenuItem);
		menuChildScene.addMenuItem(soundMenuItem);
		menuChildScene.addMenuItem(leaderBoardMenuItem);
		menuChildScene.addMenuItem(gemsImageMenuItem);
		menuChildScene.addMenuItem(aboutMenuItem);

		menuChildScene.addMenuItem(gemsMenuItem);

		menuChildScene.addMenuItem(watchVideoMenuItem);
		menuChildScene.addMenuItem(watchVideoTextMenuItem);
		menuChildScene.addMenuItem(watchVideoDecoratorMenuItem);

		menuChildScene.addMenuItem(bestScoreMenuItem);

		menuChildScene.buildAnimations();
		menuChildScene.setBackgroundEnabled(false);

		playMenuItem.setPosition(GameActivity.getWidth() / 2, 580);
		chooseRopeMenuItem.setPosition(GameActivity.getWidth() / 2, -100);
		choosePlayerMenuItem.setPosition(GameActivity.getWidth() / 2 + 130, -100);
		likeMenuItem.setPosition(GameActivity.getWidth() / 2 - 130, -100);
		soundMenuItem.setPosition(GameActivity.getWidth() / 2 - 260, -100);
		if("false".equals(ConfigManager.getInstance().getParameter(ConfigManager.STR_SOUND_ENABLED))){
			soundMenuItem.setAlpha(0.6f);
			ResourcesManager.getInstance().mute();
		}
		else {
			ResourcesManager.getInstance().setVolume();
		}

		leaderBoardMenuItem.setPosition(GameActivity.getWidth() / 2 + 260, -100);
		gemsImageMenuItem.setPosition(758,580);
		aboutMenuItem.setPosition(80,580);

		gemsMenuItem.setPosition(720-gemsMenuItem.getWidth()/2,580);

		watchVideoMenuItem.setPosition(730,360);
		watchVideoMenuItem.setVisible(getActivity().getLastAvailable());
		watchVideoTextMenuItem.setPosition(714,310);
		watchVideoTextMenuItem.setVisible(getActivity().getLastAvailable());
		watchVideoDecoratorMenuItem.setPosition(749,310);
		watchVideoDecoratorMenuItem.setVisible(getActivity().getLastAvailable());

		bestScoreMenuItem.setPosition(15,580);

		menuChildScene.setOnMenuItemClickListener(this);

		// Initialize AdColony
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				AdColony.configure(getActivity(), "version:0.1,store:google",
						getActivity().getString(R.string.adcolony_app_id),
						getActivity().getString(R.string.adcolony_zone_id));
				AdColony.addAdAvailabilityListener(getActivity());
			}
		});

		SceneManager.getInstance().showAdView(getActivity());

		setChildScene(menuChildScene);
	}


	// ---------------------------------
	// CALLBACK METHODS
	// ---------------------------------

	@Override
	public void onBackKeyPressed() {
		Log.d("MainMenuScene","ONBACKPRESSED");

		if(isInGrandChildScene){
			detachGrandChildScene();
		}
		else {
			SceneManager.getInstance().disposeMenuScene();
			getActivity().exit();
		}
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_MENU;
	}

	@Override
	public void disposeScene() {
		menuChildScene.detachSelf();
		menuChildScene.dispose();
		menuChildScene=null;
		this.detachSelf();
		this.dispose();
	}

	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,
									 float pMenuItemLocalX, float pMenuItemLocalY) {
		switch (pMenuItem.getID()) {
			case MENU_PLAY:
				ResourcesManager.getInstance().button_sound.play();

				attachModeChildScene();
				return true;
			case MENU_CHOOSE_ROPE:
				SceneManager.getInstance().showInterstitial(getActivity());
				ResourcesManager.getInstance().button_sound.play();
				Intent intent = new Intent(this.getActivity(), RopeActivity.class);
				myStartActivity(intent);
				return true;
			case MENU_CHOOSE_PLAYER:
				SceneManager.getInstance().showInterstitial(getActivity());
				ResourcesManager.getInstance().button_sound.play();
				Intent intent2 = new Intent(this.getActivity(), PlayerActivity.class);
				myStartActivity(intent2);
				return true;
			case MENU_LIKE:
				ResourcesManager.getInstance().button_sound.play();
				btnRateAppOnClick();
				return true;
			case MENU_ENABLE_SOUND:
				switchSound();
				return true;
			case MENU_LEADERBOARD:
				ResourcesManager.getInstance().button_sound.play();
				showLeaderBoard();
				return true;
			case MENU_WATCH_VIDEO:
				getActivity().watchVideo();
				return true;
			case MENU_ABOUT:
				ResourcesManager.getInstance().button_sound.play();
				attachAboutChildScene();
				return true;
			default:
				return false;
		}
	}


	// ---------------------------------
	// METHODS
	// ---------------------------------

	public void detachGrandChildScene(){
		menuChildScene.clearChildScene();
		isInGrandChildScene =false;
	}

	private void attachModeChildScene(){
		isInGrandChildScene=true;
		menuChildScene.setChildScene(new ModeChildScene(camera,this),false,true,true);
	}

	private void attachAboutChildScene(){
		isInGrandChildScene =true;
		menuChildScene.setChildScene(new AboutChildScene(camera,this),false,true,true);
	}


	// Catch les exceptions si aucune activité ne peut lancer l'intent
	private boolean myStartActivity(Intent aIntent) {
		try {
			getActivity().startActivity(aIntent);
			return true;
		} catch (ActivityNotFoundException e) {
			return false;
		}
	}

	// mets à jour l'affichage des gems
	public void majDisplayedGems(){
		gemsMenuItem.setText(""+ConfigManager.getInstance().getParameterInt(ConfigManager.INT_TOTAL_GEM));
		gemsMenuItem.setPosition(720-gemsMenuItem.getWidth()/2f,415);

		int bestScore = ConfigManager.getInstance().getParameterInt(ConfigManager.INT_BEST_SCORE);
		bestScoreMenuItem.setText(getActivity().getString(R.string.best_score)+
				" : "+bestScore);

	}

	public void majVideoButton(boolean avalaible){
		watchVideoDecoratorMenuItem.setVisible(avalaible);
		watchVideoTextMenuItem.setVisible(avalaible);
		watchVideoMenuItem.setVisible(avalaible);
	}

	public void animateIn(){
		registerUpdateHandler(new TimerHandler(0.5f, new ITimerCallback() {
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				playMenuItem.clearEntityModifiers();
				playMenuItem.registerEntityModifier
						(new SequenceEntityModifier(
								new MoveYModifier(animateInDuration,580,GameActivity.getHeight() / 2
										+ GameActivity.getHeight() / 5, EaseSineOut.getInstance()),
								new LoopEntityModifier(
										new SequenceEntityModifier(
												new MoveYModifier(1.3f,GameActivity.getHeight() / 2
														+ GameActivity.getHeight() / 5,GameActivity.getHeight() / 2
															+ GameActivity.getHeight() / 5+15, EaseSineInOut.getInstance()),
												new MoveYModifier(1.3f,GameActivity.getHeight() / 2
														+ GameActivity.getHeight() / 5 + 15,GameActivity.getHeight() / 2
														+ GameActivity.getHeight() / 5, EaseSineInOut.getInstance())))
						));
				chooseRopeMenuItem.registerEntityModifier(
						new SequenceEntityModifier(
							new MoveYModifier(animateInDuration-0.2f,-100,GameActivity.getHeight() / 2
									- GameActivity.getHeight() / 6, EaseSineOut.getInstance()),
							new LoopEntityModifier(
									new SequenceEntityModifier(
											new MoveYModifier(9.5f,GameActivity.getHeight() / 2
											- GameActivity.getHeight() / 6,GameActivity.getHeight() / 2- GameActivity.getHeight() / 6),
											new MoveYModifier(0.1f,GameActivity.getHeight() / 2
													- GameActivity.getHeight() / 6,GameActivity.getHeight() / 2- GameActivity.getHeight() / 6-7),
											new MoveYModifier(0.1f,GameActivity.getHeight() / 2
													- GameActivity.getHeight() / 6-7,GameActivity.getHeight() / 2- GameActivity.getHeight() / 6),
											new MoveYModifier(0.1f,GameActivity.getHeight() / 2
													- GameActivity.getHeight() / 6,GameActivity.getHeight() / 2- GameActivity.getHeight() / 6-7),
											new MoveYModifier(0.1f,GameActivity.getHeight() / 2
													- GameActivity.getHeight() / 6-7,GameActivity.getHeight() / 2- GameActivity.getHeight() / 6))
								)));
				choosePlayerMenuItem.registerEntityModifier(new SequenceEntityModifier(
						new MoveYModifier(animateInDuration-0.2f,-100,GameActivity.getHeight() / 2
								- GameActivity.getHeight() / 6, EaseSineOut.getInstance()),
						new LoopEntityModifier(
								new SequenceEntityModifier(
										new MoveYModifier(9f,GameActivity.getHeight() / 2
												- GameActivity.getHeight() / 6,GameActivity.getHeight() / 2- GameActivity.getHeight() / 6),
										new MoveYModifier(0.1f,GameActivity.getHeight() / 2
												- GameActivity.getHeight() / 6,GameActivity.getHeight() / 2- GameActivity.getHeight() / 6-7),
										new MoveYModifier(0.1f,GameActivity.getHeight() / 2
												- GameActivity.getHeight() / 6-7,GameActivity.getHeight() / 2- GameActivity.getHeight() / 6),
										new MoveYModifier(0.1f,GameActivity.getHeight() / 2
												- GameActivity.getHeight() / 6,GameActivity.getHeight() / 2- GameActivity.getHeight() / 6-7),
										new MoveYModifier(0.1f,GameActivity.getHeight() / 2
												- GameActivity.getHeight() / 6-7,GameActivity.getHeight() / 2- GameActivity.getHeight() / 6))
						)));
				likeMenuItem.registerEntityModifier(new MoveYModifier(animateInDuration-0.3f,-100,GameActivity.getHeight() / 2
						- GameActivity.getHeight() / 6, EaseSineOut.getInstance()));
				soundMenuItem.registerEntityModifier(new MoveYModifier(animateInDuration-0.4f,-100,GameActivity.getHeight() / 2
						- GameActivity.getHeight() / 6, EaseSineOut.getInstance()));
				leaderBoardMenuItem.registerEntityModifier(new MoveYModifier(animateInDuration,-100,GameActivity.getHeight() / 2
						- GameActivity.getHeight() / 6, EaseSineOut.getInstance()));

				SequenceEntityModifier loop = new SequenceEntityModifier(
						new MoveYModifier(animateInDuration,630,360),
						new LoopEntityModifier(
							new SequenceEntityModifier(
									new ParallelEntityModifier(
											new RotationModifier(animateInDuration,0,15),
											new ScaleModifier(animateInDuration,0.25f,0.19f)),
									new ParallelEntityModifier(
											new RotationModifier(animateInDuration,15,0),
											new ScaleModifier(animateInDuration,0.19f,0.25f)),
									new ParallelEntityModifier(
											new RotationModifier(animateInDuration,0,-15),
											new ScaleModifier(animateInDuration,0.25f,0.19f)),
									new ParallelEntityModifier(
											new RotationModifier(animateInDuration,-15,0),
											new ScaleModifier(animateInDuration,0.19f,0.25f)))));
				watchVideoMenuItem.registerEntityModifier(loop);
				watchVideoDecoratorMenuItem.registerEntityModifier(new MoveYModifier(animateInDuration,580,310));
				watchVideoTextMenuItem.registerEntityModifier(new MoveYModifier(animateInDuration,580,310));

				bestScoreMenuItem.registerEntityModifier(new MoveYModifier(animateInDuration,580,425));
				gemsMenuItem.registerEntityModifier(new MoveYModifier(animateInDuration,580,415));
				gemsImageMenuItem.registerEntityModifier(new MoveYModifier(animateInDuration,580,443));
				aboutMenuItem.registerEntityModifier(new MoveYModifier(animateInDuration,580,360));
			}
		}));
	}

	public void animateOut(){
		playMenuItem.clearEntityModifiers();
		playMenuItem.registerEntityModifier(
				new MoveYModifier(animateOutDuration,GameActivity.getHeight() / 2
								+ GameActivity.getHeight() / 5, 580, EaseSineOut.getInstance()));
		chooseRopeMenuItem.clearEntityModifiers();
		chooseRopeMenuItem.registerEntityModifier(new MoveYModifier(animateOutDuration-0.2f,GameActivity.getHeight() / 2
				- GameActivity.getHeight() / 6, -100, EaseSineOut.getInstance()));
		choosePlayerMenuItem.clearEntityModifiers();
		choosePlayerMenuItem.registerEntityModifier(new MoveYModifier(animateOutDuration-0.1f,GameActivity.getHeight() / 2
				- GameActivity.getHeight() / 6, -100, EaseSineOut.getInstance()));
		likeMenuItem.registerEntityModifier(new MoveYModifier(animateOutDuration-0.3f,GameActivity.getHeight() / 2
				- GameActivity.getHeight() / 6, -100, EaseSineOut.getInstance()));
		soundMenuItem.registerEntityModifier(new MoveYModifier(animateOutDuration-0.4f,GameActivity.getHeight() / 2
				- GameActivity.getHeight() / 6, -100, EaseSineOut.getInstance()));
		leaderBoardMenuItem.registerEntityModifier(new MoveYModifier(animateOutDuration,GameActivity.getHeight() / 2
				- GameActivity.getHeight() / 6, -100, EaseSineOut.getInstance()));

		watchVideoMenuItem.clearEntityModifiers();
		watchVideoTextMenuItem.clearEntityModifiers();
		watchVideoDecoratorMenuItem.clearEntityModifiers();

		watchVideoMenuItem.registerEntityModifier(new MoveYModifier(animateOutDuration,360,630));
		watchVideoDecoratorMenuItem.registerEntityModifier(new MoveYModifier(animateOutDuration,310,580));
		watchVideoTextMenuItem.registerEntityModifier(new MoveYModifier(animateOutDuration,310,580));

		bestScoreMenuItem.registerEntityModifier(new MoveYModifier(animateOutDuration,425,580));
		gemsMenuItem.registerEntityModifier(new MoveYModifier(animateOutDuration,415,580));
		gemsImageMenuItem.registerEntityModifier(new MoveYModifier(animateOutDuration,443,580));
		aboutMenuItem.registerEntityModifier(new MoveYModifier(animateInDuration,360,580));
	}

	private void showLeaderBoard(){
		GoogleApiClient mGoogleApiClient = getActivity().getGoogleApiClient();
		if(mGoogleApiClient!=null && mGoogleApiClient.isConnected()){
			//getActivity().startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient,
			//		getActivity().getString(R.string.leaderboard_normal_score)), 42);
			getActivity().startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(mGoogleApiClient),42);
		}
		else {
			if(mGoogleApiClient!=null) {
				ConfigManager.getInstance().setParameter(ConfigManager.STR_CANCELLED_SIGN_IN,"false");
				mGoogleApiClient.connect();
			}
		}
	}

	//On click event for "rate this app" button
	private void btnRateAppOnClick() {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(getActivity().getString(R.string.google_play_url)));
		if (!myStartActivity(intent)) {
			//Market (Google play) app seems not installed, let's try to open a webbrowser
			intent.setData(Uri.parse(getActivity().getString(R.string.http_url)));
			if (!myStartActivity(intent)) {
				//Well if this also fails, we have run out of options, inform the user.
				getActivity().toastOnUiThread(getActivity().getString(R.string.google_play_error),Toast.LENGTH_SHORT);
			}
		}
	}

	// Active et désactive les sons
	private void switchSound(){
		String isSoundEnabled = ConfigManager.getInstance().getParameter(ConfigManager.STR_SOUND_ENABLED);
		if("false".equals(isSoundEnabled)){
			ConfigManager.getInstance().setParameter(ConfigManager.STR_SOUND_ENABLED,"true");
			soundMenuItem.setAlpha(1);

			ResourcesManager.getInstance().setVolume();
			ResourcesManager.getInstance().button_sound.play();
		}
		else {
			ConfigManager.getInstance().setParameter(ConfigManager.STR_SOUND_ENABLED,"false");
			soundMenuItem.setAlpha(0.6f);
			ResourcesManager.getInstance().mute();
		}
	}


	// ---------------------------------
	// GETTERS / SETTERS
	// ---------------------------------

	public boolean isInGrandChildScene() {
		return isInGrandChildScene;
	}

	public float getAnimateOutDuration(){
		return animateOutDuration;
	}

	public MenuScene getMenuChildScene() {
		return menuChildScene;
	}
}
