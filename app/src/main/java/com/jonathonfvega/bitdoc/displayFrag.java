package com.jonathonfvega.bitdoc;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by chadhak on 4/9/2017.
 */

public class displayFrag extends Fragment {

    public View onCreateView(LayoutInflater inflator, ViewGroup container, Bundle saveInstanceState){

        return inflator.inflate(
                R.layout.display_frag, container, false);
    }
}
