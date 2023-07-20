package com.releasy.android.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.TextView;

import com.releasy.android.R;

import java.util.Locale;

public class PrivacyDialogFragment extends DialogFragment {
    private WebView webView;
    private TextView tvCancel;
    private TextView tvConfirm;
    private OnInteractionListener mListener;

    public PrivacyDialogFragment() {
    }

    public static PrivacyDialogFragment newInstance() {
        PrivacyDialogFragment fragment = new PrivacyDialogFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_privacy_dialog, container, false);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); //去除默认白色边框
        webView = view.findViewById(R.id.webView);
        tvCancel = view.findViewById(R.id.tv_cancel);
        tvConfirm = view.findViewById(R.id.tv_ok);
        Locale locale = getContext().getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.contains("zh")){
            webView.loadUrl("http://mooyee.net/PrivacyStatement.html");
        }else {
            webView.loadUrl("http://mooyee.net/PrivacyStatementEn.html");
        }
        onListener();
        return view;
    }

    private void onListener() {
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (mListener != null)
                    mListener.cancel();
            }
        });
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (mListener != null)
                    mListener.confirm();
            }
        });
    }

    public void setListener(OnInteractionListener mListener) {
        this.mListener = mListener;
    }

    public interface OnInteractionListener {
        void cancel();

        void confirm();
    }
}
