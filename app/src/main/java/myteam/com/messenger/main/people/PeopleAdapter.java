package myteam.com.messenger.main.people;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import myteam.com.messenger.main.message.MessageActivity;
import myteam.com.messenger.R;
import myteam.com.messenger.model.Room;
import myteam.com.messenger.model.User;

public class PeopleAdapter extends RecyclerView.Adapter<PeopleAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mFriends;

    public PeopleAdapter(Context mContext, List<User> mFriends) {
        this.mContext = mContext;
        this.mFriends = mFriends;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
        return new PeopleAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {
        final User friend = mFriends.get(position);
//        viewHolder.tvUserName.setText(friend.getUsername());
//        if (friend.getImageURL().equals("default")) {
//            viewHolder.ivAvatar.setImageResource(R.mipmap.meo);
//        } else  {
//            Glide.with(mContext).load(friend.getImageURL()).into(viewHolder.ivAvatar);
//        }

//        if (friend.getStatus().equals("Online")) {
//            viewHolder.ivStatus.setColorFilter(ContextCompat.getColor(mContext,R.color.online));
//        } else {
//            viewHolder.ivStatus.setColorFilter(ContextCompat.getColor(mContext,R.color.offline));
//        }


        ///
        DatabaseReference users = FirebaseDatabase.getInstance().getReference("Users").child(friend.getuID());
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User receiver = dataSnapshot.getValue(User.class);
                // set image
                assert receiver != null;
                if (receiver.getImageURL().equals("default")) {
                    viewHolder.ivAvatar.setImageResource(R.mipmap.meo);

                } else {
                    Glide.with(mContext).load(receiver.getImageURL()).into(viewHolder.ivAvatar);
                }

                // set status
                if (receiver.getStatus().equals("online")) {
                    viewHolder.ivStatus.setColorFilter(ContextCompat.getColor(mContext,R.color.online));
                } else {
                    viewHolder.ivStatus.setColorFilter(ContextCompat.getColor(mContext,R.color.offline));
                }

                // set user name
                viewHolder.tvUserName.setText(receiver.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ///

        viewHolder.btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference("Rooms").child(fuser.getUid()).child(friend.getuID());
                dbReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String roomID = "";
                        Room room = dataSnapshot.getValue(Room.class);
                        if (room != null) {
                            roomID = room.getRoomID();
                        }
                        Intent intent = new Intent(mContext,MessageActivity.class);
                        intent.putExtra("receiverID", friend.getuID());
                        if (roomID.isEmpty()) {
                            intent.putExtra("roomID", "");
                        } else intent.putExtra("roomID", roomID);
                        mContext.startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFriends.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName;
        ImageView ivAvatar;
        ImageView ivStatus;
        Button btnMessage;
        Button btnMore;

        public ViewHolder(View itemView) {
            super(itemView);

            tvUserName = itemView.findViewById(R.id.tvUserName);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            ivStatus = itemView.findViewById(R.id.ivStatus);
            btnMessage = itemView.findViewById(R.id.btnMessage);
            btnMore = itemView.findViewById(R.id.btnMore);
        }

    }
}
