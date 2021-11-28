/**
 *  @author	Tanguy SOTO
 * 	@date	25/12/15
 * 
 * 	Gestion des plateformes
 */

package ts.tangames.drop_the_rope.object;

import android.util.Log;

import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.color.Color;

import ts.tangames.drop_the_rope.activity.GameActivity;
import ts.tangames.drop_the_rope.base.BaseObject;
import ts.tangames.drop_the_rope.manager.Generateur;
import ts.tangames.drop_the_rope.manager.ResourcesManager;
import ts.tangames.drop_the_rope.scene.GameScene;

public class Platform extends BaseObject {

	// ---------------------------------------------
	// VARIABLES
	// ---------------------------------------------

	public static final String tag = "Platform";

	public static int count = 0;
	protected int id;
	protected boolean isCounted=false;	// true si la plateform a déjà donné son score

	protected Rectangle center; // si la corde atteint cette cible -> +1
	protected boolean isCenterTouched = false;

	protected ResourcesManager r;
	protected GameScene scene;

	protected int w,h; // largeur et hauteur de la plateform
	protected int centerRatio = 4;

	// ---------------------------------------------
	// CONSTRUCTOR
	// ---------------------------------------------

	public Platform(float pX, float pY,int w, int h, VertexBufferObjectManager vbo, Camera camera,
			PhysicsWorld physicsWorld, GameScene scene) {
		super(pX, pY, ResourcesManager.getInstance().platform_region, vbo);

		// numéro
		count ++ ;
		id = count;

		// paramètres
		this.r = ResourcesManager.getInstance();
		this.scene=scene;
		this.w=w;
		this.h=h;

		createPhysics(camera, physicsWorld);
	}

	protected void createPhysics(final Camera camera, final PhysicsWorld physicsWorld) {
		density = 1f;
		elasticity = 0;
		friction = 1f;

		// taille de l'entité
		setHeight(h);
		setWidth(w);

		// body (ici inactif) et connecteur physique
		body = PhysicsFactory.createBoxBody(physicsWorld, this, BodyType.StaticBody,
				PhysicsFactory.createFixtureDef(density, elasticity, friction));
		body.setUserData(this);
		body.setActive(false);

		physicsConnector = new PhysicsConnector(this, body, true, true);
		physicsWorld.registerPhysicsConnector(physicsConnector);

		// création du centre
		float[] loc =this.convertSceneCoordinatesToLocalCoordinates(this.getX(),this.getY());
		center = new Rectangle(loc[0],loc[1],w/centerRatio,h/centerRatio,r.vbom);
		center.setColor(Color.CYAN);
		this.attachChild(center);

	}

	// pseudo recréation permettant de réutiliser une plateform
	protected void reCreate(int lastX){
		Log.d(tag,"n°"+id+" : begin reCreate()");

		count ++;
		id = count;

		int t[] = Generateur.getInstance().nextPlateform(count);

		// changement de la plateforme
		setX(t[0]);
		setY(t[1]);
		setWidth(t[2]);
		setHeight(t[3]);

		body.setTransform(t[0]/ PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT,t[1]/PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT,body.getAngle());

		// changement de son centre
		float[] loc =this.convertSceneCoordinatesToLocalCoordinates(this.getX(),this.getY());
		center.setWidth(t[2]/centerRatio);
		center.setHeight(t[3]/centerRatio);
		center.setPosition(loc[0],loc[1]);

		// empêche la caméra de revenir en arrière après avoir passé une plateforme
		r.camera.setBounds(lastX,r.camera.getBoundsYMin(),r.camera.getXMax()+ GameActivity.getWidth()*3,r.camera.getBoundsYMax());
	}


	@Override
	protected void onManagedUpdate(float pSecondsElapsed){
		super.onManagedUpdate(pSecondsElapsed);

		int posX = (int) (getX()+getWidth()/2);

		// plateform passée par le joueur
		if(!isCounted && posX<scene.getPlayer().getX()){
			scene.addToScore(scene.getPlayer().getRopeDescriptor().scoreMultiplier*
					scene.getPlayer().getPlayerDescriptor().scoreMultiplier,false);
			isCounted=true;
		}

		// plateform plus visible
		if(posX < r.camera.getXMin()){
			reCreate(posX);
			isCounted=false;
			isCenterTouched=false;
		}
	}

	// Destroy the object completely (physic + sprite)
	@Override
	public void destroyObject(final PhysicsWorld physicsWorld) {
		super.destroyObject(physicsWorld);

		r.engine.runOnUpdateThread(new Runnable() {
			public void run() {
				center.detachSelf();
				center.dispose();
			}
		});
	}

	//---------------------------------------------
	// GETTERS / SETTERS
	//---------------------------------------------

	public Rectangle getCenter(){
		return center;
	}

	public void setCenterTouched(boolean b){
		isCenterTouched=b;
	}
}

