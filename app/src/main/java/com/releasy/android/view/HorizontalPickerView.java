package com.releasy.android.view;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Build;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;


/**
 * Android 横向选择器
 * Created by yanhongliang on 16/4/9.
 * 使用方法

    <custom.widget.HorizontalPickerView
        android:id="@+id/scrollPicker"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:scrollbars="none">

        <LinearLayout
            android:id="@android:id/content"
            android:layout_width="wrap_content"
            android:layout_height="match_parent"
            android:orientation="horizontal"
            android:gravity="center" />

    </custom.widget.HorizontalPickerView>


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        final HorizontalPickerView picker = (HorizontalPickerView)findViewById(R.id.scrollPicker);

        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            list.add(i);
        }

        final ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this,0,list){

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                int value = getItem(position);
                if (convertView == null) {
                    LayoutInflater inflater = LayoutInflater.from(getContext());
                    convertView = inflater.inflate(android.R.layout.simple_list_item_1,null);
                }
                TextView view = (TextView) convertView.findViewById(android.R.id.text1);
                view.setText("第"+value+"个");
                return convertView;

            }
        };

        picker.setAdapter(adapter);

        picker.setOnSelectedListener(new HorizontalPickerView.OnSelectedListener() {
            @Override
            public void selected(View v, int index) {
                ViewGroup group = (ViewGroup)picker.getChildAt(0);
                for (int i = 0; i < adapter.getCount(); i++) {
                    View view = group.getChildAt(i);
                    if (i == index) {
                        view.setBackgroundColor(0xFFFF0000);
                    } else {
                        view.setBackgroundColor(group.getDrawingCacheBackgroundColor());
                    }
                }
            }
        });
    }


*/

public class HorizontalPickerView extends android.widget.HorizontalScrollView {


    public interface OnSelectedListener {
        void selected(View v, int index);
    }

    public HorizontalPickerView(Context context) {
        super(context);
        initialize();
    }

    public HorizontalPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public HorizontalPickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    public void setOnSelectedListener(OnSelectedListener listener) {
        selectedListener = listener;
    }

    private DataSetObserver observer;
    public void setAdapter(Adapter adapter) {
        if (this.adapter != null) {
            this.adapter.unregisterDataSetObserver(observer);
        }
        this.adapter = adapter;
        adapter.registerDataSetObserver(observer);
        updateAdapter();
    }

    private void updateAdapter() {
        ViewGroup group = (ViewGroup)getChildAt(0);
        group.removeAllViews();

        for (int i = 0; i<adapter.getCount(); i++) {
            group.addView(adapter.getView(i,null,group));
        }
    }

    private Adapter adapter;
    private OnSelectedListener selectedListener;
    private GestureDetector gesture;
    private OnTouchListener listener;
    void initialize() {
        observer = new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                updateAdapter();
            }

            @Override
            public void onInvalidated() {
                super.onInvalidated();
                ((ViewGroup)getChildAt(0)).removeAllViews();
            }
        };

        gesture = new GestureDetector(getContext(), new GestureDetector.OnGestureListener() {
            public boolean onDown(MotionEvent e) {
                return false;
            }

            public void onShowPress(MotionEvent e) {

            }

            public boolean onSingleTapUp(MotionEvent e) {
                float x = e.getRawX() + getScrollX() - getWidth() / 2;
                smoothScrollTo((int)x,0);
                return false;
            }

            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            public void onLongPress(MotionEvent e) {

            }

            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }
        });

        super.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                boolean result = gesture.onTouchEvent(event);
                return listener == null ? result : listener.onTouch(v, event);
            }
        });

    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int side = getWidth() / 2;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            getChildAt(0).setPaddingRelative(side, 0, side, 0);
        } else {
            getChildAt(0).setPadding(side,0,side,0);
        }

    }

    @Override
    public void setOnTouchListener(OnTouchListener l) {
        listener = l;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        int centerX = getWidth() / 2;
        selectItemWithX(centerX);
    }

    private void selectItemWithX(float x) {
        ViewGroup group = (ViewGroup)getChildAt(0);
        for (int i = 0; i<group.getChildCount(); i++) {
            View view = group.getChildAt(i);
            float minX = view.getX() - getScrollX();
            float maxX = minX + view.getWidth();
            if (x >= minX && x <= maxX ) {
                if (selectedListener != null) {
                    selectedListener.selected(view, i);
                }
                break;
            }
        }
    }

}