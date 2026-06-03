package com.idanalyzer;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.LinkedHashMap;
import java.util.Map;

/** DocuPass remote verification &amp; e-signature link management (/docupass). */
public class Docupass {

    private final IDAnalyzerClient client;

    Docupass(IDAnalyzerClient client) {
        this.client = client;
    }

    /** Parameters for creating a Docupass link. */
    public static class CreateRequest {
        public String profile;           // KYC profile ID (required)
        public int mode = 0;             // 0=Document+Face, 1=Document, 2=Face, 3=e-Signature
        public String contractFormat = "pdf";
        public String contractGenerate;
        public String contractSign;
        public String contractPrefill;
        public boolean reusable = false;
        public String customData;
        public String language;
        public String referenceDocument;
        public String referenceDocumentBack;
        public String referenceFace;
        public String userPhone;
        public String verifyAddress;
        public String verifyAge;
        public String verifyDOB;         // docupass uses the upper-case "verifyDOB" field
        public String verifyDocumentNumber;
        public String verifyName;
        public String verifyPostcode;
    }

    private static void put(Map<String, Object> m, String k, String v) {
        if (v != null && !v.isEmpty()) m.put(k, v);
    }

    /** Create a Docupass link (POST /docupass). */
    public JsonNode createDocupass(CreateRequest req) {
        if (req == null || req.profile == null || req.profile.isEmpty()) {
            throw new InvalidArgumentException("profile is required");
        }
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("profile", req.profile);
        payload.put("mode", req.mode);
        payload.put("contractFormat", req.contractFormat == null || req.contractFormat.isEmpty() ? "pdf" : req.contractFormat);
        payload.put("contractGenerate", req.contractGenerate == null ? "" : req.contractGenerate);
        payload.put("reusable", req.reusable);
        put(payload, "contractSign", req.contractSign);
        put(payload, "contractPrefill", req.contractPrefill);
        put(payload, "customData", req.customData);
        put(payload, "language", req.language);
        put(payload, "referenceDocument", req.referenceDocument);
        put(payload, "referenceDocumentBack", req.referenceDocumentBack);
        put(payload, "referenceFace", req.referenceFace);
        put(payload, "userPhone", req.userPhone);
        put(payload, "verifyAddress", req.verifyAddress);
        put(payload, "verifyAge", req.verifyAge);
        put(payload, "verifyDOB", req.verifyDOB);
        put(payload, "verifyDocumentNumber", req.verifyDocumentNumber);
        put(payload, "verifyName", req.verifyName);
        put(payload, "verifyPostcode", req.verifyPostcode);
        return client.request("POST", "docupass", payload, null);
    }

    /** List Docupass records (GET /docupass). */
    public JsonNode listDocupass(int order, int limit, int offset) {
        Map<String, String> q = new LinkedHashMap<>();
        q.put("order", String.valueOf(order));
        q.put("limit", String.valueOf(limit));
        q.put("offset", String.valueOf(offset));
        return client.request("GET", "docupass", null, q);
    }

    /** Retrieve a single Docupass (GET /docupass/{reference}). */
    public JsonNode getDocupass(String reference) {
        if (reference == null || reference.isEmpty()) {
            throw new InvalidArgumentException("reference is required");
        }
        return client.request("GET", "docupass/" + reference, null, null);
    }

    /** Delete a Docupass (DELETE /docupass/{reference}). */
    public JsonNode deleteDocupass(String reference) {
        if (reference == null || reference.isEmpty()) {
            throw new InvalidArgumentException("reference is required");
        }
        return client.request("DELETE", "docupass/" + reference, null, null);
    }
}
