/** 
U.java
-----
By Tanguy Soto, 14/08/14 in Fermus 48200 France
Role : Contains some methods that help for dp/px conversions or bitmap loading
 **/

package ts.tangames.drop_the_rope;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

public class PixelUtility {

	/** =========== PIXEL/DP CONVERSIONS ============ */
	/*
	 * This method converts dp unit to equivalent pixels, depending on device
	 * density.
	 * 
	 * @param dp
	 *            A value in dp (density independent pixels) unit. Which we need
	 *            to convert into pixels
	 * @param context
	 *            Context to get resources and device specific display metrics
	 * @return A float value to represent px equivalent to dp depending on
	 *         device density
	 */
	public static float dpTOpx(float dp, Context context) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float px = dp * (metrics.densityDpi / 160f);
		return px;
	}

	/*
	 * This method converts device specific pixels to density independent
	 * pixels.
	 * 
	 * @param px
	 *            A value in px (pixels) unit. Which we need to convert into dp
	 * @param context
	 *            Context to get resources and device specific display metrics
	 * @return A float value to represent dp equivalent to px value
	 */
	public static float pxTOdp(float px, Context context) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float dp = px / (metrics.densityDpi / 160f);
		return dp;
	}

	/* Return the dimensions of the screen in dp
	 * 
	 * @param context
	 *            Context to get resources and device specific display metrics
	 * @return A float[] where [0]=width [1]=height
	 */
	public static float[] sizeDp(Context context) {
		float[] size = new float[2];
		//size[0] -> width
		//size[1] -> height

		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		size[0] = pxTOdp(metrics.widthPixels, context);
		size[1] = pxTOdp(metrics.heightPixels, context);

		return size;
	}
}
