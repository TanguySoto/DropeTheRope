/**
 *  @author	Tanguy SOTO
 * 	@date	25/12/15
 * 
 * 	Scène de chargement.
 */

package ts.tangames.drop_the_rope.scene;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.TextMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;
import org.andengine.util.adt.color.Color;

import ts.tangames.drop_the_rope.activity.GameActivity;
import ts.tangames.drop_the_rope.manager.ResourcesManager;
import ts.tangames.drop_the_rope.manager.SceneManager;
import ts.tangames.ventix.R;

public class PauseChildScene extends MenuScene implements MenuScene.IOnMenuItemClickListener {

	// Normals childs
	protected Sprite background;

	// Menu Item
	protected TextMenuItem title;
	protected IMenuItem resume;
	protected IMenuItem retry;
	protected IMenuItem home;

	private final int MENU_TITLE = -1;
	private final int MENU_RESUME = 0;
	private final int MENU_RETRY = 1;
	private final int MENU_HOME = 2;

	protected GameScene mParent;

	public PauseChildScene(Camera camera, final GameScene parent){
		super(camera);

		this.mParent=parent;

		this.setBackgroundEnabled(false);

		// Création du menu
		setPosition(0,0);
		setOnMenuItemClickListener(this);

		// Création des boutons du menu
		title = new TextMenuItem(MENU_TITLE, ResourcesManager.getInstance().font,
				mParent.getActivity().getString(R.string.pause), ResourcesManager.getInstance().vbom);

		resume = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_RESUME,
				ResourcesManager.getInstance().resume_region,ResourcesManager.getInstance().vbom),0.3f,0.35f);
		retry = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_RETRY,
				ResourcesManager.getInstance().retry_region,ResourcesManager.getInstance().vbom),0.3f,0.35f);
		home = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_HOME,
				ResourcesManager.getInstance().home_region,ResourcesManager.getInstance().vbom),0.3f,0.35f);

		// Création des child normaux
		background = new Sprite(GameActivity.getWidth()/2,GameActivity.getHeight()/2,
				ResourcesManager.getInstance().child_scene_background2,ResourcesManager.getInstance().vbom);
		background.setAlpha(0.85f);

		// Positionnement des boutons
		title.setPosition(GameActivity.getWidth()/2,GameActivity.getHeight()/2+65);
		resume.setPosition(GameActivity.getWidth()/2+100,GameActivity.getHeight()/2-50);
		retry.setPosition(GameActivity.getWidth()/2,GameActivity.getHeight()/2-50);
		home.setPosition(GameActivity.getWidth()/2-100,GameActivity.getHeight()/2-50);

		final Rectangle background2 = new Rectangle(GameActivity.getWidth()/2,GameActivity.getHeight()/2,
				GameActivity.getWidth(),GameActivity.getHeight(),ResourcesManager.getInstance().vbom);
		background2.setColor(Color.BLACK);
		background2.setAlpha(0.3f);

		// On attache le tout
		attachChild(background2);
		attachChild(background);
		addMenuItem(title);
		addMenuItem(resume);
		addMenuItem(home);
		addMenuItem(retry);
	}

	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,
									 float pMenuItemLocalX, float pMenuItemLocalY) {
		switch (pMenuItem.getID()) {
			case MENU_RESUME:
				ResourcesManager.getInstance().button_sound.play();
				mParent.unPauseScene();
				return true;
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
}
