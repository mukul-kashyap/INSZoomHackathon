package com.zoomlee.Zoomlee.provider;

import android.net.Uri;

import com.zoomlee.Zoomlee.provider.helpers.BaseProviderHelper;
import com.zoomlee.Zoomlee.provider.helpers.CategoriesProviderHelper;
import com.zoomlee.Zoomlee.provider.helpers.Category2DocumentsTypesProviderHelper;
import com.zoomlee.Zoomlee.provider.helpers.ColorsProviderHelper;
import com.zoomlee.Zoomlee.provider.helpers.CountriesProviderHelper;
import com.zoomlee.Zoomlee.provider.helpers.DocumentsHelper;
import com.zoomlee.Zoomlee.provider.helpers.DocumentsTypes2FieldTypesHelper;
import com.zoomlee.Zoomlee.provider.helpers.DocumentsTypesHelper;
import com.zoomlee.Zoomlee.provider.helpers.FieldsHelper;
import com.zoomlee.Zoomlee.provider.helpers.FieldsTypesProviderHelper;
import com.zoomlee.Zoomlee.provider.helpers.FileTypesProviderHelper;
import com.zoomlee.Zoomlee.provider.helpers.FilesProviderHelper;
import com.zoomlee.Zoomlee.provider.helpers.FormFieldsProviderHelper;
import com.zoomlee.Zoomlee.provider.helpers.FormsProviderHelper;
import com.zoomlee.Zoomlee.provider.helpers.GroupsHelper;
import com.zoomlee.Zoomlee.provider.helpers.PersonsProviderHelper;
import com.zoomlee.Zoomlee.provider.helpers.RestTasksHelper;
import com.zoomlee.Zoomlee.provider.helpers.Tags2DocumentsProviderHelper;
import com.zoomlee.Zoomlee.provider.helpers.TagsProviderHelper;
import com.zoomlee.Zoomlee.provider.helpers.TaxProviderHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by  Evgen Marinin <ievgen.marinin@alterplay.com> on 21.01.15.
 */
public class ProviderHelpersContainer {

    private static ProviderHelpersContainer INSTANCE = new ProviderHelpersContainer();

    public static ProviderHelpersContainer getInstance() {
        return INSTANCE;
    }

    private List<BaseProviderHelper> providerHelpers = new ArrayList<BaseProviderHelper>(13);

    private ProviderHelpersContainer() {
        providerHelpers.add(new CategoriesProviderHelper());
        providerHelpers.add(new Category2DocumentsTypesProviderHelper());
        providerHelpers.add(new ColorsProviderHelper());
        providerHelpers.add(new CountriesProviderHelper());
        providerHelpers.add(new DocumentsHelper());
        providerHelpers.add(new DocumentsTypes2FieldTypesHelper());
        providerHelpers.add(new GroupsHelper());
        providerHelpers.add(new DocumentsTypesHelper());
        providerHelpers.add(new FieldsHelper());
        providerHelpers.add(new FieldsTypesProviderHelper());
        providerHelpers.add(new FilesProviderHelper());
        providerHelpers.add(new FileTypesProviderHelper());
        providerHelpers.add(new PersonsProviderHelper());
        providerHelpers.add(new TagsProviderHelper());
        providerHelpers.add(new Tags2DocumentsProviderHelper());
        providerHelpers.add(new TaxProviderHelper());
        providerHelpers.add(new FormsProviderHelper());
        providerHelpers.add(new FormFieldsProviderHelper());

        providerHelpers.add(new RestTasksHelper());
    }

    public BaseProviderHelper getMatchedProviderHelper(Uri uri) {
        for (BaseProviderHelper baseProviderHelper : providerHelpers)
            if (baseProviderHelper.match(uri) != -1) return baseProviderHelper;
        return null;
    }

    public List<BaseProviderHelper> getProviderHelpers() {
        return new ArrayList<BaseProviderHelper>(providerHelpers);
    }

}
