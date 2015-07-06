package com.zoomlee.Zoomlee.net.model.helpers;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.util.SparseIntArray;

import com.artifex.mupdfdemo.MuPDFCore;
import com.zoomlee.Zoomlee.dao.DaoHelper;
import com.zoomlee.Zoomlee.dao.DaoHelpersContainer;
import com.zoomlee.Zoomlee.net.model.Country;
import com.zoomlee.Zoomlee.net.model.FieldsType;
import com.zoomlee.Zoomlee.net.model.Form;
import com.zoomlee.Zoomlee.net.model.FormField;
import com.zoomlee.Zoomlee.net.model.Person;
import com.zoomlee.Zoomlee.net.model.User;
import com.zoomlee.Zoomlee.provider.helpers.FieldsHelper;
import com.zoomlee.Zoomlee.utils.DeveloperUtil;
import com.zoomlee.Zoomlee.utils.SharedPreferenceUtils;
import com.zoomlee.Zoomlee.utils.Util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by
 *
 * @author Evgen Marinin <ievgen.marinin@alterplay.com>
 * @since 04.06.15.
 */
public class FormLoader {

    // assets/cache file names
    public static final String PDF_FORM_FOR_VIEW = "preview_form.pdf";
    public static final String PDF_FORM_FOR_PRINT = "print_form.pdf";

    private static final SparseIntArray PRE_FILLED_FIELDS = new SparseIntArray();
    static {
        PRE_FILLED_FIELDS.append(FormField.FAMILY_NAME_TYPE_ID, FieldsType.LAST_NAME_TYPE_ID);
        PRE_FILLED_FIELDS.append(FormField.FIRST_NAME_TYPE_ID, FieldsType.FIRST_NAME_TYPE_ID);
        PRE_FILLED_FIELDS.append(FormField.BIRTH_DATE_MOUNTH1_TYPE_ID, FieldsType.DATE_OF_BIRTH_TYPE_ID);
        PRE_FILLED_FIELDS.append(FormField.BIRTH_DATE_MOUNTH2_TYPE_ID, FieldsType.DATE_OF_BIRTH_TYPE_ID);
        PRE_FILLED_FIELDS.append(FormField.BIRTH_DATE_DAY1_TYPE_ID, FieldsType.DATE_OF_BIRTH_TYPE_ID);
        PRE_FILLED_FIELDS.append(FormField.BIRTH_DATE_DAY2_TYPE_ID, FieldsType.DATE_OF_BIRTH_TYPE_ID);
        PRE_FILLED_FIELDS.append(FormField.BIRTH_DATE_YEAR1_TYPE_ID, FieldsType.DATE_OF_BIRTH_TYPE_ID);
        PRE_FILLED_FIELDS.append(FormField.BIRTH_DATE_YEAR2_TYPE_ID, FieldsType.DATE_OF_BIRTH_TYPE_ID);
        PRE_FILLED_FIELDS.append(FormField.PASSPORT_ISSUED_BY_TYPE_ID, FieldsType.COUNTRY_OF_ISSUE_TYPE_ID);
        PRE_FILLED_FIELDS.append(FormField.PASSPORT_NUMBER_TYPE_ID, FieldsType.PASSPORT_NUMBER_TYPE_ID);
        PRE_FILLED_FIELDS.append(FormField.COUNTRY_OF_RESIDENCE_TYPE_ID, -1);
    }

    private Context context;
    private Form form;
    private boolean preFilled;

    public FormLoader(Context context, Form form) {
        this.context = context;
        this.form = form;
    }

    public FormLoader(Context context, Form form, boolean preFilled) {
        this.context = context;
        this.form = form;
        this.preFilled = preFilled;
    }

    /**
     * pre fill form using person's documents fields
     */
    public void preFillFields() {
        if (!preFilled) {
            boolean changed = preFillFieldsFromDocs();
            if (changed) {
                DaoHelper<Form> formDaoHelper = DaoHelpersContainer.getInstance().getDaoHelper(Form.class);
                formDaoHelper.saveLocalChanges(context, form);
            }
            preFilled = true;
        }
    }

