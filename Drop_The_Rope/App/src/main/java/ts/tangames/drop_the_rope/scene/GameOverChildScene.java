/**
 *  @author	Tanguy SOTO
 * 	@date	25/12/15
 * 
 * 	Scène de chargement.
 */

package ts.tangames.drop_the_rope.scene;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.TextMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.adt.color.Color;
import org.andengine.util.modifier.ease.EaseBackIn;
import org.andengine.util.modifier.ease.EaseBackOut;
import org.andengine.util.modifier.ease.EaseSineInOut;

import ts.tangames.drop_the_rope.activity.GameActivity;
import ts.tangames.drop_the_rope.manager.ConfigManager;
import ts.tangames.drop_the_rope.manager.ResourcesManager;
import ts.tangames.drop_the_rope.manager.SceneManager;
import ts.tangames.ventix.R;

public class GameOverChildScene extends MenuScene implements MenuScene.IOnMenuItemClickListener {

	// Normal childs
	protected Sprite background;
	protected Text score;
	protected Text bestScore;
	protected Text gemsCollected;
	protected Sprite gems;

	// Menu Item
	protected TextMenuItem title;
	protected IMenuItem play;
	protected IMenuItem home;

	private final int MENU_TITLE = -1;
	private final int MENU_RETRY = 0;
	private final int MENU_HOME = 1;

	private final float animateInDuration = 0.7f;
	private final float animateOutDuration = 0.6f;

	protected GameScene mParent;

