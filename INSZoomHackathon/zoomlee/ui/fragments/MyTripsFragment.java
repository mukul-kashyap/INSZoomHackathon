package com.zoomlee.Zoomlee.ui.fragments;


import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;

import com.zoomlee.Zoomlee.R;
import com.zoomlee.Zoomlee.dao.DaoHelpersContainer;
import com.zoomlee.Zoomlee.dao.TaxDaoHelper;
import com.zoomlee.Zoomlee.net.model.Person;
import com.zoomlee.Zoomlee.net.model.Tax;
import com.zoomlee.Zoomlee.provider.FilesProvider;
import com.zoomlee.Zoomlee.ui.MaterialDialog;
import com.zoomlee.Zoomlee.ui.activity.CreateEditTaxActivity;
import com.zoomlee.Zoomlee.ui.activity.SubscriptionActivity;
import com.zoomlee.Zoomlee.ui.adapters.IncitationsAdapter;
import com.zoomlee.Zoomlee.ui.adapters.TaxesAdapter;
import com.zoomlee.Zoomlee.ui.view.DateRangePicker;
import com.zoomlee.Zoomlee.ui.view.EmptyView;
import com.zoomlee.Zoomlee.ui.view.ZMTextView;
import com.zoomlee.Zoomlee.utils.BillingUtils;
import com.zoomlee.Zoomlee.utils.DeveloperUtil;
import com.zoomlee.Zoomlee.utils.Events;
import com.zoomlee.Zoomlee.utils.RequestCodes;
import com.zoomlee.Zoomlee.utils.SharedPreferenceUtils;
import com.zoomlee.Zoomlee.utils.tax.TaxReportGenerator;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class MyTripsFragment extends Fragment implements TaxesAdapter.ItemListener, DateRangePicker.RangeSetListener {

    @InjectView(R.id.date_range_picker)
    DateRangePicker dateRangePicker;

    @InjectView(R.id.text_send_report)
    ZMTextView textReport;

    @InjectView(R.id.empty_taxes)
    EmptyView emptyTaxes;

    @InjectView(R.id.taxesView)
    ListView listTaxes;

    @InjectView(R.id.addNewBtn)
    ImageView addNewBtn;

    @InjectView(R.id.lockView)
    View lockView;

    @InjectView(R.id.renewBadge)
    ZMTextView renewBadge;

    private TaxesAdapter adapter;
    private DataLoadAsyncTask loadTask;
    private TaxDaoHelper taxDaoHelper = (TaxDaoHelper) DaoHelpersContainer.getInstance().getDaoHelper(Tax.class);
    private boolean isLock = false;

    public static MyTripsFragment newInstance() {
        return new MyTripsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_trips, container, false);

        ButterKnife.inject(this, view);

        adapter = new TaxesAdapter(getActivity(), this);
        listTaxes.setAdapter(IncitationsAdapter.wrap(adapter, adapter));
        listTaxes.setEmptyView(emptyTaxes);

        dateRangePicker.above(listTaxes);
        dateRangePicker.setRangeSetListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        loadData();

        isLock = !BillingUtils.isPro(SharedPreferenceUtils.getUtils().getUserSettings());
        updateUiLock();
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);

        cancelTask();
    }

    private void loadData() {
        cancelTask();
        loadTask = new DataLoadAsyncTask();
        loadTask.execute(dateRangePicker.getFromDate(), dateRangePicker.getToDate());
    }

    private void cancelTask() {
        if (loadTask != null) {
            loadTask.cancel(true);
            loadTask = null;
        }
    }

    @OnClick(R.id.addNewBtn)
    @SuppressWarnings("unused")
    void onAddClicked() {
        CreateEditTaxActivity.startToCreateTax(getActivity());
    }

    @OnClick(R.id.renewBadge)
    @SuppressWarnings("unused")
    void onRenewBadgeClicked() {
        SubscriptionActivity.startActivity(getActivity(), BillingUtils.ActionType.RENEW);
    }

    @OnClick(R.id.text_send_report)
    @SuppressWarnings("unused")
    void onSendClicked() {
        Intent i = new Intent(Intent.ACTION_SEND_MULTIPLE);
        i.setType("message/rfc822");
        i.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
        i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.tax_report));

        List<ResolveInfo> resolveInfos = getActivity().getPackageManager().queryIntentActivities(i, 0);
        if (resolveInfos.size() == 0) {
            MaterialDialog mMaterialDialog = new MaterialDialog(getActivity())
                    .setMessage(R.string.no_app_to_share)
                    .setPositiveButton(R.string.ok, null);

            mMaterialDialog.show();
            return;
        }

        TaxReportGenerator taxReportGenerator = new TaxReportGenerator(getActivity(),
                dateRangePicker.getFromDate().getTimeInMillis(), dateRangePicker.getToDate().getTimeInMillis(),
                adapter.getDaysAbroad(), adapter.getData());
        File cvsReport = taxReportGenerator.generateCsvReport("TaxReport");
        File htmlReport = taxReportGenerator.generateHtmlReport("TaxReport");

        ArrayList<Uri> uris = new ArrayList<>();
        Uri cvsFileUri = Uri.parse("content://" + FilesProvider.CACHE_DIR_AUTHORITY + "/"
                + cvsReport.getName());
        Uri htmlFileUri = Uri.parse("content://" + FilesProvider.CACHE_DIR_AUTHORITY + "/"
                + htmlReport.getName());
        uris.add(cvsFileUri);
        uris.add(htmlFileUri);

        i.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);

        startActivityForResult(Intent.createChooser(i, null), RequestCodes.SEND_EMAIL);
    }

    @Override
    public void onItemClicked(Tax tax) {
        if (!isLock) {
            CreateEditTaxActivity.startToEditTax(getActivity(), tax);
        }
    }

    @Override
    public void onRangeSet() {
        loadData();
    }

    public void onEventMainThread(final Events.TaxChanged event) {
        loadData();
    }

    public void onEventMainThread(Events.PersonChanged event) {
        if (event.getPerson().getId() == Person.ME_ID) {
            isLock = !BillingUtils.isPro(SharedPreferenceUtils.getUtils().getUserSettings());
            updateUiLock();
        }
    }

    private void updateReportText(int days) {
        SpannableString builder = new SpannableString(getString(R.string.title_send_report, days));
        int start = builder.toString().indexOf('\n');
        int end = builder.length();
        int color = getResources().getColor(R.color.white_50);
        builder.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textReport.setText(builder);
    }

    private void updateUiLock() {
        addNewBtn.setVisibility(isLock ? View.GONE : View.VISIBLE);
        lockView.setVisibility(isLock ? View.VISIBLE : View.GONE);
        textReport.setVisibility(isLock ? View.GONE : View.VISIBLE);
        renewBadge.setVisibility(isLock ? View.VISIBLE : View.GONE);
        adapter.setShowIncitations(!isLock);
        if (isLock) {
            dateRangePicker.setVisibility(View.GONE);
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) listTaxes.getLayoutParams();
            params.topMargin = 0;
            listTaxes.setLayoutParams(params);
        } else {
            dateRangePicker.setVisibility(View.VISIBLE);
            dateRangePicker.above(listTaxes);
        }

    }

    private class DataLoadAsyncTask extends AsyncTask<Calendar, Void, List<Tax>> {

        @Override
        protected void onPreExecute() {
            adapter.clear();
        }

        @Override
        protected List<Tax> doInBackground(Calendar... range) {
            if (isLock) {
                return taxDaoHelper.getAllItems(getActivity());
            } else {
                return taxDaoHelper.getTaxInInterval(getActivity(),
                        range[0].getTimeInMillis() / 1000,
                        range[1].getTimeInMillis() / 1000);
            }
        }

        @Override
        protected void onPostExecute(List<Tax> taxes) {
            DeveloperUtil.michaelLog("taxes");
            DeveloperUtil.michaelLog(taxes);
            adapter.setData(taxes);
            updateReportText(adapter.getDaysAbroad());
        }
    }
}
