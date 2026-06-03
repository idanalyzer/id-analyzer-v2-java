package com.idanalyzer;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** AML / PEP / sanctions screening (POST /aml, /amlv3). */
public class AML {

    private final IDAnalyzerClient client;

    AML(IDAnalyzerClient client) {
        this.client = client;
    }

    /**
     * Screen against the AML database (v1 endpoint). Provide name and/or idNumber.
     *
     * @param name full name to screen; optional if idNumber is provided.
     * @param idNumber identity/document number to screen; optional if name is provided.
     * @param entity 0=Person, 1=Corporation/Legal Entity
     * @param country optional ISO country code to narrow the search; null/empty = unrestricted.
     * @param database optional list of databases (e.g. ["us_ofac","eu_fsf"]); null = all
     * @param birthYear optional year of birth to refine matches; null/empty = unrestricted.
     * @return the API response as a {@link JsonNode}.
     * @throws InvalidArgumentException if neither name nor idNumber is supplied.
     * @throws ApiException if the API returns an error or a transport error occurs.
     */
    public JsonNode search(String name, String idNumber, int entity, String country,
                           List<String> database, String birthYear) {
        if ((name == null || name.isEmpty()) && (idNumber == null || idNumber.isEmpty())) {
            throw new InvalidArgumentException("Either name or idNumber is required.");
        }
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("entity", entity);
        if (name != null && !name.isEmpty()) payload.put("name", name);
        if (idNumber != null && !idNumber.isEmpty()) payload.put("idNumber", idNumber);
        if (country != null && !country.isEmpty()) payload.put("country", country);
        if (birthYear != null && !birthYear.isEmpty()) payload.put("birthYear", birthYear);
        if (database != null && !database.isEmpty()) payload.put("database", database);
        return client.request("POST", "aml", payload, null);
    }

    /**
     * Screen against the AML v3 database (POST /amlv3). Provide text or id.
     *
     * @param text free-text query (e.g. a name) to screen; optional if id is provided.
     * @param id record id to look up; optional if text is provided.
     * @param limit maximum number of results to return; only sent when greater than 0.
     * @param page page number for paginated results; only sent when greater than 0.
     * @return the API response as a {@link JsonNode}.
     * @throws InvalidArgumentException if neither text nor id is supplied.
     * @throws ApiException if the API returns an error or a transport error occurs.
     */
    public JsonNode searchV3(String text, String id, int limit, int page) {
        if ((text == null || text.isEmpty()) && (id == null || id.isEmpty())) {
            throw new InvalidArgumentException("Either text or id is required.");
        }
        Map<String, Object> payload = new LinkedHashMap<>();
        if (text != null && !text.isEmpty()) payload.put("text", text);
        if (id != null && !id.isEmpty()) payload.put("id", id);
        if (limit > 0) payload.put("limit", limit);
        if (page > 0) payload.put("page", page);
        return client.request("POST", "amlv3", payload, null);
    }
}
