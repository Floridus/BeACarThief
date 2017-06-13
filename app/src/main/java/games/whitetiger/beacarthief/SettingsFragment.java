package games.whitetiger.beacarthief;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class SettingsFragment extends Fragment implements ICallback {

    private Button btnChangeEmail, btnChangePassword, btnRemoveUser,
            changeEmail, changePassword, signOut;

    private EditText emailInput, oldPasswordInput, newPasswordInput;
    private ProgressBar progressBar;
    private APIAuth auth;
    View myView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_settings, container, false);

        auth = new APIAuth(getActivity());

        btnChangeEmail = (Button) myView.findViewById(R.id.change_email_button);
        btnChangePassword = (Button) myView.findViewById(R.id.change_password_button);
        btnRemoveUser = (Button) myView.findViewById(R.id.remove_user_button);
        changeEmail = (Button) myView.findViewById(R.id.changeEmail);
        changePassword = (Button) myView.findViewById(R.id.changePass);
        signOut = (Button) myView.findViewById(R.id.sign_out);

        emailInput = (EditText) myView.findViewById(R.id.new_email);
        oldPasswordInput = (EditText) myView.findViewById(R.id.oldPassword);
        newPasswordInput = (EditText) myView.findViewById(R.id.newPassword);

        progressBar = (ProgressBar) myView.findViewById(R.id.progressBar);

        btnChangeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailInput.setVisibility(View.VISIBLE);
                oldPasswordInput.setVisibility(View.GONE);
                newPasswordInput.setVisibility(View.GONE);
                changeEmail.setVisibility(View.VISIBLE);
                changePassword.setVisibility(View.GONE);
            }
        });

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailInput.setVisibility(View.GONE);
                oldPasswordInput.setVisibility(View.VISIBLE);
                newPasswordInput.setVisibility(View.VISIBLE);
                changeEmail.setVisibility(View.GONE);
                changePassword.setVisibility(View.VISIBLE);
            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPassword = oldPasswordInput.getText().toString();
                String newPassword = newPasswordInput.getText().toString();
                int errors = 0;

                if (TextUtils.isEmpty(oldPassword)) {
                    oldPasswordInput.setError(getString(R.string.empty_password));
                    errors ++;
                }

                if (TextUtils.isEmpty(newPassword)) {
                    newPasswordInput.setError(getString(R.string.empty_password));
                    errors ++;
                } else if (newPassword.length() < 6) {
                    newPasswordInput.setError(getString(R.string.minimum_password));
                    errors ++;
                }

                if (errors > 0) {
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                RequestParams params = new RequestParams();
                params.put("username", auth.getUsername());
                params.put("oldPassword", oldPassword);
                params.put("newPassword", newPassword);

                RestClient.post("user/changepw", params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        // If the response is JSONObject instead of expected JSONArray
                        handleJSONObject(response);
                    }
                }, auth.getAPIKey());
            }
        });

        changeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailInput.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    emailInput.setError(getString(R.string.empty_email));
                    return;
                } else if (!Helper.isValidEmail(email)) {
                    emailInput.setError(getString(R.string.non_valid_email));
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                RequestParams params = new RequestParams();
                params.put("username", auth.getUsername());
                params.put("email", email);

                RestClient.post("user/changeemail", params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        // If the response is JSONObject instead of expected JSONArray
                        handleJSONObject(response);
                    }
                }, auth.getAPIKey());
            }
        });


        /*btnRemoveUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (user != null) {
                    user.delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getActivity(), "Your profile is deleted. Create a account now!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getActivity(), SignupActivity.class));
                                        getActivity().finish();
                                        progressBar.setVisibility(View.GONE);
                                    } else {
                                        Toast.makeText(getActivity(), "Failed to delete your account!", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                }
            }
        });*/

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        return myView;
    }

    public void signOut() {
        auth.logout();
        startActivity(new Intent(getActivity(), LoginActivity.class));
        getActivity().finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void handleJSONObject(JSONObject object) {
        try {
            int responseCode = object.getInt("responseCode");

            switch (responseCode) {
                case IConstants.CHANGE_SUCCESS:
                    signOut();
                    break;
                case IConstants.EMAIL_ALREADY_EXISTED:
                    Toast.makeText(getActivity(), getString(R.string.email_already_exists), Toast.LENGTH_LONG).show();
                    break;
                case IConstants.PASSWORD_NOT_CORRECT:
                    Toast.makeText(getActivity(), getString(R.string.password_not_correct), Toast.LENGTH_LONG).show();
                    break;
                case IConstants.AUTHORIZATION_FAIL:
                    Toast.makeText(getActivity(), getString(R.string.authorization_fail), Toast.LENGTH_LONG).show();
                    break;
                case IConstants.API_SQL_FAIL:
                    Toast.makeText(getActivity(), getString(R.string.api_sql_fail), Toast.LENGTH_LONG).show();
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressBar.setVisibility(View.GONE);
    }
}
