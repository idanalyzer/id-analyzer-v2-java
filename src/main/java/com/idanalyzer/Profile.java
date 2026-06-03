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

    /** Built-in profile preset that applies no security checks. */
    public static final String SECURITY_NONE = "security_none";
    /** Built-in profile preset that applies a low level of security checks. */
    public static final String SECURITY_LOW = "security_low";
    /** Built-in profile preset that applies a medium level of security checks. */
    public static final String SECURITY_MEDIUM = "security_medium";
    /** Built-in profile preset that applies a high level of security checks. */
    public static final String SECURITY_HIGH = "security_high";

    private final String profileId;
    private final Map<String, Object> override = new LinkedHashMap<>();

    /**
     * Creates a Profile. If profileId is null/empty, {@link #SECURITY_NONE} is used.
     *
     * @param profileId the stored profile id or a built-in security preset; null/empty selects {@link #SECURITY_NONE}.
     */
    public Profile(String profileId) {
        this.profileId = (profileId == null || profileId.isEmpty()) ? SECURITY_NONE : profileId;
    }

    /**
     * Returns the profile id this profile was created with.
     *
     * @return the profile id (or {@link #SECURITY_NONE} if none was supplied).
     */
    public String getProfileId() {
        return profileId;
    }

    /**
     * Returns the accumulated profileOverride map of settings applied to this profile.
     *
     * @return the mutable override map; empty when no overrides have been set.
     */
    public Map<String, Object> getOverride() {
        return override;
    }

    private Profile set(String key, Object value) {
        override.put(key, value);
        return this;
    }

    /**
     * Bulk-load override settings from a map (e.g. a previously exported profile).
     *
     * @param config the settings to merge into this profile's override map.
     * @return this profile, for chaining.
     */
    public Profile loadFromJson(Map<String, Object> config) {
        override.putAll(config);
        return this;
    }

    /**
     * Set the working canvas size, in pixels, used during image processing.
     *
     * @param pixels the canvas size in pixels.
     * @return this profile, for chaining.
     */
    public Profile canvasSize(int pixels) { return set("canvasSize", pixels); }

    /**
     * Enable or disable automatic orientation correction of input images.
     *
     * @param enabled true to auto-correct orientation.
     * @return this profile, for chaining.
     */
    public Profile orientationCorrection(boolean enabled) { return set("orientationCorrection", enabled); }

    /**
     * Enable or disable object detection on the document image.
     *
     * @param enabled true to enable object detection.
     * @return this profile, for chaining.
     */
    public Profile objectDetection(boolean enabled) { return set("objectDetection", enabled); }

    /**
     * Enable or disable parsing of AAMVA barcodes (US/Canada driver licenses).
     *
     * @param enabled true to enable AAMVA barcode parsing.
     * @return this profile, for chaining.
     */
    public Profile aamvaBarcodeParsing(boolean enabled) { return set("AAMVABarcodeParsing", enabled); }

    /**
     * Set the maximum output image size, in pixels.
     *
     * @param pixels the output image size in pixels.
     * @return this profile, for chaining.
     */
    public Profile outputSize(int pixels) { return set("outputSize", pixels); }

    /**
     * Enable or disable inferring the full name from name components.
     *
     * @param enabled true to infer the full name.
     * @return this profile, for chaining.
     */
    public Profile inferFullName(boolean enabled) { return set("inferFullName", enabled); }

    /**
     * Enable or disable splitting the first name into separate name fields.
     *
     * @param enabled true to split the first name.
     * @return this profile, for chaining.
     */
    public Profile splitFirstName(boolean enabled) { return set("splitFirstName", enabled); }

    /**
     * Enable or disable generation of a transaction audit report.
     *
     * @param enabled true to generate a transaction audit report.
     * @return this profile, for chaining.
     */
    public Profile transactionAuditReport(boolean enabled) { return set("transactionAuditReport", enabled); }

    /**
     * Set the timezone used for date/time fields in the transaction.
     *
     * @param timezone the timezone identifier (e.g. "UTC").
     * @return this profile, for chaining.
     */
    public Profile setTimezone(String timezone) { return set("timezone", timezone); }

    /**
     * Obscure (redact) the given result field keys in the output.
     *
     * @param fieldKeys the list of field keys to obscure.
     * @return this profile, for chaining.
     */
    public Profile obscure(List<String> fieldKeys) { return set("obscure", fieldKeys); }

    /**
     * Set the webhook URL that result notifications are posted to.
     *
     * @param url the webhook URL.
     * @return this profile, for chaining.
     */
    public Profile webhook(String url) { return set("webhook", url); }

    /**
     * Configure whether the transaction and its images are saved.
     *
     * @param saveTransaction true to persist the transaction result.
     * @param saveImages true to persist images; only applied when saveTransaction is true.
     * @return this profile, for chaining.
     */
    public Profile saveResult(boolean saveTransaction, boolean saveImages) {
        set("saveResult", saveTransaction);
        if (saveTransaction) {
            set("saveImage", saveImages);
        }
        return this;
    }

    /**
     * Configure whether a processed output image is returned, and its format.
     *
     * @param enable true to return an output image.
     * @param format the output image format; only applied when enable is true.
     * @return this profile, for chaining.
     */
    public Profile outputImage(boolean enable, String format) {
        set("outputImage", enable);
        if (enable) {
            set("outputType", format);
        }
        return this;
    }

    /**
     * Configure automatic cropping of the document from the image.
     *
     * @param crop true to enable cropping.
     * @param advancedCrop true to enable advanced cropping.
     * @return this profile, for chaining.
     */
    public Profile autoCrop(boolean crop, boolean advancedCrop) {
        set("crop", crop);
        return set("advancedCrop", advancedCrop);
    }

    /**
     * Set an individual decision threshold value.
     *
     * @param key the threshold key.
     * @param value the threshold value.
     * @return this profile, for chaining.
     */
    @SuppressWarnings("unchecked")
    public Profile threshold(String key, double value) {
        Map<String, Object> t = (Map<String, Object>) override.computeIfAbsent("thresholds", k -> new LinkedHashMap<String, Object>());
        t.put(key, value);
        return this;
    }

    /**
     * Set the overall decision-trigger scores that move a transaction to review or reject.
     *
     * @param reviewTrigger the score at or above which the decision becomes "review".
     * @param rejectTrigger the score at or above which the decision becomes "reject".
     * @return this profile, for chaining.
     */
    public Profile decisionTrigger(double reviewTrigger, double rejectTrigger) {
        Map<String, Object> d = new LinkedHashMap<>();
        d.put("review", reviewTrigger);
        d.put("reject", rejectTrigger);
        return set("decisionTrigger", d);
    }

    /**
     * Configure how a specific warning code influences the decision.
     *
     * @param code the warning code to configure.
     * @param enabled true to enable this warning.
     * @param reviewThreshold the score contribution at which this warning triggers a review.
     * @param rejectThreshold the score contribution at which this warning triggers a reject.
     * @param weight the weight applied to this warning in scoring.
     * @return this profile, for chaining.
     */
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

    /**
     * Restrict accepted documents to the given issuing country code(s).
     *
     * @param countryCodes comma-separated ISO country code(s) to accept.
     * @return this profile, for chaining.
     */
    public Profile restrictDocumentCountry(String countryCodes) {
        acceptedDocuments().put("documentCountry", countryCodes);
        return this;
    }

    /**
     * Restrict accepted documents to the given issuing state(s)/province(s).
     *
     * @param states comma-separated state/province code(s) to accept.
     * @return this profile, for chaining.
     */
    public Profile restrictDocumentState(String states) {
        acceptedDocuments().put("documentState", states);
        return this;
    }

    /**
     * Restrict accepted documents to the given document type(s).
     *
     * @param documentType the document type(s) to accept (e.g. "P" passport, "D" driver license).
     * @return this profile, for chaining.
     */
    public Profile restrictDocumentType(String documentType) {
        acceptedDocuments().put("documentType", documentType);
        return this;
    }
}
