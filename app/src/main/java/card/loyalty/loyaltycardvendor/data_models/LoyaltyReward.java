package card.loyalty.loyaltycardvendor.data_models;

/**
 * Created by Caleb T on 11/06/2017.
 */

public class LoyaltyReward {

    // Public fields for Firebase Interaction
    public String offerID;
    public String customerID;
    public String rewardDesc;
    public String vendorID;
    public String rewardsAvailable;

    public String cardID_customerID;

    private String rewardID;

    public LoyaltyReward() {}

    public LoyaltyReward(String offerID, String customerID, String rewardDesc, String vendorID, String cardID) {
        this.offerID = offerID;
        this.customerID = customerID;
        this.rewardDesc = rewardDesc;
        this.vendorID = vendorID;
        this.cardID_customerID = cardID + "_" + customerID;
        this.rewardsAvailable = "0";
    }

    public void addRewardsAvailable() {
        int rA = Integer.parseInt(this.rewardsAvailable);
        rA++;

        this.rewardsAvailable = Integer.toString(rA);
    }

    public void claimReward() {
        int rA = Integer.parseInt(this.rewardsAvailable);
        rA--;

        this.rewardsAvailable = Integer.toString(rA);
    }

    public void setRewardID(String rewardID) {
        this.rewardID = rewardID;
    }

    public String retrieveRewardID() {
        return rewardID;
    }

}
