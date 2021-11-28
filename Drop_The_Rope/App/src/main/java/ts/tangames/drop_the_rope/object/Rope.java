/**
 *  @author	Tanguy SOTO
 * 	@date	25/12/15
 * 
 *  Une corde dedescriptor.nb_morceauxmorceaux.
 */

package ts.tangames.drop_the_rope.object;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import ts.tangames.drop_the_rope.factory.RopeDescriptor;
import ts.tangames.drop_the_rope.manager.ResourcesManager;

public class Rope {

	// ---------------------------------------------
	// VARIABLES
	// ---------------------------------------------

	protected boolean isDestroyed = false;

	protected float friction = 0;
	protected float elasticity = 0;

	protected RopeDescriptor descriptor;

	protected Player player;

	protected float x, y, toY, toX; 		// point d'ancrage
	protected float width; 					// longueur de chaque morceau

	protected float w, h; // hauteur et largeur de chaque morceau

	// morceaux de corde
	protected Body[] rectBodies;
	protected Rectangle[] rects;
	protected PhysicsConnector[] physicsConnectors;

	// cercles de jointures des morceaux
	protected Sprite[] circles;
	protected Body[] circleBodies;
	protected PhysicsConnector[] physicsConnectors2;

	// Revolutes joints (2 par cercles)
	protected RevoluteJoint[] joints;

	// ---------------------------------------------
	// CONSTRUCTOR
	// ---------------------------------------------

	public Rope(RopeDescriptor descriptor, float fromx, float fromy, float toX, float toY, Scene scene, Player player) {
		this.descriptor=descriptor;
		this.player = player;

		x = fromx;
		y = fromy;
		this.toX = toX;
		this.toY = toY;

		w = (toX - x) * 1.0f / descriptor.nb_morceaux;
		h = (toY - y) * 1.0f / descriptor.nb_morceaux;

		width = (float) Math.sqrt(w * w + h * h);

			rectBodies = new Body[descriptor.nb_morceaux];
		rects = new Rectangle[descriptor.nb_morceaux];
		physicsConnectors = new PhysicsConnector[descriptor.nb_morceaux];

		circles = new Sprite[descriptor.nb_morceaux];
		circleBodies = new Body[descriptor.nb_morceaux];
		physicsConnectors2 = new PhysicsConnector[descriptor.nb_morceaux];

		joints = new RevoluteJoint[2 * descriptor.nb_morceaux];

		createRope(scene, player.getBody());
	}

