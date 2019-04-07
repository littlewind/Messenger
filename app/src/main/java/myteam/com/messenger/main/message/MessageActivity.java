package myteam.com.messenger.main.message;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import myteam.com.messenger.R;
import myteam.com.messenger.model.Message;
import myteam.com.messenger.model.Room;
import myteam.com.messenger.model.RoomDetail;
import myteam.com.messenger.model.User;

public class MessageActivity extends AppCompatActivity {

    ImageView profile_image;
    TextView username;

    FirebaseUser currentUser;
    DatabaseReference reference;

    ImageButton btn_send;
    EditText text_send;

    Intent intent;

    MessageAdapter messageAdapter;
    List<Message> messageList;
    RecyclerView recyclerView;

    String receiverID;
    String roomID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        btn_send = findViewById(R.id.btn_send);
        text_send = findViewById(R.id.text_send);

        intent = getIntent();
//        userid = intent.getStringExtra("userid");
        receiverID = intent.getStringExtra("receiverID");
        roomID = intent.getStringExtra("roomID");
        
        // create room if not exist
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final String senderID = currentUser.getUid();
//        if (TextUtils.isEmpty(roomID)) {
        if (roomID == null || roomID.equals("")) {
            // create room for sender & receiver
            roomID = createRoom(senderID, receiverID);
        }

        final String finalRoomID = roomID;
        reference = FirebaseDatabase.getInstance().getReference("Users").child(receiverID);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                if (user.getImageURL().equals("default")){
                    profile_image.setImageResource(R.mipmap.meo);
                } else {
                    //and this
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_image);
                }

                readMessage(user.getImageURL(), finalRoomID);
                setLastMessage(finalRoomID);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // send message
//        final String roomID = getRoomID;
        final String mRoomID = roomID;
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = text_send.getText().toString();
                if (!msg.equals("")) {
                    sendMessage(msg.trim(), senderID, mRoomID);
                    text_send.setText("");
                }
            }
        });
    }

    private String createRoom(String senderID, String receiverID) {

        DatabaseReference reference;

        // generate roomID
        reference = FirebaseDatabase.getInstance().getReference();
        String roomID = reference.push().getKey();

        Room newRoom = new Room(roomID);

        // add room for sender
//        Room senderRoom = new Room(roomID);
        reference = FirebaseDatabase.getInstance().getReference("Rooms").child(senderID);
//        reference.child(receiverID).setValue(senderRoom);
        reference.child(receiverID).setValue(newRoom);

        // add room for receiver
//        Room receiverRoom = new Room(roomID);
        reference = FirebaseDatabase.getInstance().getReference("Rooms").child(receiverID);
//        reference.child(senderID).setValue(receiverRoom);
        reference.child(senderID).setValue(newRoom);

        // create on room detail
        reference = FirebaseDatabase.getInstance().getReference("RoomDetail");
        ArrayList<String> memberIDs = new ArrayList<>();
        memberIDs.add(senderID);
        memberIDs.add(receiverID);
        RoomDetail roomDetail = new RoomDetail(roomID,memberIDs,"");
        reference.child(roomID).setValue(roomDetail);
//        RoomDetail detail = new RoomDetail();
//        detail.setRoomID(roomID);
//        detail.setLastMsgID("");
//        ArrayList<String> memberIDs = new ArrayList<>();
//        memberIDs.add(senderID);
//        memberIDs.add(receiverID);
//        detail.setMemberIDs(memberIDs);
//        roomDetail.child(roomID).setValue(detail);

        return roomID;
    }

    private void sendMessage(String message, String senderID, String mRoomID) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Messages").child(mRoomID);
        Message msg = new Message(message,false, senderID);
        reference.push().setValue(msg);
    }

    private void readMessage(final String userImageUrl, final String roomID) {

        messageList = new ArrayList<>();

        DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference("Messages").child(roomID);
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messageList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);
                    messageList.add(message);
                }

                messageAdapter = new MessageAdapter(MessageActivity.this, messageList, userImageUrl);
                recyclerView.setAdapter(messageAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void setLastMessage(final String roomID) {

        DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference("Messages").child(roomID);
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String lastMessageID = "";
                // get the latest msg
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    lastMessageID = snapshot.getKey();
                }

                // update last msg of room
                DatabaseReference roomDetail = FirebaseDatabase.getInstance().getReference("RoomDetail").child(roomID);
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("lastMsgID",lastMessageID);
                roomDetail.updateChildren(hashMap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void status(String status){
        reference = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }

}
