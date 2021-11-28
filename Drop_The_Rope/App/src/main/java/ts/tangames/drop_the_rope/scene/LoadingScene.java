/**
 *  @author	Tanguy SOTO
 * 	@date	25/12/15
 * 
 * 	Scène de chargement.
 */

package ts.tangames.drop_the_rope.scene;

import org.andengine.entity.text.Text;

import ts.tangames.drop_the_rope.activity.GameActivity;
import ts.tangames.drop_the_rope.base.BaseScene;
import ts.tangames.drop_the_rope.manager.SceneManager.SceneType;
import ts.tangames.ventix.R;

public class LoadingScene extends BaseScene {

	private Text loadingText;

	@Override
	public void createScene() {
		setBackgroundEnabled(false);

		loadingText = new Text(GameActivity.getWidth() / 2, GameActivity.getHeight() / 2,
				resourcesManager.font, getActivity().getString(R.string.loading), vbom);
		loadingText.setScale(0.75f);
		loadingText.setVisible(false);

		attachChild(loadingText);
	}

	@Override
	public void onBackKeyPressed() {
		return;
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_LOADING;
	}

	@Override
	public void disposeScene() {
		loadingText.detachSelf();
		loadingText.dispose();
	}
}
