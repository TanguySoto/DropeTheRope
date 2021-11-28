package ts.tangames.drop_the_rope.factory;

import android.graphics.Color;

/**
 * Created by tanguy on 02/06/16.
 */
public class RopeDescriptorFactory {

    public static RopeDescriptor createRopeDescriptor(RopeType type){
        RopeDescriptor rD;
        switch (type){
            case NORMAL_ROPE:
                rD = new RopeDescriptor(6,35,100, 5, 1,1.5f,1,0.8f);
                break;
            case SHORT_ROPE:
                rD = new RopeDescriptor(4,20,70, 5, 1,1.5f,1,0.9f);
                break;
            case LONG_ROPE:
                rD = new RopeDescriptor(8,50,160, 5, 1,2.6f,1.20f,0.75f);
                break;
            case ONE_PIECE_ROPE:
                rD = new RopeDescriptor(2,30,90, 6, 1,2f,1.1f,0.75f);
                break;
            case GOLD_ROPE:
                rD = new RopeDescriptor(6,35,100, 5, 1,1.5f,1,0.8f);
                rD.gemsMultiplier=2;
                rD.color= Color.argb(255,255,197,0);
                break;
            case SILVER_ROPE:
                rD = new RopeDescriptor(6,35,100, 5, 1,1.5f,1,0.8f);
                rD.scoreMultiplier=2;
                rD.color= Color.argb(255,205,205,205);
                break;
            default:
                rD =  null;
                break;
        }
        return rD;
    }
}
