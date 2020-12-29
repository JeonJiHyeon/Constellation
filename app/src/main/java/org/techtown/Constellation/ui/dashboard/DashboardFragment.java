package org.techtown.Constellation.ui.dashboard;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import org.techtown.Constellation.Dialogs.Dialogs;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    int icount,flag;
    private Dialogs dialogs;
    private FirebaseAuth firebaseAuth;
    UserObj userObj;
    private FirebaseFirestore db,db2;
    FirebaseUser user;
    private String Cuid;
    String string1,string2;
    private ArrayList<String> list;
    private View root;
    private TextView title,solo,name2,op1,op2;
    private ImageView imageView;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        db2 = FirebaseFirestore.getInstance();
        dialogs = new Dialogs();
        setHasOptionsMenu(true);

        final SharedPreferences sharedPref = getActivity().getSharedPreferences("sFile",MODE_PRIVATE);
        string1 = sharedPref.getString("book","asd");
        string2 = sharedPref.getString("bookcode","asd");


        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        title = root.findViewById(R.id.title_topic);
        title.setText(string1);
        imageView = root.findViewById(R.id.imageView8);
        solo = root.findViewById(R.id.textView13);


        userObj = new UserObj();
        list = new ArrayList<>();

        if (user != null) {
            // User is signed in
            dialogs.progressON(this.getActivity(),"이웃 별 보는중...");
            Log.d(TAG, "db반영시간기다림");
            Log.d(TAG, "현재 사용자 인증됨.");
            Cuid = user.getUid();
            DocumentReference docRef = db.collection("user_info").document("" + Cuid);
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    userObj = documentSnapshot.toObject(UserObj.class);
                }
            });
            startLoading();
        } else {
            // No user is signed in
            Log.d(TAG, "NoUser");
        }



        /*final TextView textView = root.findViewById(R.id.text_dashboard);
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/
        return root;
    }
    private void startLoading() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (userObj.getG_code().equals("0000A")) {
                    Log.d(TAG, "현재 사용자 그룹없음.");
                    title.setText("");
                    imageView.setVisibility(View.VISIBLE);
                    solo.setVisibility(View.VISIBLE);
                    dialogs.progressOFF();
                } else {
                    Log.d(TAG, "현재 사용자 그룹있음."+userObj.getG_code());
                    startDBLoading();
                    startsetting();
                }
            }
        }, 1500);

    }
    private void startDBLoading() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "디비로딩.");
                db.collection("user_info").whereEqualTo("g_code", ""+userObj.getG_code())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                        String temp = document.getId();
                                        icount = 0;
                                        CollectionReference uidRef = db2.collection("user_info");
                                        uidRef.document(""+temp)
                                                .collection("op").document(string2)
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            DocumentSnapshot document = task.getResult();
                                                            if (document.exists()) {
                                                                Log.d(TAG, "DocumentSnapshot data: " + document.get("opinion"));
                                                                list.add(icount++,""+document.get("opinion"));
                                                            } else {
                                                                list.add(icount++,"앗, 아직 답하지 않았네요!");
                                                                Log.d(TAG, "No such document");
                                                            }
                                                        } else {
                                                            Log.d(TAG, "get failed with ", task.getException());
                                                        }
                                                    }
                                                });
                                    }
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });
            }
        }, 1000);
    }

    private void startsetting() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialogs.progressOFF();
                Log.d(TAG, "셋팅.");
                RecyclerView recyclerView = root.findViewById(R.id.recycler11);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                if(icount>1){flag = 1;}
                else flag = 0;
                DashBoardAdapter adapter = new DashBoardAdapter(list,icount,userObj,flag);
                recyclerView.setAdapter(adapter) ;
            }
        }, 2000);
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
            case R.id.question:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}