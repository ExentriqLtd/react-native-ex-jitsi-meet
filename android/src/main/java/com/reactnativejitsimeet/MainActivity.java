package com.reactnativejitsimeet;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.jitsi.meet.sdk.JitsiMeetView;

public class MainActivity extends AppCompatActivity {
    private JitsiMeetView jitsiMeetView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inizializza la vista Jitsi Meet
        jitsiMeetView = new ExJitsiMeetView(this);

        // Configura le opzioni della conferenza
        JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                .setRoom("https://meet.jit.si/your-room-name")
                .build();

        // Carica le opzioni della conferenza nella vista Jitsi Meet
        jitsiMeetView.join(options);

        // Aggiungi la vista Jitsi Meet alla tua gerarchia di visualizzazione
        setContentView(jitsiMeetView);
    }

    @Override
    protected void onDestroy() {
        // Esegui la pulizia quando l'attività viene distrutta
        jitsiMeetView.dispose();
        jitsiMeetView = null;

        super.onDestroy();
    }
}
