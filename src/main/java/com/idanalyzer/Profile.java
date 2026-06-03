package com.idanalyzer;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Builds a KYC profile / profileOverride object that can be attached to scan,
 * biometric and docupass calls, or used to create/update a stored profile via
 * {@link ProfileApi}. All mutators return {@code this} for chaining.
 */
public class Profile {

    public static final String SECURITY_NONE = "security_none";
    public static final String SECURITY_LOW = "security_low";
    public static final String SECURITY_MEDIUM = "security_medium";
    public static final String SECURITY_HIGH = "security_high";

    private final String profileId;
    private final Map<String, Object> override = new LinkedHashMap<>();

    /** Creates a Profile. If profileId is null/empty, {@link #SECURITY_NONE} is used. */
    public Profile(String profileId) {
        this.profileId = (profileId == null || profileId.isEmpty()) ? SECURITY_NONE : profileId;
    }

    public String getProfileId() {
        return profileId;
    }

    public Map<String, Object> getOverride() {
        return override;
    }

    private Profile set(String key, Object value) {
        override.put(key, value);
        return this;
    }

    public Profile loadFromJson(Map<String, Object> config) {
        override.putAll(config);
        return this;
    }

    public Profile canvasSize(int pixels) { return set("canvasSize", pixels); }
    public Profile orientationCorrection(boolean enabled) { return set("orientationCorrection", enabled); }
    public Profile objectDetection(boolean enabled) { return set("objectDetection", enabled); }
    public Profile aamvaBarcodeParsing(boolean enabled) { return set("AAMVABarcodeParsing", enabled); }
    public Profile outputSize(int pixels) { return set("outputSize", pixels); }
    public Profile inferFullName(boolean enabled) { return set("inferFullName", enabled); }
    public Profile splitFirstName(boolean enabled) { return set("splitFirstName", enabled); }
    public Profile transactionAuditReport(boolean enabled) { return set("transactionAuditReport", enabled); }
    public Profile setTimezone(String timezone) { return set("timezone", timezone); }
    public Profile obscure(List<String> fieldKeys) { return set("obscure", fieldKeys); }
    public Profile webhook(String url) { return set("webhook", url); }

    public Profile saveResult(boolean saveTransaction, boolean saveImages) {
        set("saveResult", saveTransaction);
        if (saveTransaction) {
            set("saveImage", saveImages);
        }
        return this;
    }

    public Profile outputImage(boolean enable, String format) {
        set("outputImage", enable);
        if (enable) {
            set("outputType", format);
        }
        return this;
    }

    public Profile autoCrop(boolean crop, boolean advancedCrop) {
        set("crop", crop);
        return set("advancedCrop", advancedCrop);
    }

    @SuppressWarnings("unchecked")
    public Profile threshold(String key, double value) {
        Map<String, Object> t = (Map<String, Object>) override.computeIfAbsent("thresholds", k -> new LinkedHashMap<String, Object>());
        t.put(key, value);
        return this;
    }

    public Profile decisionTrigger(double reviewTrigger, double rejectTrigger) {
        Map<String, Object> d = new LinkedHashMap<>();
        d.put("review", reviewTrigger);
        d.put("reject", rejectTrigger);
        return set("decisionTrigger", d);
    }

    @SuppressWarnings("unchecked")
    public Profile setWarning(String code, boolean enabled, double reviewThreshold, double rejectThreshold, double weight) {
        Map<String, Object> decisions = (Map<String, Object>) override.computeIfAbsent("decisions", k -> new LinkedHashMap<String, Object>());
        Map<String, Object> entry = new LinkedHashMap<>();
        entry.put("enabled", enabled);
        entry.put("review", reviewThreshold);
        entry.put("reject", rejectThreshold);
        entry.put("weight", weight);
        decisions.put(code, entry);
        return this;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> acceptedDocuments() {
        return (Map<String, Object>) override.computeIfAbsent("acceptedDocuments", k -> new LinkedHashMap<String, Object>());
    }

    public Profile restrictDocumentCountry(String countryCodes) {
        acceptedDocuments().put("documentCountry", countryCodes);
        return this;
    }

    public Profile restrictDocumentState(String states) {
        acceptedDocuments().put("documentState", states);
        return this;
    }

    public Profile restrictDocumentType(String documentType) {
        acceptedDocuments().put("documentType", documentType);
        return this;
    }
}
