package com.zoomlee.Zoomlee.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.zoomlee.Zoomlee.R;
import com.zoomlee.Zoomlee.dao.DaoHelper;
import com.zoomlee.Zoomlee.dao.DaoHelpersContainer;
import com.zoomlee.Zoomlee.net.model.Country;
import com.zoomlee.Zoomlee.net.model.Tax;
import com.zoomlee.Zoomlee.net.model.User;
import com.zoomlee.Zoomlee.ui.MaterialDialog;
import com.zoomlee.Zoomlee.ui.view.TaxDateView;
import com.zoomlee.Zoomlee.utils.IntentUtils;
import com.zoomlee.Zoomlee.utils.RequestCodes;
import com.zoomlee.Zoomlee.utils.SharedPreferenceUtils;

import java.util.Calendar;

public class CreateEditTaxActivity extends SecuredActionBarActivity implements View.OnClickListener {

    private static final String EXTRA_TAX = "tax_to_edit";

    private Tax tax;
    private TaxDateView arrivalDateView;
    private TaxDateView departureDateView;
    private TextView countryTv;
    private View submitFrame;
    private View deleteTaxBtn;
    private View trackAutoLayout;
    private ToggleButton toggleBtn;
    private View datesTitle;
    private View countryTitle;
    private ScrollView scrollView;

    public static void startToEditTax(Activity activity, Tax tax) {
        Intent intent = getIntentForStart(activity, false, tax);
        activity.startActivityForResult(intent, RequestCodes.CREATE_EDIT_TAX);
    }

    public static void startToCreateTax(Activity activity) {
        Intent intent = new Intent(activity, CreateEditTaxActivity.class);
        intent.putExtra(IntentUtils.EXTRA_OPEN_WITH_PIN, false);
        activity.startActivityForResult(intent, RequestCodes.CREATE_EDIT_TAX);
    }

    public static Intent getIntentForStart(Context context, boolean withPin, Tax tax) {
        Intent intent = new Intent(context, CreateEditTaxActivity.class);
        intent.putExtra(IntentUtils.EXTRA_OPEN_WITH_PIN, withPin);
        intent.putExtra(EXTRA_TAX, tax);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCustomizedView(R.layout.activity_create_edit_tax, false);

        tax = getIntent().getParcelableExtra(EXTRA_TAX);

        if (tax == null) tax = createNewTax();

        initUI();
    }

    private Tax createNewTax() {
        Tax newTax = new Tax();
        User user = SharedPreferenceUtils.getUtils().getUserSettings();
        newTax.setUserId(user.getRemoteId());

        Calendar calendar = Calendar.getInstance();
        newTax.setArrival(calendar.getTimeInMillis() / 1000L);
        newTax.setDeparture(newTax.getArrival());

        return newTax;
    }

    private void initUI() {
        countryTv = (TextView) findViewById(R.id.country);
        arrivalDateView = (TaxDateView) findViewById(R.id.arrivalDateView);
        departureDateView = (TaxDateView) findViewById(R.id.departureDateView);
        submitFrame = findViewById(R.id.submitFrame);
        deleteTaxBtn = findViewById(R.id.deleteTax);
        trackAutoLayout = findViewById(R.id.trackAutoLayout);
        toggleBtn = (ToggleButton) findViewById(R.id.toggleBtn);
        countryTitle = findViewById(R.id.countryTitle);
        datesTitle = findViewById(R.id.datesTitle);
        scrollView = (ScrollView) findViewById(R.id.scrollView);

        trackAutoLayout.setVisibility(tax.getDeparture() <= 0 ? View.VISIBLE : View.GONE);
        deleteTaxBtn.setVisibility(tax.getId() == -1 ? View.GONE : View.VISIBLE);

        initListeners();
        initValues();
    }

