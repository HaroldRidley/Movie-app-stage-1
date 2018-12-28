package com.popmovies.hridley.popularmovies.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Scanner;

public final class NetworkUtilities {

    private static final String API_KEY = "<your api key here>";


    private static final String API_KEY_QUERY_PARAM = "api_key";
    private static final String METHOD_POPULAR = "/movie/popular";
    private static final String METHOD_RATED = "/movie/top_rated";
    private static final String PAGE_QUERY_PARAM = "page";
    private static final String LANGUAGE_QUERY_PARAM = "language";
    private static final String API_URL = "https://api.themoviedb.org/3";


    public static boolean hasConnection(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo.isConnected();
    }

    public static URL buildUrl(String method, Map<String, String> params) {
        Uri.Builder builder = Uri.parse(API_URL + method).buildUpon();
        builder.appendQueryParameter(API_KEY_QUERY_PARAM, API_KEY);
        for (Map.Entry<String, String> param : params.entrySet()) {
            builder.appendQueryParameter(param.getKey(), param.getValue());
        }

        Uri uri = builder.build();
        URL url = null;

        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static String getResponse(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream inputStream = urlConnection.getInputStream();
            Scanner scanner = new Scanner(inputStream);
            scanner.useDelimiter("\\A");
            if (!scanner.hasNext()) {
                return null;
            }
            return scanner.next();

        } finally {
            urlConnection.disconnect();
        }
    }


    public static String getLanguage() {
        return LANGUAGE_QUERY_PARAM;
    }

    public static String getPage() {
        return PAGE_QUERY_PARAM;
    }

    public static String getMethodPopular() {
        return METHOD_POPULAR;
    }

    public static String getMethodRated() {
        return METHOD_RATED;
    }
}
