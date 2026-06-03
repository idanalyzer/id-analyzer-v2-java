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
        /** Creates an options object with default values that can be overridden before use. */
        public ListOptions() {}

        /** Sort order for the results. */
        public int order = -1;
        /** Maximum number of transactions to return. */
        public int limit = 10;
        /** Number of transactions to skip (for pagination). */
        public int offset = 0;
        /** Lower bound (inclusive) on creation time; only applied when greater than 0. */
        public int createdAtMin = 0;
        /** Upper bound (inclusive) on creation time; only applied when greater than 0. */
        public int createdAtMax = 0;
        /** Filter by the custom data stored with the transaction; applied when non-empty. */
        public String customData;
        /** Filter by decision ("accept"/"review"/"reject"); applied when non-empty. */
        public String decision;
        /** Filter by associated Docupass reference; applied when non-empty. */
        public String docupass;
        /** Filter by KYC profile id; applied when non-empty. */
        public String profileId;
    }

    /**
     * Retrieve a single transaction (GET /transaction/{id}).
     *
     * @param transactionId the transaction id to retrieve.
     * @return the API response as a {@link JsonNode}.
     * @throws InvalidArgumentException if transactionId is null or empty.
     * @throws ApiException if the API returns an error or a transport error occurs.
     */
    public JsonNode getTransaction(String transactionId) {
        if (transactionId == null || transactionId.isEmpty()) {
            throw new InvalidArgumentException("transactionId is required");
        }
        return client.request("GET", "transaction/" + transactionId, null, null);
    }

    /**
     * Retrieve transaction history (GET /transaction).
     *
     * @param opts the filter/pagination options; defaults are used when null.
     * @return the API response as a {@link JsonNode}.
     * @throws ApiException if the API returns an error or a transport error occurs.
     */
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

    /**
     * Update a transaction decision (PATCH /transaction/{id}).
     *
     * @param transactionId the transaction id to update.
     * @param decision the new decision; must be one of "accept", "review" or "reject".
     * @return the API response as a {@link JsonNode}.
     * @throws InvalidArgumentException if transactionId is null/empty or decision is not a valid value.
     * @throws ApiException if the API returns an error or a transport error occurs.
     */
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

    /**
     * Delete a transaction (DELETE /transaction/{id}).
     *
     * @param transactionId the transaction id to delete.
     * @return the API response as a {@link JsonNode}.
     * @throws InvalidArgumentException if transactionId is null or empty.
     * @throws ApiException if the API returns an error or a transport error occurs.
     */
    public JsonNode deleteTransaction(String transactionId) {
        if (transactionId == null || transactionId.isEmpty()) {
            throw new InvalidArgumentException("transactionId is required");
        }
        return client.request("DELETE", "transaction/" + transactionId, null, null);
    }

    /**
     * Download a vault image to dest (GET /imagevault/{token}).
     *
     * @param imageToken the image vault token identifying the image.
     * @param dest the local file path to write the downloaded image to.
     * @throws InvalidArgumentException if imageToken or dest is null or empty.
     * @throws ApiException if the download fails (transport error).
     */
    public void saveImage(String imageToken, String dest) {
        if (imageToken == null || imageToken.isEmpty() || dest == null || dest.isEmpty()) {
            throw new InvalidArgumentException("imageToken and dest are required");
        }
        client.download("imagevault/" + imageToken, dest);
    }

    /**
     * Download a vault file to dest (GET /filevault/{name}).
     *
     * @param fileName the file vault name identifying the file.
     * @param dest the local file path to write the downloaded file to.
     * @throws InvalidArgumentException if fileName or dest is null or empty.
     * @throws ApiException if the download fails (transport error).
     */
    public void saveFile(String fileName, String dest) {
        if (fileName == null || fileName.isEmpty() || dest == null || dest.isEmpty()) {
            throw new InvalidArgumentException("fileName and dest are required");
        }
        client.download("filevault/" + fileName, dest);
    }

    /**
     * Request a transaction archive and download it to dest (POST /export/transaction).
     *
     * @param dest the local file path to write the downloaded archive to.
     * @param exportType the export format; defaults to "csv" when null/empty, must be "csv" or "json".
     * @param transactionIds optional explicit list of transaction ids to export; sent when non-empty.
     * @param ignoreUnrecognized whether to exclude unrecognized documents from the export.
     * @param ignoreDuplicate whether to exclude duplicate transactions from the export.
     * @param opts optional filter/pagination options; defaults are used when null.
     * @throws InvalidArgumentException if dest is null/empty or exportType is not "csv"/"json".
     * @throws ApiException if the API returns an error or a transport/download error occurs.
     */
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