	protected void createRope(Scene scene, Body holder) {
		PhysicsWorld physicsWorld = ResourcesManager.getInstance().physicsWorld;
		VertexBufferObjectManager vbom = ResourcesManager.getInstance().vbom;

		float xP = x;
		float yP = y;
		float angle = (float) Math.toDegrees(Math.atan(h / w));

		for (int i = 0; i < descriptor.nb_morceaux; i++) {

			// morceaux de cordes
			rects[i] = new Rectangle(xP + w / 2, yP + h / 2, width, descriptor.thickness, vbom);
			rects[i].setColor(descriptor.color);
			rects[i].setRotation(angle);

			xP += w;
			yP += h;

			rectBodies[i] = PhysicsFactory.createBoxBody(physicsWorld, rects[i],
					BodyType.DynamicBody,
					PhysicsFactory.createFixtureDef(descriptor.density, elasticity, friction));
			rectBodies[i].setUserData(this);

			physicsConnectors[i] = new PhysicsConnector(rects[i], rectBodies[i], true, true);
			physicsWorld.registerPhysicsConnector(physicsConnectors[i]);

			// cerles de jointures
			if (i < descriptor.nb_morceaux - 1) {
				circles[i] = new Sprite(xP, yP, ResourcesManager.getInstance().circle_region, vbom);
				circles[i].setScale(descriptor.thickness / 55f);
				circleBodies[i] = PhysicsFactory.createCircleBody(physicsWorld, circles[i],
						BodyType.DynamicBody,
						PhysicsFactory.createFixtureDef(descriptor.density, elasticity, friction));
				circleBodies[i].setUserData(this);

				physicsConnectors2[i] = new PhysicsConnector(circles[i], circleBodies[i], true,
						true);
				physicsWorld.registerPhysicsConnector(physicsConnectors2[i]);
			}

			// 2 joints entre les cercles et les morceaux
			if (i > 0) {
				RevoluteJointDef revoluteJointDef = new RevoluteJointDef();
				revoluteJointDef.initialize(rectBodies[i - 1], circleBodies[i - 1],
						circleBodies[i - 1].getWorldCenter());
				revoluteJointDef.collideConnected=false;
				joints[i - 1] = (RevoluteJoint) physicsWorld.createJoint(revoluteJointDef);
				revoluteJointDef = new RevoluteJointDef();
				revoluteJointDef.initialize(circleBodies[i - 1], rectBodies[i],
						circleBodies[i - 1].getWorldCenter());
				revoluteJointDef.collideConnected=false;
				joints[i - 1 + descriptor.nb_morceaux - 1] = (RevoluteJoint) physicsWorld.createJoint(revoluteJointDef);
			}
		}

		// Permet d'avoir les cercle devant les morceaux à l'affichage
		for(Rectangle r : rects){
			scene.attachChild(r);
		}
		for(Sprite c : circles){
			if(c!=null){ // anchrage pas encore créé
				scene.attachChild(c);
			}
		}

		// attach to holder
		RevoluteJointDef revoluteJointDef = new RevoluteJointDef();
		revoluteJointDef.initialize(rectBodies[0], holder, holder.getWorldCenter());
		revoluteJointDef.collideConnected=false;
		joints[2 * descriptor.nb_morceaux - 2] = (RevoluteJoint) physicsWorld.createJoint(revoluteJointDef);

		// anchrage
		circles[descriptor.nb_morceaux - 1] = new Sprite(xP, yP, ResourcesManager.getInstance().circle_region, vbom);
		circles[descriptor.nb_morceaux - 1].setScale(descriptor.thickness / 20f * player.playerDescriptor.targetScaleFactor/0.2f);
		circleBodies[descriptor.nb_morceaux - 1] = PhysicsFactory
				.createCircleBody(physicsWorld, circles[descriptor.nb_morceaux - 1], BodyType.DynamicBody,
						PhysicsFactory.createFixtureDef(descriptor.density, elasticity, friction));
		physicsConnectors2[descriptor.nb_morceaux-1]=new PhysicsConnector(circles[descriptor.nb_morceaux-1],circleBodies[descriptor.nb_morceaux-1],true,true);
		physicsWorld.registerPhysicsConnector(physicsConnectors2[descriptor.nb_morceaux-1]);
		circleBodies[descriptor.nb_morceaux-1].setUserData(this);
		scene.attachChild(circles[descriptor.nb_morceaux - 1]);

		revoluteJointDef = new RevoluteJointDef();
		revoluteJointDef.initialize(rectBodies[descriptor.nb_morceaux - 1], circleBodies[descriptor.nb_morceaux - 1],
				circleBodies[descriptor.nb_morceaux - 1].getWorldCenter());
		revoluteJointDef.collideConnected=false;
		joints[2 * descriptor.nb_morceaux - 1] = (RevoluteJoint) physicsWorld.createJoint(revoluteJointDef);
	}

	public void detachHolder() {
		final ResourcesManager r = ResourcesManager.getInstance();
		r.engine.runOnUpdateThread(new Runnable() {
			public void run() {
				if (joints[2 * descriptor.nb_morceaux - 2] != null) {
					ResourcesManager.getInstance().physicsWorld.destroyJoint(joints[2 * descriptor.nb_morceaux - 2]);
					joints[2 * descriptor.nb_morceaux - 2] = null;
					
				}
			}
		});
	}

	public Body getAnchrage() {
		return circleBodies[descriptor.nb_morceaux-1];
	}

	// Destroy the object completely (physic + sprite)
	public void destroyObject(final PhysicsWorld physicsWorld) {
		final ResourcesManager r = ResourcesManager.getInstance();
		r.engine.runOnUpdateThread(new Runnable() {
			public void run() {

				if(isDestroyed){return;}
				isDestroyed=true;

				for (PhysicsConnector p : physicsConnectors) {
					physicsWorld.unregisterPhysicsConnector(p);
				}
				for (PhysicsConnector p2 : physicsConnectors2) {
					physicsWorld.unregisterPhysicsConnector(p2);
				}

				for (RevoluteJoint r : joints) {
					if (r != null) {
						physicsWorld.destroyJoint(r);
					}
				}

				for (Body b : rectBodies) {
					b.setActive(false);
					physicsWorld.destroyBody(b);
				}
				for (Body b2 : circleBodies) {
					b2.setActive(false);
					physicsWorld.destroyBody(b2);
				}

				for (Rectangle rect : rects) {
					rect.clearEntityModifiers();
					rect.clearUpdateHandlers();
					rect.detachSelf();
					rect.dispose();
				}

				for (Sprite c : circles) {
					c.clearEntityModifiers();
					c.clearUpdateHandlers();
					c.detachSelf();
					c.dispose();
				}
			}

		});
	}

	public RopeDescriptor getDescriptor(){
		return descriptor;
	}

}
