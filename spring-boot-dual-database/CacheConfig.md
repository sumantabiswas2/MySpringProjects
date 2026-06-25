##Cache Configuration Summary
```

Cache Name				Purpose					TTL	Max Size
customers				All 	customers list		10 min	500
customerById				Individual customer		10 min	500	
customerOrders			Customer orders			10 min	500
products					Individual products		10 min	500
productByCategory			Products by category		10 min	500
neojCustomer				Neo4j customers			10 min	500
neojProducts				Neo4j products			10 min	500
customerRecommendations	Product recommendations	1 hour	100
topCustomers				Top customers				30 min	100
customersByProduct		Product buyers			30 min	100
categoryStats	Category 	statistics				1 hour	50

```

### Best Practices Implemented

* Cache Eviction: When data changes (create/update/delete), caches are automatically evicted

* Conditional Caching: unless prevents caching of null/empty results

* Cache Statistics: Monitor cache hit rates to optimize performance

* Multiple Cache Managers: Different TTLs for different data types

* Explicit Cache Control: Manual endpoints to clear/evict caches

* Logging: All cache hits/misses are logged for debugging



### Start Redis container
> docker run --name redis -p 6379:6379 -d redis:alpine

### Run Spring Boot with redis-cache profile
> mvn spring-boot:run -Dspring-boot.run.profiles=redis-cache