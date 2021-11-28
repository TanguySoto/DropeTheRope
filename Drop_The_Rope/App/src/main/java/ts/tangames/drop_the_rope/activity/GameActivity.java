/**
 *  @author	Tanguy SOTO
 * 	@date	25/12/15
 * 
 * 	Activité principale du jeu
 */

package ts.tangames.drop_the_rope.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.games.Games;
import com.jirbo.adcolony.AdColony;
import com.jirbo.adcolony.AdColonyAd;
import com.jirbo.adcolony.AdColonyAdAvailabilityListener;
import com.jirbo.adcolony.AdColonyAdListener;
import com.jirbo.adcolony.AdColonyVideoAd;

import org.andengine.engine.Engine;
import org.andengine.engine.LimitedFPSEngine;
import org.andengine.engine.camera.SmoothCamera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.opengl.view.RenderSurfaceView;
import org.andengine.ui.activity.BaseGameActivity;

import java.io.IOException;

import ts.tangames.drop_the_rope.activity.receiver.NetWorkReceiver;
import ts.tangames.drop_the_rope.manager.ConfigManager;
import ts.tangames.drop_the_rope.manager.ResourcesManager;
import ts.tangames.drop_the_rope.manager.SceneManager;
import ts.tangames.drop_the_rope.scene.GameScene;
import ts.tangames.drop_the_rope.scene.MainMenuScene;
import ts.tangames.ventix.R;

