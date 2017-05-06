package card.loyalty.loyaltycardvendor;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import card.loyalty.loyaltycardvendor.data_models.Vendor;


public class AddDetailFragment extends Fragment {
    private String mUid;

    private FirebaseAuth mFirebaseAuth;

    private DatabaseReference mRootRef;
    private DatabaseReference mVendorDetailsRef;

    private EditText businessName;
    private EditText businessAddress;

    private Button submitButton;
    private Button cancelButton;

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        view = inflater.inflate(R.layout.fragment_add_detail, container, false);


        //create reference point
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mVendorDetailsRef = mRootRef.child("Vendors");

        //gathers UID from VendorLandingActivity
        mFirebaseAuth = FirebaseAuth.getInstance();
        mUid = mFirebaseAuth.getCurrentUser().getUid();

        businessName = (EditText) view.findViewById(R.id.detail_name);
        businessAddress = (EditText) view.findViewById(R.id.detail_address);
        submitButton = (Button) view.findViewById(R.id.btn_submit_detail);
        cancelButton = (Button) view.findViewById(R.id.btn_cancel_detail);


        submitButton.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v) {

                if(v == submitButton) {
                    String key = mVendorDetailsRef.push().getKey();

                    // Creates new Loyalty Offer Object
                    Vendor newVendor = new Vendor(
                            businessName.getText().toString(),
                            businessAddress.getText().toString()

                    );


                    //Child under Vendor is assigned the users UID as a key and values from text fields are set into the database under UID
                    mVendorDetailsRef.child(mUid).setValue(newVendor, new DatabaseReference.CompletionListener() {

                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "New Details Added Successfully", Toast.LENGTH_SHORT).show();

                                if(getFragmentManager().getBackStackEntryCount() > 0 ) {
                                    hideKeyboard();
                                    getFragmentManager().popBackStack();
                                }
                            }
                        }
                    });
                }
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v == cancelButton) {
                    Toast.makeText(getContext(), "Detail update cancelled", Toast.LENGTH_SHORT).show();
                    // Closes Fragment
                    if(getFragmentManager().getBackStackEntryCount() > 0 ) {
                        hideKeyboard();
                        getFragmentManager().popBackStack();
                    }
                }
            }
        });
        return view;
    }



    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}