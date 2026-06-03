package com.idanalyzer;

import com.fasterxml.jackson.databind.JsonNode;

/** Account information (GET /myaccount). */
public class Account {

    private final IDAnalyzerClient client;

    Account(IDAnalyzerClient client) {
        this.client = client;
    }

    /** Retrieve the current account profile, quota and usage. */
    public JsonNode getAccount() {
        return client.request("GET", "myaccount", null, null);
    }
}
