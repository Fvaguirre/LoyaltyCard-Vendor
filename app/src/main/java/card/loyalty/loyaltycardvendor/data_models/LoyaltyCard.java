package card.loyalty.loyaltycardvendor.data_models;

import android.util.Log;

/**
 * Created by Sam on 25/04/2017.
 */

public class LoyaltyCard {

    // Public fields for Firebase interaction
    public String offerID;
    public String customerID;
    public String purchaseCount;
    public String rewardsIssued;
    public String rewardsClaimed;
    public String vendorID;
    public String purchasesPerReward;

    // Hybrid key for search
    public String offerID_customerID;

    private String cardID;

    public LoyaltyCard() {};

    public LoyaltyCard(String offerID, String customerID, String purchasesPerReward) {
        this.offerID = offerID;
        this.customerID = customerID;
        this.purchaseCount = "0";
        this.rewardsIssued = "0";
        this.rewardsClaimed = "0";
        this.offerID_customerID = offerID + "_" + customerID;
        this.purchasesPerReward = purchasesPerReward;
    }

    public String retrieveCardID() {
        return cardID;
    }

    public void setCardID(String cardID) {
        this.cardID = cardID;
    }

    // Increases the purchase count by number provided as parameter
    public void addToPurchaseCount(int amountPurchased) {
        int pC = Integer.parseInt(this.purchaseCount);
        pC += amountPurchased;
        this.purchaseCount = Integer.toString(pC);
        if (amountPurchased > 0) checkReward();
    }

    // redeem a reward
    public boolean redeem() {
        int issued = Integer.parseInt(this.rewardsIssued);
        int claimed = Integer.parseInt(this.rewardsClaimed);
        if (issued > claimed) {
            claimed ++;
            this.rewardsClaimed = Integer.toString(claimed);
            return true;
        } else {
            return false;
        }

    }

    // checks if reward should be issued
    public void checkReward() {
        int ppr = Integer.parseInt(purchasesPerReward);
        int pc = Integer.parseInt(purchaseCount);
        if (pc % ppr == 0) {
            int issued = Integer.parseInt(rewardsIssued);
            issued ++;
            this.rewardsIssued = Integer.toString(issued);
        }
    }
}
