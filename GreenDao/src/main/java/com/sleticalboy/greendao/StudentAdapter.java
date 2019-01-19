package com.sleticalboy.greendao;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sleticalboy.greendao.bean.StudentMsgBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 18-3-2.
 *
 * @author sleticalboy
 * @version 1.0
 * @description
 */
public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentHolder> {

    private OnItemClickListener mClickListener;
    private List<StudentMsgBean> mDataSet;

    @Override
    public StudentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student, parent, false);
        return new StudentHolder(view, mClickListener);
    }

    @Override
    public void onBindViewHolder(StudentHolder holder, int position) {
        final StudentMsgBean student = getStudent(position);
        holder.text.setText(student.getText());
        holder.comment.setText(student.getComment());
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public void setDataSet(@NonNull List<StudentMsgBean> dataSet) {
        mDataSet = dataSet;
        notifyDataSetChanged();
    }

    public StudentMsgBean getStudent(int position) {
        return mDataSet.get(position);
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public StudentAdapter(OnItemClickListener clickListener) {
        mClickListener = clickListener;
        mDataSet = new ArrayList<>();
    }

    static class StudentHolder extends RecyclerView.ViewHolder {

        public TextView text;
        public TextView comment;

        public StudentHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.textViewNoteText);
            comment = (TextView) itemView.findViewById(R.id.textViewNoteComment);
            if (listener != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemClick(getAdapterPosition());
                    }
                });
            }
        }
    }
}
