package com.zoomlee.Zoomlee.ui.view.selectperson;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.zoomlee.Zoomlee.R;
import com.zoomlee.Zoomlee.net.model.Person;
import com.zoomlee.Zoomlee.ui.adapters.BindableArrayAdapter;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;

public class SelectPersonView extends FrameLayout {

    @InjectView(R.id.list_items)
    ListView listView;

    @Inject
    Presenter presenter;

    private final SelectPersonAdapter adapter;

    public SelectPersonView(Context context, AttributeSet attrs) {
        super(context, attrs);

        adapter = new SelectPersonAdapter(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        if (isInEditMode()) {
            return;
        }

        ButterKnife.inject(this);

        listView.setAdapter(adapter);
    }

    @OnItemClick(R.id.list_items)
    @SuppressWarnings("unused")
    void onPersonSelected(int position) {
        presenter.selectPerson(adapter.getItem(position));
    }

    public void setPersons(List<Person> persons) {
        adapter.replaceWith(persons);
    }

    private static class SelectPersonAdapter extends BindableArrayAdapter<Person> {

        public SelectPersonAdapter(Context context) {
            super(context, R.layout.item_select_person);
        }

        @Override
        public void bindView(Person item, int position, View view) {
            SelectPersonItemView itemView = (SelectPersonItemView) view;
            itemView.bind(item);
        }
    }

    public interface Presenter {

        void selectPerson(Person person);
    }
}
