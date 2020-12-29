package org.techtown.Constellation.Dialogs;

import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.techtown.Constellation.R;
import org.techtown.Constellation.link.UserObj;

public class Custom_Dialog_for_g_code {
    private final Fragment F;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private TextView textView;
    public Custom_Dialog_for_g_code(Fragment fragment) {
        this.F = fragment;
    }
    public void Go_Dialog(UserObj userObjIN){
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        final Dialog dialog = new Dialog(F.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_g_show);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(params);
        dialog.show();
        Button No = dialog.findViewById(R.id.No_Bt);
        textView = dialog.findViewById(R.id.g_code_in);
        textView.setText(userObjIN.getG_code());

        // 취소 버튼
        No.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                dialog.dismiss();
                Toast.makeText(F.getContext(), "돌아갑니다.", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
