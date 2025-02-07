package com.vivo.vivorajonboarding.constants;

public class URLs {

    public static final String APP_BASE_URL = "https://vivorajonbording.com/api/vso_api/";
    //Login Activity
    public static final String LOGIN_URL = "https://vivorajonbording.com/api/vso_api/abhinav_test_api/" + "login.php";
    //User Dashboard Activity
    public static final String GET_PROFILE_DATA_URL = APP_BASE_URL + "getProfileData.php";
    public static final String CHECK_AND_SEND_REQUEST_URL = APP_BASE_URL + "checkAndSendRequest.php";

    //Personal Activity
    public static final String GET_PERSONAL_DATA_URL = APP_BASE_URL + "getPersonalData.php";
    public static final String SAVE_PERSONAL_DATA_URL = APP_BASE_URL + "savePersonalData.php";

    //Document Activity
    public static final String GET_ALL_DOCUMENTS_DATA_URL = APP_BASE_URL + "getAllDocumentsData.php";
    public static final String SAVE_DOCUMENT_UPLOAD_URL = APP_BASE_URL + "saveDocumentUpload.php";
    public static final String SAVE_PDF_DOCUMENT_UPLOAD_URL = APP_BASE_URL + "/fileUpload.php";

    //Qualification Activity
    public static final String GET_ALL_QUALIFICATION_DATA_URL = APP_BASE_URL + "getAllQualificationData.php";
    public static final String SAVE_QUALIFICATION_DATA_URL = APP_BASE_URL + "saveQualificationData.php";

    //Nominee Activity
    public static final String GET_ALL_NOMINEE_DATA_URL = APP_BASE_URL + "getAllNomineeData.php";
    public static final String SAVE_NOMINEE_DATA_URL = APP_BASE_URL + "saveNomineeData.php";

    //Experience Activity
    public static final String GET_ALL_EXPERIENCE_DATA_URL = APP_BASE_URL + "getAllExperienceData.php";
    public static final String SAVE_EXPERIENCE_DATA_URL = APP_BASE_URL + "saveExperienceData.php";
    public static final String REMOVE_EXPERIENCE_DATA_BY_ID_URL = APP_BASE_URL + "removeExperienceDataById.php";

    //Insurance Activity
    public static final String GET_ALL_INSURANCE_DATA_URL = APP_BASE_URL + "getAllInsuranceData.php";
    public static final String SAVE_INSURANCE_DATA_URL = APP_BASE_URL + "saveInsuranceData.php";
    public static final String REMOVE_INSURANCE_DATA_BY_ID_URL = APP_BASE_URL + "removeInsuranceDataById.php";
    public static final String UPDATE_MEMBER_SELECTION_FOR_INSURANCE_URL = APP_BASE_URL + "updateMemberSelectionForInsurance.php";

    //Preview Activity
    public static final String GET_USER_PREVIEW_DATA_URL = APP_BASE_URL + "getUserPreviewData.php";

    //Notification Activity
    public static final String GET_ALL_NOTIFICATION_DATA_URL = APP_BASE_URL + "getAllNotificationData.php";
}
