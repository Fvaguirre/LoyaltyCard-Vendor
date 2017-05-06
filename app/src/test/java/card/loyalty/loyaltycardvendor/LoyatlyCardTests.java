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
        LoyaltyCard card = new LoyaltyCard("Test", "Test");
        card.addToPurchaseCount(5);
        Assert.assertEquals(card.purchaseCount, "5");
    }
}
