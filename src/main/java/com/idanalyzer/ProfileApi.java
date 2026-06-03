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

    /**
     * List KYC profiles (GET /profile).
     *
     * @param order sort order for the results.
     * @param limit maximum number of profiles to return.
     * @param offset number of profiles to skip (for pagination).
     * @return the API response as a {@link JsonNode}.
     * @throws ApiException if the API returns an error or a transport error occurs.
     */
    public JsonNode listProfile(int order, int limit, int offset) {
        Map<String, String> q = new LinkedHashMap<>();
        q.put("order", String.valueOf(order));
        q.put("limit", String.valueOf(limit));
        q.put("offset", String.valueOf(offset));
        return client.request("GET", "profile", null, q);
    }

    /**
     * Retrieve a KYC profile (GET /profile/{id}).
     *
     * @param profileId the profile id to retrieve.
     * @return the API response as a {@link JsonNode}.
     * @throws InvalidArgumentException if profileId is null or empty.
     * @throws ApiException if the API returns an error or a transport error occurs.
     */
    public JsonNode getProfile(String profileId) {
        if (profileId == null || profileId.isEmpty()) throw new InvalidArgumentException("profileId is required");
        return client.request("GET", "profile/" + profileId, null, null);
    }

    /**
     * Create a KYC profile (POST /profile).
     *
     * @param name the profile name.
     * @param profile the profile whose override settings define the new profile; may be null.
     * @return the API response as a {@link JsonNode}.
     * @throws InvalidArgumentException if name is null or empty.
     * @throws ApiException if the API returns an error or a transport error occurs.
     */
    public JsonNode createProfile(String name, Profile profile) {
        if (name == null || name.isEmpty()) throw new InvalidArgumentException("name is required");
        return client.request("POST", "profile", body(name, profile), null);
    }

    /**
     * Update a KYC profile (PUT /profile/{id}).
     *
     * @param profileId the profile id to update.
     * @param name the new profile name; sent when non-empty.
     * @param profile the profile whose override settings replace the stored settings; may be null.
     * @return the API response as a {@link JsonNode}.
     * @throws InvalidArgumentException if profileId is null or empty.
     * @throws ApiException if the API returns an error or a transport error occurs.
     */
    public JsonNode updateProfile(String profileId, String name, Profile profile) {
        if (profileId == null || profileId.isEmpty()) throw new InvalidArgumentException("profileId is required");
        return client.request("PUT", "profile/" + profileId, body(name, profile), null);
    }

    /**
     * Delete a KYC profile (DELETE /profile/{id}).
     *
     * @param profileId the profile id to delete.
     * @return the API response as a {@link JsonNode}.
     * @throws InvalidArgumentException if profileId is null or empty.
     * @throws ApiException if the API returns an error or a transport error occurs.
     */
    public JsonNode deleteProfile(String profileId) {
        if (profileId == null || profileId.isEmpty()) throw new InvalidArgumentException("profileId is required");
        return client.request("DELETE", "profile/" + profileId, null, null);
    }

    /**
     * Export a KYC profile (GET /export/profile/{id}).
     *
     * @param profileId the profile id to export.
     * @return the API response as a {@link JsonNode}.
     * @throws InvalidArgumentException if profileId is null or empty.
     * @throws ApiException if the API returns an error or a transport error occurs.
     */
    public JsonNode exportProfile(String profileId) {
        if (profileId == null || profileId.isEmpty()) throw new InvalidArgumentException("profileId is required");
        return client.request("GET", "export/profile/" + profileId, null, null);
    }
}
