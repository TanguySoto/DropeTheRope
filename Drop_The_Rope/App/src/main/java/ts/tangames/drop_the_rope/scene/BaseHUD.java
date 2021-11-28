/**
 *  @author	Tanguy SOTO
 * 	@date	13/01/16
 * 
 * 	Gestion des HUD durant les niveaux
 */

package ts.tangames.drop_the_rope.scene;

import android.util.Log;

import org.andengine.engine.camera.SmoothCamera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.modifier.ease.EaseBounceOut;

import ts.tangames.drop_the_rope.activity.GameActivity;
import ts.tangames.drop_the_rope.manager.ResourcesManager;

public class BaseHUD extends HUD {

	// ---------------------------------
	// VARIABLES
	// ---------------------------------

	// HUD elements
	protected SmoothCamera camera;

	protected Text scoreText;

	protected Text bonusText;
	protected AnimatedSprite gemsHUD;

	protected GameScene gameScene;

	// ---------------------------------------------
	// CONSTRUCTORS
	// ---------------------------------------------

	public BaseHUD(GameScene scene, SmoothCamera camera) {
		super();

		this.gameScene=scene;
		this.camera = camera;

		ResourcesManager r = ResourcesManager.getInstance();
		VertexBufferObjectManager vbom = r.vbom;


		// CREATE score text (prepare memory with "Score : 0123456789")
		scoreText = new Text(GameActivity.getWidth()/2f, 430,
				ResourcesManager.getInstance().font, "x:* 0123456789", vbom);
		scoreText.setText("0");
		scoreText.setScale(0.85f);
		scoreText.setPosition(GameActivity.getWidth()/2f,410);
		attachChild(scoreText);

		bonusText = new Text(720, 443, ResourcesManager.getInstance().font, "x:* 0123456789",
				new TextOptions(HorizontalAlign.RIGHT),vbom);
		bonusText.setScale(0.53f);
		bonusText.setAnchorCenter(0,0);
		bonusText.setText("0");
		bonusText.setPosition(720-bonusText.getWidth()/2f,415);
		gemsHUD = new AnimatedSprite(758,443,ResourcesManager.getInstance().gems_region,ResourcesManager.getInstance().vbom);
		gemsHUD.setScale(0.35f);
		attachChild(bonusText);
		attachChild(gemsHUD);

		setTouchAreaBindingOnActionDownEnabled(true);
	}

	// ---------------------------------
	// GETTERS / SETTERS
	// ---------------------------------

	public void setScore(int s, boolean animate) {
		scoreText.setText(""+s);
		scoreText.setPosition(GameActivity.getWidth()/2f,410);

		if(!animate){ return; }

		// animations
		scoreText.clearEntityModifiers();
		scoreText.registerEntityModifier(
				new SequenceEntityModifier(
						new ScaleModifier(1.1f, 0.85f, 1.2f, EaseBounceOut.getInstance()),
						new ScaleModifier(1.1f, 1.2f, 0.85f, EaseBounceOut.getInstance())
				));

		gameScene.animatePlusUn();
	}

	public void setBonus(int b, boolean animate) {
		bonusText.setText(""+b);
		bonusText.setPosition(720-bonusText.getWidth()/2f,415);

		if(!animate){ return; }
		Log.d("game","setBonus animate");

		bonusText.clearEntityModifiers();
		bonusText.registerEntityModifier(
				new SequenceEntityModifier(
						new ScaleModifier(1.1f, 0.53f, 0.7f, EaseBounceOut.getInstance()),
						new ScaleModifier(1.1f, 0.7f, 0.53f, EaseBounceOut.getInstance())
				));

		gameScene.animate4InARow();
	}
}
