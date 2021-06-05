package fr.noalegeek.pepite_dor_bot.utils.helpers;

import fr.noalegeek.pepite_dor_bot.Main;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class RequestHelper {

    public static String getResponseAsString(Response response) throws IOException {
        return response.body().string();
    }

    public static Response sendRequest(@NotNull String url) throws IOException {
        if(url.isEmpty()) return new Response.Builder().code(404).build();
        String newURL = url.replace(',', '.');
        Request request = new Request.Builder()
                .url(newURL)
                .build();

        return Main.httpClient.newCall(request).execute();
    }

}
