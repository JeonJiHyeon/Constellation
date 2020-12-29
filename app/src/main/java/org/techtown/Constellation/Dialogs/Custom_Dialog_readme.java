package org.techtown.Constellation.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import org.techtown.Constellation.R;

public class Custom_Dialog_readme {
    private final Context context;

    public Custom_Dialog_readme(Context context)
    {
        this.context=context;
    }

    public void callDialog(){
        final Dialog readme = new Dialog(context);

        readme.requestWindowFeature(Window.FEATURE_NO_TITLE);
        readme.setContentView(R.layout.custom_dialog_readme);
        readme.show();

        final Button ok = readme.findViewById(R.id.ok);
        //final Button cancel = (Button) readme.findViewById(R.id.cancel);

        // 확인 버튼
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Toast.makeText(context, "이제 시작해볼까요?", Toast.LENGTH_SHORT).show();
                readme.dismiss();
                //((MainActivity) context).finish();
            }
        });

        // 취소 버튼
//        cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view)
//            {
//                dialog.dismiss();
//                Toast.makeText(context, "종료를 취소했습니다", Toast.LENGTH_SHORT).show();
//            }
//        });

    }
}
