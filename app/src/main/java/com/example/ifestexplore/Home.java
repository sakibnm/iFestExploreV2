package com.example.ifestexplore;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ifestexplore.fragments.Bookmarks;
import com.example.ifestexplore.fragments.CreatePosts;
import com.example.ifestexplore.fragments.FragmentContainer;
import com.example.ifestexplore.fragments.MyPosts;
import com.example.ifestexplore.fragments.ReceivedPosts;
import com.example.ifestexplore.models.Ad;
import com.example.ifestexplore.models.NotificationBundle;
import com.example.ifestexplore.utils.SharedPrefHashMap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BeaconTransmitter;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.Nullable;

public class Home extends AppCompatActivity implements BeaconConsumer, RangeNotifier, BottomNavigationView.OnNavigationItemSelectedListener, FragmentContainer.OnFragmentInteractionListener, ReceivedPosts.OnFragmentInteractionListener, MyPosts.OnFragmentInteractionListener, CreatePosts.OnFragmentInteractionListener, Bookmarks.OnFragmentInteractionListener {

    private static final String TAG2 = "ble";
    private static final String CHANNEL_ID = "NotificationsChannel";
    private static final String GROUP_KEY_REVIEWS = "ReviewsKey";
    private static final String KEY_SAVE_ADS_RECEIVED = "saveTOSP";
    private static final String KEY_NOTIF_PREF = "saveNOTIF";
    private static final String NOTIF_TAG = "savedNotifBundle";
    private static final int NOTIFY_ID = 0x00023;
    private static final String GROUP_NOTIF_KEY = "groupNotif";
    private FirebaseAuth mAuth;
    private ImageView iv_userPhoto;
    private TextView tv_userName;
    private CardView logoutCard;
    FirebaseUser user;
    FirebaseFirestore db;
    public static String masterUUID;
    public static String instanceID;
    private static final String TAG = "demo";
    BluetoothManager btManager;
    BluetoothAdapter btAdapter;
    BluetoothLeScanner btLeScanner;
    public static BottomNavigationView navigationView;
    private ArrayList<Ad> myAdArrayList = new ArrayList<>();
    private ArrayList<Ad> myFavAdArrayList = new ArrayList<>();
    private ArrayList<Ad> othersAdArrayList = new ArrayList<>();
    private HashMap<String, Integer> adMap;
    private HashMap<String, Boolean> receivedAdMap;
    private SharedPrefHashMap sharedPrefHashMap;

    private SharedPreferences notifBundlePrefs;


    BeaconManager beaconManager;


    CollectionReference adsReference;
    //Permission flags...
    private final static int REQUEST_ENABLE_BT = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    @Override
    protected void onResume() {
        super.onResume();
        beaconManager = BeaconManager.getInstanceForApplication(this.getApplicationContext());
//        beaconManager.setEnableScheduledScanJobs(true);
        // Detect the main Eddystone-UID frame:
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));
        // Detect the telemetry Eddystone-TLM frame:
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_TLM_LAYOUT));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_URL_LAYOUT));

        beaconManager.bind(this);

