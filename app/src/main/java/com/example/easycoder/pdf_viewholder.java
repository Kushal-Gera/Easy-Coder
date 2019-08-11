package com.example.easycoder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class pdf_viewholder extends RecyclerView.ViewHolder {

    public TextView name;

    pdf_viewholder(@NonNull View itemView) {

        super(itemView);
        name = itemView.findViewById(R.id.name);

    }

}
