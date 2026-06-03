package com.idanalyzer;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.LinkedHashMap;
import java.util.Map;

/** Document scanning &amp; ID verification (POST /scan, /quickscan, /veryquickscan). */
public class Scanner {

    private final IDAnalyzerClient client;
    private final Map<String, Object> config = new LinkedHashMap<>();

    Scanner(IDAnalyzerClient client) {
        this.client = client;
    }

    /**
     * Pass a user IP for issuer-country geolocation check (use "user" for the connection IP).
     *
     * @param ip the user IP address, or "user" to use the connection IP.
     * @return this scanner, for chaining.
     */
    public Scanner setUserIp(String ip) {
        config.put("ip", ip);
        return this;
    }

    /**
     * Arbitrary string stored with the transaction (e.g. internal reference).
     *
     * @param customData the custom data string to store with the transaction.
     * @return this scanner, for chaining.
     */
    public Scanner setCustomData(String customData) {
        config.put("customData", customData);
        return this;
    }

    /**
     * Auto-generate one or more contracts from the parsed ID.
     *
     * @param templateId the contract template id; when null/empty no contract is generated.
     * @param format the output format; defaults to "PDF" when null/empty.
     * @param extraFillData optional extra fields to populate the contract; applied when non-empty.
     * @return this scanner, for chaining.
     */
    public Scanner setContractOptions(String templateId, String format, Map<String, Object> extraFillData) {
        if (templateId != null && !templateId.isEmpty()) {
            config.put("contractGenerate", templateId);
            config.put("contractFormat", format == null || format.isEmpty() ? "PDF" : format);
            if (extraFillData != null && !extraFillData.isEmpty()) {
                config.put("contractPrefill", extraFillData);
            }
        }
        return this;
    }

    /**
     * Attach a KYC profile (required before {@link #scan}).
     *
     * @param profile the KYC profile to apply; its overrides are sent when non-empty.
     * @return this scanner, for chaining.
     * @throws InvalidArgumentException if profile is null.
     */
    public Scanner setProfile(Profile profile) {
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
     * Supply expected customer information to match against the document.
     *
     * @param documentNumber the expected document number.
     * @param fullName the expected full name.
     * @param dob the expected date of birth in {@code YYYY/MM/DD} format; may be null/empty.
     * @param ageRange the expected age range in {@code minAge-maxAge} format; may be null/empty.
     * @param address the expected address.
     * @param postcode the expected postcode.
     * @return this scanner, for chaining.
     * @throws InvalidArgumentException if dob or ageRange is non-empty but malformed.
     */
    public Scanner verifyUserInformation(String documentNumber, String fullName, String dob,
                                         String ageRange, String address, String postcode) {
        config.put("verifyDocumentNumber", documentNumber);
        config.put("verifyName", fullName);
        if (dob != null && !dob.isEmpty() && !dob.matches("^\\d{4}/\\d{2}/\\d{2}$")) {
            throw new InvalidArgumentException("Invalid birthday format (YYYY/MM/DD)");
        }
        config.put("verifyDob", dob == null ? "" : dob);
        if (ageRange != null && !ageRange.isEmpty() && !ageRange.matches("^\\d+-\\d+$")) {
            throw new InvalidArgumentException("Invalid age range format (minAge-maxAge)");
        }
        config.put("verifyAge", ageRange == null ? "" : ageRange);
        config.put("verifyAddress", address);
        config.put("verifyPostcode", postcode);
        return this;
    }

    /**
     * Restrict accepted documents to the given issuing country code(s).
     *
     * @param countryCodes comma-separated ISO country code(s) to accept.
     * @return this scanner, for chaining.
     */
    public Scanner restrictCountry(String countryCodes) {
        config.put("restrictCountry", countryCodes);
        return this;
    }

    /**
     * Restrict accepted documents to the given issuing state(s)/province(s).
     *
     * @param states comma-separated state/province code(s) to accept.
     * @return this scanner, for chaining.
     */
    public Scanner restrictState(String states) {
        config.put("restrictState", states);
        return this;
    }

    /**
     * Restrict accepted documents to the given document type(s).
     *
     * @param documentType the document type(s) to accept (e.g. "P" passport, "D" driver license).
     * @return this scanner, for chaining.
     */
    public Scanner restrictType(String documentType) {
        config.put("restrictType", documentType);
        return this;
    }

    /**
     * Initiate a full identity document scan &amp; optional biometric verification.
     *
     * @param documentFront the front document image (file path, base64, URL or cache reference).
     * @param documentBack the back document image; sent when non-empty.
     * @param facePhoto a face photo for biometric verification; used when non-empty.
     * @param faceVideo a face video for biometric verification; used when facePhoto is empty.
     * @return the API response as a {@link JsonNode}.
     * @throws InvalidArgumentException if no profile is configured or the front document image is missing.
     * @throws ApiException if the API returns an error or a transport error occurs.
     */
    public JsonNode scan(String documentFront, String documentBack, String facePhoto, String faceVideo) {
        if (!config.containsKey("profile")) {
            throw new InvalidArgumentException("KYC Profile not configured, call setProfile before scan().");
        }
        if (documentFront == null || documentFront.isEmpty()) {
            throw new InvalidArgumentException("Primary document image required.");
        }
        Map<String, Object> payload = new LinkedHashMap<>(config);
        payload.put("document", IDAnalyzerClient.parseInput(documentFront, true));
        if (documentBack != null && !documentBack.isEmpty()) {
            payload.put("documentBack", IDAnalyzerClient.parseInput(documentBack, true));
        }
        if (facePhoto != null && !facePhoto.isEmpty()) {
            payload.put("face", IDAnalyzerClient.parseInput(facePhoto, true));
        } else if (faceVideo != null && !faceVideo.isEmpty()) {
            payload.put("faceVideo", IDAnalyzerClient.parseInput(faceVideo, false));
        }
        return client.request("POST", "scan", payload, null);
    }

    /**
     * Quick OCR-only scan.
     *
     * @param documentFront the front document image (file path, base64 or URL).
     * @param documentBack the back document image; sent when non-empty.
     * @param cacheImage whether the uploaded image should be cached server-side for later reference.
     * @return the API response as a {@link JsonNode}.
     * @throws InvalidArgumentException if the front document image is missing.
     * @throws ApiException if the API returns an error or a transport error occurs.
     */
    public JsonNode quickScan(String documentFront, String documentBack, boolean cacheImage) {
        return quick("quickscan", documentFront, documentBack, cacheImage);
    }

    /**
     * Very fast OCR-only scan.
     *
     * @param documentFront the front document image (file path, base64 or URL).
     * @param documentBack the back document image; sent when non-empty.
     * @param cacheImage whether the uploaded image should be cached server-side for later reference.
     * @return the API response as a {@link JsonNode}.
     * @throws InvalidArgumentException if the front document image is missing.
     * @throws ApiException if the API returns an error or a transport error occurs.
     */
    public JsonNode veryQuickScan(String documentFront, String documentBack, boolean cacheImage) {
        return quick("veryquickscan", documentFront, documentBack, cacheImage);
    }

    private JsonNode quick(String uri, String documentFront, String documentBack, boolean cacheImage) {
        if (documentFront == null || documentFront.isEmpty()) {
            throw new InvalidArgumentException("Primary document image required.");
        }
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("saveFile", cacheImage);
        payload.put("document", IDAnalyzerClient.parseInput(documentFront, false));
        if (documentBack != null && !documentBack.isEmpty()) {
            payload.put("documentBack", IDAnalyzerClient.parseInput(documentBack, false));
        }
        return client.request("POST", uri, payload, null);
    }
}
