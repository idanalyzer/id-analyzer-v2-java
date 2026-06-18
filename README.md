# ID Analyzer Java SDK — Identity Verification, KYC, Document & Biometric API

[![Maven Central](https://img.shields.io/maven-central/v/com.idanalyzer/id-analyzer-v2.svg)](https://central.sonatype.com/artifact/com.idanalyzer/id-analyzer-v2)
[![javadoc](https://javadoc.io/badge2/com.idanalyzer/id-analyzer-v2/javadoc.svg)](https://javadoc.io/doc/com.idanalyzer/id-analyzer-v2)
[![license](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

Official Java / Kotlin client library for the **[ID Analyzer](https://www.idanalyzer.com) API v2** — automate identity document verification, KYC onboarding and biometric checks in minutes.

Scan and authenticate **passports, driver's licenses, ID cards, visas and residence permits from 190+ countries**, run **1:1 face match and liveness detection**, screen against **AML / PEP / sanctions** watchlists, and onboard users remotely with **DocuPass** hosted verification & e-signature.

- 🌐 **Website:** [www.idanalyzer.com](https://www.idanalyzer.com)
- 📚 **Developer docs & API reference:** [developer.idanalyzer.com](https://developer.idanalyzer.com/help)
- 📖 **Full SDK class reference (auto-generated):** [https://idanalyzer.github.io/id-analyzer-v2-java/](https://idanalyzer.github.io/id-analyzer-v2-java/)
- 🔑 **Get your API key:** [portal2.idanalyzer.com](https://portal2.idanalyzer.com)
- 💬 **Support:** support@idanalyzer.com

## Features

- **Document OCR & authentication** — passport, driver's license, ID card, visa & residence-permit recognition from 190+ countries, including MRZ and PDF417 / AAMVA barcode parsing.
- **Biometric verification** — 1:1 face match and liveness / presentation-attack detection.
- **AML screening** — PEP, sanctions, watchlist and adverse-media checks.
- **DocuPass** — hosted, no-code remote identity verification, KYC/AML onboarding and legally-binding e-signature.
- **KYC profiles, transaction vault, contract generation and webhooks.**
- **US & EU data-residency regions.**

> ⚠️ Never embed your API key in client-side apps (mobile, browser JS). Call the API from your server.

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

Requires Java 11+ (built on `java.net.http`; JSON via Jackson).

## Authentication & region

Pass your API key to the constructor, or set the `IDANALYZER_KEY` environment variable. The SDK targets the US endpoint (`https://api2.idanalyzer.com`) by default; select the EU endpoint via `IDANALYZER_REGION=eu` or `new IDAnalyzerClient(key, "eu")`. For on-premise ID Fort, call `client.setBaseUrl("https://your-host")`.

## Quick start

```java
import com.idanalyzer.*;
import com.fasterxml.jackson.databind.JsonNode;

IDAnalyzerClient client = new IDAnalyzerClient("YOUR_API_KEY");

Profile profile = new Profile(Profile.SECURITY_MEDIUM);
client.scanner.setProfile(profile);

// Scan a document + selfie for biometric verification
JsonNode result = client.scanner.scan("id_front.jpg", "", "selfie.jpg", "");
System.out.println(result.get("decision"));   // accept / review / reject
```

## Examples

```java
// AML / PEP / sanctions screening
client.aml.search("John Smith", null, 0, "US", null, null);   // POST /aml
client.aml.searchV3("John Smith", null, 10, 1);               // POST /amlv3

// KYB — business verification
// Verify a business from its registration/incorporation document. A document is
// required; an optional profile selects the KYC profile. The service extracts
// the company details, checks official company registries, screens against
// sanctions/PEP, and returns directors/owners to verify.
client.kyb.verify("registration.jpg");                 // document required
client.kyb.verify("registration.jpg", "YOUR_PROFILE"); // with an optional KYC profile

// DocuPass — hosted remote verification link
Docupass.CreateRequest req = new Docupass.CreateRequest();
req.profile = "YOUR_PROFILE_ID";
JsonNode link = client.docupass.createDocupass(req);
System.out.println(link.get("url"));
```

## API coverage

The SDK wraps the complete ID Analyzer API v2 surface via service fields on the client:

| Service | Methods |
|---|---|
| `client.scanner` | `scan`, `quickScan`, `veryQuickScan` |
| `client.biometric` | `verifyFace`, `verifyLiveness` |
| `client.aml` | `search` (`/aml`), `searchV3` (`/amlv3`) |
| `client.kyb` | `verify` (`/kyb`) |
| `client.contract` | `generate` + template CRUD |
| `client.transaction` | `getTransaction`, `listTransaction`, `updateTransaction`, `deleteTransaction`, `exportTransaction`, `saveImage`, `saveFile` |
| `client.docupass` | `createDocupass`, `listDocupass`, `getDocupass`, `deleteDocupass` |
| `client.profile` | KYC profile create / list / get / update / delete / export |
| `client.webhook` | `listWebhook`, `resendWebhook`, `deleteWebhook` |
| `client.account` | `getAccount` |

## Resources

- [ID Analyzer website](https://www.idanalyzer.com)
- [Developer documentation & API reference](https://developer.idanalyzer.com/help)
- [Java SDK guide](https://developer.idanalyzer.com/help/java)
- [Dashboard — get your API key](https://portal2.idanalyzer.com)

## Other ID Analyzer SDKs

[PHP](https://github.com/idanalyzer/id-analyzer-v2-php) · [Python](https://github.com/idanalyzer/id-analyzer-v2-python) · [Node.js](https://github.com/idanalyzer/id-analyzer-v2-nodejs) · [.NET](https://github.com/idanalyzer/id-analyzer-v2-dotnet) · [Java](https://github.com/idanalyzer/id-analyzer-v2-java) · [Go](https://github.com/idanalyzer/id-analyzer-v2-go)

## License

MIT © [ID Analyzer](https://www.idanalyzer.com) — see [LICENSE](LICENSE).
