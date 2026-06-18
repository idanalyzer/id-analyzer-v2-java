package com.idanalyzer;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * KYB (Know Your Business) verification (POST /kyb).
 *
 * <p>Verify a business from its registration/incorporation document. A document
 * is required; an optional profile selects the KYC profile. The service extracts
 * the company details, checks official company registries, screens against
 * sanctions/PEP watchlists, and returns directors and owners to verify.
 */
public class KYB {

    private final IDAnalyzerClient client;

    KYB(IDAnalyzerClient client) {
        this.client = client;
    }

    /**
     * Verify a business from its registration/incorporation document.
     *
     * @param document registration/incorporation document — a file path, raw base64, URL, or data URL; required.
     * @return the API response as a {@link JsonNode}.
     * @throws InvalidArgumentException if document is null or empty.
     * @throws ApiException if the API returns an error or a transport error occurs.
     */
    public JsonNode verify(String document) {
        return verify(document, null);
    }

    /**
     * Verify a business from its registration/incorporation document. A document
     * is required; an optional profile selects the KYC profile.
     *
     * @param document registration/incorporation document — a file path, raw base64, URL, or data URL; required.
     * @param profile KYC profile id to apply; optional.
     * @return the API response as a {@link JsonNode}.
     * @throws InvalidArgumentException if document is null or empty.
     * @throws ApiException if the API returns an error or a transport error occurs.
     */
    public JsonNode verify(String document, String profile) {
        if (document == null || document.isEmpty()) {
            throw new InvalidArgumentException("A business document (image or PDF) is required.");
        }
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("document", IDAnalyzerClient.parseInput(document, true));
        if (profile != null && !profile.isEmpty()) payload.put("profile", profile);
        // KYB is heavier than a scan, allow up to 120 seconds for the response.
        return client.request("POST", "kyb", payload, null, 120);
    }
}
