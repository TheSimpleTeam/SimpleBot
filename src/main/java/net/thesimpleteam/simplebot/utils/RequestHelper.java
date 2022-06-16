package net.thesimpleteam.simplebot.utils;

import com.google.gson.JsonObject;
import net.thesimpleteam.simplebot.SimpleBot;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class RequestHelper {

    private RequestHelper() {}

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    /**
     * @param response a api's response
     * @return the response's body as a string
     * @throws IOException if the request could not be executed due to cancellation, a connectivity problem or timeout because networks can fail during an exchange, it's possible that the remote server accepted the request before the failure
     */
    public static String getResponseAsString(Response response) throws IOException {
        return response.body().string();
    }

    /**
     *
     * @param url the api's url
     * @return the api's response
     * @throws IOException if the request could not be executed due to cancellation, a connectivity problem or timeout because networks can fail during an exchange, it's possible that the remote server accepted the request before the failure
     */
    public static Response sendRequest(@NotNull String url) throws IOException {
        return url.isEmpty() ? new Response.Builder().code(404).build() : SimpleBot.httpClient.newCall(new Request.Builder()
                .url(url.replace(',', '.'))
                .build()).execute();
    }

    /**
     * @param url a url to send a request to
     * @param json a json object to send
     * @return the url's response
     * @throws IOException if the request could not be executed due to cancellation, a connectivity problem or timeout because networks can fail during an exchange, it's possible that the remote server accepted the request before the failure
     */
    public static Response sendPostRequest(@NotNull String url, @NotNull JsonObject json) throws IOException {
        return SimpleBot.httpClient.newCall(new Request.Builder()
                .url(url)
                .post(RequestBody.create(JSON, json.toString()))
                .build()).execute();
    }
}