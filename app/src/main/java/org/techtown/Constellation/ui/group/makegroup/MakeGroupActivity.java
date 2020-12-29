package org.techtown.Constellation.ui.group.makegroup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.techtown.Constellation.Dialogs.Dialogs;
import org.techtown.Constellation.MainActivity;
import org.techtown.Constellation.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static android.content.ContentValues.TAG;

public class MakeGroupActivity extends Activity{
    private Button Yes,No;
    private FirebaseFirestore db, db2, db3;
    String[] doc_list;
    private String g_codes;
    private Random rand;
    private Dialogs dialogs;
    private TextView g_code_m,Now;
    private EditText g_namem;
    private String temp_name;
    private FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_makegroup);
        No = findViewById(R.id.button9);
        Yes = findViewById(R.id.button10);
        doc_list = new String[50];
        rand = new Random();
        g_codes = "";
        dialogs = new Dialogs();
        dialogs.progressON(this,"그룹코드 생성중...");
        startLoading();
        g_code_m = findViewById(R.id.G_code);
        g_code_m.append(""+g_codes);

        g_namem = findViewById(R.id.G_name);

        Now = findViewById(R.id.Now);
        user = FirebaseAuth.getInstance().getCurrentUser();
        Now.append(",  "+user.getDisplayName());

        db = FirebaseFirestore.getInstance();
        db2 = FirebaseFirestore.getInstance();
        db3 = FirebaseFirestore.getInstance();

        Yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("승낙");
                temp_name = g_namem.getText().toString();
                if (!TextUtils.isEmpty(temp_name)){
                    //내가 만든 그룹 코드로 문서 이름 설정하고,
                    //필드에 그룹 이름 넣어야함.
                    Map<String, Object> g1 = new HashMap<>();
                    g1.put("group_name", temp_name);
                    g1.put("code",g_codes);
                    g1.put("king", user.getUid());
                    db.collection("group_code")
                            .document(""+g_codes).set(g1);
                    Log.d(TAG, "성공");
                    Map<String, Object> p1 = new HashMap<>();
                    p1.put("name", user.getDisplayName());
                    db2.collection("group_code")
                            .document(""+g_codes).collection("member")
                            .document(""+user.getUid()).set(p1);
                    Log.d(TAG, "완료");

                    db3.collection("user_info").document(""+user.getUid())
                            .update("g_code",g_codes);
                    Log.d(TAG, "완료");
                    Toast toast = Toast.makeText(getApplicationContext(),"새로운 별무리를 만들었어요!", Toast.LENGTH_SHORT);
                    toast.show();
                    Intent intent_to_Homefrag = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(intent_to_Homefrag);   // Intent 시작
                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                    finish();

                }
                else if (TextUtils.isEmpty(temp_name)) {
                    Toast toast = Toast.makeText(getApplicationContext(),"아무것도 쓰지 않았네요~", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else if (TextUtils.getTrimmedLength(temp_name)>10){
                    Toast toast = Toast.makeText(getApplicationContext(),"너무.. 길어요..!!", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
        No.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("거절");
                finish();
            }
        });
    }
    @Override
    public void onBackPressed() {
    }
    public void random(){
        for(int i=0;i<5;i++) {
            //0~9 까지 난수 생성
            String ran = Integer.toString(rand.nextInt(10));
            g_codes += ran;
        }
        Log.d(TAG, "생성된 난수"+g_codes);
        db = FirebaseFirestore.getInstance();
    }
    private void startLoading() {
        random();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "db반영시간기다림");
                db.collection("group_code")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    int i = 0;
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                        doc_list[i] = document.getId();
                                        if(g_codes.equals(doc_list[i])){
                                            for(int j=0;j<5;j++) {
                                                //0~9 까지 난수 생성
                                                String ran = Integer.toString(rand.nextInt(10));
                                                g_codes += ran;
                                            }
                                            Log.d(TAG, "재생성된 난수"+g_codes);
                                        }
                                        i++;
                                        dialogs.progressOFF();
                                    }
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });
            }
        }, 2500);
    }
}