//        Flushing notifications....
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancelAll();
        notifBundlePrefs = this.getSharedPreferences(KEY_NOTIF_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = notifBundlePrefs.edit();
        editor.remove(NOTIF_TAG);
        editor.apply();

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_xml, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_logout:
                mAuth.signOut();
                beaconManager.unbind(Home.this );
                sharedPrefHashMap.saveHashMap(new HashMap<String, Boolean>());
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#042529")));

        db = FirebaseFirestore.getInstance();

        notifBundlePrefs = this.getSharedPreferences(KEY_NOTIF_PREF, Context.MODE_PRIVATE);

        adMap= new HashMap<>();
        receivedAdMap = new HashMap<>();
        sharedPrefHashMap = new SharedPrefHashMap(getApplicationContext(), KEY_SAVE_ADS_RECEIVED);
        if (sharedPrefHashMap.getHashMap()!=null)receivedAdMap = sharedPrefHashMap.getHashMap();

//        ___________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________
//        setTimerForBackupAds........
        getBackUpAds();

//        othersAdArrayList = receivedAdMap;
//        ___________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________
//        LOADING Prefetched ads...
        getPrefetchedAds(receivedAdMap);
//        Log.d(TAG, "onCreate: SAVED RECIVED ADS COUNT: "+othersAdArrayList.size()+" "+othersAdArrayList.toString());
//        ___________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________
//        ___________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________
//        Navigation menus and fragments...
        loadFragment(new ReceivedPosts());
//        ReceivedPosts.getUpdatedList();
        navigationView = findViewById(R.id.home_nav);
        navigationView.setOnNavigationItemSelectedListener(this);
        navigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
//        ___________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________

        iv_userPhoto = findViewById(R.id.iv_user_photo);
        tv_userName = findViewById(R.id.tv_userName);
        logoutCard = findViewById(R.id.card_logout);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        setupBluetooth();
        setImageAndName(user);

        logoutCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                beaconManager.unbind(Home.this );
                sharedPrefHashMap.saveHashMap(new HashMap<String, Boolean>());
                finish();
            }
        });
        


        //Getting the masterKey....
        db.collection("uABmaster").document("master").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    String masterKey = (String) task.getResult().get("masterKey");
                    Log.d(TAG, "MasterKey: "+masterKey);
                    Identifier masterID = Identifier.parse(masterKey);
                    masterUUID = masterID.toString();
                    String userEmail = user.getEmail();
                    getUserInstanceIDandTransmit(userEmail);
                }else{
                    Log.d(TAG, "Failed getting masterkey: "+ task.getException());
                }
            }
        });
//______________________________________________________________________________________________________________________________________
//        Fetching MYADS................
        db.collection("adsRepo")
                .whereEqualTo("creator", user.getEmail())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if(e!=null){
                            Log.e(TAG, "Failed reloading Data: ", e);
                            return;
                        }
                        ArrayList<Ad> tempAds = new ArrayList<>();
                        for (QueryDocumentSnapshot ad: queryDocumentSnapshots){
                            if (ad!=null){
                                Ad tads = new Ad(ad.getData());
                                Log.d(TAG, "MYADS ARRAY: "+tads.toString());
                                tempAds.add(new Ad(ad.getData()));
                            }
                        }
//                        Log.d(TAG, "REFRESHED LIST!!!"+ tempAds.toString());



                        myAdArrayList.clear();
                        myAdArrayList.addAll(tempAds);
                        MyPosts.getUpdatedList();
                    }
                });

        //        Fetching MY Favorite ADS................
        CollectionReference collectionReference = db.collection("favoriteAds")
                .document(user.getEmail()).collection("favorites");
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e!=null){
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }
                Log.d(TAG, "FAVORITES: triggered!");
                if (queryDocumentSnapshots!=null && !queryDocumentSnapshots.isEmpty()){
                    ArrayList<Ad> tempAds = new ArrayList<>();
                    for (QueryDocumentSnapshot ad: queryDocumentSnapshots){

                        if(ad!=null){

                            tempAds.add(new Ad(ad.getData()));
                        }

                    }


                    myFavAdArrayList.clear();
                    myFavAdArrayList.addAll(tempAds);
                    Bookmarks.getUpdatedList();
                }else{
                    myFavAdArrayList.clear();
                }
            }
        });
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

////                        Bookmarks.getUpdatedList();
//                    }
//                });


    }

    private void getPrefetchedAds(final HashMap<String, Boolean> currentAdsMap) {
        final ArrayList<Ad> adArrayList = new ArrayList<>();
        final HashMap<String, Boolean> newAdsMap = new HashMap<>();
//        List<String> currentAds = new ArrayList<>(currentAdsMap.keySet());

        db.collection("adsRepo").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot docSnap: queryDocumentSnapshots){
                    Ad ad = new Ad(docSnap.getData());
                    if (currentAdsMap.containsKey(ad.getAdSerialNo())){
                        adArrayList.add(ad);
                        newAdsMap.put(ad.getAdSerialNo(),true);
                    }
                }
                othersAdArrayList = adArrayList;
                Log.d(TAG, "onCreate: SAVED RECIVED ADS COUNT: "+othersAdArrayList.size()+" "+othersAdArrayList.toString());
                sharedPrefHashMap.saveHashMap(newAdsMap);
                loadFragment(new ReceivedPosts());
                ReceivedPosts.getUpdatedList();
            }
        });

    }


    private void setImageAndName(FirebaseUser user) {
        String name = user.getDisplayName();
        Uri url = user.getPhotoUrl();
        Log.d(TAG, "setImageAndName: "+url);

        tv_userName.setText("Hello "+name+"!");
//        iv_userPhoto.setImageURI(url);

        Picasso.get().load(url).into(iv_userPhoto);
    }
