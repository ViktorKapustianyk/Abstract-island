package org.example.worker;


import lombok.Getter;
import lombok.Setter;
import org.example.entity.map.GameField;
import org.example.entity.organism.Type;

import java.util.Map;
import java.util.concurrent.*;

@Getter
@Setter
public class GameWorker extends Thread {
    private static final int CORE_POOL_SIZE = 1; // Использует количество доступных ядер процессора
    private GameField gameField;
    private Statistics statistics;
    private CountDownLatch stepLatch;

    public GameWorker(GameField gameField) {
        this.gameField = gameField;
        this.statistics = new Statistics(gameField);
        this.stepLatch = new CountDownLatch(CORE_POOL_SIZE);
    }

    public void start() {
//        ExecutorService animalExecutor = Executors.newFixedThreadPool(CORE_POOL_SIZE);
//        ExecutorService plantExecutor = Executors.newFixedThreadPool(CORE_POOL_SIZE);
        ScheduledExecutorService statsExecutor = Executors.newScheduledThreadPool(1);
        ScheduledExecutorService animalExecutor = Executors.newScheduledThreadPool(1);
        ScheduledExecutorService plantExecutor = Executors.newScheduledThreadPool(1);

        OrganismWorker[] workersAnimals = new OrganismWorker[CORE_POOL_SIZE];
        PlantWorker[] workersPlants = new PlantWorker[CORE_POOL_SIZE];

        for (int i = 0; i < CORE_POOL_SIZE; i++) {
            workersAnimals[i] = new OrganismWorker(gameField, statistics);
            workersPlants[i] = new PlantWorker(gameField, statistics);
        }

        animalExecutor.scheduleAtFixedRate(() -> runWorkersAnimal(workersAnimals), 0, 1, TimeUnit.SECONDS);
        plantExecutor.scheduleAtFixedRate(() -> runWorkersPlant(workersPlants), 5, 5, TimeUnit.SECONDS);

        statsExecutor.scheduleAtFixedRate(() -> printOrganismStatistics(statistics), 0, 1, TimeUnit.SECONDS);

        if (!statistics.hasLivingHerbivores()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            animalExecutor.shutdownNow();
            plantExecutor.shutdownNow();
            statsExecutor.shutdownNow();
        }
    }

    private void runWorkersAnimal(OrganismWorker[] workers) {
        ExecutorService servicePool = Executors.newFixedThreadPool(CORE_POOL_SIZE);
        for (OrganismWorker worker : workers) {
            servicePool.submit(worker);
        }
        servicePool.shutdown();
    }

    private void runWorkersPlant(PlantWorker[] workers) {
        ExecutorService servicePool = Executors.newFixedThreadPool(CORE_POOL_SIZE);
        for (PlantWorker worker : workers) {
            servicePool.submit(worker);
        }
        servicePool.shutdown();
    }

    private void printOrganismStatistics(Statistics statistics) {
        System.out.println("Organism Statistics:");
        Map<Type, Integer> organismStatistics = statistics.getOrganismStatistics();
        statistics.drawHistogram(organismStatistics);
    }

    private void stepWorkers(OrganismWorker[] animalWorkers, PlantWorker[] plantWorkers) {
        ExecutorService servicePool = Executors.newFixedThreadPool(CORE_POOL_SIZE * 2);
        for (OrganismWorker worker : animalWorkers) {
            servicePool.submit(worker);
        }
        for (PlantWorker worker : plantWorkers) {
            servicePool.submit(worker);
        }
        servicePool.shutdown();
    }
}