    /**
     * pre fill form using person's documents fields
     * @return true - if changed, false - otherwise
     */
    private boolean preFillFieldsFromDocs() {
        boolean changed = false;
        Set<String> typeSet = new HashSet<>();
        List<FormField> fieldsToFill = new ArrayList<>();
        for (FormField formField : form.getData()) {
            if (formField.getValue() != null || PRE_FILLED_FIELDS.indexOfKey(formField.getFieldTypeId()) < 0) continue;

            if (formField.getFieldTypeId() == FormField.COUNTRY_OF_RESIDENCE_TYPE_ID) {
                if (form.getLocalPersonId() == Person.ME_ID)
                    changed = changed || preFillHomeCountryField(formField);
                continue;
            }
            typeSet.add(String.valueOf(PRE_FILLED_FIELDS.get(formField.getFieldTypeId())));
            fieldsToFill.add(formField);
        }

        Cursor cursor = getDocFieldsCursor(typeSet);
        if (cursor == null) return changed;

        int fieldTypeIdIndex = Util.findIndex(FieldsHelper.FieldsHistoryContract.ALL_COLUMNS_PROJECTION, FieldsHelper.FieldsHistoryContract.FIELD_TYPE_ID);
        int fieldValueIndex = Util.findIndex(FieldsHelper.FieldsHistoryContract.ALL_COLUMNS_PROJECTION, FieldsHelper.FieldsHistoryContract.FIELD_VALUE);
        while (cursor.moveToNext()) {
            int fieldTypeId = cursor.getInt(fieldTypeIdIndex);
            String fieldValue = cursor.getString(fieldValueIndex);

            Iterator<FormField> iterator = fieldsToFill.iterator();
            int month = 0;
            int day = 0;
            int year = 0;
            Calendar calendar = Calendar.getInstance();
            if (fieldTypeId == FieldsType.DATE_OF_BIRTH_TYPE_ID) {
                calendar.setTimeInMillis(Integer.valueOf(fieldValue) * 1000L);
                month = calendar.get(Calendar.MONTH) + 1;
                day = calendar.get(Calendar.DAY_OF_MONTH);
                year = calendar.get(Calendar.YEAR) % 100;
            }
            while (iterator.hasNext()) {
                FormField formField = iterator.next();
                if (PRE_FILLED_FIELDS.get(formField.getFieldTypeId()) == fieldTypeId) {
                    switch (formField.getFieldTypeId()) {
                        case FormField.BIRTH_DATE_MOUNTH1_TYPE_ID:
                            formField.setValue(String.valueOf(month / 10));
                            break;
                        case FormField.BIRTH_DATE_MOUNTH2_TYPE_ID:
                            formField.setValue(String.valueOf(month % 10));
                            break;
                        case FormField.BIRTH_DATE_DAY1_TYPE_ID:
                            formField.setValue(String.valueOf(day / 10));
                            break;
                        case FormField.BIRTH_DATE_DAY2_TYPE_ID:
                            formField.setValue(String.valueOf(day % 10));
                            break;
                        case FormField.BIRTH_DATE_YEAR1_TYPE_ID:
                            formField.setValue(String.valueOf(year / 10));
                            break;
                        case FormField.BIRTH_DATE_YEAR2_TYPE_ID:
                            formField.setValue(String.valueOf(year % 10));
                            break;
                        default:
                            formField.setValue(fieldValue);
                            iterator.remove();
                            break;
                    }
                }
            }
        }

        cursor.close();

        return changed;
    }

    private Cursor getDocFieldsCursor(Set<String> typeSet) {
        List<String> argsList = new ArrayList<>();
        argsList.add(String.valueOf(form.getLocalPersonId()));
        argsList.addAll(typeSet);

        String[] args = argsList.toArray(new String[]{});
        Cursor cursor = context.getContentResolver().query(FieldsHelper.FieldsHistoryContract.CONTENT_URI, null, null, args, null);
        return cursor;
    }

    private boolean preFillHomeCountryField(FormField formField) {
        boolean changed = false;
        User user = SharedPreferenceUtils.getUtils().getUserSettings();
        if (user.getCountryId() == -1) return changed;

        DaoHelper<Country> countryDaoHelper = DaoHelpersContainer.getInstance().getDaoHelper(Country.class);
        Country country = countryDaoHelper.getItemByRemoteId(context, user.getCountryId());
        if (country.getName() != null) {
            formField.setValue(country.getName());
            changed = true;
        }

        return changed;
    }

    /**
     *
     * @return filled pdf (for pre view) file in cache dir
     */
    public File getViewAblePdfForm() {
        return getPdfForm(false);
    }

    /**
     *
     * create filled pdf (for printing) file in cache dir
     * @param onLoadCompleteListener listener to result
     */
    public void getPrintAblePdfFormAsync(OnLoadCompleteListener onLoadCompleteListener) {
        if (onLoadCompleteListener == null) throw new IllegalArgumentException("OnLoadCompleteListener is NULL!!!");

        new LoadPrintPdfTask(onLoadCompleteListener).execute();
    }

