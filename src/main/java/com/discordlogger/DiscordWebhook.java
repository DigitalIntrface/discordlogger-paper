package com.discordlogger;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class DiscordWebhook {

    private final String webhookUrl;
    private final String username;
    private final String avatarUrl;
    private final HttpClient client;

    public DiscordWebhook(String webhookUrl, String username, String avatarUrl) {
        this.webhookUrl = webhookUrl;
        this.username = username;
        this.avatarUrl = avatarUrl;
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public void sendMessage(String content) {
        if (content == null || content.isEmpty() || webhookUrl == null || webhookUrl.isEmpty()) return;

        if (content.length() > 2000) {
            content = content.substring(0, 1997) + "...";
        }

        // Build JSON using Gson
        JsonObject json = new JsonObject();
        json.addProperty("content", content);
        json.addProperty("username", username);
        json.addProperty("avatar_url", avatarUrl);

        JsonObject allowedMentions = new JsonObject();
        allowedMentions.add("parse", new JsonArray());  // disables mentions
        json.add("allowed_mentions", allowedMentions);

        String jsonBody = json.toString();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(webhookUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() < 200 || response.statusCode() >= 300) {
                        System.err.println("Discord response error: " + response.body());
                    }
                })
                .exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                });
    }
}
