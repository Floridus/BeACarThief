package games.whitetiger.beacarthief;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ResetPasswordActivity extends AppCompatActivity implements ICallback {

    private EditText inputEmail;
    private Button btnReset, btnBack;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        inputEmail = (EditText) findViewById(R.id.email);
        btnReset = (Button) findViewById(R.id.btn_reset_password);
        btnBack = (Button) findViewById(R.id.btn_back);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    inputEmail.setError(getString(R.string.empty_email));
                    return;
                } else if (!Helper.isValidEmail(email)) {
                    inputEmail.setError(getString(R.string.non_valid_email));
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                RequestParams params = new RequestParams();
                params.put("email", email);

                RestClient.post("user/forgotpw", params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        // If the response is JSONObject instead of expected JSONArray
                        handleJSONObject(response);
                    }
                }, getString(R.string.api_key));
            }
        });
    }

    @Override
    public void handleJSONObject(JSONObject object) {
        try {
            int responseCode = object.getInt("responseCode");

            switch (responseCode) {
                case IConstants.PASSWORD_RESET_SUCCESS:
                    Toast.makeText(ResetPasswordActivity.this, getString(R.string.password_reset), Toast.LENGTH_LONG).show();
                    break;
                case IConstants.USER_NOT_EXISTS:
                    Toast.makeText(ResetPasswordActivity.this, getString(R.string.user_not_exists), Toast.LENGTH_LONG).show();
                    break;
                case IConstants.AUTHORIZATION_FAIL:
                    Toast.makeText(ResetPasswordActivity.this, getString(R.string.authorization_fail), Toast.LENGTH_LONG).show();
                    break;
                case IConstants.API_SQL_FAIL:
                    Toast.makeText(ResetPasswordActivity.this, getString(R.string.api_sql_fail), Toast.LENGTH_LONG).show();
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        progressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}