package com.zoomlee.Zoomlee.ui.view.selectdocument;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zoomlee.Zoomlee.R;
import com.zoomlee.Zoomlee.net.model.Document;
import com.zoomlee.Zoomlee.ui.view.DocumentIconView;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SelectDocumentItemView extends LinearLayout {

    @InjectView(R.id.text_document_name)
    TextView textDocumentName;

    @InjectView(R.id.document_icon)
    DocumentIconView documentIconView;

    public SelectDocumentItemView(Context context, AttributeSet attrs) {
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

    public void bind(Document document) {
        documentIconView.setDocument(document);
        textDocumentName.setText(document.getName().toUpperCase());
    }
}