//______________________________________________________________________________________________________________________________________

    private void getUserInstanceIDandTransmit(String userEmail) {
        db.collection("users").document(userEmail).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    instanceID = (String) task.getResult().get("instanceID");
                    Log.d(TAG, "INSTANCE ID: "+instanceID);
                    transmitBeacon();
                }else{
                    Log.d(TAG, "Failed getting instanceKey: "+ task.getException());
                }
            }
        });
    }

    public void transmitBeacon(){


        final String instanceUUID = instanceID;
        final Beacon beacon = new Beacon.Builder()
                .setId1(this.masterUUID)
                .setId2(instanceUUID)
                .setManufacturer(0x0118)
                .setTxPower(-59)
                .setDataFields(Arrays.asList(new Long[] {0l}))
                .build();
        int result = BeaconTransmitter.checkTransmissionSupported(getApplicationContext());
        
        if (result == BeaconTransmitter.NOT_SUPPORTED_CANNOT_GET_ADVERTISER){
            Log.e(TAG, "transmitBeacon: Failed Error 4");
        }

        BeaconParser beaconParser = new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT);
        BeaconTransmitter beaconTransmitter = new BeaconTransmitter(getApplicationContext(), beaconParser);
        beaconTransmitter.startAdvertising(beacon, new AdvertiseCallback() {
            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                super.onStartSuccess(settingsInEffect);
                Log.d(TAG, "Advertisement start succeeded with ID: "+instanceUUID+" "+beacon.getBluetoothAddress());
            }

            @Override
            public void onStartFailure(int errorCode) {
                super.onStartFailure(errorCode);
                Log.e(TAG, "Advertisement start failed with code: "+errorCode);
            }
        });
    }
