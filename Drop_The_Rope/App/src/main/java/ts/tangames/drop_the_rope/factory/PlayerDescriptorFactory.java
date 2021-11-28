package ts.tangames.drop_the_rope.factory;

import ts.tangames.drop_the_rope.manager.ResourcesManager;

/**
 * Created by tanguy on 02/06/16.
 */
public class PlayerDescriptorFactory {

    public static PlayerDescriptor createRopeDescriptor(PlayerType type){
        PlayerDescriptor pD;
        switch (type){
            case BASIC_PLAYER:
                pD = new PlayerDescriptor(0.8f,0.25f,0.27f, ResourcesManager.getInstance().player_region,
                        ResourcesManager.getInstance().target_region);
                break;
            case SMALL_PLAYER:
                pD = new PlayerDescriptor(0.9f,0.19f,0.27f, ResourcesManager.getInstance().player_small_region,
                        ResourcesManager.getInstance().target_region);
                break;
            case BIG_PLAYER:
                pD = new PlayerDescriptor(0.55f,0.35f,0.27f, ResourcesManager.getInstance().player_big_region,
                        ResourcesManager.getInstance().target_region);
                break;
            case BIG_TARGET_PLAYER:
                pD = new PlayerDescriptor(0.8f,0.23f,0.38f, ResourcesManager.getInstance().player_big_target_region,
                        ResourcesManager.getInstance().target_region);
                break;
            case GOLD_PLAYER:
                pD = new PlayerDescriptor(0.8f,0.25f,0.27f, ResourcesManager.getInstance().player_gold_region,
                        ResourcesManager.getInstance().target_region);
                pD.gemsMultiplier=2;
                break;
            case SILVER_PLAYER:
                pD = new PlayerDescriptor(0.8f,0.25f,0.27f, ResourcesManager.getInstance().player_silver_region,
                        ResourcesManager.getInstance().target_region);
                pD.scoreMultiplier=2;
                break;
            default:
                pD = null;
                break;
        }
        return pD;
    }
}
