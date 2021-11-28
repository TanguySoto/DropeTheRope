package ts.tangames.drop_the_rope.activity.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import ts.tangames.drop_the_rope.PixelUtility;
import ts.tangames.drop_the_rope.activity.RopeActivity;
import ts.tangames.drop_the_rope.factory.RopeType;
import ts.tangames.drop_the_rope.manager.ConfigManager;
import ts.tangames.ventix.R;

public class RopeItemAdapter extends BaseAdapter{

    // =======================
    // ATTRIBUTS
    // =======================

    private static final String tag="RopeItemAdapter";

    // DATA
    protected String [] description;
    protected int[] price;
    protected int [] imageId = {R.drawable.rope_normal,R.drawable.rope_short,
                                R.drawable.rope_long,R.drawable.rope_one_piece,
                                R.drawable.rope_gold,R.drawable.rope_silver};

    protected Holder[] holders;

    // Dimension de l'écran en pixels
    protected int w;
    protected int h;

    // Context
    protected RopeActivity context;
    protected static LayoutInflater inflater=null;


    // =======================
    // CONSTRUCTEUR
    // =======================

    public RopeItemAdapter(RopeActivity a) {
        this.context=a;

        // Récupération des dimensions
        float[]dim= PixelUtility.sizeDp(a);
        w = (int)PixelUtility.dpTOpx(dim[0],a);
        h = (int)PixelUtility.dpTOpx(dim[1],a);

        // Récupération des données
        description = new String[RopeType.values().length];
        description[0]=a.getString(R.string.normal_rope);
        description[1]=a.getString(R.string.short_rope);
        description[2]=a.getString(R.string.long_rope);
        description[3]=a.getString(R.string.one_piece_rope);
        description[4]=a.getString(R.string.gold_rope);
        description[5]=a.getString(R.string.silver_rope);

        price = new int[RopeType.values().length];
        price[0]=a.getResources().getInteger(R.integer.normal_rope_price);
        price[1]=a.getResources().getInteger(R.integer.short_rope_price);
        price[2]=a.getResources().getInteger(R.integer.long_rope_price);
        price[3]=a.getResources().getInteger(R.integer.one_piece_rope_price);
        price[4]=a.getResources().getInteger(R.integer.gold_rope_price);
        price[5]=a.getResources().getInteger(R.integer.silver_rope_price);

        holders = new Holder[description.length];

        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    // =======================
    // CALLBACKS METHODS
    // =======================

    @Override
    public int getCount() {
        return description.length;
    }

    @Override
    public Holder getItem(int position) {
        return holders[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class Holder{

        // Layouts
        LinearLayout rootLayout;

            LinearLayout mainLayout;

                ImageView ropeImage;

                LinearLayout buttonPriceLayout;

                    Button b;

                    LinearLayout priceLayout;

                        TextView priceText;
                        ImageView coinImage;

            TextView descriptionText;

        // Attributs
        int pos;

        boolean isHidden=false;
        boolean isBought=false;
        boolean isActivated=false;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Log.d(tag,"getView():"+position);

        // *****************************
        // Recyclage de l'item

        if(holders[position]!=null && holders[position].rootLayout !=null){
            return holders[position].rootLayout;
        }


        // *****************************
        // Pas de recyclage

        final ConfigManager conf = ConfigManager.getInstance();

        // Création de l'item
        final View rowView = inflater.inflate(R.layout.rope_item, null);
        final Holder holder=createHolder(rowView,position);
        holders[position]=holder;

        // Initialization de l'item
        initializeHolder(holder,conf);

        return rowView;
    }


    // ========================
    // CREATION METHODS
    // ========================

    // Création de la class réprésentant le l'item
    private Holder createHolder(View rowView, int position){

        // création
        Holder holder=new Holder();

        // récupération des éléments
        holder.rootLayout =(LinearLayout)rowView;

            holder.mainLayout =(LinearLayout) rowView.findViewById(R.id.main_layout);

                holder.ropeImage =(ImageView) rowView.findViewById(R.id.rope_image);

                holder.buttonPriceLayout =(LinearLayout) rowView.findViewById(R.id.button_price_layout);

                    holder.b=(Button) rowView.findViewById(R.id.button);

                    holder.priceLayout=(LinearLayout) rowView.findViewById(R.id.price_layout);

                        holder.priceText =(TextView) rowView.findViewById(R.id.price_text);
                        holder.coinImage =(ImageView) rowView.findViewById(R.id.coin_image);


            holder.descriptionText =(TextView) rowView.findViewById(R.id.description_text);

        holder.pos=position;

        return holder;
    }

    // Initialize les paramètres visuels pour la première fois
    private void initializeHolder(final Holder holder, final ConfigManager conf){

        // Paramétrages par défaut
        // =======================

        // contenu et visibilé par défaut
        holder.descriptionText.setText(description[holder.pos]);
        holder.priceText.setText(""+price[holder.pos]);

        Typeface font = Typeface.createFromAsset(context.getAssets(),"fonts/libel-suit-rg.ttf");
        holder.descriptionText.setTypeface(font);
        holder.b.setTypeface(font);
        holder.priceText.setTypeface(font);

        holder.buttonPriceLayout.setVisibility(View.GONE);
        holder.descriptionText.setVisibility(View.GONE);

        // taille
        holder.b.setTextSize(1,PixelUtility.pxTOdp(h/30f,context));
        holder.priceText.setTextSize(1,PixelUtility.pxTOdp(h/20f,context));
        holder.descriptionText.setTextSize(1,PixelUtility.pxTOdp(h/30f,context));

        holder.ropeImage.getLayoutParams().height=h/7;
        holder.ropeImage.getLayoutParams().width=h/7;

        holder.coinImage.getLayoutParams().height=h/20;
        holder.coinImage.getLayoutParams().width=h/20;

        // marge
        RopeActivity.setMargins(holder.ropeImage,h/35,h/35,h/35,h/35);
        RopeActivity.setMargins(holder.b,h/35,h/100,h/35,0);
        RopeActivity.setMargins(holder.coinImage,h/35,h/162,0,0);
        RopeActivity.setMargins(holder.descriptionText,h/35,0,h/35,h/35);


        // Détermination de la config. de l'item
        // =====================================

        Integer bought_rope = conf.getParameterInt(ConfigManager.INT_BOUGHT_ROPE);
        final String bought;

        if(bought_rope==-1){
            bought_rope=(int) Math.pow(10,RopeType.values().length) + 1;
            conf.setParameter(ConfigManager.INT_BOUGHT_ROPE,bought_rope);
        }
        bought=bought_rope.toString();

        holder.isBought= ('1'==bought.charAt(bought.length()-holder.pos-1));
        holder.isActivated= (holder.pos==conf.getParameterInt(ConfigManager.INT_ROPE_TYPE));


        // Paramétrages en fonction de la config.
        // ======================================

        if(holder.isBought){
            holder.ropeImage.setImageDrawable(context.getResources().getDrawable(imageId[holder.pos]));
            boughtState(holder);

            if(holder.isActivated){
                activatedState(holder);
            }
        }
        else {
            unBoughtState(holder);
        }

        if(!holder.isHidden){
            showHolder(holder);
        }


        // OnClickListeners
        // ======================================

        // achète ou active l'item
        holder.b.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                clickOnButton(holder,conf);
            }
        });

        // cache ou affiche les détails de l'item
        holder.rootLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //clickOnRootLayout(holder);
            }
        });
    }


    // ========================
    // CLICS METHODS
    // ========================

    // Clique sur la vue principale
    private void clickOnRootLayout(Holder holder){

        int nb_col=Integer.parseInt(context.getResources().getString(R.string.columns_number));

        if(holder.isHidden){
            for(int i=0;i<getCount();i++){
                // cache les autres sauf fin de columns
                if(i!=holder.pos){
                    holders[i].isHidden=true;
                    hideHolder(holders[i]);

                    if((i+1)%nb_col==0 && i/nb_col==holder.pos/nb_col){
                        specialState(holders[i]);
                    }
                }
                else {
                    // rend visible celui-ci
                    holder.isHidden=false;
                    showHolder(holder);
                }
            }
        }
        else {
            // cache celui-ci
            holder.isHidden=true;
            hideHolder(holder);

            normalState(holders[((holder.pos)/nb_col+1)*nb_col-1]);
        }
    }

    // Clique sur le bouton
    private void clickOnButton(Holder holder, ConfigManager conf){
        int mPrice = price[holder.pos];

        // selection de ce joueur
        if(holder.isBought){
            conf.setParameter(ConfigManager.INT_ROPE_TYPE,holder.pos);

            // changement des états
            for(int i=0;i<getCount();i++){
                if(i!=holder.pos){
                    if(holders[i].isBought) {
                        holders[i].isActivated = false;
                        boughtState(holders[i]);
                    }
                }
                else {
                    holder.isActivated=true;
                    activatedState(holder);
                }
            }
        }
        // achat de ce joueur et sélection de celui-ci
        else {
            int gems = conf.getParameterInt(ConfigManager.INT_TOTAL_GEM);
            if(mPrice<=gems){
                gems-=mPrice;
                conf.setParameter(ConfigManager.INT_TOTAL_GEM,gems);
                context.gemsText.setText(""+gems);

                int bought_rope = conf.getParameterInt(ConfigManager.INT_BOUGHT_ROPE);
                bought_rope+=Math.pow(10,holder.pos);
                conf.setParameter(ConfigManager.INT_BOUGHT_ROPE,bought_rope);
                conf.setParameter(ConfigManager.INT_ROPE_TYPE,holder.pos);

                // activation de tous les autres
                for(int i=0;i<getCount();i++){
                    if(i!=holder.pos){
                        if(holders[i].isBought) {
                            holders[i].isActivated = false;
                            boughtState(holders[i]);
                        }
                    }
                    else {
                        holder.isActivated=true;
                        holder.isBought=true;
                        holder.ropeImage.setImageDrawable(context.getResources().getDrawable(imageId[holder.pos]));
                        boughtState(holder);
                        activatedState(holder);
                    }
                }
            }
            // Trop chère
            else {
                Toast.makeText(context,context.getString(R.string.cant_buy),Toast.LENGTH_SHORT).show();
            }
        }
    }


    // ========================
    // CHANGEMENT DE LOOK
    // ========================

    private void hideHolder(Holder holder){
        holder.descriptionText.setVisibility(View.GONE);
        holder.buttonPriceLayout.setVisibility(View.GONE);
    }

    private void showHolder(Holder holder){
        holder.descriptionText.setVisibility(View.VISIBLE);
        holder.buttonPriceLayout.setVisibility(View.VISIBLE);
    }

    private void unBoughtState(Holder holder){
        holder.rootLayout.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.item_unbought_background));

        holder.priceLayout.setVisibility(View.VISIBLE);

        holder.b.setEnabled(true);
        holder.b.setText(context.getString(R.string.buy));

        holder.descriptionText.setText("?");
    }

    private void boughtState(Holder holder){
        holder.rootLayout.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.item_bought_background));

        holder.priceLayout.setVisibility(View.INVISIBLE);

        holder.b.setEnabled(true);
        holder.b.setText(context.getString(R.string.select));

        holder.descriptionText.setText(description[holder.pos]);
    }

    private void activatedState(Holder holder){
        holder.rootLayout.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.item_activated_background));

        holder.b.setEnabled(false);
        holder.b.setText(context.getString(R.string.selected));
    }

    private void specialState(Holder holder) {
        hideHolder(holder);

        holder.buttonPriceLayout.setVisibility(View.INVISIBLE);
        holder.descriptionText.setVisibility(View.INVISIBLE);
    }

    private void normalState(Holder holder){
        hideHolder(holder);

        holder.buttonPriceLayout.setVisibility(View.GONE);
        holder.descriptionText.setVisibility(View.GONE);
    }
}