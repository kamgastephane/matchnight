package com.inetti.matchnight;

import java.util.Objects;

public class Profiles {

    private static final String PROFILE_PROPERTY_PATH = "spring.profiles.active";

    /**
     * Profile name for the default(local) environment
     * This profile is in use during the jenkins build
     * Note:
     * </p>
     * This profile is in use when NO OTHER profile is in use
     * <p>
     * Components
     * <ul>
     * <li>MS SQL Server (remote)</li>
     * </ul>
     */
    public static final String DEFAULT = "default";

    /**
     * Profile name for the production environment
     * <p>
     * Components
     * <ul>
     * <li>MS SQL Server (remote production cluster)</li>
     * <li>Mongo Database (production-environment)</li>
     * <li>Rabbit MQ (production-environment)</li>
     * <li>Push enabled (production-environment)</li>
     * </ul>
     */
    public static final String PRODUCTION = "production";



    /**
     * Profile name for the  development environment
     * </p>
     * The gradle task 'runDev' runs this profile
     * <p>
     * Components
     * <ul>
     * <li>MS SQL Server (remote)</li>
     * <li>Mongo Database (local dev-environment)</li>
     * <li>Rabbit MQ (local dev-environment)</li>
     * <li>Push disabled</li>
     * </ul>
     */
    public static final String DEVELOPMENT = "dev";

    /**
     * Profile name for the test enviroment
     * This profile is used only on tests via {@link org.springframework.test.context.ActiveProfiles}
     * Components
     * <ul>
     * <li>Depending on what Other profile is included</li>
     * </ul>
     */
    public static final String TEST = "test";
    public static final String SPRING_CACHE_DISABLED = "springCacheDisabled";
    public static final String NOT_SPRING_CACHE_DISABLED = "!" + SPRING_CACHE_DISABLED;

    private Profiles() {
        // prevent instantiation of this class
    }

    public static boolean isDevelopmentProfile() {
        return isProfile(Profiles.DEVELOPMENT);
    }

    public static boolean isProduction() {
        return isProfile(Profiles.PRODUCTION);
    }

    public static boolean isDefaultProfile() {
        String profileName = System.getProperty(PROFILE_PROPERTY_PATH);
        return Objects.equals(null, profileName);
    }

    /**
     * Checks if the specified profileName is active
     *
     * @param profileName can not be null
     * @return true if current active profile matches specified profileName
     */
    public static boolean isProfile(String profileName) {
        String activeProfiles = System.getProperty(PROFILE_PROPERTY_PATH);
        if (activeProfiles != null) {
            String[] profileArr = activeProfiles.split(",");
            for (int i = 0; i < profileArr.length; i++) {
                if (profileArr[i].equalsIgnoreCase(profileName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String getCurrentProfile() {
        return System.getProperty(PROFILE_PROPERTY_PATH);
    }
}