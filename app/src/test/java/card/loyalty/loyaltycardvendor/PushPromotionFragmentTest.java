package card.loyalty.loyaltycardvendor;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.text.ParseException;

/**
 * Created by samclough on 6/06/17.
 */
public class PushPromotionFragmentTest {

    private static PushPromotionFragment mFragment;

    @BeforeClass
    public static void createFragment() {
        mFragment = new PushPromotionFragment();
    }

    @AfterClass
    public static void tearDown() {
        mFragment = null;
    }

    /**
     *  Tests that a valid leap year is accepted
     */
    @Test
    public void testValidateLeapYearDate() {
        Assert.assertTrue("29/02/2020 should be valid", mFragment.validateDate("29/02/2020"));
    }

    /**
     *  Tests that invalid leap year date is not accepted
     */
    @Test
    public void testValidateInvalidLeapYearDate() {
        Assert.assertFalse("29/02/2019 should be invalid", mFragment.validateDate("29/02/2019"));
    }

    /**
     *  Tests that a valid date 30 dec is accepted
     */
    @Test
    public void testValidateValidDate30Dec() {
        Assert.assertTrue("30/12/2016 should be valid", mFragment.validateDate("30/12/2016"));
    }

    /**
     *  Tests that an invalid date 35 dec is not accepted
     */
    @Test
    public void testValidateInvalidDate35Dec() {
        Assert.assertFalse("35/12/2019 should be invalid", mFragment.validateDate("35/12/2019"));
    }

    /**
     *  Tests that date with invalid year format YY is not accepted
     */
    @Test
    public void testValidateInvalidDateYYFormat() {
        Assert.assertFalse("01/01/17 should be invalid because of YY formatting", mFragment.validateDate("01/01/17"));
    }

    /**
     *  Tests that a date with invalid day format d is not accepted
     */
    @Test
    public void testValidateInvalidDayFormat() {
        Assert.assertFalse("1/01/2018 should be invalid", mFragment.validateDate("1/01/2018"));
    }

    /**
     *  Tests that a date with invalid month format M is not accepted
     */
    @Test
    public void testValidateInvalidMonthFormat() {
        Assert.assertFalse("01/2/2050 should be invalid", mFragment.validateDate("01/2/2018"));
    }


    /**
     *  Tests that a date that has passed is recognised as passed
     */
    @Test
    public void testPassedDate() {
        try {
            Assert.assertTrue("06/06/2017 should be recognised as in the past, today is ", mFragment.isDatePassed("06/06/2017"));
        } catch (ParseException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    /**
     *  Tests that a future date (distant future so that this test will last a long time) is recognised as not having passed
     */
    @Test
    public void testFutureDate() {
        try {
            Assert.assertFalse("01/01/2050 should be recognised as in the future", mFragment.isDatePassed("01/01/2050"));
        } catch (ParseException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    /**
     *  Tests that an invaled date throws an exception when checking that it is a passed date
     */
    @Test (expected = ParseException.class)
    public void testInvalidPassedDate() throws ParseException {
        mFragment.isDatePassed("1/1///11111");
    }

}