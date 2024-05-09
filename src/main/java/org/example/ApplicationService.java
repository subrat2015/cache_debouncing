package org.example;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Semaphore;

@AllArgsConstructor
public class ApplicationService {

    private final Map<String, Semaphore> semaphores;
    private final Cache cache;
    private final Database database;

    public String getData(String key) {
        System.out.println(Thread.currentThread().getId() + " " + "task started");
        Optional<String> data = cache.get(key);
        if (data.isPresent()) {
            System.out.println("got from cache");
            return data.get();
        }
        Semaphore semaphore = semaphores.computeIfAbsent(key, k -> new Semaphore(1));
        try {
            semaphore.acquire();
            data = cache.get(key);
            if (data.isEmpty()) {
                String dataValue = database.getData();
                data = Optional.of(dataValue);
                cache.put(key, dataValue);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            semaphore.release();
            semaphores.remove(key);
        }
        System.out.println(Thread.currentThread().getId() + " " + "task completed");
        return data.get();
    }
}
