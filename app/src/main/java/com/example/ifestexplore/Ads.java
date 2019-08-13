package com.example.ifestexplore;

import java.util.List;

public class Ads {
    private List<Ad> ads;
    public Ads(){

    }

    public List<Ad> getAds() {
        return ads;
    }

    public void setAds(List<Ad> ads) {
        this.ads = ads;
    }

    public Ads(List<Ad> ads) {
        this.ads = ads;
    }
}
