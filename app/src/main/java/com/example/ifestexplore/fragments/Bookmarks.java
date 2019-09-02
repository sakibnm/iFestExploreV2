package com.example.ifestexplore.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ifestexplore.R;
import com.example.ifestexplore.controllers.FavAdAdapter;
import com.example.ifestexplore.models.Ad;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Bookmarks.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Bookmarks#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Bookmarks extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static OnFragmentInteractionListener mListener;

    private static final String TAG = "demo";
    public static FavAdAdapter adAdapter;
    private static ArrayList<Ad> adArrayList = new ArrayList<>();
    private RecyclerView rv_Fav_Ads;
    View view;
    static SwipeRefreshLayout swipeRefreshLayout;

    public Bookmarks() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Bookmarks.
     */
    // TODO: Rename and change types and number of parameters
    public static Bookmarks newInstance(String param1, String param2) {
        Bookmarks fragment = new Bookmarks();
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
            getUpdatedList();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bookmarks, container, false);
        swipeRefreshLayout = view.findViewById(R.id.favorite_posts_swype);
        swipeRefreshLayout.setOnRefreshListener(Bookmarks.this);

        rv_Fav_Ads = view.findViewById(R.id.rv_fav_posts);
        rv_Fav_Ads.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getActivity());
        rv_Fav_Ads.setLayoutManager(linearLayoutManager);

        adAdapter = new FavAdAdapter(adArrayList, getContext(), new FavAdAdapter.MyFavClickListener() {
            @Override
            public void onRemove(int position, View view) {
//                adAdapter.notifyDataSetChanged();
            }
        });

        rv_Fav_Ads.setAdapter(adAdapter);

        //        ___________________________________________________________________________________________
//Fetching favorite posts...
        adArrayList = mListener.getFavAdsArrayList();
//        Log.d(TAG, "Fetched From Fragment: "+adArrayList.toString());
        adAdapter.setFavArrayList(adArrayList);
        adAdapter.notifyDataSetChanged();

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

    public static void getUpdatedList(){
        if(mListener!=null)adArrayList = mListener.getFavAdsArrayList();
        adAdapter.setFavArrayList(adArrayList);
        Log.d(TAG, "IN RECYCLER VIEW LIST: "+adArrayList.size()+" "+adArrayList);
        adAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        getUpdatedList();
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
        ArrayList<Ad> getFavAdsArrayList();
    }
}
