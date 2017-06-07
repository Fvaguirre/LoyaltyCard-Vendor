package card.loyalty.loyaltycardvendor.data_models;

/**
 * Created by samclough on 23/05/17.
 */

public class Promotion {

    public String title;
    public String description;
    public String creationDate;
    public String expiryDate;
    public String vendorId;

    // default constructor required for Firebase
    public Promotion(){}

    public Promotion(String title, String description, String creationDate, String expiryDate, String vendorId) {
        this.title = title;
        this.description = description;
        this.creationDate = creationDate;
        this.expiryDate = expiryDate;
        this.vendorId = vendorId;
    }
}
