package org.techtown.Constellation.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.skydoves.balloon.ArrowConstraints;
import com.skydoves.balloon.ArrowOrientation;
import com.skydoves.balloon.Balloon;
import com.skydoves.balloon.BalloonAnimation;
import com.skydoves.balloon.BalloonSizeSpec;
import com.skydoves.balloon.OnBalloonClickListener;
import com.skydoves.balloon.OnBalloonDismissListener;
import com.skydoves.balloon.OnBalloonOutsideTouchListener;
import com.skydoves.balloon.TextForm;
import com.skydoves.balloon.overlay.BalloonOverlayAnimation;
import com.skydoves.balloon.overlay.BalloonOverlayRoundRect;

import org.jetbrains.annotations.NotNull;
import org.techtown.Constellation.R;
import org.techtown.Constellation.Write_ac.WritingActivity;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {
    private HomeViewModel homeViewModel;
    Button book_title,book_info;
    String string;
    View root;
    Balloon balloon_book, balloon_info;
    Context context;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        homeViewModel = new ViewModelProvider(this)
                .get(HomeViewModel.class);
        SharedPreferences sharedPref = getActivity().getSharedPreferences("sFile",MODE_PRIVATE);
        string = sharedPref.getString("book","asd");
        root = inflater.inflate(R.layout.fragment_home, container, false);
        book_title = root.findViewById(R.id.book_title_home);
        book_info = root.findViewById(R.id.book_info);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                book_title.setText(string);
            }
        });

        context = getContext();
        balloon_book = new Balloon.Builder(context)
                .setPadding(10)
                .setArrowSize(10)
                .setArrowOrientation(ArrowOrientation.BOTTOM)
                .setArrowConstraints(ArrowConstraints.ALIGN_ANCHOR)
                .setArrowPosition(0.5f)
                .setHeight(BalloonSizeSpec.WRAP)
                .setTextSize(15f)
                .setCornerRadius(4f)
                .setAlpha(0.9f)
                .setWidth(BalloonSizeSpec.WRAP)
                .setText(" 이번에 선정된 책 이름이에요.\n 버튼을 눌러 주제를 확인하고 의견을 작성해 보세요! ")
                .setTextColor(ContextCompat.getColor(context, R.color.default_text))
                .setTextTypeface(R.font.nanumgothic_light)
                .setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimarygrey))
                .setBalloonAnimation(BalloonAnimation.OVERSHOOT)
                .setIsVisibleOverlay(true)
                .setOverlayColorResource(R.color.overlay)
                .setOverlayPadding(10f)
                .setOverlayPosition(new Point(405,700))
                .setBalloonOverlayAnimation ( BalloonOverlayAnimation. FADE )
                .setOverlayShape(new BalloonOverlayRoundRect(12f,12f))
                .setLifecycleOwner(getViewLifecycleOwner())
                .setAutoDismissDuration(4000L)
                .setOnBalloonDismissListener(new OnBalloonDismissListener() {
                    @Override
                    public void onBalloonDismiss() {
                    }
                })
                .build();
        balloon_info = new Balloon.Builder(context)
                .setPadding(10)
                .setArrowSize(10)
                .setArrowOrientation(ArrowOrientation.TOP)
                .setArrowConstraints(ArrowConstraints.ALIGN_ANCHOR)
                .setArrowPosition(0.5f)
                .setHeight(BalloonSizeSpec.WRAP)
                .setTextSize(15f)
                .setCornerRadius(4f)
                .setAlpha(0.9f)
                .setWidth(BalloonSizeSpec.WRAP)
                .setText(" 선정된 책에 대한 정보를 볼 수 있어요 :) ")
                .setTextColor(ContextCompat.getColor(context, R.color.default_text))
                .setTextTypeface(R.font.nanumgothic_light)
                .setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimarygrey))
                .setBalloonAnimation(BalloonAnimation.OVERSHOOT)
                .setIsVisibleOverlay(true)
                .setOverlayColorResource(R.color.overlay)
                .setOverlayPadding(10f)
                .setOverlayPosition(new Point(705,950))
                .setBalloonOverlayAnimation ( BalloonOverlayAnimation. FADE )
                .setOverlayShape(new BalloonOverlayRoundRect(12f,12f))
                .setLifecycleOwner(getViewLifecycleOwner())
                .setOnBalloonDismissListener(new OnBalloonDismissListener() {
                    @Override
                    public void onBalloonDismiss() {
                        Toast.makeText(getContext(), "도움이 되었나요?", Toast.LENGTH_SHORT).show();
                    }
                })
                .build();

        book_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("클릭");
                Intent intent = new Intent(book_title.getContext(), WritingActivity.class);// Intent 선언
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);   // Intent 시작
                getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            }
        });

        return root;

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
                balloon_book.showAlignTop(book_title);
                balloon_book.relayShowAlignBottom(balloon_info,book_info);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}




