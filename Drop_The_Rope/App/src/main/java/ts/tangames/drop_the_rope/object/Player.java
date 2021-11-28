/**
 *  @author	Tanguy SOTO
 * 	@date	25/12/15
 * 
 * 	Class joueur de base : pouvant se déplacer au sol et sauter.
 */

package ts.tangames.drop_the_rope.object;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import java.util.ArrayList;

import ts.tangames.drop_the_rope.base.BaseObject;
import ts.tangames.drop_the_rope.factory.PlayerDescriptor;
import ts.tangames.drop_the_rope.factory.RopeDescriptor;
import ts.tangames.drop_the_rope.manager.Generateur;
import ts.tangames.drop_the_rope.manager.ResourcesManager;
import ts.tangames.drop_the_rope.scene.GameScene;

public class Player extends BaseObject {

	// ---------------------------------------------
	// VARIABLES
	// ---------------------------------------------

	// speed limit
	protected final float MAX_VERTICALE_SPEED = 14f;

	// jump
	protected int MAX_STRENGTH = 10; // jumping strength

	// rope and player parameters
	protected RopeDescriptor ropeDescriptor;
	protected PlayerDescriptor playerDescriptor;

	protected AnimatedSprite target;

	protected ArrayList<RopeWithHandle> ropes;

	protected GameScene scene;

	// ---------------------------------------------
	// CONSTRUCTORS
	// ---------------------------------------------

	public Player(float pX, float pY,RopeDescriptor ropeDescriptor,PlayerDescriptor playerDescriptor, VertexBufferObjectManager vbo, Camera camera,
			PhysicsWorld physicsWorld, GameScene scene) {
		super(pX, pY, playerDescriptor.playerRegion, vbo);

		this.scene = scene;
		
		this.setCurrentTileIndex(0);

		long frames[] = {3000+ Generateur.getInstance().nextInt(2500),
					170+Generateur.getInstance().nextInt(100)};
		this.animate(frames);

		this.ropeDescriptor = ropeDescriptor;
		this.playerDescriptor = playerDescriptor;

		this.target = new AnimatedSprite(this.getX()+ropeDescriptor.width,this.getY()+ropeDescriptor.height,playerDescriptor.targetRegion,vbo);
		this.target.setScale(playerDescriptor.targetScaleFactor);
		scene.attachChild(target);

		createPhysics(camera, physicsWorld);
		camera.setChaseEntity(this);
	}

	protected void createPhysics(final Camera camera, PhysicsWorld physicsWorld) {
		scaleFactor = playerDescriptor.scaleFactor;
		density = playerDescriptor.density;
		elasticity = 0f;
		friction = 0.8f;

		setScale(scaleFactor);
		body = PhysicsFactory.createCircleBody(physicsWorld, this, BodyType.DynamicBody,
				PhysicsFactory.createFixtureDef(density, elasticity, friction));
		body.setUserData(this);

		physicsConnector = new PhysicsConnector(this, body, true, true) {
			@Override
			public void onUpdate(float pSecondsElapsed) {
				super.onUpdate(pSecondsElapsed);
				camera.onUpdate(0.1f);

				updateTargetPosition();

				if (getY() <= -100) {
					onDie();
				}
			}
		};
		physicsWorld.registerPhysicsConnector(physicsConnector);

		ropes = new ArrayList<RopeWithHandle>();
	}

	// Destroy the object completely (physic + sprite)
	public void destroyObject(final PhysicsWorld physicsWorld) {
		super.destroyObject(physicsWorld);

		for (RopeWithHandle r : ropes) {
			r.destroyObject(physicsWorld);
		}
		ropes.clear();

		ResourcesManager.getInstance().engine.runOnUpdateThread(new Runnable() {
			public void run() {
				target.clearEntityModifiers();
				target.clearUpdateHandlers();
				target.detachSelf();
				target.dispose();
			}

		});
	}

	//---------------------------------------------
	// PLAYER FUNCTIONS
	//---------------------------------------------

	//---------------------------------------------
	// Mouvement de base

	public void throwRopeAnyWay() {
		ropes.add(new RopeWithHandle(ropeDescriptor, getX(), getY(),scene, this));
	}

	public void detachRope() {
		final RopeWithHandle r = ropes.remove(0);

		r.detachHolder();
		body.setLinearVelocity(new Vector2(body.getLinearVelocity().x* ropeDescriptor.relanceW,
					body.getLinearVelocity().y + MAX_STRENGTH * ropeDescriptor.relanceH));
		limiteSpeed();

		scene.registerUpdateHandler(new TimerHandler(0.01f, new ITimerCallback() {
			public void onTimePassed(TimerHandler pTimerHandler) {
				r.destroyObject(ResourcesManager.getInstance().physicsWorld);
			}
		}));
	}

	// Limite la vitesse maximale du joueur
	private void limiteSpeed(){
		if(body.getLinearVelocity().y>MAX_VERTICALE_SPEED){
			body.setLinearVelocity(body.getLinearVelocity().x,MAX_VERTICALE_SPEED);
		}
	}

	public void onDie() {
		scene.endGame();
	}

	//---------------------------------------------
	// GETTERS and SETTERS
	//---------------------------------------------

	public ArrayList<RopeWithHandle> getRopes() {
		return ropes;
	}

	public AnimatedSprite getTarget(){
		return target;
	}

	public PlayerDescriptor getPlayerDescriptor() {
		return playerDescriptor;
	}

	public RopeDescriptor getRopeDescriptor() {
		return ropeDescriptor;
	}

	public void updateTargetPosition(){
		float x = this.getX();
		float y = this.getY();

		target.setPosition(x+ropeDescriptor.width,y+ropeDescriptor.height);
		scene.getGemsCollectionEffect().setPosition(x,y);
	}

}
