package org.techtown.Constellation.ui.counter;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.techtown.Constellation.R;
import org.techtown.Constellation.link.UserObj;

import static android.content.ContentValues.TAG;

import org.techtown.Constellation.Dialogs.Dialogs;

public class CounterFragment extends Fragment {

    Dialogs dialogs = new Dialogs();
    private FirebaseAuth firebaseAuth;
    UserObj userObj;
    private FirebaseFirestore db;
    FirebaseUser user;
    private TextView cnttext;
    //final SharedPreferences sharedPref = getSharedPreferences("sFile", MODE_PRIVATE);

    private CounterViewModel counterViewModel;
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        user = FirebaseAuth.getInstance().getCurrentUser();
        userObj = new UserObj();
        if (user != null) {
            //dialog.progressON(getActivity(),"별 세는 중...");
            // User is signed in
            Log.d(TAG, "db반영시간기다림");
            Log.d(TAG, "현재 사용자 인증됨.");
            String Cuid = user.getUid();
            DocumentReference docRef = db.collection("user_info").document("" + Cuid);
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    userObj = documentSnapshot.toObject(UserObj.class);
                }
            });

        } else {
            // No user is signed in
            Log.d(TAG, "NoUser");
        }
        startDBLoading();
        counterViewModel =
                ViewModelProviders.of(this).get(CounterViewModel.class);
        View root = inflater.inflate(R.layout.fragment_counter, container, false);
        cnttext = root.findViewById(R.id.cnttext);

        //textView = root.findViewById(R.id.textView);
        //counterViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
        //@Override
         //   public void onChanged(@Nullable String s) {
                //textView.setText(s);
        //    }
        //});
        return root;

    }
    private void startDBLoading() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            int starcnt = 0;
            @Override
            public void run() {
                Log.d(TAG, "디비로딩.");
                CollectionReference uidRef = db.collection("user_info");
                String Cuid = user.getUid();
                //String code = sharedPref.getString("bookcode","asd");
                uidRef.document(""+Cuid).collection("op")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                        starcnt++;
                                        cnttext.setText(""+starcnt);
                                        Log.d(TAG, document.getId() + " => " + starcnt);
                                    }
                                    //cnttext.setText(""+starcnt);
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });

                //dialog.progressOFF();

            }
        }, 1000);
    }
    @Override
    public void onResume() {
        super.onResume();
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.helpmenu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.question :
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}