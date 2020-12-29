package org.techtown.Constellation.Dialogs;

import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.techtown.Constellation.R;
import org.techtown.Constellation.link.UserObj;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class Custom_Dialog_for_g_close {
        private final Fragment F;
        private FirebaseFirestore db;
        private FirebaseUser user;
        private UserObj userObj;
        private Dialog dialog;
        private int index_count;
        private Dialogs dialogs;
        private final ArrayList<String> count = new ArrayList<>();
        public Custom_Dialog_for_g_close(Fragment fragment) {
            this.F = fragment;
        }
        public void Go_Dialog2(){
            dialogs = new Dialogs();
            db = FirebaseFirestore.getInstance();
            user = FirebaseAuth.getInstance().getCurrentUser();
            dialog = new Dialog(F.getContext());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.custom_dialog_2);
            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setAttributes(params);
            dialog.show();
            Button Yes = dialog.findViewById(R.id.Yes_Bt);
            Button No = dialog.findViewById(R.id.No_Bt);
            userObj = new UserObj();
            DocumentReference docRef = db.collection("user_info").document("" + user.getUid());
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    userObj = documentSnapshot.toObject(UserObj.class);
                }
            });

            Yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    if (user != null) {
                        // User is signed in
                        Log.d(TAG, "현재 사용자 인증됨.");
                        dialogs.progressON(F.getActivity(),"그룹 정보 확인중..");
                        start_get_member_list();
                        start_get_delete();


                    } else {
                        // No user is signed in
                        Log.d(TAG, "NoUser");
                    }


                }
            });

            // 취소 버튼
            No.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    dialog.dismiss();
                    Toast.makeText(F.getContext(), "그만둡니다.", Toast.LENGTH_SHORT).show();
                }
            });


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
                                    if(document.get("king").equals(user.getUid()) && count.size() ==1 ){
                                        dialogs.progressSET("별무리가 흩어지는중...");
                                        db.collection("group_code")
                                                .document(""+userObj.getG_code())
                                                .delete()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        Log.d(TAG, "내가 그룹장이면서 그룹원이 1명 남았을 때. 성공");
                                                        start_member_delete();
                                                        start_user_update();
                                                        dialogs.progressOFF();
                                                        dialog.dismiss();
                                                        Toast.makeText(F.getContext(), "별 무리가 흩어졌습니다...", Toast.LENGTH_SHORT).show();
                                                        Intent intent = F.getActivity().getIntent();
                                                        F.getActivity().finish();
                                                        F.getActivity().startActivity(intent);
                                                    }
                                                });
                                    }
                                    //내가 그룹장이고, 1명 이상이 남았을 때.
                                    else if(document.get("king").equals(user.getUid())&& count.size() >1){
                                        dialogs.progressSET("별무리에서 빠져나오는중...");
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
                                                        dialogs.progressOFF();
                                                        dialog.dismiss();
                                                        Toast.makeText(F.getContext(), "별 무리에서 빠져나왔습니다.", Toast.LENGTH_SHORT).show();
                                                        Intent intent = F.getActivity().getIntent();
                                                        F.getActivity().finish();
                                                        F.getActivity().startActivity(intent);
                                                    }
                                                });
                                    }
                                    else{
                                        dialogs.progressSET("별무리에서 빠져나오는중...");
                                        Log.d(TAG, "그외");
                                        start_member_delete();
                                        start_user_update();
                                        dialogs.progressOFF();
                                        dialog.dismiss();
                                        Toast.makeText(F.getContext(), "별 무리에서 빠져나왔습니다.", Toast.LENGTH_SHORT).show();
                                        Intent intent = F.getActivity().getIntent();
                                        F.getActivity().finish();
                                        F.getActivity().startActivity(intent);
                                    }
                                } else {
                                    Log.d(TAG, "get failed with ", task.getException());
                                }
                            }
                        });

            }
        }, 3000);
    }
    private void start_user_update() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialogs.progressSET("정보 업데이트중...");
                Log.d(TAG, "유저컬렉션 업데이트");
                db.collection("user_info").document("" + user.getUid())
                        .update("g_code","0000A").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, "성공");
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
                dialogs.progressSET("정보 업데이트중...");
                Log.d(TAG, "멤버컬렉션 지우기");
                db.collection("group_code")
                        .document(""+userObj.getG_code())
                        .collection("member")
                        .document(user.getUid())
                        .delete();

            }
        }, 3500);
    }



}
