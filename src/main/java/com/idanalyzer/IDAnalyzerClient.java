package com.idanalyzer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Client for the ID Analyzer API v2.
 *
 * <p>Targets the load-balanced {@code api2.idanalyzer.com} fleet (US, default) or
 * {@code api2-eu.idanalyzer.com} (EU). Use the public service fields ({@link #scanner},
 * {@link #biometric}, {@link #aml}, {@link #contract}, {@link #transaction},
 * {@link #docupass}, {@link #profile}, {@link #webhook}, {@link #account}).
 */
public class IDAnalyzerClient {

    private static final Map<String, String> REGION_ENDPOINTS = Map.of(
            "us", "https://api2.idanalyzer.com",
            "eu", "https://api2-eu.idanalyzer.com");

    private final String apiKey;
    private String baseUrl;
    private final HttpClient http;
    private final ObjectMapper mapper = new ObjectMapper();

    public final Scanner scanner;
    public final Biometric biometric;
    public final AML aml;
    public final Contract contract;
    public final Transaction transaction;
    public final Docupass docupass;
    public final ProfileApi profile;
    public final Webhook webhook;
    public final Account account;

    /** Creates a client; region is read from {@code IDANALYZER_REGION} (default "us"). */
    public IDAnalyzerClient(String apiKey) {
        this(apiKey, null);
    }

    /**
     * Creates a client for an explicit region.
     *
     * @param apiKey API key; falls back to the {@code IDANALYZER_KEY} environment variable.
     * @param region "us" or "eu"; if null, falls back to {@code IDANALYZER_REGION} (default "us").
     */
    public IDAnalyzerClient(String apiKey, String region) {
        if (apiKey == null || apiKey.isEmpty()) {
            apiKey = System.getenv("IDANALYZER_KEY");
        }
        if (apiKey == null || apiKey.isEmpty()) {
            throw new InvalidArgumentException(
                    "API key required (pass it to the constructor or set IDANALYZER_KEY).");
        }
        this.apiKey = apiKey;

        if (region == null || region.isEmpty()) {
            region = System.getenv("IDANALYZER_REGION");
        }
        if (region == null || region.isEmpty()) {
            region = "us";
        }
        region = region.toLowerCase();
        String base = REGION_ENDPOINTS.get(region);
        if (base == null) {
            throw new InvalidArgumentException(
                    "Invalid region '" + region + "', valid regions are: eu, us.");
        }
        this.baseUrl = base;

        this.http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(20)).build();

        this.scanner = new Scanner(this);
        this.biometric = new Biometric(this);
        this.aml = new AML(this);
        this.contract = new Contract(this);
        this.transaction = new Transaction(this);
        this.docupass = new Docupass(this);
        this.profile = new ProfileApi(this);
        this.webhook = new Webhook(this);
        this.account = new Account(this);
    }

    /** Override the API base URL entirely (e.g. for an on-premise ID Fort host). */
    public IDAnalyzerClient setBaseUrl(String baseUrl) {
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        this.baseUrl = baseUrl;
        return this;
    }

    String endpoint(String uri) {
        if (uri.length() >= 4 && uri.substring(0, 4).equalsIgnoreCase("http")) {
            return uri;
        }
        return baseUrl + "/" + uri;
    }

    JsonNode request(String method, String uri, Map<String, Object> body, Map<String, String> query) {
        String url = endpoint(uri);
        if (query != null && !query.isEmpty()) {
            StringBuilder qs = new StringBuilder();
            for (Map.Entry<String, String> e : query.entrySet()) {
                if (qs.length() > 0) qs.append('&');
                qs.append(URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8))
                        .append('=')
                        .append(URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8));
            }
            url = url + "?" + qs;
        }

        HttpRequest.Builder rb = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(80))
                .header("X-Api-Key", apiKey);

        HttpRequest.BodyPublisher publisher;
        if (body != null) {
            String json;
            try {
                json = mapper.writeValueAsString(body);
            } catch (IOException e) {
                throw new ApiException("Failed to encode request body: " + e.getMessage(), "ENCODE", e);
            }
            publisher = HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8);
            rb.header("Content-Type", "application/json");
        } else {
            publisher = HttpRequest.BodyPublishers.noBody();
        }
        rb.method(method, publisher);

        HttpResponse<String> resp;
        try {
            resp = http.send(rb.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new ApiException("Transport error: " + e.getMessage(), "TRANSPORT", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException("Request interrupted", "TRANSPORT", e);
        }

        JsonNode node;
        try {
            String b = resp.body();
            node = (b == null || b.isEmpty()) ? mapper.createObjectNode() : mapper.readTree(b);
        } catch (IOException e) {
            throw new ApiException("Failed to decode response: " + e.getMessage(), "DECODE", e);
        }

        JsonNode error = node.get("error");
        if (error != null && error.isObject()) {
            String msg = error.path("message").asText("Unknown error");
            String code = error.path("code").asText("");
            throw new ApiException(msg, code);
        }
        return node;
    }

    void download(String uri, String dest) {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(endpoint(uri)))
                .timeout(Duration.ofSeconds(300))
                .header("X-Api-Key", apiKey)
                .GET()
                .build();
        try {
            HttpResponse<Path> resp = http.send(req, HttpResponse.BodyHandlers.ofFile(Path.of(dest)));
            resp.body();
        } catch (IOException e) {
            throw new ApiException("Download failed: " + e.getMessage(), "TRANSPORT", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException("Download interrupted", "TRANSPORT", e);
        }
    }

    /**
     * Accepts a file path, base64 string, URL, or (when allowCache) a "ref:" cache
     * reference, and returns the value to send to the API.
     */
    public static String parseInput(String input, boolean allowCache) {
        if (allowCache && input.startsWith("ref:")) {
            return input;
        }
        if (input.startsWith("http://") || input.startsWith("https://")) {
            return input;
        }
        Path p = Path.of(input);
        if (Files.isRegularFile(p)) {
            try {
                return Base64.getEncoder().encodeToString(Files.readAllBytes(p));
            } catch (IOException e) {
                throw new InvalidArgumentException("Failed to read file: " + input);
            }
        }
        if (input.length() > 100) {
            return input;
        }
        throw new InvalidArgumentException("Invalid input image, file not found or malformed URL.");
    }

    /** Convenience for building ordered request maps. */
    static Map<String, Object> map() {
        return new LinkedHashMap<>();
    }
}
