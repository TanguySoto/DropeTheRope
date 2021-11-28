/**
 *  @author	Tanguy SOTO
 * 	@date	25/12/15
 * 
 * 	Scène de chargement.
 */

package ts.tangames.drop_the_rope.scene;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.input.touch.TouchEvent;

public class TutorialChildScene extends MenuScene {

	protected GameScene mParent;

	public TutorialChildScene(Camera camera, final GameScene parent){
		super(camera);

		this.mParent=parent;

		this.setBackgroundEnabled(false);

		this.setOnSceneTouchListener(new IOnSceneTouchListener() {
			@Override
			public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
				mParent.tutorial();
				return true;
			}
		});
	}
}
