package card.loyalty.loyaltycardvendor.data_models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by samclough on 23/05/17.
 */

public class Subscription {

    public Boolean subscribed;

    public Subscription() {}

    public Subscription(Boolean subscribed) {
        this.subscribed = subscribed;
    }


}
