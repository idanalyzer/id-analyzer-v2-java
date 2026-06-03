package com.idanalyzer;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.LinkedHashMap;
import java.util.Map;

/** Contract generation &amp; template management (/generate, /contract). */
public class Contract {

    private final IDAnalyzerClient client;

    Contract(IDAnalyzerClient client) {
        this.client = client;
    }

    /** Generate a document from a template (POST /generate). */
    public JsonNode generate(String templateId, String format, String transactionId, Map<String, Object> fillData) {
        if (templateId == null || templateId.isEmpty()) {
            throw new InvalidArgumentException("templateId is required");
        }
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("templateId", templateId);
        payload.put("format", format == null || format.isEmpty() ? "PDF" : format);
        if (transactionId != null && !transactionId.isEmpty()) payload.put("transactionId", transactionId);
        if (fillData != null && !fillData.isEmpty()) payload.put("fillData", fillData);
        return client.request("POST", "generate", payload, null);
    }

    /** List contract templates (GET /contract). */
    public JsonNode listTemplate(int order, int limit, int offset, String filterTemplateId) {
        Map<String, String> q = new LinkedHashMap<>();
        q.put("order", String.valueOf(order));
        q.put("limit", String.valueOf(limit));
        q.put("offset", String.valueOf(offset));
        if (filterTemplateId != null && !filterTemplateId.isEmpty()) q.put("templateId", filterTemplateId);
        return client.request("GET", "contract", null, q);
    }

    /** Get a contract template (GET /contract/{id}). */
    public JsonNode getTemplate(String templateId) {
        if (templateId == null || templateId.isEmpty()) {
            throw new InvalidArgumentException("templateId is required");
        }
        return client.request("GET", "contract/" + templateId, null, null);
    }

    /** Create a contract template (POST /contract). */
    public JsonNode createTemplate(String name, String content, String orientation, String timezone, String font) {
        if (name == null || name.isEmpty()) throw new InvalidArgumentException("name is required");
        if (content == null || content.isEmpty()) throw new InvalidArgumentException("content is required");
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("name", name);
        payload.put("content", content);
        payload.put("orientation", orientation == null ? "0" : orientation);
        payload.put("timezone", timezone == null ? "UTC" : timezone);
        payload.put("font", font == null ? "Open Sans" : font);
        return client.request("POST", "contract", payload, null);
    }

    /** Update a contract template (POST /contract/{id}). */
    public JsonNode updateTemplate(String templateId, String name, String content, String orientation, String timezone, String font) {
        if (templateId == null || templateId.isEmpty()) throw new InvalidArgumentException("templateId is required");
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("name", name);
        payload.put("content", content);
        payload.put("orientation", orientation == null ? "0" : orientation);
        payload.put("timezone", timezone == null ? "UTC" : timezone);
        payload.put("font", font == null ? "Open Sans" : font);
        return client.request("POST", "contract/" + templateId, payload, null);
    }

    /** Delete a contract template (DELETE /contract/{id}). */
    public JsonNode deleteTemplate(String templateId) {
        if (templateId == null || templateId.isEmpty()) throw new InvalidArgumentException("templateId is required");
        return client.request("DELETE", "contract/" + templateId, null, null);
    }
}
