package software.latic.readability_indices.v2;

/**
 * Simple feature flag util for the new decoupled readability feature (V2).
 * Default is disabled to avoid any behavioral change unless explicitly enabled.
 */
final class FeatureToggle {
    private static final String SYS_PROP = "latic.features.readability.v2";
    private static final String ENV_VAR = "LATIC_FEATURES_READABILITY_V2";

    private FeatureToggle() { }

    static boolean enabled() {
        String prop = System.getProperty(SYS_PROP);
        if (prop != null) {
            return isTruthy(prop);
        }
        String env = System.getenv(ENV_VAR);
        if (env != null) {
            return isTruthy(env);
        }
        return false; // default disabled
    }

    private static boolean isTruthy(String v) {
        String s = v.trim().toLowerCase();
        return s.equals("1") || s.equals("true") || s.equals("yes") || s.equals("on");
    }
}
