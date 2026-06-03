package com.idanalyzer;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.LinkedHashMap;
import java.util.Map;

/** Biometric face &amp; liveness verification (POST /face, /liveness). */
public class Biometric {

    private final IDAnalyzerClient client;
    private final Map<String, Object> config = new LinkedHashMap<>();

    Biometric(IDAnalyzerClient client) {
        this.client = client;
    }

    /**
     * Attach an arbitrary string to be stored with the transaction (e.g. an internal reference).
     *
     * @param customData the custom data string to store with the verification.
     * @return this instance, for chaining.
     */
    public Biometric setCustomData(String customData) {
        config.put("customData", customData);
        return this;
    }

    /**
     * Attach a KYC profile (required before {@link #verifyFace} / {@link #verifyLiveness}).
     *
     * @param profile the KYC profile to apply; its overrides are sent when non-empty.
     * @return this instance, for chaining.
     * @throws InvalidArgumentException if profile is null.
     */
    public Biometric setProfile(Profile profile) {
        if (profile == null) {
            throw new InvalidArgumentException("profile is required");
        }
        config.put("profile", profile.getProfileId());
        if (!profile.getOverride().isEmpty()) {
            config.put("profileOverride", profile.getOverride());
        } else {
            config.remove("profileOverride");
        }
        return this;
    }

    /**
     * Perform 1:1 face verification against a reference image.
     *
     * @param referenceFaceImage the reference face image (file path, base64, URL or cache reference).
     * @param facePhoto the face photo to verify; used when non-empty, otherwise faceVideo is used.
     * @param faceVideo the face video to verify; used when facePhoto is empty.
     * @return the API response as a {@link JsonNode}.
     * @throws InvalidArgumentException if no profile is configured, the reference image is missing,
     *         or neither facePhoto nor faceVideo is supplied.
     * @throws ApiException if the API returns an error or a transport error occurs.
     */
    public JsonNode verifyFace(String referenceFaceImage, String facePhoto, String faceVideo) {
        if (!config.containsKey("profile")) {
            throw new InvalidArgumentException("KYC Profile not configured, call setProfile first.");
        }
        if (referenceFaceImage == null || referenceFaceImage.isEmpty()) {
            throw new InvalidArgumentException("Reference face image required.");
        }
        if ((facePhoto == null || facePhoto.isEmpty()) && (faceVideo == null || faceVideo.isEmpty())) {
            throw new InvalidArgumentException("Verification face image required.");
        }
        Map<String, Object> payload = new LinkedHashMap<>(config);
        payload.put("reference", IDAnalyzerClient.parseInput(referenceFaceImage, true));
        if (facePhoto != null && !facePhoto.isEmpty()) {
            payload.put("face", IDAnalyzerClient.parseInput(facePhoto, true));
        } else {
            payload.put("faceVideo", IDAnalyzerClient.parseInput(faceVideo, false));
        }
        return client.request("POST", "face", payload, null);
    }

    /**
     * Perform a standalone liveness check on a selfie photo or video.
     *
     * @param facePhoto the selfie photo to check; used when non-empty, otherwise faceVideo is used.
     * @param faceVideo the selfie video to check; used when facePhoto is empty.
     * @return the API response as a {@link JsonNode}.
     * @throws InvalidArgumentException if no profile is configured, or neither facePhoto nor faceVideo is supplied.
     * @throws ApiException if the API returns an error or a transport error occurs.
     */
    public JsonNode verifyLiveness(String facePhoto, String faceVideo) {
        if (!config.containsKey("profile")) {
            throw new InvalidArgumentException("KYC Profile not configured, call setProfile first.");
        }
        if ((facePhoto == null || facePhoto.isEmpty()) && (faceVideo == null || faceVideo.isEmpty())) {
            throw new InvalidArgumentException("Verification face image required.");
        }
        Map<String, Object> payload = new LinkedHashMap<>(config);
        if (facePhoto != null && !facePhoto.isEmpty()) {
            payload.put("face", IDAnalyzerClient.parseInput(facePhoto, true));
        } else {
            payload.put("faceVideo", IDAnalyzerClient.parseInput(faceVideo, false));
        }
        return client.request("POST", "liveness", payload, null);
    }
}
