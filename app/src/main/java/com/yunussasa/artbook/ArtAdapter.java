package com.yunussasa.artbook;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yunussasa.artbook.databinding.RecyclerRowBinding;

import java.util.ArrayList;


public class ArtAdapter extends RecyclerView.Adapter<ArtAdapter.ArtHolder> {

    ArrayList<Art> arrayList;

    public ArtAdapter(ArrayList<Art> arrayList){
        this.arrayList=arrayList;
    }

    @NonNull
    @Override
    public ArtHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerRowBinding recyclerRowBinding=RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ArtHolder(recyclerRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.binding.recyclerViewTextView.setText(arrayList.get(position).name);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(holder.itemView.getContext(),ArtActivity.class);
                intent.putExtra("info","old");
                intent.putExtra("artId",arrayList.get(position).id);
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ArtHolder extends RecyclerView.ViewHolder {
        private  RecyclerRowBinding binding;
        public ArtHolder(RecyclerRowBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }

}
