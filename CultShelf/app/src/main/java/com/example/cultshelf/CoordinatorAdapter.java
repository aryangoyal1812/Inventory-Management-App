package com.example.cultshelf;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cultshelf.modals.Club;
import com.example.cultshelf.modals.Coordinator;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class CoordinatorAdapter extends FirestoreRecyclerAdapter<Coordinator, CoordinatorAdapter.CoordinatorViewHolder> {

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public CoordinatorAdapter(@NonNull FirestoreRecyclerOptions<Coordinator> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CoordinatorAdapter.CoordinatorViewHolder holder, int position, @NonNull Coordinator model) {

        holder.coorName.setText(model.name);
        holder.email.setText(model.emailId);

    }

    @NonNull
    @Override
    public CoordinatorAdapter.CoordinatorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewHolder = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coordinator, parent, false);

        return new CoordinatorAdapter.CoordinatorViewHolder(viewHolder);
    }



    public class CoordinatorViewHolder extends RecyclerView.ViewHolder{
        TextView coorName;
        TextView email;
        public CoordinatorViewHolder(@NonNull View coordinatorView){
            super(coordinatorView);
            coorName = coordinatorView.findViewById(R.id.Name);
            email = coordinatorView.findViewById(R.id.Email);
        }


    }
}
