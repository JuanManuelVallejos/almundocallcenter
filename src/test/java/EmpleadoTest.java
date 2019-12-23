import exceptions.NullCargoException;
import models.CARGO;
import models.Call;
import models.Empleado;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmpleadoTest {

    @Test
    void testEmpleadoExitoso() throws NullCargoException {
        CARGO testCargo = CARGO.DIRECTOR;
        Empleado empleado = new Empleado(testCargo);
        assertEquals(empleado.getCargo(), testCargo);
    }

    @Test
    void testEmpleadoSinCargo(){
        Assertions.assertThrows(NullCargoException.class, () -> {
            new Empleado(null);
        });
    }

    @Test
    void testRealizarLlamada() throws NullCargoException, InterruptedException {
        Call call = mock(Call.class);
        Empleado empleado = new Empleado(CARGO.DIRECTOR);
        empleado.realizarLlamada(call);
        Mockito.verify(call, Mockito.times(1)).setLlamadaExitosa();
    }

    @Test
    void testLocked() throws NullCargoException, InterruptedException {
        Empleado empleado = new Empleado(CARGO.DIRECTOR);
        assertFalse(empleado.isLocked());
        empleado.lock();
        assertTrue(empleado.isLocked());
        empleado.unlock();
        assertFalse(empleado.isLocked());
    }

    @Test
    void testRun() throws NullCargoException, InterruptedException {
        Empleado empleado = new Empleado(CARGO.SUPERVISOR);
        Call call = mock(Call.class);
        when(call.getTime()).thenReturn((long)500);
        empleado.asignarLlamada(call);
        empleado.lock();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(empleado);
        Thread.sleep(1000);
        executorService.shutdown();
        Mockito.verify(call, Mockito.times(1)).setLlamadaExitosa();
        Mockito.verify(call, Mockito.times(2)).getTime();
        assertFalse(empleado.isLocked());
    }
}
