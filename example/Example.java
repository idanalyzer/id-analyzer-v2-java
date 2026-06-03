import com.idanalyzer.*;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Example usage of the ID Analyzer API v2 Java SDK.
 * Set IDANALYZER_KEY (and optionally IDANALYZER_REGION=eu) before running.
 */
public class Example {
    public static void main(String[] args) {
        IDAnalyzerClient client = new IDAnalyzerClient(System.getenv("IDANALYZER_KEY"));

        // Standard scan with biometric verification.
        Profile profile = new Profile(Profile.SECURITY_MEDIUM);
        client.scanner.setProfile(profile);
        JsonNode scan = client.scanner.scan("id_front.jpg", "", "selfie.jpg", "");
        System.out.println("decision: " + scan.path("decision"));

        // Quick OCR-only scan.
        client.scanner.quickScan("id_front.jpg", "", true);

        // AML screening.
        client.aml.search("John Smith", null, 0, "US", null, null);
        client.aml.searchV3("John Smith", null, 10, 1);

        // Account quota.
        System.out.println("account: " + client.account.getAccount());
    }
}
