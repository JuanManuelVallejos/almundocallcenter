package models;

import java.util.Random;

public class Call extends Thread {

    private int MAX_TIME = 10000;
    private int MIN_TIME = 5000;

    private int identificador;
    private Dispatcher dispatcher;
    private long time;
    private Boolean exitosa = false;

    /**
     * La clase Call tiene una duración de entre 5 a 10 segundos.
     * @param dispatcher Es la encargada de darle el curso a la llamada derivandolo
     *                   a uno de sus empleados.
     * @param identificador Debe ser un numero univoco dentro del set de llamadas.
     */
    public Call(Dispatcher dispatcher, int identificador){
        this.dispatcher = dispatcher;
        this.calculateTime();
        this.identificador = identificador;
    }

    /**
     * Calcula propiedad interna time con una duración de entre 5000 y 10000 milisegundos.
     */
    private void calculateTime(){
        Random random = new Random();
        this.time = random.nextInt(MAX_TIME - MIN_TIME) + MIN_TIME;
    }

    /**
     * @return Devuelve el tiempo en milisegundos calculado al momento de crearse la Call.
     */
    public long getTime(){
        return this.time;
    }

    /**
     * @return Devuelve el número que se le asignó como identificación al momento de crearse la Call.
     */
    public int getIdentificador(){
        return  this.identificador;
    }

    /**
     * Este metodo debe ser invocado una vez que la llamada se encuentre finalizada.
     */
    public void setLlamadaExitosa(){
        synchronized(this.exitosa){
            this.exitosa = true;
        }
    }

    /**
     * @return Devuelve true si y solo si la llamada fue finalizada correctamente.
     */
    public boolean getLlamadaExitosa(){
        synchronized(this.exitosa){
            return exitosa;
        }
    }

    /**
     * El hilo de ejecucion de una llamada no finalizara hasta que esta no se marque
     * como exitosa.
     */
    @Override
    public void run() {
        this.dispatcher.dispatchCall(this);
        while(!getLlamadaExitosa());
    }
}
