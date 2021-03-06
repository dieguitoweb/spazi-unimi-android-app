import junit.framework.TestCase;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Rule;
import org.junit.Test;

import it.unimi.unimiplaces.core.api.APIRequest;

public class APIRequestMakerTest extends TestCase {

    @Rule
    Mockery context = new JUnit4Mockery();

    @Test
    public void testNotFoundRequest(){
        APIRequest requestMaker = new APIRequest("http://istotallyanawesome404.com", APIRequest.APIRequestIdentifier.AVAILABLE_SERVICES);
        String response = requestMaker.getResponse();
        assertTrue(requestMaker.is404());
        assertNull(response);
    }

    @Test
    public void testRequest(){
        APIRequest requestMaker = new APIRequest("http://google.com", APIRequest.APIRequestIdentifier.BUILDINGS);
        String response = requestMaker.getResponse();
        assertFalse(requestMaker.is404());
        assertNotNull(response);
    }
}
