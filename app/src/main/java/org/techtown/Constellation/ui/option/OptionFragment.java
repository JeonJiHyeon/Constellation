package org.techtown.Constellation.ui.option;

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
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.techtown.Constellation.Dialogs.Dialogs;
import org.techtown.Constellation.R;
import org.techtown.Constellation.link.UserObj;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

public class OptionFragment extends Fragment {

    private OptionViewModel optionViewModel;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    private FirebaseFirestore db;
    private UserObj userObj;
    private String token;
    private final ArrayList<String> count = new ArrayList<>();
    private final ArrayList<String> delete_list = new ArrayList<>();
    private int index_count;
    private Button log_out;
    private AppCompatDialog progressDialog;
    private GoogleSignInClient mGoogleSignInClient;
    private Dialogs dialogs;
    private AuthCredential credential;
    private FirebaseAuth.AuthStateListener mAuthListener;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        dialogs = new Dialogs();
        dialogs.setdialog(progressDialog);
        dialogs.progressON(getActivity(),"회원정보 모으는중...");
        optionViewModel =
                ViewModelProviders.of(this).get(OptionViewModel.class);
        View root = inflater.inflate(R.layout.fragment_option, container, false);
        log_out = root.findViewById(R.id.sign_out_button);
        log_out.setClickable(false);
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        String Cuid = user.getUid();

        DocumentReference docRef = db.collection("user_info").document("" + Cuid);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                userObj = documentSnapshot.toObject(UserObj.class);
            }
        });
        start_get_member_list();

        log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("회원 탈퇴 클릭");
                dialogs.progressON(getActivity(),"안녕히 가세요...");
                start_get_delete();

            }
        });

        return root;
    }
    private void start_get_member_list() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "db 가져오는 시간");
                db.collection("group_code").document("" +userObj.getG_code())
                        .collection("member")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    index_count = 0;
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                        count.add(index_count,document.getId());
                                        index_count++;
                                        // count[i] = document.getId();

                                    }
                                    dialogs.progressOFF();
                                    log_out.setClickable(true);
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });

            }
        }, 2500);
    }
    private void start_get_delete() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "db 가져오는 시간");
                Log.d(TAG, ""+userObj.getG_code());
                db.collection("group_code").document("" +userObj.getG_code())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    Log.d(TAG, "DocumentSnapshot data: " + document.get("king"));
                                    //내가 그룹장이면서 그룹원이 1명 남았을 때.
                                    if(!userObj.getG_code().equals("0000A") && document.get("king").equals(user.getUid()) && count.size() ==1 ){
                                        db.collection("group_code")
                                                .document(""+userObj.getG_code())
                                                .delete()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        Log.d(TAG, "내가 그룹장이면서 그룹원이 1명 남았을 때. 성공");
                                                        start_member_delete();
                                                        start_user_update();
                                                        start_user_delete();
                                                    }
                                                });
                                    }
                                    //내가 그룹장이고, 1명 이상이 남았을 때.
                                    else if(!userObj.getG_code().equals("0000A") && document.get("king").equals(user.getUid())&& count.size() >1){
                                        Log.d(TAG, "배열 요소 수: " + count.size());
                                        String temp = "";
                                        //카운트배열이 빌때까지
                                        for(index_count--;index_count>=0;index_count--){
                                            if (!count.get(index_count).isEmpty()) {
                                                if (!count.get(index_count).equals(user.getUid())) {
                                                    temp = count.get(index_count);
                                                }
                                            }
                                        }
                                        //king을 temp로 업데이트
                                        db.collection("group_code")
                                                .document(""+userObj.getG_code())
                                                .update("king", ""+temp)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        Log.d(TAG, "내가 그룹장이고, 1명 이상이 남았을 때. 성공");
                                                        //업데이트 되면, 멤버에 있는 내 문서를 지움.
                                                        start_member_delete();
                                                        start_user_update();
                                                        start_user_delete();
                                                    }
                                                });
                                    }
                                    else{
                                        Log.d(TAG, "그외");
                                        start_member_delete();
                                        start_user_update();
                                        start_user_delete();
                                    }
                                } else {
                                    Log.d(TAG, "get failed with ", task.getException());
                                }
                            }
                        });

            }
        }, 3000);
    }
    private void start_member_delete() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "멤버컬렉션 지우기");
                db.collection("group_code")
                        .document(""+userObj.getG_code())
                        .collection("member")
                        .document(user.getUid())
                        .delete();
            }
        }, 3500);
    }
    private void start_user_update() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "유저컬렉션 업데이트");
                db.collection("user_info").document("" + user.getUid())
                        .delete();
                db.collection("user_info").document("" + user.getUid()).collection("op")
                        .whereNotEqualTo("bookcode", "null").get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    int i = 0;
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                        delete_list.add(i,""+document.getId());
                                        i++;
                                    }
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });
            }
        }, 3000);
    }
    private void start_user_delete() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "유저컬렉션 업데이트2");
                int i = 0;
                for(;delete_list.size()-1>=i;i++){
                    Log.d(TAG, "유저컬렉션 업데이트21111111");
                    Log.d(TAG, "유저컬렉션 업데이트21115411"+delete_list.get(i));
                    db.collection("user_info").document("" + user.getUid())
                            .collection("op").document(delete_list.get(i))
                            .delete();
                }

                if(delete_list.size()-1<i){
                    Log.d(TAG, "유저컬렉션 업데이트21595911111");
                    user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d(TAG, "삭제완료");
                        }
                    });
                    firebaseAuth.signOut();
                    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(getString(R.string.server_client_id))
                            .requestEmail()
                            .build();
                    SharedPreferences sharedPref1 = getContext().getSharedPreferences("Wr_AcFile",MODE_PRIVATE);
                    SharedPreferences.Editor editor1 = sharedPref1.edit();
                    editor1.remove("OP");
                    editor1.commit(); // 돌아올 일은 없겠지만...

                    mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
                    mGoogleSignInClient.revokeAccess();
                    mGoogleSignInClient.signOut();
                    dialogs.progressOFF();
                    getActivity().finish();

                }
            }
        }, 4500);
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
    @Override
    public void onDestroy() {
        super.onDestroy();
        firebaseAuth = FirebaseAuth.getInstance();
        if (mAuthListener != null) {
            firebaseAuth.removeAuthStateListener(mAuthListener);
        }
    }

}

