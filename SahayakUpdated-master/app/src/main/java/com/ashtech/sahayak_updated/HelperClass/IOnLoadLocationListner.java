package com.ashtech.sahayak_updated.HelperClass;

import java.util.List;

public interface IOnLoadLocationListner {
    void onLoadLocationSuccess(List<MyLatLng> latLngs);
    void onLoadLocationFailed(String msg);
}
