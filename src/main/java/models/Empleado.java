package models;

import java.util.concurrent.Semaphore;

public class Empleado implements Runnable{

    private CARGO cargo;
    private Call call;
    private Semaphore semaphore;

    /**
     * La clase Empleado es la encargada de llevar a cabo la llamada.
     * @param cargo Es obligatorio y fundamental para saber en que orden ser llamado.
     */
    public Empleado(CARGO cargo) {
        this.cargo = cargo;
        this.semaphore = new Semaphore(1);
    }

    /**
     * Este es el metodo en el cual una llamada es llevada a cabo y marcada como exitosa.
     * @param call Es la llamada a ser procesada.
     * @throws InterruptedException
     *          if any thread has interrupted the current thread. The
     *          <i>interrupted status</i> of the current thread is
     *          cleared when this exception is thrown.
     */
    public void realizarLlamada(Call call) throws InterruptedException{
        long mills = call.getTime();
        Thread.sleep(mills);
        call.setLlamadaExitosa();
    }

    /**
     * Metodo que deja preparada la llamada para ser procesada.
     * @param llamada Es la llamada a ser procesada.
     */
    public void asignarLlamada(Call llamada){
        this.call = llamada;
    }

    /**
     * @return Devuelve el cargo del empleado.
     */
    public CARGO getCargo(){
        return this.cargo;
    }

    /**
     * @return Devuelve true si el empleado no se encuentra disponible.
     */
    public synchronized boolean isLocked(){
        return this.semaphore.availablePermits() == 0;
    }

    /**
     * Metodo que marca al empleado como ocupado.
     * @throws InterruptedException  if the current thread is interrupted.
     */
    public void lock() throws InterruptedException{
        this.semaphore.acquire();
    }

    /**
     * Metodo que libera al empleado para pasar a estar disponible
     */
    public void unlock(){
        this.semaphore.release();
    }

    /**
     * PRECOND: Debe necesariamente tener una llamada asignada.
     * Realiza una llamada, la desasigna y se libera.
     */
    @Override
    public void run() {
        try{
            System.out.println("Soy "+getCargo()+" y voy a atender el llamado "+call.getIdentificador());
            realizarLlamada(this.call);
            System.out.println(getCargo() + " termine llamado "+call.getIdentificador()+" y finalice con "+call.getTime()/1000+" segundos.");
            this.call = null;
            this.unlock();
        }catch (Exception ex){

        }
    }
}
