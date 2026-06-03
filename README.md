# ID Analyzer Java SDK (API v2)

Official Java/Kotlin client library for the [ID Analyzer API v2](https://www.idanalyzer.com) —
worldwide passport, driver license and ID card scanning, biometric face/liveness
verification, AML/PEP screening, DocuPass remote verification & e-signature, KYC
profile management and contract generation.

Targets the load-balanced `api2.idanalyzer.com` fleet (US, default) or
`api2-eu.idanalyzer.com` (EU). Java 11+ (uses the built-in `java.net.http` client;
JSON via Jackson).

## Installation
Maven:
```xml
<dependency>
    <groupId>com.idanalyzer</groupId>
    <artifactId>id-analyzer-v2</artifactId>
    <version>1.0.0</version>
</dependency>
```
Gradle:
```groovy
implementation 'com.idanalyzer:id-analyzer-v2:1.0.0'
```

## Base URL / Region
The default region is US. Select EU via the `IDANALYZER_REGION=eu` environment
variable or the constructor:

```java
IDAnalyzerClient client = new IDAnalyzerClient("YOUR_API_KEY", "eu");
```

The API key also falls back to the `IDANALYZER_KEY` environment variable. For an
on-premise ID Fort host, call `client.setBaseUrl("https://your-host")`. An
unrecognized region throws `InvalidArgumentException`.

## Quick start
```java
import com.idanalyzer.*;
import com.fasterxml.jackson.databind.JsonNode;

public class Example {
    public static void main(String[] args) {
        IDAnalyzerClient client = new IDAnalyzerClient("YOUR_API_KEY");

        Profile profile = new Profile(Profile.SECURITY_MEDIUM);
        client.scanner.setProfile(profile);
        JsonNode result = client.scanner.scan("id_front.jpg", "", "selfie.jpg", "");
        System.out.println(result.get("decision"));

        // AML screening
        client.aml.search("John Smith", null, 0, "US", null, null);   // POST /aml
        client.aml.searchV3("John Smith", null, 10, 1);               // POST /amlv3

        // Account quota
        System.out.println(client.account.getAccount());
    }
}
```

## API Coverage
The SDK exposes the full ID Analyzer API v2 surface via service fields on the client:

- **client.scanner** — `scan`, `quickScan`, `veryQuickScan`
- **client.biometric** — `verifyFace`, `verifyLiveness`
- **client.aml** — `search` (`/aml`), `searchV3` (`/amlv3`)
- **client.contract** — `generate` + template CRUD
- **client.transaction** — `getTransaction`, `listTransaction`, `updateTransaction`, `deleteTransaction`, `exportTransaction`, `saveImage`, `saveFile`
- **client.docupass** — `createDocupass`, `listDocupass`, `getDocupass`, `deleteDocupass`
- **client.profile** — KYC profile create/list/get/update/delete/export
- **client.webhook** — `listWebhook`, `resendWebhook`, `deleteWebhook`
- **client.account** — `getAccount` (`/myaccount`)

## Errors
API-level errors throw `ApiException` (with `getCode()` and `getMessage()`);
invalid client-side arguments throw `InvalidArgumentException`. Both are unchecked.

## License
MIT
