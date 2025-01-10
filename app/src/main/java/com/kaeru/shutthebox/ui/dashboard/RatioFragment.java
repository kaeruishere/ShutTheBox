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

public class RatioFragment extends Fragment {

    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private RatioAdapter adapter;
    private List<Ratio> ratioList;

    public RatioFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_ratio, container, false);

        db = FirebaseFirestore.getInstance();
        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ratioList = new ArrayList<>();
        adapter = new RatioAdapter(ratioList); // Oranları gösterecek adapter
        recyclerView.setAdapter(adapter);

        // Firebase'den ratio verilerini al
        getRatioData();

        return rootView;
    }

    private void getRatioData() {
        db.collection("users") // users koleksiyonunu alıyoruz
                .get() // tüm kullanıcı belgelerini alıyoruz
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (var userDocument : task.getResult()) {
                            String userId = userDocument.getId(); // Kullanıcı ID'sini alıyoruz

                            // Kullanıcı adı verisini alıyoruz
                            String userName = userDocument.getString("username"); // Kullanıcı adını alıyoruz

                            // Şimdi her bir kullanıcı için puan ve oyun sayısını alıyoruz
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
                                            // Oranı hesaplıyoruz: totalpuan / oynamasayısı
                                            if (gameCount > 0) {
                                                double ratio = (double) totalScore / gameCount;

                                                // Oranı bir basamağa yuvarlıyoruz
                                                String formattedRatio = String.format("%.1f", ratio);

                                                // Ratio nesnesi oluşturup listeye ekliyoruz
                                                Ratio ratioObj = new Ratio(userId, userName, formattedRatio);
                                                ratioList.add(ratioObj);
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
