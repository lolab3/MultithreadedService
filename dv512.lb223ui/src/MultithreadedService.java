import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import static java.util.concurrent.Executors.newFixedThreadPool;

/*
 * File:	MultithreadedService.java
 * Course: 	20HT - Operating Systems - 1DV512
 * Author: 	Lola Barberan Baeta - lb223ui
 * Date: 	December 2020
 */

// TODO: put this source code file into a new Java package with meaningful name (e.g., dv512.YourStudentID)!

// You can implement additional fields and methods in code below, but
// you are not allowed to rename or remove any of it!

// Additionally, please remember that you are not allowed to use any third-party libraries




public class MultithreadedService {

    public static class Task implements Callable<Task> {
        int id;
        long burstTime;
        long startTime;
        long endTime;

        public Task call() throws Exception {
            try {
                startTime = System.currentTimeMillis();
                //System.out.println("Task " + id + " running by " + Thread.currentThread().getName());
                Thread.sleep(burstTime);
                endTime = System.currentTimeMillis();
                return this;
            } catch (InterruptedException e) {
                this.id = -id;
                return this;
            }
        }

    }
    // TODO: implement a nested public class titled Task here
    // which must have an integer ID and specified burst time (duration) in milliseconds,
    // see below
    // Add further fields and methods to it, if necessary
    // As the task is being executed for the specified burst time, 
    // it is expected to simply go to sleep every X milliseconds (specified below)


    // Random number generator that must be used for the simulation
	Random rng;

    // ... add further fields, methods, and even classes, if necessary

    List<Future<Task>> f = new ArrayList<Future<Task>>();
    List<Task> taskList;
    long startingTime;



    public MultithreadedService (long rngSeed) {
        this.rng = new Random(rngSeed);
    }


	public void reset() {
		// TODO - remove any information from the previous simulation, if necessary
    }
    

    // If the implementation requires your code to throw some exceptions, 
    // you are allowed to add those to the signature of this method
    public void runNewSimulation(final long totalSimulationTimeMs, final int numThreads, final int numTasks,
        final long minBurstTimeMs, final long maxBurstTimeMs, final long sleepTimeMs) {
        reset();

        taskList = new ArrayList<>();

        for(int i = 0 ; i < numTasks; i ++){
            Task task = new Task();
            task.id = i;
            task.burstTime = rng.nextInt((int)maxBurstTimeMs - (int)minBurstTimeMs + 1) + minBurstTimeMs;
            taskList.add(task);
        }

        ExecutorService threadPool = newFixedThreadPool(numThreads);

        startingTime = System.currentTimeMillis();

        try {
             f = threadPool.invokeAll(taskList, totalSimulationTimeMs, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {}

        threadPool.shutdown();
        // TODO:
        // 1. Run the simulation for the specified time, totalSimulationTimeMs
        // 2. While the simulation is running, use a fixed thread pool with numThreads
        // (see https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/Executors.html#newFixedThreadPool(int) )
        // to execute Tasks (implement the respective class, see above!)
        // 3. The total maximum number of tasks is numTasks, 
        // and each task has a burst time (duration) selected randomly
        // between minBurstTimeMs and maxBurstTimeMs (inclusive)
        // 4. The implementation should assign sequential task IDs to the created tasks (0, 1, 2...)
        // and it should assign them to threads in the same sequence (rather any other scheduling approach)
        // 5. When the simulation time is up, it should make sure to stop all of the currently executing
        // and waiting threads!

    }


    public void printResults() {
        // TODO:

        List<Integer> idCompleted = new ArrayList<Integer>();
        List<Integer> idInterrupted = new ArrayList<Integer>();
        List<Integer> idCancelled = new ArrayList<Integer>();

        for(int i = 0; i < taskList.size(); i ++) { idCancelled.add(i); }

        System.out.println("Completed tasks:");
        for(Future<Task> future : f) {
            if(!future.isCancelled()) {
                try {
                    idCompleted.add(future.get().id);
                    System.out.println("Task " + future.get().id + " - Starting Time " + (future.get().startTime - startingTime) + " - Burst Time " + future.get().burstTime + " - End Time " + (future.get().endTime - startingTime));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            };
        }
        // 1. For each *completed* task, print its ID, burst time (duration),
        // its start time (moment since the start of the simulation), and finish time

        for(int i : idCompleted) { idCancelled.remove(Integer.valueOf(i)); }

        for(Task t : taskList) {
            if (Integer.valueOf(t.id) < 0) {
                idInterrupted.add(-t.id);
                idCancelled.remove(Integer.valueOf(-t.id));
            }
        }
        
        System.out.println("Interrupted tasks:");
        for(int i : idInterrupted) {
            System.out.println("Task " + i);
        }
        // 2. Afterwards, print the list of tasks IDs for the tasks which were currently
        // executing when the simulation was finished/interrupted

        
        System.out.println("Waiting tasks:");
        for(int i : idCancelled) {
            System.out.println("Task " + i);
        }

        // 3. Finally, print the list of tasks IDs for the tasks which were waiting for execution,
        // but were never started as the simulation was finished/interrupted
	}




    // If the implementation requires your code to throw some exceptions, 
    // you are allowed to add those to the signature of this method
    public static void main(String args[]) {
		// TODO: replace the seed value below with your birth date, e.g., "20001001"
		final long rngSeed = 20000405;
				
        // Do not modify the code below — instead, complete the implementation
        // of other methods!
        MultithreadedService service = new MultithreadedService(rngSeed);
        
        final int numSimulations = 3;
        final long totalSimulationTimeMs = 15*1000L; // 15 seconds
        
        final int numThreads = 4;
        final int numTasks = 30;
        final long minBurstTimeMs = 1*1000L; // 1 second  
        final long maxBurstTimeMs = 10*1000L; // 10 seconds
        final long sleepTimeMs = 100L; // 100 ms

        for (int i = 0; i < numSimulations; i++) {
            System.out.println("Running simulation #" + i);

            service.runNewSimulation(totalSimulationTimeMs,
                numThreads, numTasks,
                minBurstTimeMs, maxBurstTimeMs, sleepTimeMs);

            System.out.println("Simulation results:"
					+ "\n" + "----------------------");	
            service.printResults();

            System.out.println("\n");
        }

        System.out.println("----------------------");
        System.out.println("Exiting...");
        
        // If your program has not completed after the message printed above,
        // it means that some threads are not properly stopped! -> this issue will affect the grade
    }
}
