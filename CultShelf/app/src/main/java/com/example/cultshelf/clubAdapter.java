package com.example.cultshelf;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cultshelf.modals.Club;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class clubAdapter extends FirestoreRecyclerAdapter<Club, clubAdapter.clubViewHolder>{

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     *
     */
    public clubAdapter(@NonNull FirestoreRecyclerOptions<Club> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull clubViewHolder holder, int position, @NonNull Club model) {

        holder.clubName.setText(model.clubname);


    }

    @NonNull
    @Override
    public clubViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View viewHolder = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_club, parent, false);
        return new clubViewHolder(viewHolder);
    }

    public class clubViewHolder extends RecyclerView.ViewHolder{
        TextView clubName;
        ImageView clublogo;
        public clubViewHolder(@NonNull View clubView){
            super(clubView);
            clubName = clubView.findViewById(R.id.clubName);
            clublogo = clubView.findViewById(R.id.clublogo);
        }


    }


}
