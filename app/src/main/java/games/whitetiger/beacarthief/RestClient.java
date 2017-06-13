package games.whitetiger.beacarthief;

import com.loopj.android.http.*;

public class RestClient {
    public static final String BASE_URL = "http://api.dynaball.at/";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler, String apiKey) {
        client.addHeader("Authorization", apiKey);
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler, String apiKey) {
        client.addHeader("Authorization", apiKey);
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
