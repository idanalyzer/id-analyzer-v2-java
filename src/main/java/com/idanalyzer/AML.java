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
     * @param entity 0=Person, 1=Corporation/Legal Entity
     * @param database optional list of databases (e.g. ["us_ofac","eu_fsf"]); null = all
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

    /** Screen against the AML v3 database (POST /amlv3). Provide text or id. */
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
