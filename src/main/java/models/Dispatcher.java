package models;

import exceptions.EmptyOrNullEmpleadosExeption;
import exceptions.InvalidNumberOfThreadsException;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.Arrays.asList;

public class Dispatcher implements Runnable {

    /** 'llamadasEntrantes' es una queue que debe funcionar de manera FIFO **/
    private ConcurrentLinkedDeque<Call> llamadasEntrantes;
    private List<Empleado> empleados;
    /** 'cargos' es una lista de cargos ordenados por prioridad **/
    private final List<CARGO> cargos = asList(CARGO.OPERARIO, CARGO.SUPERVISOR, CARGO.DIRECTOR);
    private ExecutorService executorService;

    /**
     * La clase Dispatcher es la encargada de derivar las llamadas entrantes a sus
     * empleados para su correcta ejecucion.
     * @param empleados La lista de empleados no puede estar vacia y debe tener a cada uno de ellos con un cargo .
     * @param threadsConcurrentes La cantidad de hilos que debe soportar ejecutandose concurrentemente.
     */
    public Dispatcher(List<Empleado> empleados, int threadsConcurrentes) throws EmptyOrNullEmpleadosExeption, InvalidNumberOfThreadsException{
        this.validateArgs(empleados, threadsConcurrentes);
        this.llamadasEntrantes = new ConcurrentLinkedDeque<>();
        this.empleados = empleados;
        this.executorService = Executors.newFixedThreadPool(threadsConcurrentes);
    }

    private void validateArgs(List<Empleado> empleados, int threadsConcurrentes) throws EmptyOrNullEmpleadosExeption, InvalidNumberOfThreadsException{
        if(empleados == null || empleados.isEmpty())
            throw new EmptyOrNullEmpleadosExeption();
        if(threadsConcurrentes <= 0)
            throw new InvalidNumberOfThreadsException();
    }

    /**
     * Metodo que recibe las llamadas, la cual puede ser invocada concurrentemente
     */
    public synchronized void dispatchCall(Call call){
        this.llamadasEntrantes.add(call);
    }

    /**
     * @return Devuelve true en caso de que hayan llamadas entrantes por procesar
     */
    public synchronized boolean hayLlamadasEntrantes(){
        return !llamadasEntrantes.isEmpty();
    }

    /**
     * @return Devuelve un empleado en caso de encontrar uno disponible y lo lockea
     *         Devuelve null en caso de no encontrar ningun empleado disponible
     * @throws InterruptedException if the current thread is interrupted
     */
    public synchronized Empleado findAndLockEmpleado() throws InterruptedException{
        for (CARGO cargo : cargos){
            for(Empleado empleado : empleados){
                if(cargo.equals(empleado.getCargo()) && !empleado.isLocked()){
                    empleado.lock();
                    return empleado;
                }
            }
        }
        return null;
    }

    /**
     *  El hilo corre siempre que su thred siga vivo.
     *  En caso de haber llamadas entrantes encuentra y lockea un empleado de la lista
     *  para luego desencolar la primer llamada en espera, asignarselo y ejecutarlo en su Thread.
     *  En caso de no haber empleados no desencola nunca el llamado, quedando asi reservado su lugar
     *  en la fila.
     */
    @Override
    public void run() {
        while(true){
            if(hayLlamadasEntrantes()){
                try{
                    Empleado empleado = findAndLockEmpleado();
                    if(empleado != null) {
                        empleado.asignarLlamada(llamadasEntrantes.poll());
                        executorService.execute(empleado);
                    }else{
                        //System.out.println("Esto con 10 no deberia pasar");
                    }
                }catch(Exception ex){

                }
            }
        }
    }
}
