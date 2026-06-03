package com.idanalyzer;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.LinkedHashMap;
import java.util.Map;

/** Webhook delivery log management (/webhook). */
public class Webhook {

    private final IDAnalyzerClient client;

    Webhook(IDAnalyzerClient client) {
        this.client = client;
    }

    /**
     * List webhook delivery logs (GET /webhook).
     *
     * @param success 0 or 1 to filter by success status; any other value is ignored
     */
    public JsonNode listWebhook(int order, int limit, int offset, String event, int success,
                                String createdAtMin, String createdAtMax) {
        Map<String, String> q = new LinkedHashMap<>();
        q.put("order", String.valueOf(order));
        q.put("limit", String.valueOf(limit));
        q.put("offset", String.valueOf(offset));
        if (event != null && !event.isEmpty()) q.put("event", event);
        if (success == 0 || success == 1) q.put("success", String.valueOf(success));
        if (createdAtMin != null && !createdAtMin.isEmpty()) q.put("createdAtMin", createdAtMin);
        if (createdAtMax != null && !createdAtMax.isEmpty()) q.put("createdAtMax", createdAtMax);
        return client.request("GET", "webhook", null, q);
    }

    /** Resend a webhook delivery (POST /webhook/{id}). */
    public JsonNode resendWebhook(String webhookId) {
        if (webhookId == null || webhookId.isEmpty()) throw new InvalidArgumentException("webhookId is required");
        return client.request("POST", "webhook/" + webhookId, new LinkedHashMap<>(), null);
    }

    /** Delete a webhook delivery log (DELETE /webhook/{id}). */
    public JsonNode deleteWebhook(String webhookId) {
        if (webhookId == null || webhookId.isEmpty()) throw new InvalidArgumentException("webhookId is required");
        return client.request("DELETE", "webhook/" + webhookId, null, null);
    }
}
