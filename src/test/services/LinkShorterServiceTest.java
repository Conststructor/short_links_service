package test.services;

import models.Link;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import services.LinkShorterService;

import java.time.LocalDateTime;

class LinkShorterServiceTest {
    LinkShorterService LSS;

    @BeforeEach
    void setUp(){
        LSS = new LinkShorterService();
    }

    @Test
    void isValidLink_testTrue(){
        String str = "https://www.google.com/";

        boolean result = LSS.isValidLink(str);
        Assertions.assertTrue(result);
    }

    @ParameterizedTest
    @CsvSource({
            "https://www.googlecom/",
            "https//www.google.com/",
            "https:/www.google.com/",
            "htp://www.google.com/",
            "soiuh34o098sudf"
    })
    void isValidLink_testFalse(String str){

        boolean result = LSS.isValidLink(str);
        Assertions.assertFalse(result);
    }


}
