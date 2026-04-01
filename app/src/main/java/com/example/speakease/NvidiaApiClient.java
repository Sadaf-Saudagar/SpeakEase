package com.example.speakease;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Call;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import java.util.List;

public class NvidiaApiClient {

    private static final String BASE_URL = "https://integrate.api.nvidia.com/v1/";
    private static Retrofit retrofit = null;

    public static NvidiaService getService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(NvidiaService.class);
    }

    public interface NvidiaService {
        @POST("chat/completions")
        Call<NvidiaResponse> getChatCompletion(
                @Header("Authorization") String apiKey,
                @Body NvidiaRequest request
        );
    }

    // --- Request Data Models ---
    public static class NvidiaRequest {
        public String model;
        public List<Message> messages;
        public double temperature;
        public int max_tokens;

        public NvidiaRequest(String model, List<Message> messages) {
            this.model = model;
            this.messages = messages;
            this.temperature = 0.5;
            this.max_tokens = 500;
        }
    }

    public static class Message {
        public String role;
        public String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }

    // --- Response Data Models ---
    public static class NvidiaResponse {
        public List<Choice> choices;

        public static class Choice {
            public Message message;
        }
    }
}