package ts.tangames.drop_the_rope.factory;

import org.andengine.opengl.texture.region.ITiledTextureRegion;

/**
 * Created by tanguy on 02/06/16.
 */
public class PlayerDescriptor {

    public final float density;
    public final float scaleFactor;

    public final float targetScaleFactor;

    public ITiledTextureRegion playerRegion;
    public ITiledTextureRegion targetRegion;

    public int scoreMultiplier = 1;
    public int gemsMultiplier = 1;

    public PlayerDescriptor(float density, float scaleFactor, float targetScaleFactor, ITiledTextureRegion playerRegion, ITiledTextureRegion targetRegion){
        this.density = density;
        this.scaleFactor=scaleFactor;

        this.targetScaleFactor = targetScaleFactor;

        this.playerRegion=playerRegion;
        this.targetRegion=targetRegion;
    }
}
