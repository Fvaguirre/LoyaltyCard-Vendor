package card.loyalty.loyaltycardvendor;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;

import card.loyalty.loyaltycardvendor.data_models.LoyaltyOffer;

/**
 * Created by Caleb T on 3/05/2017.
 */

public class AddOfferFragment extends Fragment {

    private static final String TAG = "AddOfferFragment";

    // Firebase Authentication
    private FirebaseAuth mFirebaseAuth;

    // Firebase Database References
    private DatabaseReference mRootRef;
    private DatabaseReference mLoyaltyOffersRef;

    // Firebase User ID
    private String mUid;

    // View object
    private View view;

    private StorageReference mStorage;

    private static final int CHOOSE_IMAGE_INTENT = 111;

    // Fields
    private EditText description;
    private EditText purchasesPerReward;
    private EditText reward;
    private Button submitButton;
    private Button cancelButton;
    private Button chooseButton;

    private String key = "123";

    // Image identifiers
    private Bitmap selectedImage;
    private Uri uri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        // Initialises the View object
        view = inflater.inflate(R.layout.fragment_add_offer, container, false);

        // Gets UID
        mFirebaseAuth = FirebaseAuth.getInstance();
        mUid = mFirebaseAuth.getCurrentUser().getUid();

        // Gets Text for TextView and sets it
            // Finds the TextView
        TextView uidView = (TextView) view.findViewById(R.id.offer_uid);
            // Places text into TextView
        uidView.setText(mUid);

        // Firebase Initialisation
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mLoyaltyOffersRef = mRootRef.child("LoyaltyOffers");
        mStorage = FirebaseStorage.getInstance().getReference();

        // Field Initialisation
        description = (EditText) view.findViewById(R.id.offer_description);
        purchasesPerReward = (EditText) view.findViewById(R.id.offer_purchases_per_reward);
        reward = (EditText) view.findViewById(R.id.offer_reward);
        submitButton = (Button) view.findViewById(R.id.btn_submit_offer);
        cancelButton = (Button) view.findViewById(R.id.btn_cancel_offer);
        chooseButton = (Button) view.findViewById(R.id.btn_choose_image);

        chooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v == chooseButton){
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);

                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), CHOOSE_IMAGE_INTENT);
                }}

        });

        // Submit's info and closes fragment
        submitButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(v == submitButton) {

                    key = mLoyaltyOffersRef.push().getKey();

                    // Takes the push key for the database types as an identifier for the image child
                    StorageReference filepath = mStorage.child("Images").child(key);
                    // download path assigned upon image selection
                    filepath.putFile(uri);

                    // Creates new Loyalty Offer Object
                    LoyaltyOffer newOffer = new LoyaltyOffer(
                            mUid,
                            description.getText().toString(),
                            purchasesPerReward.getText().toString(),
                            reward.getText().toString()
                    );

                    mLoyaltyOffersRef.child(key).setValue(newOffer, new DatabaseReference.CompletionListener() {

                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                Toast.makeText(getContext(), "Adding Loyalty Offer Failed", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Loyalty Offer Successfully Added!", Toast.LENGTH_SHORT).show();
                                // Closes Fragment
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

        // Closes fragment when pressed
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v == cancelButton) {
                    Toast.makeText(getContext(), "Offer Creation Canceled", Toast.LENGTH_SHORT).show();
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOOSE_IMAGE_INTENT && resultCode == VendorActivity.RESULT_OK) {
            uri = data.getData();
            try {
                InputStream imageStream = getActivity().getContentResolver().openInputStream(uri);
                // Assigns selectedImage global variable to a decoded version of the picture
                selectedImage = BitmapFactory.decodeStream(imageStream);

                // Sets the selectedImage as an ImageView for UI purposes
                ImageView imageView = (ImageView)view.findViewById(R.id.imageView);
                imageView.setImageBitmap(selectedImage);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    // Hides Keyboard after fragment closes
    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    //TODO possibility of failure/success listeners for image file upload

}
