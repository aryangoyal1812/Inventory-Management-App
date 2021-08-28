package com.example.cultshelf;


import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
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
    protected void onBindViewHolder(@NonNull clubViewHolder holder, final int position, @NonNull final Club model) {

        holder.clubName.setText(model.clubname);
        Glide.with(holder.clublogo.getContext()).load(model.logoRef).transform(new CircleCrop()).into(holder.clublogo);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(v.getContext(), "Item clicked at " + position, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(v.getContext(), ProductActivity.class);
                intent.putExtra("ClubName",model.clubname);
                v.getContext().startActivity(intent);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(v.getContext(), AddMemberActivity.class);
                intent.putExtra("ClubName",model.clubname);
                v.getContext().startActivity(intent);

                return true;
            }


        });






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
