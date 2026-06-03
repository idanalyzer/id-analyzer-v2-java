package com.idanalyzer;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.LinkedHashMap;
import java.util.Map;

/** Server-side KYC profile management (/profile, /export/profile). */
public class ProfileApi {

    private final IDAnalyzerClient client;

    ProfileApi(IDAnalyzerClient client) {
        this.client = client;
    }

    private static Map<String, Object> body(String name, Profile profile) {
        Map<String, Object> b = new LinkedHashMap<>();
        if (name != null && !name.isEmpty()) b.put("name", name);
        if (profile != null) b.putAll(profile.getOverride());
        return b;
    }

    /** List KYC profiles (GET /profile). */
    public JsonNode listProfile(int order, int limit, int offset) {
        Map<String, String> q = new LinkedHashMap<>();
        q.put("order", String.valueOf(order));
        q.put("limit", String.valueOf(limit));
        q.put("offset", String.valueOf(offset));
        return client.request("GET", "profile", null, q);
    }

    /** Retrieve a KYC profile (GET /profile/{id}). */
    public JsonNode getProfile(String profileId) {
        if (profileId == null || profileId.isEmpty()) throw new InvalidArgumentException("profileId is required");
        return client.request("GET", "profile/" + profileId, null, null);
    }

    /** Create a KYC profile (POST /profile). */
    public JsonNode createProfile(String name, Profile profile) {
        if (name == null || name.isEmpty()) throw new InvalidArgumentException("name is required");
        return client.request("POST", "profile", body(name, profile), null);
    }

    /** Update a KYC profile (PUT /profile/{id}). */
    public JsonNode updateProfile(String profileId, String name, Profile profile) {
        if (profileId == null || profileId.isEmpty()) throw new InvalidArgumentException("profileId is required");
        return client.request("PUT", "profile/" + profileId, body(name, profile), null);
    }

    /** Delete a KYC profile (DELETE /profile/{id}). */
    public JsonNode deleteProfile(String profileId) {
        if (profileId == null || profileId.isEmpty()) throw new InvalidArgumentException("profileId is required");
        return client.request("DELETE", "profile/" + profileId, null, null);
    }

    /** Export a KYC profile (GET /export/profile/{id}). */
    public JsonNode exportProfile(String profileId) {
        if (profileId == null || profileId.isEmpty()) throw new InvalidArgumentException("profileId is required");
        return client.request("GET", "export/profile/" + profileId, null, null);
    }
}
