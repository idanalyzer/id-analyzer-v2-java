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
        /** Creates an empty request whose fields are populated directly before use. */
        public CreateRequest() {}

        /** KYC profile ID (required). */
        public String profile;
        /** Verification mode: 0=Document+Face, 1=Document, 2=Face, 3=e-Signature. */
        public int mode = 0;
        /** Output format for generated contracts; defaults to "pdf". */
        public String contractFormat = "pdf";
        /** Contract template id to generate, if any. */
        public String contractGenerate;
        /** Contract template id to have the user sign, if any. */
        public String contractSign;
        /** Pre-fill data for the generated/signed contract, if any. */
        public String contractPrefill;
        /** Whether the link can be reused by multiple users. */
        public boolean reusable = false;
        /** Arbitrary string stored with the resulting transaction. */
        public String customData;
        /** UI language for the verification flow. */
        public String language;
        /** Reference document image to match against. */
        public String referenceDocument;
        /** Reference document back image to match against. */
        public String referenceDocumentBack;
        /** Reference face image to match against. */
        public String referenceFace;
        /** User phone number for the verification flow. */
        public String userPhone;
        /** Expected address to verify against the document. */
        public String verifyAddress;
        /** Expected age range to verify against the document. */
        public String verifyAge;
        /** Expected date of birth to verify (docupass uses the upper-case "verifyDOB" field). */
        public String verifyDOB;
        /** Expected document number to verify. */
        public String verifyDocumentNumber;
        /** Expected name to verify against the document. */
        public String verifyName;
        /** Expected postcode to verify against the document. */
        public String verifyPostcode;
    }

    private static void put(Map<String, Object> m, String k, String v) {
        if (v != null && !v.isEmpty()) m.put(k, v);
    }

    /**
     * Create a Docupass link (POST /docupass).
     *
     * @param req the link parameters; {@code req.profile} (KYC profile id) is required.
     * @return the API response as a {@link JsonNode}.
     * @throws InvalidArgumentException if req is null or its profile is null or empty.
     * @throws ApiException if the API returns an error or a transport error occurs.
     */
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

    /**
     * List Docupass records (GET /docupass).
     *
     * @param order sort order for the results.
     * @param limit maximum number of records to return.
     * @param offset number of records to skip (for pagination).
     * @return the API response as a {@link JsonNode}.
     * @throws ApiException if the API returns an error or a transport error occurs.
     */
    public JsonNode listDocupass(int order, int limit, int offset) {
        Map<String, String> q = new LinkedHashMap<>();
        q.put("order", String.valueOf(order));
        q.put("limit", String.valueOf(limit));
        q.put("offset", String.valueOf(offset));
        return client.request("GET", "docupass", null, q);
    }

    /**
     * Retrieve a single Docupass (GET /docupass/{reference}).
     *
     * @param reference the Docupass reference to retrieve.
     * @return the API response as a {@link JsonNode}.
     * @throws InvalidArgumentException if reference is null or empty.
     * @throws ApiException if the API returns an error or a transport error occurs.
     */
    public JsonNode getDocupass(String reference) {
        if (reference == null || reference.isEmpty()) {
            throw new InvalidArgumentException("reference is required");
        }
        return client.request("GET", "docupass/" + reference, null, null);
    }

    /**
     * Delete a Docupass (DELETE /docupass/{reference}).
     *
     * @param reference the Docupass reference to delete.
     * @return the API response as a {@link JsonNode}.
     * @throws InvalidArgumentException if reference is null or empty.
     * @throws ApiException if the API returns an error or a transport error occurs.
     */
    public JsonNode deleteDocupass(String reference) {
        if (reference == null || reference.isEmpty()) {
            throw new InvalidArgumentException("reference is required");
        }
        return client.request("DELETE", "docupass/" + reference, null, null);
    }
}
