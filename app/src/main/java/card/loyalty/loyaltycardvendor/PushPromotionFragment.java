package card.loyalty.loyaltycardvendor;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import card.loyalty.loyaltycardvendor.data_models.Promotion;

/**
 * Created by samclough on 23/05/17.
 */

public class PushPromotionFragment extends Fragment {

    private static final String TAG = "PushPromotionFragment";

    // Firebase
    private DatabaseReference mPromotionsRef;


    // View
    View mView;

    // Promotion Key
    String mPromoKey;

    // VendorID
    String mVendorId;

    // Widgets
    EditText mTitle;
    EditText mDescription;
    EditText mExpiryDate;
    Button mCancelButton;
    Button mSubmitButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_push_promo, container, false);
        mView.setTag(TAG);

        if (((VendorActivity) getActivity()).isAuthenticated()) {
            onSignedInVerified();
        } else {
            FragmentManager manager = getFragmentManager();
            manager.popBackStack(0, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        mTitle = (EditText) mView.findViewById(R.id.promo_title);
        mDescription = (EditText) mView.findViewById(R.id.promo_description);
        mExpiryDate = (EditText) mView.findViewById(R.id.promo_description);
        mCancelButton = (Button) mView.findViewById(R.id.btn_cancel_promo);
        mSubmitButton = (Button) mView.findViewById(R.id.btn_submit_promo);


        // Clicking the submit button creates a Promotion and passes it to the createPromotion method
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO add regex to check date validity

                Promotion promo = new Promotion(
                        mTitle.getText().toString(),
                        mDescription.getText().toString(),
                        mExpiryDate.getText().toString(),
                        mVendorId
                );

                createPromotion(promo);
            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getContext(), "Push Promo Cancelled", Toast.LENGTH_SHORT).show();
                hideKeyboard(mView);
                getFragmentManager().popBackStack();

            }
        });

        return mView;
    }

    // creates a push promotion in the firebase promotions table
    private void createPromotion(Promotion promo) {
        mPromoKey = mPromotionsRef.push().getKey();
        mPromotionsRef.child(mPromoKey).setValue(promo, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Toast.makeText(getContext(), "Push Promotion Failed!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "Promotion Created!", Toast.LENGTH_LONG).show();

                    // Make the call to the cloud function API to push the promotion out to vendor's customers
                    pushPromotion(mPromoKey, mVendorId);

                    // Close Fragment
                    if(getFragmentManager().getBackStackEntryCount() > 0 ) {
                        hideKeyboard(mView);
                        getFragmentManager().popBackStack();
                    }
                }
            }
        });
    }

    // when promotion has been successfully created pushPromotion() should be called (from the success callback)
    private void pushPromotion(String promoID, String vendorId) {
        final RequestQueue queue = Volley.newRequestQueue(getContext());
        final String URL = "https://us-central1-loyaltycard-48904.cloudfunctions.net/push";

        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("promo", promoID);
        params.put("vendor", vendorId);

        JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            VolleyLog.v("Response:%n %s", response.toString(4));
                            Log.d(TAG, "onResponse: " + response.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });

        // add the request object to the queue to be executed
        queue.add(req);
    }

    private void onSignedInVerified() {
        mPromotionsRef = FirebaseDatabase.getInstance().getReference().child("Promotions");
        mVendorId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    // Hides Keyboard after fragment closes
    public void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
