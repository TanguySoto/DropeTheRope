/**
 *  @author	Tanguy SOTO
 * 	@date	25/12/15
 * 
 * 	Class de base pour tout objet du jeu. Permettant la gestion de la physique et de l'affichage
 *  grâce à des méthodes réunissant les deux problématiques.
 */

package ts.tangames.drop_the_rope.base;

import com.badlogic.gdx.physics.box2d.Body;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import ts.tangames.drop_the_rope.manager.ResourcesManager;

public abstract class BaseObject extends AnimatedSprite {

	// ---------------------------------------------
	// VARIABLES
	// ---------------------------------------------

	protected float friction = 1;
	protected float elasticity = 0;
	protected float density = 1;

	protected float scaleFactor = 1;

	protected Body body;
	protected PhysicsConnector physicsConnector;

	public BaseObject(float pX, float pY, ITiledTextureRegion pTextureRegion,
			VertexBufferObjectManager vbo) {
		super(pX, pY, pTextureRegion, vbo);
		this.setCullingEnabled(true);
	}

	protected abstract void createPhysics(final Camera camera, PhysicsWorld physicsWorld);

	// Destroy the object completely (physic + sprite)
	public void destroyObject(final PhysicsWorld physicsWorld) {
		final ResourcesManager r = ResourcesManager.getInstance();
		r.engine.runOnUpdateThread(new Runnable() {
			public void run() {
				if (physicsConnector != null) {
					physicsWorld.unregisterPhysicsConnector(physicsConnector);
				}
				body.setActive(false);
				physicsWorld.destroyBody(body);
				BaseObject.this.clearEntityModifiers();
				BaseObject.this.clearUpdateHandlers();
				BaseObject.this.detachSelf();
				BaseObject.this.dispose();
			}

		});
	}

	//---------------------------------------------
	// GETTERS and SETTERS
	//---------------------------------------------

	public Body getBody(){
		return body;
	}
}
