import models.CARGO;
import models.Call;
import models.Dispatcher;
import models.Empleado;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DispatcherTest {

    @Test
    public void testDiezLlamadasCon10Hilos(){
        List<Empleado> empleados = buildEmpleados(4,3,3);

        Dispatcher dispatcher = new Dispatcher(empleados,10);

        List<Call> calls = buildNCalls(dispatcher, 10);
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

    private List<Call> buildNCalls(Dispatcher dispatcher, int cantidad){
        List<Call> calls = new ArrayList<>();
        for (int i = 0 ; i< cantidad; i++){
            Call call = new Call(dispatcher, i+1);
            calls.add(call);
        }
        return calls;
    }

    private List<Empleado> buildEmpleados(Integer cantidadOperarios, Integer cantidadSupervisores, Integer cantidadDirectores){
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
