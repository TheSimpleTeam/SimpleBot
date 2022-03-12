package net.thesimpleteam.simplebot.utils;

import net.thesimpleteam.simplebot.SimpleBot;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;

import java.io.IOException;

public class RequestHelper {

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public static String getResponseAsString(Response response) throws IOException {
        return response.body().string();
    }

    /**
     *
     * @param url Url of the api
     * @return the response of the API
     * @throws IOException if the request could not be executed due to cancellation, a connectivity
     *    problem or timeout. Because networks can fail during an exchange, it is possible that the
     *    remote server accepted the request before the failure.
     */
    public static Response sendRequest(@NotNull String url) throws IOException {
        if(url.isEmpty()) return new Response.Builder().code(404).build();
        String newURL = url.replace(',', '.');
        Request request = new Request.Builder()
                .url(newURL)
                .build();

        return SimpleBot.httpClient.newCall(request).execute();
    }

    public static Response sendPostRequest(@NotNull String url, @NotNull JSONObject json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json.toString());
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        return SimpleBot.httpClient.newCall(request).execute();
    }

}
