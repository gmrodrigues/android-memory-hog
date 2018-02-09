package com.gmrodrigues.androidmemoryhog;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import java.util.concurrent.locks.ReentrantLock;

/**
 * A login screen that offers login via email/password.
 */
public class MainActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    // UI references.
    private View mProgressView;
    private View mHogFormView;
    private TextView mHogInfoView;
    private Button mHogButton;
    private transient boolean  hog = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Activity act = this; //graubio

        mHogButton = (Button) findViewById(R.id.hog_button);
        mHogButton.setOnClickListener(new OnClickListener() {
            Hogger hogger = new Hogger();

            @Override
            public void onClick(View view) {
                hogger.setActivityManager((ActivityManager)getBaseContext().getSystemService(ACTIVITY_SERVICE));
                hogger.hog();
                mHogInfoView.setText(hogger.info());
            }
        });

        mHogFormView = findViewById(R.id.hog_form);
        mProgressView = findViewById(R.id.hog_progress);
        mHogInfoView = findViewById(R.id.hog_info_text);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}