	public GameOverChildScene(Camera camera, final GameScene parent, boolean isNewBestScore){
		super(camera);

		this.mParent=parent;

		this.setBackgroundEnabled(false);

		// Création du menu
		setPosition(0,0);
		setOnMenuItemClickListener(this);

		// Création des boutons du menu
		title = new TextMenuItem(MENU_TITLE, ResourcesManager.getInstance().font,
				mParent.getActivity().getString(R.string.game_over), ResourcesManager.getInstance().vbom);
		play = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_RETRY,
				ResourcesManager.getInstance().retry_region,ResourcesManager.getInstance().vbom),0.3f,0.35f);
		play.setScale(0);
		home = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_HOME,
				ResourcesManager.getInstance().home_region,ResourcesManager.getInstance().vbom),0.3f,0.35f);

		// Création des child normaux
		background = new Sprite(GameActivity.getWidth()/2,GameActivity.getHeight()/2+35,
				ResourcesManager.getInstance().child_scene_background,ResourcesManager.getInstance().vbom);
		background.setAlpha(0.8f);

		score = new Text(-200,GameActivity.getHeight()/2+90,
				ResourcesManager.getInstance().font,
				""+ ConfigManager.getInstance().getParameterInt(ConfigManager.INT_CURRENT_SCORE),
				ResourcesManager.getInstance().vbom);
		score.setScale(0.85f);
		bestScore = new Text(-200,GameActivity.getHeight()/2+35,
				ResourcesManager.getInstance().font,mParent.getActivity().getString(R.string.best_score)+
				" : "+ConfigManager.getInstance().getParameterInt(ConfigManager.INT_BEST_SCORE),
				ResourcesManager.getInstance().vbom);
		bestScore.setScale(0.4f);
		gemsCollected = new Text(1000,GameActivity.getHeight()/2-31,
				ResourcesManager.getInstance().font,""+
					ConfigManager.getInstance().getParameterInt(ConfigManager.INT_COLLECTED_GEM),
					new TextOptions(HorizontalAlign.RIGHT),ResourcesManager.getInstance().vbom);
		gemsCollected.setScale(0.45f);
		gemsCollected.setAnchorCenter(0,0);

		gems = new AnimatedSprite(1000,GameActivity.getHeight()/2-10,ResourcesManager.getInstance().gems_region,ResourcesManager.getInstance().vbom);
		gems.setScale(0.35f);

		// Positionnement des boutons
		title.setPosition(GameActivity.getWidth()/2,GameActivity.getHeight()/2+170);
		play.setPosition(GameActivity.getWidth()/2+50,GameActivity.getHeight()/2-90);
		home.setPosition(GameActivity.getWidth()/2-50,GameActivity.getHeight()/2-90);

		final Rectangle background2 = new Rectangle(GameActivity.getWidth()/2,GameActivity.getHeight()/2,
				GameActivity.getWidth(),GameActivity.getHeight(),ResourcesManager.getInstance().vbom);
		background2.setColor(Color.BLACK);
		background2.setAlpha(0.3f);

		// On attache le tout
		attachChild(background2);
		attachChild(background);
		attachChild(score);
		attachChild(bestScore);
		attachChild(gemsCollected);
		attachChild(gems);

		addMenuItem(title);
		addMenuItem(home);
		addMenuItem(play);

		animateIn(isNewBestScore);
	}

	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,
									 float pMenuItemLocalX, float pMenuItemLocalY) {
		switch (pMenuItem.getID()) {
			case MENU_RETRY:
				SceneManager.getInstance().showInterstitial(mParent.getActivity());
				ResourcesManager.getInstance().button_sound.play();
				SceneManager.getInstance().reloadGameScene(ResourcesManager.getInstance().engine);
				return true;
			case MENU_HOME:
				SceneManager.getInstance().showInterstitial(mParent.getActivity());
				ResourcesManager.getInstance().button_sound.play();
				SceneManager.getInstance().loadMenuScene(ResourcesManager.getInstance().engine);
				return true;
			default:
				return false;
		}
	}

	public void animateOut(){
		play.registerEntityModifier(
			new ScaleModifier(animateOutDuration,0.35f,0){
				@Override
				protected void onModifierFinished(IEntity pItem){
					super.onModifierFinished(pItem);
					registerUpdateHandler(new TimerHandler(0.05f,new ITimerCallback(){
						@Override
						public void onTimePassed(TimerHandler pTimerHandler) {
							SceneManager.getInstance().loadMenuScene(ResourcesManager.getInstance().engine);
						}
					}));
				}
			});

		score.registerEntityModifier(
				new MoveXModifier(animateOutDuration,
						GameActivity.getWidth()/2,-200,EaseBackIn.getInstance()));
		bestScore.registerEntityModifier(
				new MoveXModifier(animateOutDuration,
						GameActivity.getWidth()/2,-200, EaseBackIn.getInstance()));

		gemsCollected.registerEntityModifier(
				new MoveXModifier(animateOutDuration,
						GameActivity.getWidth()/2,1000,EaseBackIn.getInstance()));
	}

	public void animateIn(boolean isNewBestScore){
		play.registerEntityModifier(
				new SequenceEntityModifier(
						new ScaleModifier(0.6f,0,0),
						new ScaleModifier(animateInDuration,0,0.35f, EaseBackOut.getInstance())));


		if(isNewBestScore){
			score.registerEntityModifier(
					new SequenceEntityModifier(
							new MoveXModifier(animateInDuration,-200,
									GameActivity.getWidth()/2, EaseBackOut.getInstance()),
							new LoopEntityModifier(
									new SequenceEntityModifier(
											new ScaleModifier(0.4f,0.85f,0.92f, EaseSineInOut.getInstance()),
											new ScaleModifier(0.4f,0.92f,0.85f, EaseSineInOut.getInstance()))
							)));
			bestScore.registerEntityModifier(
					new SequenceEntityModifier(
						new MoveXModifier(animateInDuration,-200,
								GameActivity.getWidth()/2, EaseBackOut.getInstance()),
						new LoopEntityModifier(
								new SequenceEntityModifier(
									new ScaleModifier(0.4f,0.4f,0.45f, EaseSineInOut.getInstance()),
									new ScaleModifier(0.4f,0.45f,0.4f, EaseSineInOut.getInstance()))
						)));
		}
		else {
			score.registerEntityModifier(
					new MoveXModifier(animateInDuration,-200,
							GameActivity.getWidth()/2,EaseBackOut.getInstance()));
			bestScore.registerEntityModifier(
					new MoveXModifier(animateInDuration,-200,
							GameActivity.getWidth()/2, EaseBackOut.getInstance()));

		}

		gemsCollected.registerEntityModifier(
				new MoveXModifier(animateOutDuration,1000,
						GameActivity.getWidth()/2-gemsCollected.getWidth()/2f,EaseBackOut.getInstance()));
		gems.registerEntityModifier(
				new MoveXModifier(animateOutDuration,1000,
						GameActivity.getWidth()/2+30,EaseBackOut.getInstance()));
	}
}
