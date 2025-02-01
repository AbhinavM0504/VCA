package com.vivo.vivorajonboarding;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.foysaldev.cropper.CropImage;
import com.foysaldev.cropper.CropImageView;
import com.vivo.vivorajonboarding.common.NetworkChangeListener;
import com.vivo.vivorajonboarding.constants.Prevalent;
import com.vivo.vivorajonboarding.constants.URLs;
import com.vivo.vivorajonboarding.model.DocumentModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DocumentActivity extends AppCompatActivity {

    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    //views
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageButton backBtn;

    //Passport Image Fields
    private LinearLayout passportImgLayout;
    private ImageView passportImageIv;
    private ProgressBar passportPg;
    private LinearLayout passportImgPdfLayout;
    private TextView passportImgPdfLinkTv;
    private ImageButton passportImgPdfViewBtn;
    private Button addPassportImgBtn, uploadPassportImgBtn;

    //Employee Sign Image Fields
    private LinearLayout empSignImgLayout;
    private ImageView employeeSignImageIv;
    private ProgressBar empSignPg;
    private LinearLayout employeeSignImgPdfLayout;
    private TextView employeeSignImgPdfLinkTv;
    private ImageButton employeeSignImgPdfViewBtn;
    private Button addEmployeeSignImgBtn, uploadEmployeeSignImgBtn;

    //Aadhar Card Image Fields
    private LinearLayout aadharCardImgLayout, aadharCardBackImgLayout;
    private ImageView aadharCardImageIv, aadharCardBackImageIv;
    private ProgressBar aadharCardPg, aadharCardBackPg;
    private LinearLayout aadharCardImgPdfLayout, aadharCardBackImgPdfLayout;
    private TextView aadharCardImgPdfLinkTv, aadharCardBackImgPdfLinkTv;
    private ImageButton aadharCardImgPdfViewBtn, aadharCardBackImgPdfViewBtn;
    private Button addAadharCardImgBtn, addAadharCardBackImgBtn, uploadAadharCardImgBtn, uploadAadharCardBackImgBtn;

    //PAN Card Image Fields
    private LinearLayout panCardImgLayout;
    private ImageView panCardImageIv;
    private ProgressBar panCardPg;
    private LinearLayout panCardImgPdfLayout;
    private TextView panCardImgPdfLinkTv;
    private ImageButton panCardImgPdfViewBtn;
    private Button addPanCardImgBtn, uploadPanCardImgBtn;

    //Bank Proof Image Fields
    private LinearLayout bankProofImgLayout;
    private ImageView bankProofImageIv;
    private ProgressBar bankProofPg;
    private LinearLayout bankProofImgPdfLayout;
    private TextView bankProofImgPdfLinkTv;
    private ImageButton bankProofImgPdfViewBtn;
    private Button addBankProofImgBtn, uploadBankProofImgBtn;


    //Last Company Exp Letter Image Fields
    private LinearLayout lastCmpExpImgLayout;
    private ImageView lastCpExpLtrImageIv;
    private ProgressBar lastCmpExpPg;
    private LinearLayout lastCpExpLtrImgPdfLayout;
    private TextView lastCpExpLtrImgPdfLinkTv;
    private ImageButton lastCpExpLtrImgPdfViewBtn;
    private Button addLastCpExpLtrImgBtn, uploadLastCpExpLtrImgBtn;

    //Pay Slip Image Fields
    private LinearLayout paySlipImgLayout, paySlip2ImgLayout, paySlip3ImgLayout;
    private ImageView paySlipExpLtrImageIv, paySlipExp2LtrImageIv, paySlipExp3LtrImageIv;
    private LinearLayout paySlipImgPdfLayout, paySlip2ImgPdfLayout, paySlip3ImgPdfLayout;
    private TextView paySlipImgPdfLinkTv, paySlip2ImgPdfLinkTv, paySlip3ImgPdfLinkTv;
    private ImageButton paySlipImgPdfViewBtn, paySlip2ImgPdfViewBtn, paySlip3ImgPdfViewBtn;
    private ProgressBar paySlipPg, paySlip2Pg, paySlip3Pg;
    private Button addPaySlipExpLtrImgBtn, addPaySlipExp2LtrImgBtn, addPaySlipExp3LtrImgBtn, uploadPaySlipExpLtrImgBtn, uploadPaySlipExp2LtrImgBtn, uploadPaySlipExp3LtrImgBtn;

    //Resign Mail Image Fields
    private LinearLayout resignMailImgLayout;
    private ImageView resignMailImageIv;
    private ProgressBar resignMailPg;
    private LinearLayout resignMailImgPdfLayout;
    private TextView resignMailImgPdfLinkTv;
    private ImageButton resignMailImgPdfViewBtn;
    private Button addResignEmailImgBtn, uploadResignEmailImgBtn;

    //Bank Statement Image Fields
    private LinearLayout bankStmtImgLayout;
    private ImageView bankStmtImageIv;
    private ProgressBar bankStmtPg;
    private LinearLayout bankStmtImgPdfLayout;
    private TextView bankStmtImgPdfLinkTv;
    private ImageButton bankStmtImgPdfViewBtn;
    private Button addBankStmtImgBtn, uploadBankStmtImgBtn;

    //Offer Letter Image Fields
    private LinearLayout offerLetterLayout;
    private ImageView offerLetterIv;
    private ProgressBar offerLetterPg;
    private LinearLayout offerLetterPdfLayout;
    private TextView offerLetterPdfLinkTv;
    private ImageButton offerLetterPdfViewBtn;
    private Button addOfferLetterBtn, uploadOfferLetterBtn;

    //Bank Proof Image Fields
    private LinearLayout vaccineCertificateImgLayout;
    private ImageView vaccineCertificateImageIv;
    private ProgressBar vaccineCertificatePg;
    private LinearLayout vaccineCertificateImgPdfLayout;
    private TextView vaccineCertificateImgPdfLinkTv;
    private ImageButton vaccineCertificateImgPdfViewBtn;
    private Button addVaccineCertificateImgBtn, uploadVaccineCertificateImgBtn;

    //Other Documents Image Fields
    private LinearLayout otherDocumentsImgLayout;
    private ImageView otherDocumentsImageIv;
    private ProgressBar otherDocumentsPg;
    private LinearLayout otherDocumentsImgPdfLayout;
    private TextView otherDocumentsImgPdfLinkTv;
    private ImageButton otherDocumentsImgPdfViewBtn;
    private Button addOtherDocumentsImgBtn, uploadOtherDocumentsImgBtn;

    //warning textview
    private TextView warningTv;


    //permissions
    private String[] cameraPermissions;
    private String[] storagePermissions;

    private String[] cameraPermissions13;
    private String[] storagePermissions13;

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 101;
    private static final int IMAGE_PICK_CAMERA_CODE = 102;
    private static final int IMAGE_PICK_GALLERY_CODE = 103;

    //image variables
    String value = "";
    private Uri imageUri = null, passportImageUri = null, empSignImageUri = null, aadharCardImageUri = null, aadharCardBackImageUri = null, panCardImageUri = null, bankProofImageUri = null,
            lastCmpExpImageUri = null, paySlipImageUri = null, paySlip2ImageUri = null, paySlip3ImageUri = null, resignMailImageUri = null, bankStmtImageUri = null, offerLetterImageUri = null, otherDocumentsImageUri = null, vaccineCertificateImageUri = null;
    String passportImgName = "passport_", empSignImgName = "empSign_", aadharCardImgName = "aadharCardFront_", aadharCardBackImgName = "aadharCardBack_", panCardImgName = "panCard_",
            bankProofImgName = "bankProof", lastCmpExpImgName = "lastCmpExp_", paySlipImgName = "paySlipLastMonth_", paySlip2ImgName = "paySlipSecondLastMonth_",
            paySlip3ImgName = "paySlipThirdLastMonth_", resignMailImgName = "resignMail_",
            bankStmtImgName = "bankStmt_", offerLetterImgName = "offerLetter_", otherDocumentsImgName = "otherDocuments_", vaccineCertificateImgName = "vaccineCertificate_";


    JSONObject jsonObject;
    RequestQueue rQueue;

    //PDF Variables
    private RequestQueue resultQueue;
    private ArrayList<HashMap<String, String>> arraylist;
    String url = "https://www.google.com";

    String pdfValue = "";
    private Uri pdfUri = null, passportPdfUri = null, empSignPdfUri = null, aadharCardPdfUri = null, aadharCardBackPdfUri = null, panCardPdfUri = null, bankProofPdfUri = null,
            lastCmpExpPdfUri = null, paySlipPdfUri = null, paySlip2PdfUri = null, paySlip3PdfUri = null, resignMailPdfUri = null, bankStmtPdfUri = null, offerLetterPdfUri = null, otherDocumentsPdfUri = null, vaccineCertificatePdfUri = null;
    String passportImgPdfName = "passport_", empSignImgPdfName = "empSign_", aadharCardImgPdfName = "aadharCard_", panCardImgPdfName = "panCard_",
            bankProofImgPdfName = "bankProof", lastCmpExpImgPdfName = "lastCmpExp_", paySlipImgPdfName = "paySlip_", resignMailImgPdfName = "resignMail_",
            bankStmtImgPdfName = "bankStmt_", offerLetterImgPdfName = "offerLetter_", otherDocumentsImgPdfName = "otherDocuments_";


    //Loading Bar
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document);

        initialization();

        //handle back button click
        backBtn.setOnClickListener(view -> onBackPressed());

        //get All Documents
        getAllDocuments();


        if (!Prevalent.currentOnlineUser.getCandidate_category().equalsIgnoreCase("Fresher")) {
            lastCmpExpImgLayout.setVisibility(View.GONE);
            paySlipImgLayout.setVisibility(View.VISIBLE);
            paySlip2ImgLayout.setVisibility(View.VISIBLE);
            paySlip3ImgLayout.setVisibility(View.VISIBLE);
            resignMailImgLayout.setVisibility(View.VISIBLE);
            bankStmtImgLayout.setVisibility(View.VISIBLE);
        } else {
            lastCmpExpImgLayout.setVisibility(View.GONE);
            paySlipImgLayout.setVisibility(View.GONE);
            paySlip2ImgLayout.setVisibility(View.GONE);
            paySlip3ImgLayout.setVisibility(View.GONE);
            resignMailImgLayout.setVisibility(View.GONE);
            bankStmtImgLayout.setVisibility(View.GONE);
        }
        if (!Prevalent.currentOnlineUser.getEmployee_level().equalsIgnoreCase("VBA")) {
            offerLetterLayout.setVisibility(View.VISIBLE);
        } else {
            offerLetterLayout.setVisibility(View.GONE);
        }


        //handle add passport image btn
        addPassportImgBtn.setOnClickListener(view -> {
            value = "passport";
            showImagePopupMenu(addPassportImgBtn);
            /*PopupMenu popupMenu = new PopupMenu(this, addPassportImgBtn);
            popupMenu.getMenu().add(Menu.NONE, 0, 0, "Image");
            popupMenu.getMenu().add(Menu.NONE, 1, 1, "PDF");

            popupMenu.show();
            //handle menu item clicks
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                int which = menuItem.getItemId();
                if (which == 0) {
                    //show image menu
                    value = "passport";
                    showImagePopupMenu(addPassportImgBtn);

                } else if (which == 1) {
                    //show pdf menu
                    pdfValue = "passport";
                    showPdfPopupMenu();
                }
                return false;
            });*/
        });
        uploadPassportImgBtn.setOnClickListener(view -> {
            if (passportImageUri != null && passportPdfUri == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DocumentActivity.this);
                builder.setTitle("Confirm");
                builder.setMessage("Are you sure ?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    dialog.dismiss();
                    loadingDialog.showDialog("Uploading...");
                    new UploadImage("passport", passportImgName, passportImageUri).execute();
                    //uploadPDF("passport", passportImgName, passportImageUri);

                });
                builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
                builder.show();
            }
            /*else if (passportImageUri == null && passportPdfUri != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DocumentActivity.this);
                builder.setTitle("Confirm");
                builder.setMessage("Are you sure ?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    dialog.dismiss();
                    loadingDialog.showDialog("Uploading...");
                    uploadPDF("passport", passportImgPdfName, pdfUri);

                });
                builder.setNegativeButton("No", (dialog, which) -> {
                    dialog.cancel();
                });
                builder.show();
            }*/
            else {
                showToast("Please select passport size image");
            }
        });
        passportImgPdfViewBtn.setOnClickListener(view -> {
            String fileValue = passportImgPdfLinkTv.getText().toString().trim();
            if (!fileValue.isEmpty()) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(fileValue));
                startActivity(browserIntent);
            }
        });

        //handle employee sign image btn
        addEmployeeSignImgBtn.setOnClickListener(view -> {
            value = "emp_sign";
            showImagePopupMenu(addEmployeeSignImgBtn);
            /*PopupMenu popupMenu = new PopupMenu(this, addEmployeeSignImgBtn);
            popupMenu.getMenu().add(Menu.NONE, 0, 0, "Image");
            popupMenu.getMenu().add(Menu.NONE, 1, 1, "PDF");

            popupMenu.show();
            //handle menu item clicks
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                int which = menuItem.getItemId();
                if (which == 0) {
                    //show image menu
                    value = "emp_sign";
                    showImagePopupMenu(addEmployeeSignImgBtn);

                } else if (which == 1) {
                    //show pdf menu
                    pdfValue = "emp_sign";
                    showPdfPopupMenu();
                }
                return false;
            });*/
        });
        uploadEmployeeSignImgBtn.setOnClickListener(view -> {
            if (empSignImageUri != null && empSignPdfUri == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DocumentActivity.this);
                builder.setTitle("Confirm");
                builder.setMessage("Are you sure ?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    dialog.dismiss();

                    loadingDialog.showDialog("Uploading...");
                    new UploadImage("emp_sign", empSignImgName, empSignImageUri).execute();
                    //uploadPDF("emp_sign", empSignImgName, empSignImageUri);

                });
                builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
                builder.show();
            } /*else if (empSignImageUri == null && empSignPdfUri != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DocumentActivity.this);
                builder.setTitle("Confirm");
                builder.setMessage("Are you sure ?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    dialog.dismiss();
                    loadingDialog.showDialog("Uploading...");
                    uploadPDF("emp_sign", empSignImgPdfName, pdfUri);

                });
                builder.setNegativeButton("No", (dialog, which) -> {
                    dialog.cancel();
                });
                builder.show();
            }*/ else {
                showToast("Please select employee sign image");
            }
        });
        employeeSignImgPdfViewBtn.setOnClickListener(view -> {
            String fileValue = employeeSignImgPdfLinkTv.getText().toString().trim();
            if (!fileValue.isEmpty()) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(fileValue));
                startActivity(browserIntent);
            }
        });

        //handle aadhar card image btn
        addAadharCardImgBtn.setOnClickListener(view -> {
            value = "aadhar_card";
            showImagePopupMenu(addAadharCardImgBtn);
            /*PopupMenu popupMenu = new PopupMenu(this, addAadharCardImgBtn);
            popupMenu.getMenu().add(Menu.NONE, 0, 0, "Image");
            popupMenu.getMenu().add(Menu.NONE, 1, 1, "PDF");

            popupMenu.show();
            //handle menu item clicks
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                int which = menuItem.getItemId();
                if (which == 0) {
                    //show image menu
                    value = "aadhar_card";
                    showImagePopupMenu(addAadharCardImgBtn);

                } else if (which == 1) {
                    //show pdf menu
                    pdfValue = "aadhar_card";
                    showPdfPopupMenu();
                }
                return false;
            });*/
        });
        uploadAadharCardImgBtn.setOnClickListener(view -> {
            if (aadharCardImageUri != null && aadharCardPdfUri == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DocumentActivity.this);
                builder.setTitle("Confirm");
                builder.setMessage("Are you sure ?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    dialog.dismiss();

                    loadingDialog.showDialog("Uploading...");
                    new UploadImage("aadhar_card", aadharCardImgName, aadharCardImageUri).execute();
                    //uploadPDF("aadhar_card", aadharCardImgName, aadharCardImageUri);

                });
                builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
                builder.show();

            }
            /*else if (aadharCardImageUri == null && aadharCardPdfUri != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DocumentActivity.this);
                builder.setTitle("Confirm");
                builder.setMessage("Are you sure ?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    dialog.dismiss();
                    loadingDialog.showDialog("Uploading...");
                    uploadPDF("aadhar_card", aadharCardImgPdfName, pdfUri);

                });
                builder.setNegativeButton("No", (dialog, which) -> {
                    dialog.cancel();
                });
                builder.show();
            }*/
            else {
                showToast("Please select aadhar card image");
            }
        });
        aadharCardImgPdfViewBtn.setOnClickListener(view -> {
            String fileValue = aadharCardImgPdfLinkTv.getText().toString().trim();
            if (!fileValue.isEmpty()) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(fileValue));
                startActivity(browserIntent);
            }
        });

        //handle aadhar card back image btn
        addAadharCardBackImgBtn.setOnClickListener(view -> {
            value = "aadhar_card_back";
            showImagePopupMenu(addAadharCardBackImgBtn);
            /*PopupMenu popupMenu = new PopupMenu(this, addAadharCardImgBtn);
            popupMenu.getMenu().add(Menu.NONE, 0, 0, "Image");
            popupMenu.getMenu().add(Menu.NONE, 1, 1, "PDF");

            popupMenu.show();
            //handle menu item clicks
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                int which = menuItem.getItemId();
                if (which == 0) {
                    //show image menu
                    value = "aadhar_card";
                    showImagePopupMenu(addAadharCardImgBtn);

                } else if (which == 1) {
                    //show pdf menu
                    pdfValue = "aadhar_card";
                    showPdfPopupMenu();
                }
                return false;
            });*/
        });
        uploadAadharCardBackImgBtn.setOnClickListener(view -> {
            if (aadharCardBackImageUri != null && aadharCardBackPdfUri == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DocumentActivity.this);
                builder.setTitle("Confirm");
                builder.setMessage("Are you sure ?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    dialog.dismiss();

                    loadingDialog.showDialog("Uploading...");
                    new UploadImage("aadhar_card_back", aadharCardBackImgName, aadharCardBackImageUri).execute();
                    //uploadPDF("aadhar_card", aadharCardImgName, aadharCardImageUri);

                });
                builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
                builder.show();

            }
            /*else if (aadharCardImageUri == null && aadharCardPdfUri != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DocumentActivity.this);
                builder.setTitle("Confirm");
                builder.setMessage("Are you sure ?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    dialog.dismiss();
                    loadingDialog.showDialog("Uploading...");
                    uploadPDF("aadhar_card", aadharCardImgPdfName, pdfUri);

                });
                builder.setNegativeButton("No", (dialog, which) -> {
                    dialog.cancel();
                });
                builder.show();
            }*/
            else {
                showToast("Please select aadhar card back image");
            }
        });
        aadharCardBackImgPdfViewBtn.setOnClickListener(view -> {
            String fileValue = aadharCardBackImgPdfLinkTv.getText().toString().trim();
            if (!fileValue.isEmpty()) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(fileValue));
                startActivity(browserIntent);
            }
        });

        //handle PAN Card image btn
        addPanCardImgBtn.setOnClickListener(view -> {
            value = "pan_card";
            showImagePopupMenu(addPanCardImgBtn);
            /*PopupMenu popupMenu = new PopupMenu(this, addPanCardImgBtn);
            popupMenu.getMenu().add(Menu.NONE, 0, 0, "Image");
            popupMenu.getMenu().add(Menu.NONE, 1, 1, "PDF");

            popupMenu.show();
            //handle menu item clicks
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                int which = menuItem.getItemId();
                if (which == 0) {
                    //show image menu
                    value = "pan_card";
                    showImagePopupMenu(addPanCardImgBtn);

                } else if (which == 1) {
                    //show pdf menu
                    pdfValue = "pan_card";
                    showPdfPopupMenu();
                }
                return false;
            });*/
        });
        uploadPanCardImgBtn.setOnClickListener(view -> {
            if (panCardImageUri != null && panCardPdfUri == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DocumentActivity.this);
                builder.setTitle("Confirm");
                builder.setMessage("Are you sure ?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    dialog.dismiss();

                    loadingDialog.showDialog("Uploading...");
                    new UploadImage("pan_card", panCardImgName, panCardImageUri).execute();
                    //uploadPDF("pan_card", panCardImgName, panCardImageUri);

                });
                builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
                builder.show();

            } /*else if (panCardImageUri == null && panCardPdfUri != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DocumentActivity.this);
                builder.setTitle("Confirm");
                builder.setMessage("Are you sure ?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    dialog.dismiss();
                    loadingDialog.showDialog("Uploading...");
                    uploadPDF("pan_card", panCardImgPdfName, pdfUri);

                });
                builder.setNegativeButton("No", (dialog, which) -> {
                    dialog.cancel();
                });
                builder.show();
            }*/ else {
                showToast("Please select pan card image");
            }
        });
        panCardImgPdfViewBtn.setOnClickListener(view -> {
            String fileValue = panCardImgPdfLinkTv.getText().toString().trim();
            if (!fileValue.isEmpty()) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(fileValue));
                startActivity(browserIntent);
            }
        });

        //handle bank proof image btn
        addBankProofImgBtn.setOnClickListener(view -> {
            value = "bank_proof";
            showImagePopupMenu(addBankProofImgBtn);
            /*PopupMenu popupMenu = new PopupMenu(this, addBankProofImgBtn);
            popupMenu.getMenu().add(Menu.NONE, 0, 0, "Image");
            popupMenu.getMenu().add(Menu.NONE, 1, 1, "PDF");

            popupMenu.show();
            //handle menu item clicks
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                int which = menuItem.getItemId();
                if (which == 0) {
                    //show image menu
                    value = "bank_proof";
                    showImagePopupMenu(addBankProofImgBtn);

                } else if (which == 1) {
                    //show pdf menu
                    pdfValue = "bank_proof";
                    showPdfPopupMenu();
                }
                return false;
            });*/
        });
        uploadBankProofImgBtn.setOnClickListener(view -> {
            if (bankProofImageUri != null && bankProofPdfUri == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DocumentActivity.this);
                builder.setTitle("Confirm");
                builder.setMessage("Are you sure ?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    dialog.dismiss();

                    loadingDialog.showDialog("Uploading...");
                    new UploadImage("bank_proof", bankProofImgName, bankProofImageUri).execute();
                    //uploadPDF("bank_proof", bankProofImgName, bankProofImageUri);

                });
                builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
                builder.show();

            } /*else if (bankProofImageUri == null && bankProofPdfUri != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DocumentActivity.this);
                builder.setTitle("Confirm");
                builder.setMessage("Are you sure ?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    dialog.dismiss();
                    loadingDialog.showDialog("Uploading...");
                    uploadPDF("bank_proof", bankProofImgPdfName, pdfUri);

                });
                builder.setNegativeButton("No", (dialog, which) -> {
                    dialog.cancel();
                });
                builder.show();
            }*/ else {
                showToast("Please select bank proof image");
            }
        });
        bankProofImgPdfViewBtn.setOnClickListener(view -> {
            String fileValue = bankProofImgPdfLinkTv.getText().toString().trim();
            if (!fileValue.isEmpty()) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(fileValue));
                startActivity(browserIntent);
            }
        });

        //handle last company exp letter image btn
        addLastCpExpLtrImgBtn.setOnClickListener(view -> {
            value = "cmp_last_exp";
            showImagePopupMenu(addLastCpExpLtrImgBtn);
            /*PopupMenu popupMenu = new PopupMenu(this, addLastCpExpLtrImgBtn);
            popupMenu.getMenu().add(Menu.NONE, 0, 0, "Image");
            popupMenu.getMenu().add(Menu.NONE, 1, 1, "PDF");

            popupMenu.show();
            //handle menu item clicks
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                int which = menuItem.getItemId();
                if (which == 0) {
                    //show image menu
                    value = "cmp_last_exp";
                    showImagePopupMenu(addLastCpExpLtrImgBtn);

                } else if (which == 1) {
                    //show pdf menu
                    pdfValue = "cmp_last_exp";
                    showPdfPopupMenu();
                }
                return false;
            });*/
        });
        uploadLastCpExpLtrImgBtn.setOnClickListener(view -> {
            if (lastCmpExpImageUri != null && lastCmpExpPdfUri == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DocumentActivity.this);
                builder.setTitle("Confirm");
                builder.setMessage("Are you sure ?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    dialog.dismiss();

                    loadingDialog.showDialog("Uploading...");
                    new UploadImage("cmp_last_exp", lastCmpExpImgName, lastCmpExpImageUri).execute();
                    //uploadPDF("cmp_last_exp", lastCmpExpImgName, lastCmpExpImageUri);

                });
                builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
                builder.show();
            } /*else if (lastCmpExpImageUri == null && lastCmpExpPdfUri != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DocumentActivity.this);
                builder.setTitle("Confirm");
                builder.setMessage("Are you sure ?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    dialog.dismiss();
                    loadingDialog.showDialog("Uploading...");
                    uploadPDF("cmp_last_exp", lastCmpExpImgPdfName, pdfUri);

                });
                builder.setNegativeButton("No", (dialog, which) -> {
                    dialog.cancel();
                });
                builder.show();
            }*/ else {
                showToast("Please select last company experience image");
            }
        });
        lastCpExpLtrImgPdfViewBtn.setOnClickListener(view -> {
            String fileValue = lastCpExpLtrImgPdfLinkTv.getText().toString().trim();
            if (!fileValue.isEmpty()) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(fileValue));
                startActivity(browserIntent);
            }
        });

        //handle pay slip last month image btn
        addPaySlipExpLtrImgBtn.setOnClickListener(view -> {
            value = "pay_slip";
            showImagePopupMenu(addPaySlipExpLtrImgBtn);
            /*PopupMenu popupMenu = new PopupMenu(this, addPaySlipExpLtrImgBtn);
            popupMenu.getMenu().add(Menu.NONE, 0, 0, "Image");
            popupMenu.getMenu().add(Menu.NONE, 1, 1, "PDF");

            popupMenu.show();
            //handle menu item clicks
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                int which = menuItem.getItemId();
                if (which == 0) {
                    //show image menu
                    value = "pay_slip";
                    showImagePopupMenu(addPaySlipExpLtrImgBtn);

                } else if (which == 1) {
                    //show pdf menu
                    pdfValue = "pay_slip";
                    showPdfPopupMenu();
                }
                return false;
            });*/
        });
        uploadPaySlipExpLtrImgBtn.setOnClickListener(view -> {
            if (paySlipImageUri != null && paySlipPdfUri == null) {

                AlertDialog.Builder builder = new AlertDialog.Builder(DocumentActivity.this);
                builder.setTitle("Confirm");
                builder.setMessage("Are you sure ?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    dialog.dismiss();

                    loadingDialog.showDialog("Uploading...");
                    new UploadImage("pay_slip", paySlipImgName, paySlipImageUri).execute();
                    //uploadPDF("pay_slip", paySlipImgName, paySlipImageUri);

                });
                builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
                builder.show();


            } /*else if (paySlipImageUri == null && paySlipPdfUri != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DocumentActivity.this);
                builder.setTitle("Confirm");
                builder.setMessage("Are you sure ?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    dialog.dismiss();
                    loadingDialog.showDialog("Uploading...");
                    uploadPDF("pay_slip", paySlipImgPdfName, pdfUri);

                });
                builder.setNegativeButton("No", (dialog, which) -> {
                    dialog.cancel();
                });
                builder.show();
            }*/ else {
                showToast("Please select last month pay slip image");
            }
        });
        paySlipImgPdfViewBtn.setOnClickListener(view -> {
            String fileValue = paySlipImgPdfLinkTv.getText().toString().trim();
            if (!fileValue.isEmpty()) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(fileValue));
                startActivity(browserIntent);
            }
        });

        //handle pay slip last 2 month image btn
        addPaySlipExp2LtrImgBtn.setOnClickListener(view -> {
            value = "pay_slip_second_last_month";
            showImagePopupMenu(addPaySlipExp2LtrImgBtn);
            /*PopupMenu popupMenu = new PopupMenu(this, addPaySlipExpLtrImgBtn);
            popupMenu.getMenu().add(Menu.NONE, 0, 0, "Image");
            popupMenu.getMenu().add(Menu.NONE, 1, 1, "PDF");

            popupMenu.show();
            //handle menu item clicks
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                int which = menuItem.getItemId();
                if (which == 0) {
                    //show image menu
                    value = "pay_slip";
                    showImagePopupMenu(addPaySlipExpLtrImgBtn);

                } else if (which == 1) {
                    //show pdf menu
                    pdfValue = "pay_slip";
                    showPdfPopupMenu();
                }
                return false;
            });*/
        });
        uploadPaySlipExp2LtrImgBtn.setOnClickListener(view -> {
            if (paySlip2ImageUri != null && paySlip2PdfUri == null) {

                AlertDialog.Builder builder = new AlertDialog.Builder(DocumentActivity.this);
                builder.setTitle("Confirm");
                builder.setMessage("Are you sure ?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    dialog.dismiss();

                    loadingDialog.showDialog("Uploading...");
                    new UploadImage("pay_slip_second_last_month", paySlip2ImgName, paySlip2ImageUri).execute();
                    //uploadPDF("pay_slip", paySlipImgName, paySlipImageUri);

                });
                builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
                builder.show();


            } /*else if (paySlipImageUri == null && paySlipPdfUri != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DocumentActivity.this);
                builder.setTitle("Confirm");
                builder.setMessage("Are you sure ?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    dialog.dismiss();
                    loadingDialog.showDialog("Uploading...");
                    uploadPDF("pay_slip", paySlipImgPdfName, pdfUri);

                });
                builder.setNegativeButton("No", (dialog, which) -> {
                    dialog.cancel();
                });
                builder.show();
            }*/ else {
                showToast("Please select second last month pay slip image");
            }
        });
        paySlip2ImgPdfViewBtn.setOnClickListener(view -> {
            String fileValue = paySlip2ImgPdfLinkTv.getText().toString().trim();
            if (!fileValue.isEmpty()) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(fileValue));
                startActivity(browserIntent);
            }
        });

        //handle pay slip last 3 month image btn
        addPaySlipExp3LtrImgBtn.setOnClickListener(view -> {
            value = "pay_slip_third_last_month";
            showImagePopupMenu(addPaySlipExp3LtrImgBtn);
            /*PopupMenu popupMenu = new PopupMenu(this, addPaySlipExpLtrImgBtn);
            popupMenu.getMenu().add(Menu.NONE, 0, 0, "Image");
            popupMenu.getMenu().add(Menu.NONE, 1, 1, "PDF");

            popupMenu.show();
            //handle menu item clicks
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                int which = menuItem.getItemId();
                if (which == 0) {
                    //show image menu
                    value = "pay_slip";
                    showImagePopupMenu(addPaySlipExpLtrImgBtn);

                } else if (which == 1) {
                    //show pdf menu
                    pdfValue = "pay_slip";
                    showPdfPopupMenu();
                }
                return false;
            });*/
        });
        uploadPaySlipExp3LtrImgBtn.setOnClickListener(view -> {
            if (paySlip3ImageUri != null && paySlip3PdfUri == null) {

                AlertDialog.Builder builder = new AlertDialog.Builder(DocumentActivity.this);
                builder.setTitle("Confirm");
                builder.setMessage("Are you sure ?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    dialog.dismiss();

                    loadingDialog.showDialog("Uploading...");
                    new UploadImage("pay_slip_third_last_month", paySlip3ImgName, paySlip3ImageUri).execute();
                    //uploadPDF("pay_slip", paySlipImgName, paySlipImageUri);

                });
                builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
                builder.show();


            } /*else if (paySlipImageUri == null && paySlipPdfUri != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DocumentActivity.this);
                builder.setTitle("Confirm");
                builder.setMessage("Are you sure ?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    dialog.dismiss();
                    loadingDialog.showDialog("Uploading...");
                    uploadPDF("pay_slip", paySlipImgPdfName, pdfUri);

                });
                builder.setNegativeButton("No", (dialog, which) -> {
                    dialog.cancel();
                });
                builder.show();
            }*/ else {
                showToast("Please select third last month pay slip image");
            }
        });
        paySlip3ImgPdfViewBtn.setOnClickListener(view -> {
            String fileValue = paySlip3ImgPdfLinkTv.getText().toString().trim();
            if (!fileValue.isEmpty()) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(fileValue));
                startActivity(browserIntent);
            }
        });

        //handle resign mail image btn
        addResignEmailImgBtn.setOnClickListener(view -> {
            value = "resign_mail";
            showImagePopupMenu(addResignEmailImgBtn);
            /*PopupMenu popupMenu = new PopupMenu(this, addResignEmailImgBtn);
            popupMenu.getMenu().add(Menu.NONE, 0, 0, "Image");
            popupMenu.getMenu().add(Menu.NONE, 1, 1, "PDF");

            popupMenu.show();
            //handle menu item clicks
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                int which = menuItem.getItemId();
                if (which == 0) {
                    //show image menu
                    value = "resign_mail";
                    showImagePopupMenu(addResignEmailImgBtn);

                } else if (which == 1) {
                    //show pdf menu
                    pdfValue = "resign_mail";
                    showPdfPopupMenu();
                }
                return false;
            });*/
        });
        uploadResignEmailImgBtn.setOnClickListener(view -> {
            if (resignMailImageUri != null && resignMailPdfUri == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DocumentActivity.this);
                builder.setTitle("Confirm");
                builder.setMessage("Are you sure ?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    dialog.dismiss();

                    loadingDialog.showDialog("Uploading...");
                    new UploadImage("resign_mail", resignMailImgName, resignMailImageUri).execute();
                    //uploadPDF("resign_mail", resignMailImgName, resignMailImageUri);

                });
                builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
                builder.show();

            } /*else if (resignMailImageUri == null && resignMailPdfUri != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DocumentActivity.this);
                builder.setTitle("Confirm");
                builder.setMessage("Are you sure ?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    dialog.dismiss();
                    loadingDialog.showDialog("Uploading...");
                    uploadPDF("resign_mail", resignMailImgPdfName, pdfUri);

                });
                builder.setNegativeButton("No", (dialog, which) -> {
                    dialog.cancel();
                });
                builder.show();
            }*/ else {
                showToast("Please select resign mail image");
            }
        });
        resignMailImgPdfViewBtn.setOnClickListener(view -> {
            String fileValue = resignMailImgPdfLinkTv.getText().toString().trim();
            if (!fileValue.isEmpty()) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(fileValue));
                startActivity(browserIntent);
            }
        });

        //handle bank stmt image btn
        addBankStmtImgBtn.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(this, addBankStmtImgBtn);
            popupMenu.getMenu().add(Menu.NONE, 0, 0, "Image");
            popupMenu.getMenu().add(Menu.NONE, 1, 1, "PDF");

            popupMenu.show();
            //handle menu item clicks
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                int which = menuItem.getItemId();
                if (which == 0) {
                    //show image menu
                    value = "bank_stmt";
                    showImagePopupMenu(addBankStmtImgBtn);

                } else if (which == 1) {
                    //show pdf menu
                    pdfValue = "bank_stmt";
                    showPdfPopupMenu();
                }
                return false;
            });
        });
        uploadBankStmtImgBtn.setOnClickListener(view -> {
            if (bankStmtImageUri != null && bankStmtPdfUri == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DocumentActivity.this);
                builder.setTitle("Confirm");
                builder.setMessage("Are you sure ?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    dialog.dismiss();
                    loadingDialog.showDialog("Uploading...");
                    new UploadImage("bank_stmt", bankStmtImgName, bankStmtImageUri).execute();
                    //uploadPDF("bank_stmt", bankStmtImgName, bankStmtImageUri);

                });
                builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
                builder.show();
            } else if (bankStmtImageUri == null && bankStmtPdfUri != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DocumentActivity.this);
                builder.setTitle("Confirm");
                builder.setMessage("Are you sure ?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    dialog.dismiss();
                    loadingDialog.showDialog("Uploading...");
                    uploadPDF("bank_stmt", bankStmtImgPdfName, bankStmtPdfUri);

                });
                builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
                builder.show();
            } else {
                showToast("Please select bank statement image");
            }
        });
        bankStmtImgPdfViewBtn.setOnClickListener(view -> {
            String fileValue = bankStmtImgPdfLinkTv.getText().toString().trim();
            if (!fileValue.isEmpty()) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(fileValue));
                startActivity(browserIntent);
            }
        });

        //handle appointment Letter image btn
        addOfferLetterBtn.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(this, addOfferLetterBtn);
            popupMenu.getMenu().add(Menu.NONE, 0, 0, "Image");
            popupMenu.getMenu().add(Menu.NONE, 1, 1, "PDF");

            popupMenu.show();
            //handle menu item clicks
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                int which = menuItem.getItemId();
                if (which == 0) {
                    //show image menu
                    value = "offer_letter";
                    showImagePopupMenu(addOfferLetterBtn);

                } else if (which == 1) {
                    //show pdf menu
                    pdfValue = "offer_letter";
                    showPdfPopupMenu();
                }
                return false;
            });
        });
        uploadOfferLetterBtn.setOnClickListener(view -> {
            if (offerLetterImageUri != null && offerLetterPdfUri == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DocumentActivity.this);
                builder.setTitle("Confirm");
                builder.setMessage("Are you sure ?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    dialog.dismiss();
                    loadingDialog.showDialog("Uploading...");
                    new UploadImage("offer_letter", offerLetterImgName, offerLetterImageUri).execute();
                    //uploadPDF("bank_stmt", bankStmtImgName, bankStmtImageUri);

                });
                builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
                builder.show();
            } else if (offerLetterImageUri == null && offerLetterPdfUri != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DocumentActivity.this);
                builder.setTitle("Confirm");
                builder.setMessage("Are you sure ?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    dialog.dismiss();
                    loadingDialog.showDialog("Uploading...");
                    uploadPDF("offer_letter", offerLetterImgPdfName, offerLetterPdfUri);

                });
                builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
                builder.show();
            } else {
                showToast("Please select appointment letter");
            }
        });
        offerLetterPdfViewBtn.setOnClickListener(view -> {
            String fileValue = offerLetterPdfLinkTv.getText().toString().trim();
            if (!fileValue.isEmpty()) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(fileValue));
                startActivity(browserIntent);
            }
        });

        //handle add vaccine certificate image btn
        addVaccineCertificateImgBtn.setOnClickListener(view -> {
            value = "vaccine_certificate";
            showImagePopupMenu(addVaccineCertificateImgBtn);
            /*PopupMenu popupMenu = new PopupMenu(this, addPassportImgBtn);
            popupMenu.getMenu().add(Menu.NONE, 0, 0, "Image");
            popupMenu.getMenu().add(Menu.NONE, 1, 1, "PDF");

            popupMenu.show();
            //handle menu item clicks
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                int which = menuItem.getItemId();
                if (which == 0) {
                    //show image menu
                    value = "passport";
                    showImagePopupMenu(addPassportImgBtn);

                } else if (which == 1) {
                    //show pdf menu
                    pdfValue = "passport";
                    showPdfPopupMenu();
                }
                return false;
            });*/
        });
        uploadVaccineCertificateImgBtn.setOnClickListener(view -> {
            if (vaccineCertificateImageUri != null && vaccineCertificatePdfUri == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DocumentActivity.this);
                builder.setTitle("Confirm");
                builder.setMessage("Are you sure ?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    dialog.dismiss();
                    loadingDialog.showDialog("Uploading...");
                    new UploadImage("vaccine_certificate", vaccineCertificateImgName, vaccineCertificateImageUri).execute();
                    //uploadPDF("passport", passportImgName, passportImageUri);

                });
                builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
                builder.show();
            }
            /*else if (passportImageUri == null && passportPdfUri != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DocumentActivity.this);
                builder.setTitle("Confirm");
                builder.setMessage("Are you sure ?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    dialog.dismiss();
                    loadingDialog.showDialog("Uploading...");
                    uploadPDF("passport", passportImgPdfName, pdfUri);

                });
                builder.setNegativeButton("No", (dialog, which) -> {
                    dialog.cancel();
                });
                builder.show();
            }*/
            else {
                showToast("Please select vaccine certificate image");
            }
        });
        vaccineCertificateImgPdfViewBtn.setOnClickListener(view -> {
            String fileValue = vaccineCertificateImgPdfLinkTv.getText().toString().trim();
            if (!fileValue.isEmpty()) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(fileValue));
                startActivity(browserIntent);
            }
        });

        //handle other Documents image btn
        addOtherDocumentsImgBtn.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(this, addOtherDocumentsImgBtn);
            popupMenu.getMenu().add(Menu.NONE, 0, 0, "Image");
            popupMenu.getMenu().add(Menu.NONE, 1, 1, "PDF");

            popupMenu.show();
            //handle menu item clicks
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                int which = menuItem.getItemId();
                if (which == 0) {
                    //show image menu
                    value = "other_documents";
                    showImagePopupMenu(addOtherDocumentsImgBtn);

                } else if (which == 1) {
                    //show pdf menu
                    pdfValue = "other_documents";
                    showPdfPopupMenu();
                }
                return false;
            });
        });
        uploadOtherDocumentsImgBtn.setOnClickListener(view -> {
            if (otherDocumentsImageUri != null && otherDocumentsPdfUri == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DocumentActivity.this);
                builder.setTitle("Confirm");
                builder.setMessage("Are you sure ?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    dialog.dismiss();
                    loadingDialog.showDialog("Uploading...");
                    new UploadImage("other_documents", otherDocumentsImgName, otherDocumentsImageUri).execute();
                    //uploadPDF("bank_stmt", bankStmtImgName, bankStmtImageUri);

                });
                builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
                builder.show();
            } else if (otherDocumentsImageUri == null && otherDocumentsPdfUri != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DocumentActivity.this);
                builder.setTitle("Confirm");
                builder.setMessage("Are you sure ?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    dialog.dismiss();
                    loadingDialog.showDialog("Uploading...");
                    uploadPDF("other_documents", otherDocumentsImgPdfName, otherDocumentsPdfUri);

                });
                builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
                builder.show();
            } else {
                showToast("Please select other document pdf");
            }
        });
        otherDocumentsImgPdfViewBtn.setOnClickListener(view -> {
            String fileValue = otherDocumentsImgPdfLinkTv.getText().toString().trim();
            if (!fileValue.isEmpty()) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(fileValue));
                startActivity(browserIntent);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            //load All Documents
            getAllDocuments();
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    private void setDataToFields(DocumentModel documentModel) {
        //Passport Size Image
        if (documentModel.getPassport_size_image() != null && !documentModel.getPassport_size_image().equalsIgnoreCase("")) {
            passportPg.setVisibility(View.VISIBLE);

            //set passport image/pdf link value and show view button
            passportImgPdfLayout.setVisibility(View.VISIBLE);
            passportImgPdfLinkTv.setText(documentModel.getPassport_size_image());
            passportImgPdfViewBtn.setVisibility(View.VISIBLE);

            if (!documentModel.getPassport_size_image().contains(".pdf")) {
                //show image view
                passportImageIv.setVisibility(View.VISIBLE);
                Picasso.get().load(documentModel.getPassport_size_image()).fit().into(passportImageIv, new Callback() {
                    @Override
                    public void onSuccess() {
                        passportImageUri = null;
                        passportPdfUri = null;
                        addPassportImgBtn.setText("Update");
                        passportPg.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e("PASSPORT IMG ERROR", e.toString());
                        passportPg.setVisibility(View.GONE);
                        showToast(e.toString());
                    }
                });
            } else {
                passportImageIv.setVisibility(View.GONE);//hide imageview
                passportPg.setVisibility(View.GONE);//hide progress bar
                passportImageUri = null;
                passportPdfUri = null;
                addPassportImgBtn.setText("Update");//set update to add passport button
            }

        }

        //Employee Sign Image
        if (documentModel.getEmployee_sign_image() != null && !documentModel.getEmployee_sign_image().equalsIgnoreCase("")) {
            empSignPg.setVisibility(View.VISIBLE);

            //set passport image/pdf link value and show view button
            employeeSignImgPdfLayout.setVisibility(View.VISIBLE);
            employeeSignImgPdfLinkTv.setText(documentModel.getEmployee_sign_image());
            employeeSignImgPdfViewBtn.setVisibility(View.VISIBLE);

            if (!documentModel.getEmployee_sign_image().contains(".pdf")) {
                //show image view
                employeeSignImageIv.setVisibility(View.VISIBLE);
                Picasso.get().load(documentModel.getEmployee_sign_image()).fit().into(employeeSignImageIv, new Callback() {
                    @Override
                    public void onSuccess() {
                        empSignImageUri = null;
                        empSignPdfUri = null;
                        addEmployeeSignImgBtn.setText("Update");
                        empSignPg.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e("EMPLOYEE SIGN IMG ERROR", e.toString());
                        empSignPg.setVisibility(View.GONE);
                        showToast(e.toString());
                    }
                });
            } else {
                employeeSignImageIv.setVisibility(View.GONE);//hide imageview
                empSignPg.setVisibility(View.GONE);//hide progress bar
                empSignImageUri = null;
                empSignPdfUri = null;
                addEmployeeSignImgBtn.setText("Update");//set update to add employee Sign button
            }
        }

        //Aadhar Card
        if (documentModel.getAadhar_card_image() != null && !documentModel.getAadhar_card_image().equalsIgnoreCase("")) {
            aadharCardPg.setVisibility(View.VISIBLE);

            //set aadhar Card image/pdf link value and show view button
            aadharCardImgPdfLayout.setVisibility(View.VISIBLE);
            aadharCardImgPdfLinkTv.setText(documentModel.getAadhar_card_image());
            aadharCardImgPdfViewBtn.setVisibility(View.VISIBLE);

            if (!documentModel.getAadhar_card_image().contains(".pdf")) {
                //show image view
                aadharCardImageIv.setVisibility(View.VISIBLE);
                Picasso.get().load(documentModel.getAadhar_card_image()).fit().into(aadharCardImageIv, new Callback() {
                    @Override
                    public void onSuccess() {
                        aadharCardImageUri = null;
                        aadharCardPdfUri = null;
                        addAadharCardImgBtn.setText("Update");
                        aadharCardPg.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e("AADHAR CARD IMG ERROR", e.toString());
                        aadharCardPg.setVisibility(View.GONE);
                        showToast(e.toString());
                    }
                });
            } else {
                aadharCardImageIv.setVisibility(View.GONE);//hide imageview
                aadharCardPg.setVisibility(View.GONE);//hide progress bar
                aadharCardImageUri = null;
                aadharCardPdfUri = null;
                addAadharCardImgBtn.setText("Update");//set update to add aadhar card button
            }

        }

        //Aadhar Card Back
        if (documentModel.getAadhar_card_back_image() != null && !documentModel.getAadhar_card_back_image().equalsIgnoreCase("")) {
            aadharCardBackPg.setVisibility(View.VISIBLE);

            //set aadhar Card image/pdf link value and show view button
            aadharCardBackImgPdfLayout.setVisibility(View.VISIBLE);
            aadharCardBackImgPdfLinkTv.setText(documentModel.getAadhar_card_back_image());
            aadharCardBackImgPdfViewBtn.setVisibility(View.VISIBLE);

            if (!documentModel.getAadhar_card_back_image().contains(".pdf")) {
                //show image view
                aadharCardBackImageIv.setVisibility(View.VISIBLE);
                Picasso.get().load(documentModel.getAadhar_card_back_image()).fit().into(aadharCardBackImageIv, new Callback() {
                    @Override
                    public void onSuccess() {
                        aadharCardBackImageUri = null;
                        aadharCardBackPdfUri = null;
                        addAadharCardBackImgBtn.setText("Update");
                        aadharCardBackPg.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e("AC_BACK IMG ERROR", e.toString());
                        aadharCardBackPg.setVisibility(View.GONE);
                        showToast(e.toString());
                    }
                });
            } else {
                aadharCardBackImageIv.setVisibility(View.GONE);//hide imageview
                aadharCardBackPg.setVisibility(View.GONE);//hide progress bar
                aadharCardBackImageUri = null;
                aadharCardBackPdfUri = null;
                addAadharCardBackImgBtn.setText("Update");//set update to add aadhar card back button
            }

        }

        //PAN Card
        if (documentModel.getPan_card_image() != null && !documentModel.getPan_card_image().equalsIgnoreCase("")) {
            panCardPg.setVisibility(View.VISIBLE);

            //set pan Card image/pdf link value and show view button
            panCardImgPdfLayout.setVisibility(View.VISIBLE);
            panCardImgPdfLinkTv.setText(documentModel.getPan_card_image());
            panCardImgPdfViewBtn.setVisibility(View.VISIBLE);

            if (!documentModel.getPan_card_image().contains(".pdf")) {
                //show image view
                panCardImageIv.setVisibility(View.VISIBLE);
                Picasso.get().load(documentModel.getPan_card_image()).fit().into(panCardImageIv, new Callback() {
                    @Override
                    public void onSuccess() {
                        panCardImageUri = null;
                        panCardPdfUri = null;
                        addPanCardImgBtn.setText("Update");
                        panCardPg.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e("AADHAR CARD IMG ERROR", e.toString());
                        panCardPg.setVisibility(View.GONE);
                        showToast(e.toString());
                    }
                });
            } else {
                panCardImageIv.setVisibility(View.GONE);//hide imageview
                panCardPg.setVisibility(View.GONE);//hide progress bar
                panCardImageUri = null;
                panCardPdfUri = null;
                addPanCardImgBtn.setText("Update");//set update to add pan Card button
            }
        }

        //Bank Proof Image
        if (documentModel.getBank_proof_image() != null && !documentModel.getBank_proof_image().equalsIgnoreCase("")) {
            bankProofPg.setVisibility(View.VISIBLE);

            //set bankProof image/pdf link value and show view button
            bankProofImgPdfLayout.setVisibility(View.VISIBLE);
            bankProofImgPdfLinkTv.setText(documentModel.getBank_proof_image());
            bankProofImgPdfViewBtn.setVisibility(View.VISIBLE);

            if (!documentModel.getBank_proof_image().contains(".pdf")) {
                //show image view
                bankProofImageIv.setVisibility(View.VISIBLE);
                Picasso.get().load(documentModel.getBank_proof_image()).fit().into(bankProofImageIv, new Callback() {
                    @Override
                    public void onSuccess() {
                        bankProofImageUri = null;
                        bankProofPdfUri = null;
                        addBankProofImgBtn.setText("Update");
                        bankProofPg.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e("Bank Proof IMG ERROR", e.toString());
                        bankProofPg.setVisibility(View.GONE);
                        showToast(e.toString());
                    }
                });
            } else {
                bankProofImageIv.setVisibility(View.GONE);//hide imageview
                bankProofPg.setVisibility(View.GONE);//hide progress bar
                bankProofImageUri = null;
                bankProofPdfUri = null;
                addBankProofImgBtn.setText("Update");//set update to add pan Card button
            }


        }

        //Last Company Exp Letter Image
        if (documentModel.getLast_company_exp_letter_image() != null && !documentModel.getLast_company_exp_letter_image().equalsIgnoreCase("")) {
            lastCmpExpPg.setVisibility(View.VISIBLE);

            //set last Cmp image/pdf link value and show view button
            lastCpExpLtrImgPdfLayout.setVisibility(View.VISIBLE);
            lastCpExpLtrImgPdfLinkTv.setText(documentModel.getLast_company_exp_letter_image());
            lastCpExpLtrImgPdfViewBtn.setVisibility(View.VISIBLE);

            if (!documentModel.getLast_company_exp_letter_image().contains(".pdf")) {
                //show image view
                lastCpExpLtrImageIv.setVisibility(View.VISIBLE);
                Picasso.get().load(documentModel.getLast_company_exp_letter_image()).fit().into(lastCpExpLtrImageIv, new Callback() {
                    @Override
                    public void onSuccess() {
                        lastCmpExpImageUri = null;
                        lastCmpExpPdfUri = null;
                        addLastCpExpLtrImgBtn.setText("Update");
                        lastCmpExpPg.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e("LAST CMP IMG ERROR", e.toString());
                        lastCmpExpPg.setVisibility(View.GONE);
                        showToast(e.toString());
                    }
                });
            } else {
                lastCpExpLtrImageIv.setVisibility(View.GONE);//hide imageview
                lastCmpExpPg.setVisibility(View.GONE);//hide progress bar
                lastCmpExpImageUri = null;
                lastCmpExpPdfUri = null;
                addLastCpExpLtrImgBtn.setText("Update");//set update to add last cmp exp letter button
            }

        }

        //Pay Slip Last Month Image
        if (documentModel.getPay_slip_exp_letter_image() != null && !documentModel.getPay_slip_exp_letter_image().equalsIgnoreCase("")) {
            paySlipPg.setVisibility(View.VISIBLE);

            //set paySlip image/pdf link value and show view button
            paySlipImgPdfLayout.setVisibility(View.VISIBLE);
            paySlipImgPdfLinkTv.setText(documentModel.getPay_slip_exp_letter_image());
            paySlipImgPdfViewBtn.setVisibility(View.VISIBLE);

            if (!documentModel.getPay_slip_exp_letter_image().contains(".pdf")) {
                //show image view
                paySlipExpLtrImageIv.setVisibility(View.VISIBLE);
                Picasso.get().load(documentModel.getPay_slip_exp_letter_image()).fit().into(paySlipExpLtrImageIv, new Callback() {
                    @Override
                    public void onSuccess() {
                        paySlipImageUri = null;
                        paySlipPdfUri = null;
                        addPaySlipExpLtrImgBtn.setText("Update");
                        paySlipPg.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e("PAY_SLIP IMG ERROR", e.toString());
                        paySlipPg.setVisibility(View.GONE);
                        showToast(e.toString());
                    }
                });
            } else {
                paySlipExpLtrImageIv.setVisibility(View.GONE);//hide imageview
                paySlipPg.setVisibility(View.GONE);//hide progress bar
                paySlipImageUri = null;
                paySlipPdfUri = null;
                addPaySlipExpLtrImgBtn.setText("Update");//set update to add paySlip button
            }
        }

        //Pay Slip Second Last Month Image
        if (documentModel.getPay_slip_second_last_month_image() != null && !documentModel.getPay_slip_second_last_month_image().equalsIgnoreCase("")) {
            paySlip2Pg.setVisibility(View.VISIBLE);

            //set paySlip 2 image/pdf link value and show view button
            paySlip2ImgPdfLayout.setVisibility(View.VISIBLE);
            paySlip2ImgPdfLinkTv.setText(documentModel.getPay_slip_second_last_month_image());
            paySlip2ImgPdfViewBtn.setVisibility(View.VISIBLE);

            if (!documentModel.getPay_slip_second_last_month_image().contains(".pdf")) {
                //show image view
                paySlipExp2LtrImageIv.setVisibility(View.VISIBLE);
                Picasso.get().load(documentModel.getPay_slip_second_last_month_image()).fit().into(paySlipExp2LtrImageIv, new Callback() {
                    @Override
                    public void onSuccess() {
                        paySlip2ImageUri = null;
                        paySlip2PdfUri = null;
                        addPaySlipExp2LtrImgBtn.setText("Update");
                        paySlip2Pg.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e("PAY_SLIP_2 IMG ERROR", e.toString());
                        paySlip2Pg.setVisibility(View.GONE);
                        showToast(e.toString());
                    }
                });
            } else {
                paySlipExp2LtrImageIv.setVisibility(View.GONE);//hide imageview
                paySlip2Pg.setVisibility(View.GONE);//hide progress bar
                paySlip2ImageUri = null;
                paySlip2PdfUri = null;
                addPaySlipExp2LtrImgBtn.setText("Update");//set update to add paySlip 2 button
            }
        }

        //Pay Slip Third Last Month Image
        if (documentModel.getPay_slip_third_last_month_image() != null && !documentModel.getPay_slip_third_last_month_image().equalsIgnoreCase("")) {
            paySlip3Pg.setVisibility(View.VISIBLE);

            //set paySlip 3 image/pdf link value and show view button
            paySlip3ImgPdfLayout.setVisibility(View.VISIBLE);
            paySlip3ImgPdfLinkTv.setText(documentModel.getPay_slip_third_last_month_image());
            paySlip3ImgPdfViewBtn.setVisibility(View.VISIBLE);

            if (!documentModel.getPay_slip_third_last_month_image().contains(".pdf")) {
                //show image view
                paySlipExp3LtrImageIv.setVisibility(View.VISIBLE);
                Picasso.get().load(documentModel.getPay_slip_third_last_month_image()).fit().into(paySlipExp3LtrImageIv, new Callback() {
                    @Override
                    public void onSuccess() {
                        paySlip3ImageUri = null;
                        paySlip3PdfUri = null;
                        addPaySlipExp3LtrImgBtn.setText("Update");
                        paySlip3Pg.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e("PAY_SLIP_3 IMG ERROR", e.toString());
                        paySlip3Pg.setVisibility(View.GONE);
                        showToast(e.toString());
                    }
                });
            } else {
                paySlipExp3LtrImageIv.setVisibility(View.GONE);//hide imageview
                paySlip3Pg.setVisibility(View.GONE);//hide progress bar
                paySlip3ImageUri = null;
                paySlip3PdfUri = null;
                addPaySlipExp3LtrImgBtn.setText("Update");//set update to add paySlip button
            }
        }

        //Resign Mail Image
        if (documentModel.getResign_mail_image() != null && !documentModel.getResign_mail_image().equalsIgnoreCase("")) {
            resignMailPg.setVisibility(View.VISIBLE);

            //set resignMail image/pdf link value and show view button
            resignMailImgPdfLayout.setVisibility(View.VISIBLE);
            resignMailImgPdfLinkTv.setText(documentModel.getResign_mail_image());
            resignMailImgPdfViewBtn.setVisibility(View.VISIBLE);

            if (!documentModel.getResign_mail_image().contains(".pdf")) {
                //show image view
                resignMailImageIv.setVisibility(View.VISIBLE);
                Picasso.get().load(documentModel.getResign_mail_image()).fit().into(resignMailImageIv, new Callback() {
                    @Override
                    public void onSuccess() {
                        resignMailImageUri = null;
                        resignMailPdfUri = null;
                        addResignEmailImgBtn.setText("Update");
                        resignMailPg.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e("Resign Mail IMG ERROR", e.toString());
                        resignMailPg.setVisibility(View.GONE);
                        showToast(e.toString());
                    }
                });
            } else {
                resignMailImageIv.setVisibility(View.GONE);//hide imageview
                resignMailPg.setVisibility(View.GONE);//hide progress bar
                resignMailImageUri = null;
                resignMailPdfUri = null;
                addResignEmailImgBtn.setText("Update");//set update to add resignMail button
            }
        }

        //Bank Statement Image
        if (documentModel.getBank_stmt_last_3_mth_image() != null && !documentModel.getBank_stmt_last_3_mth_image().equalsIgnoreCase("")) {
            bankStmtPg.setVisibility(View.VISIBLE);

            //set bankStmt image/pdf link value and show view button
            bankStmtImgPdfLayout.setVisibility(View.VISIBLE);
            bankStmtImgPdfLinkTv.setText(documentModel.getBank_stmt_last_3_mth_image());
            bankStmtImgPdfViewBtn.setVisibility(View.VISIBLE);

            if (!documentModel.getBank_stmt_last_3_mth_image().contains(".pdf")) {
                //show image view
                bankStmtImageIv.setVisibility(View.VISIBLE);
                Picasso.get().load(documentModel.getBank_stmt_last_3_mth_image()).fit().into(bankStmtImageIv, new Callback() {
                    @Override
                    public void onSuccess() {
                        bankStmtImageUri = null;
                        bankStmtPdfUri = null;
                        addBankStmtImgBtn.setText("Update");
                        bankStmtPg.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e("Bank Stmt IMG ERROR", e.toString());
                        bankStmtPg.setVisibility(View.GONE);
                        showToast(e.toString());
                    }
                });
            } else {
                bankStmtImageIv.setVisibility(View.GONE);//hide imageview
                bankStmtPg.setVisibility(View.GONE);//hide progress bar
                bankStmtImageUri = null;
                bankStmtPdfUri = null;
                addBankStmtImgBtn.setText("Update");//set update to add bankStmt button
            }
        }

        //offer Letter Image
        if (documentModel.getOffer_letter() != null && !documentModel.getOffer_letter().equalsIgnoreCase("")) {
            offerLetterPg.setVisibility(View.VISIBLE);

            //set offer Letter image/pdf link value and show view button
            offerLetterPdfLayout.setVisibility(View.VISIBLE);
            offerLetterPdfLinkTv.setText(documentModel.getOffer_letter());
            offerLetterPdfViewBtn.setVisibility(View.VISIBLE);

            if (!documentModel.getOffer_letter().contains(".pdf")) {
                //show image view
                offerLetterIv.setVisibility(View.VISIBLE);
                Picasso.get().load(documentModel.getOffer_letter()).fit().into(offerLetterIv, new Callback() {
                    @Override
                    public void onSuccess() {
                        offerLetterImageUri = null;
                        offerLetterPdfUri = null;
                        addOfferLetterBtn.setText("Update");
                        offerLetterPg.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e("OFFER LETTER ERROR", e.toString());
                        offerLetterPg.setVisibility(View.GONE);
                        showToast(e.toString());
                    }
                });
            } else {
                offerLetterIv.setVisibility(View.GONE);//hide imageview
                offerLetterPg.setVisibility(View.GONE);//hide progress bar
                offerLetterImageUri = null;
                offerLetterPdfUri = null;
                addOfferLetterBtn.setText("Update");//set update to add bankStmt button
            }
        }

        //Vaccine Certificate Image
        if (documentModel.getVaccine_certificate_image() != null && !documentModel.getVaccine_certificate_image().equalsIgnoreCase("")) {
            vaccineCertificatePg.setVisibility(View.VISIBLE);

            //set vaccineCertificate image/pdf link value and show view button
            vaccineCertificateImgPdfLayout.setVisibility(View.VISIBLE);
            vaccineCertificateImgPdfLinkTv.setText(documentModel.getVaccine_certificate_image());
            vaccineCertificateImgPdfViewBtn.setVisibility(View.VISIBLE);

            if (!documentModel.getVaccine_certificate_image().contains(".pdf")) {
                //show image view
                vaccineCertificateImageIv.setVisibility(View.VISIBLE);
                Picasso.get().load(documentModel.getVaccine_certificate_image()).fit().into(vaccineCertificateImageIv, new Callback() {
                    @Override
                    public void onSuccess() {
                        vaccineCertificateImageUri = null;
                        vaccineCertificatePdfUri = null;
                        addVaccineCertificateImgBtn.setText("Update");
                        vaccineCertificatePg.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e("Bank Proof IMG ERROR", e.toString());
                        vaccineCertificatePg.setVisibility(View.GONE);
                        showToast(e.toString());
                    }
                });
            }
            else {
                vaccineCertificateImageIv.setVisibility(View.GONE);//hide imageview
                vaccineCertificatePg.setVisibility(View.GONE);//hide progress bar
                vaccineCertificateImageUri = null;
                vaccineCertificatePdfUri = null;
                addVaccineCertificateImgBtn.setText("Update");//set update to add vaccineCertificate button
            }


        }

        //Other Documents Image
        if (documentModel.getOther_documents_pdf() != null && !documentModel.getOther_documents_pdf().equalsIgnoreCase("")) {
            otherDocumentsPg.setVisibility(View.VISIBLE);

            //set otherDocuments image/pdf link value and show view button
            otherDocumentsImgPdfLayout.setVisibility(View.VISIBLE);
            otherDocumentsImgPdfLinkTv.setText(documentModel.getOther_documents_pdf());
            otherDocumentsImgPdfViewBtn.setVisibility(View.VISIBLE);

            if (!documentModel.getOther_documents_pdf().contains(".pdf")) {
                //show image view
                otherDocumentsImageIv.setVisibility(View.VISIBLE);
                Picasso.get().load(documentModel.getOther_documents_pdf()).fit().into(otherDocumentsImageIv, new Callback() {
                    @Override
                    public void onSuccess() {
                        otherDocumentsImageUri = null;
                        otherDocumentsPdfUri = null;
                        addOtherDocumentsImgBtn.setText("Update");
                        otherDocumentsPg.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e("OTHER IMG ERROR", e.toString());
                        otherDocumentsPg.setVisibility(View.GONE);
                        showToast(e.toString());
                    }
                });
            } else {
                otherDocumentsImageIv.setVisibility(View.GONE);//hide imageview
                otherDocumentsPg.setVisibility(View.GONE);//hide progress bar
                otherDocumentsImageUri = null;
                otherDocumentsPdfUri = null;
                addOtherDocumentsImgBtn.setText("Update");//set update to add otherDocuments button
            }
        }

        //check if request is pending or not
        if (!documentModel.getRequest().isEmpty()) {
            warningTv.setVisibility(View.VISIBLE);

            //hide all buttons
            uploadPassportImgBtn.setAlpha(0.6f);
            uploadPassportImgBtn.setEnabled(false);

            uploadEmployeeSignImgBtn.setAlpha(0.6f);
            uploadEmployeeSignImgBtn.setEnabled(false);

            uploadAadharCardImgBtn.setAlpha(0.6f);
            uploadAadharCardImgBtn.setEnabled(false);

            uploadAadharCardBackImgBtn.setAlpha(0.6f);
            uploadAadharCardBackImgBtn.setEnabled(false);

            uploadPanCardImgBtn.setAlpha(0.6f);
            uploadPanCardImgBtn.setEnabled(false);

            uploadBankProofImgBtn.setAlpha(0.6f);
            uploadBankProofImgBtn.setEnabled(false);

            uploadLastCpExpLtrImgBtn.setAlpha(0.6f);
            uploadLastCpExpLtrImgBtn.setEnabled(false);

            uploadPaySlipExpLtrImgBtn.setAlpha(0.6f);
            uploadPaySlipExpLtrImgBtn.setEnabled(false);

            uploadPaySlipExp2LtrImgBtn.setAlpha(0.6f);
            uploadPaySlipExp2LtrImgBtn.setEnabled(false);

            uploadPaySlipExp3LtrImgBtn.setAlpha(0.6f);
            uploadPaySlipExp3LtrImgBtn.setEnabled(false);

            uploadResignEmailImgBtn.setAlpha(0.6f);
            uploadResignEmailImgBtn.setEnabled(false);

            uploadBankStmtImgBtn.setAlpha(0.6f);
            uploadBankStmtImgBtn.setEnabled(false);

            uploadOfferLetterBtn.setAlpha(0.6f);
            uploadOfferLetterBtn.setEnabled(false);

            uploadVaccineCertificateImgBtn.setAlpha(0.6f);
            uploadVaccineCertificateImgBtn.setEnabled(false);

            uploadOtherDocumentsImgBtn.setAlpha(0.6f);
            uploadOtherDocumentsImgBtn.setEnabled(false);

        } else {
            warningTv.setVisibility(View.GONE);

            //show all buttons
            uploadPassportImgBtn.setAlpha(1f);
            uploadPassportImgBtn.setEnabled(true);

            uploadEmployeeSignImgBtn.setAlpha(1f);
            uploadEmployeeSignImgBtn.setEnabled(true);

            uploadAadharCardImgBtn.setAlpha(1f);
            uploadAadharCardImgBtn.setEnabled(true);

            uploadAadharCardBackImgBtn.setAlpha(1f);
            uploadAadharCardBackImgBtn.setEnabled(true);

            uploadPanCardImgBtn.setAlpha(1f);
            uploadPanCardImgBtn.setEnabled(true);

            uploadBankProofImgBtn.setAlpha(1f);
            uploadBankProofImgBtn.setEnabled(true);

            uploadLastCpExpLtrImgBtn.setAlpha(1f);
            uploadLastCpExpLtrImgBtn.setEnabled(true);

            uploadPaySlipExpLtrImgBtn.setAlpha(1f);
            uploadPaySlipExpLtrImgBtn.setEnabled(true);

            uploadPaySlipExp2LtrImgBtn.setAlpha(1f);
            uploadPaySlipExp2LtrImgBtn.setEnabled(true);

            uploadPaySlipExp3LtrImgBtn.setAlpha(1f);
            uploadPaySlipExp3LtrImgBtn.setEnabled(true);

            uploadResignEmailImgBtn.setAlpha(1f);
            uploadResignEmailImgBtn.setEnabled(true);

            uploadBankStmtImgBtn.setAlpha(1f);
            uploadBankStmtImgBtn.setEnabled(true);

            uploadOfferLetterBtn.setAlpha(1f);
            uploadOfferLetterBtn.setEnabled(true);

            uploadVaccineCertificateImgBtn.setAlpha(1f);
            uploadVaccineCertificateImgBtn.setEnabled(true);

            uploadOtherDocumentsImgBtn.setAlpha(1f);
            uploadOtherDocumentsImgBtn.setEnabled(true);
        }
    }

    /*private final ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                //used to handle result of camera intent
                //get uri of image
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Log.d("ActivityResult: Camera", String.valueOf(imageUri));
                    Intent data = result.getData();//no need here as in camera case we already have image in image uri variable

                    //profileImage.setImageURI(imageUri);
                    if (imageUri != null) {
                        if (value.equalsIgnoreCase("passport")) {
                            //show passport image view
                            passportImageUri = imageUri;
                            passportImageIv.setVisibility(View.VISIBLE);
                            Picasso.get().load(passportImageUri).fit().into(passportImageIv);

                            //hide passport pdf layout and make pdf uri null and set blank value to pdf link textview
                            passportPdfUri = null;
                            passportImgPdfLayout.setVisibility(View.GONE);
                            passportImgPdfLinkTv.setText("");
                            passportImgPdfViewBtn.setVisibility(View.GONE);

                            //set update to passport image button
                            addPassportImgBtn.setText("Update");
                        }
                        if (value.equalsIgnoreCase("emp_sign")) {
                            //show employee sign image view
                            empSignImageUri = imageUri;
                            employeeSignImageIv.setVisibility(View.VISIBLE);
                            Picasso.get().load(empSignImageUri).fit().into(employeeSignImageIv);

                            //hide employee sign pdf layout and make pdf uri null and set blank value to pdf link textview
                            empSignPdfUri = null;
                            employeeSignImgPdfLayout.setVisibility(View.GONE);
                            employeeSignImgPdfLinkTv.setText("");
                            employeeSignImgPdfViewBtn.setVisibility(View.GONE);

                            //set update to employee sign image button
                            addEmployeeSignImgBtn.setText("Update");
                        }
                        if (value.equalsIgnoreCase("aadhar_card")) {
                            //show aadharCard image view
                            aadharCardImageUri = imageUri;
                            aadharCardImageIv.setVisibility(View.VISIBLE);
                            Picasso.get().load(aadharCardImageUri).fit().into(aadharCardImageIv);

                            //hide aadhar Card pdf layout and make pdf uri null and set blank value to pdf link textview
                            aadharCardPdfUri = null;
                            aadharCardImgPdfLayout.setVisibility(View.GONE);
                            aadharCardImgPdfLinkTv.setText("");
                            aadharCardImgPdfViewBtn.setVisibility(View.GONE);

                            //set update to aadharCard image button
                            addAadharCardImgBtn.setText("Update");

                        }

                        if (value.equalsIgnoreCase("aadhar_card_back")) {
                            //show aadharCard back image view
                            aadharCardBackImageUri = imageUri;
                            aadharCardBackImageIv.setVisibility(View.VISIBLE);
                            Picasso.get().load(aadharCardBackImageUri).fit().into(aadharCardBackImageIv);

                            //hide aadhar Card back pdf layout and make pdf uri null and set blank value to pdf link textview
                            aadharCardBackPdfUri = null;
                            aadharCardBackImgPdfLayout.setVisibility(View.GONE);
                            aadharCardBackImgPdfLinkTv.setText("");
                            aadharCardBackImgPdfViewBtn.setVisibility(View.GONE);

                            //set update to aadharCard image button
                            addAadharCardBackImgBtn.setText("Update");

                        }
                        if (value.equalsIgnoreCase("pan_card")) {
                            //show pan Card image view
                            panCardImageUri = imageUri;
                            panCardImageIv.setVisibility(View.VISIBLE);
                            Picasso.get().load(panCardImageUri).fit().into(panCardImageIv);

                            //hide pan Card pdf layout and make pdf uri null and set blank value to pdf link textview
                            panCardPdfUri = null;
                            panCardImgPdfLayout.setVisibility(View.GONE);
                            panCardImgPdfLinkTv.setText("");
                            panCardImgPdfViewBtn.setVisibility(View.GONE);

                            //set update to pan Card image button
                            addPanCardImgBtn.setText("Update");

                        }
                        if (value.equalsIgnoreCase("bank_proof")) {
                            //show bank Proof image view
                            bankProofImageUri = imageUri;
                            bankProofImageIv.setVisibility(View.VISIBLE);
                            Picasso.get().load(bankProofImageUri).fit().into(bankProofImageIv);

                            //hide bank Proof pdf layout and make pdf uri null and set blank value to pdf link textview
                            bankProofPdfUri = null;
                            bankProofImgPdfLayout.setVisibility(View.GONE);
                            bankProofImgPdfLinkTv.setText("");
                            bankProofImgPdfViewBtn.setVisibility(View.GONE);

                            //set update to bank Proof image button
                            addBankProofImgBtn.setText("Update");

                        }
                        if (value.equalsIgnoreCase("cmp_last_exp")) {
                            //show lastCmpExp image view
                            lastCmpExpImageUri = imageUri;
                            lastCpExpLtrImageIv.setVisibility(View.VISIBLE);
                            Picasso.get().load(lastCmpExpImageUri).fit().into(lastCpExpLtrImageIv);

                            //hide lastCmpExp pdf layout and make pdf uri null and set blank value to pdf link textview
                            lastCmpExpPdfUri = null;
                            lastCpExpLtrImgPdfLayout.setVisibility(View.GONE);
                            lastCpExpLtrImgPdfLinkTv.setText("");
                            lastCpExpLtrImgPdfViewBtn.setVisibility(View.GONE);

                            //set update to lastCmpExp image button
                            addLastCpExpLtrImgBtn.setText("Update");

                        }
                        if (value.equalsIgnoreCase("pay_slip")) {
                            //show pay slip image view
                            paySlipImageUri = imageUri;
                            paySlipExpLtrImageIv.setVisibility(View.VISIBLE);
                            Picasso.get().load(paySlipImageUri).fit().into(paySlipExpLtrImageIv);

                            //hide pay slip pdf layout and make pdf uri null and set blank value to pdf link textview
                            paySlipPdfUri = null;
                            paySlipImgPdfLayout.setVisibility(View.GONE);
                            paySlipImgPdfLinkTv.setText("");
                            paySlipImgPdfViewBtn.setVisibility(View.GONE);

                            //set update to pay slip image button
                            addPaySlipExpLtrImgBtn.setText("Update");

                        }
                        if (value.equalsIgnoreCase("pay_slip_second_last_month")) {
                            //show pay slip 2 image view
                            paySlip2ImageUri = imageUri;
                            paySlipExp2LtrImageIv.setVisibility(View.VISIBLE);
                            Picasso.get().load(paySlip2ImageUri).fit().into(paySlipExp2LtrImageIv);

                            //hide pay slip 2 pdf layout and make pdf uri null and set blank value to pdf link textview
                            paySlip2PdfUri = null;
                            paySlip2ImgPdfLayout.setVisibility(View.GONE);
                            paySlip2ImgPdfLinkTv.setText("");
                            paySlip2ImgPdfViewBtn.setVisibility(View.GONE);

                            //set update to pay slip 2 image button
                            addPaySlipExp2LtrImgBtn.setText("Update");

                        }
                        if (value.equalsIgnoreCase("pay_slip_third_last_month")) {
                            //show pay slip 3 image view
                            paySlip3ImageUri = imageUri;
                            paySlipExp3LtrImageIv.setVisibility(View.VISIBLE);
                            Picasso.get().load(paySlip3ImageUri).fit().into(paySlipExp3LtrImageIv);

                            //hide pay slip 3 pdf layout and make pdf uri null and set blank value to pdf link textview
                            paySlip3PdfUri = null;
                            paySlip3ImgPdfLayout.setVisibility(View.GONE);
                            paySlip3ImgPdfLinkTv.setText("");
                            paySlip3ImgPdfViewBtn.setVisibility(View.GONE);

                            //set update to pay slip 3 image button
                            addPaySlipExp3LtrImgBtn.setText("Update");

                        }
                        if (value.equalsIgnoreCase("resign_mail")) {
                            //show resign mail image view
                            resignMailImageUri = imageUri;
                            resignMailImageIv.setVisibility(View.VISIBLE);
                            Picasso.get().load(resignMailImageUri).fit().into(resignMailImageIv);

                            //hide resign mail pdf layout and make pdf uri null and set blank value to pdf link textview
                            resignMailPdfUri = null;
                            resignMailImgPdfLayout.setVisibility(View.GONE);
                            resignMailImgPdfLinkTv.setText("");
                            resignMailImgPdfViewBtn.setVisibility(View.GONE);

                            //set update to resign mail image button
                            addResignEmailImgBtn.setText("Update");

                        }
                        if (value.equalsIgnoreCase("bank_stmt")) {
                            //show bank statement image view
                            bankStmtImageUri = imageUri;
                            bankStmtImageIv.setVisibility(View.VISIBLE);
                            Picasso.get().load(bankStmtImageUri).fit().into(bankStmtImageIv);

                            //hide bank statement pdf layout and make pdf uri null and set blank value to pdf link textview
                            bankStmtPdfUri = null;
                            bankStmtImgPdfLayout.setVisibility(View.GONE);
                            bankStmtImgPdfLinkTv.setText("");
                            bankStmtImgPdfViewBtn.setVisibility(View.GONE);

                            //set update to bank statement image button
                            addBankStmtImgBtn.setText("Update");

                        }
                        if (value.equalsIgnoreCase("vaccine_certificate")) {
                            //show vaccine certificate image view
                            vaccineCertificateImageUri = imageUri;
                            vaccineCertificateImageIv.setVisibility(View.VISIBLE);
                            Picasso.get().load(vaccineCertificateImageUri).fit().into(vaccineCertificateImageIv);

                            //hide vaccineCertificate pdf layout and make pdf uri null and set blank value to pdf link textview
                            vaccineCertificatePdfUri = null;
                            vaccineCertificateImgPdfLayout.setVisibility(View.GONE);
                            vaccineCertificateImgPdfLinkTv.setText("");
                            vaccineCertificateImgPdfViewBtn.setVisibility(View.GONE);

                            //set update to vaccineCertificate image button
                            addVaccineCertificateImgBtn.setText("Update");

                        }
                        if (value.equalsIgnoreCase("other_documents")) {
                            //show other Documents image view
                            otherDocumentsImageUri = imageUri;
                            otherDocumentsImageIv.setVisibility(View.VISIBLE);
                            Picasso.get().load(otherDocumentsImageUri).fit().into(otherDocumentsImageIv);

                            //hide otherDocuments pdf layout and make pdf uri null and set blank value to pdf link textview
                            otherDocumentsPdfUri = null;
                            otherDocumentsImgPdfLayout.setVisibility(View.GONE);
                            otherDocumentsImgPdfLinkTv.setText("");
                            otherDocumentsImgPdfViewBtn.setVisibility(View.GONE);

                            //set update to otherDocuments image button
                            addOtherDocumentsImgBtn.setText("Update");

                        }
                    }
                } else {
                    showToast("Cancelled");
                }
            }
    );

    private final ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    //used to handle result of gallery intent
                    //get uri of image
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();//no need here as in camera case we already have image in image uri variable
                        assert data != null;
                        imageUri = data.getData();
                        Log.d("ActivityResult: Gallery", String.valueOf(imageUri));
                        if (imageUri != null) {
                            if (value.equalsIgnoreCase("passport")) {
                                //show passport image view
                                passportImageUri = imageUri;
                                passportImageIv.setVisibility(View.VISIBLE);
                                Picasso.get().load(passportImageUri).fit().into(passportImageIv);

                                //hide passport pdf layout and make pdf uri null and set blank value to pdf link textview
                                passportPdfUri = null;
                                passportImgPdfLayout.setVisibility(View.GONE);
                                passportImgPdfLinkTv.setText("");
                                passportImgPdfViewBtn.setVisibility(View.GONE);

                                //set update to passport image button
                                addPassportImgBtn.setText("Update");
                            }
                            if (value.equalsIgnoreCase("emp_sign")) {
                                //show employee sign image view
                                empSignImageUri = imageUri;
                                employeeSignImageIv.setVisibility(View.VISIBLE);
                                Picasso.get().load(empSignImageUri).fit().into(employeeSignImageIv);

                                //hide employee sign pdf layout and make pdf uri null and set blank value to pdf link textview
                                empSignPdfUri = null;
                                employeeSignImgPdfLayout.setVisibility(View.GONE);
                                employeeSignImgPdfLinkTv.setText("");
                                employeeSignImgPdfViewBtn.setVisibility(View.GONE);

                                //set update to employee sign image button
                                addEmployeeSignImgBtn.setText("Update");
                            }
                            if (value.equalsIgnoreCase("aadhar_card")) {
                                //show aadharCard image view
                                aadharCardImageUri = imageUri;
                                aadharCardImageIv.setVisibility(View.VISIBLE);
                                Picasso.get().load(aadharCardImageUri).fit().into(aadharCardImageIv);

                                //hide aadhar Card pdf layout and make pdf uri null and set blank value to pdf link textview
                                aadharCardPdfUri = null;
                                aadharCardImgPdfLayout.setVisibility(View.GONE);
                                aadharCardImgPdfLinkTv.setText("");
                                aadharCardImgPdfViewBtn.setVisibility(View.GONE);

                                //set update to aadharCard image button
                                addAadharCardImgBtn.setText("Update");

                            }
                            if (value.equalsIgnoreCase("aadhar_card_back")) {
                                //show aadharCard back image view
                                aadharCardBackImageUri = imageUri;
                                aadharCardBackImageIv.setVisibility(View.VISIBLE);
                                Picasso.get().load(aadharCardBackImageUri).fit().into(aadharCardBackImageIv);

                                //hide aadhar Card back pdf layout and make pdf uri null and set blank value to pdf link textview
                                aadharCardBackPdfUri = null;
                                aadharCardBackImgPdfLayout.setVisibility(View.GONE);
                                aadharCardBackImgPdfLinkTv.setText("");
                                aadharCardBackImgPdfViewBtn.setVisibility(View.GONE);

                                //set update to aadharCard image button
                                addAadharCardBackImgBtn.setText("Update");

                            }
                            if (value.equalsIgnoreCase("pan_card")) {
                                //show pan Card image view
                                panCardImageUri = imageUri;
                                panCardImageIv.setVisibility(View.VISIBLE);
                                Picasso.get().load(panCardImageUri).fit().into(panCardImageIv);

                                //hide pan Card pdf layout and make pdf uri null and set blank value to pdf link textview
                                panCardPdfUri = null;
                                panCardImgPdfLayout.setVisibility(View.GONE);
                                panCardImgPdfLinkTv.setText("");
                                panCardImgPdfViewBtn.setVisibility(View.GONE);

                                //set update to pan Card image button
                                addPanCardImgBtn.setText("Update");

                            }
                            if (value.equalsIgnoreCase("bank_proof")) {
                                //show bank Proof image view
                                bankProofImageUri = imageUri;
                                bankProofImageIv.setVisibility(View.VISIBLE);
                                Picasso.get().load(bankProofImageUri).fit().into(bankProofImageIv);

                                //hide bank Proof pdf layout and make pdf uri null and set blank value to pdf link textview
                                bankProofPdfUri = null;
                                bankProofImgPdfLayout.setVisibility(View.GONE);
                                bankProofImgPdfLinkTv.setText("");
                                bankProofImgPdfViewBtn.setVisibility(View.GONE);

                                //set update to bank Proof image button
                                addBankProofImgBtn.setText("Update");

                            }
                            if (value.equalsIgnoreCase("cmp_last_exp")) {
                                //show lastCmpExp image view
                                lastCmpExpImageUri = imageUri;
                                lastCpExpLtrImageIv.setVisibility(View.VISIBLE);
                                Picasso.get().load(lastCmpExpImageUri).fit().into(lastCpExpLtrImageIv);

                                //hide lastCmpExp pdf layout and make pdf uri null and set blank value to pdf link textview
                                lastCmpExpPdfUri = null;
                                lastCpExpLtrImgPdfLayout.setVisibility(View.GONE);
                                lastCpExpLtrImgPdfLinkTv.setText("");
                                lastCpExpLtrImgPdfViewBtn.setVisibility(View.GONE);

                                //set update to lastCmpExp image button
                                addLastCpExpLtrImgBtn.setText("Update");

                            }
                            if (value.equalsIgnoreCase("pay_slip")) {
                                //show pay slip image view
                                paySlipImageUri = imageUri;
                                paySlipExpLtrImageIv.setVisibility(View.VISIBLE);
                                Picasso.get().load(paySlipImageUri).fit().into(paySlipExpLtrImageIv);

                                //hide pay slip pdf layout and make pdf uri null and set blank value to pdf link textview
                                paySlipPdfUri = null;
                                paySlipImgPdfLayout.setVisibility(View.GONE);
                                paySlipImgPdfLinkTv.setText("");
                                paySlipImgPdfViewBtn.setVisibility(View.GONE);

                                //set update to pay slip image button
                                addPaySlipExpLtrImgBtn.setText("Update");

                            }
                            if (value.equalsIgnoreCase("pay_slip_second_last_month")) {
                                //show pay slip 2 image view
                                paySlip2ImageUri = imageUri;
                                paySlipExp2LtrImageIv.setVisibility(View.VISIBLE);
                                Picasso.get().load(paySlip2ImageUri).fit().into(paySlipExp2LtrImageIv);

                                //hide pay slip 2 pdf layout and make pdf uri null and set blank value to pdf link textview
                                paySlip2PdfUri = null;
                                paySlip2ImgPdfLayout.setVisibility(View.GONE);
                                paySlip2ImgPdfLinkTv.setText("");
                                paySlip2ImgPdfViewBtn.setVisibility(View.GONE);

                                //set update to pay slip 2 image button
                                addPaySlipExp2LtrImgBtn.setText("Update");

                            }
                            if (value.equalsIgnoreCase("pay_slip_third_last_month")) {
                                //show pay slip 3 image view
                                paySlip3ImageUri = imageUri;
                                paySlipExp3LtrImageIv.setVisibility(View.VISIBLE);
                                Picasso.get().load(paySlip3ImageUri).fit().into(paySlipExp3LtrImageIv);

                                //hide pay slip 3 pdf layout and make pdf uri null and set blank value to pdf link textview
                                paySlip3PdfUri = null;
                                paySlip3ImgPdfLayout.setVisibility(View.GONE);
                                paySlip3ImgPdfLinkTv.setText("");
                                paySlip3ImgPdfViewBtn.setVisibility(View.GONE);

                                //set update to pay slip 3 image button
                                addPaySlipExp3LtrImgBtn.setText("Update");

                            }
                            if (value.equalsIgnoreCase("resign_mail")) {
                                //show resign mail image view
                                resignMailImageUri = imageUri;
                                resignMailImageIv.setVisibility(View.VISIBLE);
                                Picasso.get().load(resignMailImageUri).fit().into(resignMailImageIv);

                                //hide resign mail pdf layout and make pdf uri null and set blank value to pdf link textview
                                resignMailPdfUri = null;
                                resignMailImgPdfLayout.setVisibility(View.GONE);
                                resignMailImgPdfLinkTv.setText("");
                                resignMailImgPdfViewBtn.setVisibility(View.GONE);

                                //set update to resign mail image button
                                addResignEmailImgBtn.setText("Update");

                            }
                            if (value.equalsIgnoreCase("bank_stmt")) {
                                //show bank statement image view
                                bankStmtImageUri = imageUri;
                                bankStmtImageIv.setVisibility(View.VISIBLE);
                                Picasso.get().load(bankStmtImageUri).fit().into(bankStmtImageIv);

                                //hide bank statement pdf layout and make pdf uri null and set blank value to pdf link textview
                                bankStmtPdfUri = null;
                                bankStmtImgPdfLayout.setVisibility(View.GONE);
                                bankStmtImgPdfLinkTv.setText("");
                                bankStmtImgPdfViewBtn.setVisibility(View.GONE);

                                //set update to bank statement image button
                                addBankStmtImgBtn.setText("Update");

                            }
                            if (value.equalsIgnoreCase("vaccine_certificate")) {
                                //show vaccine certificate image view
                                vaccineCertificateImageUri = imageUri;
                                vaccineCertificateImageIv.setVisibility(View.VISIBLE);
                                Picasso.get().load(vaccineCertificateImageUri).fit().into(vaccineCertificateImageIv);

                                //hide vaccineCertificate pdf layout and make pdf uri null and set blank value to pdf link textview
                                vaccineCertificatePdfUri = null;
                                vaccineCertificateImgPdfLayout.setVisibility(View.GONE);
                                vaccineCertificateImgPdfLinkTv.setText("");
                                vaccineCertificateImgPdfViewBtn.setVisibility(View.GONE);

                                //set update to vaccineCertificate image button
                                addVaccineCertificateImgBtn.setText("Update");

                            }
                            if (value.equalsIgnoreCase("other_documents")) {
                                //show otherDocuments image view
                                otherDocumentsImageUri = imageUri;
                                otherDocumentsImageIv.setVisibility(View.VISIBLE);
                                Picasso.get().load(otherDocumentsImageUri).fit().into(otherDocumentsImageIv);

                                //hide otherDocuments pdf layout and make pdf uri null and set blank value to pdf link textview
                                otherDocumentsPdfUri = null;
                                otherDocumentsImgPdfLayout.setVisibility(View.GONE);
                                otherDocumentsImgPdfLinkTv.setText("");
                                otherDocumentsImgPdfViewBtn.setVisibility(View.GONE);

                                //set update to otherDocuments image button
                                addOtherDocumentsImgBtn.setText("Update");

                            }
                        }
                    } else {
                        showToast("Cancelled");
                    }
                }
            }
    );*/

    @SuppressLint("Range")
    private final ActivityResultLauncher<Intent> pdfGalleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    // Get the Uri of the selected file
                    Intent data = result.getData();
                    pdfUri = data.getData();
                    String uriString = pdfUri.toString();
                    File myFile = new File(uriString);
                    String path = myFile.getAbsolutePath();
                    String displayName = null;

                    if (uriString.startsWith("content://")) {
                        Cursor cursor = null;
                        try {
                            cursor = getContentResolver().query(pdfUri, null, null, null, null);
                            if (cursor != null && cursor.moveToFirst()) {
                                displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                                Log.d("PDF NAME>>>>  ", displayName);

                                //uploadPDF(displayName, pdfUri);

                                if (pdfUri != null) {
                                    if (pdfValue.equalsIgnoreCase("passport")) {
                                        //hide passport image view
                                        passportImageUri = null;
                                        passportImageIv.setVisibility(View.GONE);

                                        //show passport pdf layout
                                        passportPdfUri = pdfUri;
                                        passportImgPdfLayout.setVisibility(View.VISIBLE);
                                        passportImgPdfLinkTv.setText("Selected : " + displayName);
                                        passportImgPdfViewBtn.setVisibility(View.GONE);

                                        //set update to passport image button
                                        addPassportImgBtn.setText("Update");
                                    }
                                    if (pdfValue.equalsIgnoreCase("emp_sign")) {
                                        //hide employee sign image view
                                        empSignImageUri = null;
                                        employeeSignImageIv.setVisibility(View.GONE);

                                        //show employee sign pdf layout
                                        empSignPdfUri = pdfUri;
                                        employeeSignImgPdfLayout.setVisibility(View.VISIBLE);
                                        employeeSignImgPdfLinkTv.setText("Selected : " + displayName);
                                        employeeSignImgPdfViewBtn.setVisibility(View.GONE);

                                        //set update to employee sign image button
                                        addEmployeeSignImgBtn.setText("Update");

                                    }
                                    if (pdfValue.equalsIgnoreCase("aadhar_card")) {
                                        //hide aadhar Card image view
                                        aadharCardImageUri = null;
                                        aadharCardImageIv.setVisibility(View.GONE);

                                        //show aadhar Card pdf layout
                                        aadharCardPdfUri = pdfUri;
                                        aadharCardImgPdfLayout.setVisibility(View.VISIBLE);
                                        aadharCardImgPdfLinkTv.setText("Selected : " + displayName);
                                        aadharCardImgPdfViewBtn.setVisibility(View.GONE);

                                        //set update to aadhar Card image button
                                        addAadharCardImgBtn.setText("Update");

                                    }
                                    if (pdfValue.equalsIgnoreCase("pan_card")) {
                                        //hide pan Card image view
                                        panCardImageUri = null;
                                        panCardImageIv.setVisibility(View.GONE);

                                        //show pan Card pdf layout
                                        panCardPdfUri = pdfUri;
                                        panCardImgPdfLayout.setVisibility(View.VISIBLE);
                                        panCardImgPdfLinkTv.setText("Selected : " + displayName);
                                        panCardImgPdfViewBtn.setVisibility(View.GONE);

                                        //set update to pan Card image button
                                        addPanCardImgBtn.setText("Update");

                                    }
                                    if (pdfValue.equalsIgnoreCase("bank_proof")) {
                                        //hide bank proof image view
                                        bankProofImageUri = null;
                                        bankProofImageIv.setVisibility(View.GONE);

                                        //show bank proof pdf layout
                                        bankProofPdfUri = pdfUri;
                                        bankProofImgPdfLayout.setVisibility(View.VISIBLE);
                                        bankProofImgPdfLinkTv.setText("Selected : " + displayName);
                                        bankProofImgPdfViewBtn.setVisibility(View.GONE);

                                        //set update to bank proof image button
                                        addBankProofImgBtn.setText("Update");

                                    }
                                    if (pdfValue.equalsIgnoreCase("cmp_last_exp")) {
                                        //hide last company exp letter image view
                                        lastCmpExpImageUri = null;
                                        lastCpExpLtrImageIv.setVisibility(View.GONE);

                                        //show last company exp letter pdf layout
                                        lastCmpExpPdfUri = pdfUri;
                                        lastCpExpLtrImgPdfLayout.setVisibility(View.VISIBLE);
                                        lastCpExpLtrImgPdfLinkTv.setText("Selected : " + displayName);
                                        lastCpExpLtrImgPdfViewBtn.setVisibility(View.GONE);

                                        //set update to last company exp letter image button
                                        addLastCpExpLtrImgBtn.setText("Update");
                                    }
                                    if (pdfValue.equalsIgnoreCase("pay_slip")) {
                                        //hide pay Slip image view
                                        paySlipImageUri = null;
                                        paySlipExpLtrImageIv.setVisibility(View.GONE);

                                        //show pay Slip pdf layout
                                        paySlipPdfUri = pdfUri;
                                        paySlipImgPdfLayout.setVisibility(View.VISIBLE);
                                        paySlipImgPdfLinkTv.setText("Selected : " + displayName);
                                        paySlipImgPdfViewBtn.setVisibility(View.GONE);

                                        //set update to pay Slip image button
                                        addPaySlipExpLtrImgBtn.setText("Update");

                                    }
                                    if (pdfValue.equalsIgnoreCase("resign_mail")) {
                                        //hide resignMail image view
                                        resignMailImageUri = null;
                                        resignMailImageIv.setVisibility(View.GONE);

                                        //show resignMail pdf layout
                                        resignMailPdfUri = pdfUri;
                                        resignMailImgPdfLayout.setVisibility(View.VISIBLE);
                                        resignMailImgPdfLinkTv.setText("Selected : " + displayName);
                                        resignMailImgPdfViewBtn.setVisibility(View.GONE);

                                        //set update to resignMail image button
                                        addResignEmailImgBtn.setText("Update");

                                    }
                                    if (pdfValue.equalsIgnoreCase("bank_stmt")) {
                                        //hide bank stmt image view
                                        bankStmtImageUri = null;
                                        bankStmtImageIv.setVisibility(View.GONE);

                                        //show bank stmt pdf layout
                                        bankStmtPdfUri = pdfUri;
                                        bankStmtImgPdfLayout.setVisibility(View.VISIBLE);
                                        bankStmtImgPdfLinkTv.setText("Selected : " + displayName);
                                        bankStmtImgPdfViewBtn.setVisibility(View.GONE);

                                        //set update to bank stmt image button
                                        addBankStmtImgBtn.setText("Update");

                                    }

                                    if (pdfValue.equalsIgnoreCase("offer_letter")) {
                                        //hide offer letter image view
                                        offerLetterImageUri = null;
                                        offerLetterIv.setVisibility(View.GONE);

                                        //show offer letter pdf layout
                                        offerLetterPdfUri = pdfUri;
                                        offerLetterPdfLayout.setVisibility(View.VISIBLE);
                                        offerLetterPdfLinkTv.setText("Selected : " + displayName);
                                        offerLetterPdfViewBtn.setVisibility(View.GONE);

                                        //set update to offer letter image button
                                        addOfferLetterBtn.setText("Update");

                                    }
                                    if (pdfValue.equalsIgnoreCase("other_documents")) {
                                        //hide otherDocuments image view
                                        otherDocumentsImageUri = null;
                                        otherDocumentsImageIv.setVisibility(View.GONE);

                                        //show otherDocuments pdf layout
                                        otherDocumentsPdfUri = pdfUri;
                                        otherDocumentsImgPdfLayout.setVisibility(View.VISIBLE);
                                        otherDocumentsImgPdfLinkTv.setText("Selected : " + displayName);
                                        otherDocumentsImgPdfViewBtn.setVisibility(View.GONE);

                                        //set update to otherDocuments image button
                                        addOtherDocumentsImgBtn.setText("Update");
                                    }
                                }

                            }
                        } finally {
                            cursor.close();
                        }
                    } else if (uriString.startsWith("file://")) {
                        displayName = myFile.getName();
                        Log.d("nameeeee>>>>  ", displayName);
                    }
                }

            }
    );

    private void uploadPDF(String value, final String pdfName, Uri pdfFile) {

        InputStream iStream = null;
        try {

            iStream = getContentResolver().openInputStream(pdfFile);
            final byte[] inputData = getBytes(iStream);

            VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, URLs.SAVE_PDF_DOCUMENT_UPLOAD_URL,
                    response -> {
                        Log.d("PDF RESULT", new String(response.data));
                        resultQueue.getCache().clear();
                        try {
                            JSONObject jsonObject = new JSONObject(new String(response.data));
                            loadingDialog.hideDialog();

                            jsonObject.toString().replace("\\\\", "");

                            if (jsonObject.getString("status").equals("true")) {
                                Log.d("come::: >>>  ", "yes");
                                arraylist = new ArrayList<>();
                                JSONArray dataArray = jsonObject.getJSONArray("data");

                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject dataObj = dataArray.getJSONObject(i);
                                    url = dataObj.optString("pathToFile");
                                    Log.e("URL", url);
                                }

                                showToast(jsonObject.getString("message"));

                                //load all documents
                                getAllDocuments();
                            } else {
                                showToast(jsonObject.getString("status"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    },
                    error -> {
                        showToast(error.toString());
                        loadingDialog.hideDialog();
                    }) {

                /*
                 * If you want to add more parameters with the image
                 * you can do it here
                 * here we have only one parameter with the image
                 * which is tags
                 * */
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    // params.put("tags", "ccccc");  add string parameters
                    params.put("value", value);
                    params.put("userid", Prevalent.currentOnlineUser.getUserid());
                    return params;
                }

                /*
                 *pass files using below method
                 * */
                @Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<>();
                    params.put("filename", new DataPart(pdfName + System.currentTimeMillis() + ".pdf", inputData));
                    return params;
                }
            };

            volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            resultQueue = Volley.newRequestQueue(DocumentActivity.this);
            resultQueue.add(volleyMultipartRequest);

        } catch (IOException e) {
            showToast(e.toString());
            e.printStackTrace();
        }
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    private class UploadImage extends AsyncTask<String, Void, Boolean> {

        String value, imageName;
        Uri imageUri;
        Bitmap bitmap = null;

        public UploadImage(String value, String imageName, Uri imageUri) {
            this.value = value;
            this.imageName = imageName;
            this.imageUri = imageUri;
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
                String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
                try {
                    jsonObject = new JSONObject();
                    jsonObject.put("userid", Prevalent.currentOnlineUser.getUserid());
                    jsonObject.put("value", value);
                    jsonObject.put("name", imageName + System.currentTimeMillis() + ".jpg");
                    jsonObject.put("image", encodedImage);
                } catch (JSONException e) {
                    Log.e("JSONObject Here", e.toString());
                }
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLs.SAVE_DOCUMENT_UPLOAD_URL, jsonObject,
                        jsonObject -> {
                            Log.d("IMAGE RESPONSE", jsonObject.toString());
                            rQueue.getCache().clear();
                            loadingDialog.hideDialog();
                            try {
                                if (jsonObject.getString("message").equalsIgnoreCase("Yes")) {
                                    Log.d("IMAGE JSON RESPONSE", jsonObject.getString("message"));
                                    showToast(jsonObject.getString("message"));

                                    //load all documents
                                    getAllDocuments();

                                } else {
                                    showToast(jsonObject.getString("message"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }, volleyError -> {
                    Log.e("Error Image Response", volleyError.toString());
                    showToast(volleyError.toString());
                    loadingDialog.hideDialog();
                });
                jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
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

                    }
                });
                rQueue = Volley.newRequestQueue(DocumentActivity.this);
                rQueue.add(jsonObjectRequest);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
    }

    private void showPdfPopupMenu() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        pdfGalleryActivityResultLauncher.launch(intent);
    }

    private void showImagePopupMenu(Button button) {
        PopupMenu imageMenu = new PopupMenu(this, button);
        imageMenu.getMenu().add(Menu.NONE, 0, 0, "Camera");
        imageMenu.getMenu().add(Menu.NONE, 1, 1, "Gallery");

        imageMenu.show();
        //handle menu item clicks
        imageMenu.setOnMenuItemClickListener(imageMenuItem -> {
            int i = imageMenuItem.getItemId();
            if (i == 0) {
                //camera clicked
                if (!checkCameraPermission()) {
                    requestCameraPermission();
                } else {
                    pickFromCamera();
                }
            } else if (i == 1) {
                //gallery clicked
                if (!checkStoragePermission()) {
                    requestStoragePermission();
                } else {
                    pickFromGallery();
                }
            }
            return false;
        });
    }

    //onActivityResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                //picked from gallery
                CropImage.activity(data.getData())
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAutoZoomEnabled(true)
                        .start(this);
            } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAutoZoomEnabled(true)
                        .start(this);
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult crop_result = CropImage.getActivityResult(data);
                Uri resultUri = crop_result.getUri();
                imageUri = resultUri;
                File f = new File(String.valueOf(imageUri));
                //imageName = f.getName();

                /*imageView.setImageURI(imageUri);

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    imageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
                if (value.equalsIgnoreCase("passport")) {
                    //show passport image view
                    passportImageUri = resultUri;
                    passportImageIv.setVisibility(View.VISIBLE);
                    Picasso.get().load(passportImageUri).fit().into(passportImageIv);

                    //hide passport pdf layout and make pdf uri null and set blank value to pdf link textview
                    passportPdfUri = null;
                    passportImgPdfLayout.setVisibility(View.GONE);
                    passportImgPdfLinkTv.setText("");
                    passportImgPdfViewBtn.setVisibility(View.GONE);

                    //set update to passport image button
                    addPassportImgBtn.setText("Update");
                }
                if (value.equalsIgnoreCase("emp_sign")) {
                    //show employee sign image view
                    empSignImageUri = resultUri;
                    employeeSignImageIv.setVisibility(View.VISIBLE);
                    Picasso.get().load(empSignImageUri).fit().into(employeeSignImageIv);

                    //hide employee sign pdf layout and make pdf uri null and set blank value to pdf link textview
                    empSignPdfUri = null;
                    employeeSignImgPdfLayout.setVisibility(View.GONE);
                    employeeSignImgPdfLinkTv.setText("");
                    employeeSignImgPdfViewBtn.setVisibility(View.GONE);

                    //set update to employee sign image button
                    addEmployeeSignImgBtn.setText("Update");
                }
                if (value.equalsIgnoreCase("aadhar_card")) {
                    //show aadharCard image view
                    aadharCardImageUri = resultUri;
                    aadharCardImageIv.setVisibility(View.VISIBLE);
                    Picasso.get().load(aadharCardImageUri).fit().into(aadharCardImageIv);

                    //hide aadhar Card pdf layout and make pdf uri null and set blank value to pdf link textview
                    aadharCardPdfUri = null;
                    aadharCardImgPdfLayout.setVisibility(View.GONE);
                    aadharCardImgPdfLinkTv.setText("");
                    aadharCardImgPdfViewBtn.setVisibility(View.GONE);

                    //set update to aadharCard image button
                    addAadharCardImgBtn.setText("Update");

                }
                if (value.equalsIgnoreCase("aadhar_card_back")) {
                    //show aadharCard back image view
                    aadharCardBackImageUri = resultUri;
                    aadharCardBackImageIv.setVisibility(View.VISIBLE);
                    Picasso.get().load(aadharCardBackImageUri).fit().into(aadharCardBackImageIv);

                    //hide aadhar Card back pdf layout and make pdf uri null and set blank value to pdf link textview
                    aadharCardBackPdfUri = null;
                    aadharCardBackImgPdfLayout.setVisibility(View.GONE);
                    aadharCardBackImgPdfLinkTv.setText("");
                    aadharCardBackImgPdfViewBtn.setVisibility(View.GONE);

                    //set update to aadharCard image button
                    addAadharCardBackImgBtn.setText("Update");

                }
                if (value.equalsIgnoreCase("pan_card")) {
                    //show pan Card image view
                    panCardImageUri = resultUri;
                    panCardImageIv.setVisibility(View.VISIBLE);
                    Picasso.get().load(panCardImageUri).fit().into(panCardImageIv);

                    //hide pan Card pdf layout and make pdf uri null and set blank value to pdf link textview
                    panCardPdfUri = null;
                    panCardImgPdfLayout.setVisibility(View.GONE);
                    panCardImgPdfLinkTv.setText("");
                    panCardImgPdfViewBtn.setVisibility(View.GONE);

                    //set update to pan Card image button
                    addPanCardImgBtn.setText("Update");

                }
                if (value.equalsIgnoreCase("bank_proof")) {
                    //show bank Proof image view
                    bankProofImageUri = resultUri;
                    bankProofImageIv.setVisibility(View.VISIBLE);
                    Picasso.get().load(bankProofImageUri).fit().into(bankProofImageIv);

                    //hide bank Proof pdf layout and make pdf uri null and set blank value to pdf link textview
                    bankProofPdfUri = null;
                    bankProofImgPdfLayout.setVisibility(View.GONE);
                    bankProofImgPdfLinkTv.setText("");
                    bankProofImgPdfViewBtn.setVisibility(View.GONE);

                    //set update to bank Proof image button
                    addBankProofImgBtn.setText("Update");

                }
                if (value.equalsIgnoreCase("cmp_last_exp")) {
                    //show lastCmpExp image view
                    lastCmpExpImageUri = resultUri;
                    lastCpExpLtrImageIv.setVisibility(View.VISIBLE);
                    Picasso.get().load(lastCmpExpImageUri).fit().into(lastCpExpLtrImageIv);

                    //hide lastCmpExp pdf layout and make pdf uri null and set blank value to pdf link textview
                    lastCmpExpPdfUri = null;
                    lastCpExpLtrImgPdfLayout.setVisibility(View.GONE);
                    lastCpExpLtrImgPdfLinkTv.setText("");
                    lastCpExpLtrImgPdfViewBtn.setVisibility(View.GONE);

                    //set update to lastCmpExp image button
                    addLastCpExpLtrImgBtn.setText("Update");

                }
                if (value.equalsIgnoreCase("pay_slip")) {
                    //show pay slip image view
                    paySlipImageUri = resultUri;
                    paySlipExpLtrImageIv.setVisibility(View.VISIBLE);
                    Picasso.get().load(paySlipImageUri).fit().into(paySlipExpLtrImageIv);

                    //hide pay slip pdf layout and make pdf uri null and set blank value to pdf link textview
                    paySlipPdfUri = null;
                    paySlipImgPdfLayout.setVisibility(View.GONE);
                    paySlipImgPdfLinkTv.setText("");
                    paySlipImgPdfViewBtn.setVisibility(View.GONE);

                    //set update to pay slip image button
                    addPaySlipExpLtrImgBtn.setText("Update");

                }
                if (value.equalsIgnoreCase("pay_slip_second_last_month")) {
                    //show pay slip 2 image view
                    paySlip2ImageUri = resultUri;
                    paySlipExp2LtrImageIv.setVisibility(View.VISIBLE);
                    Picasso.get().load(paySlip2ImageUri).fit().into(paySlipExp2LtrImageIv);

                    //hide pay slip 2 pdf layout and make pdf uri null and set blank value to pdf link textview
                    paySlip2PdfUri = null;
                    paySlip2ImgPdfLayout.setVisibility(View.GONE);
                    paySlip2ImgPdfLinkTv.setText("");
                    paySlip2ImgPdfViewBtn.setVisibility(View.GONE);

                    //set update to pay slip 2 image button
                    addPaySlipExp2LtrImgBtn.setText("Update");

                }
                if (value.equalsIgnoreCase("pay_slip_third_last_month")) {
                    //show pay slip 3 image view
                    paySlip3ImageUri = resultUri;
                    paySlipExp3LtrImageIv.setVisibility(View.VISIBLE);
                    Picasso.get().load(paySlip3ImageUri).fit().into(paySlipExp3LtrImageIv);

                    //hide pay slip 3 pdf layout and make pdf uri null and set blank value to pdf link textview
                    paySlip3PdfUri = null;
                    paySlip3ImgPdfLayout.setVisibility(View.GONE);
                    paySlip3ImgPdfLinkTv.setText("");
                    paySlip3ImgPdfViewBtn.setVisibility(View.GONE);

                    //set update to pay slip 3 image button
                    addPaySlipExp3LtrImgBtn.setText("Update");

                }
                if (value.equalsIgnoreCase("resign_mail")) {
                    //show resign mail image view
                    resignMailImageUri = resultUri;
                    resignMailImageIv.setVisibility(View.VISIBLE);
                    Picasso.get().load(resignMailImageUri).fit().into(resignMailImageIv);

                    //hide resign mail pdf layout and make pdf uri null and set blank value to pdf link textview
                    resignMailPdfUri = null;
                    resignMailImgPdfLayout.setVisibility(View.GONE);
                    resignMailImgPdfLinkTv.setText("");
                    resignMailImgPdfViewBtn.setVisibility(View.GONE);

                    //set update to resign mail image button
                    addResignEmailImgBtn.setText("Update");

                }
                if (value.equalsIgnoreCase("bank_stmt")) {
                    //show bank statement image view
                    bankStmtImageUri = resultUri;
                    bankStmtImageIv.setVisibility(View.VISIBLE);
                    Picasso.get().load(bankStmtImageUri).fit().into(bankStmtImageIv);

                    //hide bank statement pdf layout and make pdf uri null and set blank value to pdf link textview
                    bankStmtPdfUri = null;
                    bankStmtImgPdfLayout.setVisibility(View.GONE);
                    bankStmtImgPdfLinkTv.setText("");
                    bankStmtImgPdfViewBtn.setVisibility(View.GONE);

                    //set update to bank statement image button
                    addBankStmtImgBtn.setText("Update");

                }

                if (value.equalsIgnoreCase("offer_letter")) {
                    //show offer letter image view
                    offerLetterImageUri = resultUri;
                    offerLetterIv.setVisibility(View.VISIBLE);
                    Picasso.get().load(offerLetterImageUri).fit().into(offerLetterIv);

                    //hide offer letter pdf layout and make pdf uri null and set blank value to pdf link textview
                    offerLetterPdfUri = null;
                    offerLetterPdfLayout.setVisibility(View.GONE);
                    offerLetterPdfLinkTv.setText("");
                    offerLetterPdfViewBtn.setVisibility(View.GONE);

                    //set update to offer letter image button
                    addOfferLetterBtn.setText("Update");

                }
                if (value.equalsIgnoreCase("vaccine_certificate")) {
                    //show vaccine certificate image view
                    vaccineCertificateImageUri = resultUri;
                    vaccineCertificateImageIv.setVisibility(View.VISIBLE);
                    Picasso.get().load(vaccineCertificateImageUri).fit().into(vaccineCertificateImageIv);

                    //hide vaccineCertificate pdf layout and make pdf uri null and set blank value to pdf link textview
                    vaccineCertificatePdfUri = null;
                    vaccineCertificateImgPdfLayout.setVisibility(View.GONE);
                    vaccineCertificateImgPdfLinkTv.setText("");
                    vaccineCertificateImgPdfViewBtn.setVisibility(View.GONE);

                    //set update to vaccineCertificate image button
                    addVaccineCertificateImgBtn.setText("Update");

                }
                if (value.equalsIgnoreCase("other_documents")) {
                    //show otherDocuments image view
                    otherDocumentsImageUri = resultUri;
                    otherDocumentsImageIv.setVisibility(View.VISIBLE);
                    Picasso.get().load(otherDocumentsImageUri).fit().into(otherDocumentsImageIv);

                    //hide otherDocuments pdf layout and make pdf uri null and set blank value to pdf link textview
                    otherDocumentsPdfUri = null;
                    otherDocumentsImgPdfLayout.setVisibility(View.GONE);
                    otherDocumentsImgPdfLinkTv.setText("");
                    otherDocumentsImgPdfViewBtn.setVisibility(View.GONE);

                    //set update to otherDocuments image button
                    addOtherDocumentsImgBtn.setText("Update");

                }

                copyFileOrDirectory("" + imageUri.getPath(), "" + getDir("VivoImages", MODE_PRIVATE));
            }
        }
    }

    private void copyFileOrDirectory(String srcDir, String desDir) {
        try {
            File src = new File(srcDir);
            File des = new File(desDir, src.getName());
            if (src.isDirectory()) {
                String[] files = src.list();
                int filesLength = files.length;
                for (String file : files) {
                    String src1 = new File(src, file).getPath();
                    String dst1 = des.getPath();

                    copyFileOrDirectory(src1, dst1);
                }
            } else {
                copyFile(src, des);
            }
        } catch (Exception e) {
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void copyFile(File srcDir, File desDir) throws IOException {
        if (!desDir.getParentFile().exists()) {
            desDir.mkdirs();
        }
        if (!desDir.exists()) {
            desDir.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(srcDir).getChannel();
            destination = new FileOutputStream(desDir).getChannel();
            destination.transferFrom(source, 0, source.size());

            imageUri = Uri.parse(desDir.getPath());
            Log.d("ImagePath", "copyFile:" + imageUri);
        } catch (Exception e) {
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }

    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");//only image
        //galleryActivityResultLauncher.launch(intent);
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
        //mGetContent.launch("image/*");
    }

    private void pickFromCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Pick");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Sample Image description");
        //put image uri
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        //intent to open camera for image
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        //cameraActivityResultLauncher.launch(intent);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);

        //Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //mGetContentCamera.launch(takePictureIntent);

    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return result && result1;
    }


    private void requestCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this, cameraPermissions13, CAMERA_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
        }
    }

    private boolean checkStoragePermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == (PackageManager.PERMISSION_GRANTED);
        } else {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        }
    }

    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this, storagePermissions13, STORAGE_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    try {
                        boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                        boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                        if (cameraAccepted && storageAccepted) {
                            pickFromCamera();
                        } else {
                            showToast("Camera & Storage permissions are required");
                        }
                    } catch (Exception e) {
                        Log.e("Error", e.toString());
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (storageAccepted) {
                        pickFromGallery();
                    } else {
                        showToast("Storage permissions is required");
                    }
                }
            }
            break;
        }
    }

    private void getAllDocuments() {
        loadingDialog.showDialog("Loading...");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.GET_ALL_DOCUMENTS_DATA_URL, response -> {
            Log.d("DOCUMENT IMAGES", response);
            loadingDialog.hideDialog();

            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);

                    DocumentModel documentModel = new DocumentModel();
                    documentModel.setId(object.getString("id"));
                    documentModel.setUserid(object.getString("userid"));
                    documentModel.setPassport_size_image(object.getString("passport_size_image"));
                    documentModel.setEmployee_sign_image(object.getString("employee_sign_image"));
                    documentModel.setAadhar_card_image(object.getString("aadhar_card_image"));
                    documentModel.setAadhar_card_back_image(object.getString("aadhar_card_back_image"));
                    documentModel.setPan_card_image(object.getString("pan_card_image"));
                    documentModel.setBank_proof_image(object.getString("bank_proof_image"));
                    documentModel.setLast_company_exp_letter_image(object.getString("last_company_exp_letter_image"));
                    documentModel.setPay_slip_exp_letter_image(object.getString("pay_slip_exp_letter_image"));
                    documentModel.setPay_slip_second_last_month_image(object.getString("pay_slip_second_last_month_image"));
                    documentModel.setPay_slip_third_last_month_image(object.getString("pay_slip_third_last_month_image"));
                    documentModel.setResign_mail_image(object.getString("resign_mail_image"));
                    documentModel.setBank_stmt_last_3_mth_image(object.getString("bank_stmt_last_3_mth_image"));
                    documentModel.setOffer_letter(object.getString("offer_letter_image"));
                    documentModel.setVaccine_certificate_image(object.getString("vaccine_certificate_image"));
                    documentModel.setOther_documents_pdf(object.getString("other_documents_pdf"));

                    documentModel.setRequest(object.getString("request"));
                    documentModel.setHo_request(object.getString("ho_request"));

                    //set Image to image view
                    setDataToFields(documentModel);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.e("DOCUMENT ERROR", error.toString());
            showToast(error.toString());
        }) {
            @NonNull
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("userid", Prevalent.currentOnlineUser.getUserid());
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

    private void initialization() {
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        backBtn = findViewById(R.id.backBtn);
        //Passport Image Fields
        passportImgLayout = findViewById(R.id.passportImgLayout);
        passportImageIv = findViewById(R.id.passportImageIv);
        passportPg = findViewById(R.id.passportPg);
        passportImgPdfLayout = findViewById(R.id.passportImgPdfLayout);
        passportImgPdfLinkTv = findViewById(R.id.passportImgPdfLinkTv);
        passportImgPdfViewBtn = findViewById(R.id.passportImgPdfViewBtn);
        addPassportImgBtn = findViewById(R.id.addPassportImgBtn);
        uploadPassportImgBtn = findViewById(R.id.uploadPassportImgBtn);

        //Employee Sign Image
        empSignImgLayout = findViewById(R.id.empSignImgLayout);
        employeeSignImageIv = findViewById(R.id.employeeSignImageIv);
        empSignPg = findViewById(R.id.empSignPg);
        employeeSignImgPdfLayout = findViewById(R.id.employeeSignImgPdfLayout);
        employeeSignImgPdfLinkTv = findViewById(R.id.employeeSignImgPdfLinkTv);
        employeeSignImgPdfViewBtn = findViewById(R.id.employeeSignImgPdfViewBtn);
        addEmployeeSignImgBtn = findViewById(R.id.addEmployeeSignImgBtn);
        uploadEmployeeSignImgBtn = findViewById(R.id.uploadEmployeeSignImgBtn);

        //Aadhar Card Image
        aadharCardImgLayout = findViewById(R.id.aadharCardImgLayout);
        aadharCardImageIv = findViewById(R.id.aadharCardImageIv);
        aadharCardPg = findViewById(R.id.aadharCardPg);
        aadharCardImgPdfLayout = findViewById(R.id.aadharCardImgPdfLayout);
        aadharCardImgPdfLinkTv = findViewById(R.id.aadharCardImgPdfLinkTv);
        aadharCardImgPdfViewBtn = findViewById(R.id.aadharCardImgPdfViewBtn);
        addAadharCardImgBtn = findViewById(R.id.addAadharCardImgBtn);
        uploadAadharCardImgBtn = findViewById(R.id.uploadAadharCardImgBtn);

        //Aadhar Card Back Image
        aadharCardBackImgLayout = findViewById(R.id.aadharCardBackImgLayout);
        aadharCardBackImageIv = findViewById(R.id.aadharCardBackImageIv);
        aadharCardBackPg = findViewById(R.id.aadharCardBackPg);
        aadharCardBackImgPdfLayout = findViewById(R.id.aadharCardBackImgPdfLayout);
        aadharCardBackImgPdfLinkTv = findViewById(R.id.aadharCardBackImgPdfLinkTv);
        aadharCardBackImgPdfViewBtn = findViewById(R.id.aadharCardBackImgPdfViewBtn);
        addAadharCardBackImgBtn = findViewById(R.id.addAadharCardBackImgBtn);
        uploadAadharCardBackImgBtn = findViewById(R.id.uploadAadharCardBackImgBtn);

        //Pan Card Image
        panCardImgLayout = findViewById(R.id.panCardImgLayout);
        panCardImageIv = findViewById(R.id.panCardImageIv);
        panCardPg = findViewById(R.id.panCardPg);
        panCardImgPdfLayout = findViewById(R.id.panCardImgPdfLayout);
        panCardImgPdfLinkTv = findViewById(R.id.panCardImgPdfLinkTv);
        panCardImgPdfViewBtn = findViewById(R.id.panCardImgPdfViewBtn);
        addPanCardImgBtn = findViewById(R.id.addPanCardImgBtn);
        uploadPanCardImgBtn = findViewById(R.id.uploadPanCardImgBtn);

        //Bank Proof Image
        bankProofImgLayout = findViewById(R.id.bankProofImgLayout);
        bankProofImageIv = findViewById(R.id.bankProofImageIv);
        bankProofPg = findViewById(R.id.bankProofPg);
        bankProofImgPdfLayout = findViewById(R.id.bankProofImgPdfLayout);
        bankProofImgPdfLinkTv = findViewById(R.id.bankProofImgPdfLinkTv);
        bankProofImgPdfViewBtn = findViewById(R.id.bankProofImgPdfViewBtn);
        addBankProofImgBtn = findViewById(R.id.addBankProofImgBtn);
        uploadBankProofImgBtn = findViewById(R.id.uploadBankProofImgBtn);

        //Last Company Exp Letter Image
        lastCmpExpImgLayout = findViewById(R.id.lastCmpExpImgLayout);
        lastCpExpLtrImageIv = findViewById(R.id.lastCpExpLtrImageIv);
        lastCmpExpPg = findViewById(R.id.lastCmpExpPg);
        lastCpExpLtrImgPdfLayout = findViewById(R.id.lastCpExpLtrImgPdfLayout);
        lastCpExpLtrImgPdfLinkTv = findViewById(R.id.lastCpExpLtrImgPdfLinkTv);
        lastCpExpLtrImgPdfViewBtn = findViewById(R.id.lastCpExpLtrImgPdfViewBtn);
        addLastCpExpLtrImgBtn = findViewById(R.id.addLastCpExpLtrImgBtn);
        uploadLastCpExpLtrImgBtn = findViewById(R.id.uploadLastCpExpLtrImgBtn);

        //Pay Slip Image 1
        paySlipImgLayout = findViewById(R.id.paySlipImgLayout);
        paySlipExpLtrImageIv = findViewById(R.id.paySlipExpLtrImageIv);
        paySlipPg = findViewById(R.id.paySlipPg);
        paySlipImgPdfLayout = findViewById(R.id.paySlipImgPdfLayout);
        paySlipImgPdfLinkTv = findViewById(R.id.paySlipImgPdfLinkTv);
        paySlipImgPdfViewBtn = findViewById(R.id.paySlipImgPdfViewBtn);
        addPaySlipExpLtrImgBtn = findViewById(R.id.addPaySlipExpLtrImgBtn);
        uploadPaySlipExpLtrImgBtn = findViewById(R.id.uploadPaySlipExpLtrImgBtn);

        //Pay Slip Image 2
        paySlip2ImgLayout = findViewById(R.id.paySlip2ImgLayout);
        paySlipExp2LtrImageIv = findViewById(R.id.paySlipExp2LtrImageIv);
        paySlip2ImgPdfLayout = findViewById(R.id.paySlip2ImgPdfLayout);
        paySlip2ImgPdfLinkTv = findViewById(R.id.paySlip2ImgPdfLinkTv);
        paySlip2ImgPdfViewBtn = findViewById(R.id.paySlip2ImgPdfViewBtn);
        paySlip2Pg = findViewById(R.id.paySlip2Pg);
        addPaySlipExp2LtrImgBtn = findViewById(R.id.addPaySlipExp2LtrImgBtn);
        uploadPaySlipExp2LtrImgBtn = findViewById(R.id.uploadPaySlipExp2LtrImgBtn);

        //Pay Slip Image 3
        paySlip3ImgLayout = findViewById(R.id.paySlip3ImgLayout);
        paySlipExp3LtrImageIv = findViewById(R.id.paySlipExp3LtrImageIv);
        paySlip3ImgPdfLayout = findViewById(R.id.paySlip3ImgPdfLayout);
        paySlip3ImgPdfLinkTv = findViewById(R.id.paySlip3ImgPdfLinkTv);
        paySlip3ImgPdfViewBtn = findViewById(R.id.paySlip3ImgPdfViewBtn);
        paySlip3Pg = findViewById(R.id.paySlip3Pg);
        addPaySlipExp3LtrImgBtn = findViewById(R.id.addPaySlipExp3LtrImgBtn);
        uploadPaySlipExp3LtrImgBtn = findViewById(R.id.uploadPaySlipExp3LtrImgBtn);

        //Resign Mail Image
        resignMailImgLayout = findViewById(R.id.resignMailImgLayout);
        resignMailImageIv = findViewById(R.id.resignMailImageIv);
        resignMailPg = findViewById(R.id.resignMailPg);
        resignMailImgPdfLayout = findViewById(R.id.resignMailImgPdfLayout);
        resignMailImgPdfLinkTv = findViewById(R.id.resignMailImgPdfLinkTv);
        resignMailImgPdfViewBtn = findViewById(R.id.resignMailImgPdfViewBtn);
        addResignEmailImgBtn = findViewById(R.id.addResignEmailImgBtn);
        uploadResignEmailImgBtn = findViewById(R.id.uploadResignEmailImgBtn);

        //Bank Statement Image
        bankStmtImgLayout = findViewById(R.id.bankStmtImgLayout);
        bankStmtImageIv = findViewById(R.id.bankStmtImageIv);
        bankStmtPg = findViewById(R.id.bankStmtPg);
        bankStmtImgPdfLayout = findViewById(R.id.bankStmtImgPdfLayout);
        bankStmtImgPdfLinkTv = findViewById(R.id.bankStmtImgPdfLinkTv);
        bankStmtImgPdfViewBtn = findViewById(R.id.bankStmtImgPdfViewBtn);
        addBankStmtImgBtn = findViewById(R.id.addBankStmtImgBtn);
        uploadBankStmtImgBtn = findViewById(R.id.uploadBankStmtImgBtn);

        //Appointment Letter Image
        offerLetterLayout = findViewById(R.id.offerLetterLayout);
        offerLetterIv = findViewById(R.id.offerLetterIv);
        offerLetterPg = findViewById(R.id.offerLetterPg);
        offerLetterPdfLayout = findViewById(R.id.offerLetterPdfLayout);
        offerLetterPdfLinkTv = findViewById(R.id.offerLetterPdfLinkTv);
        offerLetterPdfViewBtn = findViewById(R.id.offerLetterPdfViewBtn);
        addOfferLetterBtn = findViewById(R.id.addOfferLetterBtn);
        uploadOfferLetterBtn = findViewById(R.id.uploadOfferLetterBtn);

        //Other Documents Image
        otherDocumentsImgLayout = findViewById(R.id.otherDocumentsImgLayout);
        otherDocumentsImageIv = findViewById(R.id.otherDocumentsImageIv);
        otherDocumentsPg = findViewById(R.id.otherDocumentsPg);
        otherDocumentsImgPdfLayout = findViewById(R.id.otherDocumentsImgPdfLayout);
        otherDocumentsImgPdfLinkTv = findViewById(R.id.otherDocumentsImgPdfLinkTv);
        otherDocumentsImgPdfViewBtn = findViewById(R.id.otherDocumentsImgPdfViewBtn);
        addOtherDocumentsImgBtn = findViewById(R.id.addOtherDocumentsImgBtn);
        uploadOtherDocumentsImgBtn = findViewById(R.id.uploadOtherDocumentsImgBtn);

        //Vaccine Certificate Image
        vaccineCertificateImgLayout = findViewById(R.id.vaccineCertificateImgLayout);
        vaccineCertificateImageIv = findViewById(R.id.vaccineCertificateImageIv);
        vaccineCertificatePg = findViewById(R.id.vaccineCertificatePg);
        vaccineCertificateImgPdfLayout = findViewById(R.id.vaccineCertificateImgPdfLayout);
        vaccineCertificateImgPdfLinkTv = findViewById(R.id.vaccineCertificateImgPdfLinkTv);
        vaccineCertificateImgPdfViewBtn = findViewById(R.id.vaccineCertificateImgPdfViewBtn);
        addVaccineCertificateImgBtn = findViewById(R.id.addVaccineCertificateImgBtn);
        uploadVaccineCertificateImgBtn = findViewById(R.id.uploadVaccineCertificateImgBtn);

        warningTv = findViewById(R.id.warningTv);

        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        cameraPermissions13 = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions13 = new String[]{Manifest.permission.READ_MEDIA_IMAGES};

        loadingDialog = new LoadingDialog(this);
    }

    private void showToast(String message) {
        Toast.makeText(DocumentActivity.this, message, Toast.LENGTH_SHORT).show();
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
}