/**
 *  @author	Tanguy SOTO
 * 	@date	25/12/15
 * 
 * 	Scène principale de jeu.
 */

package ts.tangames.drop_the_rope.scene;

import android.util.Log;
import android.widget.Switch;

import com.badlogic.gdx.math.Vector2;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.MoveYModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.AutoParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.modifier.ease.EaseBounceOut;
import org.andengine.util.modifier.ease.EaseCubicOut;
import org.andengine.util.modifier.ease.EaseLinear;

import java.util.ArrayList;

import ts.tangames.drop_the_rope.activity.GameActivity;
import ts.tangames.drop_the_rope.base.BaseScene;
import ts.tangames.drop_the_rope.factory.ModeType;
import ts.tangames.drop_the_rope.factory.PlayerDescriptor;
import ts.tangames.drop_the_rope.factory.PlayerDescriptorFactory;
import ts.tangames.drop_the_rope.factory.PlayerType;
import ts.tangames.drop_the_rope.factory.RopeDescriptor;
import ts.tangames.drop_the_rope.factory.RopeDescriptorFactory;
import ts.tangames.drop_the_rope.factory.RopeType;
import ts.tangames.drop_the_rope.manager.ConfigManager;
import ts.tangames.drop_the_rope.manager.Generateur;
import ts.tangames.drop_the_rope.manager.ResourcesManager;
import ts.tangames.drop_the_rope.manager.SceneManager;
import ts.tangames.drop_the_rope.manager.SceneManager.SceneType;
import ts.tangames.drop_the_rope.object.Gem;
import ts.tangames.drop_the_rope.object.Platform;
import ts.tangames.drop_the_rope.object.Player;
import ts.tangames.ventix.R;

public class GameScene extends BaseScene implements IOnSceneTouchListener {

	// ---------------------------------
	// VARIABLES
	// ---------------------------------

	protected boolean isGameOver = false;
	protected boolean isGoingBackToMainMenu = false;
	protected boolean isNewBestScore = false;
	protected boolean isPause = false;
	protected boolean isFirstPlatform = true;

	protected int tutorialStep;
	protected Text tutorialText;
	// préférences avant activation du tuto
	protected ModeType precMode;
	protected int precRopeType;
	protected int precPlayerType;

	// AndEngine objects
	private PhysicsWorld physicsWorld;
	private BaseHUD hud;

	// Game objects
	private Player player;

	private Long lastTimeThrow;
	private final int MIN_GAP_BETWEEN_ROPE = 400;

	private final int NB_MAX_PLATFORMS = 7;
	private ArrayList<Platform> platforms;

	private final int NB_MAX_GEMS = 12;
	private ArrayList<Gem> gems;

	private final int NB_MAX_LEAFS = 10;
	private final int NB_MIN_LEAFS = 5;
	private final float leafsAnimationDuration = 1.5f;
	private ArrayList<AnimatedSprite> leafs;

	private Text plusUn;

	private AnimatedSprite gems_collection_effect;
	private Text gemsPlusUn;
	private Text gems4InARow;

	private Text modeDescription;

	private int bestScore;
	private int score = 0;
	private int collectedGems = 0;
	// ---------------------------------
	// CONSTRUCTORS
	// ---------------------------------

	@Override
	public void createScene() {
		tutorialStep=-1;

		createBackground();
		createPhysics();
		createLevel();
		hud = new BaseHUD(this, camera);
		camera.setHUD(hud);
		camera.setMaxZoomFactorChange(GameActivity.MAX_ZOOM_FACTOR);
		camera.setBoundsEnabled(true);
		camera.setBounds(0,0,100000,GameActivity.getHeight());
		setOnSceneTouchListener(this);
	}

	private void createBackground() {

		final AutoParallaxBackground autoParallaxBackground = new AutoParallaxBackground(0, 0, 0, 5);
		autoParallaxBackground.attachParallaxEntity(new ParallaxEntity(0, new Sprite(GameActivity
				.getWidth() / 2, GameActivity.getHeight() / 2, resourcesManager.game_background, vbom)));

		setBackground(autoParallaxBackground);
	}

