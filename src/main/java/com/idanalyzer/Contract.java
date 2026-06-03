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

    /**
     * Generate a document from a template (POST /generate).
     *
     * @param templateId the contract template id to generate from.
     * @param format the output format; defaults to "PDF" when null/empty.
     * @param transactionId optional transaction id to pull fill data from; sent when non-empty.
     * @param fillData optional map of fields to populate the template; sent when non-empty.
     * @return the API response as a {@link JsonNode}.
     * @throws InvalidArgumentException if templateId is null or empty.
     * @throws ApiException if the API returns an error or a transport error occurs.
     */
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

    /**
     * List contract templates (GET /contract).
     *
     * @param order sort order for the results.
     * @param limit maximum number of templates to return.
     * @param offset number of templates to skip (for pagination).
     * @param filterTemplateId optional template id to filter by; sent when non-empty.
     * @return the API response as a {@link JsonNode}.
     * @throws ApiException if the API returns an error or a transport error occurs.
     */
    public JsonNode listTemplate(int order, int limit, int offset, String filterTemplateId) {
        Map<String, String> q = new LinkedHashMap<>();
        q.put("order", String.valueOf(order));
        q.put("limit", String.valueOf(limit));
        q.put("offset", String.valueOf(offset));
        if (filterTemplateId != null && !filterTemplateId.isEmpty()) q.put("templateId", filterTemplateId);
        return client.request("GET", "contract", null, q);
    }

    /**
     * Get a contract template (GET /contract/{id}).
     *
     * @param templateId the contract template id to retrieve.
     * @return the API response as a {@link JsonNode}.
     * @throws InvalidArgumentException if templateId is null or empty.
     * @throws ApiException if the API returns an error or a transport error occurs.
     */
    public JsonNode getTemplate(String templateId) {
        if (templateId == null || templateId.isEmpty()) {
            throw new InvalidArgumentException("templateId is required");
        }
        return client.request("GET", "contract/" + templateId, null, null);
    }

    /**
     * Create a contract template (POST /contract).
     *
     * @param name the template name.
     * @param content the template content (HTML/markup).
     * @param orientation page orientation; defaults to "0" when null.
     * @param timezone the template timezone; defaults to "UTC" when null.
     * @param font the template font; defaults to "Open Sans" when null.
     * @return the API response as a {@link JsonNode}.
     * @throws InvalidArgumentException if name or content is null or empty.
     * @throws ApiException if the API returns an error or a transport error occurs.
     */
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

    /**
     * Update a contract template (POST /contract/{id}).
     *
     * @param templateId the contract template id to update.
     * @param name the new template name.
     * @param content the new template content (HTML/markup).
     * @param orientation page orientation; defaults to "0" when null.
     * @param timezone the template timezone; defaults to "UTC" when null.
     * @param font the template font; defaults to "Open Sans" when null.
     * @return the API response as a {@link JsonNode}.
     * @throws InvalidArgumentException if templateId is null or empty.
     * @throws ApiException if the API returns an error or a transport error occurs.
     */
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

    /**
     * Delete a contract template (DELETE /contract/{id}).
     *
     * @param templateId the contract template id to delete.
     * @return the API response as a {@link JsonNode}.
     * @throws InvalidArgumentException if templateId is null or empty.
     * @throws ApiException if the API returns an error or a transport error occurs.
     */
    public JsonNode deleteTemplate(String templateId) {
        if (templateId == null || templateId.isEmpty()) throw new InvalidArgumentException("templateId is required");
        return client.request("DELETE", "contract/" + templateId, null, null);
    }
}