public class GameActivity extends BaseGameActivity implements GoogleApiClient.ConnectionCallbacks,
		OnConnectionFailedListener,AdColonyAdAvailabilityListener, AdColonyAdListener {

	// ---------------------------------
	// VARIABLES
	// ---------------------------------

	// Google Play services
	protected GoogleApiClient mGoogleApiClient;
	// Request code to use when launching the resolution activity
	private static final int REQUEST_RESOLVE_ERROR = 1001;
	// Unique tag for the error dialog fragment
	private static final String DIALOG_ERROR = "dialog_error";
	// Bool to track whether the app is already resolving an error
	private boolean mResolvingError = false;
	private static final String STATE_RESOLVING_ERROR = "resolving_error";

	// AdColony video availability
	private boolean lastAvailable = false;

	// NetWorkReceiver
	private NetWorkReceiver mNetWorkReceiver;

	// Dimensions
	private final static int WIDTH = 800;
	private final static int HEIGHT = 480;

	// Caméra
	private SmoothCamera camera;
	public static final float MAX_ZOOM_FACTOR=0.03f;

	// ---------------------------------
	// CONSTRUCTORS
	// ---------------------------------

	// Initialisation de la caméra et des options du moteur
	public EngineOptions onCreateEngineOptions() {
		camera = new SmoothCamera(0, 0, WIDTH, HEIGHT,100,100,MAX_ZOOM_FACTOR);

		EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_SENSOR,
				new RatioResolutionPolicy(WIDTH, HEIGHT), this.camera);
		engineOptions.getAudioOptions().setNeedsMusic(true).setNeedsSound(true);
		engineOptions.setWakeLockOptions(WakeLockOptions.SCREEN_ON);
		engineOptions.getTouchOptions().setNeedsMultiTouch(true);

		return engineOptions;
	}

	// Création du moteur du jeu
	@Override
	public Engine onCreateEngine(EngineOptions pEngineOptions) {
		return new LimitedFPSEngine(pEngineOptions, 60);
	}

	// Préparation des resources et création du ResourcesManager
	public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback)
			throws IOException {
		ResourcesManager.prepareManager(mEngine, this, camera, getVertexBufferObjectManager());
		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) throws IOException {
		SceneManager.getInstance().createSplashScene(pOnCreateSceneCallback);
	}

	public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback)
			throws IOException {
		mEngine.registerUpdateHandler(new TimerHandler(1.5f, new ITimerCallback() {
			public void onTimePassed(final TimerHandler pTimerHandler) {
				mEngine.unregisterUpdateHandler(pTimerHandler);
				SceneManager.getInstance().createMenuScene();
			}
		}));
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}

	// ---------------------------------
	// GETTERS/SETTERS
	// ---------------------------------

	public static int getWidth() {
		return WIDTH;
	}

	public static int getHeight() {
		return HEIGHT;
	}

	public GoogleApiClient getGoogleApiClient(){
		return mGoogleApiClient;
	}

	// ---------------------------------
	// ACTIVITY LIFECYCLE
	// ---------------------------------

	@Override
	public void onBackPressed(){
		Log.d("GameActivity","ONBACKPRESSED");

		SceneManager.getInstance().getCurrentScene().onBackKeyPressed();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(STATE_RESOLVING_ERROR, mResolvingError);
	}

	@Override
	protected void onSetContentView() {
		//Creating the parent frame layout:
		final FrameLayout frameLayout = new FrameLayout(this);

		//Creating its layout params, making it fill the screen.
		final FrameLayout.LayoutParams frameLayoutLayoutParams =
				new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT,
						FrameLayout.LayoutParams.FILL_PARENT);



		//Creating the banner view.
		AdView mAdView = createAndInitBanner();
		SceneManager.getInstance().setmAdView(mAdView);

		// Creating the interstital view
		InterstitialAd interstitialAd = createAndInitInterstitial();
		SceneManager.getInstance().setmInterstitialAd(interstitialAd);

		//Creating the banner layout params. With this params, the ad will be placed in the top of the screen, middle horizontally.
		final FrameLayout.LayoutParams bannerViewLayoutParams =
				new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
						FrameLayout.LayoutParams.WRAP_CONTENT,
						Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);


		//Creating AndEngine's view.
		this.mRenderSurfaceView = new RenderSurfaceView(this);
		mRenderSurfaceView.setRenderer(mEngine, this);

		//createSurfaceViewLayoutParams is an AndEngine method for creating the params for its view.
		final FrameLayout.LayoutParams surfaceViewLayoutParams = new FrameLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT);
		surfaceViewLayoutParams.gravity = Gravity.CENTER;


		//Adding the views to the frame layout.
		frameLayout.addView(this.mRenderSurfaceView, surfaceViewLayoutParams);
		frameLayout.addView(mAdView, bannerViewLayoutParams);

		//Setting content view
		this.setContentView(frameLayout, frameLayoutLayoutParams);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// add network receiver to handle network changes
		mNetWorkReceiver = new NetWorkReceiver();
		registerReceiver(mNetWorkReceiver,
				new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

		// Create a GoogleApiClient instance
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Games.API)
				.addConnectionCallbacks(this)
            	.addOnConnectionFailedListener(this)
                .build();

		// GOOGLE ADMOB initialization
		MobileAds.initialize(getApplicationContext(), getString(R.string.admob_app_id));


		// test if activity was already resolving an error with GoogleApiClient
		mResolvingError = savedInstanceState != null
            && savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false);


		ConfigManager.prepareManager(this);
	}

	@Override
	public void onStart(){
		super.onStart();

		String cancelledSignIn = ConfigManager.getInstance().getParameter(ConfigManager.STR_CANCELLED_SIGN_IN);
		if("false".equals(cancelledSignIn) || "not found".equals(cancelledSignIn)){
			mGoogleApiClient.connect();
		}
	}

	@Override
	public void onResume(){
		super.onResume();

		// Mets à jour les infos affichées (gems...)
		if(SceneManager.getInstance().getCurrentSceneType()== SceneManager.SceneType.SCENE_MENU){
			MainMenuScene scene = (MainMenuScene) SceneManager.getInstance().getCurrentScene();
			scene.majDisplayedGems();
		}

		// AdColony
		AdColony.resume(this);
	}

	@Override
	public void onPause(){
		super.onPause();
		Log.d("GameActivity","onPause()");

		AdColony.pause();

		ConfigManager.getInstance().commit();

		if(SceneManager.getInstance().getCurrentSceneType()== SceneManager.SceneType.SCENE_GAME){
			((GameScene)SceneManager.getInstance().getCurrentScene()).pauseScene();
		}
	}

	@Override
	public void onStop(){
		super.onStop();

		mGoogleApiClient.disconnect();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		// remove network receiver
		unregisterReceiver(mNetWorkReceiver);
	}

	public void exit(){
		Log.d("GameActivity","EXIT");

		onPause();
		onStop();
		onDestroy();
		System.exit(0);
	}

	// ---------------------------------
	// ADCOLONY CALLBACKS METHODS
	// ---------------------------------

	@Override
	public void onAdColonyAdAvailabilityChange(boolean available, String zoneId) {
		lastAvailable = available;

		MainMenuScene scene = SceneManager.getInstance().getMenuScene();
		if(scene!=null){
			scene.majVideoButton(available && isOnline());
		}
	}

	@Override
	public void onAdColonyAdAttemptFinished(AdColonyAd ad) {
		Log.w("ADC", "finished");
		if (ad.shown() && !ad.canceled() && !ad.noFill() && !ad.skipped()) { // count has "seen"
			int gems = ConfigManager.getInstance().getParameterInt(ConfigManager.INT_TOTAL_GEM);
			gems+=getResources().getInteger(R.integer.adcolony_gems_reward);
			ConfigManager.getInstance().setParameter(ConfigManager.INT_TOTAL_GEM,gems);

			MainMenuScene scene = SceneManager.getInstance().getMenuScene();
			if(scene!=null){
				scene.majDisplayedGems();
			}
		} else { // not count has "seen"

		}
	}

	@Override
	public void onAdColonyAdStarted(AdColonyAd adColonyAd) {

	}

	private boolean isOnline(){
		ConnectivityManager cm =(ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

		return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
	}

	public void setConnectionState(){
		MainMenuScene scene = SceneManager.getInstance().getMenuScene();
		if(scene!=null){
			scene.majVideoButton(lastAvailable && isOnline());
		}
	}

	public boolean getLastAvailable() {
		return lastAvailable;
	}

	public void watchVideo(){
		AdColonyVideoAd ad1 = new AdColonyVideoAd(getString(R.string.adcolony_zone_id)).withListener(this);
		ad1.show();
	}

	// ---------------------------------
	// GOOGLE ADMOB METHODS
	// ---------------------------------

	public void requestNewInterstitial(final AdRequest adRequest) {
		SceneManager.getInstance().getmInterstitialAd().loadAd(adRequest);
	}

	private InterstitialAd createAndInitInterstitial(){

		InterstitialAd mInterstitialAd = new InterstitialAd(this);
		mInterstitialAd.setAdUnitId(getString(R.string.interstitial_id));

		final AdRequest adRequest = new AdRequest.Builder()
				.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
				.addTestDevice("605D8EE33DA567C14FC078EFEB23A91B")
				.addTestDevice("B22213B0CD9A828585D1BD126D498197")
				.build();

		mInterstitialAd.setAdListener(new AdListener() {
			@Override
			public void onAdClosed() {
				requestNewInterstitial(adRequest);
			}
		});

		mInterstitialAd.loadAd(adRequest);

		return mInterstitialAd;
	}

	private AdView createAndInitBanner(){
		AdView mAdView = new AdView(this);
		mAdView.setAdUnitId(getString(R.string.banner_id));
		mAdView.setAdSize(AdSize.SMART_BANNER);

		AdRequest adRequest = new AdRequest.Builder()
				.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
				.addTestDevice("605D8EE33DA567C14FC078EFEB23A91B")
				.addTestDevice("B22213B0CD9A828585D1BD126D498197")
				.build();
		mAdView.loadAd(adRequest);

		return mAdView;
	}


	// ---------------------------------
	// GOOGLE SERVICES METHODS
	// ---------------------------------

	// CallBacks methods

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("GameActivity","onActivityResult()");
		if (requestCode == REQUEST_RESOLVE_ERROR) {
			Log.d("GameActivity","onActivityResult() 2 c:"+resultCode);
			mResolvingError = false;
			if (resultCode == Activity.RESULT_OK) {
				Log.d("GameActivity","onActivityResult() 3");
				// Make sure the app is not already connected or attempting to connect
				if (!mGoogleApiClient.isConnecting() &&
						!mGoogleApiClient.isConnected()) {
					Log.d("GameActivity","onActivityResult() 4");
					mGoogleApiClient.connect();
				}
			}
			else if(resultCode == Activity.RESULT_CANCELED){
				ConfigManager.getInstance().setParameter(ConfigManager.STR_CANCELLED_SIGN_IN,"true");
			}
			else {
				toastOnUiThread(getString(R.string.cant_connect), Toast.LENGTH_SHORT);
			}
		}
	}

	@Override
	public void onConnectionFailed(@NonNull ConnectionResult result) {
		Log.d("GameActivity","GOOGLE PLAY GAMES CONNECTION FAILED");
		if (mResolvingError) {
			// Already attempting to resolve an error.
			return;
		} else if (result.hasResolution()) {
			Log.d("GameActivity","GOOGLE PLAY GAMES hasResolution() m:"+result.getErrorMessage()+" c:"+result.getErrorCode());
			try {
				mResolvingError = true;
				result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
			} catch (IntentSender.SendIntentException e) {
				Log.d("GameActivity","GOOGLE PLAY GAMES connect() again");
				// There was an error with the resolution intent. Try again.
				mGoogleApiClient.connect();
			}
		} else {
			Log.d("GameActivity","GOOGLE PLAY GAMES no resolution available.");
			// Show dialog using GoogleApiAvailability.getErrorDialog()
			showErrorDialog(result.getErrorCode());
			mResolvingError = true;
		}

	}

	@Override
	public void onConnected(@Nullable Bundle bundle) {
		Log.d("GameActivity","GOOGLE PLAY GAMES CONNECTED");
	}

	@Override
	public void onConnectionSuspended(int i) {

	}

	// The rest of this code is all about building the error dialog

	/* Creates a dialog for an error message */
	private void showErrorDialog(int errorCode) {
		// Create a fragment for the error dialog
		ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
		// Pass the error that should be displayed
		Bundle args = new Bundle();
		args.putInt(DIALOG_ERROR, errorCode);
		dialogFragment.setArguments(args);
		dialogFragment.show(getFragmentManager(), "errordialog");
	}

	/* Called from ErrorDialogFragment when the dialog is dismissed. */
	public void onDialogDismissed() {
		mResolvingError = false;
	}

	/* A fragment to display an error dialog */
	public static class ErrorDialogFragment extends DialogFragment {
		public ErrorDialogFragment() { }

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Get the error code and retrieve the appropriate dialog
			int errorCode = this.getArguments().getInt(DIALOG_ERROR);
			return GoogleApiAvailability.getInstance().getErrorDialog(
					this.getActivity(), errorCode, REQUEST_RESOLVE_ERROR);
		}

		@Override
		public void onDismiss(DialogInterface dialog) {
			((GameActivity) getActivity()).onDialogDismissed();
		}
	}
}