//______________________________________________________________________________________________________________________________________

    @Override
    public void onBeaconServiceConnect() {
        Region region = new Region("all-beacons-region", null, null, null);
        try {
            beaconManager.startRangingBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        beaconManager.addRangeNotifier(this);
    }

//______________________________________________________________________________________________________________________________________

//    Scanning surrounding beacons.............
    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> collection, Region region) {
        for (Beacon beacon: collection) {
            Identifier namespaceId = beacon.getId1();
            Identifier instanceId = beacon.getId2();

            if (String.valueOf(masterUUID).equals(String.valueOf(namespaceId)) && beacon.getServiceUuid() == 0xfeaa && beacon.getBeaconTypeCode() == 0x00 ) {
                // This is a Eddystone-UID frame
                Log.d(TAG, "New Beacon found: " + beacon.getBluetoothName()+", "+beacon.getBluetoothAddress()+", "+beacon.getServiceUuid());
                db.collection("mapIDtoemail").document(String.valueOf(instanceId)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            final String emailRec = task.getResult().getString("email");
                            Log.d(TAG, "FOUND Instance for: " + emailRec);
                            if(!adMap.containsKey(emailRec.trim())){
                                adMap.put(emailRec.trim(),0);
                            }
                            //______________________________________________________________________________________________________________________________________
//        Fetching Others' ads...
//                            final int[] adscount = {0};
                            db.collection("adsRepo")
//                                    .whereEqualTo("creator",emailRec.trim())
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                            int adscount = 0;
                                            if(e!=null){
                                                Log.e(TAG, "Failed reloading Data: ", e);
                                                return;
                                            }
                                            ArrayList<Ad> tempAds = new ArrayList<>();

                                            for (QueryDocumentSnapshot ad: queryDocumentSnapshots){
                                                if (ad.contains("count"))continue; //adscounter...
                                                if (ad.get("creator").equals(emailRec)) {
                                                    if (ad != null && !String.valueOf(ad.get("creator")).equals(user.getEmail())) {
                                                        Ad gotAd = new Ad(ad.getData());

//___________________________  NOTIFICATIONS!!!!!!!___________________________________________________________
                                                        if (!receivedAdMap.containsKey(gotAd.getAdSerialNo())) {
//                                                            Reading current notifications.....
                                                            ArrayList<NotificationBundle> notificationBundle =  new ArrayList<>();
                                                            Gson gson = new Gson();
                                                            String json = notifBundlePrefs.getString(NOTIF_TAG, "");
                                                            Type type = new TypeToken<ArrayList<NotificationBundle>>(){}.getType();
                                                            notificationBundle = gson.fromJson(json, type);

                                                            if (notificationBundle==null)notificationBundle = new ArrayList<>();

                                                            notificationBundle.add(new NotificationBundle(gotAd.getTitle()+" - "+gotAd.getCreatorName()));

//                                                            Saving the list of notifications....
                                                            json = gson.toJson(notificationBundle);
                                                            SharedPreferences.Editor editor = notifBundlePrefs.edit();
                                                            editor.putString(NOTIF_TAG, json);
                                                            editor.commit();

                                                            createNotification(gotAd, notificationBundle);
                                                            showNewReviewSign();
                                                            receivedAdMap.put(gotAd.getAdSerialNo(), true);
                                                            sharedPrefHashMap.saveHashMap(receivedAdMap);

                                                        }
                                                        tempAds.add(0, gotAd);
                                                        adscount++;
                                                    }
                                                }
                                            }

//                                            if (adMap.get(emailRec)!=adscount){
                                                int differenceAdCount = adMap.get(emailRec.trim()) - adscount;
                                                Log.d(TAG, "HASHMAP DIFFERENCE: "+differenceAdCount);
                                                adMap.put(emailRec.trim(), adscount);
                                                ArrayList<Ad> toBeDeletedAds = new ArrayList<>();
                                                for (Ad ad: othersAdArrayList){
                                                    if(ad.getCreatorEmail().equals(emailRec)){
                                                        Log.d(TAG, "OthersArrayList for "+emailRec+ " "+ ad.toString());

                                                        if (!tempAds.contains(ad)){
//                                                            othersAdArrayList.remove(ad);
                                                            toBeDeletedAds.add(ad);
                                                            Log.d(TAG, "Removing from TempArrayList "+ ad.toString());
                                                        }
                                                    }
                                                }
                                                for (Ad ad: toBeDeletedAds){
                                                    othersAdArrayList.remove(ad);
                                                }

                                                Log.d(TAG, "Updated TEMP Ads, size: "+ tempAds.size()+ " "+ tempAds);
                                                for (Ad ad: tempAds){
                                                    Log.d(TAG, "TempArrayList for "+emailRec+ " "+ ad.toString());
                                                    if (!othersAdArrayList.contains(ad)){
                                                        othersAdArrayList.add(ad);
                                                        Log.d(TAG, "ADDING to LIST: "+ad.toString());
                                                    }

                                                }
//                                            }
//                        Log.d(TAG, "REFRESHED LIST!!!"+ othersAdArrayList.toString());


//                                            othersAdArrayList.clear();
//                                            othersAdArrayList.addAll(tempAds);
//                                            loadFragment(new ReceivedPosts());
                                            ReceivedPosts.getUpdatedList();

                                        }
                                    });
//______________________________________________________________________________________________________________________________________


                        }

                    }
                });

////                Log.d(TAG, "I see a beacon transmitting namespace id: " + namespaceId +
//                        " and instance id: " + instanceId +
//                        " approximately " + beacon.getDistance() + " meters away." + ", " + beacon.getExtraDataFields().toString());
//// __________________________________________________________________________________________________________


//__________________________________________________________________________________________________________
            }

        }

    }


//______________________________________________________________________________________________________________________________________


    void setupBluetooth(){
        btManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();
        btLeScanner = btAdapter.getBluetoothLeScanner();

        beaconManager = BeaconManager.getInstanceForApplication(this);
//        beaconManager.setEnableScheduledScanJobs(true);
        beaconManager.setBackgroundMode(true);

//        ANDROID 8+.....
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            Notification.Builder builder = new Notification.Builder(this);
            builder.setSmallIcon(R.drawable.new_notif);
            builder.setContentTitle("Scanning for Beacons");
            Intent intent = new Intent();
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
            );
            builder.setContentIntent(pendingIntent);

            NotificationChannel channel = new NotificationChannel("My Notification Channel ID",
                    "My Notification Name", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("My Notification Channel Description");
            NotificationManager notificationManager = (NotificationManager) getSystemService(
                    Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
            builder.setChannelId(channel.getId());

            beaconManager.enableForegroundServiceScanning(builder.build(), 456);
            // For the above foreground scanning service to be useful, you need to disable
            // JobScheduler-based scans (used on Android 8+) and set a fast background scan
            // cycle that would otherwise be disallowed by the operating system.
            //
            beaconManager.setEnableScheduledScanJobs(false);
        }


//        beaconManager.setBackgroundBetweenScanPeriod(500);
//        beaconManager.setBackgroundScanPeriod(1000);beaconManager.setBackgroundBetweenScanPeriod(500);
//        beaconManager.setBackgroundScanPeriod(1000);
        beaconManager.getBeaconParsers().add(new BeaconParser()
                .setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));
