package com.yunbao.phonelive.custom;

import android.content.Context;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.widget.EditText;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cxf on 2018/7/24.
 */

public class AtEditText extends EditText {

    private Map<String, AtSpan> mAtSpanMap;
    private TextWatcher mTextWatcher;
    private ActionListener mActionListener;

    public AtEditText(Context context) {
        super(context);
        init();
    }

    public AtEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AtEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mAtSpanMap = new LinkedHashMap<>();
        mTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (mAtSpanMap != null && mAtSpanMap.size() > 0) {
                    for (Map.Entry<String, AtSpan> entry : mAtSpanMap.entrySet()) {
                        AtSpan atSpan = entry.getValue();
                        if (start < atSpan.getStartIndex()) {
                            atSpan.moveIndex(after - count);
                        }
                    }
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mAtSpanMap != null && mAtSpanMap.size() > 0) {
                    String text = getText().toString();
                    List<String> removeKeyList = null;
                    for (Map.Entry<String, AtSpan> entry : mAtSpanMap.entrySet()) {
                        AtSpan atSpan = entry.getValue();
                        if (!atSpan.validate() || !text.contains(atSpan.getName())) {
                            if (removeKeyList == null) {
                                removeKeyList = new ArrayList<>();
                            }
                            removeKeyList.add(atSpan.getUid());
                        }
                    }
                    if (removeKeyList != null) {
                        for (String key : removeKeyList) {
                            mAtSpanMap.remove(key);
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        addTextChangedListener(mTextWatcher);
    }

    @Override
    public boolean onTextContextMenuItem(int id) {
        return false;
    }

    @Override
    public boolean performContextClick() {
        return false;
    }

    @Override
    public boolean performLongClick() {
        return false;
    }

    public boolean addAtSpan(String uid, String name) {
        if (TextUtils.isEmpty(uid) || TextUtils.isEmpty(name)) {
            return false;
        }
        for (Map.Entry<String, AtSpan> entry : mAtSpanMap.entrySet()) {
            AtSpan atSpan = entry.getValue();
            if (uid.equals(atSpan.getUid())) {
                if (mActionListener != null) {
                    mActionListener.onContainsUid();
                }
                return false;
            }
            if (name.equals(atSpan.getName())) {
                if (mActionListener != null) {
                    mActionListener.onContainsName();
                }
                return false;
            }
        }
        String content = "@" + name + " ";
        Editable editable = getText();
        SpannableStringBuilder builder = new SpannableStringBuilder(content);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(0xfff3e835);
        builder.setSpan(colorSpan, 0, content.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        int startIndex = getSelectionStart();
        editable.insert(startIndex, builder);
        AtSpan atSpan = new AtSpan(uid, name, content, startIndex);
        mAtSpanMap.put(uid, atSpan);
        return true;
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        if (mAtSpanMap != null && mAtSpanMap.size() > 0) {
            if (selStart == selEnd) {
                for (Map.Entry<String, AtSpan> entry : mAtSpanMap.entrySet()) {
                    AtSpan atSpan = entry.getValue();
                    if (selStart > atSpan.getStartIndex() && selStart < atSpan.getEndIndex()) {
                        setSelection(atSpan.getEndIndex());
                        break;
                    }
                }
            }
        }
        super.onSelectionChanged(selStart, selEnd);
    }

    private boolean onDelete() {
        boolean delAt = false;//删除@xxx
        if (mAtSpanMap != null && mAtSpanMap.size() > 0) {
            if (getSelectionStart() == getSelectionEnd()) {
                for (Map.Entry<String, AtSpan> entry : mAtSpanMap.entrySet()) {
                    AtSpan atSpan = entry.getValue();
                    if (atSpan.validate() && getSelectionStart() == atSpan.getEndIndex()) {
                        getText().delete(atSpan.getStartIndex(), atSpan.getEndIndex());
                        delAt = true;
                        break;
                    }
                }
            }
        }
        return delAt;
    }


    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        return new InputConnectionWrapper(super.onCreateInputConnection(outAttrs), true) {

            @Override
            public boolean sendKeyEvent(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.KEYCODE_DEL && e.getAction() == KeyEvent.ACTION_DOWN) {
                    if (onDelete()) {
                        return true;
                    }
                }
                return super.sendKeyEvent(e);
            }

            @Override
            public boolean commitText(CharSequence text, int newCursorPosition) {
                if ("@".equals(text)) {
                    if (mActionListener != null) {
                        mActionListener.onAtClick();
                    }
                    return false;
                }
                return super.commitText(text, newCursorPosition);
            }


        };
    }

    public String getAtUserInfo() {
        JSONArray jsonArray = null;
        String text = getText().toString();
        for (Map.Entry<String, AtSpan> entry : mAtSpanMap.entrySet()) {
            AtSpan atSpan = entry.getValue();
            if (text.contains(atSpan.getName())) {
                JSONObject obj = new JSONObject();
                obj.put("uid", atSpan.getUid());
                obj.put("name", atSpan.getName());
                if (jsonArray == null) {
                    jsonArray = new JSONArray();
                }
                jsonArray.add(obj);
            }
        }
        if (jsonArray != null && jsonArray.size() > 0) {
            return jsonArray.toJSONString();
        } else {
            return "";
        }

    }

    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }

    public interface ActionListener {
        void onAtClick();

        void onContainsUid();

        void onContainsName();
    }

}
