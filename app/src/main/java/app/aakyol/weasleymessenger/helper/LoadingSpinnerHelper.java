package app.aakyol.weasleymessenger.helper;

import android.app.Activity;
import android.view.View;
import android.widget.ProgressBar;

import app.aakyol.weasleymessenger.R;

public class LoadingSpinnerHelper {

    private static ProgressBar progressBar;

    public static void setLoadingSpinner(final Activity activity) {
        progressBar = activity.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);
    }

    public static void setSpinnerVisible() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public static void setSpinnerGone() {
        progressBar.setVisibility(View.GONE);
    }
}