    private File getPdfForm(boolean forPrint) {
        preFillFields();
        String pdfFile = forPrint ? PDF_FORM_FOR_PRINT : PDF_FORM_FOR_VIEW;
        PDDocument pdfDocument = loadBasePdf(pdfFile);
        fillPdfFields(pdfDocument);

        File file = new File(context.getCacheDir(), pdfFile);
        try {
            pdfDocument.save(file);
            pdfDocument.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }

    private void setField(PDAcroForm acroForm, String name, String value) throws IOException {
        if (value == null) return;
        PDField field = acroForm.getField(name);
        if (field instanceof PDTextField) {
            ((PDTextField)field).setValue(value);
        }
        else
        {
            System.err.println("No field found with name:" + name);
        }
    }

    private void fillPdfFields(PDDocument pdf) {
        try {
            PDDocumentCatalog docCatalog = pdf.getDocumentCatalog();
            PDAcroForm acroForm = docCatalog.getAcroForm();
            for (FormField formField: form.getData()) {
                setField(acroForm, formField.getFieldTypeId() + "", formField.getValue());
            }
        }
        catch (Exception e){
            DeveloperUtil.michaelLog(e);
            e.printStackTrace();
        }
    }

    private PDDocument loadBasePdf(String pdfFile) {
        PDDocument pdf = null;
        try {
            pdf = PDDocument.load(context.getAssets().open(pdfFile));
        } catch (Exception e){
            DeveloperUtil.michaelLog(e);
            e.printStackTrace();
        }

        return pdf;
    }

    public interface OnLoadCompleteListener {
        void loadCompleted(File formFile);
    }

    private class LoadPrintPdfTask extends AsyncTask<Void, Void, MuPDFCore> {

        private OnLoadCompleteListener onLoadCompleteListener;
        private File printPdfFile;

        public LoadPrintPdfTask(OnLoadCompleteListener onLoadCompleteListener) {
            this.onLoadCompleteListener = onLoadCompleteListener;
        }

        @Override
        protected MuPDFCore doInBackground(Void... params) {
            printPdfFile = getPdfForm(true);
            MuPDFCore muPDFCore = null;
            try {
                muPDFCore = new MuPDFCore(context, printPdfFile.getAbsolutePath());
                if (muPDFCore != null && (muPDFCore.needsPassword() || muPDFCore.countPages() == 0)) {
                    muPDFCore = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return muPDFCore;
        }

        @Override
        protected void onPostExecute(MuPDFCore muPDFCore) {
            if (muPDFCore == null) onLoadCompleteListener.loadCompleted(printPdfFile);
            else new DrawPageTask(muPDFCore, printPdfFile, onLoadCompleteListener).execute();
        }
    }

    private class DrawPageTask extends AsyncTask<Void, Void, File> {
        private MuPDFCore muPDFCore;
        private File printPdfFile;
        private OnLoadCompleteListener onLoadCompleteListener;

        public DrawPageTask(MuPDFCore muPDFCore, File printPdfFile, OnLoadCompleteListener onLoadCompleteListener) {
            this.muPDFCore = muPDFCore;
            this.printPdfFile = printPdfFile;
            this.onLoadCompleteListener = onLoadCompleteListener;
        }

        @Override
        protected File doInBackground(Void... params) {
            File result = null;
            try {
                PointF pageSize = muPDFCore.getPageSize(0);

                if (pageSize.x != 100f || pageSize.y != 100f) {
                    Bitmap bitmap = Bitmap.createBitmap((int) pageSize.x, (int) pageSize.y, Bitmap.Config.ARGB_8888);
                    MuPDFCore.Cookie cookie = muPDFCore.new Cookie();
                    muPDFCore.drawPage(bitmap, 0, (int) pageSize.x, (int) pageSize.y, 0, 0, (int) pageSize.x, (int) pageSize.y, cookie);

                    File imageFile = new File(context.getCacheDir(), "form.png");
                    FileOutputStream fout = new FileOutputStream(imageFile);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 0, fout);
                    fout.close();
                    result = imageFile;
                    bitmap.recycle();
                    cookie.destroy();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                muPDFCore.onDestroy();
                muPDFCore = null;
            }
            return result;
        }

        @Override
        protected void onPostExecute(File imageFile) {
            if (imageFile != null) onLoadCompleteListener.loadCompleted(imageFile);
            else onLoadCompleteListener.loadCompleted(printPdfFile);
        }
    }
}
