package com.idanalyzer;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * KYB (Know Your Business) verification (POST /kyb).
 *
 * <p>Verifies a business from its registration/incorporation document: extracts
 * the company details, checks official company registries, screens against
 * sanctions/PEP watchlists, and returns directors and owners to verify.
 */
public class KYB {

    private final IDAnalyzerClient client;

    KYB(IDAnalyzerClient client) {
        this.client = client;
    }

    /**
     * Verify a business. Provide a registration/incorporation document and/or known
     * business identifiers; the service extracts the company details, checks official
     * company registries, screens against sanctions/PEP watchlists, and returns
     * directors and owners to verify. At least one of document, legalName or
     * registrationNumber must be supplied.
     *
     * @param document registration/incorporation document — a file path, raw base64, URL, or data URL; optional.
     * @param legalName registered legal name of the business; optional.
     * @param legalNameLocal registered legal name in the local language/script; optional.
     * @param registrationNumber company registration / incorporation number; optional.
     * @param taxNumber business tax number; optional.
     * @param lei Legal Entity Identifier (LEI); optional.
     * @param country two-letter ISO country code where the business is registered; optional.
     * @param state state/province where the business is registered; optional.
     * @param entityType business entity type; optional.
     * @return the API response as a {@link JsonNode}.
     * @throws InvalidArgumentException if none of document, legalName or registrationNumber is supplied.
     * @throws ApiException if the API returns an error or a transport error occurs.
     */
    public JsonNode verify(String document, String legalName, String legalNameLocal,
                           String registrationNumber, String taxNumber, String lei,
                           String country, String state, String entityType) {
        if ((document == null || document.isEmpty())
                && (legalName == null || legalName.isEmpty())
                && (registrationNumber == null || registrationNumber.isEmpty())) {
            throw new InvalidArgumentException("Provide a document, or legalName/registrationNumber.");
        }
        Map<String, Object> payload = new LinkedHashMap<>();
        if (document != null && !document.isEmpty()) payload.put("document", IDAnalyzerClient.parseInput(document, true));
        if (legalName != null && !legalName.isEmpty()) payload.put("legalName", legalName);
        if (legalNameLocal != null && !legalNameLocal.isEmpty()) payload.put("legalNameLocal", legalNameLocal);
        if (registrationNumber != null && !registrationNumber.isEmpty()) payload.put("registrationNumber", registrationNumber);
        if (taxNumber != null && !taxNumber.isEmpty()) payload.put("taxNumber", taxNumber);
        if (lei != null && !lei.isEmpty()) payload.put("lei", lei);
        if (entityType != null && !entityType.isEmpty()) payload.put("entityType", entityType);
        if (country != null && !country.isEmpty()) payload.put("countryIso2", country);
        if (state != null && !state.isEmpty()) payload.put("state", state);
        // KYB is heavier than a scan, allow up to 120 seconds for the response.
        return client.request("POST", "kyb", payload, null, 120);
    }
}
