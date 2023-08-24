package org.example.worker;

import org.example.creators.Tribe;
import org.example.entity.map.GameField;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GameSimulation {
    public void startSimulation() {
        GameField gameField = null;
        try {
            gameField = GameField.readGameFieldConfigFile("game_field_config.yaml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Tribe tribe = new Tribe(gameField);

        try {
            tribe.createTribe();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        ScheduledExecutorService reproducePlant = Executors.newScheduledThreadPool(1);
        ScheduledExecutorService reproduceTribe = Executors.newScheduledThreadPool(1);
        ScheduledExecutorService animalMove = Executors.newScheduledThreadPool(1);
        ScheduledExecutorService animalEat = Executors.newScheduledThreadPool(1);
        ScheduledExecutorService statistic = Executors.newScheduledThreadPool(1);

        PlantThread plantThread = new PlantThread(gameField);
        AnimalThreadReproduce animalThreadRep = new AnimalThreadReproduce(gameField);
        AnimalThreadMove animalThreadMove = new AnimalThreadMove(gameField);
        AnimalThread animalThread = new AnimalThread(gameField);
        StatsThread statisticsThread = new StatsThread(gameField);

        reproducePlant.scheduleAtFixedRate(plantThread, 0, 2, TimeUnit.MILLISECONDS);
        animalMove.scheduleAtFixedRate(animalThreadMove, 0, 500, TimeUnit.MILLISECONDS);
        reproduceTribe.scheduleAtFixedRate(animalThreadRep, 0, 1, TimeUnit.SECONDS);
        animalEat.scheduleAtFixedRate(animalThread, 0, 1, TimeUnit.MILLISECONDS);
        statistic.scheduleAtFixedRate(statisticsThread, 0, 1, TimeUnit.SECONDS);

            try {
                Thread.sleep(100000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                // Останавливаем планировщик
                reproducePlant.shutdown();
                animalMove.shutdown();
                reproduceTribe.shutdown();
                animalEat.shutdownNow();
                statistic.shutdown();
                System.out.println("The program STOPPED :\n You saw how the number of animals has changed over a hundred years.\n 1 second one year");
                statisticsThread.drawHistogram(statisticsThread.getOrganismStatistics(gameField));
            }

    }
}
