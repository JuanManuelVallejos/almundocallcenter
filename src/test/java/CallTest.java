import exceptions.NullDispatcherException;
import models.Call;
import models.Dispatcher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class CallTest {

    @Test
    void testCallExitosa() throws NullDispatcherException {
        int testIdentificador = 1;
        Call call = new Call(mock(Dispatcher.class), testIdentificador);
        assertEquals(call.getIdentificador(),testIdentificador);
    }

    @Test
    void testCallDispatcherNull(){
        int testIdentificador = -10;
        Assertions.assertThrows(NullDispatcherException.class, () -> {
            new Call(null, testIdentificador);
        });
    }

    @Test
    void testCallTimeMin5Seconds() throws NullDispatcherException {
        int testIdentificador = 1;
        Call call = new Call(mock(Dispatcher.class), testIdentificador);
        assertTrue(call.getTime() >= 5000);
    }

    @Test
    void testCallTimeMax10Seconds() throws NullDispatcherException {
        int testIdentificador = 1;
        Call call = new Call(mock(Dispatcher.class), testIdentificador);
        assertTrue(call.getTime() <= 10000);
    }

    @Test
    void testCallTerminate() throws NullDispatcherException, InterruptedException {
        int testIdentificador = 1;
        Call call = new Call(mock(Dispatcher.class), testIdentificador);
        call.start();
        Thread.sleep(500);
        call.setLlamadaExitosa();
        Thread.sleep(500);
        assertFalse(call.isAlive());
    }

}
