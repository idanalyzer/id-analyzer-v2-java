# Changelog

## 1.0.0 (2026-06-03)

Initial release of the official ID Analyzer **API v2** Java/Kotlin SDK.

- Full v2 surface: Scanner (scan / quickScan / veryQuickScan), Biometric
  (verifyFace / verifyLiveness), AML (search / searchV3), Contract (generate +
  template CRUD), Transaction (get / list / update / delete / export + image/file
  vault), Docupass (create / list / get / delete), ProfileApi (KYC profile CRUD +
  export), Webhook (list / resend / delete), Account (myaccount).
- Targets the `api2.idanalyzer.com` (US, default) /
  `api2-eu.idanalyzer.com` (EU); region via `IDANALYZER_REGION` or the constructor.
- Java 11+, built on `java.net.http.HttpClient`; JSON via Jackson; returns
  `com.fasterxml.jackson.databind.JsonNode`.
- Coordinates `com.idanalyzer:id-analyzer-v2`.
