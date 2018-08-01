package io.github.project_travel_mate;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.airbnb.lottie.LottieAnimationView;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static utils.Constants.API_LINK_V2;
import static utils.Constants.READ_NOTIF_STATUS;
import static utils.Constants.USER_TOKEN;

public class SettingsFragment extends Fragment {

    @BindView(R.id.switch_notification)
    Switch notificationSwitch;
    @BindView(R.id.old_password)
    EditText oldPasswordText;
    @BindView(R.id.new_password)
    EditText newPasswordText;
    @BindView(R.id.connfirm_password)
    EditText confirmPasswordText;
    @BindView(R.id.done_button)
    Button doneButton;
    @BindView(R.id.layout)
    LinearLayout layout;
    @BindView(R.id.animation_view)
    LottieAnimationView animationView;

    private String mToken;
    private Handler mHandler;
    private Activity mActivity;
    private SharedPreferences mSharedPrefrences;

    public SettingsFragment() {
        //required public constructor
    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, view);

        mSharedPrefrences = PreferenceManager.getDefaultSharedPreferences(mActivity);
        boolean readNotifStatus = mSharedPrefrences.getBoolean(READ_NOTIF_STATUS, true);
        mToken = mSharedPrefrences.getString(USER_TOKEN, null);
        mHandler = new Handler(Looper.getMainLooper());

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkEmptyText())
                    checkPasswordMatch();
            }
        });

        if (readNotifStatus) {
            notificationSwitch.setChecked(true);
        }
        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    mSharedPrefrences.edit().putBoolean(READ_NOTIF_STATUS, false).apply();
                } else {
                    mSharedPrefrences.edit().putBoolean(READ_NOTIF_STATUS, true).apply();
                }
            }
        });
        return view;
    }

    /**
     * Checks if any editText is left empty by user
     * @return true if all fields are filled
     */
    private boolean checkEmptyText() {
        if (oldPasswordText.getText().toString().equals("")) {
            oldPasswordText.setError(getString(R.string.required_error));
            oldPasswordText.requestFocus();
            return false;
        } else if (newPasswordText.getText().toString().equals("")) {
            newPasswordText.setError(getString(R.string.required_error));
            newPasswordText.requestFocus();
            return false;
        } else if (confirmPasswordText.getText().toString().equals("")) {
            confirmPasswordText.setError(getString(R.string.required_error));
            confirmPasswordText.requestFocus();
            return false;
        } else {
            return true;
        }

    }

    /**
     * Checks if newPassword and confirmPassword match
     */
    private void checkPasswordMatch() {
        String oldPassword = oldPasswordText.getText().toString();
        String newPassword = newPasswordText.getText().toString();
        String confirmPassword = confirmPasswordText.getText().toString();
        if (validatePassword(newPassword)) {
            if (newPassword.equals(confirmPassword)) {
                changePassword(newPassword, oldPassword);
            } else {
                Snackbar snackbar = Snackbar
                        .make(mActivity.findViewById(android.R.id.content),
                                R.string.passwords_check, Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        }
    }

    /**
     * Validates the given password, checks if given password proper format as standard password string
     * @param passString password string to be validate
     * @return Boolean returns true if email format is correct, otherwise false
     */
    public boolean validatePassword(String passString) {
        if (passString.length() >= 8) {
            Pattern pattern;
            Matcher matcher;
            final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
            pattern = Pattern.compile(PASSWORD_PATTERN);
            matcher = pattern.matcher(passString);

            if (matcher.matches()) {
                return true;
            } else {
                Snackbar snackbar = Snackbar
                        .make(mActivity.findViewById(android.R.id.content),
                                R.string.invalid_password, Snackbar.LENGTH_LONG);
                snackbar.show();
                return false;
            }
        } else {
            Snackbar snackbar = Snackbar
                    .make(mActivity.findViewById(android.R.id.content),
                            R.string.password_length, Snackbar.LENGTH_LONG);
            snackbar.show();
            return false;
        }
    }

    //TODO :: Update API and check its functionality
    public void changePassword(String newPassword, String oldPassword) {

        String uri = API_LINK_V2 + "update-password";
        Log.v("EXECUTING", uri);
        //Set up client
        OkHttpClient client = new OkHttpClient();
        //Execute request
        final Request request = new Request.Builder()
                .header("Authorization", "Token " + mToken)
                .url(uri)
                .build();
        //Setup callback
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                Log.e("Request Failed", "Message : " + e.getMessage());
                //mHandler.post(() ->// networkError());
            }
            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                mHandler.post(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        Snackbar snackbar = Snackbar.make(mActivity.findViewById(android.R.id.content),
                                getString(R.string.password_updated), Snackbar.LENGTH_LONG);
                        snackbar.show();
                    } else {
                        networkError();
                    }
                });
            }
        });
    }

    /**
     * Plays the network lost animation in the view
     */
    private void networkError() {
        layout.setVisibility(View.INVISIBLE);
        animationView.setVisibility(View.VISIBLE);
        animationView.setAnimation(R.raw.network_lost);
        animationView.playAnimation();
    }

}
