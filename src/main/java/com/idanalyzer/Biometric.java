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

    public Biometric setCustomData(String customData) {
        config.put("customData", customData);
        return this;
    }

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

    /** Perform 1:1 face verification against a reference image. */
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

    /** Perform a standalone liveness check on a selfie photo or video. */
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
