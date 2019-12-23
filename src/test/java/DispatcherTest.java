import exceptions.EmptyOrNullEmpleadosExeption;
import exceptions.InvalidNumberOfThreadsException;
import exceptions.NullCargoException;
import exceptions.NullDispatcherException;
import models.CARGO;
import models.Call;
import models.Dispatcher;
import models.Empleado;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DispatcherTest {

    @Test
    void test10LlamadasCon10Hilos() throws Exception{
        templateXLLamadasConYHilos(10,10);
    }

    @Test
    void test20LlamadasCon10Hilos() throws Exception{
        templateXLLamadasConYHilos(20,10);
    }

    @Test
    void testDispatcherExitoso() throws NullCargoException, EmptyOrNullEmpleadosExeption, InvalidNumberOfThreadsException {
        List<Empleado> empleados = buildEmpleados(1,0,0);
        Dispatcher dispatcher = new Dispatcher(empleados, 1);
    }

    @Test
    void testDispatcherSinEmpleados(){
        List<Empleado> empleados = new ArrayList<>();
        Assertions.assertThrows(EmptyOrNullEmpleadosExeption.class, () -> {
            new Dispatcher(empleados, 1);
        });
    }

    @Test
    void testDispatcherConEmpleadosNull(){
        Assertions.assertThrows(EmptyOrNullEmpleadosExeption.class, () -> {
            new Dispatcher(null, 1);
        });
    }

    @Test
    void testDispatcherConCeroHilos() throws NullCargoException{
        List<Empleado> empleados = this.buildEmpleados(1,1,1);
        Assertions.assertThrows(InvalidNumberOfThreadsException.class, () -> {
            new Dispatcher(empleados, 0);
        });
    }

    @Test
    void testDispatcherConHilosNegativos() throws NullCargoException{
        List<Empleado> empleados = this.buildEmpleados(1,1,1);
        Assertions.assertThrows(InvalidNumberOfThreadsException.class, () -> {
            new Dispatcher(empleados, -5);
        });
    }

    private void templateXLLamadasConYHilos(int cantidadLlamadas, int cantidadDeHilos) throws Exception{
        List<Empleado> empleados = buildEmpleados(4,3,3);

        Dispatcher dispatcher = new Dispatcher(empleados,cantidadDeHilos);

        List<Call> calls = buildNCalls(dispatcher, cantidadLlamadas);
        for(Call call : calls){
            call.start();
        }

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(dispatcher);

        for (Call call : calls){
            try{
                call.join();
            }catch (Exception e){}
        }

        executorService.shutdown();
        assertTrue(calls.stream().allMatch(llamada -> llamada.getLlamadaExitosa()));
    }

    private List<Call> buildNCalls(Dispatcher dispatcher, int cantidad) throws NullDispatcherException {
        List<Call> calls = new ArrayList<>();
        for (int i = 0 ; i< cantidad; i++){
            Call call = new Call(dispatcher, i+1);
            calls.add(call);
        }
        return calls;
    }

    private List<Empleado> buildEmpleados(Integer cantidadOperarios, Integer cantidadSupervisores, Integer cantidadDirectores) throws NullCargoException {
        List<Empleado> empleados = new ArrayList<>();

        Map<CARGO, Integer> mapCargosCantidades = new HashMap<CARGO, Integer>() {{
            put(CARGO.OPERARIO, cantidadOperarios);
            put(CARGO.SUPERVISOR, cantidadSupervisores);
            put(CARGO.DIRECTOR, cantidadDirectores);
        }};

        for(CARGO cargo : mapCargosCantidades.keySet()){
            for(int i = 0; i < mapCargosCantidades.get(cargo); i++){
                Empleado empleado = new Empleado(cargo);
                empleados.add(empleado);
            }
        }

        return empleados;
    }

}
