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
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import card.loyalty.loyaltycardvendor.data_models.Promotion;

/**
 * Created by samclough on 23/05/17.
 */

public class PushPromotionFragment extends Fragment {

    private static final String TAG = "PushPromotionFragment";

    // Firebase
    private DatabaseReference mPromotionsRef;


    // View
    private View mView;

    // Promotion Key
    private String mPromoKey;

    // VendorID
    private String mVendorId;

    // Widgets
    private EditText mTitle;
    private EditText mDescription;
    private EditText mExpiryDate;
    private Button mCancelButton;
    private Button mSubmitButton;

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
        mExpiryDate = (EditText) mView.findViewById(R.id.promo_expiry);
        mCancelButton = (Button) mView.findViewById(R.id.btn_cancel_promo);
        mSubmitButton = (Button) mView.findViewById(R.id.btn_submit_promo);


        // Clicking the submit button creates a Promotion and passes it to the createPromotion method
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Regex match to make sure date is valid
                if (!validateDate(mExpiryDate.getText().toString())) {
                    Toast.makeText(getContext(), "Invalid Date", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onClick: " + mExpiryDate.getText().toString());
                    return;
                }
                // check that the date has not already passed to avoid push notifications with no current promotion associated
                try {
                    if (isDatePassed(mExpiryDate.getText().toString())) {
                        Toast.makeText(getContext(), "Date Has Already Passed", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Unexpected Date Format", Toast.LENGTH_SHORT).show();
                    return;
                }

                Promotion promo = new Promotion(
                        mTitle.getText().toString(),
                        mDescription.getText().toString(),
                        getCurrentDate(),
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

    /**
     * Validates a date according to regular expression
     * @param date a string representing a date, expected to be in format dd/MM/yyyy
     * @return boolean true if date matches required format
     */
    public static boolean validateDate(String date) {
        String datePattern = "^(?:(?:31(\\/|-|\\.)(?:0?[13578]|1[02]))\\1|(?:(?:29|30)(\\/|-|\\.)(?:0?[1,3-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$|^(?:29(\\/|-|\\.)0?2\\3(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))$|^(?:0?[1-9]|1\\d|2[0-8])(\\/|-|\\.)(?:(?:0?[1-9])|(?:1[0-2]))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$";
        String yearYYYY = "^\\d{2}\\/\\d{2}\\/\\d{4}$";
        Pattern p = Pattern.compile(datePattern);
        Matcher m = p.matcher(date);

        // above regex allows YY format dates which we don't want
        Pattern pYYYY = Pattern.compile(yearYYYY);
        Matcher mYYYY = pYYYY.matcher(date);

        return m.matches() && mYYYY.matches();
    }

    /**
     *
     * @param date that may have passed
     * @return true if date has passed
     * @throws ParseException if the string cannot be parsed into date
     */
    public static boolean isDatePassed(String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date strDate = sdf.parse(date);
        Date today = removeTime(new Date());
        if (strDate.before(today)) {
            return true;
        } else return false;
    }

    /**
     * Method to remove the time component of the date (this is so it can be compared wth a date with no time component)
     * @param date a date to remove the time component from
     * @return the date with time component set to 00:00:00
     */
    public static Date removeTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * Gets the current date as a string
     * @return current date
     */
    public static String getCurrentDate() {
        Date date = removeTime(new Date());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(date);
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
