package org.techtown.Constellation.Dialogs;

import android.app.Dialog;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.techtown.Constellation.R;
import org.techtown.Constellation.link.UserObj;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static android.content.Context.INPUT_METHOD_SERVICE;

public class Custom_Dialog_for_g_search {
        private EditText code_get;
        private final Fragment F;
        private String return_code;
        private FirebaseFirestore db;
        private FirebaseUser user;
        private InputMethodManager inputMethodManager;
        private int flag;
        public Custom_Dialog_for_g_search(Fragment fragment) {
            this.F = fragment;
        }
        public void Go_Dialog1(final UserObj userObj){
            flag = 0;
            db = FirebaseFirestore.getInstance();
            user = FirebaseAuth.getInstance().getCurrentUser();
            final Dialog dialog = new Dialog(F.getContext());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.custom_dialog);
            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setAttributes(params);
            dialog.show();
            inputMethodManager= (InputMethodManager) F.getActivity().getSystemService(INPUT_METHOD_SERVICE);
            Button Yes = dialog.findViewById(R.id.Yes_B);
            Button No = dialog.findViewById(R.id.No_B);
            code_get = dialog.findViewById(R.id.g_code_in);
            Yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    inputMethodManager.hideSoftInputFromWindow(code_get.getWindowToken(), 0);
                    return_code = code_get.getText().toString();
                    if (!TextUtils.isEmpty(return_code)) {
                        if (user != null) {
                            // User is signed in
                            Log.d(TAG, "현재 사용자 인증됨.");
                            final String Cuid = user.getUid();
                            final Map<String, Object> g_code = new HashMap<>();
                            g_code.put("g_code", return_code);
                            db.collection("group_code")
                                    .whereEqualTo("code", "" + return_code)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    if (document.exists()) {
                                                        db.collection("user_info").document("" + Cuid)
                                                                .update(g_code)
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            flag = 1;
                                                                            Log.d(TAG, "성공");

                                                                            Map<String, Object> info1 = new HashMap<>();
                                                                            info1.put("name", userObj.getname());
                                                                            db.collection("group_code")

                                                                                    .document(""+return_code)
                                                                                    .collection("member")
                                                                                    .document(""+user.getUid())
                                                                                    .set(info1);

                                                                            Log.d(TAG, "입력 성공");


                                                                            Toast.makeText(F.getContext(), "성공적으로 가입했어요!", Toast.LENGTH_SHORT).show();
                                                                            dialog.dismiss();
                                                                            Intent intent = F.getActivity().getIntent();
                                                                            F.getActivity().finish();
                                                                            F.getActivity().startActivity(intent);
                                                                        }
                                                                    }
                                                                });
                                                    }
                                                }
                                            } else {
                                                Log.d(TAG, "Error getting documents: ", task.getException());
                                            }
                                        }
                                    });
                        } else {
                            // No user is signed in
                            Log.d(TAG, "NoUser");
                        }
                    }
                    else{
                        Toast toast = Toast.makeText(F.getContext(),"아무것도 쓰지 않았네요~", Toast.LENGTH_SHORT);
                        toast.show();
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



}
