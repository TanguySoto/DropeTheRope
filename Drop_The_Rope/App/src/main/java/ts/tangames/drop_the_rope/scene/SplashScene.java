/**
 *  @author	Tanguy SOTO
 * 	@date	25/12/15
 * 
 * 	Première scène affichant un logo pendant chargement des ressources.
 */


package ts.tangames.drop_the_rope.scene;

import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.util.adt.color.Color;

import ts.tangames.drop_the_rope.activity.GameActivity;
import ts.tangames.drop_the_rope.base.BaseScene;
import ts.tangames.drop_the_rope.manager.SceneManager;

public class SplashScene extends BaseScene
{
	private Sprite splash;	
	
    @Override
    public void createScene()
    {
    	// création du sprite depuis la ITextureRegion chargée
    	splash = new Sprite(0, 0, resourcesManager.splash_region, vbom);

        splash.setScale(0.8f);
    	splash.setPosition(GameActivity.getWidth()/2, GameActivity.getHeight()/2);
        this.setBackground(new Background(new Color(Color.WHITE)));
    	this.attachChild(splash);
    }

    @Override
    public void onBackKeyPressed()
    {

    }

    @Override
    public SceneManager.SceneType getSceneType()
    {
    	return SceneManager.SceneType.SCENE_SPLASH;
    }

    @Override
    public void disposeScene()
    {
    	splash.detachSelf();
        splash.dispose();
        this.detachSelf();
        this.dispose();
    }
}
