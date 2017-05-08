package card.loyalty.loyaltycardvendor;

import junit.framework.Assert;

import org.junit.Test;

import card.loyalty.loyaltycardvendor.data_models.LoyaltyCard;

/**
 * Created by Sam on 6/05/2017.
 */

public class LoyatlyCardTests {

    // This tests the method addToPurchaseCount of the LoyaltyCard class
    @Test
    public void testAddToPurchaseCount() {
        LoyaltyCard card = new LoyaltyCard("Test", "Test", "Test");
        card.addToPurchaseCount(5);
        Assert.assertEquals(card.purchaseCount, "5");
    }

    // tests the addToPurchaseCount method with negative number
    @Test
    public void testAddToPurchaseCountNegative() {
        LoyaltyCard card = new LoyaltyCard("Test", "Test", "Test");
        card.addToPurchaseCount(-20);
        Assert.assertEquals(card.purchaseCount, "-20");
    }

    // tests the addToPurchaseCount method with a large number
    @Test
    public void testAddToPurchaseCountLargeNumber() {
        LoyaltyCard card = new LoyaltyCard("Test", "Test", "Test");
        card.addToPurchaseCount(999999999);
        Assert.assertEquals(card.purchaseCount, "999999999");
    }


    // test the redeem method
    @Test
    public void testRedeem() {
        LoyaltyCard card = new LoyaltyCard("Test", "Test", "Test");

        // check cannot be redeemed when no rewards issued.
        Assert.assertFalse(card.redeem());

        // issue reward and check that can be redeemed
        card.rewardsIssued = "1";
        Assert.assertTrue(card.redeem());

        // check that rewards claimed is now equal to rewards issued
        Assert.assertEquals(card.rewardsIssued, card.rewardsClaimed);
        Assert.assertEquals(card.rewardsClaimed, "1");

        // check that reward cannot be redeemed a second time
        Assert.assertFalse(card.redeem());

    }
}
