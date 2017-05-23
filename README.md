# qapital-backend-test
My solution for the backend task

Notes:
 * Added some Bean validation to the endpoint both to give feedback on what's missing and to protect the business logic from missing data
 * Refactored Double to BigDecimal for safer handling of decimals in Transaction/SavingEvent and SavingsRule POJOs
 * Instead of relying on the auto-configured MappingJackson2HttpMessageConverter which did not handle the timestamps the way I wanted I created a Spring configuration for the necessary bean
 * I took some assumptions where I felt the specification was lacking information, these are marked with TODOs
  