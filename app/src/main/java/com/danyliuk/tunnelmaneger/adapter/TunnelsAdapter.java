package com.danyliuk.tunnelmaneger.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.danyliuk.tunnelmaneger.R;
import com.danyliuk.tunnelmaneger.entity.Tunnel;

import java.util.List;

public class TunnelsAdapter extends RecyclerView.Adapter<TunnelsAdapter.TunnelViewHolder> {
    private OnPowerTunnelClickListener onPowerTunnelClickListener;
    private OnEditTunnelClickListener onEditTunnelClickListener;
    private List<Tunnel> tunnels;

    private List<Tunnel> getTunnels() {
        return tunnels;
    }
    @SuppressLint("NotifyDataSetChanged")
    public void setTunnels(List<Tunnel> tunnels) {
        this.tunnels = tunnels;
        notifyDataSetChanged();
    }
    @SuppressLint("NotifyDataSetChanged")
    public void addTunnel(Tunnel tunnel) {
        this.tunnels.add(tunnel);
        notifyDataSetChanged();
    }
    public interface OnPowerTunnelClickListener {
        void onPowerTunnelClick(int position);
    }
    public void setOnPowerTunnelClickListener(OnPowerTunnelClickListener onPowerTunnelClickListener) {
        this.onPowerTunnelClickListener = onPowerTunnelClickListener;
    }

    public interface OnEditTunnelClickListener {
        void onEditTunnelClick(int position);
    }

    public void setOnEditTunnelClickListener(OnEditTunnelClickListener onEditTunnelClickListener) {
        this.onEditTunnelClickListener = onEditTunnelClickListener;
    }

    @NonNull
    @Override
    public TunnelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tunnel_item, parent, false);
        return new TunnelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TunnelViewHolder holder, int position) {
        Tunnel tunnel = tunnels.get(position);
        holder.tvNumber.setText(Integer.toString(position + 1));
        holder.tvName.setText(tunnel.getNameTunnel());
        if(tunnel.getSession() != null) {
            holder.cbStateItem.setChecked(true);
//            holder.cbStateItem.setBackgroundColor(55);
        } else {
            holder.cbStateItem.setChecked(false);
//            holder.cbStateItem.setBackgroundColor(250);
        }
    }

    @Override
    public int getItemCount() {
        return tunnels.size();
    }

    public class TunnelViewHolder extends RecyclerView.ViewHolder {
        TextView tvNumber;
        TextView tvName;
        CheckBox cbStateItem;
        ImageButton btEditTunnelItem;

        public TunnelViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNumber = itemView.findViewById(R.id.tvNumberItem);
            tvName = itemView.findViewById(R.id.tvNameItem);
            cbStateItem = itemView.findViewById(R.id.cbStateItem);
            btEditTunnelItem = itemView.findViewById(R.id.btEditTunnelItem);

            cbStateItem.setOnClickListener(view -> {
                if(onPowerTunnelClickListener != null){
                    onPowerTunnelClickListener.onPowerTunnelClick(getAdapterPosition());
                }
            });

            btEditTunnelItem.setOnClickListener(view -> {
                if(onEditTunnelClickListener != null){
                    onEditTunnelClickListener.onEditTunnelClick(getAdapterPosition());
                }
            });
        }
    }
}
