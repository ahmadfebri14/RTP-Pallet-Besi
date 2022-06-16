package com.example.rtppalletbesi.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rtppalletbesi.ListPacking;
import com.example.rtppalletbesi.R;

import java.util.ArrayList;

public class PackingListAdapter extends RecyclerView.Adapter<PackingListAdapter.ListViewHolder> {
    private ArrayList<ListPacking> listPacking;
    private PackingListAdapter.OnItemClickCallback onItemClickCallback;

    public void setOnItemClickCallback(PackingListAdapter.OnItemClickCallback onItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback;
    }

    public PackingListAdapter(ArrayList<ListPacking> list) {
        this.listPacking = list;
    }

    @NonNull
    @Override
    public PackingListAdapter.ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_packing, parent, false);
        return new PackingListAdapter.ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PackingListAdapter.ListViewHolder holder, int position) {
        ListPacking data = listPacking.get(position);

        String core = !data.getCoreId().equals("") ? data.getCoreId() : "-";

        holder.txtPallet.setText(data.getPalletId());
        holder.txtRoll.setText(data.getNoRoll());
        holder.txtCore.setText(core);
        String scanDate = "Scanned by " + data.getGroup() + " on " + data.getDateScanPallet();
        holder.txtDate.setText(scanDate);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                onItemClickCallback.onItemClicked(data, holder.getAdapterPosition());
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return listPacking.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder{
        TextView txtPallet, txtRoll, txtDate, txtCore;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            txtPallet = itemView.findViewById(R.id.txtPallete);
            txtDate = itemView.findViewById(R.id.txtScanDate);
            txtCore = itemView.findViewById(R.id.txtCore);
            txtRoll = itemView.findViewById(R.id.txtRoll);
        }
    }

    public interface OnItemClickCallback {
        void onItemClicked(ListPacking data, int position);
    }
}
