/**
 *  @author	Tanguy SOTO
 * 	@date	25/12/15
 * 
 *  Une corde de n morceaux avec  poignée statique.
 */

package ts.tangames.drop_the_rope.object;

import android.graphics.Color;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;

import ts.tangames.drop_the_rope.factory.RopeDescriptor;
import ts.tangames.drop_the_rope.manager.ResourcesManager;
import ts.tangames.drop_the_rope.scene.GameScene;

public class RopeWithHandle {

	// ---------------------------------------------
	// VARIABLES
	// ---------------------------------------------

	protected GameScene gameScene;

	protected float friction = 0;
	protected float elasticity = 0;

	// handle
	protected Rectangle handle;
	protected Body handleBody;
	protected PhysicsConnector handleConnector;

	protected  RevoluteJoint join;

	protected Rope rope;

	// ---------------------------------------------
	// CONSTRUCTOR
	// ---------------------------------------------

	public RopeWithHandle(RopeDescriptor descriptor, float fromx, float fromy, Scene scene, Player holder) {

		// handle entity
		float toX=fromx+descriptor.width;
		float toY=fromy+descriptor.height;

		handle = new Rectangle(toX,toY-descriptor.handleWidth/2,descriptor.handleWidth,descriptor.thickness, ResourcesManager.getInstance().vbom);
		handle.setRotation(90);
		handle.setColor(Color.GRAY);
		scene.attachChild(handle);

		BodyType type = BodyType.DynamicBody;

		// rope
		rope = new Rope(descriptor,fromx,fromy,toX,toY-descriptor.handleWidth,scene,holder);

		gameScene=(GameScene) scene;

		// recherche si un plateforme est atteinte
		Platform platform=null;
		for(Platform p:gameScene.getPlatform()){
			if(gameScene.getPlayer().getTarget().collidesWith(p)){
				platform=p;
				break;
			}
		}

		if(platform!=null){
			type = BodyType.StaticBody;
			// recherche si le centre de la plateforme est atteint
			if(gameScene.getPlayer().getTarget().collidesWith(platform.getCenter())){
				platform.setCenterTouched(true);
				if(!platform.isCounted) {
					gameScene.addToScore(gameScene.getPlayer().getRopeDescriptor().scoreMultiplier*
							gameScene.getPlayer().getPlayerDescriptor().scoreMultiplier, true);
				}
			}
		}

		// handle body
		handleBody=PhysicsFactory.createBoxBody(ResourcesManager.getInstance().physicsWorld, handle,
					type,
					PhysicsFactory.createFixtureDef(descriptor.density, elasticity, friction));
		handleBody.setUserData(this);

		handleConnector = new PhysicsConnector(handle, handleBody, true, true);
		ResourcesManager.getInstance().physicsWorld.registerPhysicsConnector(handleConnector);

		// attach handle to rope
		RevoluteJointDef revoluteJointDef = new RevoluteJointDef();
		revoluteJointDef.initialize(handleBody, rope.getAnchrage(),rope.getAnchrage().getWorldCenter());
		revoluteJointDef.collideConnected=false;
		join = (RevoluteJoint) ResourcesManager.getInstance().physicsWorld.createJoint(revoluteJointDef);
	}

	public void destroyObject(final PhysicsWorld physicsWorld){

		ResourcesManager.getInstance().engine.runOnUpdateThread(new Runnable() {
			@Override
			public void run() {
				physicsWorld.unregisterPhysicsConnector(handleConnector);

				if(join!=null) {
					//physicsWorld.destroyJoint(join);
				}

				handleBody.setActive(false);
				physicsWorld.destroyBody(handleBody);

				handle.clearEntityModifiers();
				handle.clearUpdateHandlers();
				handle.detachSelf();
				handle.dispose();
			}
		});

		rope.destroyObject(physicsWorld);
	}

	public void detachHolder(){
		rope.detachHolder();
	}
}
