/**
 *  @author	Tanguy SOTO
 * 	@date	25/12/15
 * 
 * 	Gestion des boîtes.
 */

package ts.tangames.drop_the_rope.object;

import android.util.Log;

import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.modifier.ease.EaseSineInOut;

import ts.tangames.drop_the_rope.base.BaseObject;
import ts.tangames.drop_the_rope.manager.Generateur;
import ts.tangames.drop_the_rope.manager.ResourcesManager;
import ts.tangames.drop_the_rope.scene.GameScene;

public class Gem extends BaseObject {

	// ---------------------------------------------
	// VARIABLES
	// ---------------------------------------------

	public static final String tag = "Gem";

	public static int count = 0;
	protected int id;

	protected static int lastCollected = 0;
	protected static int collectedInARow = 0;
	protected static final int ROW_LENGTH=4;

	protected ResourcesManager r;

	protected GameScene scene;

	// ---------------------------------------------
	// CONSTRUCTOR
	// ---------------------------------------------

	public Gem(float pX, float pY, VertexBufferObjectManager vbo, Camera camera,
			   PhysicsWorld physicsWorld, GameScene s) {
		super(pX, pY, ResourcesManager.getInstance().gems_region, vbo);
		createPhysics(camera, physicsWorld);
		scene=s;

		count++;
		id=count;

		r = ResourcesManager.getInstance();

		this.registerEntityModifier(
				new LoopEntityModifier(
						new SequenceEntityModifier(
								new ScaleModifier(0.8f, 0.3f,0.42f, EaseSineInOut.getInstance()),
								new ScaleModifier(0.8f,0.42f,0.3f, EaseSineInOut.getInstance())))
		);
	}

	protected void createPhysics(final Camera camera, PhysicsWorld physicsWorld) {
		scaleFactor=0.3f;
		density = 1;
		elasticity = 0.05f;
		friction = 1;

		setScale(scaleFactor);
		
		body = PhysicsFactory.createBoxBody(physicsWorld, this, BodyType.StaticBody,
				PhysicsFactory.createFixtureDef(density, elasticity, friction));
		body.setUserData(this);
		body.setActive(false);
		
		physicsConnector = new PhysicsConnector(this, body, true, true);
		physicsWorld.registerPhysicsConnector(physicsConnector);
	}

	// pseudo recréation permettant de réutiliser une gem
	protected void reCreate(){
		Log.d(tag,"n°"+id+" : begin reCreate()");

		count ++;
		id = count;

		int t[] = Generateur.getInstance().nextGem(count);

		setX(t[0]);
		setY(t[1]);

		body.setTransform(t[0]/ PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT,t[1]/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT,body.getAngle());

		this.clearEntityModifiers();
		this.registerEntityModifier(
				new LoopEntityModifier(
						new SequenceEntityModifier(
								new ScaleModifier(0.8f, 0.3f,0.42f, EaseSineInOut.getInstance()),
								new ScaleModifier(0.8f,0.42f,0.3f, EaseSineInOut.getInstance())))
		);
	}

	@Override
	protected void onManagedUpdate(float pSecondsElapsed){
		super.onManagedUpdate(pSecondsElapsed);

		if(scene.getPlayer().collidesWith(this)){
			//
			int additionner = 0;
			if(id==lastCollected+1){
				collectedInARow++;
			}
			else {
				collectedInARow=1;
			}
			if(collectedInARow==ROW_LENGTH){
				collectedInARow=0;
				additionner=1;
				Log.d("game","ROW");
			}

			lastCollected=id;

			int mult = scene.getPlayer().getRopeDescriptor().gemsMultiplier*
					scene.getPlayer().getPlayerDescriptor().gemsMultiplier;

			scene.addGemsScore(mult+additionner*mult,getX(),getY(),additionner==1);

			reCreate();
		}

		// gem plus visible
		if(getX()+getWidth()/2 < r.camera.getXMin()){
			reCreate();
		}
	}

	//---------------------------------------------
	// GETTERS / SETTERS
	//---------------------------------------------
}
