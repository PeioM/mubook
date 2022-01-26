package com.libumu.mubook;

import com.libumu.mubook.api.*;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(org.junit.runners.Suite.class)

@Suite.SuiteClasses({
        AjaxControllerTest.class,
        DataControllerTest.class,
        FaqControllerTest.class,
        HomeControllerTest.class,
        ItemModelControllerTest.class,
        NewsControllerTest.class,
        ReservationControllerTest.class,
        SearchControllerTest.class,
        UserControllerTest.class,
        UserTypeControllerTest.class,
        MubookApplicationTest.class
})
public class MubookApplicationTest {
}
