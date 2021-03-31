package com.fly.tkuilife.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fly.tkuilife.R;
import com.fly.tkuilife.bean.BeanContact;

import java.util.ArrayList;

public class AdapterRecyclerViewContact extends RecyclerView.Adapter<AdapterRecyclerViewContact.ViewHolder> {


    private ArrayList<BeanContact> contacts;

    public AdapterRecyclerViewContact(){
        contacts = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_contact_recyclerview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TextView name, office, telephone, extension;
        name = holder.itemView.findViewById(R.id.cell_contact_recyclerview_name);
        office = holder.itemView.findViewById(R.id.cell_contact_recyclerview_office);
        telephone = holder.itemView.findViewById(R.id.cell_contact_recyclerview_telephone);
        extension = holder.itemView.findViewById(R.id.cell_contact_recyclerview_extension);

        name.setText(contacts.get(position).getName());
        office.setText(contacts.get(position).getOffice());
        telephone.setText(contacts.get(position).getTelephone());
        if (contacts.get(position).getExtension().equals("")) extension.setText("專線");
        else extension.setText("分機 "+contacts.get(position).getExtension());
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public void clearAllItem(){
        contacts.clear();
    }

    public void addItem(BeanContact contact){
        this.contacts.add(contact);
    }

    public BeanContact getItem(int position){
        return contacts.get(position);
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

    }
}
