package com.binlee.sample.view;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.binlee.sample.ArchService;
import com.binlee.sample.R;
import com.binlee.sample.model.ArchDevice;
import com.binlee.sample.util.Glog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 21-2-27.
 * <p>
 * A fragment representing a list of Items.
 *
 * @author binlee sleticalboy@gmail.com
 */
public final class ListFragment extends Fragment implements ServiceConnection, IView {

    private static final String TAG = Glog.wrapTag("ListFragment");
    private static final String ARG_COLUMN_COUNT = "column-count";

    private final List<ArchDevice> mDevices;
    private ArchService.LocalBinder mService;
    private ArchDeviceAdapter mAdapter;
    private int mColumnCount = 1;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ListFragment() {
        mDevices = new ArrayList<>();
    }

    public static ListFragment newInstance(int columnCount) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), mColumnCount));
            }
            recyclerView.setAdapter(mAdapter = new ArchDeviceAdapter(mDevices));
        }
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        boolean started = ArchService.bind(context, this);
        Glog.v(TAG, "onAttach() bind service: " + started);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mService != null) mService.detachView(this);
        ArchService.unbind(getContext(), this);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mService = ((ArchService.LocalBinder) service);
        mService.attachView(this);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mService = null;
    }

    @Override
    public void onDeviceChanged(ArchDevice device, boolean removed) {
        int index = mDevices.indexOf(device);
        if (removed) {
            if (index >= 0) {
                mDevices.remove(device);
                mAdapter.notifyItemRemoved(index);
            }
        } else {
            if (index < 0) {
                mDevices.add(device);
                mAdapter.notifyItemInserted(mDevices.size() - 1);
            } else {
                mDevices.set(index, device);
                mAdapter.notifyItemChanged(index, null);
            }
        }
    }

    @Override
    public void onScanTimeout() {
        Glog.i(TAG, "onScanTimeout()");
    }

    @Override
    public void onConnectTimeout() {
        Glog.i(TAG, "onConnectTimeout()");
    }

    @Override
    public void onClearInfo(ArchDevice device, boolean remote) {
        Glog.i(TAG, "onClearInfo()");
    }
}