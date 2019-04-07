package myteam.com.messenger.chats;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import myteam.com.messenger.R;
import myteam.com.messenger.model.Room;
import myteam.com.messenger.model.User;

public class ChatsFragment extends Fragment {

    private final static String TAG = ChatsFragment.class.getSimpleName();
    public static final int TITLE_ID = R.string.title_frag_chats;

    CircleImageView profile_image;

    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<Room> roomList;

    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chats, container, false);
        profile_image = view.findViewById(R.id.ivUserAvatar);

        FirebaseUser firebaseUser;
        DatabaseReference reference;

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user.getImageURL().equals("default")){
                    profile_image.setImageResource(R.mipmap.meo);
                } else {

                    //change this
                    Glide.with(getContext()).load(user.getImageURL()).into(profile_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        roomList = new ArrayList<>();
        readChats();

        return view;
    }

    private void readChats(){
        FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userRooms = FirebaseDatabase.getInstance().getReference("Rooms").child(fuser.getUid());
        userRooms.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                roomList.clear();
                for (final DataSnapshot snapshot : dataSnapshot.getChildren()){
                    final Room room = snapshot.getValue(Room.class);    // final ???
//                    ///  Check if room has any message
//                    DatabaseReference roomDetailRef = FirebaseDatabase.getInstance().getReference("RoomDetail").child(room.getRoomID());
//                    roomDetailRef.addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            RoomDetail roomDetail = snapshot.getValue(RoomDetail.class);
//                            if (!TextUtils.isEmpty(roomDetail.getLastMsgID())) {
//                                roomList.add(room);
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                        }
//                    });
                    ///

                    roomList.add(room);
                }

                chatAdapter = new ChatAdapter(getContext(), roomList);
                recyclerView.setAdapter(chatAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
//        readChats();
    }
}
