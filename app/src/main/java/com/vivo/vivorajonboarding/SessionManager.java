package com.vivo.vivorajonboarding;

import android.content.Context;
import android.content.SharedPreferences;

import com.vivo.vivorajonboarding.model.UserModel;

public class SessionManager {
    // Shared Preferences constants
    private static final String PREF_NAME = "VivoRajOnboardingSession";
    private static final int PRIVATE_MODE = 0;

    // Shared preferences keys
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_ID = "id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_USERID = "userid";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_EMPLOYEE_LEVEL = "employee_level";
    private static final String KEY_CATEGORY = "category";
    private static final String KEY_USER_STATUS = "user_status";
    private static final String KEY_CANDIDATE_CATEGORY = "candidate_category";
    private static final String KEY_REMEMBER_ME = "remember_me";

    // App version related keys
    private static final String KEY_VERSION_CODE = "version_code";
    private static final String KEY_VERSION_NAME = "version_name";
    private static final String KEY_ANDROID_VERSION = "android_version";
    private static final String KEY_ANDROID_SDK = "android_sdk";

    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;
    private final Context context;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Stores user login session data
     */
    public void createLoginSession(UserModel user, boolean rememberMe) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putBoolean(KEY_REMEMBER_ME, rememberMe);

        // Store user details
        editor.putString(KEY_ID, user.getId());
        editor.putString(KEY_USERNAME, user.getUserName());
        editor.putString(KEY_USERID, user.getUserid());
        editor.putString(KEY_PASSWORD, user.getPassword());
        editor.putString(KEY_EMPLOYEE_LEVEL, user.getEmployee_level());
        editor.putString(KEY_CATEGORY, user.getCategory());
        editor.putString(KEY_USER_STATUS, user.getUser_status());
        editor.putString(KEY_CANDIDATE_CATEGORY, user.getCandidate_category());

        // Commit changes
        editor.apply();
    }

    /**
     * Stores app version information
     */
    public void saveAppVersionInfo(String versionCode, String versionName,
                                   String androidVersion, String androidSDK) {
        editor.putString(KEY_VERSION_CODE, versionCode);
        editor.putString(KEY_VERSION_NAME, versionName);
        editor.putString(KEY_ANDROID_VERSION, androidVersion);
        editor.putString(KEY_ANDROID_SDK, androidSDK);
        editor.apply();
    }

    /**
     * Get stored session data
     */
    public UserModel getUserDetails() {
        UserModel user = new UserModel();
        user.setId(pref.getString(KEY_ID, null));
        user.setUserName(pref.getString(KEY_USERNAME, null));
        user.setUserid(pref.getString(KEY_USERID, null));
        user.setPassword(pref.getString(KEY_PASSWORD, null));
        user.setEmployee_level(pref.getString(KEY_EMPLOYEE_LEVEL, null));
        user.setCategory(pref.getString(KEY_CATEGORY, null));
        user.setUser_status(pref.getString(KEY_USER_STATUS, null));
        user.setCandidate_category(pref.getString(KEY_CANDIDATE_CATEGORY, null));
        return user;
    }

    /**
     * Quick check for login
     */
    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Check if remember me is enabled
     */
    public boolean isRememberMeEnabled() {
        return pref.getBoolean(KEY_REMEMBER_ME, false);
    }

    /**
     * Get specific user attributes
     */
    public String getUserId() {
        return pref.getString(KEY_USERID, null);
    }

    public String getUserName() {
        return pref.getString(KEY_USERNAME, null);
    }

    public String getCategory() {
        return pref.getString(KEY_CATEGORY, null);
    }

    public String getEmployeeLevel() {
        return pref.getString(KEY_EMPLOYEE_LEVEL, null);
    }

    /**
     * Get app version information
     */
    public AppVersionInfo getAppVersionInfo() {
        return new AppVersionInfo(
                pref.getString(KEY_VERSION_CODE, null),
                pref.getString(KEY_VERSION_NAME, null),
                pref.getString(KEY_ANDROID_VERSION, null),
                pref.getString(KEY_ANDROID_SDK, null)
        );
    }

    /**
     * Update user status
     */
    public void updateUserStatus(String newStatus) {
        editor.putString(KEY_USER_STATUS, newStatus);
        editor.apply();
    }

    /**
     * Update user category
     */
    public void updateUserCategory(String newCategory) {
        editor.putString(KEY_CATEGORY, newCategory);
        editor.apply();
    }

    /**
     * Clear session details
     */
    public void logout() {
        // If remember me is not enabled, clear all data
        if (!isRememberMeEnabled()) {
            editor.clear();
        } else {
            // Keep only version information if remember me is enabled
            String vCode = pref.getString(KEY_VERSION_CODE, null);
            String vName = pref.getString(KEY_VERSION_NAME, null);
            String androidV = pref.getString(KEY_ANDROID_VERSION, null);
            String sdk = pref.getString(KEY_ANDROID_SDK, null);

            editor.clear();

            // Restore version information
            editor.putString(KEY_VERSION_CODE, vCode);
            editor.putString(KEY_VERSION_NAME, vName);
            editor.putString(KEY_ANDROID_VERSION, androidV);
            editor.putString(KEY_ANDROID_SDK, sdk);
        }
        editor.putBoolean(KEY_IS_LOGGED_IN, false);
        editor.apply();
    }

    /**
     * Inner class to hold app version information
     */
    public static class AppVersionInfo {
        private final String versionCode;
        private final String versionName;
        private final String androidVersion;
        private final String androidSDK;

        public AppVersionInfo(String versionCode, String versionName,
                              String androidVersion, String androidSDK) {
            this.versionCode = versionCode;
            this.versionName = versionName;
            this.androidVersion = androidVersion;
            this.androidSDK = androidSDK;
        }

        public String getVersionCode() { return versionCode; }
        public String getVersionName() { return versionName; }
        public String getAndroidVersion() { return androidVersion; }
        public String getAndroidSDK() { return androidSDK; }
    }
}