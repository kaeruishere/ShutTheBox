package com.kaeru.shutthebox.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kaeru.shutthebox.R;
import java.util.ArrayList;
import java.util.List;

public class RankingFragment extends Fragment {

    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private RankingAdapter adapter;
    private List<Ranking> rankingList; // Skorlar ve kullanıcı adlarını tutacak liste

    public RankingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_ranking, container, false);

        db = FirebaseFirestore.getInstance();
        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        rankingList = new ArrayList<>();
        adapter = new RankingAdapter(rankingList); // Ranking verilerini gösterecek adapter
        recyclerView.setAdapter(adapter);

        // Firebase'den sıralama verilerini al
        getRankingData();

        return rootView;
    }

    private void getRankingData() {
        db.collection("users") // users koleksiyonunu alıyoruz
                .get() // tüm kullanıcı belgelerini alıyoruz
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (var userDocument : task.getResult()) {
                            String userId = userDocument.getId(); // Kullanıcı ID'sini alıyoruz
                            String userName = userDocument.getString("username"); // Kullanıcı adı verisini alıyoruz

                            // Şimdi her bir kullanıcı için puan verilerini alıyoruz
                            db.collection("users")
                                    .document(userId)
                                    .collection("scores")
                                    .get()
                                    .addOnCompleteListener(scoreTask -> {
                                        if (scoreTask.isSuccessful()) {
                                            int totalScore = 0;
                                            int gameCount = 0;

                                            for (var scoreDocument : scoreTask.getResult()) {
                                                // Skoru alıyoruz
                                                Score score = scoreDocument.toObject(Score.class);
                                                totalScore += score.getScore(); // Toplam skoru hesaplıyoruz
                                                gameCount++; // Oynanan oyun sayısını artırıyoruz
                                            }

                                            // Oranı hesaplıyoruz: totalpuan / oynamasayısı
                                            if (gameCount > 0) {
                                                double ratio = (double) totalScore / gameCount;

                                                // Ranking nesnesi oluşturup listeye ekliyoruz
                                                Ranking rankingObj = new Ranking(userId, userName, totalScore, ratio);
                                                rankingList.add(rankingObj);
                                            }
                                        }

                                        // Veriler alındığında RecyclerView'i güncelliyoruz
                                        adapter.notifyDataSetChanged();
                                    });
                        }
                    }
                });
    }

}
