package ts.tangames.drop_the_rope.activity.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ts.tangames.drop_the_rope.activity.GameActivity;

/**
 * Created by tanguy on 05/06/16.
 */
public class NetWorkReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
		/*boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY,
				false);
		String reason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
		boolean isFailover = intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);
		*/

        GameActivity A = ((GameActivity) context);

        A.setConnectionState();

		/*NetworkInfo otherNetworkInfo = (NetworkInfo) intent
				.getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);*/

        // do application-specific task(s) based on the current network state, such
        // as enabling queuing of HTTP requests when currentNetworkInfo is connected etc.
    }
}
