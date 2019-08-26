package com.example.ifestexplore;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ifestexplore.fragments.Bookmarks;
import com.example.ifestexplore.fragments.CreatePosts;
import com.example.ifestexplore.fragments.FragmentContainer;
import com.example.ifestexplore.fragments.MyPosts;
import com.example.ifestexplore.fragments.ReceivedPosts;
import com.example.ifestexplore.models.Ad;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationMenu;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BeaconTransmitter;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import javax.annotation.Nullable;

public class Home extends AppCompatActivity implements BeaconConsumer, RangeNotifier, BottomNavigationView.OnNavigationItemSelectedListener, FragmentContainer.OnFragmentInteractionListener, ReceivedPosts.OnFragmentInteractionListener, MyPosts.OnFragmentInteractionListener, CreatePosts.OnFragmentInteractionListener, Bookmarks.OnFragmentInteractionListener {

    private FirebaseAuth mAuth;
    private ImageView iv_userPhoto;
    private TextView tv_userName;
    FirebaseUser user;
    FirebaseFirestore db;
    public static String masterUUID;
    public static String instanceID;
    private static final String TAG = "demo";
    BluetoothManager btManager;
    BluetoothAdapter btAdapter;
    BluetoothLeScanner btLeScanner;
    BottomNavigationView navigationView;
    private ArrayList<Ad> myAdArrayList = new ArrayList<>();
    private ArrayList<Ad> othersAdArrayList = new ArrayList<>();

    BeaconManager beaconManager;


    CollectionReference adsReference;
    //Permission flags...
    private final static int REQUEST_ENABLE_BT = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    @Override
    protected void onResume() {
        super.onResume();
        beaconManager = BeaconManager.getInstanceForApplication(this.getApplicationContext());
        // Detect the main Eddystone-UID frame:
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));
        // Detect the telemetry Eddystone-TLM frame:
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_TLM_LAYOUT));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_URL_LAYOUT));
        beaconManager.bind(this);
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
                FirebaseAuth.getInstance().signOut();
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

//        ___________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________
//        Navigation menus and fragments...
        loadFragment(new ReceivedPosts());

        navigationView = findViewById(R.id.home_nav);
        navigationView.setOnNavigationItemSelectedListener(this);
        navigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
//        ___________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________

        iv_userPhoto = findViewById(R.id.iv_user_photo);
        tv_userName = findViewById(R.id.tv_userName);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        setupBluetooth();
        setImageAndName(user);
        
        db = FirebaseFirestore.getInstance();

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
                                tempAds.add(new Ad(ad.getData()));
                            }
                        }
                        Log.d(TAG, "REFRESHED LIST!!!"+ tempAds.toString());

                        myAdArrayList.clear();
                        myAdArrayList.addAll(tempAds);
                    }
                });
//______________________________________________________________________________________________________________________________________
//        Fetching Others' ads...
        db.collection("adsRepo")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if(e!=null){
                            Log.e(TAG, "Failed reloading Data: ", e);
                            return;
                        }
                        ArrayList<Ad> tempAds = new ArrayList<>();
                        for (QueryDocumentSnapshot ad: queryDocumentSnapshots){
                            if (ad!=null && String.valueOf(ad.get("creator"))!= user.getEmail()){
                                tempAds.add(new Ad(ad.getData()));
                            }
                        }
                        Log.d(TAG, "REFRESHED LIST!!!"+ tempAds.toString());

                        othersAdArrayList.clear();
                        othersAdArrayList.addAll(tempAds);
                    }
                });
//______________________________________________________________________________________________________________________________________


    }

    private void setImageAndName(FirebaseUser user) {
        String name = user.getDisplayName();
        Uri url = user.getPhotoUrl();
        Log.d(TAG, "setImageAndName: "+url);

        tv_userName.setText(name);
//        iv_userPhoto.setImageURI(url);

        Picasso.get().load(url).into(iv_userPhoto);
    }

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

    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> collection, Region region) {
        for (Beacon beacon: collection) {
            Identifier namespaceId = beacon.getId1();
            Identifier instanceId = beacon.getId2();

            if (String.valueOf(masterUUID).equals(String.valueOf(namespaceId)) && beacon.getServiceUuid() == 0xfeaa && beacon.getBeaconTypeCode() == 0x00 ) {
                // This is a Eddystone-UID frame

                db.collection("mapIDtoemail").document(String.valueOf(instanceId)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            String emailRec = task.getResult().getString("email");
                            Log.d(TAG, "FOUND Instance for: " + emailRec);
                            db.collection("usersData").document(emailRec).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    DocumentSnapshot documentSnapshot = task.getResult();
                                    ArrayList<Ad> ads = new ArrayList();
                                    ArrayList adsGot = (ArrayList) documentSnapshot.get("ads");

                                    for (Object ad : adsGot) {
                                        HashMap<String, String> adHM = (HashMap<String, String>) ad;
//                                        ads.add(new Ad(adHM.get("comment"),adHM.get("serial_no")));

                                    }
                                }
                            });
                        }

                    }
                });

                Log.d(TAG, "I see a beacon transmitting namespace id: " + namespaceId +
                        " and instance id: " + instanceId +
                        " approximately " + beacon.getDistance() + " meters away." + ", " + beacon.getExtraDataFields().toString());
//                __________________________________________________________________________________________________________


//                __________________________________________________________________________________________________________
            }

        }

    }

    void setupBluetooth(){
        btManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();
        btLeScanner = btAdapter.getBluetoothLeScanner();

        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));
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
    public ArrayList<Ad> getOtherAdsArrayList() {
        return othersAdArrayList;
    }

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
        navigationView.getMenu().getItem(0).setChecked(true);
    }
}
