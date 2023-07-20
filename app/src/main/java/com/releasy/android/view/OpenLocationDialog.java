package com.releasy.android.view;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.releasy.android.R;

import java.util.Locale;

public class OpenLocationDialog extends Dialog {

    private Context context;
    private ImageView closeImg;
    private ImageView msgPic;
    private TextView msgTxt;
    private Button positiveBtn;
    private DialogListener dialogListener;

    public OpenLocationDialog(Context context) {
        super(context, R.style.Theme_Light_FullScreenDialogAct);
        this.context = context;

        setContentView(R.layout.layout_open_location_dialog);
        setCancelable(true);
        setCanceledOnTouchOutside(false);

        init();
    }

    /**
     * 初始化
     */
    private void init() {
        initView();
        initEvents();

        if (isZh(context)) {
            msgPic.setImageResource(R.drawable.ic_open_location_c);
        }
    }

    /**
     * 初始化视图
     */
    private void initView() {
        closeImg = (ImageView) findViewById(R.id.closeDialog);
        msgPic = (ImageView) findViewById(R.id.msgPic);
        msgTxt = (TextView) findViewById(R.id.msgTxt);
        positiveBtn = (Button) findViewById(R.id.positiveBtn);
    }

    /**
     * 初始化点击事件
     */
    private void initEvents() {
        closeImg.setOnClickListener(new ImageView.OnClickListener() {
            public void onClick(View arg0) {
                if (dialogListener != null) {
                    dialogListener.onCancel();
                }
                dismiss();
            }
        });

        positiveBtn.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View arg0) {
                if (dialogListener != null) {
                    dialogListener.onSummit();
                }
                dismiss();
            }
        });
    }

    public DialogListener getDialogListener() {
        return dialogListener;
    }

    public OpenLocationDialog setDialogListener(DialogListener dialogListener) {
        this.dialogListener = dialogListener;
        return this;
    }

    public static boolean isZh(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.endsWith("zh"))
            return true;
        else
            return false;
    }

    public interface DialogListener {
        void onCancel();

        void onSummit();
    }

}
