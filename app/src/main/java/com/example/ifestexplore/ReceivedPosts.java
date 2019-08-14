package com.example.ifestexplore;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ReceivedPosts.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ReceivedPosts#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReceivedPosts extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private static final String TAG = "demo";
    private AdAdapter adAdapter;
    private ArrayList<Ad> adArrayList = new ArrayList<>();
    private RecyclerView rv_Ads;

    public ReceivedPosts() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReceivedPosts.
     */
    // TODO: Rename and change types and number of parameters
    public static ReceivedPosts newInstance(String param1, String param2) {
        ReceivedPosts fragment = new ReceivedPosts();
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_received_posts, container, false);
        rv_Ads = view.findViewById(R.id.rv_rec);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getActivity());
        rv_Ads.setLayoutManager(linearLayoutManager);
        adAdapter = new AdAdapter(adArrayList, this.getActivity());
        rv_Ads.setAdapter(adAdapter);

//        ___________________________________________________________________________________________
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usersData").document("abc@fgh.com").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    ArrayList adsGot = (ArrayList) documentSnapshot.get("ads");

                    for (Object ad: adsGot){
                        HashMap<String, String> adHM = (HashMap<String, String>) ad;
                        adArrayList.add(new Ad(adHM.get("comment"),adHM.get("serial_no")));

                    }
                    adAdapter.notifyDataSetChanged();
                }
            }
        });

//        ___________________________________________________________________________________________



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
    }
}
