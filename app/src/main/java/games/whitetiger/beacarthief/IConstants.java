package games.whitetiger.beacarthief;

class IConstants {
    /**
     * API Returns
     */
    static final int API_SQL_FAIL = 100;
    static final int AUTHORIZATION_FAIL = 101;

    static final int USER_CREATED_SUCCESSFULLY = 0;
    static final int USER_ALREADY_EXISTED = 1;

    static final int USER_LOGIN_SUCCESS = 0;
    static final int USER_NOT_EXISTS = 1;
    static final int PASSWORD_NOT_CORRECT = 2;
    static final int USER_NOT_ENABLED = 3;
    static final int USER_IS_BANNED = 4;

    static final int PASSWORD_RESET_SUCCESS = 0;

    static final int CHANGE_SUCCESS = 0;
    static final int EMAIL_ALREADY_EXISTED = 1;

    static final int VEHICLE_CREATED_SUCCESSFULLY = 0;

    /**
     * Preferences Names
     */
    static final String PREFERENCE_USER = "userData";
    static final String PREFERENCE_USER_VEHICLES = "userVehiclesData";
    static final String PREFERENCE_GLOBAL = "globalData";
    static final String USERNAME = "username";
    static final String EMAIL = "email";
    static final String API_KEY = "apiKey";
    static final String LEVEL = "level";
    static final String EXPERIENCE = "experience";
    static final String VEHICLES = "vehicles";

    /**
     * Game Data
     */
    static final int RADIUS = 80;
    static final int VEHICLE_STEAL_CHANCE = 35;
    static final int VEHICLE_GENERATE_CHANCE = 30;
    static final int VEHICLE_EARN_EXPERIENCE = 5;
}