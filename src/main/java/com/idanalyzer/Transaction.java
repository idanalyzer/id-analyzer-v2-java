package com.idanalyzer;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Transaction history, decisions, vault assets and export (/transaction, /imagevault, /filevault, /export). */
public class Transaction {

    private final IDAnalyzerClient client;

    Transaction(IDAnalyzerClient client) {
        this.client = client;
    }

    /** Filters for listing/exporting transactions. */
    public static class ListOptions {
        public int order = -1;
        public int limit = 10;
        public int offset = 0;
        public int createdAtMin = 0;
        public int createdAtMax = 0;
        public String customData;
        public String decision;
        public String docupass;
        public String profileId;
    }

    /** Retrieve a single transaction (GET /transaction/{id}). */
    public JsonNode getTransaction(String transactionId) {
        if (transactionId == null || transactionId.isEmpty()) {
            throw new InvalidArgumentException("transactionId is required");
        }
        return client.request("GET", "transaction/" + transactionId, null, null);
    }

    /** Retrieve transaction history (GET /transaction). */
    public JsonNode listTransaction(ListOptions opts) {
        if (opts == null) opts = new ListOptions();
        Map<String, String> q = new LinkedHashMap<>();
        q.put("order", String.valueOf(opts.order));
        q.put("limit", String.valueOf(opts.limit));
        q.put("offset", String.valueOf(opts.offset));
        if (opts.createdAtMin > 0) q.put("createdAtMin", String.valueOf(opts.createdAtMin));
        if (opts.createdAtMax > 0) q.put("createdAtMax", String.valueOf(opts.createdAtMax));
        if (opts.customData != null && !opts.customData.isEmpty()) q.put("customData", opts.customData);
        if (opts.decision != null && !opts.decision.isEmpty()) q.put("decision", opts.decision);
        if (opts.docupass != null && !opts.docupass.isEmpty()) q.put("docupass", opts.docupass);
        if (opts.profileId != null && !opts.profileId.isEmpty()) q.put("profileId", opts.profileId);
        return client.request("GET", "transaction", null, q);
    }

    /** Update a transaction decision (PATCH /transaction/{id}). */
    public JsonNode updateTransaction(String transactionId, String decision) {
        if (transactionId == null || transactionId.isEmpty()) {
            throw new InvalidArgumentException("transactionId is required");
        }
        if (!"accept".equals(decision) && !"review".equals(decision) && !"reject".equals(decision)) {
            throw new InvalidArgumentException("decision should be one of accept, review, reject");
        }
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("decision", decision);
        return client.request("PATCH", "transaction/" + transactionId, payload, null);
    }

    /** Delete a transaction (DELETE /transaction/{id}). */
    public JsonNode deleteTransaction(String transactionId) {
        if (transactionId == null || transactionId.isEmpty()) {
            throw new InvalidArgumentException("transactionId is required");
        }
        return client.request("DELETE", "transaction/" + transactionId, null, null);
    }

    /** Download a vault image to dest (GET /imagevault/{token}). */
    public void saveImage(String imageToken, String dest) {
        if (imageToken == null || imageToken.isEmpty() || dest == null || dest.isEmpty()) {
            throw new InvalidArgumentException("imageToken and dest are required");
        }
        client.download("imagevault/" + imageToken, dest);
    }

    /** Download a vault file to dest (GET /filevault/{name}). */
    public void saveFile(String fileName, String dest) {
        if (fileName == null || fileName.isEmpty() || dest == null || dest.isEmpty()) {
            throw new InvalidArgumentException("fileName and dest are required");
        }
        client.download("filevault/" + fileName, dest);
    }

    /** Request a transaction archive and download it to dest (POST /export/transaction). */
    public void exportTransaction(String dest, String exportType, List<String> transactionIds,
                                  boolean ignoreUnrecognized, boolean ignoreDuplicate, ListOptions opts) {
        if (dest == null || dest.isEmpty()) {
            throw new InvalidArgumentException("dest is required");
        }
        if (exportType == null || exportType.isEmpty()) exportType = "csv";
        if (!"csv".equals(exportType) && !"json".equals(exportType)) {
            throw new InvalidArgumentException("exportType should be 'csv' or 'json'");
        }
        if (opts == null) opts = new ListOptions();
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("exportType", exportType);
        payload.put("ignoreUnrecognized", ignoreUnrecognized);
        payload.put("ignoreDuplicate", ignoreDuplicate);
        if (transactionIds != null && !transactionIds.isEmpty()) payload.put("transactionId", transactionIds);
        if (opts.createdAtMin > 0) payload.put("createdAtMin", opts.createdAtMin);
        if (opts.createdAtMax > 0) payload.put("createdAtMax", opts.createdAtMax);
        if (opts.customData != null && !opts.customData.isEmpty()) payload.put("customData", opts.customData);
        if (opts.decision != null && !opts.decision.isEmpty()) payload.put("decision", opts.decision);
        if (opts.docupass != null && !opts.docupass.isEmpty()) payload.put("docupass", opts.docupass);
        if (opts.profileId != null && !opts.profileId.isEmpty()) payload.put("profileId", opts.profileId);

        JsonNode resp = client.request("POST", "export/transaction", payload, null);
        JsonNode url = resp.get("Url");
        if (url != null && !url.asText("").isEmpty()) {
            client.download(url.asText(), dest);
        }
    }
}
