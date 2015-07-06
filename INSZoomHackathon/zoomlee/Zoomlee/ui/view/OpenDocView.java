package com.zoomlee.Zoomlee.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zoomlee.Zoomlee.R;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class OpenDocView extends LinearLayout {

    @InjectView(R.id.text_file_name)
    TextView textFileName;

    @Inject
    Presenter presenter;

    public OpenDocView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        if (isInEditMode()) {
            return;
        }

        ButterKnife.inject(this);
    }

    /**
     * Sets file name.
     * @param name to set
     */
    public void setFileName(String name) {
        textFileName.setText(name);
    }

    @OnClick(R.id.button_cancel)
    @SuppressWarnings("unused")
    void onCancelClicked() {
        presenter.cancel();
    }

    @OnClick(R.id.button_create_new)
    @SuppressWarnings("unused")
    void onCreateNewClicked() {
        presenter.createNewDocument();
    }

    @OnClick(R.id.button_add_to_existing)
    @SuppressWarnings("unused")
    void onAddToExistingClicked() {
        presenter.addToExistingDocument();
    }

    public interface Presenter {

        void cancel();

        void addToExistingDocument();

        void createNewDocument();
    }
}
