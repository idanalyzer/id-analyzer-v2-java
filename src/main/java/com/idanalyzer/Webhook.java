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
     * @param order sort order for the results.
     * @param limit maximum number of logs to return.
     * @param offset number of logs to skip (for pagination).
     * @param event optional event name to filter by; sent when non-empty.
     * @param success 0 or 1 to filter by success status; any other value is ignored
     * @param createdAtMin optional lower bound (inclusive) on creation time; sent when non-empty.
     * @param createdAtMax optional upper bound (inclusive) on creation time; sent when non-empty.
     * @return the API response as a {@link JsonNode}.
     * @throws ApiException if the API returns an error or a transport error occurs.
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

    /**
     * Resend a webhook delivery (POST /webhook/{id}).
     *
     * @param webhookId the webhook delivery log id to resend.
     * @return the API response as a {@link JsonNode}.
     * @throws InvalidArgumentException if webhookId is null or empty.
     * @throws ApiException if the API returns an error or a transport error occurs.
     */
    public JsonNode resendWebhook(String webhookId) {
        if (webhookId == null || webhookId.isEmpty()) throw new InvalidArgumentException("webhookId is required");
        return client.request("POST", "webhook/" + webhookId, new LinkedHashMap<>(), null);
    }

    /**
     * Delete a webhook delivery log (DELETE /webhook/{id}).
     *
     * @param webhookId the webhook delivery log id to delete.
     * @return the API response as a {@link JsonNode}.
     * @throws InvalidArgumentException if webhookId is null or empty.
     * @throws ApiException if the API returns an error or a transport error occurs.
     */
    public JsonNode deleteWebhook(String webhookId) {
        if (webhookId == null || webhookId.isEmpty()) throw new InvalidArgumentException("webhookId is required");
        return client.request("DELETE", "webhook/" + webhookId, null, null);
    }
}
