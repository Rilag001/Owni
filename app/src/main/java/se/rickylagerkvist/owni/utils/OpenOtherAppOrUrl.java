package se.rickylagerkvist.owni.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.view.View;

public class OpenOtherAppOrUrl {

    // generic open app with intent or show snackbar with install URL action
    static public void openAppOrOpenSnackbarWithInstallAction(String appPackageName, String appName, final String appUrl, View view, final Context context) {

        PackageManager pm = context.getPackageManager();
        Intent appStartIntent = pm.getLaunchIntentForPackage(appPackageName);
        if (null != appStartIntent) {
            context.startActivity(appStartIntent);
        } else {
            Snackbar snackbar = Snackbar
                    .make(view, appName + " is not installed.", Snackbar.LENGTH_LONG)
                    .setAction("INSTALL " + appName.toUpperCase(), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //goToUrl(appUrl, getBaseContext());
                            OpenOtherAppOrUrl.goToUrl(appUrl, context);
                        }
                    });
            snackbar.show();
        }

    }

    // generic go to Url
    static public void goToUrl(String url, android.content.Context context) {
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        launchBrowser.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(launchBrowser);
    }
}
