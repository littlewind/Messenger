package myteam.com.messenger.addcontact;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import myteam.com.messenger.R;
import myteam.com.messenger.model.User;

public class AddContactActivity extends AppCompatActivity {

    EditText etSearch;
    private RecyclerView mRecyclerView;
    private UserAdapter userAdapter;
    private List<User> mUsers;
    private List<User> friendList;
    List<Boolean> isFriend;

    DatabaseReference reference;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        friendList = new ArrayList<>();

        readFriends();

        mRecyclerView = findViewById(R.id.rvSearchUsers);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        mUsers = new ArrayList<>();
        isFriend = new ArrayList<>();
        
        etSearch = findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchUsers(charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }

    private void readFriends() {
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference friendsReference = FirebaseDatabase.getInstance().getReference("Friends").child(currentUser.getUid());
        friendsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                friendList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User friend = snapshot.getValue(User.class);
                    friendList.add(friend);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void searchUsers(String s) {

        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("search")
                .startAt(s)
                .endAt(s+"\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                isFriend.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);

                    assert user != null;
                    assert fuser != null;

                    if (!user.getuID().equals(fuser.getUid())) {
                        if (inFriendList(friendList,user)){
                            isFriend.add(true);
                        } else {
                            isFriend.add(false);
                        }
                        mUsers.add(user);
                    }
                }

                Log.d("AddContact","Friend List");
                for (User user : friendList) {
                    Log.d("AddContact",user.getUsername());
                }
                Log.d("AddContact","----------");

                userAdapter = new UserAdapter(getApplicationContext(), mUsers, fuser, isFriend);
                mRecyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private boolean inFriendList(List<User> friendList, User user) {
        for (User curUser : friendList) {
            if (curUser.getuID().equals(user.getuID())) {
                return true;
            }
        }
        return false;
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
