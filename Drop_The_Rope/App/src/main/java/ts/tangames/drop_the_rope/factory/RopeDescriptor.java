package ts.tangames.drop_the_rope.factory;

import android.graphics.Color;

/**
 * Created by tanguy on 02/06/16.
 */
public class RopeDescriptor {

    public final int nb_morceaux;

    public final int height;    // longueur de la corde sur l'axe verticale
    public final int width;     // longueur de la corde sur l'axe horizontale

    public final int thickness; // épaisseur de la corde

    public final int handleWidth; // longueur de la poignée de la corde

    public final float density;

    public final float relanceW; // relance horizontale
    public final float relanceH; // relance verticale

    public int scoreMultiplier = 1;
    public int gemsMultiplier = 1;

    public int color = Color.argb(255,130,130,130); // défaut color : gris

    public RopeDescriptor(int nb_morceaux, int height, int width, int thickness, int handleWidth, float density, float relanceW, float relanceH){
        this.nb_morceaux=nb_morceaux;

        this.height = height;
        this.width = width;
        this.thickness = thickness;

        this.handleWidth = handleWidth;

        this.density = density;

        this.relanceW = relanceW;
        this.relanceH = relanceH;
    }
}
