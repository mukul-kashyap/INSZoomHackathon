package com.zoomlee.Zoomlee.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.artifex.mupdfdemo.listener.OnLoadCompleteListener;
import com.artifex.mupdfdemo.ui.MuPDFView;
import com.zoomlee.Zoomlee.R;
import com.zoomlee.Zoomlee.net.model.File;
import com.zoomlee.Zoomlee.net.model.FilesType;
import com.zoomlee.Zoomlee.provider.FilesProvider;
import com.zoomlee.Zoomlee.ui.view.ImageLoadingView;
import com.zoomlee.Zoomlee.ui.view.TouchImageView;
import com.zoomlee.Zoomlee.utils.FileLoader;
import com.zoomlee.Zoomlee.utils.GAEvents;
import com.zoomlee.Zoomlee.utils.GAUtil;
import com.zoomlee.Zoomlee.utils.IntentUtils;
import com.zoomlee.Zoomlee.utils.RequestCodes;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


/**
 * Created by
 *
 * @author Michael Oknov <misha.oknov@alterplay.com>
 * @since 3/11/15
 */
public class AttachmentActivity extends SecuredActionBarActivity implements FileLoader.FileLoadListener {

    private static final String EXTRA_FILE = "zoomlee_extra_file";
    private static final String EXTRA_DOC_NAME = "zoomlee_extra_document_name";
    private static final int IMAGE_MAX_SIZE = 2048 * 2048;

    private ImageLoadingView loadingView;
    private TouchImageView imageView;
    private MuPDFView pdfView;

    private String documentName;
    private FileLoader fileLoader;

    public static void startWithFile(Activity activity, File file, String docName) {
        Intent intent = new Intent(activity, AttachmentActivity.class);
        intent.putExtra(IntentUtils.EXTRA_OPEN_WITH_PIN, false);
        intent.putExtra(EXTRA_FILE, file);
        intent.putExtra(EXTRA_DOC_NAME, docName);
        activity.startActivityForResult(intent, RequestCodes.OPEN_ATTACHMENT);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attachment);

        File file = getIntent().getParcelableExtra(EXTRA_FILE);
        documentName = getIntent().getStringExtra(EXTRA_DOC_NAME);

        fileLoader = new FileLoader(this, file, this);

        pdfView = (MuPDFView) findViewById(R.id.pdfView);
        imageView = (TouchImageView) findViewById(R.id.imageView);
        loadingView = (ImageLoadingView) findViewById(R.id.loading);

        fileLoader.revisitFile();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        updateABAvatar(null);
        GAUtil.getUtil().timeSpent(GAEvents.ACTION_FILE_DETAILS_VIEW);
    }

    private void loadImage(String filePath) {
        pdfView.setVisibility(View.GONE);
        imageView.setVisibility(View.VISIBLE);

        java.io.File imgFile = new java.io.File(filePath);

        try {
            Bitmap myBitmap = getBitmap(imgFile);
            imageView.setImageBitmap(myBitmap);
            if (myBitmap == null) loadingView.showError();
        } catch (FileNotFoundException fnfe) {
            Log.w("loadImage", "Can't find file - " + filePath);
            loadingView.showError();
        }
    }

    private Bitmap getBitmap(java.io.File file) throws FileNotFoundException {
        InputStream in;
        try {
            in = new FileInputStream(file);

            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, o);
            in.close();

            int scale = 2 * o.outWidth * o.outHeight / IMAGE_MAX_SIZE;

            Bitmap b;
            in = new FileInputStream(file);
            if (scale > 1) {
                o = new BitmapFactory.Options();
                o.inSampleSize = scale;
                b = BitmapFactory.decodeStream(in, null, o);

                System.gc();
            } else {
                b = BitmapFactory.decodeStream(in);
            }
            in.close();

            return b;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void loadPdf(String filePath) {
        imageView.setVisibility(View.GONE);
        pdfView.setVisibility(View.VISIBLE);
        OnLoadCompleteListener loadCompleteListener = new OnLoadCompleteListener() {
            @Override
            public void loadComplete() {

            }

            @Override
            public void loadCrashed() {
                pdfView.setVisibility(View.GONE);
                loadingView.showError();
            }
        };

        pdfView.setLoadListener(loadCompleteListener);
        pdfView.loadFromFile(filePath);
    }

    @Override
    protected void onResume() {
        super.onResume();

        fileLoader.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();

        fileLoader.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_doc_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_mail:
                share();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void share() {
        if (fileLoader.isFileLoaded()) {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("message/rfc822");
            i.putExtra(Intent.EXTRA_SUBJECT, documentName);
            i.putExtra(Intent.EXTRA_STREAM, Uri.parse("content://" + FilesProvider.FILE_DIR_AUTHORITY + "/"
                    + new java.io.File(fileLoader.getFile().getLocalPath()).getName()));
            startActivityForResult(Intent.createChooser(i, null), RequestCodes.SEND_EMAIL);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCodes.SEND_EMAIL) {
            unpin();
        }
    }

    @Override
    public void onFileLoaded(File file) {
        loadingView.hide();
        if (file.getTypeId() == FilesType.IMAGE_TYPE) {
            loadImage(file.getLocalPath());
        } else {
            loadPdf(file.getLocalPath());
        }
    }

    @Override
    public void onLoadStarted() {
        loadingView.show();
    }

    @Override
    public void onFileGone() {
        finish();
    }
}
