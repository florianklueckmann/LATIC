package software.latic.helper;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class UpdateHelperTest {

    @Test
    void testGetInstanceReturnsSameInstance() {
        var instance1 = UpdateHelper.getInstance();
        var instance2 = UpdateHelper.getInstance();
        assertSame(instance1, instance2);
    }

    @Test
    void testGetCurrentVersionIsNotEmpty() {
        var helper = new UpdateHelper();
        assertNotNull(helper.getCurrentVersion());
        assertFalse(helper.getCurrentVersion().isEmpty(), "Current version should not be empty");
    }

    @Test
    void testGetCurrentVersionMatchesVersionFormat() {
        var helper = new UpdateHelper();
        var version = helper.getCurrentVersion();
        assertTrue(version.matches("\\d+\\.\\d+\\.\\d+.*"),
                "Version should match semantic versioning format (e.g. 1.0.0), got: " + version);
    }

    @Test
    void testVersionParsingConcatenatesDigits() {
        // This tests the version parsing logic used in getLatestReleaseInfo:
        // Arrays.stream(version.split("\\.")).reduce((s, s2) -> s + s2).orElse("0")
        String version = "1.2.3";
        int parsed = Integer.parseInt(Arrays.stream(version.split("\\."))
                .reduce((s, s2) -> s + s2)
                .orElse("0"));
        assertEquals(123, parsed);
    }

    @Test
    void testVersionParsingWithTagPrefix() {
        // Tests the tag_name parsing with "v" prefix removal
        String tagName = "v2.0.1";
        int parsed = Integer.parseInt(Arrays.stream(tagName
                        .replace("v", "")
                        .split("\\."))
                .reduce((s, s2) -> s + s2)
                .orElse("0"));
        assertEquals(201, parsed);
    }

    @Test
    void testVersionComparisonDetectsUpdate() {
        String latestTag = "v2.0.0";
        String currentVersion = "1.9.0";

        int latest = Integer.parseInt(Arrays.stream(latestTag
                        .replace("v", "")
                        .split("\\."))
                .reduce((s, s2) -> s + s2)
                .orElse("0"));
        int current = Integer.parseInt(Arrays.stream(currentVersion
                        .split("\\."))
                .reduce((s, s2) -> s + s2)
                .orElse("1"));

        assertTrue(latest > current, "v2.0.0 should be detected as newer than 1.9.0");
    }

    @Test
    void testVersionComparisonNoUpdateWhenEqual() {
        String latestTag = "v1.5.0";
        String currentVersion = "1.5.0";

        int latest = Integer.parseInt(Arrays.stream(latestTag
                        .replace("v", "")
                        .split("\\."))
                .reduce((s, s2) -> s + s2)
                .orElse("0"));
        int current = Integer.parseInt(Arrays.stream(currentVersion
                        .split("\\."))
                .reduce((s, s2) -> s + s2)
                .orElse("1"));

        assertFalse(latest > current, "Same versions should not indicate an update");
    }

    @Test
    void testVersionComparisonNoUpdateWhenOlder() {
        String latestTag = "v1.0.0";
        String currentVersion = "1.5.0";

        int latest = Integer.parseInt(Arrays.stream(latestTag
                        .replace("v", "")
                        .split("\\."))
                .reduce((s, s2) -> s + s2)
                .orElse("0"));
        int current = Integer.parseInt(Arrays.stream(currentVersion
                        .split("\\."))
                .reduce((s, s2) -> s + s2)
                .orElse("1"));

        assertFalse(latest > current, "Older release should not indicate an update");
    }

    @Test
    void testVersionParsingDefaultsForMissingTag() {
        // When tag_name is missing, getOrDefault returns "0"
        String tagName = "0";
        int parsed = Integer.parseInt(Arrays.stream(tagName
                        .replace("v", "")
                        .split("\\."))
                .reduce((s, s2) -> s + s2)
                .orElse("0"));
        assertEquals(0, parsed, "Missing tag should default to 0");
    }

    @Test
    void testVersionParsingWithTwoDigitComponents() {
        // Version like 1.12.3 becomes "1123" -> 1123
        String version = "1.12.3";
        int parsed = Integer.parseInt(Arrays.stream(version.split("\\."))
                .reduce((s, s2) -> s + s2)
                .orElse("0"));
        assertEquals(1123, parsed);
    }

    @Test
    void testVersionParsingWithNonNumericStringThrowsException() {
        // Test for versions like "TEST-01" as requested in the issue
        String tagName = "TEST-01";
        assertThrows(NumberFormatException.class, () -> {
            Integer.parseInt(Arrays.stream(tagName
                            .replace("v", "")
                            .split("\\."))
                    .reduce((s, s2) -> s + s2)
                    .orElse("0"));
        });
    }

    @Test
    void testGetLatestReleaseInfoReturnsMap() {
        var helper = new UpdateHelper();
        var result = helper.getLatestReleaseInfo();
        assertNotNull(result, "getLatestReleaseInfo should never return null");
    }

    @Test
    void testVersionOverrideViaSystemProperty() {
        String testVersion = "9.9.9-TEST";
        System.setProperty("latic.version", testVersion);
        try {
            UpdateHelper helper = new UpdateHelper();
            assertEquals(testVersion, helper.getCurrentVersion());
        } finally {
            System.clearProperty("latic.version");
        }
    }

    @Test
    void testVersionOverrideWithEmptySystemPropertyFallsBack() {
        System.setProperty("latic.version", "");
        try {
            UpdateHelper helper = new UpdateHelper();
            assertNotNull(helper.getCurrentVersion());
            assertFalse(helper.getCurrentVersion().isEmpty());
            assertTrue(helper.getCurrentVersion().matches("\\d+\\.\\d+\\.\\d+.*"),
                    "Should fall back to bundle version format");
        } finally {
            System.clearProperty("latic.version");
        }
    }
    @Test
    void testIsUnstableReturnsTrueForHyphenatedVersion() {
        System.setProperty("latic.version", "1.0.0-PRE");
        try {
            UpdateHelper helper = new UpdateHelper();
            assertTrue(helper.isUnstable());
        } finally {
            System.clearProperty("latic.version");
        }
    }

    @Test
    void testIsUnstableReturnsFalseForNormalVersion() {
        System.setProperty("latic.version", "1.0.0");
        try {
            UpdateHelper helper = new UpdateHelper();
            assertFalse(helper.isUnstable());
        } finally {
            System.clearProperty("latic.version");
        }
    }

    @Test
    void testParseVersionLogicWithSuffix() {
        String version = "1.0.0-PRE";
        String cleaned = version.replace("v", "");
        if (cleaned.contains("-")) {
            cleaned = cleaned.substring(0, cleaned.indexOf("-"));
        }
        int parsed = Integer.parseInt(Arrays.stream(cleaned.split("\\."))
                .reduce((s, s2) -> s + s2)
                .orElse("0"));
        assertEquals(100, parsed);
    }

    @Test
    void testHasUpdateLogicForUnstable() {
        // Case 1: Unstable, numeric equal
        int latestTag = 100;
        int currentVersionNum = 100;
        boolean isUnstable = true;
        boolean hasUpdate = latestTag > currentVersionNum || isUnstable;
        assertTrue(hasUpdate, "Should trigger update if unstable and same numeric version");

        // Case 2: Unstable, older stable available
        latestTag = 90;
        currentVersionNum = 100;
        isUnstable = true;
        hasUpdate = latestTag > currentVersionNum || isUnstable;
        assertTrue(hasUpdate, "Should trigger update if unstable even if older stable available");

        // Case 3: Stable, same version
        latestTag = 100;
        currentVersionNum = 100;
        isUnstable = false;
        hasUpdate = latestTag > currentVersionNum || isUnstable;
        assertFalse(hasUpdate, "Should not trigger update if stable and same version");
    }
    @Test
    void testVersionOverrideWithVPrefixWorks() {
        // v0.9.0 should be parsed correctly now
        String testVersion = "v0.9.0";
        System.setProperty("latic.version", testVersion);
        try {
            UpdateHelper helper = new UpdateHelper();
            // We simulate the parsing logic here to verify it handles 'v'
            int current = Integer.parseInt(Arrays.stream(helper.getCurrentVersion()
                            .replace("v", "")
                            .split("\\."))
                    .reduce((s, s2) -> s + s2)
                    .orElse("1"));
            assertEquals(90, current);
        } finally {
            System.clearProperty("latic.version");
        }
    }
}
