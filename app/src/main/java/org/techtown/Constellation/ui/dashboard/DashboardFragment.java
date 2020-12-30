package org.techtown.Constellation.ui.dashboard;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.Spinner;
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

import org.techtown.Constellation.MainActivity;
import org.techtown.Constellation.R;
import org.techtown.Constellation.link.BookObj;
import org.techtown.Constellation.link.UserObj;
import org.techtown.Constellation.Dialogs.Dialogs;
import org.techtown.Constellation.ui.dashboard.Custom_Spinner.sp_adapter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

public class DashboardFragment extends Fragment {
    int icount, flag, i_c, i_cc, indexx;
    private Dialogs dialogs;
    private FirebaseAuth firebaseAuth;
    UserObj userObj;
    private FirebaseFirestore db, db2, db3, db4;
    FirebaseUser user;
    private String Cuid; //현재 유저 uid
    String string1, string2;
    private ArrayList<String> list;
    private View root;
    private Spinner spinner_title;
    private List<String> title_array, bookcode_array;
    private sp_adapter sp_adapter;
    private TextView title, solo;
    private ImageView imageView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        db2 = FirebaseFirestore.getInstance();
        db3 = FirebaseFirestore.getInstance();
        db4 = FirebaseFirestore.getInstance();
        dialogs = new Dialogs();
        // 책 제목 리스트
        bookcode_array = new ArrayList<String>(); // 책 코드 리스트

        setHasOptionsMenu(true);

        root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        imageView = root.findViewById(R.id.imageView8);
        solo = root.findViewById(R.id.textView13);
        spinner_title = root.findViewById(R.id.spinner_title);
        spinner_title.setPrompt("책을 선택하세요.");
        user = FirebaseAuth.getInstance().getCurrentUser();
        userObj = new UserObj();


        final SharedPreferences sharedPref = getActivity().getSharedPreferences("sFile", MODE_PRIVATE);
        string1 = sharedPref.getString("book", "asd");
//        string2 = sharedPref.getString("bookcode","asd");

        if (user != null) {
            // User is signed in
            dialogs.progressON(this.getActivity(), "이웃 별 보는중...");
            Log.d(TAG, "현재 사용자 인증됨.");

            Cuid = user.getUid();

            DocumentReference docRef = db.collection("user_info").document("" + Cuid);
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    userObj = documentSnapshot.toObject(UserObj.class);
                }
            });

            startLoading(); //그룹 유무 확인.

        } else {
            // No user is signed in
            Log.d(TAG, "NoUser");
        }

        return root;
    }

    private void startLoading() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (userObj.getG_code().equals("0000A")) {
                    Log.d(TAG, "현재 사용자 그룹없음.");
                    //title.setText("");
                    imageView.setVisibility(View.VISIBLE);
                    solo.setVisibility(View.VISIBLE);
                    dialogs.progressOFF();
                } else {
                    Log.d(TAG, "현재 사용자 그룹있음." + userObj.getG_code());
                    startUDBLoading();
                    startSpLoading();
                }
            }
        }, 1500);

    }

    private void startUDBLoading() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "스피너로딩.");
                db3.collection("user_info").document("" + Cuid)
                        .collection("op")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    i_c = 0;
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                        Log.d(TAG, "" + i_c);
                                        bookcode_array.add(i_c, document.getId());
                                        i_c++;
                                    }
                                }
                            }
                        });
            }
        }, 1000);
    }

    private void startSpLoading() {

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                title_array = new ArrayList<String>();
//                bookcode_array.add(i_c,document.getId());
                i_cc = 0;
                indexx = 1;
                if (!bookcode_array.isEmpty()) {
                    for (; i_cc < bookcode_array.size(); i_cc++) {
                        CollectionReference uidRef = db4.collection("book_and_topic");
                        uidRef.document("" + bookcode_array.get(i_cc))
                                .get()// 북코드와 북네임 어레이를 따로 둘 것.
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        Log.d(TAG, "" + documentSnapshot.get("book"));
                                        title_array.add(indexx++, "" + documentSnapshot.get("book"));
                                        Log.d(TAG, "" + (indexx - 1) + " 그리고 " + title_array.get(indexx - 1));
                                    }
                                });

                    }
                    title_array.add(0, "책을 선택해 주세요.");
                    sp_adapter = new sp_adapter(getContext(), title_array);
                    spinner_title.setAdapter(sp_adapter);
                    spinner_title.setSelection(0, false);
                    dialogs.progressOFF();
                    spinner_title.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (position != 0) {
                                Log.d(TAG, "" + position);
                                string2 = bookcode_array.get(position - 1);
                                dialogs.progressON(getActivity(), "목소리 가져오는중...");
                                startDBLoading();
                                startsetting();
                            } else {
                                Log.d(TAG, "" + position);
                                dialogs.progressON(getActivity(), "잠시만요!");
                                list = new ArrayList<>(); //의견 리스트
                                startsetting();
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                } else {
                    //아무 책에도 의견을 등록하지 않은 경우에 할 행동 써라...
                }
            }


        }, 2000);
    }

    private void startDBLoading() {
        list = new ArrayList<>(); //의견 리스트
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "디비로딩.");
                db.collection("user_info").whereEqualTo("g_code", "" + userObj.getG_code())
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
                                        uidRef.document("" + temp)
                                                .collection("op").document(string2)
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            DocumentSnapshot document = task.getResult();
                                                            if (document.exists()) {
                                                                Log.d(TAG, "DocumentSnapshot data: " + document.get("opinion"));
                                                                list.add(icount++, "" + document.get("opinion"));
                                                            } else {
                                                                list.add(icount++, "앗, 아직 답하지 않았네요!");
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
                if (icount > 1) {
                    flag = 1;
                } else flag = 0;
                DashBoardAdapter adapter = new DashBoardAdapter(list, icount, userObj, flag);
                recyclerView.setAdapter(adapter);
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
        switch (item.getItemId()) {
            case R.id.question:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}