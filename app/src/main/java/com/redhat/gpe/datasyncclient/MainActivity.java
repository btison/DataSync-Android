package com.redhat.gpe.datasyncclient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.feedhenry.sdk.FH;
import com.feedhenry.sdk.FHActCallback;
import com.feedhenry.sdk.FHResponse;
import com.feedhenry.sdk.sync.FHSyncClient;
import com.feedhenry.sdk.sync.FHSyncConfig;
import com.feedhenry.sdk.sync.FHSyncListener;
import com.feedhenry.sdk.sync.NotificationMessage;

import org.json.fh.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends Activity {

    private ProgressDialog progressDialog = null;
    private FHSyncClient syncClient = null;
    private ArrayAdapter<String> adapter = null;

    private static final String DATAID = "tasks";
    private static final String TAG = "datasync";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Context that = this;
        FH.setLogLevel(FH.LOG_LEVEL_VERBOSE);
        progressDialog = ProgressDialog.show(this, "Loading", "Please wait...");
        FH.init(this, new FHActCallback() {
            @Override
            public void success(FHResponse fhResponse) {
                progressDialog.dismiss();
                initSync();
                startSync();
            }

            @Override
            public void fail(FHResponse fhResponse) {
                progressDialog.dismiss();
                Util.showMessage(that, "Error", fhResponse.getErrorMessage());
            }
        });

        List<String> values = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this, R.layout.list_item);
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                String item = (String) parent.getItemAtPosition(position);
                Util.showMessage(that, "ListView", "Selected item: " +item);
            }
        });

    }

    private void initSync() {
        syncClient = FHSyncClient.getInstance();
        FHSyncConfig config = new FHSyncConfig();
        config.setNotifySyncStarted(true);
        config.setNotifySyncComplete(true);
        config.setAutoSyncLocalUpdates(true);
        config.setNotifyDeltaReceived(true);

        syncClient.init(this, config, new FHSyncListener() {
            @Override
            public void onSyncStarted(NotificationMessage notificationMessage) {
                Log.d(TAG, "SYNC LOOP STARTED!!");
            }

            @Override
            public void onSyncCompleted(NotificationMessage notificationMessage) {
                Log.d(TAG, "Sync complete: " + notificationMessage);
                JSONObject alldata = syncClient.list(DATAID);
                adapter.clear();
                Iterator<String> it = alldata.keys();
                while(it.hasNext()){
                    String key = it.next();
                    JSONObject data = alldata.getJSONObject(key);
                    JSONObject dataObj = data.getJSONObject("data");
                    String task = dataObj.optString("data", "NO task name");
                    adapter.add(task);
                }
            }

            @Override
            public void onUpdateOffline(NotificationMessage notificationMessage) {

            }

            @Override
            public void onCollisionDetected(NotificationMessage notificationMessage) {

            }

            @Override
            public void onRemoteUpdateFailed(NotificationMessage notificationMessage) {

            }

            @Override
            public void onRemoteUpdateApplied(NotificationMessage notificationMessage) {

            }

            @Override
            public void onLocalUpdateApplied(NotificationMessage notificationMessage) {
                Log.d(TAG, "Local Update Applied: " + notificationMessage);
            }

            @Override
            public void onDeltaReceived(NotificationMessage notificationMessage) {
                Log.d(TAG, "Delta Received: " + notificationMessage);
            }

            @Override
            public void onSyncFailed(NotificationMessage notificationMessage) {
                Log.d(TAG, "Sync failed: " + notificationMessage);
            }

            @Override
            public void onClientStorageFailed(NotificationMessage notificationMessage) {

            }
        });
    }

    private void startSync() {
        //start the sync process
        try {
            syncClient.manage(DATAID, null, new JSONObject());
            syncClient.list(DATAID);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }
}