// Detect the telemetry (TLM) frame:
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(BeaconParser.EDDYSTONE_TLM_LAYOUT));
// Detect the URL frame:
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(BeaconParser.EDDYSTONE_URL_LAYOUT));
        //Detect iBeacons...
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.bind(this);



        //Bluetooth permission check...
        if (btAdapter != null && !btAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }

        // Make sure we have access coarse location enabled, if not, prompt the user to enable it
        if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("This app needs location access");
            builder.setMessage("Please grant location access so this app can detect peripherals.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                }
            });
            builder.show();
        }
    }

//______________________________________________________________________________________________________________________________________

    //FRAGMENTS and NAVIGATION...........
    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment = null;

        switch(menuItem.getItemId()){
            case R.id.navigation_received_posts:
                fragment = new ReceivedPosts();
                break;
            case R.id.navigation_new_post:
                fragment = new CreatePosts();
                break;
            case R.id.nav_my_posts:
                fragment = new MyPosts();
                break;
            case R.id.nav_bookmarks:
                fragment = new Bookmarks();
                break;
        }

        return loadFragment(fragment);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public ArrayList<Ad> getFavAdsArrayList() {

        Collections.sort(myFavAdArrayList, new Comparator<Ad>() {
            @Override
            public int compare(Ad t1, Ad t2) {
                return Integer.parseInt(t2.getAdSerialNo().trim()) - Integer.parseInt(t1.getAdSerialNo().trim());
            }
        });
        return myFavAdArrayList;
    }

    @Override
    public ArrayList<Ad> getOtherAdsArrayList() {
        Collections.sort(othersAdArrayList, new Comparator<Ad>() {
            @Override
            public int compare(Ad t1, Ad t2) {
                return Integer.parseInt(t2.getAdSerialNo().trim()) - Integer.parseInt(t1.getAdSerialNo().trim());
            }
        });

        return othersAdArrayList;
    }

//    @Override
//    public void refreshList() {
//        loadFragment(new ReceivedPosts());
//    }

    @Override
    public ArrayList<Ad> getMyAdsArrayList() {
        return myAdArrayList;
    }

    @Override
    public void onClearAllPressedFromCreatePosts() {
        loadFragment(new CreatePosts());
    }

    @Override
    public void onCreatePressedFromCreatePosts() {

        loadFragment(new ReceivedPosts());

//        navigationView.setSelectedItemId(navigationView.getMenu().getItem(0).getItemId());

    }

//    NOTIFICATIONS.........
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void createNotification(final Ad ad, ArrayList<NotificationBundle> notificationBun){
        createNotificationChannel();
        Intent intent = new Intent(getApplicationContext(), Home.class);
        intent.setAction("REFRESH_HOME");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(intent);
        final PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
//        final PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);


        if (notificationBun.size()<2){
            new AsyncTask<String, Void, Bitmap>() {
                @Override
                protected Bitmap doInBackground(String... strings) {
                    Bitmap notifBitmap = null;
                    try {
                        URL bitmapURL = new URL(strings[0]);
                        notifBitmap = BitmapFactory.decodeStream(bitmapURL.openConnection().getInputStream());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return notifBitmap;
                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    super.onPostExecute(bitmap);
                    NotificationCompat.Builder newReviewNotificationBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                            .setSmallIcon(R.drawable.new_notif)
                            .setContentTitle(ad.getCreatorName()+" posted around you!")
                            .setContentText(ad.getTitle())
                            .setGroup(GROUP_KEY_REVIEWS)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setTimeoutAfter(50000)
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true);
                    if(bitmap==null)newReviewNotificationBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.content_24dp));
                    else newReviewNotificationBuilder.setLargeIcon(bitmap);

                    Notification notification = newReviewNotificationBuilder.build();

                    NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
                    notificationManagerCompat.notify(NOTIFY_ID, notification);
                }
            }.execute(ad.getItemPhotoURL());
        }else{
            Notification summaryNotification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                    .setSmallIcon(R.drawable.ble_notif)
                    .setContentTitle("iFestExplore: New Posts Around!!")
                    .setContentText(notificationBun.size()+" posts received!")
                    .setStyle(new NotificationCompat.InboxStyle()
                            .addLine(notificationBun.get(notificationBun.size()-1).getmText())
                            .addLine(notificationBun.get(notificationBun.size()-2).getmText())
                    )

                    .setGroup(GROUP_NOTIF_KEY)
                    .setGroupSummary(true)
                    .build();
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
            notificationManagerCompat.notify(NOTIFY_ID, summaryNotification);
        }



    }
