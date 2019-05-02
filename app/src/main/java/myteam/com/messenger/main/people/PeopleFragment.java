package myteam.com.messenger.main.people;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import myteam.com.messenger.addcontact.AddContactActivity;
import myteam.com.messenger.R;
import myteam.com.messenger.addcontact.UserAdapter;
import myteam.com.messenger.model.User;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class PeopleFragment extends Fragment {

    private final static String TAG = PeopleFragment.class.getSimpleName();
    public static final int TITLE_ID = R.string.title_frag_people;

    CircleImageView profile_image;

    RecyclerView mRecyclerView;
    PeopleAdapter mPeopleAdapter;
//    List<String> FriendsUID;
    List<User> Friends;
    List<User> searchUserList;

    Button btnAddContact;

    EditText etSearch;

    public PeopleFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_people, container, false);

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

        btnAddContact = view.findViewById(R.id.btnAddContact);
        btnAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),AddContactActivity.class);
                startActivity(intent);
            }
        });


        mRecyclerView = view.findViewById(R.id.rvContacts);
//        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Friends = new ArrayList<>();
        searchUserList = new ArrayList<>();

        readFriends();

        etSearch = view.findViewById(R.id.etSearch);
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

        return view;
    }

    private void readFriends() {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Friends").child(firebaseUser.getUid());

        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Friends.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);

                    if (!user.getuID().equals(firebaseUser.getUid())) {
                        Friends.add(user);
                    }

                }

                mPeopleAdapter = new PeopleAdapter(getContext(), Friends);
                mRecyclerView.setAdapter(mPeopleAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void searchUsers(String s) {
        searchUserList.clear();
        for (User user : Friends) {
            if (user.getSearch().contains(s)) {
                searchUserList.add(user);
            }

        }
        mPeopleAdapter = new PeopleAdapter(getContext(), searchUserList);
        mRecyclerView.setAdapter(mPeopleAdapter);

    }

}
