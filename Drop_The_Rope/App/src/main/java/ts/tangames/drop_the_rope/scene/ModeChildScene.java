/**
 *  @author	Tanguy SOTO
 * 	@date	25/12/15
 * 
 * 	Scène de chargement.
 */

package ts.tangames.drop_the_rope.scene;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.adt.color.Color;

import ts.tangames.drop_the_rope.activity.GameActivity;
import ts.tangames.drop_the_rope.factory.ModeType;
import ts.tangames.drop_the_rope.manager.Generateur;
import ts.tangames.drop_the_rope.manager.ResourcesManager;
import ts.tangames.drop_the_rope.manager.SceneManager;
import ts.tangames.ventix.R;

public class ModeChildScene extends MenuScene implements MenuScene.IOnMenuItemClickListener {

	protected Text title;

	protected Text easyMode;
	protected IMenuItem easyModeMenuItem;

	protected Text normalMode;
	protected IMenuItem normalModeMenuItem;

	protected Text difficultMode;
	protected IMenuItem difficultModeMenuItem;

	protected final int MENU_MODE_DIFFICULT=2;
	protected final int MENU_MODE_NORMAL=1;
	protected final int MENU_MODE_EASY=0;


	protected MainMenuScene mParent;

	public ModeChildScene(Camera camera, final MainMenuScene parent){
		super(camera);

		this.mParent=parent;

		this.setBackgroundEnabled(false);


		final Rectangle background = new Rectangle(GameActivity.getWidth()/2,GameActivity.getHeight()/2,
				GameActivity.getWidth(),GameActivity.getHeight(),ResourcesManager.getInstance().vbom);
		background.setColor(Color.BLACK);
		background.setAlpha(0.3f);
		final Sprite cadre = new Sprite(GameActivity.getWidth()/2,GameActivity.getHeight()/2+15,
				ResourcesManager.getInstance().child_scene_background,
				ResourcesManager.getInstance().vbom);
		cadre.setAlpha(0.85f);

		easyMode = new Text(GameActivity.getWidth()/2,GameActivity.getHeight()/2+50,
				ResourcesManager.getInstance().font,mParent.getActivity().getString(R.string.easy),
				ResourcesManager.getInstance().vbom);
		easyMode.setScale(0.4f);
		easyModeMenuItem = new SpriteMenuItem(MENU_MODE_EASY,
				ResourcesManager.getInstance().how_to_play_region,
				ResourcesManager.getInstance().vbom);
		easyModeMenuItem.setPosition(GameActivity.getWidth()/2,GameActivity.getHeight()/2+50);

		normalMode = new Text(GameActivity.getWidth()/2,GameActivity.getHeight()/2-30,
				ResourcesManager.getInstance().font,mParent.getActivity().getString(R.string.normal),
				ResourcesManager.getInstance().vbom);
		normalMode.setScale(0.55f);
		normalModeMenuItem = new SpriteMenuItem(MENU_MODE_NORMAL,
					ResourcesManager.getInstance().how_to_play_region,
					ResourcesManager.getInstance().vbom);
		normalModeMenuItem.setScale(1.15f);
		normalModeMenuItem.setPosition(GameActivity.getWidth()/2,GameActivity.getHeight()/2-30);

		difficultMode = new Text(GameActivity.getWidth()/2,GameActivity.getHeight()/2-110,
				ResourcesManager.getInstance().font,mParent.getActivity().getString(R.string.difficult),
				ResourcesManager.getInstance().vbom);
		difficultMode.setScale(0.4f);
		difficultModeMenuItem = new SpriteMenuItem(MENU_MODE_DIFFICULT,
				ResourcesManager.getInstance().how_to_play_region,
				ResourcesManager.getInstance().vbom);
		difficultModeMenuItem.setPosition(GameActivity.getWidth()/2,GameActivity.getHeight()/2-110);

		title = new Text(400,380,ResourcesManager.getInstance().font,
				"Mode",ResourcesManager.getInstance().vbom);

		this.attachChild(background);
		this.attachChild(cadre);
		this.attachChild(title);

		this.addMenuItem(easyModeMenuItem);
		this.attachChild(easyMode);
		this.addMenuItem(normalModeMenuItem);
		this.attachChild(normalMode);
		this.addMenuItem(difficultModeMenuItem);
		this.attachChild(difficultMode);

		// Création du menu
		setPosition(0,0);
		setOnMenuItemClickListener(this);
		setOnSceneTouchListener(new IOnSceneTouchListener() {
			@Override
			public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
				if(pSceneTouchEvent.isActionDown() && !cadre.contains(pSceneTouchEvent.getX(),pSceneTouchEvent.getY())){
					mParent.detachGrandChildScene();
				}
				return false;
			}
		});
	}

	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,
									 float pMenuItemLocalX, float pMenuItemLocalY) {
		switch (pMenuItem.getID()) {
			case MENU_MODE_EASY:
				mParent.detachGrandChildScene();

				Generateur.getInstance().setMode(ModeType.EASY);

				SceneManager.getInstance().loadGameScene(ResourcesManager.getInstance().engine);
				return true;
			case MENU_MODE_NORMAL:
				mParent.detachGrandChildScene();

				Generateur.getInstance().setMode(ModeType.NORMAL);

				SceneManager.getInstance().loadGameScene(ResourcesManager.getInstance().engine);
				return true;
			case MENU_MODE_DIFFICULT:
				mParent.detachGrandChildScene();

				Generateur.getInstance().setMode(ModeType.DIFFICULT);

				SceneManager.getInstance().loadGameScene(ResourcesManager.getInstance().engine);
				return true;
			default:
				return false;
		}
	}
}