//    _____________________________________________________________________________________________________________________________________________
//    SHOW NEW AD
    private void showNewReviewSign() {
        findViewById(R.id.cv_new_Review).setVisibility(View.VISIBLE);
        Runnable mRunnable;
        Handler mHandler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.cv_new_Review).setVisibility(View.GONE);
            }
        };

        mHandler.postDelayed(mRunnable, 20*1000);
    }


    private void getBackUpAds() {
        Runnable mRunnable;
        final Handler mHandler = new Handler();
        Timer timer = new Timer();
        TimerTask getBackUpAsyncScheduler = new TimerTask() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        final ArrayList<Ad> arrayLists;
                        arrayLists = othersAdArrayList;
                        final Ad[] ad = {new Ad()};
                        db.collection("adsRepo").get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    Ad tempAd;
                                    for (QueryDocumentSnapshot queryDocumentSnapshot: queryDocumentSnapshots){
                                        if (queryDocumentSnapshot.contains("count"))continue;
//                                        if (queryDocumentSnapshot.contains(user.getEmail()))continue;
                                        tempAd = new Ad(queryDocumentSnapshot.getData());
                                        if (tempAd.getCreatorEmail().equals(user.getEmail())){
                                            Log.d(TAG, "MY AD FOUND!!!!!");
                                            continue;
                                        }
                                        Log.d(TAG, "THREAD BACKUP In Snap: "+tempAd.toString());

                                        if (arrayLists.size()==0) {
                                            Log.d(TAG, "THREAD BACKUP others size=0: "+tempAd.toString());
                                            ad[0] = tempAd;
                                            othersAdArrayList.add(ad[0]);
                                            ReceivedPosts.getUpdatedList();
                                            if (!receivedAdMap.containsKey(ad[0].getAdSerialNo())) {
//                                                Reading current notifications.....
                                                ArrayList<NotificationBundle> notificationBundle = new ArrayList<NotificationBundle>();
                                                Gson gson = new Gson();
                                                String json = notifBundlePrefs.getString(NOTIF_TAG, "");
                                                Type type = new TypeToken<ArrayList<NotificationBundle>>(){}.getType();
                                                notificationBundle = gson.fromJson(json, type);

                                                if (notificationBundle==null)notificationBundle = new ArrayList<>();

                                                notificationBundle.add(new NotificationBundle(ad[0].getTitle()+" - "+ad[0].getCreatorName()));

//                                                 Saving the list of notifications....
                                                json = gson.toJson(notificationBundle);
                                                SharedPreferences.Editor editor = notifBundlePrefs.edit();
                                                editor.putString(NOTIF_TAG, json);
                                                editor.commit();
                                                createNotification(ad[0], notificationBundle);
                                                showNewReviewSign();
                                                receivedAdMap.put(ad[0].getAdSerialNo(), true);
                                                sharedPrefHashMap.saveHashMap(receivedAdMap);
                                                String adEmail = ad[0].getCreatorEmail();
                                                if (adMap.containsKey(adEmail)) {
                                                    int count = adMap.get(adEmail);
                                                    adMap.put(adEmail, count + 1);
                                                } else {
                                                    adMap.put(adEmail, 1);
                                                }

                                            }
                                            break;
                                        }
                                        Boolean containsAd = false;
                                        for (int i=0;i<arrayLists.size();i++){

                                            if (tempAd.equals(arrayLists.get(i))){
                                                containsAd = true;
                                            }
                                        }
                                        if (!containsAd){
                                            ad[0] = tempAd;
                                            othersAdArrayList.add(ad[0]);
                                            ReceivedPosts.getUpdatedList();
                                            if (!receivedAdMap.containsKey(ad[0].getAdSerialNo())) {
//                                                Reading current notifications.....
                                                ArrayList<NotificationBundle> notificationBundle = new ArrayList<>();
                                                Gson gson = new Gson();
                                                String json = notifBundlePrefs.getString(NOTIF_TAG, "");
                                                Type type = new TypeToken<ArrayList<NotificationBundle>>(){}.getType();
                                                notificationBundle = gson.fromJson(json, type);

                                                if (notificationBundle==null)notificationBundle = new ArrayList<>();

                                                notificationBundle.add(new NotificationBundle(ad[0].getTitle()+" - "+ad[0].getCreatorName()));

//                                                Saving the list of notifications....
                                                json = gson.toJson(notificationBundle);
                                                SharedPreferences.Editor editor = notifBundlePrefs.edit();
                                                editor.putString(NOTIF_TAG, json);
                                                editor.commit();
                                                createNotification(ad[0], notificationBundle);
                                                showNewReviewSign();
                                                receivedAdMap.put(ad[0].getAdSerialNo(), true);
                                                sharedPrefHashMap.saveHashMap(receivedAdMap);
                                                String adEmail = ad[0].getCreatorEmail();
                                                if (adMap.containsKey(adEmail)) {
                                                    int count = adMap.get(adEmail);
                                                    adMap.put(adEmail, count + 1);
                                                } else {
                                                    adMap.put(adEmail, 1);
                                                }

                                            }
                                            break;
                                        }
                                    }
                                }
                            });
                    }
                });
            }
        };
        timer.schedule(getBackUpAsyncScheduler, 60000, 1*60*1000);
