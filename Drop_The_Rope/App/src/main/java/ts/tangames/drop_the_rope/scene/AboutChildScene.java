/**
 *  @author	Tanguy SOTO
 * 	@date	25/12/15
 * 
 * 	Scène de chargement.
 */

package ts.tangames.drop_the_rope.scene;

import android.util.Log;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.adt.color.Color;

import ts.tangames.drop_the_rope.activity.GameActivity;
import ts.tangames.drop_the_rope.manager.ConfigManager;
import ts.tangames.drop_the_rope.manager.ResourcesManager;
import ts.tangames.drop_the_rope.manager.SceneManager;
import ts.tangames.ventix.R;

public class AboutChildScene extends MenuScene implements MenuScene.IOnMenuItemClickListener {

	protected Text appName;
	protected Text versionName;
	protected Text devName;

	protected Text howToPlay;
	protected IMenuItem howToPlayMenuItem;
	protected final int MENU_HOW_TO=0;

	protected MainMenuScene mParent;

	public AboutChildScene(Camera camera, final MainMenuScene parent){
		super(camera);

		this.mParent=parent;

		this.setBackgroundEnabled(false);

		appName = new Text(GameActivity.getWidth()/2,GameActivity.getHeight()/2-25,
				ResourcesManager.getInstance().font,mParent.getActivity().getString(R.string.app_name),
				ResourcesManager.getInstance().vbom);
		appName.setScale(0.25f);
		versionName = new Text(GameActivity.getWidth()/2,GameActivity.getHeight()/2-55,
				ResourcesManager.getInstance().font,mParent.getActivity().getString(R.string.version)+
				" "+mParent.getActivity().getString(R.string.version_name),
				ResourcesManager.getInstance().vbom);
		versionName.setScale(0.25f);
		versionName.setAlpha(0.75f);
		devName = new Text(GameActivity.getWidth()/2,GameActivity.getHeight()/2-100,
				ResourcesManager.getInstance().font,mParent.getActivity().getString(R.string.tangames),
				ResourcesManager.getInstance().vbom);
		devName.setScale(0.3f);

		final Rectangle background = new Rectangle(GameActivity.getWidth()/2,GameActivity.getHeight()/2,
				GameActivity.getWidth(),GameActivity.getHeight(),ResourcesManager.getInstance().vbom);
		background.setColor(Color.BLACK);
		background.setAlpha(0.3f);
		final Rectangle cadre = new Rectangle(GameActivity.getWidth()/2,GameActivity.getHeight()/2-15,260,250,
				ResourcesManager.getInstance().vbom);

		howToPlay = new Text(GameActivity.getWidth()/2,GameActivity.getHeight()/2+50,
				ResourcesManager.getInstance().font,mParent.getActivity().getString(R.string.how_to_play),
				ResourcesManager.getInstance().vbom);
		howToPlay.setScale(0.4f);
		howToPlayMenuItem =	new SpriteMenuItem(MENU_HOW_TO,
					ResourcesManager.getInstance().how_to_play_region,
					ResourcesManager.getInstance().vbom);
		howToPlayMenuItem.setPosition(GameActivity.getWidth()/2,GameActivity.getHeight()/2+50);

		this.attachChild(background);
		this.attachChild(cadre);

		this.attachChild(appName);
		this.attachChild(versionName);
		this.attachChild(devName);

		this.addMenuItem(howToPlayMenuItem);
		this.attachChild(howToPlay);

		// Création du menu
		setPosition(0,0);
		setOnMenuItemClickListener(this);
		setOnSceneTouchListener(new IOnSceneTouchListener() {
			@Override
			public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
				Log.d("test",""+pSceneTouchEvent.getX()+" - "+pSceneTouchEvent.getY());
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
			case MENU_HOW_TO:
				mParent.detachGrandChildScene();
				ConfigManager.getInstance().setParameter(ConfigManager.STR_FIRST_LAUNCH,"not found");
				SceneManager.getInstance().loadGameScene(ResourcesManager.getInstance().engine);
				return true;
			default:
				return false;
		}
	}
}
