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
import java.util.*;
import java.util.List;


/**
 * Created by
 *
 * @author Evgen Marinin <ievgen.marinin@alterplay.com>
 * @date 03.02.15.
 */
public class DocumentsListAdapter extends ArrayAdapter<Document> implements IncitationsAdapter.Incitated {

    private static final int MIN_POSITION = 1;
    private static final int MAX_POSITION = 3;

    private boolean allPersons;
    private List<Person> personsList;
    private User user;
    private Document documentToOpen;
    private DocumentItemView.DocumentItemListener documentListener;
    private DocumentItemView selectedView;
    private IncitationsAdapter.Incitated incitated;
    private final IncitationsController controller = new IncitationsController();
    private boolean isShowIncitations;

    private ItemFilter mFilter = new ItemFilter();
    private static final int SEPARATOR = 0;
    private static final int ENDSEPARATOR = 1;
    private int separatorPositionFiltered;
    private final List<Document> mDataset = new ArrayList<>();
    private List<Document> itemsFiltered = new ArrayList<>();


    public DocumentsListAdapter(Context context, List<Document> items, boolean allPersons, List<Person> persons) {
        super(context, R.layout.item_document, items);
        this.allPersons = allPersons;
        if (allPersons) {
            this.personsList = persons;
            this.user = SharedPreferenceUtils.getUtils().getUserSettings();
        }
        mDataset.addAll(items) ;
        updateIncitations();
    }

    private void updateIncitations() {
        incitated = controller.createIncitated(getCount(), IncitationsController.Screen.DOCUMENTS, MIN_POSITION, MAX_POSITION);
    }

    public void showIncitations(boolean isShow) {
        isShowIncitations = isShow;
        if(isShow) {
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
        itemView.bind(item, allPersons, user, personsList);
        mDataset.add(item);
        Dictionary dict = new Hashtable();
        

        if (item.equals(documentToOpen)) {
            itemView.openActions(true, documentListener);
            selectedView = itemView;
            // do this only once
            documentToOpen = null;
            documentListener = null;
        }

        return view;
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

            FilterResults results = new FilterResults();

            final List<Document> list = mDataset;

            int count = list.size();

            if (TextUtils.isEmpty(filterString)) {
                results.values = list;
                results.count = count;
                return results;
            }
            final ArrayList<NamedItem> nlist = new ArrayList<>(count);

            Document filterableItem;
            for (int i = 0; i < count; i++) {
                filterableItem = list.get(i);
                if (filterableItem.getId() == -1) {
                    if (nlist.size() == 0) {
                        separatorPositionFiltered = -1;
                        continue;
                    }
                    separatorPositionFiltered = nlist.size();
                    nlist.add(filterableItem);
                    continue;
                }
                if (filterableItem.getName().toLowerCase().contains(constraint))
                    nlist.add(filterableItem);
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            itemsFiltered = (ArrayList<Document>) results.values;
            notifyDataSetChanged();
        }
    }
}