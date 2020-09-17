package com.binlee.emoji.ui.fragment;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.binlee.emoji.R;
import com.binlee.emoji.generic.ComplexGeneric;
import com.binlee.emoji.generic.SimpleGeneric;
import com.binlee.emoji.generic.StrList;
import com.binlee.emoji.generic.TypeGetter;
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
        appendV("泛型擦除：");
        appendV("\r\nnew ArrayList<String>().getClass() -> " + new ArrayList<String>().getClass());
        appendV("\r\nnew ArrayList<Integer>().getClass() -> " + new ArrayList<Integer>().getClass());

        appendV("\r\n泛型参数: class StrList extends ArrayList<String>\r\n");
        final Type type = StrList.class.getGenericSuperclass();
        appendV("StrList.class.getGenericSuperclass() -> " + type);
        if (type != null) {
            if (type instanceof ParameterizedType) {
                final Type[] typeArguments = ((ParameterizedType) type).getActualTypeArguments();
                for (Type arg : typeArguments) {
                    appendV("\r\nparam type: " + arg);
                }
            }
        }
        appendV("\r\nstrList.getClass().getGenericInterfaces() ->");
        final Type[] types = StrList.class.getGenericInterfaces();
        for (Type t : types) {
            appendV("\r\ntype: " + t);
        }

        foo(new SimpleGeneric("sleticalboy", 26));
        foo(new ComplexGeneric());

        typeGetter();
    }

    private void typeGetter() {
        appendV("\r\nList<String>'s generic parameter type is " + new TypeGetter<List<String>>() {
        }.getType());
        appendV("\r\nArrayList<String>'s generic parameter type is " + new TypeGetter<ArrayList<String>>() {
        }.getType());
        appendV("\r\nList<ArrayList<String>>'s generic parameter type is " + new TypeGetter<List<ArrayList<String>>>() {
        }.getType());
        appendV("\r\nSimpleGeneric<String, Integer>'s generic parameter type is " + new TypeGetter<SimpleGeneric>() {
        }.getType());
        appendV("\r\nComplexGeneric<>'s generic parameter type is " + new TypeGetter<ComplexGeneric>() {
        }.getType());
    }

    private void foo(Object obj) {
        final Type superclass = obj.getClass().getGenericSuperclass();
        appendV("\r\n" + obj.getClass().getName() + ".class.getGenericSuperclass() -> " + superclass);
        if (superclass instanceof ParameterizedType) {
            final Type[] arguments = ((ParameterizedType) superclass).getActualTypeArguments();
            for (Type arg : arguments) {
                appendV("\r\nparam type: " + arg);
            }
        }
        final Type[] interfaces = obj.getClass().getGenericInterfaces();
        appendV("\r\n" + obj.getClass().getName() + ".class.getGenericInterfaces() -> " + Arrays.toString(interfaces));
        for (Type inter : interfaces) {
            if (inter instanceof ParameterizedType) {
                final Type[] arguments = ((ParameterizedType) inter).getActualTypeArguments();
                for (Type arg : arguments) {
                    appendV("\r\nparam type: " + arg);
                }
            }
        }
        newLine();
    }

    private void appendV(Object obj) {
        String msg;
        if (obj instanceof String) {
            msg = ((String) obj);
        } else {
            msg = String.valueOf(obj);
        }
        mLogContent.append(msg);
        Log.d(TAG, "msg: " + msg.replace("\r\n", ""));
    }

    private void appendD(Object obj) {
        String msg;
        if (obj instanceof String) {
            msg = ((String) obj);
        } else {
            msg = String.valueOf(obj);
        }
        mLogContent.append(msg);
        Log.d(TAG, "msg: " + msg.replace("\r\n", ""));
    }

    private void appendI() {
    }

    private void appendW() {
    }

    private void newLine() {
        mLogContent.append("\r\n");
    }
}
