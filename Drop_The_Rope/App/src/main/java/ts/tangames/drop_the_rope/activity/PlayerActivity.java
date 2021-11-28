package ts.tangames.drop_the_rope.activity;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ts.tangames.drop_the_rope.PixelUtility;
import ts.tangames.drop_the_rope.activity.adapter.PlayerItemAdapter;
import ts.tangames.drop_the_rope.factory.PlayerType;
import ts.tangames.drop_the_rope.manager.ConfigManager;
import ts.tangames.ventix.R;


public class PlayerActivity extends Activity {

    protected FrameLayout back;

    // Root
    protected FrameLayout root;

    // header
    protected RelativeLayout header;

    // grille des items
    protected GridView grid;

    // Bouton retour
    protected ImageView homeButton;

    // Titre
    protected TextView title;

    // Gems possédés
    protected ImageView gemsImage;
    public TextView gemsText;

    // Dimension de l'écran
    protected int w;
    protected int h;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Récupération des dimensions
        float[]dim= PixelUtility.sizeDp(this);
        w = (int)PixelUtility.dpTOpx(dim[0],this);
        h = (int)PixelUtility.dpTOpx(dim[1],this);

        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_player);


        back = (FrameLayout) findViewById(R.id.back_player);
        root = (FrameLayout) findViewById(R.id.root_player);

        // remplissage de la grille des cordes
        grid=(GridView) findViewById(R.id.gridview_player);
        grid.setAdapter(new PlayerItemAdapter(this));
        grid.setVerticalScrollBarEnabled(false);

        for(int i=0;i< PlayerType.values().length;i++){
            grid.getAdapter().getView(i,null,null);
        }

        header=(RelativeLayout) findViewById(R.id.header_player);

        // activation du bouton de retour au menu
        homeButton = (ImageView) findViewById(R.id.homeButton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        title = (TextView) findViewById(R.id.textView_title);

        gemsImage = (ImageView) findViewById(R.id.imageView_gem);
        gemsText = (TextView) findViewById(R.id.textView_gem);

        // paramètres lié à l'écran
        setSize();
        setMargin();

        initializeGems();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setSize(){
        gemsText.setTextSize(1,PixelUtility.pxTOdp(h/20f,this));
        gemsText.setTypeface(Typeface.createFromAsset(this.getAssets(),"fonts/libel-suit-rg.ttf"));

        title.setTextSize(1,PixelUtility.pxTOdp(h/15f,this));
        title.setTypeface(Typeface.createFromAsset(this.getAssets(),"fonts/libel-suit-rg.ttf"));

        homeButton.getLayoutParams().height=h/12;
        homeButton.getLayoutParams().width=h/12;

        gemsImage.getLayoutParams().height=h/18;
        gemsImage.getLayoutParams().width=h/18;

        header.getLayoutParams().height=(int)(h/8.2);
    }

    private void setMargin(){
        // Marges de root
        setMargins(root,h/4,h/10,h/4,h/10);

        // Titre
        title.setPadding(0,-h/145,0,0);

        // Marges du header
        header.setPadding(h/50,h/50,h/50,h/50);
        setMargins(header,0,0,0,0);
        setMargins(gemsImage,h/35,h/153,0,0);

        // Marges de la grille
        setMargins(grid,h/25,header.getLayoutParams().height,h/25,h/25);
        grid.setHorizontalSpacing(h/40);
        grid.setVerticalSpacing(h/40);
    }

    private void initializeGems(){
        int gems = ConfigManager.getInstance().getParameterInt(ConfigManager.INT_TOTAL_GEM);
        if(gems<0){
            gems=0;
        }
        gemsText.setText(""+gems);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

    @Override
    public void onResume(){
        super.onResume();

        back.setBackgroundDrawable(getResources().getDrawable(R.drawable.player_activity_background));
    }

    @Override
	public void onPause(){
		super.onPause();
        Log.d("PlayerActivity","onPause()");

		ConfigManager.getInstance().commit();
	}

    @Override
    public void onBackPressed(){
        back.setAlpha(0);

        super.onBackPressed();
    }

    public static void setMargins (View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }
}
