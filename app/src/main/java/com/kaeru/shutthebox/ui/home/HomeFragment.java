package com.kaeru.shutthebox.ui.home;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kaeru.shutthebox.R;
import com.kaeru.shutthebox.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class HomeFragment extends Fragment implements View.OnTouchListener {

    private ImageView kazandin, kaybettin, imageViewZar1, imageViewZar2, gifImageView;
    private TextView skorugoster, kalanZarBakiyesi, kaybetmenedeni , bilgi;
    private MediaPlayer mediaPlayer, mediaPlayer2, mediaPlayer3 , sayikapatma, kazanmasesi, kaybetmesesi;
    private int zar1, zar2, zarbakiyesi, skor;
    private List<Integer> kalansayilar;
    private List<ImageView> sayiImageViews;
    private Button zarat, restart;

    private static final int[] diceImages = {
            R.drawable.zar1, R.drawable.zar2, R.drawable.zar3,
            R.drawable.zar4, R.drawable.zar5, R.drawable.zar6
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentHomeBinding binding = FragmentHomeBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        initializeUI(rootView);
        skor = 0;
        updateScoreDisplay();
        zarbakiyesi = 0 ;
        return rootView;
    }

    private void initializeUI(View rootView) {
        gifImageView = rootView.findViewById(R.id.gifImageView);
        bilgi = rootView.findViewById((R.id.bilgi));
        kalanZarBakiyesi = rootView.findViewById(R.id.kalanzarbakiyesi);
        skorugoster = rootView.findViewById(R.id.skorugoster);
        kazandin = rootView.findViewById(R.id.winner);
        kaybettin = rootView.findViewById(R.id.loser);
        kaybetmenedeni = rootView.findViewById(R.id.kaybetmenedeni);
        restart = rootView.findViewById(R.id.restart);
        imageViewZar1 = rootView.findViewById(R.id.imageViewZar1);
        imageViewZar2 = rootView.findViewById(R.id.imageViewZar2);
        zarat = rootView.findViewById(R.id.zarat);

        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.rolling_dice);
        mediaPlayer2 = MediaPlayer.create(requireContext(), R.raw.click);
        mediaPlayer3 = MediaPlayer.create(requireContext(), R.raw.restart);
        sayikapatma = MediaPlayer.create(requireContext(), R.raw.axe);
        kaybetmesesi = MediaPlayer.create(requireContext(), R.raw.losesound);
        kazanmasesi = MediaPlayer.create(requireContext(), R.raw.winsound);

        kalansayilar = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        sayiImageViews = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            int resId = getResources().getIdentifier("sayi" + (i + 1), "id", requireContext().getPackageName());
            ImageView imageView = rootView.findViewById(resId);
            imageView.setTag(i + 1);
            imageView.setOnTouchListener(this);
            sayiImageViews.add(imageView);
        }
        bilgi.setVisibility(View.GONE);
        restart.setVisibility(View.GONE);
        restart.setOnClickListener(v -> restartGame());
        zarat.setOnClickListener(v -> rollDice());
        kazandin.setVisibility(View.GONE);
        kaybettin.setVisibility(View.GONE);
        kaybetmenedeni.setVisibility(View.INVISIBLE);
    }


    private void rollDice() {
        Random random = new Random();
        zar1 = random.nextInt(6) + 1;
        zar2 = random.nextInt(6) + 1;
        zarbakiyesi = zar1 + zar2;
        kalanZarBakiyesi.setVisibility(View.VISIBLE);
        kalanZarBakiyesi.setText("Zar bakiyeniz: " + zarbakiyesi);
        bilgi.setVisibility(View.VISIBLE);
        imageViewZar1.setImageResource(diceImages[zar1 - 1]);
        imageViewZar2.setImageResource(diceImages[zar2 - 1]);

        zarat.setVisibility(View.INVISIBLE);

        if (mediaPlayer != null) {
            mediaPlayer.seekTo(0);
            mediaPlayer.start();
        }

        if (!kontrol(kalansayilar, zarbakiyesi)) {
            endGame(false);
        }
    }

    private boolean kontrol(List<Integer> sayilar, int zarToplami) {
        if (zarToplami == 0) {

            return true;
        }

        return isPossible(sayilar, zarToplami, 0);
    }

    // Kombinasyonları kontrol etmek için yardımcı bir method (backtracking).
    private boolean isPossible(List<Integer> sayilar, int target, int startIndex) {
        if (target == 0) {
            return true;
        }

        for (int i = startIndex; i < sayilar.size(); i++) {

            if (sayilar.get(i) <= target) {
                int number = sayilar.get(i);
                sayilar.remove(i);
                boolean result = isPossible(sayilar, target - number, i);
                sayilar.add(i, number);

                if (result) {
                    return true;
                }
            }
        }
        return false;
    }


    private void endGame(boolean isWin) {
        restart.setVisibility(View.VISIBLE);
        kalanZarBakiyesi.setVisibility(View.INVISIBLE);
        bilgi.setVisibility(View.GONE);
        zarat.setVisibility(View.INVISIBLE);

        if (isWin) {
            kazanmasesi.seekTo(0);
            kazanmasesi.start();
            Glide.with(requireContext()).asGif().load(R.drawable.win2).into(gifImageView);
            kazandin.setVisibility(View.VISIBLE);
        } else {
            kaybetmesesi.seekTo(0);
            kaybetmesesi.start();
            kaybettin.setVisibility(View.VISIBLE);
            kaybetmenedeni.setVisibility(View.VISIBLE);
        }

        saveGameScore();
        zarat.setVisibility(View.GONE);
    }

    private void restartGame() {
        if (mediaPlayer3 != null) {
            mediaPlayer3.seekTo(0);
            mediaPlayer3.start();
        }
        restart.setVisibility(View.GONE);
        kazandin.setVisibility(View.GONE);
        kaybettin.setVisibility(View.GONE);
        kaybetmenedeni.setVisibility(View.INVISIBLE);

        zarat.setVisibility(View.VISIBLE);


        kalansayilar = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        for (ImageView imageView : sayiImageViews) {
            imageView.setVisibility(View.VISIBLE);
        }
        skor = 0;
        updateScoreDisplay();
        zarbakiyesi = 0;
        kalanZarBakiyesi.setText("Lütfen zar atın");
    }

    private void updateScoreDisplay() {
        int kalansayilartoplami = 0;
        for (int num : kalansayilar) {
            kalansayilartoplami += num;
        }
        skor = (int) (100 - (kalansayilartoplami * 100.0 / 55.0));
        skorugoster.setText("Skor: " + skor);
    }

    private void saveGameScore() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String userId = auth.getCurrentUser().getUid();
            db.collection("users")
                    .document(userId)
                    .collection("scores")
                    .add(new Score(skor))
                    .addOnSuccessListener(doc -> showToast("Skor kaydedildi"))
                    .addOnFailureListener(e -> showToast("Skor kaydedilemedi: " + e.getMessage()));
        } else {
            showToast("Kullanıcı giriş yapmadı!");
        }
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int tappedNumber = (int) v.getTag();
            if (kalansayilar.contains(tappedNumber) && zarbakiyesi>=Integer.valueOf(tappedNumber)) {
                if (sayikapatma != null) {
                    sayikapatma.seekTo(0);
                    sayikapatma.start(); }
                kalansayilar.remove(Integer.valueOf(tappedNumber));
                zarbakiyesi -= tappedNumber;

                if (!kontrol(kalansayilar, zarbakiyesi)) {
                    endGame(false);
                }
                if(kalansayilar.size()==0){
                    endGame(true);
                }
                v.setVisibility(View.GONE);
                if(zarbakiyesi==0)
                {kalanZarBakiyesi.setText("Lütfen zar Atın");}
                else{
                kalanZarBakiyesi.setText("Kalan zar bakiyeniz: " + zarbakiyesi);}
                updateScoreDisplay();

                if (zarbakiyesi == 0){
                    zarat.setVisibility(View.VISIBLE);
                }
            }
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) mediaPlayer.release();
        if (mediaPlayer2 != null) mediaPlayer2.release();
        if (mediaPlayer3 != null) mediaPlayer3.release();
        if (kazanmasesi != null) kazanmasesi.release();
        if (kaybetmesesi != null) kaybetmesesi.release();
    }

    public static class Score {
        private int score;

        public Score(int score) {
            this.score = score;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }
    }
}
