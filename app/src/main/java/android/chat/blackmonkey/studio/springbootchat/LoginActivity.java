package android.chat.blackmonkey.studio.springbootchat;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.support.v4.util.Pair;
import android.support.v4.util.PatternsCompat;
import android.support.v7.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    /**
     * Common username pattern widely used in different websites.
     */
    private static final Pattern NICKNAME_PATTERN = Pattern.compile("^[\\p{L} .'-]{2,10}$");

    // UI references.
    private EditText mNameView;
    private EditText mHostView;
    private View mProgressView;
    private View mLoginFormView;
    private Disposable mSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mNameView = (EditText) findViewById(R.id.nickname);
        mHostView = (EditText) findViewById(R.id.host);

        Button loginButton = (Button) findViewById(R.id.sign_in_button);
        loginButton.setOnClickListener(view -> attemptLogin());

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    /**
     * Attempts to login the nickname specified by the login form.
     * If there are form errors (invalid nickname, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mSubscription != null && !mSubscription.isDisposed()) {
            return;
        }

        // Reset errors.
        mNameView.setError(null);
        mHostView.setError(null);

        // Store values at the time of the login attempt.
        String nickname = mNameView.getText().toString();
        String host = mHostView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid nickname.
        if (TextUtils.isEmpty(nickname)) {
            mNameView.setError(getString(R.string.error_field_required));
            focusView = mNameView;
            cancel = true;
        } else if (!isNicknameValid(nickname)) {
            mNameView.setError(getString(R.string.error_invalid_nickname));
            focusView = mNameView;
            cancel = true;
        }

        // Check for a valid host.
        if (TextUtils.isEmpty(host)) {
            mHostView.setError(getString(R.string.error_field_required));
            focusView = mHostView;
            cancel = true;
        } else if (!isHostValid(host)) {
            mHostView.setError(getString(R.string.error_invalid_host));
            focusView = mHostView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
            return;
        }

        mSubscription = Observable.just(new Pair<>(nickname, host))
                .map(this::loginInBackground)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(this::onBeforeLogin)
                .subscribe(this::onAfterLogin);
        Log.d(TAG, "mSubscription=" + mSubscription);
    }

    private boolean isNicknameValid(String name) {
        Matcher matcher = NICKNAME_PATTERN.matcher(name);
        return matcher.matches();
    }

    private boolean isHostValid(String host) {
        Matcher matcher = PatternsCompat.WEB_URL.matcher(host);
        return matcher.matches();
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private <T> Boolean loginInBackground(Pair<String, String> loginInfo) {
        String nickname = loginInfo.first;
        String host = loginInfo.second;

        Log.d(TAG, "loginInBackground() nickname=" + nickname + ", host=" + host);

        // TODO: attempt authentication against a network service.

        try {
            // Simulate network access.
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            return false;
        }

        // TODO: register the new account here.
        return true;
    }

    private void onBeforeLogin(Disposable disposable) {
        Log.d(TAG, "onBeforeLogin() disposable=" + disposable);
        showProgress(true);
    }

    private void onAfterLogin(Boolean success) {
        Log.d(TAG, "onAfterLogin() success=" + success + ", mSubscription=" + mSubscription);
        if (mSubscription != null) {
            mSubscription.dispose();
            mSubscription = null;
        }
        showProgress(false);
    }
}