	private void createPhysics() {
		physicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0, -25), false);
		ResourcesManager.getInstance().physicsWorld = physicsWorld;
		registerUpdateHandler(physicsWorld);

		lastTimeThrow = System.currentTimeMillis();
	}

	private void createLevel() {
		Generateur.getInstance().reset();

		// premier lancement -> TUTORIAL
		if("not found".equals(ConfigManager.getInstance().getParameter(ConfigManager.STR_FIRST_LAUNCH))){
			precMode = Generateur.getInstance().getMode();
			precRopeType = ConfigManager.getInstance().getParameterInt(ConfigManager.INT_ROPE_TYPE);
			precPlayerType = ConfigManager.getInstance().getParameterInt(ConfigManager.INT_PLAYER_TYPE);

			Generateur.getInstance().setMode(ModeType.NORMAL);
			ConfigManager.getInstance().setParameter(ConfigManager.INT_ROPE_TYPE,RopeType.NORMAL_ROPE.ordinal());
			ConfigManager.getInstance().setParameter(ConfigManager.INT_PLAYER_TYPE,PlayerType.BASIC_PLAYER.ordinal());

			initTutorial();
			tutorial();
		}
		else {
			switch (Generateur.getInstance().getMode()){
				case EASY:
					modeDescription = new Text(550,350,ResourcesManager.getInstance().font,
							getActivity().getString(R.string.easy_desc),vbom);
					modeDescription.setScale(0.35f);
					attachChild(modeDescription);
					break;
				case DIFFICULT:
					modeDescription = new Text(600,350,ResourcesManager.getInstance().font,
							getActivity().getString(R.string.difficult_desc),vbom);
					modeDescription.setScale(0.35f);
					attachChild(modeDescription);
					break;
				default:
					break;
			}

		}

		// ===== création des plateformes
		platforms = new ArrayList<Platform>();

		for(int i=0; i<NB_MAX_PLATFORMS; i++){
			int t[] = Generateur.getInstance().nextPlateform(Platform.count);

			Platform p = new Platform(t[0],t[1],t[2],t[3],vbom,camera,physicsWorld,this);
			platforms.add(p);
			this.attachChild(p);
		}

		// ===== création des gems
		gems = new ArrayList<Gem>();
		for(int i=0; i<NB_MAX_GEMS; i++){
			int t[] = Generateur.getInstance().nextGem(Gem.count);

			Gem g = new Gem(t[0],t[1],vbom,camera,physicsWorld,this);
			gems.add(g);
			this.attachChild(g);
		}
		gems_collection_effect = new AnimatedSprite(0,0,
				ResourcesManager.getInstance().gems_collection_effect_region, vbom);
		gems_collection_effect.setVisible(false);
		gems_collection_effect.setScale(0.26f);
		this.attachChild(gems_collection_effect);

		gemsPlusUn = new Text(-15,-15,ResourcesManager.getInstance().goldFont,"+1",vbom);
		gemsPlusUn.setScale(0.28f);
		gemsPlusUn.setVisible(true);
		attachChild(gemsPlusUn);

		gems4InARow = new Text(-15,-50,ResourcesManager.getInstance().goldFont,getActivity().getString(R.string.four_in_a_row),vbom);
		gems4InARow.setScale(0.32f);
		gems4InARow.setVisible(true);
		attachChild(gems4InARow);

		// création du joueur
		ConfigManager conf = ConfigManager.getInstance();

		RopeType ropeType = RopeType.values()[conf.getParameterInt(ConfigManager.INT_ROPE_TYPE)];
		RopeDescriptor ropeDescriptor = RopeDescriptorFactory.createRopeDescriptor(ropeType);

		PlayerType playerType = PlayerType.values()[conf.getParameterInt(ConfigManager.INT_PLAYER_TYPE)];
		PlayerDescriptor playerDescriptor = PlayerDescriptorFactory.createRopeDescriptor(playerType);

		player = new Player(platforms.get(0).getX()-ropeDescriptor.width,platforms.get(0).getY()-ropeDescriptor.height,ropeDescriptor,playerDescriptor,vbom,camera,physicsWorld,this);
		player.throwRopeAnyWay();
		this.attachChild(player);

		// création du Text PlusUn
		plusUn = new Text(-15,-15,ResourcesManager.getInstance().font,"+1",vbom);
		plusUn.setScale(0.28f);
		plusUn.setVisible(true);
		attachChild(plusUn);

		// création des feuilles
		leafs=new ArrayList<AnimatedSprite>();
		for(int i=0;i<NB_MAX_LEAFS;i++){
			AnimatedSprite l = new AnimatedSprite(-100,-100,ResourcesManager.getInstance().leaf_region,vbom);
			l.setAlpha(0);
			l.setScale(0.25f);
			leafs.add(l);
			attachChild(l);
		}

		// récupération de meilleur score avant la partie
		bestScore = ConfigManager.getInstance().getParameterInt(ConfigManager.INT_BEST_SCORE);

		SceneManager.getInstance().hideAdView(getActivity());
	}

	// ---------------------------------
	// SCENE LIFE CYCLE
	// ---------------------------------

	@Override
	public void onBackKeyPressed() {
		// partie finie -> menu principal
		if(isGameOver && !isGoingBackToMainMenu){
			isGoingBackToMainMenu=true;
			SceneManager.getInstance().loadMenuScene(ResourcesManager.getInstance().engine);
		}
		// partie en cours
		else {
			// Déjà en pause -> on "dépause"
			if(isPause){
				unPauseScene();
			}
			else {
				ResourcesManager.getInstance().button_sound.play();
				pauseScene();
			}
		}
	}

	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		if(pSceneTouchEvent.isActionDown()) {

			if(tutorialStep==-1) {
				if (player.getRopes().size() <= 0) {
					Long t = System.currentTimeMillis();

					if (t - lastTimeThrow > MIN_GAP_BETWEEN_ROPE) {
						ResourcesManager.getInstance().throw_sound.play();
						player.throwRopeAnyWay();
						lastTimeThrow = t;
					}
				} else {
					player.detachRope();
				}
			}
		}

		return false;
	}

	public void pauseScene(){
		if(isGameOver){
			return;
		}
		majScore();
		isPause=true;
		SceneManager.getInstance().showAdView(getActivity());
		this.setChildScene(new PauseChildScene(camera,this),false,true,true);
	}

	public void unPauseScene(){
		SceneManager.getInstance().hideAdView(getActivity());
		this.clearChildScene();
		isPause=false;
	}

	public void endGame() {

		if(isGameOver){
			return;
		}
		isGameOver = true;

		// animation des feuilles
		camera.setChaseEntity(null);
		fallInLeaf();

		registerUpdateHandler(new TimerHandler(leafsAnimationDuration+0.01f, new ITimerCallback() {
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				majScore();
				GameScene.this.getHUD().setVisible(false);
				SceneManager.getInstance().showAdView(getActivity());
				GameScene.this.setChildScene(new GameOverChildScene(camera, GameScene.this, isNewBestScore), false, true, true);
			}
		}));
	}


	private void majScore(){
		// Maj des SharedPreferences
		ConfigManager.getInstance().setParameter(ConfigManager.INT_CURRENT_SCORE,score);
		ConfigManager.getInstance().setParameter(ConfigManager.INT_BEST_SCORE,bestScore);

		ConfigManager.getInstance().setParameter(ConfigManager.INT_COLLECTED_GEM,collectedGems);
		int gems = ConfigManager.getInstance().getParameterInt(ConfigManager.INT_TOTAL_GEM);
		gems+=collectedGems;
		ConfigManager.getInstance().setParameter(ConfigManager.INT_TOTAL_GEM,gems);

		// Maj du google play Games
		GoogleApiClient mGoogleApiClient = getActivity().getGoogleApiClient();
		if(mGoogleApiClient!=null && mGoogleApiClient.isConnected()){
			Games.Leaderboards.submitScore(mGoogleApiClient,
							getActivity().getString(R.string.leaderboard_normal_score), bestScore);

		}
	}

	@Override
	public void disposeScene() {

		// Destroys both physic and sprite
		player.destroyObject(physicsWorld);
		for (Platform p : platforms) {
			p.destroyObject(physicsWorld);
		}
		platforms.clear();

		engine.runOnUpdateThread(new Runnable() {
			public void run() {
				physicsWorld.clearForces();
				physicsWorld.clearPhysicsConnectors();
				physicsWorld.reset();
			}
		});

		hud.detachSelf();
		hud.dispose();

		this.clearUpdateHandlers();
		this.clearTouchAreas();
		this.detachSelf();
		this.dispose();
	}

	// ---------------------------------
	// GETTERS / SETTERS
	// ---------------------------------

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_GAME;
	}

	public BaseHUD getHUD() {
		return hud;
	}

	public AnimatedSprite getGemsCollectionEffect(){
		return gems_collection_effect;
	}

	public ArrayList<Platform> getPlatform() {
		return platforms;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player p) {
		player = p;
	}

	public void addToScore(int i,boolean animate) {
		if(hud!=null && !isFirstPlatform) {

			if(animate){
				resourcesManager.score_sound.play();
			}

			score += i;
			hud.setScore(score,animate);

			ModeType mode = Generateur.getInstance().getMode();

			if(bestScore<score && (mode==ModeType.NORMAL || mode==ModeType.DIFFICULT)){
				isNewBestScore=true;
				bestScore=score;
			}
		}
		isFirstPlatform=false;
	}

	public void addGemsScore(int i,float xG, float yG, boolean animate) {
		if(hud!=null) {

			if(animate){
				resourcesManager.fourInARow_sound.play();
			}
			else {
				resourcesManager.coin_sound.play();
			}

			collectedGems += i;
			animateGemsCollection(xG,yG);
			hud.setBonus(collectedGems,animate);
		}
	}

	// ---------------------------------
	// ANIMATIONS
	// ---------------------------------

	public void fallInLeaf(){
		float x = player.getX();
		float y = 0;
		float xDest = x-80;
		float yDest = Generateur.getInstance().nextInt(30,50);

		int n = Generateur.getInstance().nextInt(NB_MIN_LEAFS,NB_MAX_LEAFS);

		for(int i=0;i<n;i++){
			AnimatedSprite l = leafs.get(i);
			l.setScale((float) (Generateur.getInstance().nextDouble()*(0.3-0.15)+0.15));
			l.setAlpha(1);

			l.registerEntityModifier(
					new ParallelEntityModifier(
							new MoveModifier(leafsAnimationDuration,x,y,xDest,yDest+Generateur.getInstance().nextInt(20,70), EaseCubicOut.getInstance()),
							new AlphaModifier(leafsAnimationDuration,1,0),
							new RotationModifier(leafsAnimationDuration,l.getRotation(),
									l.getRotation()+Generateur.getInstance().nextInt(-100,100), EaseLinear.getInstance())
					){
						@Override
						protected void onModifierStarted(IEntity pItem){
							super.onModifierStarted(pItem);
							resourcesManager.game_over_sound.play();
						}
					}
			);

			xDest+=160/n+Generateur.getInstance().nextInt(0,30);
		}
	}

	public void animate4InARow(){
		Log.d("game","animate4InARow");

		final float x = player.getX();
		final float y = player.getY();

		gemsPlusUn.clearEntityModifiers();
		gemsPlusUn.registerEntityModifier(
				new SequenceEntityModifier(
						new ScaleModifier(0.6f,0.1f,0.28f, EaseBounceOut.getInstance()),
						new MoveYModifier(0.8f,y-45,y-45))
				{
					@Override
					protected void onModifierStarted(IEntity pItem){
						super.onModifierStarted(pItem);
						gemsPlusUn.setPosition(x+300,y-45);
						gemsPlusUn.setScale(0.1f);
						gemsPlusUn.setVisible(true);
					}

					@Override
					protected void onModifierFinished(IEntity pItem){
						super.onModifierFinished(pItem);
						gemsPlusUn.setVisible(false);
					}
				}
		);

		gems4InARow.clearEntityModifiers();
		gems4InARow.registerEntityModifier(
				new LoopEntityModifier(
					new SequenceEntityModifier(
							new ScaleModifier(0.6f,0,0.32f,EaseBounceOut.getInstance()),
							new MoveYModifier(0.8f,y-10,y-10)),1)
				{
					@Override
					protected void onModifierStarted(IEntity pItem){
						super.onModifierStarted(pItem);
						gems4InARow.setPosition(x+300,y-10);
						gems4InARow.setScale(0.1f);
						gems4InARow.setVisible(true);
					}

					@Override
					protected void onModifierFinished(IEntity pItem){
						super.onModifierFinished(pItem);
						gems4InARow.setVisible(false);
				}
		});
	}

	public void animatePlusUn(){
		final float x = player.getTarget().getX();
		final float y = player.getTarget().getY();

		plusUn.clearEntityModifiers();
		plusUn.registerEntityModifier(
				new SequenceEntityModifier(
						new MoveYModifier(0.9f,y+40,y+100),
						new AlphaModifier(0.5f,1,0)){
					@Override
					protected void onModifierStarted(IEntity pItem){
						super.onModifierStarted(pItem);
						plusUn.setPosition(x,y);
						plusUn.setAlpha(1);
						plusUn.setVisible(true);
					}

					@Override
					protected void onModifierFinished(IEntity pItem){
						super.onModifierFinished(pItem);
						plusUn.setVisible(false);
					}
				}
		);
	}

	private void animateGemsCollection(float xG, float yG){
		//gems_collection_effect.setPosition(xG, yG);
		gems_collection_effect.clearEntityModifiers();
		gems_collection_effect.registerEntityModifier(
				new ParallelEntityModifier(
						new ScaleModifier(0.11f,0.41f,0.43f+(float)Generateur.getInstance().nextDouble()/8),
						new AlphaModifier(0.11f,1,0.9f)){
					@Override
					protected void onModifierStarted(IEntity pItem){
						super.onModifierStarted(pItem);
						gems_collection_effect.setVisible(true);
					}

					@Override
					protected void onModifierFinished(IEntity pItem){
						super.onModifierFinished(pItem);
						gems_collection_effect.setVisible(false);
					}
				}
		);
	}


	// ---------------------------------
	// TUTORIAL
	// ---------------------------------

	public int getTutorialStep(){
		return tutorialStep;
	}

	private void initTutorial(){
		Generateur.getInstance().setSeed(Generateur.seed);
	}

	public void tutorial(){
		tutorialStep++;

		switch (tutorialStep){
			case 0:
				registerUpdateHandler(new TimerHandler(1.1f, new ITimerCallback() {
					@Override
					public void onTimePassed(TimerHandler pTimerHandler) {
						tutorialText = new Text(560,370,ResourcesManager.getInstance().font,
								getActivity().getString(R.string.tuto_step0),vbom);
						tutorialText.setScale(0.3f);
						attachChild(tutorialText);

						GameScene.this.setChildScene(new TutorialChildScene(camera,GameScene.this),false,true,true);
					}
				}));
				break;
			case 1:
				GameScene.this.clearChildScene();
				tutorialText.setVisible(false);
				player.detachRope();

				registerUpdateHandler(new TimerHandler(0.75f, new ITimerCallback() {
					@Override
					public void onTimePassed(TimerHandler pTimerHandler) {
						tutorialText = new Text(690,370,ResourcesManager.getInstance().font,
								getActivity().getString(R.string.tuto_step1),vbom);
						tutorialText.setScale(0.3f);
						attachChild(tutorialText);

						GameScene.this.setChildScene(new TutorialChildScene(camera,GameScene.this),false,true,true);
					}
				}));
				break;
			case 2:
				GameScene.this.clearChildScene();
				tutorialText.detachSelf();
				tutorialText.dispose();
				player.throwRopeAnyWay();

				registerUpdateHandler(new TimerHandler(0.77f, new ITimerCallback() {
					@Override
					public void onTimePassed(TimerHandler pTimerHandler) {
						tutorialText = new Text(890,370,ResourcesManager.getInstance().font,
								getActivity().getString(R.string.tuto_step2),vbom);
						tutorialText.setScale(0.3f);
						attachChild(tutorialText);

						GameScene.this.setChildScene(new TutorialChildScene(camera,GameScene.this),false,true,true);
					}
				}));
				break;
			case 3:
				GameScene.this.clearChildScene();
				tutorialText.detachSelf();
				tutorialText.dispose();
				player.detachRope();

				registerUpdateHandler(new TimerHandler(0.95f, new ITimerCallback() {
					@Override
					public void onTimePassed(TimerHandler pTimerHandler) {
						tutorialText = new Text(1100,370,ResourcesManager.getInstance().font,
								getActivity().getString(R.string.tuto_step3),vbom);
						tutorialText.setScale(0.3f);
						attachChild(tutorialText);

						GameScene.this.setChildScene(new TutorialChildScene(camera,GameScene.this),false,true,true);
					}
				}));
				break;
			case 4:
				GameScene.this.clearChildScene();
				tutorialText.detachSelf();
				tutorialText.dispose();
				player.throwRopeAnyWay();

				registerUpdateHandler(new TimerHandler(0.38f, new ITimerCallback() {
					@Override
					public void onTimePassed(TimerHandler pTimerHandler) {
						tutorialText = new Text(1160,380,ResourcesManager.getInstance().font,
								getActivity().getString(R.string.tuto_step4),vbom);
						tutorialText.setScale(0.3f);
						attachChild(tutorialText);

						GameScene.this.setChildScene(new TutorialChildScene(camera,GameScene.this),false,true,true);
					}
				}));
				break;
			case 5:
				GameScene.this.clearChildScene();
				tutorialText.detachSelf();
				tutorialText.dispose();

				registerUpdateHandler(new TimerHandler(1f, new ITimerCallback() {
					@Override
					public void onTimePassed(TimerHandler pTimerHandler) {

						tutorialText = new Text(1270,380,ResourcesManager.getInstance().font,
								getActivity().getString(R.string.tuto_step5),vbom);
						tutorialText.setScale(0.4f);
						attachChild(tutorialText);

						tutorialStep=-1;
						ConfigManager.getInstance().setParameter(ConfigManager.STR_FIRST_LAUNCH,"false");
						Generateur.getInstance().setMode(precMode);
						ConfigManager.getInstance().setParameter(ConfigManager.INT_ROPE_TYPE,precRopeType);
						ConfigManager.getInstance().setParameter(ConfigManager.INT_PLAYER_TYPE,precPlayerType);

						registerUpdateHandler(new TimerHandler(2f, new ITimerCallback() {
							@Override
							public void onTimePassed(TimerHandler pTimerHandler) {
								tutorialText.detachSelf();
								tutorialText.dispose();
								getActivity().runOnUpdateThread(new Runnable() {
									@Override
									public void run() {
										SceneManager.getInstance().reloadGameScene(ResourcesManager.getInstance().engine);
									}
								});
							}
						}));
					}
				}));
				break;
			default:
				break;
		}
	}
}
