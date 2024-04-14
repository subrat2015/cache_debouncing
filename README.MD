# Context
Application will cache the database response. Now suppose if 10k requests concurrently came for same key
in single application instance for which data is not present in cache , it will fetch from db hence db
load increases, How to avoid that ?

## Approach
Use cache debouncing 
For each key, we used semaphore so that only one database call can be make for same key

## Steps to Implement
Cache Setup: Set up a cache mechanism using a library like Google Guava or a simple ConcurrentHashMap if your needs are basic.

Semaphore Map: Create a ConcurrentHashMap where keys are the data keys and values are semaphores. This map will control access to the database load operation for each key.

Synchronization Logic:

When a request arrives for a specific key, check the cache first.
If the cache hit occurs, return the data immediately.
If the cache miss occurs, the process should then check the semaphore map.
If no semaphore exists for the key, create a new semaphore (with a single permit), put it in the map, and acquire it.
If a semaphore already exists, try acquiring it or wait until it's available.
The thread that acquires the semaphore is responsible for fetching the data from the database and updating the cache.
Once the data is fetched and cached, release the semaphore and notify other threads.

Handling Semaphore:

Ensure that the semaphore is released properly in all cases to avoid deadlocks.
Remove the semaphore from the map once the data is loaded into the cache to clean up resources.



