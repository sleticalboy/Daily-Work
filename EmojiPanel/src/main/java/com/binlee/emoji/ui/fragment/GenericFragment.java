package com.binlee.emoji.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.binlee.emoji.R;
import com.binlee.emoji.helper.LogHelper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created on 20-9-16.
 *
 * @author binlee sleticalboy@gmail.com
 */
public final class GenericFragment extends Fragment {

    private static final String TAG = "GenericFragment";

    private TextView mLogContent;

    public GenericFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        LogHelper.debug(TAG, "onCreateView() -> $savedInstanceState");
        return inflater.inflate(R.layout.fragment_generic, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // view.findViewById(R.id.run).setOnClickListener(v -> startRunning());
        mLogContent = view.findViewById(R.id.logContent);
        startRunning();
    }

    private void startRunning() {
        final List<String> strList = new ArrayList<>();
        final List<Integer> intList = new ArrayList<>();
        appendV("泛型擦除：\r\n");
        appendV("new ArrayList<String>().getClass() ->");
        newLine();
        appendV(strList.getClass());
        newLine();
        appendV("new ArrayList<Integer>().getClass() ->");
        newLine();
        appendV(intList.getClass());
        newLine();

        appendV("泛型参数:");
        appendV("strList.getClass().getGenericSuperclass() ->");
        newLine();
        final Type type = strList.getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            appendV(" -> " + type.getClass() + "\r\n");
            appendV("rawType: " + ((ParameterizedType) type).getRawType() + "\r\n");
            appendV("content: " + ((ParameterizedType) type).getActualTypeArguments()[0] + "\r\n");
        }
    }

    private void appendV(Object msg) {
        mLogContent.append(String.valueOf(msg));
    }

    private void appendD(Object obj) {
        mLogContent.append(String.valueOf(obj));
    }

    private void appendI() {
    }

    private void appendW() {
    }

    private void newLine() {
        mLogContent.append("\r\n");
    }
}
