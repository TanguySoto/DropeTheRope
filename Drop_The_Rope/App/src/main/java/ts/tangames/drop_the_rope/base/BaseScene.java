/**
 *  @author	Tanguy SOTO
 * 	@date	25/12/15
 * 
 * 	Class abstraite. Définition de la logique de base de chaque scène du jeu.	
 */

package ts.tangames.drop_the_rope.base;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.SmoothCamera;
import org.andengine.entity.scene.Scene;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import ts.tangames.drop_the_rope.activity.GameActivity;
import ts.tangames.drop_the_rope.manager.ResourcesManager;
import ts.tangames.drop_the_rope.manager.SceneManager;

public abstract class BaseScene extends Scene
{
    //---------------------------------------------
    // VARIABLES
    //---------------------------------------------
    
    protected Engine engine;
    protected GameActivity activity;
    protected ResourcesManager resourcesManager;
    protected VertexBufferObjectManager vbom;
    protected SmoothCamera camera;
    
    //---------------------------------------------
    // CONSTRUCTOR
    //---------------------------------------------
    
    public BaseScene()
    {
        this.resourcesManager = ResourcesManager.getInstance();
        this.engine = resourcesManager.engine;
        this.activity = resourcesManager.activity;
        this.vbom = resourcesManager.vbom;
        this.camera = resourcesManager.camera;
        createScene();
    }
    
    //---------------------------------------------
    // ABSTRACTION
    //---------------------------------------------
    
    public abstract void createScene();
    
    public abstract void onBackKeyPressed();
    
    public abstract SceneManager.SceneType getSceneType();
    
    public abstract void disposeScene();

	public GameActivity getActivity() {
		return activity;
	}

	public SmoothCamera getCamera() {
		return camera;
	}
}
