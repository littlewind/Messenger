package myteam.com.messenger.addcontact;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import myteam.com.messenger.message.MessageActivity;
import myteam.com.messenger.R;
import myteam.com.messenger.model.Room;
import myteam.com.messenger.model.User;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>{
    private Context mContext;
    private List<User> mUsers;
    FirebaseUser currentUser;
    List<Boolean> isFriend;

    public UserAdapter(Context mContext, List<User> mUsers, FirebaseUser currentUser, List<Boolean> isFriend) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.currentUser = currentUser;
        this.isFriend = isFriend;
    }



    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_search_item, parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final UserAdapter.ViewHolder viewHolder, int position) {
        final User user = mUsers.get(position);
        viewHolder.tvUserName.setText(user.getUsername());
        if (user.getImageURL().equals("default")) {
            viewHolder.ivAvatar.setImageResource(R.mipmap.meo);
        } else  {
            Glide.with(mContext).load(user.getImageURL()).into(viewHolder.ivAvatar);
        }
        if (isFriend.get(position)) {
            viewHolder.btnAction.setBackgroundResource(R.mipmap.message);
//            viewHolder.btnAction.setOnClickListener(new View.OnClickListener() {
////                @Override
////                public void onClick(View view) {
////                    Intent intent = new Intent(mContext, MessageActivity.class);
////                    intent.putExtra("userid", user.getuID());
////                    mContext.startActivity(intent);
////                }
////            });
            viewHolder.btnAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
                    DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference("Rooms").child(fuser.getUid()).child(user.getuID());
                    dbReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String roomID = "";
                            Room room = dataSnapshot.getValue(Room.class);
                            if (room != null) {
                                roomID = room.getRoomID();
                            }
                            Intent intent = new Intent(mContext,MessageActivity.class);
                            intent.putExtra("receiverID", user.getuID());
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
        } else {
            viewHolder.btnAction.setBackgroundResource(R.drawable.ic_person_add_black_24dp);
            viewHolder.btnAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference friendsReference = FirebaseDatabase.getInstance().getReference("Friends").child(currentUser.getUid()).child(user.getuID());
                    friendsReference.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            viewHolder.btnAction.setBackgroundResource(R.mipmap.message);
                        }
                    });
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName;
        ImageView ivAvatar;
        Button btnAction;


        public ViewHolder(View itemView) {
            super(itemView);

            tvUserName = itemView.findViewById(R.id.tvUserName);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            btnAction = itemView.findViewById(R.id.btnMessage);

        }

    }
}
