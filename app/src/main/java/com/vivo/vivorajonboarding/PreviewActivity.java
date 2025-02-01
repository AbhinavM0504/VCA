package com.vivo.vivorajonboarding;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.vivo.vivorajonboarding.common.NetworkChangeListener;
import com.vivo.vivorajonboarding.constants.URLs;
import com.vivo.vivorajonboarding.model.PreviewModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PreviewActivity extends AppCompatActivity {

    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    //views
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageButton backBtn, downloadBtn;
    private LinearLayout mainLayout;

    //Joining Layout views
    private LinearLayout joiningLayout;
    private TextView companyTv, nameTv, fatherNameTv, dojTv, dobTv, genderTv, bloodGroupTv, maritalStatusTv, departmentTv, designationTv, gradeTv, branchTv,
            zoneTv, emailTv, contactNoTv, permanentAddressTv, currentAddressTv, bankAccountNoTv, ifscCodeTv, uanNoTv, esicNoTv, aadharNoTv, panNoTv,
            emergencyNameTv, emergencyNoTv, emergencyRelationTv;

    private LinearLayout fatherNameRow, fatherNameRow1, motherNameRow, motherNameRow1, spouseNameRow, spouseNameRow1,
            guardianNameRow, guardianNameRow1, siblingNameRow, siblingNameRow1, childOneNameRow, childOneNameRow1, childTwoNameRow, childTwoNameRow1;
    private View fatherNameRowView, fatherNameRowView1, motherNameRowView, motherNameRowView1, spouseNameRowView, spouseNameRowView1,
            guardianNameRowView, guardianNameRowView1, siblingNameRowView, siblingNameRowView1, childOneNameRowView, childOneNameRowView1, childTwoNameRowView, childTwoNameRowView1;
    private TextView nFatherNameTv, nFatherRelationTv, nFatherDobTv, nFatherAmountTv,
            nMotherNameTv, nMotherRelationTv, nMotherDobTv, nMotherAmountTv,
            nWifeNameTv, nWifeRelationTv, nWifeDobTv, nWifeAmountTv,
            nGuardianNameTv, nGuardianRelationTv, nGuardianDobTv, nGuardianAmountTv,
            nSiblingNameTv, nSiblingRelationTv, nSiblingDobTv, nSiblingAmountTv,
            nChildOneNameTv, nChildOneRelationTv, nChildOneDobTv, nChildOneAmountTv,
            nChildTwoNameTv, nChildTwoRelationTv, nChildTwoDobTv, nChildTwoAmountTv, todayDateTv;
    private ImageView userImgView, empSignImgView, empSignImgView2;
    private ProgressBar userImgPg, empImgPg, empImgPg2;

    //PF Layout Views
    private LinearLayout pfLayout;
    private TextView pfTextTv, pfText2, pfText3, pfATextTv, pfBTextTv, pfTodayDate1Tv, pfTodayDate2Tv;
    private ImageView hrSignImgView;
    private ProgressBar hrImgPg;

    //Form G Layout views
    private LinearLayout formGLayout, formG2Layout;
    private TextView formGText0, formGText1, statementTv, formGDatePlace1Tv, formGDatePlace2Tv, formGDate1Tv, formGDate2Tv;
    private TextView nFatherNameTv1, nFatherRelationTv1, nFatherDobTv1, nFatherAmountTv1,
            nMotherNameTv1, nMotherRelationTv1, nMotherDobTv1, nMotherAmountTv1,
            nWifeNameTv1, nWifeRelationTv1, nWifeDobTv1, nWifeAmountTv1,
            nGuardianNameTv1, nGuardianRelationTv1, nGuardianDobTv1, nGuardianAmountTv1,
            nSiblingNameTv1, nSiblingRelationTv1, nSiblingDobTv1, nSiblingAmountTv1,
            nChildOneNameTv1, nChildOneRelationTv1, nChildOneDobTv1, nChildOneAmountTv1, nChildTwoNameTv1,
            nChildTwoRelationTv1, nChildTwoDobTv1, nChildTwoAmountTv1;
    private ImageView empSignImgView3, hrSignImgView2;
    private ProgressBar empImgPg3, hrImgPg2;

    //Privacy Layout views
    private LinearLayout privacyTermsLayout, privacyTerms2Layout;
    private TextView nameEmpCodeTv, privacyDateTv;
    private ImageView empSignImgView4;
    private ProgressBar empImgPg4;


    //Non Disclosure Layout views
    private LinearLayout nonDisclosureLayout, nonDisclosure2Layout, nonDisclosure3Layout, nonDisclosure4Layout, nonDisclosure5Layout,
            nonDisclosure6Layout, nonDisclosure7Layout, nonDisclosure8Layout;
    private TextView nonDisclosureText0, nonDisclosureText1, partyBTextTv, partyBEmpNoTv, hrSignImgTv3, companyNameTextTv;
    private ImageView hrSignImgView3, empSignImgView5;
    private ProgressBar hrImgPg3, empImgPg5;


    //Declaration Layout Views
    private LinearLayout declarationFormLayout, decInstructionLayout;
    private TextView dInsuranceNoTv, dNameTv, dFatherHusbandNameTv, dMarriedTv, dDayTv, dMonthTv, dYearTv, dSexTv, dPresentAddressTv,
            dPermanentAddressTv, dBranchOfficeTv, dDispensaryTv, dAppointmentDayTv, dAppointmentMonthTv, dAppointmentYearTv, dAppointmentDateTv, declarationFromTv1, declarationFromTv2;

    //first nominee table
    private LinearLayout fatherNameRow2, motherNameRow2, spouseNameRow2, guardianNameRow2, siblingNameRow2, childOneNameRow2, childTwoNameRow2;
    private View fatherNameRowView2, motherNameRowView2, spouseNameRowView2, guardianNameRowView2, siblingNameRowView2, childOneNameRowView2, childTwoNameRowView2;
    private TextView nFatherNameTv2, nFatherRelationTv2, nFatherAddressTv2,
            nMotherNameTv2, nMotherRelationTv2, nMotherAddressTv2,
            nWifeNameTv2, nWifeRelationTv2, nWifeAddressTv2,
            nGuardianNameTv2, nGuardianRelationTv2, nGuardianAddressTv2,
            nSiblingNameTv2, nSiblingRelationTv2, nSiblingAddressTv2,
            nChildOneNameTv2, nChildOneRelationTv2, nChildOneAddressTv2,
            nChildTwoNameTv2, nChildTwoRelationTv2, nChildTwoAddressTv2;

    //second nominee table
    private LinearLayout fatherNameRow3, motherNameRow3, spouseNameRow3, guardianNameRow3, siblingNameRow3, childOneNameRow3, childTwoNameRow3;
    private View fatherNameRowView3, motherNameRowView3, spouseNameRowView3, guardianNameRowView3, siblingNameRowView3, childOneNameRowView3, childTwoNameRowView3;
    private TextView nFatherNameTv3, nFatherRelationTv3, nFatherDobTv3,
            nMotherNameTv3, nMotherRelationTv3, nMotherDobTv3,
            nWifeNameTv3, nWifeRelationTv3, nWifeDobTv3,
            nGuardianNameTv3, nGuardianRelationTv3, nGuardianDobTv3,
            nSiblingNameTv3, nSiblingRelationTv3, nSiblingDobTv3,
            nChildOneNameTv3, nChildOneRelationTv3, nChildOneDobTv3,
            nChildTwoNameTv3, nChildTwoRelationTv3, nChildTwoDobTv3;

    //third nominee table
    private LinearLayout fatherNameRow4, motherNameRow4, spouseNameRow4, guardianNameRow4, siblingNameRow4, childOneNameRow4, childTwoNameRow4;
    private View fatherNameRowView4, motherNameRowView4, spouseNameRowView4, guardianNameRowView4, siblingNameRowView4, childOneNameRowView4, childTwoNameRowView4;
    private TextView nFatherNameTv4, nFatherRelationTv4, nFatherDobTv4,
            nMotherNameTv4, nMotherRelationTv4, nMotherDobTv4,
            nWifeNameTv4, nWifeRelationTv4, nWifeDobTv4,
            nGuardianNameTv4, nGuardianRelationTv4, nGuardianDobTv4,
            nSiblingNameTv4, nSiblingRelationTv4, nSiblingDobTv4,
            nChildOneNameTv4, nChildOneRelationTv4, nChildOneDobTv4,
            nChildTwoNameTv4, nChildTwoRelationTv4, nChildTwoDobTv4;


    private ImageView hrSignImgView4, empSignImgView6, empSignImgView7;
    private ProgressBar hrImgPg4, empImgPg6, empImgPg7;


    //FTA Layout views
    private LinearLayout ftaLayout, fta2Layout, fta3Layout, fta4Layout, fta5Layout;
    private TextView ftaDateTv, ftaTextTv, ftaPoint1Tv, ftaPoint2Tv, hrSignNameTv, hrSignName1Tv, tAndCTextTv1, tAndCTextTv4, tAndCTextTv9,
            tAndCTextTv15, tAndCTextTv17, tAndCTextTv18, tAndCTextTv19, tAndCTextTv20A,
            ftaHerebyNameTv, ftaHerebySignatureTv, ftaHerebyAddressTv, ftaHerebyDateTv, ftaHerebyPlaceTv, ftaCompanyNameTv, ftaCompanyNameTv2;
    private ImageView hrSignImgView5, hrSignImgView6, empSignImgView9, empSignImgView10;
    private ProgressBar hrImgPg5, hrImgPg6, empImgPg9, empImgPg10;
    private TextView empNameTv;

    //Image Layout Views
    private LinearLayout passportImgLayout, empSignImgLayout, acImgLayout, acBImgLayout, pcImgLayout, bpImgLayout, lastCmpExpImgLayout, paySlipImgLayout, paySlip2ImgLayout,
            paySlip3ImgLayout, resignMailImgLayout, bankStmtImgLayout, offerLetterImgLayout;
    private ImageView passportIv, empSignIv, acIv, acBIv, pcIv, bpIv, lceIv, paySlipIv, paySlip2Iv, paySlip3Iv, resignMailIv, bankStmtIv, offerLetterIv;
    private ProgressBar passportPg, empSignPg, acPg, acBPg, pcPg, bpPg, lcePg, paySlipPg, paySlip2Pg, paySlip3Pg, resignMailPg, bankStmtPg, offerLetterPg;

    //Vaccine Certificate Views
    private LinearLayout vaccineCertificateImgLayout;
    private ImageView vaccineCertificateIv;
    private ProgressBar vaccineCertificatePg;

    //qualification image views
    private LinearLayout sImgLayout, ssImgLayout, gImgLayout, pgImgLayout, otherImgLayout;
    private ImageView sIv, ssIv, gIv, pgIv, otherIv;
    private ProgressBar sPg, ssPg, gPg, pgPg, otherPg;

    //Terms and condition layout
    private LinearLayout selfDeclarationLayout;
    private ImageView empSignImgView8;
    private ProgressBar empImgPg8;
    private TextView signNameTv, signNameTv2;

    //Loading Dialog
    private LoadingDialog loadingDialog;

    //PDF create variables
    private Bitmap joiningBitmap, pfBitmap, formGBitmap, formG2Bitmap, privacyBitmap, privacy2Bitmap, nonDisclosureBitmap, nonDisclosure2Bitmap,
            nonDisclosure3Bitmap, nonDisclosure4Bitmap, nonDisclosure5Bitmap, nonDisclosure6Bitmap, nonDisclosure7Bitmap, nonDisclosure8Bitmap,
            declarationBitmap, decInstructionBitmap, ftaBitmap, fta2Bitmap, fta3Bitmap, fta4Bitmap, fta5Bitmap, passportBitmap, empSignBitmap, acBitmap,
            pcBitmap, bpBitmap, lastCmpExpBitmap, paySlipBitmap, resignMailBitmap, bankStmtBitmap, vaccineCertificateBitmap, sBitmap, ssBitmap, gBitmap, pgBitmap, otherBitmap, selfDeclarationBitmap;

    //permissions
    private String[] storagePermissions;
    private String[] storagePermissions13;
    private static final int STORAGE_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        initialization();

        //handle back button click
        backBtn.setOnClickListener(view -> onBackPressed());

        //get User Data
        String userid = getIntent().getStringExtra("userid");
        getUserPreviewData(userid);

        //handle download btn click
        downloadBtn.setOnClickListener(view -> {
            if (!checkStoragePermission()) {
                requestStoragePermission();
            } else {
                //start creating bitmap
                loadingDialog.showDialog("Please wait pdf is creating...");
                new BitmapCreate().execute();

            }
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            //load preview data
            getUserPreviewData(userid);
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    private class BitmapCreate extends AsyncTask<String, Void, Boolean> {

        public BitmapCreate() {

        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                Log.d("size", "" + mainLayout.getWidth() + " " + mainLayout.getWidth());
                joiningBitmap = LoadBitmap(joiningLayout, joiningLayout.getWidth(), joiningLayout.getHeight());
                pfBitmap = LoadBitmap(pfLayout, pfLayout.getWidth(), pfLayout.getHeight());
                formGBitmap = LoadBitmap(formGLayout, formGLayout.getWidth(), formGLayout.getHeight());
                formG2Bitmap = LoadBitmap(formG2Layout, formG2Layout.getWidth(), formG2Layout.getHeight());
                privacyBitmap = LoadBitmap(privacyTermsLayout, privacyTermsLayout.getWidth(), privacyTermsLayout.getHeight());
                privacy2Bitmap = LoadBitmap(privacyTerms2Layout, privacyTerms2Layout.getWidth(), privacyTerms2Layout.getHeight());
                nonDisclosureBitmap = LoadBitmap(nonDisclosureLayout, nonDisclosureLayout.getWidth(), nonDisclosureLayout.getHeight());
                nonDisclosure2Bitmap = LoadBitmap(nonDisclosure2Layout, nonDisclosure2Layout.getWidth(), nonDisclosure2Layout.getHeight());
                nonDisclosure3Bitmap = LoadBitmap(nonDisclosure3Layout, nonDisclosure3Layout.getWidth(), nonDisclosure3Layout.getHeight());
                nonDisclosure4Bitmap = LoadBitmap(nonDisclosure4Layout, nonDisclosure4Layout.getWidth(), nonDisclosure4Layout.getHeight());
                nonDisclosure5Bitmap = LoadBitmap(nonDisclosure5Layout, nonDisclosure5Layout.getWidth(), nonDisclosure5Layout.getHeight());
                nonDisclosure6Bitmap = LoadBitmap(nonDisclosure6Layout, nonDisclosure6Layout.getWidth(), nonDisclosure6Layout.getHeight());
                nonDisclosure7Bitmap = LoadBitmap(nonDisclosure7Layout, nonDisclosure7Layout.getWidth(), nonDisclosure7Layout.getHeight());
                nonDisclosure8Bitmap = LoadBitmap(nonDisclosure8Layout, nonDisclosure8Layout.getWidth(), nonDisclosure8Layout.getHeight());

                if (declarationFormLayout.getVisibility() == View.VISIBLE) {
                    declarationBitmap = LoadBitmap(declarationFormLayout, declarationFormLayout.getWidth(), declarationFormLayout.getHeight());
                    decInstructionBitmap = LoadBitmap(decInstructionLayout, decInstructionLayout.getWidth(), decInstructionLayout.getHeight());
                }
                if (ftaLayout.getVisibility() == View.VISIBLE) {
                    ftaBitmap = LoadBitmap(ftaLayout, ftaLayout.getWidth(), ftaLayout.getHeight());
                    fta2Bitmap = LoadBitmap(fta2Layout, fta2Layout.getWidth(), fta2Layout.getHeight());
                    fta3Bitmap = LoadBitmap(fta3Layout, fta3Layout.getWidth(), fta3Layout.getHeight());
                    fta4Bitmap = LoadBitmap(fta4Layout, fta4Layout.getWidth(), fta4Layout.getHeight());
                    fta5Bitmap = LoadBitmap(fta5Layout, fta5Layout.getWidth(), fta5Layout.getHeight());
                }

                passportBitmap = LoadBitmap(passportImgLayout, passportImgLayout.getWidth(), passportImgLayout.getHeight());
                empSignBitmap = LoadBitmap(empSignImgLayout, empSignImgLayout.getWidth(), empSignImgLayout.getHeight());
                acBitmap = LoadBitmap(acImgLayout, acImgLayout.getWidth(), acImgLayout.getHeight());
                pcBitmap = LoadBitmap(pcImgLayout, pcImgLayout.getWidth(), pcImgLayout.getHeight());
                bpBitmap = LoadBitmap(bpImgLayout, bpImgLayout.getWidth(), bpImgLayout.getHeight());
                if (lastCmpExpImgLayout.getVisibility() == View.VISIBLE) {
                    lastCmpExpBitmap = LoadBitmap(lastCmpExpImgLayout, lastCmpExpImgLayout.getWidth(), lastCmpExpImgLayout.getHeight());
                }
                if (paySlipImgLayout.getVisibility() == View.VISIBLE) {
                    paySlipBitmap = LoadBitmap(paySlipImgLayout, paySlipImgLayout.getWidth(), paySlipImgLayout.getHeight());
                }
                if (resignMailImgLayout.getVisibility() == View.VISIBLE) {
                    resignMailBitmap = LoadBitmap(resignMailImgLayout, resignMailImgLayout.getWidth(), resignMailImgLayout.getHeight());
                }
                if (bankStmtImgLayout.getVisibility() == View.VISIBLE) {
                    bankStmtBitmap = LoadBitmap(bankStmtImgLayout, bankStmtImgLayout.getWidth(), bankStmtImgLayout.getHeight());
                }

                vaccineCertificateBitmap = LoadBitmap(vaccineCertificateImgLayout, vaccineCertificateImgLayout.getWidth(), vaccineCertificateImgLayout.getHeight());
                if (sImgLayout.getVisibility() == View.VISIBLE) {
                    sBitmap = LoadBitmap(sImgLayout, sImgLayout.getWidth(), sImgLayout.getHeight());
                }
                if (ssImgLayout.getVisibility() == View.VISIBLE) {
                    ssBitmap = LoadBitmap(ssImgLayout, ssImgLayout.getWidth(), ssImgLayout.getHeight());
                }
                if (gImgLayout.getVisibility() == View.VISIBLE) {
                    gBitmap = LoadBitmap(gImgLayout, gImgLayout.getWidth(), gImgLayout.getHeight());
                }
                if (pgImgLayout.getVisibility() == View.VISIBLE) {
                    pgBitmap = LoadBitmap(pgImgLayout, pgImgLayout.getWidth(), pgImgLayout.getHeight());
                }
                if (otherImgLayout.getVisibility() == View.VISIBLE) {
                    otherBitmap = LoadBitmap(otherImgLayout, otherImgLayout.getWidth(), otherImgLayout.getHeight());
                }

                selfDeclarationBitmap = LoadBitmap(selfDeclarationLayout, selfDeclarationLayout.getWidth(), selfDeclarationLayout.getHeight());

                createPdf();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
    }

    private boolean checkStoragePermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            return ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_MEDIA_IMAGES) == (PackageManager.PERMISSION_GRANTED);
        }else{
            return ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        }
    }

    private void requestStoragePermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            ActivityCompat.requestPermissions(this, storagePermissions13, STORAGE_REQUEST_CODE);
        }else{
            ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case STORAGE_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (storageAccepted) {
                        loadingDialog.showDialog("Please wait pdf is creating...");
                        new BitmapCreate().execute();
                    } else {
                        showToast("Permissions is required");
                    }
                }
            }
            break;
        }
    }

    private Bitmap LoadBitmap(View v, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        return bitmap;
    }

    private void createPdf() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        float height = displaymetrics.heightPixels;
        float width = displaymetrics.widthPixels;

        int convertHeight = (int) height, convertWidth = (int) width;

        //create new document
        PdfDocument document = new PdfDocument();

        //create page description
        PdfDocument.PageInfo pageInfo;
        pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHeight, 1).create();

        //start a page
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        canvas.drawPaint(paint);
        joiningBitmap = Bitmap.createScaledBitmap(joiningBitmap, convertWidth, convertHeight, true);
        canvas.drawBitmap(joiningBitmap, 0, 0, null);
        document.finishPage(page);

        //create page 2
        pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHeight, 2).create();
        page = document.startPage(pageInfo);
        canvas = page.getCanvas();
        paint = new Paint();
        canvas.drawPaint(paint);
        pfBitmap = Bitmap.createScaledBitmap(pfBitmap, convertWidth, convertHeight, true);
        canvas.drawBitmap(pfBitmap, 0, 0, null);
        document.finishPage(page);

        //create page 3
        pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHeight, 3).create();
        page = document.startPage(pageInfo);
        canvas = page.getCanvas();
        paint = new Paint();
        canvas.drawPaint(paint);
        formGBitmap = Bitmap.createScaledBitmap(formGBitmap, convertWidth, convertHeight, true);
        canvas.drawBitmap(formGBitmap, 0, 0, null);
        document.finishPage(page);

        //create page 4
        pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHeight, 4).create();
        page = document.startPage(pageInfo);
        canvas = page.getCanvas();
        paint = new Paint();
        canvas.drawPaint(paint);
        formG2Bitmap = Bitmap.createScaledBitmap(formG2Bitmap, convertWidth, convertHeight, true);
        canvas.drawBitmap(formG2Bitmap, 0, 0, null);
        document.finishPage(page);

        //create page 5
        pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHeight, 5).create();
        page = document.startPage(pageInfo);
        canvas = page.getCanvas();
        paint = new Paint();
        canvas.drawPaint(paint);
        privacyBitmap = Bitmap.createScaledBitmap(privacyBitmap, convertWidth, convertHeight, true);
        canvas.drawBitmap(privacyBitmap, 0, 0, null);
        document.finishPage(page);

        //create page 6
        pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHeight, 6).create();
        page = document.startPage(pageInfo);
        canvas = page.getCanvas();
        paint = new Paint();
        canvas.drawPaint(paint);
        privacy2Bitmap = Bitmap.createScaledBitmap(privacy2Bitmap, convertWidth, convertHeight, true);
        canvas.drawBitmap(privacy2Bitmap, 0, 0, null);
        document.finishPage(page);

        //create page 7
        pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHeight, 7).create();
        page = document.startPage(pageInfo);
        canvas = page.getCanvas();
        paint = new Paint();
        canvas.drawPaint(paint);
        nonDisclosureBitmap = Bitmap.createScaledBitmap(nonDisclosureBitmap, convertWidth, convertHeight, true);
        canvas.drawBitmap(nonDisclosureBitmap, 0, 0, null);
        document.finishPage(page);

        //create page 8
        pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHeight, 8).create();
        page = document.startPage(pageInfo);
        canvas = page.getCanvas();
        paint = new Paint();
        canvas.drawPaint(paint);
        nonDisclosure2Bitmap = Bitmap.createScaledBitmap(nonDisclosure2Bitmap, convertWidth, convertHeight, true);
        canvas.drawBitmap(nonDisclosure2Bitmap, 0, 0, null);
        document.finishPage(page);

        //create page 9
        pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHeight, 9).create();
        page = document.startPage(pageInfo);
        canvas = page.getCanvas();
        paint = new Paint();
        canvas.drawPaint(paint);
        nonDisclosure3Bitmap = Bitmap.createScaledBitmap(nonDisclosure3Bitmap, convertWidth, convertHeight, true);
        canvas.drawBitmap(nonDisclosure3Bitmap, 0, 0, null);
        document.finishPage(page);

        //create page 10
        pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHeight, 10).create();
        page = document.startPage(pageInfo);
        canvas = page.getCanvas();
        paint = new Paint();
        canvas.drawPaint(paint);
        nonDisclosure4Bitmap = Bitmap.createScaledBitmap(nonDisclosure4Bitmap, convertWidth, convertHeight, true);
        canvas.drawBitmap(nonDisclosure4Bitmap, 0, 0, null);
        document.finishPage(page);

        //create page 11
        pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHeight, 10).create();
        page = document.startPage(pageInfo);
        canvas = page.getCanvas();
        paint = new Paint();
        canvas.drawPaint(paint);
        nonDisclosure5Bitmap = Bitmap.createScaledBitmap(nonDisclosure5Bitmap, convertWidth, convertHeight, true);
        canvas.drawBitmap(nonDisclosure5Bitmap, 0, 0, null);
        document.finishPage(page);

        //create page 12
        pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHeight, 12).create();
        page = document.startPage(pageInfo);
        canvas = page.getCanvas();
        paint = new Paint();
        canvas.drawPaint(paint);
        nonDisclosure6Bitmap = Bitmap.createScaledBitmap(nonDisclosure6Bitmap, convertWidth, convertHeight, true);
        canvas.drawBitmap(nonDisclosure6Bitmap, 0, 0, null);
        document.finishPage(page);

        //create page 13
        pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHeight, 13).create();
        page = document.startPage(pageInfo);
        canvas = page.getCanvas();
        paint = new Paint();
        canvas.drawPaint(paint);
        nonDisclosure7Bitmap = Bitmap.createScaledBitmap(nonDisclosure7Bitmap, convertWidth, convertHeight, true);
        canvas.drawBitmap(nonDisclosure7Bitmap, 0, 0, null);
        document.finishPage(page);

        //create page 14
        pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHeight, 14).create();
        page = document.startPage(pageInfo);
        canvas = page.getCanvas();
        paint = new Paint();
        canvas.drawPaint(paint);
        nonDisclosure8Bitmap = Bitmap.createScaledBitmap(nonDisclosure8Bitmap, convertWidth, convertHeight, true);
        canvas.drawBitmap(nonDisclosure8Bitmap, 0, 0, null);
        document.finishPage(page);

        if (declarationFormLayout.getVisibility() == View.VISIBLE) {
            //create page 15
            pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHeight, 15).create();
            page = document.startPage(pageInfo);
            canvas = page.getCanvas();
            paint = new Paint();
            canvas.drawPaint(paint);
            declarationBitmap = Bitmap.createScaledBitmap(declarationBitmap, convertWidth, convertHeight, true);
            canvas.drawBitmap(declarationBitmap, 0, 0, null);
            document.finishPage(page);

            //create page 16
            pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHeight, 16).create();
            page = document.startPage(pageInfo);
            canvas = page.getCanvas();
            paint = new Paint();
            canvas.drawPaint(paint);
            decInstructionBitmap = Bitmap.createScaledBitmap(decInstructionBitmap, convertWidth, convertHeight, true);
            canvas.drawBitmap(decInstructionBitmap, 0, 0, null);
            document.finishPage(page);
        }

        if (ftaLayout.getVisibility() == View.VISIBLE) {
            //create page 17
            pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHeight, 17).create();
            page = document.startPage(pageInfo);
            canvas = page.getCanvas();
            paint = new Paint();
            canvas.drawPaint(paint);
            ftaBitmap = Bitmap.createScaledBitmap(ftaBitmap, convertWidth, convertHeight, true);
            canvas.drawBitmap(ftaBitmap, 0, 0, null);
            document.finishPage(page);

            //create page 18
            pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHeight, 18).create();
            page = document.startPage(pageInfo);
            canvas = page.getCanvas();
            paint = new Paint();
            canvas.drawPaint(paint);
            fta2Bitmap = Bitmap.createScaledBitmap(fta2Bitmap, convertWidth, convertHeight, true);
            canvas.drawBitmap(fta2Bitmap, 0, 0, null);
            document.finishPage(page);

            //create page 19
            pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHeight, 19).create();
            page = document.startPage(pageInfo);
            canvas = page.getCanvas();
            paint = new Paint();
            canvas.drawPaint(paint);
            fta3Bitmap = Bitmap.createScaledBitmap(fta3Bitmap, convertWidth, convertHeight, true);
            canvas.drawBitmap(fta3Bitmap, 0, 0, null);
            document.finishPage(page);

            //create page 20
            pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHeight, 20).create();
            page = document.startPage(pageInfo);
            canvas = page.getCanvas();
            paint = new Paint();
            canvas.drawPaint(paint);
            fta4Bitmap = Bitmap.createScaledBitmap(fta4Bitmap, convertWidth, convertHeight, true);
            canvas.drawBitmap(fta4Bitmap, 0, 0, null);
            document.finishPage(page);

            //create page 21
            pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHeight, 21).create();
            page = document.startPage(pageInfo);
            canvas = page.getCanvas();
            paint = new Paint();
            canvas.drawPaint(paint);
            fta5Bitmap = Bitmap.createScaledBitmap(fta5Bitmap, convertWidth, convertHeight, true);
            canvas.drawBitmap(fta5Bitmap, 0, 0, null);
            document.finishPage(page);

        }

        //create page 22
        pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHeight, 22).create();
        page = document.startPage(pageInfo);
        canvas = page.getCanvas();
        paint = new Paint();
        //canvas.drawPaint(paint);
        passportBitmap = Bitmap.createScaledBitmap(passportBitmap, convertWidth, passportImgLayout.getHeight(), true);
        canvas.drawBitmap(passportBitmap, 0, 0, null);
        document.finishPage(page);

        //create page 23
        pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHeight, 23).create();
        page = document.startPage(pageInfo);
        canvas = page.getCanvas();
        paint = new Paint();
        //canvas.drawPaint(paint);
        empSignBitmap = Bitmap.createScaledBitmap(empSignBitmap, convertWidth, empSignImgLayout.getHeight(), true);
        canvas.drawBitmap(empSignBitmap, 0, 0, null);
        document.finishPage(page);

        //create page 24
        pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHeight, 24).create();
        page = document.startPage(pageInfo);
        canvas = page.getCanvas();
        paint = new Paint();
        //canvas.drawPaint(paint);
        acBitmap = Bitmap.createScaledBitmap(acBitmap, convertWidth, acImgLayout.getHeight(), true);
        canvas.drawBitmap(acBitmap, 0, 0, null);
        document.finishPage(page);

        //create page 25
        pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHeight, 25).create();
        page = document.startPage(pageInfo);
        canvas = page.getCanvas();
        paint = new Paint();
        //canvas.drawPaint(paint);
        pcBitmap = Bitmap.createScaledBitmap(pcBitmap, convertWidth, pcImgLayout.getHeight(), true);
        canvas.drawBitmap(pcBitmap, 0, 0, null);
        document.finishPage(page);

        //create page 26
        pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHeight, 26).create();
        page = document.startPage(pageInfo);
        canvas = page.getCanvas();
        paint = new Paint();
        //canvas.drawPaint(paint);
        bpBitmap = Bitmap.createScaledBitmap(bpBitmap, convertWidth, bpImgLayout.getHeight(), true);
        canvas.drawBitmap(bpBitmap, 0, 0, null);
        document.finishPage(page);

        if (lastCmpExpImgLayout.getVisibility() == View.VISIBLE) {
            //create page 27
            pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHeight, 27).create();
            page = document.startPage(pageInfo);
            canvas = page.getCanvas();
            paint = new Paint();
            //canvas.drawPaint(paint);
            lastCmpExpBitmap = Bitmap.createScaledBitmap(lastCmpExpBitmap, convertWidth, lastCmpExpImgLayout.getHeight(), true);
            canvas.drawBitmap(lastCmpExpBitmap, 0, 0, null);
            document.finishPage(page);
        }
        if (paySlipImgLayout.getVisibility() == View.VISIBLE) {
            //create page 28
            pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHeight, 28).create();
            page = document.startPage(pageInfo);
            canvas = page.getCanvas();
            paint = new Paint();
            //canvas.drawPaint(paint);
            paySlipBitmap = Bitmap.createScaledBitmap(paySlipBitmap, convertWidth, paySlipImgLayout.getHeight(), true);
            canvas.drawBitmap(paySlipBitmap, 0, 0, null);
            document.finishPage(page);
        }
        if (resignMailImgLayout.getVisibility() == View.VISIBLE) {
            //create page 29
            pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHeight, 29).create();
            page = document.startPage(pageInfo);
            canvas = page.getCanvas();
            paint = new Paint();
            //canvas.drawPaint(paint);
            resignMailBitmap = Bitmap.createScaledBitmap(resignMailBitmap, convertWidth, resignMailImgLayout.getHeight(), true);
            canvas.drawBitmap(resignMailBitmap, 0, 0, null);
            document.finishPage(page);
        }
        if (bankStmtImgLayout.getVisibility() == View.VISIBLE) {
            //create page 30
            pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHeight, 30).create();
            page = document.startPage(pageInfo);
            canvas = page.getCanvas();
            paint = new Paint();
            //canvas.drawPaint(paint);
            bankStmtBitmap = Bitmap.createScaledBitmap(bankStmtBitmap, convertWidth, bankStmtImgLayout.getHeight(), true);
            canvas.drawBitmap(bankStmtBitmap, 0, 0, null);
            document.finishPage(page);
        }

        //create page 31
        pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHeight, 31).create();
        page = document.startPage(pageInfo);
        canvas = page.getCanvas();
        paint = new Paint();
        //canvas.drawPaint(paint);
        vaccineCertificateBitmap = Bitmap.createScaledBitmap(vaccineCertificateBitmap, convertWidth, vaccineCertificateImgLayout.getHeight(), true);
        canvas.drawBitmap(vaccineCertificateBitmap, 0, 0, null);
        document.finishPage(page);


        if (sImgLayout.getVisibility() == View.VISIBLE) {

            //create page 32
            pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHeight, 32).create();
            page = document.startPage(pageInfo);
            canvas = page.getCanvas();
            paint = new Paint();
            //canvas.drawPaint(paint);
            sBitmap = Bitmap.createScaledBitmap(sBitmap, convertWidth, sImgLayout.getHeight(), true);
            canvas.drawBitmap(sBitmap, 0, 0, null);
            document.finishPage(page);
        }
        if (ssImgLayout.getVisibility() == View.VISIBLE) {
            //create page 33
            pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHeight, 33).create();
            page = document.startPage(pageInfo);
            canvas = page.getCanvas();
            paint = new Paint();
            //canvas.drawPaint(paint);
            ssBitmap = Bitmap.createScaledBitmap(ssBitmap, convertWidth, ssImgLayout.getHeight(), true);
            canvas.drawBitmap(ssBitmap, 0, 0, null);
            document.finishPage(page);
        }
        if (gImgLayout.getVisibility() == View.VISIBLE) {
            //create page 33
            pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHeight, 33).create();
            page = document.startPage(pageInfo);
            canvas = page.getCanvas();
            paint = new Paint();
            //canvas.drawPaint(paint);
            gBitmap = Bitmap.createScaledBitmap(gBitmap, convertWidth, gImgLayout.getHeight(), true);
            canvas.drawBitmap(gBitmap, 0, 0, null);
            document.finishPage(page);
        }
        if (pgImgLayout.getVisibility() == View.VISIBLE) {

            //create page 34
            pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHeight, 34).create();
            page = document.startPage(pageInfo);
            canvas = page.getCanvas();
            paint = new Paint();
            //canvas.drawPaint(paint);
            pgBitmap = Bitmap.createScaledBitmap(pgBitmap, convertWidth, pgImgLayout.getHeight(), true);
            canvas.drawBitmap(pgBitmap, 0, 0, null);
            document.finishPage(page);
        }
        if (otherImgLayout.getVisibility() == View.VISIBLE) {

            //create page 35
            pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHeight, 35).create();
            page = document.startPage(pageInfo);
            canvas = page.getCanvas();
            paint = new Paint();
            //canvas.drawPaint(paint);
            otherBitmap = Bitmap.createScaledBitmap(otherBitmap, convertWidth, otherImgLayout.getHeight(), true);
            canvas.drawBitmap(otherBitmap, 0, 0, null);
            document.finishPage(page);
        }

        //create page 36
        pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHeight, 36).create();
        page = document.startPage(pageInfo);
        canvas = page.getCanvas();
        paint = new Paint();
        //canvas.drawPaint(paint);
        selfDeclarationBitmap = Bitmap.createScaledBitmap(selfDeclarationBitmap, convertWidth, selfDeclarationLayout.getHeight(), true);
        canvas.drawBitmap(selfDeclarationBitmap, 0, 0, null);
        document.finishPage(page);

        // write the document content
        String directory_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/ONBOARDING/";
        /*String directory_path;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // only for gingerbread and newer versions
            directory_path = getApplicationContext().getExternalFilesDir("") + "/ONBOARDING/";
        } else {
            directory_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ONBOARDING/";
        }*/
        File file = new File(directory_path);

        if (!file.exists()) {
            file.mkdirs();
        }
        String targetPdf = directory_path + System.currentTimeMillis() + ".pdf";
        File filePath;
        filePath = new File(targetPdf);

        try {
            document.writeTo(new FileOutputStream(filePath));

            // close the document
            document.close();
            loadingDialog.hideDialog();

            PreviewActivity.this.runOnUiThread(() -> Toast.makeText(PreviewActivity.this, "PDF created successfully at\n" + directory_path, Toast.LENGTH_SHORT).show());

        } catch (Exception e) {
            e.printStackTrace();
            PreviewActivity.this.runOnUiThread(() -> Toast.makeText(PreviewActivity.this, "Something wrong: " + e, Toast.LENGTH_SHORT).show());

        }
    }

    private void openPdf(String targetPdf) {
        File file = new File(targetPdf);
        if (file.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.fromFile(file);
            intent.setDataAndType(uri, "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, "No Application for pdf view", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getUserPreviewData(String userid) {
        loadingDialog.showDialog("Loading...");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.GET_USER_PREVIEW_DATA_URL, response -> {
            loadingDialog.hideDialog();
            Log.d("USER RESPONSE", response);
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);

                    PreviewModel previewModel = new PreviewModel();
                    previewModel.setId(object.getString("id"));
                    previewModel.setUserid(object.getString("userid"));
                    previewModel.setCandidate_category(object.getString("candidate_category"));
                    previewModel.setEmployee_level(object.getString("employee_level"));
                    previewModel.setSalary(object.getString("salary"));
                    previewModel.setName(object.getString("name"));
                    previewModel.setFather_name(object.getString("father_name"));
                    previewModel.setJoining_date(object.getString("joining_date"));
                    previewModel.setDob(object.getString("dob"));
                    previewModel.setGender(object.getString("gender"));
                    previewModel.setBlood_group(object.getString("blood_group"));
                    previewModel.setMarital_status(object.getString("marital_status"));
                    previewModel.setDepartment(object.getString("department"));
                    previewModel.setDesignation(object.getString("designation"));
                    previewModel.setGrade(object.getString("grade"));
                    previewModel.setBranch(object.getString("branch"));
                    previewModel.setZone(object.getString("zone"));
                    previewModel.setCompany_name(object.getString("company_name"));
                    previewModel.setCompany_address(object.getString("company_address"));
                    previewModel.setCompany_email(object.getString("company_email"));
                    previewModel.setEmail(object.getString("email"));
                    previewModel.setNumber(object.getString("number"));
                    previewModel.setPin_code(object.getString("pin_code"));
                    previewModel.setPermanent_address(object.getString("permanent_address"));
                    previewModel.setCurrent_address(object.getString("current_address"));
                    previewModel.setBank_account_no(object.getString("bank_account_no"));
                    previewModel.setBank_ifsc(object.getString("bank_ifsc"));
                    previewModel.setUan_no(object.getString("uan_no"));
                    previewModel.setEsic_no(object.getString("esic_no"));
                    previewModel.setAadhar_no(object.getString("aadhar_no"));
                    previewModel.setPan_no(object.getString("pan_no"));
                    previewModel.setPerson_name(object.getString("person_name"));
                    previewModel.setPerson_number(object.getString("person_number"));
                    previewModel.setPerson_relation(object.getString("person_relation"));
                    previewModel.setFather_n_name(object.getString("father_n_name"));
                    previewModel.setFather_relation(object.getString("father_relation"));
                    previewModel.setFather_dob(object.getString("father_dob"));
                    previewModel.setFather_amount(object.getString("father_amount"));
                    previewModel.setMother_n_name(object.getString("mother_n_name"));
                    previewModel.setMother_relation(object.getString("mother_relation"));
                    previewModel.setMother_dob(object.getString("mother_dob"));
                    previewModel.setMother_amount(object.getString("mother_amount"));
                    previewModel.setWife_n_name(object.getString("wife_n_name"));
                    previewModel.setWife_relation(object.getString("wife_relation"));
                    previewModel.setWife_dob(object.getString("wife_dob"));
                    previewModel.setWife_amount(object.getString("wife_amount"));
                    previewModel.setGuardian_n_name(object.getString("guardian_n_name"));
                    previewModel.setGuardian_relation(object.getString("guardian_relation"));
                    previewModel.setGuardian_dob(object.getString("guardian_dob"));
                    previewModel.setGuardian_amount(object.getString("guardian_amount"));
                    previewModel.setSibling_n_name(object.getString("sibling_n_name"));
                    previewModel.setSibling_relation(object.getString("sibling_relation"));
                    previewModel.setSibling_dob(object.getString("sibling_dob"));
                    previewModel.setSibling_amount(object.getString("sibling_amount"));
                    previewModel.setChild_one_name(object.getString("child_one_name"));
                    previewModel.setChild_one_relation(object.getString("child_one_relation"));
                    previewModel.setChild_one_dob(object.getString("child_one_dob"));
                    previewModel.setChild_one_amount(object.getString("child_one_amount"));
                    previewModel.setChild_two_name(object.getString("child_two_name"));
                    previewModel.setChild_two_relation(object.getString("child_two_relation"));
                    previewModel.setChild_two_dob(object.getString("child_two_dob"));
                    previewModel.setChild_two_amount(object.getString("child_two_amount"));
                    previewModel.setLastCompanyName(object.getString("last_company_name"));
                    previewModel.setLastCompanyJob(object.getString("last_company_job"));
                    previewModel.setLastCompanyDoj(object.getString("last_company_doj"));
                    previewModel.setLastCompanyDol(object.getString("last_company_dol"));
                    previewModel.setPassport_size_image(object.getString("passport_size_image"));
                    previewModel.setEmployee_sign_image(object.getString("employee_sign_image"));
                    previewModel.setAadhar_card_image(object.getString("aadhar_card_image"));
                    previewModel.setAadhar_card_back_image(object.getString("aadhar_card_back_image"));
                    previewModel.setPan_card_image(object.getString("pan_card_image"));
                    previewModel.setBank_proof_image(object.getString("bank_proof_image"));
                    previewModel.setLast_company_exp_letter_image(object.getString("last_company_exp_letter_image"));
                    previewModel.setPay_slip_exp_letter_image(object.getString("pay_slip_exp_letter_image"));
                    previewModel.setPay_slip_second_last_month_image(object.getString("pay_slip_second_last_month_image"));
                    previewModel.setPay_slip_third_last_month_image(object.getString("pay_slip_third_last_month_image"));
                    previewModel.setResign_mail_image(object.getString("resign_mail_image"));
                    previewModel.setBank_stmt_last_3_mth_image(object.getString("bank_stmt_last_3_mth_image"));
                    previewModel.setOffer_letter(object.getString("offer_letter_image"));
                    previewModel.setVaccine_certificate_image(object.getString("vaccine_certificate_image"));
                    previewModel.setRequest(object.getString("request"));
                    previewModel.setHo_request(object.getString("ho_request"));
                    previewModel.setEmployee_id(object.getString("employee_id"));
                    previewModel.setV_work_code(object.getString("v_work_code"));
                    previewModel.setHr_sign_image(object.getString("hr_sign_image"));
                    previewModel.setHead_hr_sign_image(object.getString("head_hr_sign_image"));
                    previewModel.setHead_hr_sign_name(object.getString("head_hr_sign_name"));
                    previewModel.setImage_10(object.getString("image_10"));
                    previewModel.setImage_12(object.getString("image_12"));
                    previewModel.setImage_g(object.getString("image_g"));
                    previewModel.setImage_pg(object.getString("image_pg"));
                    previewModel.setImage_other(object.getString("image_other"));


                    //set values to fields
                    setValuesToFields(previewModel);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, error -> {
            loadingDialog.hideDialog();
            Log.e("USER ERROR", error.toString());

        }) {
            @NonNull
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("userid", userid);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 30000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 30000;
            }

            @Override
            public void retry(VolleyError error) {
                Log.e("RETRY ERROR", error.toString());
                showToast(error.toString());
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void setValuesToFields(PreviewModel previewModel) throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String todayDate = previewModel.getJoining_date();

        //set company name
        companyTv.setText(Html.fromHtml(String.format("<u>%s</u>", previewModel.getCompany_name())));

        //HR Sign Image
        if (previewModel.getHr_sign_image() != null && !previewModel.getHr_sign_image().equalsIgnoreCase("")) {
            hrImgPg.setVisibility(View.VISIBLE);
            Picasso.get().load(previewModel.getHr_sign_image()).fit().into(hrSignImgView, new Callback() {
                @Override
                public void onSuccess() {
                    hrImgPg.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {
                    hrImgPg.setVisibility(View.GONE);
                    Log.e("ERROR", e.toString());
                    showToast(e.toString());
                }
            });

            hrImgPg2.setVisibility(View.VISIBLE);
            Picasso.get().load(previewModel.getHr_sign_image()).fit().into(hrSignImgView2, new Callback() {
                @Override
                public void onSuccess() {
                    hrImgPg2.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {
                    hrImgPg2.setVisibility(View.GONE);
                    Log.e("ERROR", e.toString());
                    showToast(e.toString());
                }
            });

            //set hr image to non disclosure layout field
            hrSignImgTv3.setText(Html.fromHtml(String.format("(%s)<br> Authorized Signature", previewModel.getHead_hr_sign_name())));
            hrImgPg3.setVisibility(View.VISIBLE);
            Picasso.get().load(previewModel.getHead_hr_sign_image()).fit().into(hrSignImgView3, new Callback() {
                @Override
                public void onSuccess() {
                    hrImgPg3.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {
                    hrImgPg3.setVisibility(View.GONE);
                    Log.e("ERROR", e.toString());
                    showToast(e.toString());
                }
            });

            //set hr image to declaration form layout field
            hrImgPg4.setVisibility(View.VISIBLE);
            Picasso.get().load(previewModel.getHr_sign_image()).fit().into(hrSignImgView4, new Callback() {
                @Override
                public void onSuccess() {
                    hrImgPg4.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {
                    hrImgPg4.setVisibility(View.GONE);
                    Log.e("ERROR", e.toString());
                    showToast(e.toString());
                }
            });

            //set hr image to FTA form layout field
            hrImgPg5.setVisibility(View.VISIBLE);
            Picasso.get().load(previewModel.getHead_hr_sign_image()).fit().into(hrSignImgView5, new Callback() {
                @Override
                public void onSuccess() {
                    hrImgPg5.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {
                    hrImgPg5.setVisibility(View.GONE);
                    Log.e("ERROR", e.toString());
                    showToast(e.toString());
                }
            });
            hrSignNameTv.setText(Html.fromHtml(String.format("(%s)<br>Sr. Manager- Human Resources", previewModel.getHead_hr_sign_name())));

            hrImgPg6.setVisibility(View.VISIBLE);
            Picasso.get().load(previewModel.getHead_hr_sign_image()).fit().into(hrSignImgView6, new Callback() {
                @Override
                public void onSuccess() {
                    hrImgPg6.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {
                    hrImgPg6.setVisibility(View.GONE);
                    Log.e("ERROR", e.toString());
                    showToast(e.toString());
                }
            });
            hrSignName1Tv.setText(Html.fromHtml(String.format("(%s)<br>Sr. Manager- Human Resources", previewModel.getHead_hr_sign_name())));


        } else {
            hrImgPg2.setVisibility(View.GONE);
            Picasso.get().load(R.drawable.ic_raw_image).into(hrSignImgView2);

            hrImgPg3.setVisibility(View.GONE);
            Picasso.get().load(R.drawable.ic_raw_image).into(hrSignImgView3);

            hrImgPg4.setVisibility(View.GONE);
            Picasso.get().load(R.drawable.ic_raw_image).into(hrSignImgView4);

            hrImgPg5.setVisibility(View.GONE);
            Picasso.get().load(R.drawable.ic_raw_image).into(hrSignImgView5);

            hrImgPg6.setVisibility(View.GONE);
            Picasso.get().load(R.drawable.ic_raw_image).into(hrSignImgView6);
        }

        //Emp Sign Image
        if (previewModel.getEmployee_sign_image() != null && !previewModel.getEmployee_sign_image().equalsIgnoreCase("")) {
            //set emp sign image gto joining layout views
            empImgPg.setVisibility(View.VISIBLE);
            Picasso.get().load(previewModel.getEmployee_sign_image()).fit().into(empSignImgView, new Callback() {
                @Override
                public void onSuccess() {
                    empImgPg.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {
                    empImgPg.setVisibility(View.GONE);
                    Log.e("ERROR", e.toString());
                    showToast(e.toString());
                }
            });

            empImgPg2.setVisibility(View.VISIBLE);
            Picasso.get().load(previewModel.getEmployee_sign_image()).fit().into(empSignImgView2, new Callback() {
                @Override
                public void onSuccess() {
                    empImgPg2.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {
                    empImgPg2.setVisibility(View.GONE);
                    Log.e("ERROR", e.toString());
                    showToast(e.toString());
                }
            });

            empImgPg3.setVisibility(View.VISIBLE);
            Picasso.get().load(previewModel.getEmployee_sign_image()).fit().into(empSignImgView3, new Callback() {
                @Override
                public void onSuccess() {
                    empImgPg3.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {
                    empImgPg3.setVisibility(View.GONE);
                    Log.e("ERROR", e.toString());
                    showToast(e.toString());
                }
            });

            empImgPg4.setVisibility(View.VISIBLE);
            Picasso.get().load(previewModel.getEmployee_sign_image()).fit().into(empSignImgView4, new Callback() {
                @Override
                public void onSuccess() {
                    empImgPg4.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {
                    empImgPg4.setVisibility(View.GONE);
                    Log.e("ERROR", e.toString());
                    showToast(e.toString());
                }
            });

            //set employee image to non disclosure layout
            empImgPg5.setVisibility(View.VISIBLE);
            Picasso.get().load(previewModel.getEmployee_sign_image()).fit().into(empSignImgView5, new Callback() {
                @Override
                public void onSuccess() {
                    empImgPg5.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {
                    empImgPg5.setVisibility(View.GONE);
                    Log.e("ERROR", e.toString());
                    showToast(e.toString());
                }
            });

            //set employee image to declaration form layout
            empImgPg6.setVisibility(View.VISIBLE);
            Picasso.get().load(previewModel.getEmployee_sign_image()).fit().into(empSignImgView6, new Callback() {
                @Override
                public void onSuccess() {
                    empImgPg6.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {
                    empImgPg6.setVisibility(View.GONE);
                    Log.e("ERROR", e.toString());
                    showToast(e.toString());
                }
            });

            empImgPg7.setVisibility(View.VISIBLE);
            Picasso.get().load(previewModel.getEmployee_sign_image()).fit().into(empSignImgView7, new Callback() {
                @Override
                public void onSuccess() {
                    empImgPg7.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {
                    empImgPg7.setVisibility(View.GONE);
                    Log.e("ERROR", e.toString());
                    showToast(e.toString());
                }
            });

            empImgPg8.setVisibility(View.VISIBLE);
            Picasso.get().load(previewModel.getEmployee_sign_image()).fit().into(empSignImgView8, new Callback() {
                @Override
                public void onSuccess() {
                    empImgPg8.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {
                    empImgPg8.setVisibility(View.GONE);
                    Log.e("ERROR", e.toString());
                    showToast(e.toString());
                }
            });

            empImgPg9.setVisibility(View.VISIBLE);
            Picasso.get().load(previewModel.getEmployee_sign_image()).fit().into(empSignImgView9, new Callback() {
                @Override
                public void onSuccess() {
                    empImgPg9.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {
                    empImgPg9.setVisibility(View.GONE);
                    Log.e("ERROR", e.toString());
                    showToast(e.toString());
                }
            });

            empImgPg10.setVisibility(View.VISIBLE);
            Picasso.get().load(previewModel.getEmployee_sign_image()).fit().into(empSignImgView10, new Callback() {
                @Override
                public void onSuccess() {
                    empImgPg10.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {
                    empImgPg10.setVisibility(View.GONE);
                    Log.e("ERROR", e.toString());
                    showToast(e.toString());
                }
            });

        } else {
            empImgPg.setVisibility(View.GONE);
            Picasso.get().load(R.drawable.ic_raw_image).into(empSignImgView);

            empImgPg2.setVisibility(View.GONE);
            Picasso.get().load(R.drawable.ic_raw_image).into(empSignImgView2);

            empImgPg3.setVisibility(View.GONE);
            Picasso.get().load(R.drawable.ic_raw_image).into(empSignImgView3);

            empImgPg4.setVisibility(View.GONE);
            Picasso.get().load(R.drawable.ic_raw_image).into(empSignImgView4);

            empImgPg5.setVisibility(View.GONE);
            Picasso.get().load(R.drawable.ic_raw_image).into(empSignImgView5);

            empImgPg6.setVisibility(View.GONE);
            Picasso.get().load(R.drawable.ic_raw_image).into(empSignImgView6);

            empImgPg7.setVisibility(View.GONE);
            Picasso.get().load(R.drawable.ic_raw_image).into(empSignImgView7);

            empImgPg8.setVisibility(View.GONE);
            Picasso.get().load(R.drawable.ic_raw_image).into(empSignImgView8);

            empImgPg9.setVisibility(View.GONE);
            Picasso.get().load(R.drawable.ic_raw_image).into(empSignImgView9);

            empImgPg10.setVisibility(View.GONE);
            Picasso.get().load(R.drawable.ic_raw_image).into(empSignImgView10);
        }

        //set values to joining layout fields
        nameTv.setText(previewModel.getName());
        fatherNameTv.setText(previewModel.getFather_name());
        dojTv.setText(previewModel.getJoining_date());
        dobTv.setText(previewModel.getDob());
        genderTv.setText(previewModel.getGender());

        if (previewModel.getPassport_size_image() != null && !previewModel.getPassport_size_image().equalsIgnoreCase("")) {
            userImgPg.setVisibility(View.VISIBLE);
            Picasso.get().load(previewModel.getPassport_size_image()).fit().into(userImgView, new Callback() {
                @Override
                public void onSuccess() {
                    userImgPg.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {
                    userImgPg.setVisibility(View.GONE);
                    Log.e("ERROR", e.toString());
                    showToast(e.toString());
                }
            });
        } else {
            userImgPg.setVisibility(View.GONE);
            Picasso.get().load(R.drawable.ic_username).into(userImgView);
        }
        bloodGroupTv.setText(previewModel.getBlood_group());
        maritalStatusTv.setText(previewModel.getMarital_status());
        departmentTv.setText(previewModel.getDepartment());
        designationTv.setText(previewModel.getDesignation());
        gradeTv.setText(previewModel.getGrade());
        branchTv.setText(previewModel.getBranch());
        zoneTv.setText(previewModel.getZone());
        emailTv.setText(previewModel.getEmail());
        contactNoTv.setText(previewModel.getNumber());
        permanentAddressTv.setText(previewModel.getPermanent_address());
        currentAddressTv.setText(previewModel.getCurrent_address());
        bankAccountNoTv.setText(previewModel.getBank_account_no());
        ifscCodeTv.setText(previewModel.getBank_ifsc());
        uanNoTv.setText(previewModel.getUan_no());
        esicNoTv.setText(previewModel.getEsic_no());
        aadharNoTv.setText(previewModel.getAadhar_no());
        panNoTv.setText(previewModel.getPan_no());
        emergencyNameTv.setText(previewModel.getPerson_name());
        emergencyNoTv.setText(previewModel.getPerson_number());
        emergencyRelationTv.setText(previewModel.getPerson_relation());

        //set nominees field
        if (!previewModel.getFather_n_name().isEmpty() && !previewModel.getFather_relation().isEmpty() &&
                !previewModel.getFather_dob().isEmpty() && !previewModel.getFather_amount().isEmpty()) {
            //set father nominee field to joining page
            fatherNameRowView.setVisibility(View.VISIBLE);
            fatherNameRow.setVisibility(View.VISIBLE);
            nFatherNameTv.setText(previewModel.getFather_n_name());
            nFatherRelationTv.setText(previewModel.getFather_relation());
            nFatherDobTv.setText(previewModel.getFather_dob());
            nFatherAmountTv.setText(previewModel.getFather_amount());

            //set father nominee field to form g page
            fatherNameRowView1.setVisibility(View.VISIBLE);
            fatherNameRow1.setVisibility(View.VISIBLE);
            nFatherNameTv1.setText(previewModel.getFather_n_name());
            nFatherRelationTv1.setText(previewModel.getFather_relation());
            nFatherDobTv1.setText(previewModel.getFather_dob());
            nFatherAmountTv1.setText(previewModel.getFather_amount());

            //set father nominee field to declaration page
            fatherNameRowView2.setVisibility(View.VISIBLE);
            fatherNameRow2.setVisibility(View.VISIBLE);
            nFatherNameTv2.setText(previewModel.getFather_n_name());
            nFatherRelationTv2.setText(previewModel.getFather_relation());

            //set father nominee field to declaration page part 2
            fatherNameRowView3.setVisibility(View.VISIBLE);
            fatherNameRow3.setVisibility(View.VISIBLE);
            nFatherNameTv3.setText(previewModel.getFather_n_name());
            nFatherDobTv3.setText(previewModel.getFather_dob());
            nFatherRelationTv3.setText(previewModel.getFather_relation());

            //set father nominee field to declaration page part 3
            fatherNameRowView4.setVisibility(View.VISIBLE);
            fatherNameRow4.setVisibility(View.VISIBLE);
            nFatherNameTv4.setText(previewModel.getFather_n_name());
            nFatherDobTv4.setText(previewModel.getFather_dob());
            nFatherRelationTv4.setText(previewModel.getFather_relation());


        } else {
            fatherNameRowView.setVisibility(View.GONE);
            fatherNameRow.setVisibility(View.GONE);

            fatherNameRowView1.setVisibility(View.GONE);
            fatherNameRow1.setVisibility(View.GONE);

            fatherNameRowView2.setVisibility(View.GONE);
            fatherNameRow2.setVisibility(View.GONE);

            fatherNameRowView3.setVisibility(View.GONE);
            fatherNameRow3.setVisibility(View.GONE);

            fatherNameRowView4.setVisibility(View.GONE);
            fatherNameRow4.setVisibility(View.GONE);

        }

        if (!previewModel.getMother_n_name().isEmpty() && !previewModel.getMother_relation().isEmpty() &&
                !previewModel.getMother_dob().isEmpty() && !previewModel.getMother_amount().isEmpty()) {

            //set mother nominee field to joining page
            motherNameRowView.setVisibility(View.VISIBLE);
            motherNameRow.setVisibility(View.VISIBLE);
            nMotherNameTv.setText(previewModel.getMother_n_name());
            nMotherRelationTv.setText(previewModel.getMother_relation());
            nMotherDobTv.setText(previewModel.getMother_dob());
            nMotherAmountTv.setText(previewModel.getMother_amount());

            //set mother nominee field to Form G page
            motherNameRowView1.setVisibility(View.VISIBLE);
            motherNameRow1.setVisibility(View.VISIBLE);
            nMotherNameTv1.setText(previewModel.getMother_n_name());
            nMotherRelationTv1.setText(previewModel.getMother_relation());
            nMotherDobTv1.setText(previewModel.getMother_dob());
            nMotherAmountTv1.setText(previewModel.getMother_amount());

            //set mother nominee field to declaration page
            motherNameRowView2.setVisibility(View.VISIBLE);
            motherNameRow2.setVisibility(View.VISIBLE);
            nMotherNameTv2.setText(previewModel.getMother_n_name());
            nMotherRelationTv2.setText(previewModel.getMother_relation());

            //set mother nominee field to declaration page 2
            motherNameRowView3.setVisibility(View.VISIBLE);
            motherNameRow3.setVisibility(View.VISIBLE);
            nMotherNameTv3.setText(previewModel.getMother_n_name());
            nMotherDobTv3.setText(previewModel.getMother_dob());
            nMotherRelationTv3.setText(previewModel.getMother_relation());

            //set mother nominee field to declaration page 4
            motherNameRowView4.setVisibility(View.VISIBLE);
            motherNameRow4.setVisibility(View.VISIBLE);
            nMotherNameTv4.setText(previewModel.getMother_n_name());
            nMotherDobTv4.setText(previewModel.getMother_dob());
            nMotherRelationTv4.setText(previewModel.getMother_relation());

        } else {

            motherNameRowView.setVisibility(View.GONE);
            motherNameRow.setVisibility(View.GONE);

            motherNameRowView1.setVisibility(View.GONE);
            motherNameRow1.setVisibility(View.GONE);

            motherNameRowView2.setVisibility(View.GONE);
            motherNameRow2.setVisibility(View.GONE);

            motherNameRowView3.setVisibility(View.GONE);
            motherNameRow3.setVisibility(View.GONE);

            motherNameRowView4.setVisibility(View.GONE);
            motherNameRow4.setVisibility(View.GONE);

        }

        if (!previewModel.getWife_n_name().isEmpty() && !previewModel.getWife_relation().isEmpty() &&
                !previewModel.getWife_dob().isEmpty() && !previewModel.getWife_amount().isEmpty()) {

            //set spouse nominee field to joining page
            spouseNameRowView.setVisibility(View.VISIBLE);
            spouseNameRow.setVisibility(View.VISIBLE);
            nWifeNameTv.setText(previewModel.getWife_n_name());
            nWifeRelationTv.setText(previewModel.getWife_relation());
            nWifeDobTv.setText(previewModel.getWife_dob());
            nWifeAmountTv.setText(previewModel.getWife_amount());

            //set spouse nominee field to Form G page
            spouseNameRowView1.setVisibility(View.VISIBLE);
            spouseNameRow1.setVisibility(View.VISIBLE);
            nWifeNameTv1.setText(previewModel.getWife_n_name());
            nWifeRelationTv1.setText(previewModel.getWife_relation());
            nWifeDobTv1.setText(previewModel.getWife_dob());
            nWifeAmountTv1.setText(previewModel.getWife_amount());

            //set spouse nominee field to declaration page
            spouseNameRowView2.setVisibility(View.VISIBLE);
            spouseNameRow2.setVisibility(View.VISIBLE);
            nWifeNameTv2.setText(previewModel.getWife_n_name());
            nWifeRelationTv2.setText(previewModel.getWife_relation());

            //set spouse nominee field to declaration page 2
            spouseNameRowView3.setVisibility(View.VISIBLE);
            spouseNameRow3.setVisibility(View.VISIBLE);
            nWifeNameTv3.setText(previewModel.getWife_n_name());
            nWifeDobTv3.setText(previewModel.getWife_dob());
            nWifeRelationTv3.setText(previewModel.getWife_relation());

            //set spouse nominee field to declaration page 4
            spouseNameRowView4.setVisibility(View.VISIBLE);
            spouseNameRow4.setVisibility(View.VISIBLE);
            nWifeNameTv4.setText(previewModel.getWife_n_name());
            nWifeDobTv4.setText(previewModel.getWife_dob());
            nWifeRelationTv4.setText(previewModel.getWife_relation());


        } else {

            spouseNameRowView.setVisibility(View.GONE);
            spouseNameRow.setVisibility(View.GONE);

            spouseNameRowView1.setVisibility(View.GONE);
            spouseNameRow1.setVisibility(View.GONE);

            spouseNameRowView2.setVisibility(View.GONE);
            spouseNameRow2.setVisibility(View.GONE);

            spouseNameRowView3.setVisibility(View.GONE);
            spouseNameRow3.setVisibility(View.GONE);

            spouseNameRowView4.setVisibility(View.GONE);
            spouseNameRow4.setVisibility(View.GONE);
        }

        if (!previewModel.getGuardian_n_name().isEmpty() && !previewModel.getGuardian_relation().isEmpty() &&
                !previewModel.getGuardian_dob().isEmpty() && !previewModel.getGuardian_amount().isEmpty()) {

            //set guardian nominee field to joining page
            guardianNameRowView.setVisibility(View.VISIBLE);
            guardianNameRow.setVisibility(View.VISIBLE);
            nGuardianNameTv.setText(previewModel.getGuardian_n_name());
            nGuardianRelationTv.setText(previewModel.getGuardian_relation());
            nGuardianDobTv.setText(previewModel.getGuardian_dob());
            nGuardianAmountTv.setText(previewModel.getGuardian_amount());

            //set guardian nominee field to Form G page
            guardianNameRowView1.setVisibility(View.VISIBLE);
            guardianNameRow1.setVisibility(View.VISIBLE);
            nGuardianNameTv1.setText(previewModel.getGuardian_n_name());
            nGuardianRelationTv1.setText(previewModel.getGuardian_relation());
            nGuardianDobTv1.setText(previewModel.getGuardian_dob());
            nGuardianAmountTv1.setText(previewModel.getGuardian_amount());

            //set guardian nominee field to declaration page
            guardianNameRowView2.setVisibility(View.VISIBLE);
            guardianNameRow2.setVisibility(View.VISIBLE);
            nGuardianNameTv2.setText(previewModel.getGuardian_n_name());
            nGuardianRelationTv2.setText(previewModel.getGuardian_relation());

            //set guardian nominee field to declaration page 2
            guardianNameRowView3.setVisibility(View.VISIBLE);
            guardianNameRow3.setVisibility(View.VISIBLE);
            nGuardianNameTv3.setText(previewModel.getGuardian_n_name());
            nGuardianDobTv3.setText(previewModel.getGuardian_dob());
            nGuardianRelationTv3.setText(previewModel.getGuardian_relation());

            //set guardian nominee field to declaration page 4
            guardianNameRowView4.setVisibility(View.VISIBLE);
            guardianNameRow4.setVisibility(View.VISIBLE);
            nGuardianNameTv4.setText(previewModel.getGuardian_n_name());
            nGuardianDobTv4.setText(previewModel.getGuardian_dob());
            nGuardianRelationTv4.setText(previewModel.getGuardian_relation());

        } else {

            guardianNameRowView.setVisibility(View.GONE);
            guardianNameRow.setVisibility(View.GONE);

            guardianNameRowView1.setVisibility(View.GONE);
            guardianNameRow1.setVisibility(View.GONE);

            guardianNameRowView2.setVisibility(View.GONE);
            guardianNameRow2.setVisibility(View.GONE);

            guardianNameRowView3.setVisibility(View.GONE);
            guardianNameRow3.setVisibility(View.GONE);

            guardianNameRowView4.setVisibility(View.GONE);
            guardianNameRow4.setVisibility(View.GONE);
        }

        if (!previewModel.getSibling_n_name().isEmpty() && !previewModel.getSibling_relation().isEmpty() &&
                !previewModel.getSibling_dob().isEmpty() && !previewModel.getSibling_amount().isEmpty()) {

            //set sibling nominee field to joining page
            siblingNameRowView.setVisibility(View.VISIBLE);
            siblingNameRow.setVisibility(View.VISIBLE);
            nSiblingNameTv.setText(previewModel.getSibling_n_name());
            nSiblingRelationTv.setText(previewModel.getSibling_relation());
            nSiblingDobTv.setText(previewModel.getSibling_dob());
            nSiblingAmountTv.setText(previewModel.getSibling_amount());

            //set sibling nominee field to Form G page
            spouseNameRowView1.setVisibility(View.VISIBLE);
            spouseNameRow1.setVisibility(View.VISIBLE);
            nSiblingNameTv1.setText(previewModel.getSibling_n_name());
            nSiblingRelationTv1.setText(previewModel.getSibling_relation());
            nSiblingDobTv1.setText(previewModel.getSibling_dob());
            nSiblingAmountTv1.setText(previewModel.getSibling_amount());

            //set sibling nominee field to declaration page
            siblingNameRowView2.setVisibility(View.VISIBLE);
            siblingNameRow2.setVisibility(View.VISIBLE);
            nSiblingNameTv2.setText(previewModel.getSibling_n_name());
            nSiblingRelationTv2.setText(previewModel.getSibling_relation());

            //set sibling nominee field to declaration page 2
            siblingNameRowView3.setVisibility(View.VISIBLE);
            siblingNameRow3.setVisibility(View.VISIBLE);
            nSiblingNameTv3.setText(previewModel.getSibling_n_name());
            nSiblingDobTv3.setText(previewModel.getSibling_dob());
            nSiblingRelationTv3.setText(previewModel.getSibling_relation());

            //set sibling nominee field to declaration page 4
            siblingNameRowView4.setVisibility(View.VISIBLE);
            siblingNameRow4.setVisibility(View.VISIBLE);
            nSiblingNameTv4.setText(previewModel.getSibling_n_name());
            nSiblingDobTv4.setText(previewModel.getSibling_dob());
            nSiblingRelationTv4.setText(previewModel.getSibling_relation());


        } else {

            siblingNameRowView.setVisibility(View.GONE);
            siblingNameRow.setVisibility(View.GONE);

            siblingNameRowView1.setVisibility(View.GONE);
            siblingNameRow1.setVisibility(View.GONE);

            siblingNameRowView2.setVisibility(View.GONE);
            siblingNameRow2.setVisibility(View.GONE);

            siblingNameRowView3.setVisibility(View.GONE);
            siblingNameRow3.setVisibility(View.GONE);

            siblingNameRowView4.setVisibility(View.GONE);
            siblingNameRow4.setVisibility(View.GONE);
        }

        if (!previewModel.getChild_one_name().isEmpty() && !previewModel.getChild_one_relation().isEmpty() &&
                !previewModel.getChild_one_relation().isEmpty() && !previewModel.getChild_one_amount().isEmpty()) {

            //set child one nominee field to joining page
            childOneNameRowView.setVisibility(View.VISIBLE);
            childOneNameRow.setVisibility(View.VISIBLE);
            nChildOneNameTv.setText(previewModel.getChild_one_name());
            nChildOneRelationTv.setText(previewModel.getChild_one_relation());
            nChildOneDobTv.setText(previewModel.getChild_one_relation());
            nChildOneAmountTv.setText(previewModel.getChild_one_amount());

            //set child one nominee field to Form G Page
            childOneNameRowView1.setVisibility(View.VISIBLE);
            childOneNameRow1.setVisibility(View.VISIBLE);
            nChildOneNameTv1.setText(previewModel.getChild_one_name());
            nChildOneRelationTv1.setText(previewModel.getChild_one_relation());
            nChildOneDobTv1.setText(previewModel.getChild_one_relation());
            nChildOneAmountTv1.setText(previewModel.getChild_one_amount());

            //set child one nominee field to declaration page
            childOneNameRowView2.setVisibility(View.VISIBLE);
            childOneNameRow2.setVisibility(View.VISIBLE);
            nChildOneNameTv2.setText(previewModel.getChild_one_name());
            nChildOneRelationTv2.setText(previewModel.getChild_one_relation());

            //set child one nominee field to declaration page 2
            childOneNameRowView3.setVisibility(View.VISIBLE);
            childOneNameRow3.setVisibility(View.VISIBLE);
            nChildOneNameTv3.setText(previewModel.getChild_one_name());
            nChildOneDobTv3.setText(previewModel.getChild_one_dob());
            nChildOneRelationTv3.setText(previewModel.getChild_one_relation());

            //set child one nominee field to declaration page 3
            childOneNameRowView4.setVisibility(View.VISIBLE);
            childOneNameRow4.setVisibility(View.VISIBLE);
            nChildOneNameTv4.setText(previewModel.getChild_one_name());
            nChildOneDobTv4.setText(previewModel.getChild_one_dob());
            nChildOneRelationTv4.setText(previewModel.getChild_one_relation());


        } else {
            childOneNameRowView.setVisibility(View.GONE);
            childOneNameRow.setVisibility(View.GONE);

            childOneNameRowView1.setVisibility(View.GONE);
            childOneNameRow1.setVisibility(View.GONE);

            childOneNameRowView2.setVisibility(View.GONE);
            childOneNameRow2.setVisibility(View.GONE);

            childOneNameRowView3.setVisibility(View.GONE);
            childOneNameRow3.setVisibility(View.GONE);

            childOneNameRowView4.setVisibility(View.GONE);
            childOneNameRow4.setVisibility(View.GONE);
        }

        if (!previewModel.getChild_two_name().isEmpty() && !previewModel.getChild_two_relation().isEmpty() &&
                !previewModel.getChild_two_relation().isEmpty() && !previewModel.getChild_two_amount().isEmpty()) {

            //set child two nominee field to joining page
            childTwoNameRowView.setVisibility(View.VISIBLE);
            childTwoNameRow.setVisibility(View.VISIBLE);
            nChildTwoNameTv.setText(previewModel.getChild_two_name());
            nChildTwoRelationTv.setText(previewModel.getChild_two_relation());
            nChildTwoDobTv.setText(previewModel.getChild_two_relation());
            nChildTwoAmountTv.setText(previewModel.getChild_two_amount());

            //set child two nominee field to Form G page
            childTwoNameRowView1.setVisibility(View.VISIBLE);
            childTwoNameRow1.setVisibility(View.VISIBLE);
            nChildTwoNameTv1.setText(previewModel.getChild_two_name());
            nChildTwoRelationTv1.setText(previewModel.getChild_two_relation());
            nChildTwoDobTv1.setText(previewModel.getChild_two_relation());
            nChildTwoAmountTv1.setText(previewModel.getChild_two_amount());

            //set child two nominee field to declaration page
            childTwoNameRowView2.setVisibility(View.VISIBLE);
            childTwoNameRow2.setVisibility(View.VISIBLE);
            nChildTwoNameTv2.setText(previewModel.getChild_two_name());
            nChildTwoRelationTv2.setText(previewModel.getChild_two_relation());

            //set child two nominee field to declaration page 2
            childTwoNameRowView3.setVisibility(View.VISIBLE);
            childTwoNameRow3.setVisibility(View.VISIBLE);
            nChildTwoNameTv3.setText(previewModel.getChild_two_name());
            nChildTwoDobTv3.setText(previewModel.getChild_two_dob());
            nChildTwoRelationTv3.setText(previewModel.getChild_two_relation());

            //set child two nominee field to declaration page 3
            childTwoNameRowView4.setVisibility(View.VISIBLE);
            childTwoNameRow4.setVisibility(View.VISIBLE);
            nChildTwoNameTv4.setText(previewModel.getChild_two_name());
            nChildTwoDobTv4.setText(previewModel.getChild_two_dob());
            nChildTwoRelationTv4.setText(previewModel.getChild_two_relation());

        } else {

            childTwoNameRowView.setVisibility(View.GONE);
            childTwoNameRow.setVisibility(View.GONE);

            childTwoNameRowView1.setVisibility(View.GONE);
            childTwoNameRow1.setVisibility(View.GONE);

            childTwoNameRowView2.setVisibility(View.GONE);
            childTwoNameRow2.setVisibility(View.GONE);

            childTwoNameRowView3.setVisibility(View.GONE);
            childTwoNameRow3.setVisibility(View.GONE);

            childTwoNameRowView4.setVisibility(View.GONE);
            childTwoNameRow4.setVisibility(View.GONE);
        }

        todayDateTv.setText(todayDate);

        //set data to pf layout fields
        pfTextTv.setText(Html.fromHtml(String.format("Employee Code : %s<br>Joining Date : %s", previewModel.getEmployee_id(), previewModel.getJoining_date())));
        pfText2.setText(Html.fromHtml(String.format("I <u>%s</u> Son/ wife/ daughter of *Sh. <u>%s</u> do hereby solemnly declare that :-", previewModel.getName(), previewModel.getFather_name())));
        pfText3.setText(Html.fromHtml(String.format("Shri/Smt. <u>%s</u> is appointed as <u>%s</u> in M/s <u>%s</u> with effect from <u>%s</u> P.F. Account Number <u>%s</u>", previewModel.getName(), previewModel.getDesignation(), previewModel.getCompany_name(), previewModel.getJoining_date(), previewModel.getUan_no())));
        pfTodayDate1Tv.setText(Html.fromHtml(String.format("Date: <u>%s</u>", todayDate)));
        pfTodayDate2Tv.setText(Html.fromHtml(String.format("Date: <u>%s</u>", todayDate)));

        //set fields to form g layout fields
        formGText0.setText(Html.fromHtml(String.format("To,<br>%s,<br>%s", previewModel.getCompany_name(), previewModel.getCompany_address())));
        formGText1.setText(Html.fromHtml(String.format("      1. I, Shri/Shrimati <u>%s</u> whose particulars are given in the statement below, have acquired a " +
                "family within the meaning of clause (h) of section (2) of the Payment of Gratuity Act, 1972 with effect from the <u>%s</u> " +
                "in the manner indicated below and therefore nominate afresh the person(s) mentioned below to receive the gratuity payable after " +
                "my death as also the gratuity standing to my credit in the event of my death before that amount has become payable, or having become" +
                " payable has not been paid direct that the said amount of gratuity " +
                "shall be paid in proportion indicated against the name(s) of the nominee(s).", previewModel.getName(), previewModel.getJoining_date())));

        statementTv.setText(Html.fromHtml(String.format("1. Name of the employee in full: <u>%s</u><br/>2. Sex: <u>%s</u><br/>3. Whether unmarried/married/widow/widower: <u>%s</u><br/>" +
                        "4. Department/Branch/Section where employed: <u>%s</u><br/>5. Post held with Ticket No. or Serial No., if any.<br/>6. Date of appointment: <u>%s</u><br/>" +
                        "7. Permanent address: <u>%s</u>.", previewModel.getName(),
                previewModel.getGender(), previewModel.getMarital_status(), previewModel.getDepartment(), previewModel.getJoining_date(),
                previewModel.getPermanent_address())));

        /*statementTv.setText(Html.fromHtml(String.format("1. Name of the employee in full: <u>%s</u><br/>2. Sex: <u>%s</u><br/>3. Religion: <u>%s</u><br/>4. Whether unmarried/married/widow/widower: <u>%s</u><br/>" +
                        "5. Department/Branch/Section where employed: <u>%s</u><br/>6. Post held with Ticket No. or Serial No., if any.<br/>7. Date of appointment: <u>%s</u><br/>" +
                        "8. Permanent address: <u>%s</u><br/> Village %s. Thana %s. Sub-division %s. Post Office %s. District %s. State <u>%s</u>.", previewModel.getName(),
                previewModel.getGender(), previewModel.getName(), previewModel.getMarital_status(), previewModel.getDepartment(), previewModel.getJoining_date(),
                previewModel.getPermanent_address(), "............", "............", "............", "............", "............", "Jaipur")));*/

        formGDatePlace1Tv.setText(Html.fromHtml(String.format("Place: <u>Jaipur</u><br/>Date: <u>%s</u>", todayDate)));
        formGDatePlace2Tv.setText(Html.fromHtml(String.format("Place: <u>Jaipur</u><br/>Date: <u>%s</u>", todayDate)));
        formGDate1Tv.setText(Html.fromHtml(String.format("Date: <u>%s</u>", todayDate)));
        formGDate2Tv.setText(Html.fromHtml(String.format("Date: <u>%s</u>", todayDate)));


        //set values to privacy fields
        nameEmpCodeTv.setText(Html.fromHtml(String.format("Full Name: <u>%s</u><br/>Emp/MD/RD Code: <u>%s</u>", previewModel.getName(), previewModel.getEmployee_id())));
        privacyDateTv.setText(Html.fromHtml(String.format("(Signature of Employee)<br/>Date: <u>%s</u>", todayDate)));

        //set values to employee non disclosure views
        nonDisclosureText0.setText(Html.fromHtml(String.format("This Employee Non-Disclosure Agreement (hereinafter referred to as “Agreement”) has been entered into on this 20 day of Jan , 2020 at Jaipur.<br/><br/>Between<br><br/><b>Employer: %s</b>, having its registered office at %s (hereinafter referred to as the <b>“Party-A”/ “Company”</b><br><br>And<br>",
                previewModel.getCompany_name(), previewModel.getCompany_address())));
        nonDisclosureText1.setText(Html.fromHtml(String.format("Employee: <u>%s</u> S/o <u>%s</u>, R/o, <u>%s</u> (hereinafter referred to as the <b>“Party-B”</b>)", previewModel.getName(), previewModel.getFather_name(), previewModel.getPermanent_address())));
        partyBTextTv.setText(Html.fromHtml(String.format("<b>Party B</b> <br/> (Name): <u>%s</u>", previewModel.getName())));
        partyBEmpNoTv.setText(Html.fromHtml(String.format("Employee Code: <u>%s</u> <br/>Mobile Number: <u>%s</u>", " ", previewModel.getNumber())));
        companyNameTextTv.setText(Html.fromHtml(String.format("(Party-A)<br>For: %s", previewModel.getCompany_name())));


        //set value to declaration layout views
        int salary = Integer.parseInt(previewModel.getSalary());
        Log.d("SALARY", String.valueOf(salary));
        if (salary <= 21000) {
            declarationFormLayout.setVisibility(View.VISIBLE);
            decInstructionLayout.setVisibility(View.VISIBLE);
        } else {
            declarationFormLayout.setVisibility(View.GONE);
            decInstructionLayout.setVisibility(View.GONE);
        }

        dNameTv.setText(previewModel.getName());
        dFatherHusbandNameTv.setText(previewModel.getFather_name());

        //split date of birth to day, month and year
        Date dobDate = new SimpleDateFormat("dd/MM/yyyy").parse(previewModel.getDob());

        dDayTv.setText(DateFormat.format("dd", dobDate));
        dMonthTv.setText(DateFormat.format("MMM", dobDate));
        dYearTv.setText(DateFormat.format("yyyy", dobDate));

        dMarriedTv.setText(previewModel.getMarital_status());
        dSexTv.setText(previewModel.getGender());
        dPresentAddressTv.setText(Html.fromHtml(String.format("7. Present Address: <br/> <u>%s</u> <br/>Pin Code: <u>%s</u><br/>Mobile No: <u>%s</u><br/>",
                previewModel.getCurrent_address(), previewModel.getPin_code(), previewModel.getNumber())));
        dPermanentAddressTv.setText(Html.fromHtml(String.format("8. Permanent Address: <br/> <u>%s</u> <br/>Pin Code: <u>%s</u><br/>Mobile No: <u>%s</u><br/>",
                previewModel.getPermanent_address(), previewModel.getPin_code(), previewModel.getNumber())));
        dBranchOfficeTv.setText("Branch Office: ");
        dDispensaryTv.setText("Dispensary: ");

        //split date of appointment to day, month and year
        Date joiningDate = new SimpleDateFormat("dd/MM/yyyy").parse(previewModel.getJoining_date());

        dAppointmentDayTv.setText(DateFormat.format("dd", joiningDate));
        dAppointmentMonthTv.setText(DateFormat.format("MMM", joiningDate));
        dAppointmentYearTv.setText(DateFormat.format("yyyy", joiningDate));
        dAppointmentDateTv.setText(Html.fromHtml(String.format("Date of Appointment: <u>%s</u>", previewModel.getJoining_date())));
        declarationFromTv1.setText(Html.fromHtml(String.format("11. Name and Address of the Employer:<br> <u>%s", previewModel.getCompany_name())));
        declarationFromTv2.setText(Html.fromHtml(String.format("(c). Name and Address of the Employer:<br> <u>%s</u><br>Email Address:%s", previewModel.getCompany_name(), previewModel.getCompany_email())));


        //set values to FTA layout views
        if (previewModel.getEmployee_level().equalsIgnoreCase("VBA")) {
            ftaLayout.setVisibility(View.VISIBLE);
            fta2Layout.setVisibility(View.VISIBLE);
            fta3Layout.setVisibility(View.VISIBLE);
            fta4Layout.setVisibility(View.VISIBLE);
            fta5Layout.setVisibility(View.VISIBLE);
        } else {
            ftaLayout.setVisibility(View.GONE);
            fta2Layout.setVisibility(View.GONE);
            fta3Layout.setVisibility(View.GONE);
            fta4Layout.setVisibility(View.GONE);
            fta5Layout.setVisibility(View.GONE);
        }

        ftaDateTv.setText(Html.fromHtml(String.format("Date: <u>%s</u>", todayDate)));
        ftaTextTv.setText(Html.fromHtml(String.format("To,<br/>Name: <u>%s</u>,<br/>S/O: <u>%s</u>,<br/>Address: <u>%s</u>,<br/><br/>Dear Mr./Mrs. <u>%s</u>",
                previewModel.getName(), previewModel.getFather_name(), previewModel.getPermanent_address(), previewModel.getName())));

        //convert joining date to 1 year after date
        Date date1 = new SimpleDateFormat("dd/MM/yyyy").parse(previewModel.getJoining_date());
        Calendar cal = Calendar.getInstance();
        cal.setTime(date1);
        cal.add(Calendar.YEAR, 1);

        ftaPoint1Tv.setText(Html.fromHtml(String.format("Your employment will be for a fixed period of One Year from <u>%s</u> to <u>%s</u>. This Fixed Term Appointment neither creates any right of employment nor any preference in the employment and at the end of term, your services will automatically stand terminated.",
                previewModel.getJoining_date(), new SimpleDateFormat("dd/MM/yyyy").format(cal.getTime()))));
        ftaPoint2Tv.setText(Html.fromHtml(String.format("You will be paid consolidated Gross Salary <u>%s</u>/- per month as per the VBA Salary Slab applicable at the organisation under your capacity of role and designation. Statutory & Compliance norms as applicable under the employment conditions.",
                previewModel.getSalary())));


        ftaCompanyNameTv.setText(Html.fromHtml(String.format("<b>For %s</b>", previewModel.getCompany_name())));
        ftaCompanyNameTv2.setText(Html.fromHtml(String.format("<b>For %s</b>", previewModel.getCompany_name())));
        tAndCTextTv1.setText(Html.fromHtml(String.format("<b>Fixed Period Employment</b>: Your employment with <b>%s</b> shall be period for the 1 year and shall cease on completion of the period stated above. Renewal of your fixed term of appointment will be purely based on your performance and subsequent of recommendation of your Reporting Manager as well as the need of the organisation. Since the appointment is being made for a specific period and purpose there shall not be any right or a lien on the job and you will not be eligible to claim regular employment even if such a vacancy exists for the post held by you or otherwise. In case of termination of services before completion of fix period, the management will be liable to pay only notice period if applicable. Management is not liable to pay any compensation or wages/salary for the remaining employment period and no Layoff/Retrenchment compensation will be payable.", previewModel.getCompany_name())));
        tAndCTextTv4.setText(Html.fromHtml(String.format("<b>Assignments/Deputation/Transfers</b>: Though you have been engaged for a specific position, %s reserves the right to send you on training / deputation /secondment/ assignments / transfers to any branch or location/department/post in Rajasthan wherever Company has its operations or may have operations in future.", previewModel.getCompany_name())));
        tAndCTextTv9.setText(Html.fromHtml(String.format("<b>Intellectual Property Rights</b>: Employees shall protect intellectual property rights of (%s). Intellectual property held by employees if any shall be declared at the time of joining. All intellectual property rights, including but not limited to, patents, copyrights, design, trademarks developed by you during your tenure with (%s) or using the (%s) infrastructure, or acquired in performance of or discharging official duties at the Organization shall be the sole and exclusive property of the (%s) and the same shall be deemed to the “work made for hire”. You shall execute / sign such documents for the purpose of assignment such intellectual property, as and when required by the organisation. (%s) reserves the right to proceed legally against you and recover damages, where any such intellectual property is sought to be protected by you independently of (%s).", previewModel.getCompany_name(), previewModel.getCompany_name(), previewModel.getCompany_name(), previewModel.getCompany_name(), previewModel.getCompany_name(), previewModel.getCompany_name())));
        tAndCTextTv15.setText(Html.fromHtml(String.format("<b>Safe custody of Assets</b>: You shall be responsible to ensure safe custody of Company assets, documents and or information in your custody and handover the same to designated person while proceeding on leave / transfer / separation or changing over of the shift (if any), or as specified by the organisation. (%s) resources shall be put to use only for official purpose.", previewModel.getCompany_name())));
        tAndCTextTv17.setText(Html.fromHtml(String.format("<b>Conflict of Interest</b>: Your positions with the Company calls for whole time employment and you will devote yourself exclusively to the business of (%s). You will not take up any other work for remuneration (part time or otherwise) or work on advisory capacity or be interested directly or indirectly in any other trade business, during your employment with the Company without written permission. In case of any related part transaction the employees are to disclose the same and be not part such business decision.", previewModel.getCompany_name())));
        tAndCTextTv18.setText(Html.fromHtml(String.format("<b>Confidentiality</b>: You are expected to maintain utmost confidentiality in regard to the affairs of (%s) and shall keep secured of any information, whether written or oral, which relates to internal controls, computer or data processing programme, techniques or systems, or information concerning the business or financial affairs and method of operation or proposed, trade secrets, know-how, or inventions of (%s) or its affiliate, or any client, agent, contractor or vendor. You shall not disclose the identities and other related information of any of its clients, breach of this provision shall be treated as gross violation of the terms herein and your services are liable to be terminated without notice.", previewModel.getCompany_name(), previewModel.getCompany_name())));
        tAndCTextTv19.setText(Html.fromHtml(String.format("<b>Submission of Documents</b>: This offer has been made based on the information furnished by you. However, if there is any discrepancy in the copies of the documents /certificates given by you as a proof in support of the above, (%s) reserves the right to revoke the offer.", previewModel.getCompany_name())));
        tAndCTextTv20A.setText(Html.fromHtml(String.format("<b>Authorisation</b>: Only those authorised by a specific power of attorney may sign legal document, representing (%s).", previewModel.getCompany_name())));

        ftaHerebyNameTv.setText(previewModel.getName());
        ftaHerebySignatureTv.setText(previewModel.getName());
        ftaHerebyAddressTv.setText(previewModel.getPermanent_address());
        //ftaHerebyDateTv.setText(todayDate);
        ftaHerebyDateTv.setText(Html.fromHtml(String.format("Date : %s", todayDate)));
        //ftaHerebyPlaceTv.setText("Jaipur");
        ftaHerebyPlaceTv.setText(Html.fromHtml(String.format("Place : %s", "Jaipur")));
        empNameTv.setText(Html.fromHtml(String.format("(%s)", previewModel.getName())));
        signNameTv2.setText(Html.fromHtml(String.format("(%s)", previewModel.getName())));

        //set Value to Image Layout
        //Passport Image
        if (previewModel.getPassport_size_image() != null && !previewModel.getPassport_size_image().equalsIgnoreCase("")) {
            passportPg.setVisibility(View.VISIBLE);
            Picasso.get().load(previewModel.getPassport_size_image()).fit().into(passportIv, new Callback() {
                @Override
                public void onSuccess() {
                    passportPg.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {
                    passportPg.setVisibility(View.GONE);
                    Log.e("ERROR", e.toString());
                    showToast(e.toString());
                }
            });
        } else {
            passportPg.setVisibility(View.GONE);
            Picasso.get().load(R.drawable.ic_raw_image).into(passportIv);
        }

        //Employee Sign Image
        if (previewModel.getEmployee_sign_image() != null && !previewModel.getEmployee_sign_image().equalsIgnoreCase("")) {
            empSignPg.setVisibility(View.VISIBLE);
            Picasso.get().load(previewModel.getEmployee_sign_image()).fit().into(empSignIv, new Callback() {
                @Override
                public void onSuccess() {
                    empSignPg.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {
                    empSignPg.setVisibility(View.GONE);
                    Log.e("ERROR", e.toString());
                    showToast(e.toString());
                }
            });
        } else {
            empSignPg.setVisibility(View.GONE);
            Picasso.get().load(R.drawable.ic_raw_image).into(empSignIv);
        }

        //Aadhar Card Front Image
        if (previewModel.getAadhar_card_image() != null && !previewModel.getAadhar_card_image().equalsIgnoreCase("")) {
            acPg.setVisibility(View.VISIBLE);
            Picasso.get().load(previewModel.getAadhar_card_image()).fit().into(acIv, new Callback() {
                @Override
                public void onSuccess() {
                    acPg.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {
                    acPg.setVisibility(View.GONE);
                    Log.e("ERROR", e.toString());
                    showToast(e.toString());
                }
            });
        } else {
            acPg.setVisibility(View.GONE);
            Picasso.get().load(R.drawable.ic_raw_image).into(acIv);
        }

        //Aadhar Card Back Image
        if (previewModel.getAadhar_card_back_image() != null && !previewModel.getAadhar_card_back_image().equalsIgnoreCase("")) {
            acBPg.setVisibility(View.VISIBLE);
            Picasso.get().load(previewModel.getAadhar_card_back_image()).fit().into(acBIv, new Callback() {
                @Override
                public void onSuccess() {
                    acBPg.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {
                    acBPg.setVisibility(View.GONE);
                    Log.e("ERROR", e.toString());
                    showToast(e.toString());
                }
            });
        } else {
            acBPg.setVisibility(View.GONE);
            Picasso.get().load(R.drawable.ic_raw_image).into(acBIv);
        }

        //PAN Card Image
        if (previewModel.getPan_card_image() != null && !previewModel.getPan_card_image().equalsIgnoreCase("")) {
            pcPg.setVisibility(View.VISIBLE);
            Picasso.get().load(previewModel.getPan_card_image()).fit().into(pcIv, new Callback() {
                @Override
                public void onSuccess() {
                    pcPg.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {
                    pcPg.setVisibility(View.GONE);
                    Log.e("ERROR", e.toString());
                    showToast(e.toString());
                }
            });
        } else {
            pcPg.setVisibility(View.GONE);
            Picasso.get().load(R.drawable.ic_raw_image).into(pcIv);
        }

        //Bank Proof Image
        if (previewModel.getBank_proof_image() != null && !previewModel.getBank_proof_image().equalsIgnoreCase("")) {
            bpPg.setVisibility(View.VISIBLE);
            Picasso.get().load(previewModel.getBank_proof_image()).fit().into(bpIv, new Callback() {
                @Override
                public void onSuccess() {
                    bpPg.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {
                    bpPg.setVisibility(View.GONE);
                    Log.e("ERROR", e.toString());
                    showToast(e.toString());
                }
            });
        } else {
            bpPg.setVisibility(View.GONE);
            Picasso.get().load(R.drawable.ic_raw_image).into(bpIv);
        }

        if (!previewModel.getCandidate_category().equalsIgnoreCase("Fresher")) {
            lastCmpExpImgLayout.setVisibility(View.VISIBLE);
            paySlipImgLayout.setVisibility(View.VISIBLE);
            paySlip2ImgLayout.setVisibility(View.VISIBLE);
            paySlip3ImgLayout.setVisibility(View.VISIBLE);
            resignMailImgLayout.setVisibility(View.VISIBLE);
            bankStmtImgLayout.setVisibility(View.GONE);

            //set last company details to pf form
            pfATextTv.setText(Html.fromHtml(String.format("I was employed in M/s <u>%s</u> and left service on <u>%s</u> prior to that, I was employed in <u>%s</u> from <u>%s</u> to <u>%s</u>.",
                    previewModel.getLastCompanyName(), "", previewModel.getLastCompanyJob(), previewModel.getLastCompanyDoj(), previewModel.getLastCompanyDol())));


            //Last Company Experience Image
            if (previewModel.getLast_company_exp_letter_image() != null && !previewModel.getLast_company_exp_letter_image().equalsIgnoreCase("")) {
                lcePg.setVisibility(View.VISIBLE);
                Picasso.get().load(previewModel.getLast_company_exp_letter_image()).fit().into(lceIv, new Callback() {
                    @Override
                    public void onSuccess() {
                        lcePg.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        lcePg.setVisibility(View.GONE);
                        Log.e("ERROR", e.toString());
                        showToast(e.toString());
                    }
                });
            } else {
                lcePg.setVisibility(View.GONE);
                Picasso.get().load(R.drawable.ic_raw_image).into(lceIv);
            }

            //Pay Slip Last Month Image
            if (previewModel.getPay_slip_exp_letter_image() != null && !previewModel.getPay_slip_exp_letter_image().equalsIgnoreCase("")) {
                paySlipPg.setVisibility(View.VISIBLE);
                Picasso.get().load(previewModel.getPay_slip_exp_letter_image()).fit().into(paySlipIv, new Callback() {
                    @Override
                    public void onSuccess() {
                        paySlipPg.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        paySlipPg.setVisibility(View.GONE);
                        Log.e("ERROR", e.toString());
                        showToast(e.toString());
                    }
                });
            } else {
                paySlipPg.setVisibility(View.GONE);
                Picasso.get().load(R.drawable.ic_raw_image).into(paySlipIv);
            }

            //Pay Slip Second Last Month Image
            if (previewModel.getPay_slip_second_last_month_image() != null && !previewModel.getPay_slip_second_last_month_image().equalsIgnoreCase("")) {
                paySlip2Pg.setVisibility(View.VISIBLE);
                Picasso.get().load(previewModel.getPay_slip_second_last_month_image()).fit().into(paySlip2Iv, new Callback() {
                    @Override
                    public void onSuccess() {
                        paySlip2Pg.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        paySlip2Pg.setVisibility(View.GONE);
                        Log.e("ERROR", e.toString());
                        showToast(e.toString());
                    }
                });
            } else {
                paySlip2Pg.setVisibility(View.GONE);
                Picasso.get().load(R.drawable.ic_raw_image).into(paySlip2Iv);
            }

            //Pay Slip Third Last Month Image
            if (previewModel.getPay_slip_third_last_month_image() != null && !previewModel.getPay_slip_third_last_month_image().equalsIgnoreCase("")) {
                paySlip3Pg.setVisibility(View.VISIBLE);
                Picasso.get().load(previewModel.getPay_slip_third_last_month_image()).fit().into(paySlip3Iv, new Callback() {
                    @Override
                    public void onSuccess() {
                        paySlip3Pg.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        paySlip3Pg.setVisibility(View.GONE);
                        Log.e("ERROR", e.toString());
                        showToast(e.toString());
                    }
                });
            } else {
                paySlip3Pg.setVisibility(View.GONE);
                Picasso.get().load(R.drawable.ic_raw_image).into(paySlip3Iv);
            }

            //Resign Mail Image
            if (previewModel.getResign_mail_image() != null && !previewModel.getResign_mail_image().equalsIgnoreCase("")) {
                resignMailPg.setVisibility(View.VISIBLE);
                Picasso.get().load(previewModel.getResign_mail_image()).fit().into(resignMailIv, new Callback() {
                    @Override
                    public void onSuccess() {
                        resignMailPg.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        resignMailPg.setVisibility(View.GONE);
                        Log.e("ERROR", e.toString());
                        showToast(e.toString());
                    }
                });
            } else {
                resignMailPg.setVisibility(View.GONE);
                Picasso.get().load(R.drawable.ic_raw_image).into(resignMailIv);
            }

            //Bank Statement Image
            /*if (previewModel.getBank_stmt_last_3_mth_image() != null && !previewModel.getBank_stmt_last_3_mth_image().equalsIgnoreCase("")) {
                bankStmtPg.setVisibility(View.VISIBLE);
                Picasso.get().load(previewModel.getBank_stmt_last_3_mth_image()).fit().into(bankStmtIv, new Callback() {
                    @Override
                    public void onSuccess() {
                        bankStmtPg.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        bankStmtPg.setVisibility(View.GONE);
                        Log.e("ERROR", e.toString());
                        showToast(e.toString());
                    }
                });
            } else {
                bankStmtPg.setVisibility(View.GONE);
                Picasso.get().load(R.drawable.ic_raw_image).into(bankStmtIv);
            }*/

        } else {
            lastCmpExpImgLayout.setVisibility(View.GONE);
            paySlipImgLayout.setVisibility(View.GONE);
            paySlip2ImgLayout.setVisibility(View.GONE);
            paySlip3ImgLayout.setVisibility(View.GONE);
            resignMailImgLayout.setVisibility(View.GONE);
            bankStmtImgLayout.setVisibility(View.GONE);
        }

        //Vaccine Certificate Image
        if (previewModel.getVaccine_certificate_image() != null && !previewModel.getVaccine_certificate_image().equalsIgnoreCase("")) {
            vaccineCertificatePg.setVisibility(View.VISIBLE);
            Picasso.get().load(previewModel.getVaccine_certificate_image()).fit().into(vaccineCertificateIv, new Callback() {
                @Override
                public void onSuccess() {
                    vaccineCertificatePg.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {
                    vaccineCertificatePg.setVisibility(View.GONE);
                    Log.e("ERROR", e.toString());
                    showToast(e.toString());
                }
            });
        } else {
            vaccineCertificatePg.setVisibility(View.GONE);
            Picasso.get().load(R.drawable.ic_raw_image).into(vaccineCertificateIv);
        }

        //set qualification images
        //10 Image
        if (previewModel.getImage_10() != null && !previewModel.getImage_10().equalsIgnoreCase("")) {
            sImgLayout.setVisibility(View.VISIBLE);
            sPg.setVisibility(View.VISIBLE);
            Picasso.get().load(previewModel.getImage_10()).fit().into(sIv, new Callback() {
                @Override
                public void onSuccess() {
                    sPg.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {
                    sPg.setVisibility(View.GONE);
                    Log.e("ERROR", e.toString());
                    showToast(e.toString());
                }
            });
        } else {
            sPg.setVisibility(View.GONE);
            Picasso.get().load(R.drawable.ic_raw_image).into(sIv);
        }

        //12 Image
        if (previewModel.getImage_12() != null && !previewModel.getImage_12().equalsIgnoreCase("")) {
            ssImgLayout.setVisibility(View.VISIBLE);
            ssPg.setVisibility(View.VISIBLE);
            Picasso.get().load(previewModel.getImage_12()).fit().into(ssIv, new Callback() {
                @Override
                public void onSuccess() {
                    ssPg.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {
                    ssPg.setVisibility(View.GONE);
                    Log.e("ERROR", e.toString());
                    showToast(e.toString());
                }
            });
        } else {
            ssPg.setVisibility(View.GONE);
            Picasso.get().load(R.drawable.ic_raw_image).into(ssIv);
        }

        //Graduation Image
        if (previewModel.getImage_g() != null && !previewModel.getImage_g().equalsIgnoreCase("")) {
            gImgLayout.setVisibility(View.VISIBLE);
            gPg.setVisibility(View.VISIBLE);
            Picasso.get().load(previewModel.getImage_g()).fit().into(gIv, new Callback() {
                @Override
                public void onSuccess() {
                    gPg.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {
                    gPg.setVisibility(View.GONE);
                    Log.e("ERROR", e.toString());
                    showToast(e.toString());
                }
            });
        } else {
            gPg.setVisibility(View.GONE);
            Picasso.get().load(R.drawable.ic_raw_image).into(gIv);
        }

        //Post Graduation Image
        if (previewModel.getImage_pg() != null && !previewModel.getImage_pg().equalsIgnoreCase("")) {
            pgImgLayout.setVisibility(View.VISIBLE);
            pgPg.setVisibility(View.VISIBLE);
            Picasso.get().load(previewModel.getImage_pg()).fit().into(pgIv, new Callback() {
                @Override
                public void onSuccess() {
                    pgPg.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {
                    pgPg.setVisibility(View.GONE);
                    Log.e("ERROR", e.toString());
                    showToast(e.toString());
                }
            });
        } else {
            pgPg.setVisibility(View.GONE);
            Picasso.get().load(R.drawable.ic_raw_image).into(pgIv);
        }

        //Other Qualification Image
        if (previewModel.getImage_other() != null && !previewModel.getImage_other().equalsIgnoreCase("")) {
            otherImgLayout.setVisibility(View.VISIBLE);
            otherPg.setVisibility(View.VISIBLE);
            Picasso.get().load(previewModel.getImage_other()).fit().into(otherIv, new Callback() {
                @Override
                public void onSuccess() {
                    otherPg.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {
                    otherPg.setVisibility(View.GONE);
                    Log.e("ERROR", e.toString());
                    showToast(e.toString());
                }
            });
        } else {
            otherPg.setVisibility(View.GONE);
            Picasso.get().load(R.drawable.ic_raw_image).into(otherIv);
        }


        //set values to terms and condition layout
        signNameTv.setText(Html.fromHtml(String.format("(<u>%s</u>)", previewModel.getName())));
    }

    private void showToast(String message) {
        Toast.makeText(PreviewActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, intentFilter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }

    private void initialization() {
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        backBtn = findViewById(R.id.backBtn);
        downloadBtn = findViewById(R.id.downloadBtn);

        mainLayout = findViewById(R.id.mainLayout);

        //Joining Layout views
        joiningLayout = findViewById(R.id.joiningLayout);
        companyTv = findViewById(R.id.companyTv);
        nameTv = findViewById(R.id.nameTv);
        fatherNameTv = findViewById(R.id.fatherNameTv);
        dojTv = findViewById(R.id.dojTv);
        dobTv = findViewById(R.id.dobTv);
        genderTv = findViewById(R.id.genderTv);
        userImgView = findViewById(R.id.userImgView);
        userImgPg = findViewById(R.id.userImgPg);

        bloodGroupTv = findViewById(R.id.bloodGroupTv);
        maritalStatusTv = findViewById(R.id.maritalStatusTv);
        departmentTv = findViewById(R.id.departmentTv);
        designationTv = findViewById(R.id.designationTv);
        gradeTv = findViewById(R.id.gradeTv);
        branchTv = findViewById(R.id.branchTv);
        zoneTv = findViewById(R.id.zoneTv);
        emailTv = findViewById(R.id.emailTv);
        contactNoTv = findViewById(R.id.contactNoTv);
        permanentAddressTv = findViewById(R.id.permanentAddressTv);
        currentAddressTv = findViewById(R.id.currentAddressTv);
        bankAccountNoTv = findViewById(R.id.bankAccountNoTv);
        ifscCodeTv = findViewById(R.id.ifscCodeTv);
        uanNoTv = findViewById(R.id.uanNoTv);
        esicNoTv = findViewById(R.id.esicNoTv);
        aadharNoTv = findViewById(R.id.aadharNoTv);
        panNoTv = findViewById(R.id.panNoTv);
        emergencyNameTv = findViewById(R.id.emergencyNameTv);
        emergencyNoTv = findViewById(R.id.emergencyNoTv);
        emergencyRelationTv = findViewById(R.id.emergencyRelationTv);
        //nominee fields

        fatherNameRow = findViewById(R.id.fatherNameRow);
        motherNameRow = findViewById(R.id.motherNameRow);
        spouseNameRow = findViewById(R.id.spouseNameRow);
        guardianNameRow = findViewById(R.id.guardianNameRow);
        siblingNameRow = findViewById(R.id.siblingNameRow);
        childOneNameRow = findViewById(R.id.childOneNameRow);
        childTwoNameRow = findViewById(R.id.childTwoNameRow);
        fatherNameRowView = findViewById(R.id.fatherNameRowView);
        motherNameRowView = findViewById(R.id.motherNameRowView);
        spouseNameRowView = findViewById(R.id.spouseNameRowView);
        guardianNameRowView = findViewById(R.id.guardianNameRowView);
        siblingNameRowView = findViewById(R.id.siblingNameRowView);
        childOneNameRowView = findViewById(R.id.childOneNameRowView);
        childTwoNameRowView = findViewById(R.id.childTwoNameRowView);

        fatherNameRow1 = findViewById(R.id.fatherNameRow1);
        motherNameRow1 = findViewById(R.id.motherNameRow1);
        spouseNameRow1 = findViewById(R.id.spouseNameRow1);
        guardianNameRow1 = findViewById(R.id.guardianNameRow1);
        siblingNameRow1 = findViewById(R.id.siblingNameRow1);
        childOneNameRow1 = findViewById(R.id.childOneNameRow1);
        childTwoNameRow1 = findViewById(R.id.childTwoNameRow1);
        fatherNameRowView1 = findViewById(R.id.fatherNameRowView1);
        motherNameRowView1 = findViewById(R.id.motherNameRowView1);
        spouseNameRowView1 = findViewById(R.id.spouseNameRowView1);
        guardianNameRowView1 = findViewById(R.id.guardianNameRowView1);
        siblingNameRowView1 = findViewById(R.id.siblingNameRowView1);
        childOneNameRowView1 = findViewById(R.id.childOneNameRowView1);
        childTwoNameRowView1 = findViewById(R.id.childTwoNameRowView1);


        nFatherNameTv = findViewById(R.id.nFatherNameTv);
        nFatherRelationTv = findViewById(R.id.nFatherRelationTv);
        nFatherDobTv = findViewById(R.id.nFatherDobTv);
        nFatherAmountTv = findViewById(R.id.nFatherAmountTv);
        nMotherNameTv = findViewById(R.id.nMotherNameTv);
        nMotherRelationTv = findViewById(R.id.nMotherRelationTv);
        nMotherDobTv = findViewById(R.id.nMotherDobTv);
        nMotherAmountTv = findViewById(R.id.nMotherAmountTv);
        nWifeNameTv = findViewById(R.id.nWifeNameTv);
        nWifeRelationTv = findViewById(R.id.nWifeRelationTv);
        nWifeDobTv = findViewById(R.id.nWifeDobTv);
        nWifeAmountTv = findViewById(R.id.nWifeAmountTv);
        nGuardianNameTv = findViewById(R.id.nGuardianNameTv);
        nGuardianRelationTv = findViewById(R.id.nGuardianRelationTv);
        nGuardianDobTv = findViewById(R.id.nGuardianDobTv);
        nGuardianAmountTv = findViewById(R.id.nGuardianAmountTv);
        nSiblingNameTv = findViewById(R.id.nSiblingNameTv);
        nSiblingRelationTv = findViewById(R.id.nSiblingRelationTv);
        nSiblingDobTv = findViewById(R.id.nSiblingDobTv);
        nSiblingAmountTv = findViewById(R.id.nSiblingAmountTv);
        nChildOneNameTv = findViewById(R.id.nChildOneNameTv);
        nChildOneRelationTv = findViewById(R.id.nChildOneRelationTv);
        nChildOneDobTv = findViewById(R.id.nChildOneDobTv);
        nChildOneAmountTv = findViewById(R.id.nChildOneAmountTv);
        nChildTwoNameTv = findViewById(R.id.nChildTwoNameTv);
        nChildTwoRelationTv = findViewById(R.id.nChildTwoRelationTv);
        nChildTwoDobTv = findViewById(R.id.nChildTwoDobTv);
        nChildTwoAmountTv = findViewById(R.id.nChildTwoAmountTv);

        todayDateTv = findViewById(R.id.todayDateTv);

        empSignImgView = findViewById(R.id.empSignImgView);
        empImgPg = findViewById(R.id.empImgPg);

        //PF Layout views
        pfLayout = findViewById(R.id.pfLayout);
        pfTextTv = findViewById(R.id.pfTextTv);
        pfText2 = findViewById(R.id.pfText2);
        pfATextTv = findViewById(R.id.pfATextTv);
        pfBTextTv = findViewById(R.id.pfBTextTv);
        pfText3 = findViewById(R.id.pfText3);
        pfTodayDate1Tv = findViewById(R.id.pfTodayDate1Tv);
        pfTodayDate2Tv = findViewById(R.id.pfTodayDate2Tv);
        empSignImgView2 = findViewById(R.id.empSignImgView2);
        empImgPg2 = findViewById(R.id.empImgPg2);
        hrSignImgView = findViewById(R.id.hrSignImgView);
        hrImgPg = findViewById(R.id.hrImgPg);

        //Form G Layout views
        formGLayout = findViewById(R.id.formGLayout);
        formG2Layout = findViewById(R.id.formG2Layout);
        formGText0 = findViewById(R.id.formGText0);
        formGText1 = findViewById(R.id.formGText1);
        statementTv = findViewById(R.id.statementTv);
        formGDatePlace1Tv = findViewById(R.id.formGDatePlace1Tv);
        formGDatePlace2Tv = findViewById(R.id.formGDatePlace2Tv);
        formGDate1Tv = findViewById(R.id.formGDate1Tv);
        formGDate2Tv = findViewById(R.id.formGDate2Tv);

        //nominee fields
        nFatherNameTv1 = findViewById(R.id.nFatherNameTv1);
        nFatherRelationTv1 = findViewById(R.id.nFatherRelationTv1);
        nFatherDobTv1 = findViewById(R.id.nFatherDobTv1);
        nFatherAmountTv1 = findViewById(R.id.nFatherAmountTv1);
        nMotherNameTv1 = findViewById(R.id.nMotherNameTv1);
        nMotherRelationTv1 = findViewById(R.id.nMotherRelationTv1);
        nMotherDobTv1 = findViewById(R.id.nMotherDobTv1);
        nMotherAmountTv1 = findViewById(R.id.nMotherAmountTv1);
        nWifeNameTv1 = findViewById(R.id.nWifeNameTv1);
        nWifeRelationTv1 = findViewById(R.id.nWifeRelationTv1);
        nWifeDobTv1 = findViewById(R.id.nWifeDobTv1);
        nWifeAmountTv1 = findViewById(R.id.nWifeAmountTv1);
        nGuardianNameTv1 = findViewById(R.id.nGuardianNameTv1);
        nGuardianRelationTv1 = findViewById(R.id.nGuardianRelationTv1);
        nGuardianDobTv1 = findViewById(R.id.nGuardianDobTv1);
        nGuardianAmountTv1 = findViewById(R.id.nGuardianAmountTv1);
        nSiblingNameTv1 = findViewById(R.id.nSiblingNameTv1);
        nSiblingRelationTv1 = findViewById(R.id.nSiblingRelationTv1);
        nSiblingDobTv1 = findViewById(R.id.nSiblingDobTv1);
        nSiblingAmountTv1 = findViewById(R.id.nSiblingAmountTv1);
        nChildOneNameTv1 = findViewById(R.id.nChildOneNameTv1);
        nChildOneRelationTv1 = findViewById(R.id.nChildOneRelationTv1);
        nChildOneDobTv1 = findViewById(R.id.nChildOneDobTv1);
        nChildOneAmountTv1 = findViewById(R.id.nChildOneAmountTv1);
        nChildTwoNameTv1 = findViewById(R.id.nChildTwoNameTv1);
        nChildTwoRelationTv1 = findViewById(R.id.nChildTwoRelationTv1);
        nChildTwoDobTv1 = findViewById(R.id.nChildTwoDobTv1);
        nChildTwoAmountTv1 = findViewById(R.id.nChildTwoAmountTv1);

        empSignImgView3 = findViewById(R.id.empSignImgView3);
        empImgPg3 = findViewById(R.id.empImgPg3);
        hrSignImgView2 = findViewById(R.id.hrSignImgView2);
        hrImgPg2 = findViewById(R.id.hrImgPg2);

        //Privacy Layout
        privacyTermsLayout = findViewById(R.id.privacyTermsLayout);
        privacyTerms2Layout = findViewById(R.id.privacyTerms2Layout);
        nameEmpCodeTv = findViewById(R.id.nameEmpCodeTv);
        privacyDateTv = findViewById(R.id.privacyDateTv);

        empSignImgView4 = findViewById(R.id.empSignImgView4);
        empImgPg4 = findViewById(R.id.empImgPg4);

        //Employee Non Disclosure Layout
        nonDisclosureLayout = findViewById(R.id.nonDisclosureLayout);
        nonDisclosure2Layout = findViewById(R.id.nonDisclosure2Layout);
        nonDisclosure3Layout = findViewById(R.id.nonDisclosure3Layout);
        nonDisclosure4Layout = findViewById(R.id.nonDisclosure4Layout);
        nonDisclosure5Layout = findViewById(R.id.nonDisclosure5Layout);
        nonDisclosure6Layout = findViewById(R.id.nonDisclosure6Layout);
        nonDisclosure7Layout = findViewById(R.id.nonDisclosure7Layout);
        nonDisclosure8Layout = findViewById(R.id.nonDisclosure8Layout);
        nonDisclosureText0 = findViewById(R.id.nonDisclosureText0);
        nonDisclosureText1 = findViewById(R.id.nonDisclosureText1);

        partyBTextTv = findViewById(R.id.partyBTextTv);
        partyBEmpNoTv = findViewById(R.id.partyBEmpNoTv);
        empSignImgView5 = findViewById(R.id.empSignImgView5);
        empImgPg5 = findViewById(R.id.empImgPg5);

        hrSignImgView3 = findViewById(R.id.hrSignImgView3);
        hrImgPg3 = findViewById(R.id.hrImgPg3);
        hrSignImgTv3 = findViewById(R.id.hrSignImgTv3);
        companyNameTextTv = findViewById(R.id.companyNameTextTv);


        //Declaration Layout Views
        declarationFormLayout = findViewById(R.id.declarationFormLayout);
        decInstructionLayout = findViewById(R.id.decInstructionLayout);
        dInsuranceNoTv = findViewById(R.id.dInsuranceNoTv);
        dNameTv = findViewById(R.id.dNameTv);
        dFatherHusbandNameTv = findViewById(R.id.dFatherHusbandNameTv);
        dMarriedTv = findViewById(R.id.dMarriedTv);
        dDayTv = findViewById(R.id.dDayTv);
        dMonthTv = findViewById(R.id.dMonthTv);
        dYearTv = findViewById(R.id.dYearTv);
        dSexTv = findViewById(R.id.dSexTv);
        dPresentAddressTv = findViewById(R.id.dPresentAddressTv);
        dPermanentAddressTv = findViewById(R.id.dPermanentAddressTv);
        dBranchOfficeTv = findViewById(R.id.dBranchOfficeTv);
        dDispensaryTv = findViewById(R.id.dDispensaryTv);
        dAppointmentDayTv = findViewById(R.id.dAppointmentDayTv);
        dAppointmentMonthTv = findViewById(R.id.dAppointmentMonthTv);
        dAppointmentYearTv = findViewById(R.id.dAppointmentYearTv);

        //nominee fields
        fatherNameRow2 = findViewById(R.id.fatherNameRow2);
        motherNameRow2 = findViewById(R.id.motherNameRow2);
        spouseNameRow2 = findViewById(R.id.spouseNameRow2);
        guardianNameRow2 = findViewById(R.id.guardianNameRow2);
        siblingNameRow2 = findViewById(R.id.siblingNameRow2);
        childOneNameRow2 = findViewById(R.id.childOneNameRow2);
        childTwoNameRow2 = findViewById(R.id.childTwoNameRow2);
        fatherNameRowView2 = findViewById(R.id.fatherNameRowView2);
        motherNameRowView2 = findViewById(R.id.motherNameRowView2);
        spouseNameRowView2 = findViewById(R.id.spouseNameRowView2);
        guardianNameRowView2 = findViewById(R.id.guardianNameRowView2);
        siblingNameRowView2 = findViewById(R.id.siblingNameRowView2);
        childOneNameRowView2 = findViewById(R.id.childOneNameRowView2);
        childTwoNameRowView2 = findViewById(R.id.childTwoNameRowView2);
        nFatherNameTv2 = findViewById(R.id.nFatherNameTv2);
        nFatherRelationTv2 = findViewById(R.id.nFatherRelationTv2);
        nFatherAddressTv2 = findViewById(R.id.nFatherAddressTv2);
        nMotherNameTv2 = findViewById(R.id.nMotherNameTv2);
        nMotherRelationTv2 = findViewById(R.id.nMotherRelationTv2);
        nMotherAddressTv2 = findViewById(R.id.nMotherAddressTv2);
        nWifeNameTv2 = findViewById(R.id.nWifeNameTv2);
        nWifeRelationTv2 = findViewById(R.id.nWifeRelationTv2);
        nWifeAddressTv2 = findViewById(R.id.nWifeAddressTv2);
        nGuardianNameTv2 = findViewById(R.id.nGuardianNameTv2);
        nGuardianRelationTv2 = findViewById(R.id.nGuardianRelationTv2);
        nGuardianAddressTv2 = findViewById(R.id.nGuardianAddressTv2);
        nSiblingNameTv2 = findViewById(R.id.nSiblingNameTv2);
        nSiblingRelationTv2 = findViewById(R.id.nSiblingRelationTv2);
        nSiblingAddressTv2 = findViewById(R.id.nSiblingAddressTv2);
        nChildOneNameTv2 = findViewById(R.id.nChildOneNameTv2);
        nChildOneRelationTv2 = findViewById(R.id.nChildOneRelationTv2);
        nChildOneAddressTv2 = findViewById(R.id.nChildOneAddressTv2);
        nChildTwoNameTv2 = findViewById(R.id.nChildTwoNameTv2);
        nChildTwoRelationTv2 = findViewById(R.id.nChildTwoRelationTv2);
        nChildTwoAddressTv2 = findViewById(R.id.nChildTwoAddressTv2);

        //nominee field 2
        fatherNameRow3 = findViewById(R.id.fatherNameRow3);
        motherNameRow3 = findViewById(R.id.motherNameRow3);
        spouseNameRow3 = findViewById(R.id.spouseNameRow3);
        guardianNameRow3 = findViewById(R.id.guardianNameRow3);
        siblingNameRow3 = findViewById(R.id.siblingNameRow3);
        childOneNameRow3 = findViewById(R.id.childOneNameRow3);
        childTwoNameRow3 = findViewById(R.id.childTwoNameRow3);
        fatherNameRowView3 = findViewById(R.id.fatherNameRowView3);
        motherNameRowView3 = findViewById(R.id.motherNameRowView3);
        spouseNameRowView3 = findViewById(R.id.spouseNameRowView3);
        guardianNameRowView3 = findViewById(R.id.guardianNameRowView3);
        siblingNameRowView3 = findViewById(R.id.siblingNameRowView3);
        childOneNameRowView3 = findViewById(R.id.childOneNameRowView3);
        childTwoNameRowView3 = findViewById(R.id.childTwoNameRowView3);
        nFatherNameTv3 = findViewById(R.id.nFatherNameTv3);
        nFatherRelationTv3 = findViewById(R.id.nFatherRelationTv3);
        nFatherDobTv3 = findViewById(R.id.nFatherDobTv3);
        nMotherNameTv3 = findViewById(R.id.nMotherNameTv3);
        nMotherRelationTv3 = findViewById(R.id.nMotherRelationTv3);
        nMotherDobTv3 = findViewById(R.id.nMotherDobTv3);
        nWifeNameTv3 = findViewById(R.id.nWifeNameTv3);
        nWifeRelationTv3 = findViewById(R.id.nWifeRelationTv3);
        nWifeDobTv3 = findViewById(R.id.nWifeDobTv3);
        nGuardianNameTv3 = findViewById(R.id.nGuardianNameTv3);
        nGuardianRelationTv3 = findViewById(R.id.nGuardianRelationTv3);
        nGuardianDobTv3 = findViewById(R.id.nGuardianDobTv3);
        nSiblingNameTv3 = findViewById(R.id.nSiblingNameTv3);
        nSiblingRelationTv3 = findViewById(R.id.nSiblingRelationTv3);
        nSiblingDobTv3 = findViewById(R.id.nSiblingDobTv3);
        nChildOneNameTv3 = findViewById(R.id.nChildOneNameTv3);
        nChildOneRelationTv3 = findViewById(R.id.nChildOneRelationTv3);
        nChildOneDobTv3 = findViewById(R.id.nChildOneDobTv3);
        nChildTwoNameTv3 = findViewById(R.id.nChildTwoNameTv3);
        nChildTwoRelationTv3 = findViewById(R.id.nChildTwoRelationTv3);
        nChildTwoDobTv3 = findViewById(R.id.nChildTwoDobTv3);

        //nominee field 3
        fatherNameRow4 = findViewById(R.id.fatherNameRow4);
        motherNameRow4 = findViewById(R.id.motherNameRow4);
        spouseNameRow4 = findViewById(R.id.spouseNameRow4);
        guardianNameRow4 = findViewById(R.id.guardianNameRow4);
        siblingNameRow4 = findViewById(R.id.siblingNameRow4);
        childOneNameRow4 = findViewById(R.id.childOneNameRow4);
        childTwoNameRow4 = findViewById(R.id.childTwoNameRow4);
        fatherNameRowView4 = findViewById(R.id.fatherNameRowView4);
        motherNameRowView4 = findViewById(R.id.motherNameRowView4);
        spouseNameRowView4 = findViewById(R.id.spouseNameRowView4);
        guardianNameRowView4 = findViewById(R.id.guardianNameRowView4);
        siblingNameRowView4 = findViewById(R.id.siblingNameRowView4);
        childOneNameRowView4 = findViewById(R.id.childOneNameRowView4);
        childTwoNameRowView4 = findViewById(R.id.childTwoNameRowView4);
        nFatherNameTv4 = findViewById(R.id.nFatherNameTv4);
        nFatherRelationTv4 = findViewById(R.id.nFatherRelationTv4);
        nFatherDobTv4 = findViewById(R.id.nFatherDobTv4);
        nMotherNameTv4 = findViewById(R.id.nMotherNameTv4);
        nMotherRelationTv4 = findViewById(R.id.nMotherRelationTv4);
        nMotherDobTv4 = findViewById(R.id.nMotherDobTv4);
        nWifeNameTv4 = findViewById(R.id.nWifeNameTv4);
        nWifeRelationTv4 = findViewById(R.id.nWifeRelationTv4);
        nWifeDobTv4 = findViewById(R.id.nWifeDobTv4);
        nGuardianNameTv4 = findViewById(R.id.nGuardianNameTv4);
        nGuardianRelationTv4 = findViewById(R.id.nGuardianRelationTv4);
        nGuardianDobTv4 = findViewById(R.id.nGuardianDobTv4);
        nSiblingNameTv4 = findViewById(R.id.nSiblingNameTv4);
        nSiblingRelationTv4 = findViewById(R.id.nSiblingRelationTv4);
        nSiblingDobTv4 = findViewById(R.id.nSiblingDobTv4);
        nChildOneNameTv4 = findViewById(R.id.nChildOneNameTv4);
        nChildOneRelationTv4 = findViewById(R.id.nChildOneRelationTv4);
        nChildOneDobTv4 = findViewById(R.id.nChildOneDobTv4);
        nChildTwoNameTv4 = findViewById(R.id.nChildTwoNameTv4);
        nChildTwoRelationTv4 = findViewById(R.id.nChildTwoRelationTv4);
        nChildTwoDobTv4 = findViewById(R.id.nChildTwoDobTv4);

        dAppointmentDateTv = findViewById(R.id.dAppointmentDateTv);
        declarationFromTv1 = findViewById(R.id.declarationFromTv1);
        declarationFromTv2 = findViewById(R.id.declarationFromTv2);

        hrSignImgView4 = findViewById(R.id.hrSignImgView4);
        hrImgPg4 = findViewById(R.id.hrImgPg4);

        empSignImgView6 = findViewById(R.id.empSignImgView6);
        empImgPg6 = findViewById(R.id.empImgPg6);

        empSignImgView7 = findViewById(R.id.empSignImgView7);
        empImgPg7 = findViewById(R.id.empImgPg7);

        //FTA Layout views
        ftaLayout = findViewById(R.id.ftaLayout);
        fta2Layout = findViewById(R.id.fta2Layout);
        fta3Layout = findViewById(R.id.fta3Layout);
        fta4Layout = findViewById(R.id.fta4Layout);
        fta5Layout = findViewById(R.id.fta5Layout);
        ftaDateTv = findViewById(R.id.ftaDateTv);
        ftaTextTv = findViewById(R.id.ftaTextTv);
        ftaPoint1Tv = findViewById(R.id.ftaPoint1Tv);
        ftaPoint2Tv = findViewById(R.id.ftaPoint2Tv);

        hrSignNameTv = findViewById(R.id.hrSignNameTv);
        hrSignImgView5 = findViewById(R.id.hrSignImgView5);
        hrImgPg5 = findViewById(R.id.hrImgPg5);

        empSignImgView9 = findViewById(R.id.empSignImgView9);
        empImgPg9 = findViewById(R.id.empImgPg9);
        empSignImgView10 = findViewById(R.id.empSignImgView10);
        empImgPg10 = findViewById(R.id.empImgPg10);
        empNameTv = findViewById(R.id.empNameTv);

        hrSignName1Tv = findViewById(R.id.hrSignName1Tv);
        hrSignImgView6 = findViewById(R.id.hrSignImgView6);
        hrImgPg6 = findViewById(R.id.hrImgPg6);

        tAndCTextTv1 = findViewById(R.id.tAndCTextTv1);
        tAndCTextTv4 = findViewById(R.id.tAndCTextTv4);
        tAndCTextTv9 = findViewById(R.id.tAndCTextTv9);
        tAndCTextTv15 = findViewById(R.id.tAndCTextTv15);
        tAndCTextTv17 = findViewById(R.id.tAndCTextTv17);
        tAndCTextTv18 = findViewById(R.id.tAndCTextTv18);
        tAndCTextTv19 = findViewById(R.id.tAndCTextTv19);
        tAndCTextTv20A = findViewById(R.id.tAndCTextTv20A);
        ftaHerebyNameTv = findViewById(R.id.ftaHerebyNameTv);
        ftaHerebySignatureTv = findViewById(R.id.ftaHerebySignatureTv);
        ftaHerebyAddressTv = findViewById(R.id.ftaHerebyAddressTv);
        ftaHerebyDateTv = findViewById(R.id.ftaHerebyDateTv);
        ftaHerebyPlaceTv = findViewById(R.id.ftaHerebyPlaceTv);
        ftaCompanyNameTv = findViewById(R.id.ftaCompanyNameTv);
        ftaCompanyNameTv2 = findViewById(R.id.ftaCompanyNameTv2);

        //Image Layout views
        passportImgLayout = findViewById(R.id.passportImgLayout);
        empSignImgLayout = findViewById(R.id.empSignImgLayout);
        acImgLayout = findViewById(R.id.acImgLayout);
        acBImgLayout = findViewById(R.id.acBImgLayout);
        pcImgLayout = findViewById(R.id.pcImgLayout);
        bpImgLayout = findViewById(R.id.bpImgLayout);
        lastCmpExpImgLayout = findViewById(R.id.lastCmpExpImgLayout);
        paySlipImgLayout = findViewById(R.id.paySlipImgLayout);
        paySlip2ImgLayout = findViewById(R.id.paySlip2ImgLayout);
        paySlip3ImgLayout = findViewById(R.id.paySlip3ImgLayout);
        resignMailImgLayout = findViewById(R.id.resignMailImgLayout);
        bankStmtImgLayout = findViewById(R.id.bankStmtImgLayout);
        offerLetterImgLayout = findViewById(R.id.offerLetterImgLayout);

        passportIv = findViewById(R.id.passportIv);
        empSignIv = findViewById(R.id.empSignIv);
        acIv = findViewById(R.id.acIv);
        acBIv = findViewById(R.id.acBIv);
        pcIv = findViewById(R.id.pcIv);
        bpIv = findViewById(R.id.bpIv);
        lceIv = findViewById(R.id.lceIv);
        paySlipIv = findViewById(R.id.paySlipIv);
        paySlip2Iv = findViewById(R.id.paySlip2Iv);
        paySlip3Iv = findViewById(R.id.paySlip3Iv);
        resignMailIv = findViewById(R.id.resignMailIv);
        bankStmtIv = findViewById(R.id.bankStmtIv);
        offerLetterIv = findViewById(R.id.offerLetterIv);

        passportPg = findViewById(R.id.passportPg);
        empSignPg = findViewById(R.id.empSignPg);
        acPg = findViewById(R.id.acPg);
        acBPg = findViewById(R.id.acBPg);
        pcPg = findViewById(R.id.pcPg);
        bpPg = findViewById(R.id.bpPg);
        lcePg = findViewById(R.id.lcePg);
        paySlipPg = findViewById(R.id.paySlipPg);
        paySlip2Pg = findViewById(R.id.paySlip2Pg);
        paySlip3Pg = findViewById(R.id.paySlip3Pg);
        resignMailPg = findViewById(R.id.resignMailPg);
        bankStmtPg = findViewById(R.id.bankStmtPg);
        offerLetterPg = findViewById(R.id.offerLetterPg);

        //Vaccine Certificate
        vaccineCertificateImgLayout = findViewById(R.id.vaccineCertificateImgLayout);
        vaccineCertificateIv = findViewById(R.id.vaccineCertificateIv);
        vaccineCertificatePg = findViewById(R.id.vaccineCertificatePg);

        //qualification image views
        sImgLayout = findViewById(R.id.sImgLayout);
        ssImgLayout = findViewById(R.id.ssImgLayout);
        gImgLayout = findViewById(R.id.gImgLayout);
        pgImgLayout = findViewById(R.id.pgImgLayout);
        otherImgLayout = findViewById(R.id.otherImgLayout);
        sIv = findViewById(R.id.sIv);
        ssIv = findViewById(R.id.ssIv);
        gIv = findViewById(R.id.gIv);
        pgIv = findViewById(R.id.pgIv);
        otherIv = findViewById(R.id.otherIv);
        sPg = findViewById(R.id.sPg);
        ssPg = findViewById(R.id.ssPg);
        gPg = findViewById(R.id.gPg);
        pgPg = findViewById(R.id.pgPg);
        otherPg = findViewById(R.id.otherPg);

        //terms and condition layout
        selfDeclarationLayout = findViewById(R.id.selfDeclarationLayout);
        empSignImgView8 = findViewById(R.id.empSignImgView8);
        empImgPg8 = findViewById(R.id.empImgPg8);
        signNameTv = findViewById(R.id.signNameTv);
        signNameTv2 = findViewById(R.id.signNameTv2);

        //permission array
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions13 = new String[]{Manifest.permission.READ_MEDIA_IMAGES};

        loadingDialog = new LoadingDialog(this);
    }
}