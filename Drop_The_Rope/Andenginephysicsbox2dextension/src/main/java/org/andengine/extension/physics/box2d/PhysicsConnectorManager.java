package org.andengine.extension.physics.box2d;

import com.badlogic.gdx.physics.box2d.Body;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.IEntity;

import java.util.ArrayList;

/**
 * (c) 2010 Nicolas Gramlich
 * (c) 2011 Zynga Inc.
 *
 * @author Nicolas Gramlich
 * @since 15:52:27 - 15.07.2010
 */
public class PhysicsConnectorManager extends ArrayList<PhysicsConnector> implements IUpdateHandler {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final long serialVersionUID = 412969510084261799L;

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	PhysicsConnectorManager() {

	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public void onUpdate(final float pSecondsElapsed) {
		final ArrayList<PhysicsConnector> physicsConnectors = this;
		for (int i = physicsConnectors.size() - 1; i >= 0; i--) {
			physicsConnectors.get(i).onUpdate(pSecondsElapsed);
		}
	}

	@Override
	public void reset() {
		final ArrayList<PhysicsConnector> physicsConnectors = this;
		for (int i = physicsConnectors.size() - 1; i >= 0; i--) {
			physicsConnectors.get(i).reset();
		}
	}

	// ===========================================================
	// Methods
	// ===========================================================

	public Body findBodyByShape(final IEntity pShape) {
		final ArrayList<PhysicsConnector> physicsConnectors = this;
		for (int i = physicsConnectors.size() - 1; i >= 0; i--) {
			final PhysicsConnector physicsConnector = physicsConnectors.get(i);
			if (physicsConnector.mEntity == pShape) {
				return physicsConnector.mBody;
			}
		}
		return null;
	}

	public PhysicsConnector findPhysicsConnectorByShape(final IEntity pShape) {
		final ArrayList<PhysicsConnector> physicsConnectors = this;
		for (int i = physicsConnectors.size() - 1; i >= 0; i--) {
			final PhysicsConnector physicsConnector = physicsConnectors.get(i);
			if (physicsConnector.mEntity == pShape) {
				return physicsConnector;
			}
		}
		return null;
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
