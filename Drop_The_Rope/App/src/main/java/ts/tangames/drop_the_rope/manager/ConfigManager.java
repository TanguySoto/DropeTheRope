package ts.tangames.drop_the_rope.manager;

import android.content.SharedPreferences;

import ts.tangames.drop_the_rope.activity.GameActivity;

public class ConfigManager {

	private static ConfigManager INSTANCE;

	private static GameActivity activity;

	private SharedPreferences config;
	private SharedPreferences.Editor editor;

	/* ======= PREFERENCES NAMES ======= */
	private static final String PREFS_NAME = "drop_the_ropePrefs";

	// ===== GAME ==== //
	public static final String INT_ROPE_TYPE = "rope_type";
	public static final String INT_PLAYER_TYPE = "player_type";

	public static final String INT_CURRENT_SCORE = "current_score";
	public static final String INT_BEST_SCORE = "best_score";

	public static final String INT_COLLECTED_GEM = "collected_gem";
	public static final String INT_TOTAL_GEM = "total_gem";

	public static final String INT_BOUGHT_ROPE = "bought_rope";
	public static final String INT_BOUGHT_PLAYER = "bought_player";

	public static final String STR_FIRST_LAUNCH = "first_launch";


	// ===== PREFERENCES ===== //
	public static final String STR_SOUND_ENABLED = "sound_enabled";
	public static final String STR_CANCELLED_SIGN_IN = "cancelled_sign_in";

	private ConfigManager(){
		// Chargement de la configuration
		config = activity.getSharedPreferences(PREFS_NAME,0);
		editor = config.edit();
	}

	public static void prepareManager(GameActivity a){
		activity=a;
	}
	
	public static ConfigManager getInstance(){
		if(INSTANCE==null){
			INSTANCE=new ConfigManager();
		}
		return INSTANCE;
	}

	public void setParameter(String key, String value){
		editor.putString(key,value);
	}

	public void setParameter(String key, int value){
		editor.putInt(key,value);
	}

	public String getParameter(String key){
		editor.commit();
		return config.getString(key,"not found");
	}

	public int getParameterInt(String key){
		editor.commit();
		return config.getInt(key,-1);
	}

	public void commit(){
		editor.commit();
	}

}