    private void initValues() {
        if (tax.getCountryId() == -1 || tax.getCountryName() == null) {
            countryTv.setText(R.string.tap_to_select_country);
            submitFrame.setEnabled(false);
        } else {
            countryTv.setText(tax.getCountryName());
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(tax.getArrival() * 1000L);
        arrivalDateView.setCalendar(calendar);

        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(tax.getDeparture() * 1000L);
        departureDateView.setCalendar(calendar);

        if (tax.getDeparture() <= 0) toggleBtn.setChecked(true);
    }

    private void initListeners() {
        deleteTaxBtn.setOnClickListener(this);
        countryTv.setOnClickListener(this);
        submitFrame.setOnClickListener(this);
        findViewById(R.id.cancelFrame).setOnClickListener(this);

        toggleBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) applyAutoCheckInState();
                else applyNormalState();
            }
        });

        arrivalDateView.setDropDownListener(new TaxDateView.OnStateChangedListener() {
            @Override
            public void onDropDown() {
                scrollAnimation();

                if (departureDateView.isExpanded())
                    departureDateView.changeState();
            }

            @Override
            public void onDateChanged(Calendar arrival) {
                Calendar departure = departureDateView.getCalendar();
                if (departure.before(arrival)) {
                    arrivalDateView.setCalendar(departure);
                }
            }
        });
        departureDateView.setDropDownListener(new TaxDateView.OnStateChangedListener() {
            @Override
            public void onDropDown() {
                scrollAnimation();

                if (arrivalDateView.isExpanded())
                    arrivalDateView.changeState();
            }

            @Override
            public void onDateChanged(Calendar departure) {
                Calendar arrival = arrivalDateView.getCalendar();
                if (departure.before(arrival)) {
                    departureDateView.setCalendar(arrival);
                }
            }
        });
    }

    private void scrollAnimation() {
        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                super.applyTransformation(interpolatedTime, t);
                scrollView.smoothScrollTo(0, arrivalDateView.getBottom());
            }
        };
        animation.setDuration(TaxDateView.ANIM_DURATION);
        scrollView.startAnimation(animation);
    }

    private void saveTax() {
        // set arrival time to a start of the day
        Calendar arrivalDate = arrivalDateView.getCalendar();
        arrivalDate.set(Calendar.HOUR_OF_DAY, 0);
        arrivalDate.set(Calendar.MINUTE, 0);
        arrivalDate.set(Calendar.MILLISECOND, 0);
        tax.setArrival(arrivalDate.getTimeInMillis() / 1000L);

        // set departure time to the end of the day
        Calendar departureDate = departureDateView.getCalendar();
        departureDate.set(Calendar.HOUR_OF_DAY, 23);
        departureDate.set(Calendar.MINUTE, 59);
        departureDate.set(Calendar.MILLISECOND, 999);

        // set up tax to save
        if (toggleBtn.isChecked()) {
            int prevCountryId = SharedPreferenceUtils.getUtils().getIntSetting(SharedPreferenceUtils.CURRENT_COUNTRY_KEY);
            if (prevCountryId != tax.getCountryId()) {
                tax.setDeparture(departureDate.getTimeInMillis() / 1000L);
            }
        } else {
            tax.setDeparture(departureDate.getTimeInMillis() / 1000L);
        }

        DaoHelper<Tax> taxDaoHelper = DaoHelpersContainer.getInstance().getDaoHelper(Tax.class);
        taxDaoHelper.saveLocalChanges(this, tax);

        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    private void applyAutoCheckInState() {
        countryTv.setEnabled(false);
        arrivalDateView.setEnabled(false);
        departureDateView.setEnabled(false);
        countryTitle.setEnabled(false);
        datesTitle.setEnabled(false);

        departureDateView.setTracking();
        tax.setDeparture(-1);
    }

    private void applyNormalState() {
        countryTv.setEnabled(true);
        arrivalDateView.setEnabled(true);
        departureDateView.setEnabled(true);
        countryTitle.setEnabled(true);
        datesTitle.setEnabled(true);

        Calendar calendar = Calendar.getInstance();
        departureDateView.setCalendar(calendar);
        tax.setDeparture(calendar.getTimeInMillis() / 1000L);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.country:
                CountriesActivity.startForResult(this, tax.getCountryId());
                break;
            case R.id.cancelFrame:
                finish();
                break;
            case R.id.submitFrame:
                saveTax();
                break;
            case R.id.deleteTax:
                showDeleteTaxDialog();
                break;
            default:
                break;
        }
    }

    private void deleteTax(){
        DaoHelper<Tax> taxDaoHelper = DaoHelpersContainer.getInstance().getDaoHelper(Tax.class);
        taxDaoHelper.deleteItem(this, tax);
        finish();
    }

    private void showDeleteTaxDialog() {
        final MaterialDialog mMaterialDialog = new MaterialDialog(this);
        mMaterialDialog
                .setTitle(R.string.delete_trip)
                .setCancelable(true)
                .setPositiveButton(getString(R.string.title_delete).toUpperCase(), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteTax();
                    }
                })
                .setNegativeButton(getString(R.string.cancel).toUpperCase(), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialDialog.dismiss();
                    }
                });

        mMaterialDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCodes.GET_COUNTRY) {
            unpin();

            if (resultCode == Activity.RESULT_OK) {
                int countryId = data.getIntExtra(CountriesActivity.COUNTRY_ID_KEY, -1);
                tax.setCountryId(countryId);
                DaoHelper<Country> countryDaoHelper = DaoHelpersContainer.getInstance().getDaoHelper(Country.class);
                Country countryObj = countryDaoHelper.getItemByRemoteId(this, tax.getCountryId());
                tax.setCountryName(countryObj.getName());
                countryTv.setText(countryObj.getName());

                submitFrame.setEnabled(true);
            }
        }
    }
}