//        TODO: change the timer from 1 minute to 23...
    }

    @Override
    public void onBackPressed() {

    }

//    private class PerformGetBackUpAds extends AsyncTask<Void, Void, Ad>{
//        @Override
//        protected Ad doInBackground(Void... voids) {
//            final ArrayList<Ad> arrayLists;
//            arrayLists = othersAdArrayList;
//            final Ad[] ad = {new Ad()};
//            db.collection("adsRepo").get()
//                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                        @Override
//                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                            Ad tempAd;
//                            for (QueryDocumentSnapshot queryDocumentSnapshot: queryDocumentSnapshots){
//                                if (queryDocumentSnapshot.contains("count"))continue;
//                                tempAd = new Ad(queryDocumentSnapshot.getData());
//                                Log.d(TAG, "THREAD BACKUP In Snap: "+tempAd.toString());
////                            Log.d(TAG, "THREAD BACKUP OTHERS: "+tempAd.toString());
//                                if (arrayLists.size()==0) {
//                                    Log.d(TAG, "THREAD BACKUP others size=0: "+tempAd.toString());
//                                    ad[0] = tempAd;
//                                    break;
//                                }
//                                for (int i=0;i<arrayLists.size();i++){
//                                    if (!tempAd.equals(arrayLists.get(i))){
//                                        ad[0] = tempAd;
//                                        break;
//                                    }
//                                }
//                            }
//                        }
//                    });
//            Log.d(TAG, "THREAD BACKUP OTHERS: "+ad[0].toString());
//            if (ad[0].getAdSerialNo()==null){
//                return null;
//            }else
//                return ad[0];
//        }
//
//        @Override
//        protected void onPostExecute(Ad ad) {
//            super.onPostExecute(ad);
//            if (ad != null) {
//                othersAdArrayList.add(ad);
//                ReceivedPosts.getUpdatedList();
//                if (!receivedAdMap.containsKey(ad.getAdSerialNo())) {
//                    createNotification(ad);
//                    showNewReviewSign();
//                    receivedAdMap.put(ad.getAdSerialNo(), true);
//                    sharedPrefHashMap.saveHashMap(receivedAdMap);
//                    String adEmail = ad.getCreatorEmail();
//                    if (adMap.containsKey(adEmail)) {
//                        int count = adMap.get(adEmail);
//                        adMap.put(adEmail, count + 1);
//                    } else {
//                        adMap.put(adEmail, 1);
//                    }
//
//                }
//
//            }
//        }
//    }
}
