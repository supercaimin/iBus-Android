package cn.homecaught.ibus_jhr.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.content.pm.PackageManager;

import java.io.File;
import java.io.IOException;

import org.json.JSONObject;


import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import com.nostra13.universalimageloader.core.ImageLoader;

import cn.homecaught.ibus_jhr.MyApplication;
import cn.homecaught.ibus_jhr.util.CameraDialog;
import cn.homecaught.ibus_jhr.util.HttpData;
import cn.homecaught.ibus_jhr.util.ImageUntils;
import cn.homecaught.ibus_jhr.view.CircleImageView;
import cn.homecaught.ibus_jhr.R;


public class AddStudentActivity extends AppCompatActivity {
    private CircleImageView ivHeadImageView;
    private EditText etFistName;
    private EditText etLastName;
    private EditText etSN;
    private EditText etGrade;
    private CameraDialog cameraDialog;

    private ProgressDialog progressDialog;

    private String mHeadPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("提示");
        progressDialog.setMessage("请求网络中，请稍等...");

        ivHeadImageView = (CircleImageView) findViewById(R.id.ivHead);
        ivHeadImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cameraDialog == null) {
                    cameraDialog = new CameraDialog(AddStudentActivity.this);
                }
                cameraDialog.show();

            }
        });
        etFistName = (EditText) findViewById(R.id.etFirstName);
        etLastName = (EditText) findViewById(R.id.etLastName);
        etSN = (EditText) findViewById(R.id.etSN);
        etGrade = (EditText) findViewById(R.id.grade);

        findViewById(R.id.btn_finish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                new AddChildTask(etFistName.getText().toString(), etLastName.getText().toString(),etSN.getText().toString(), etGrade.getText().toString()).execute();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 123: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_SHORT).show();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onActivityResult(int request, int result, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(request, result, data);
        if (result == RESULT_OK) {
            switch (request) {
                case CameraDialog.CAMERA_CODE: {
                    File outFile = new File(CameraDialog.UPLOAD_FILE_PARENT_PATH
                            + "img" + System.currentTimeMillis() + ".png");
                    boolean comSuc = ImageUntils.compressBmpByOptions(
                            cameraDialog.getCameraFile(), 400, 400, outFile);
                    if (comSuc) {
                        new UploadImageTask(outFile.getAbsolutePath()).execute();
                    }
                }
                break;
                case CameraDialog.IMAGE_CODE: {
                    final boolean isKitKat = Build.VERSION.SDK_INT >= 19;
                    if (!isKitKat) {
                        Bitmap bm = null;
                        ContentResolver resolver = getContentResolver();
                        try {
                            Uri originalUri = data.getData();
                            bm = MediaStore.Images.Media.getBitmap(resolver,
                                    originalUri);
                            String[] proj = {MediaStore.Images.Media.DATA};
                            Cursor cursor = getContentResolver().query(originalUri,
                                    proj, null, null, null);
                            int column_index = cursor
                                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                            cursor.moveToFirst();
                            String path = cursor.getString(column_index);

                            File outFile = new File(
                                    CameraDialog.UPLOAD_FILE_PARENT_PATH + "img"
                                            + System.currentTimeMillis() + ".png");
                            boolean comSuc = ImageUntils.compressBmpByOptions(
                                    new File(path), 200, 200, outFile);
                            if (comSuc) {
                                new UploadImageTask(outFile.getAbsolutePath()).execute();
                            }

                        } catch (IOException e) {
                        }
                    } else {
                        Uri selectedImage = data.getData();
                        String path = ImageUntils.getPath(this, selectedImage);
                        File outFile = new File(
                                CameraDialog.UPLOAD_FILE_PARENT_PATH + "img"
                                        + System.currentTimeMillis() + ".png");
                        boolean comSuc = ImageUntils.compressBmpByOptions(new File(
                                path), 200, 200, outFile);
                        if (comSuc) {
                            //       UpImgeForEnd(outFile.getAbsolutePath());
                            new UploadImageTask(outFile.getAbsolutePath()).execute();
                        }

                    }
                }
                break;
                case CameraDialog.CLIP_IMAGE:

                    break;

                default:
                    break;
            }
        }
    }

    //{"status":true,"msg":"","info":{"state":"SUCCESS","url":"\/data\/upload\/images\/20160629\/150030_760113.png","title":"150030_760113.png","original":"img1467183628581.png","type":".png","size":25917}}
    public class UploadImageTask extends AsyncTask<Void, Void, String>{

        private String mFilePath;
        public UploadImageTask(String filePath) {
            super();
            mFilePath = filePath;
        }

        @Override
        protected String doInBackground(Void... params) {
            return HttpData.uploadImage(mFilePath);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                boolean status = jsonObject.getBoolean("status");
                if (status) {
                    mHeadPath = jsonObject.getJSONObject("info").getString("url");
                    ImageLoader.getInstance().displayImage(HttpData.getBaseUrl() + mHeadPath, ivHeadImageView);
                    Toast.makeText(AddStudentActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(AddStudentActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

    public class AddChildTask extends AsyncTask<Void, Void, String> {

        private String mSN;
        private String mFirstName;
        private String mLastName;
        private String mGrade;

        public AddChildTask(String sn, String firstName, String lastName, String grade) {
            super();

            mSN = sn;
            mFirstName = firstName;
            mLastName = lastName;
            mGrade = grade;
        }

        @Override
        protected String doInBackground(Void... params) {
            String userID = MyApplication.getInstance().getLoginUser().getId();
            return HttpData.addChild(userID, mFirstName, mLastName, mSN, mGrade);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            progressDialog.hide();

            try {
                JSONObject jsonObject = new JSONObject(s);
                boolean status = jsonObject.getBoolean("status");
                Toast.makeText(AddStudentActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                if (status) {
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
}
