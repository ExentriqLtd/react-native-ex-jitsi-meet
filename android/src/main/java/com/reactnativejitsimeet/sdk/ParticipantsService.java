// 
// These are decompiled files from the jitsi meet sdk modify these files only if it is extremely necessary.
// 

package org.jitsi.meet.sdk;

import org.jitsi.meet.sdk.log.JitsiMeetLogger;
import java.util.ArrayList;
import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;
import java.util.List;
import android.content.Intent;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;
import java.util.UUID;
import android.content.IntentFilter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import java.util.HashMap;
import android.content.Context;
import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.Map;
import android.content.BroadcastReceiver;

public class ParticipantsService extends BroadcastReceiver
{
    private static final String TAG;
    private static final String REQUEST_ID = "requestId";
    private final Map<String, WeakReference<ParticipantsInfoCallback>> participantsInfoCallbackMap;
    private static ParticipantsService instance;
    
    @Nullable
    public static ParticipantsService getInstance() {
        return ParticipantsService.instance;
    }
    
    private ParticipantsService(final Context context) {
        this.participantsInfoCallbackMap = new HashMap<String, WeakReference<ParticipantsInfoCallback>>();
        final LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastEvent.Type.PARTICIPANTS_INFO_RETRIEVED.getAction());
        localBroadcastManager.registerReceiver((BroadcastReceiver)this, intentFilter);
    }
    
    static void init(final Context context) {
        ParticipantsService.instance = new ParticipantsService(context);
    }
    
    public void retrieveParticipantsInfo(final ParticipantsInfoCallback participantsInfoCallback) {
        final String callbackKey = UUID.randomUUID().toString();
        this.participantsInfoCallbackMap.put(callbackKey, new WeakReference<ParticipantsInfoCallback>(participantsInfoCallback));
        final String actionName = BroadcastAction.Type.RETRIEVE_PARTICIPANTS_INFO.getAction();
        final WritableMap data = Arguments.createMap();
        data.putString("requestId", callbackKey);
        ReactInstanceManagerHolder.emitEvent(actionName, data);
    }
    
    public void onReceive(final Context context, final Intent intent) {
        final BroadcastEvent event = new BroadcastEvent(intent);
        switch (event.getType()) {
            case PARTICIPANTS_INFO_RETRIEVED: {
                try {
                    final List<ParticipantInfo> participantInfoList = (List<ParticipantInfo>)new Gson().fromJson(event.getData().get("participantsInfo").toString(), new TypeToken<ArrayList<ParticipantInfo>>() {}.getType());
                    final ParticipantsInfoCallback participantsInfoCallback = this.participantsInfoCallbackMap.get(event.getData().get("requestId").toString()).get();
                    if (participantsInfoCallback != null) {
                        participantsInfoCallback.onReceived(participantInfoList);
                        this.participantsInfoCallbackMap.remove(participantsInfoCallback);
                    }
                }
                catch (Exception e) {
                    JitsiMeetLogger.w(ParticipantsService.TAG + "error parsing participantsList", e);
                }
                break;
            }
        }
    }
    
    static {
        TAG = ParticipantsService.class.getSimpleName();
    }
    
    public interface ParticipantsInfoCallback
    {
        void onReceived(final List<ParticipantInfo> p0);
    }
}
