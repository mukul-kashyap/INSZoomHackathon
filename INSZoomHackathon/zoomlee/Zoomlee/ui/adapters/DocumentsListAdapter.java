package com.zoomlee.Zoomlee.ui.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.zoomlee.Zoomlee.R;
import com.zoomlee.Zoomlee.incitations.Incitation;
import com.zoomlee.Zoomlee.incitations.IncitationsController;
import com.zoomlee.Zoomlee.net.model.Country;
import com.zoomlee.Zoomlee.net.model.Document;
import com.zoomlee.Zoomlee.net.model.NamedItem;
import com.zoomlee.Zoomlee.net.model.Person;
import com.zoomlee.Zoomlee.net.model.User;
import com.zoomlee.Zoomlee.ui.view.DocumentItemView;
import com.zoomlee.Zoomlee.utils.SharedPreferenceUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by
 *
 * @author Evgen Marinin <ievgen.marinin@alterplay.com>
 * @date 03.02.15.
 */
public class DocumentsListAdapter extends ArrayAdapter<Document> implements IncitationsAdapter.Incitated, Filterable {

    private static final int MIN_POSITION = 1;
    private static final int MAX_POSITION = 3;

    private ItemFilter mFilter = new ItemFilter();
    private HashMap<Integer, Document> filteredData = new HashMap<Integer, Document>();
    private HashMap<Integer, Document> originalData = new HashMap<Integer, Document>();
    private boolean allPersons;
    private List<Person> personsList;
    private User user;
    private Document documentToOpen;
    private DocumentItemView.DocumentItemListener documentListener;
    private DocumentItemView selectedView;
    private IncitationsAdapter.Incitated incitated;
    private final IncitationsController controller = new IncitationsController();
    private boolean isShowIncitations;
    private String searchCond;

    public DocumentsListAdapter(Context context, List<Document> items, boolean allPersons, List<Person> persons) {
        super(context, R.layout.item_document, items);
        this.allPersons = allPersons;
        if (allPersons) {
            this.personsList = persons;
            this.user = SharedPreferenceUtils.getUtils().getUserSettings();
        }
        updateIncitations();
        filteredData.clear();
        originalData.clear();
        searchCond = "";
    }

    private void updateIncitations() {
        incitated = controller.createIncitated(getCount(), IncitationsController.Screen.DOCUMENTS, MIN_POSITION, MAX_POSITION);
    }

    public void showIncitations(boolean isShow) {
        isShowIncitations = isShow;
        if (isShow) {
            notifyDataSetChanged();
        }
    }

    @Override
    public void notifyDataSetChanged() {
        // incitation change along with data changes, for example can disappear when too few items
        updateIncitations();

        super.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.item_document, parent, false);
        }
        Document item = getItem(position);
        DocumentItemView itemView = (DocumentItemView) view;

        if (!originalData.containsKey(position)) {
            originalData.put(position, item);
        }
        if ("".equals(searchCond) ) {
            itemView.bind(item, allPersons, user, personsList);
            itemView.showView();
        } else if ((!"".equals(searchCond)) && filteredData.size() > 0) {
            if (filteredData.containsKey(position)) {
                itemView.bind(item, allPersons, user, personsList);
                itemView.showView();
            }
            else
                itemView.hideView();
        } else {
            itemView.hideView();
        }
        if (item.equals(documentToOpen)) {
            itemView.openActions(true, documentListener);
            selectedView = itemView;
            // do this only once
            documentToOpen = null;
            documentListener = null;
        }

        //searchCond = "";
        return itemView;
    }

    /**
     * Sets document to be opened after binded.
     *
     * @param documentToOpen document
     * @param listener       to handle actions
     */
    public void setOpenDocument(Document documentToOpen, DocumentItemView.DocumentItemListener listener) {
        this.documentToOpen = documentToOpen;
        this.documentListener = listener;
    }

    public void setSelectedView(DocumentItemView selectedView) {
        this.selectedView = selectedView;
    }

    public DocumentItemView getSelectedView() {
        return selectedView;
    }

    @Override
    public int getIncitationPosition() {
        return isShowIncitations ? incitated.getIncitationPosition() : AdapterView.INVALID_POSITION;
    }

    @Override
    public Incitation getIncitation() {
        return incitated.getIncitation();
    }

    public Filter getFilter() {
        return mFilter;
    }

    private class ItemFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString().toLowerCase();
            searchCond = constraint.toString();
            filteredData = new HashMap<>();
            FilterResults results = new FilterResults();

            final HashMap<Integer, Document> list = originalData;

            int count = list.size();

            if (TextUtils.isEmpty(filterString)) {
                results.values = list;
                results.count = count;
                return results;
            }
            final HashMap<Integer, Document> nlist = new HashMap<>(count);

            Document filterableItem;
            for (int i = 0; i < count; i++) {
                filterableItem = list.get(i);
                if (filterableItem.getId() == -1) {
                    if (nlist.size() == 0) {
                        continue;
                    }
                    nlist.put(i, filterableItem);
                    continue;
                }
                if (filterableItem.getName().toLowerCase().contains(constraint))
                    nlist.put(i, filterableItem);
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (HashMap<Integer, Document>) results.values;
            notifyDataSetChanged();
        }
    }
}