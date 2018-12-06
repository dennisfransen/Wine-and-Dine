package grupp3.iths.se.wineanddineparalell.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

import grupp3.iths.se.wineanddineparalell.activities.LoginActivity;
import grupp3.iths.se.wineanddineparalell.R;

public class AppInfoFragment extends Fragment {

    public AppInfoFragment() {
    }

    private Button mSignOut;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_app_info, container, false);




        // Sign out button. Send end user to LoginActivity if button is pressed.
        mSignOut = view.findViewById(R.id.info_sign_out_btn);
        mSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
            }
        });



        return view;
    }

}
