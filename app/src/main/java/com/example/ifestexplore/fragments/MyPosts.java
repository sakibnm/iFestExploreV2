package com.example.ifestexplore.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.ifestexplore.R;
import com.example.ifestexplore.controllers.MyAdAdapter;
import com.example.ifestexplore.models.Ad;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nullable;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyPosts.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyPosts#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyPosts extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private FirebaseFirestore db;

    private static final String TAG = "demo";
    private MyAdAdapter myAdAdapter;
    private ArrayList<Ad> adArrayList = new ArrayList<>();
    private RecyclerView rv_MyPosts;
    private View view;
    CollectionReference adsReference;
    SwipeRefreshLayout swipeRefreshLayout;

    public MyPosts() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyPosts.
     */
    // TODO: Rename and change types and number of parameters
    public static MyPosts newInstance(String param1, String param2) {
        MyPosts fragment = new MyPosts();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            adsReference = db.collection("adsRepo");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_my_posts, container, false);
        swipeRefreshLayout = view.findViewById(R.id.swipe_myposts);
        swipeRefreshLayout.setOnRefreshListener(MyPosts.this);
        rv_MyPosts = view.findViewById(R.id.rv_my_posts);
        rv_MyPosts.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rv_MyPosts.setLayoutManager(linearLayoutManager);
        myAdAdapter = new MyAdAdapter(adArrayList, getContext());
        rv_MyPosts.setAdapter(myAdAdapter);
//       Fetching my posts...
//        ____________________________________________________________________________________
        adArrayList = mListener.getAdsArrayList();
        Log.d(TAG, "Fetched From Fragment: "+adArrayList.toString());
        myAdAdapter.setAdArrayList(adArrayList);
        myAdAdapter.notifyDataSetChanged();
//        adsReference.get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        for (DocumentSnapshot documentSnapshot: queryDocumentSnapshots){
//
//                            Ad tempAd = new Ad(documentSnapshot.getData());
//                            if(tempAd.getCreatorEmail().equals("ab@d.com")){
//                                adArrayList.add(tempAd);
//                            }
//
//                        }
//                        myAdAdapter.notifyDataSetChanged();
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(getContext(), "Unable to fetch data, please try again!", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//
////        Waiting for new data to be added.....
////        CollectionReference adsReference = db.collection("adsRepo");
//        adsReference.whereEqualTo("creator", "ab@d.com").addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
//                if(e!=null){
//                    Toast.makeText(getContext(), "Network issues! :(", Toast.LENGTH_SHORT).show();
//
//                }else if (queryDocumentSnapshots!=null && !queryDocumentSnapshots.isEmpty()){
//                    for (DocumentSnapshot documentSnapshot: queryDocumentSnapshots){
//                        Ad tempAd = new Ad(documentSnapshot.getData());
//                        if(tempAd.getCreatorEmail().equals("ab@d.com")){
//                            adArrayList.add(tempAd);
//                        }
//                    }
//                    myAdAdapter.notifyDataSetChanged();
//                }
//            }
//        });
//        ____________________________________________________________________________________

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onRefresh() {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
        ArrayList<Ad> getAdsArrayList();

    }
}
