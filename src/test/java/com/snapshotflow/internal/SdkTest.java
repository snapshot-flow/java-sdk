package com.snapshotflow.internal;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SdkTest {

    @Test
    void versionMatchesTheBuiltProjectVersion() {
        assertEquals(System.getProperty("snapshotflow.expectedVersion"), Sdk.VERSION);
    }

    @Test
    void userAgentCarriesTheVersion() {
        assertTrue(Sdk.USER_AGENT.startsWith("snapshotflow-java/" + Sdk.VERSION + " "));
    }
}
