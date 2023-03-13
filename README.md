Welcome to this project that serves as a technical evaluation for the role of senior-back-end dev at Anymind. This is my
first time to develop an api using GraphQl. In the past I have only worked on Restfull Api's.

Below I will outline the following things:
- How to run the application
- Calling the api
- Architectural design decisions
- What I would have liked to improve.

If you have any questions after reading this document please do not hesitate to contact me directly.


## How to run the application
In order to run the application please use the following steps:
1. make sure you have the following installed:
   - git
   - docker
   - gradle / gradle wrapper
2. Clone the repository to your local directory
3. You can either import this project into your favourite IDEA (IntelliJ of course) or start the application from the commandline using ./gradlew run
4. First start the database by running 'docker-compose up -d' from the /local/ folder
5. Once you have decided which way to run the application please run the application with the following environment variables: DB_NAME=postgres;DB_PASSWORD=123;DB_PORT=5432;DB_USERNAME=postgres
6. You should now be able to access the api on 'http://localhost:8080' or if you like navigate to 'http://localhost:8080/graphiql' in your browser to use the convenient interface provided by graphQL
7. The database can be accessed on 'http://localhost:5432' using the credentials provided above.
 

## Calling the api

When everything has started running successfully, the following queries can be executed at 'http://localhost:8080' to retrieve the required responses:

### Retrieving all supported payment methods:
```
query{
  paymentMethods{
    
    method,
    priceModifierLower,
    priceModifierUpper,
    pointsApplicable
  }
}
```

### Retrieving all historical sales:
```
query{
  sales{
    datetime,
    sales
    points
  }
}
```

### Retrieving sales between two dates:
```
query{
  getSales(startDateTime: "2023-03-09T15:46:26.851+09:00",
    endDateTime:"2023-03-09T15:47:01.055+09:00"){
      datetime,
      sales
      points
  }
}
```

### Adding a new sale:
```
mutation{
  addSale(sale: {
    price: 10000.0,
    priceModifier: 0.95,
    paymentMethod: "Cash",
    datetime: "2022-09-01T00:01:00Z"
  })
  {
    ...on Sale{
      finalPrice,
      points
    }
    ...on NewSaleReturnFailedPayload{
      errorMessage
    } 
  }
}
```
The `... on Sale` and `... on NewSaleReturnFailedPayload` are required to retrieve either the sale-information or the 
error message if the mutation was not successful.

## Architectural design decisions:
In this part I would like to clarify some of the decisions I've made in designing and creating this example application.
In order to reduce confusion I will summarize these decisions by their relative domains
### Database:
As outlined I used Postgres as a db type. I chose to run the database within a docker-container so it would allow for easy 
startup and interaction with any database client.

the database initialization script found in "/local/db/" creates the required tables.
- tables were created without nullable fields to guarantee data-consistency
- enum-type was created for payment-type to reduce accidental incorrect input on db level (even though checks are made throughout application itself)
- Default timestamp set to now() on db level even though it is also handled in the application
- pre-populating payment_methods table with relevant values

### Repository - level:
I have made the decision to implement the two repositories in Jooq as I have had trouble with inconsistencies when using JPA.
Although it would have been faster to implement the repositories in JPA, I am not a big fan of hidden complexity that it brings 
with itself. In my experience this hidden complexity may also result in performance issues which are difficult to debug/resolve.

Although the implementation using Jooq has taken more time I believe it will create a more performant and stable application
in the longer run. 

### Service - level:
On the service level I have decided to separate the logic in two service-classes:
- PaymentService class only handles and verifies the payment-method that is provided by the user.
- SaleService class only handles the proper storage and retrieval of the sale.

Although for this application it would have been quite possible to do both in the same service-class I believe that creating 
separation where possible creates an application that is more simple to understand. Furthermore, the separation of logic
makes it easier for people that are new on a project to get started as it breaks the learning-curve up in smaller chunks.

### Controller - level:
As this application basically has two functions it did not make sense to split the Controller class into multiple parts.
Also, this is my first time using GraphQl so, I know there is plenty of room for improvement.



## What I would have liked to improve.

### Docker-compose:
I have started to create a docker-compose file in order to easily containerize the application. In its current form the 
containerisation does not function as the api-application is not able to find the database. I did spend some time trying to 
debug why this was happening, but decided to give up as it was costing me too much time, even though I believe I
was almost there.

### Testing:
Although I am confident that the tests cover the core of the application's logic, I would have liked to implement a
controller-class test using the GraphQlTester. It seems like a straight-forward tool to use, but provided the effort required
to either mock the complete database or spin-up a docker-container for the database, I decided to leave experimenting 
with this for another time.

### Kubernetes / multiple pods:
Although I Believe it is out of scope for this assignment, I think it is always a good idea to think about creating an 
application that is capable of running in multiple instances without creating racing-conditions or deadlocks on database
mutations. In this instance I decided not to spend the time as this is just a test and not a production application.

In its current form I believe it is possible to connect multiple instances of this api to a single database and have it work (although I did not test this).
Because the relative simplicity of the app I would not expect any weird things to happen. This confidence is supported by 
the use of Jooq since no lazy-fetching or unexpected operations on the database are being performed throughout the life of the
application.

### Git-commits
As I was diving straight into developing this application I paid little attention to committing in the proper way. Usually 
I would advise anyone to use clean-commit messages and split functionality across different commits. In this case, I
decided to commit most of the functionality at once as there will be no other colleges working on this project with me.

### GraphQl errors / best practices
As this is the first time I have created an api using GraphQl I think there are a lot of things that may be improved. I am sure
the error-handling can be done in better way. Furthermore, I think it would improve readability when splitting up the schema.graphqls
file.

### Error handling
Rather than the current implementation that basically catches errors on the controller-level and creates a
more descriptive error-message, I think it would be better to handle this more centrally and have custom exception-handlers
that produce these error-messages without the necessity to catch them on the controller level.