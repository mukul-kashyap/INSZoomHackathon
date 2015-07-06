package com.zoomlee.Zoomlee.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.zoomlee.Zoomlee.R;
import com.zoomlee.Zoomlee.dao.DaoHelper;
import com.zoomlee.Zoomlee.dao.DaoHelpersContainer;
import com.zoomlee.Zoomlee.net.model.Country;
import com.zoomlee.Zoomlee.ui.adapters.CountryAdapter;
import com.zoomlee.Zoomlee.utils.IntentUtils;
import com.zoomlee.Zoomlee.utils.RequestCodes;
import com.zoomlee.Zoomlee.utils.UiUtil;

import java.util.List;

import static com.zoomlee.Zoomlee.provider.helpers.CountriesProviderHelper.CountriesContract;

public class CountriesActivity extends SecuredActionBarActivity implements AdapterView.OnItemClickListener {

    public static final String COUNTRY_ID_KEY = "country_id";

    private CountryAdapter adapter;
    private int selectedInd = -1;
    private List<Country> countryList;
    private static final String sortOrder = CountriesContract.PRIORITIZE + " DESC, " + CountriesContract.NAME + " ASC";

    public static void startForResult(Activity context, int selectedCountryId) {
        Intent intent = new Intent(context, CountriesActivity.class);
        intent.putExtra(IntentUtils.EXTRA_OPEN_WITH_PIN, false);
        intent.putExtra(COUNTRY_ID_KEY, selectedCountryId);
        context.startActivityForResult(intent, RequestCodes.GET_COUNTRY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_countries);

        ListView listView = (ListView) findViewById(R.id.listView);
        final int currentCountryId = getIntent().getIntExtra(COUNTRY_ID_KEY, -1);
        DaoHelper<Country> countryDaoHelper = DaoHelpersContainer.getInstance().getDaoHelper(Country.class);
        countryList = countryDaoHelper.getAllItems(this, null, null, sortOrder);

        int countryCount = countryList.size();
        for (int i = 0; i < countryCount; i++) {
            int id = countryList.get(i).getRemoteId();
            if (id == currentCountryId) {
                selectedInd = i;
                break;
            }
        }

        adapter = new CountryAdapter(this, countryList, currentCountryId);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        listView.setSelection(selectedInd - 1);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        updateABAvatar(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        UiUtil.customizeMenuForSearch(this, menu, new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Country selected = adapter.getItem(position);
        if (selected.getId() == -1) return;
        Intent intent = new Intent();
        intent.putExtra(COUNTRY_ID_KEY, selected.getRemoteId());
        setResult(RESULT_OK, intent);
        finish();
    }
}
