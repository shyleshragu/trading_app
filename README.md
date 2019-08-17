# Trading Application



## Introduction

This repository contains an application that allows users to run a stock trading simulation using REST API. The application uses CRUD (Creation, Read, Update, Read) operations to create an account. It also was used to purchase, sell and view stocks. The application was developed with the intention of primary users being traders. 

Several microservices were used in order to implement the application:

1. IexCloud - API that was used populate the market stock data 
2. PostgreSQL - A database which was used to contain the transaction data. 
3. SpringBoot - Was used to configure dependencies.
4. Swagger UI - Used to create a GUI (Graphical User Interface) to run the program

---



## Quick Start

- Perquisites installed:
  1. Java
  2. Docker
  3. CentOS 7

- PSQL initialization was used to start the application. Code for it will be found at  [/scripts/run_trading_app.sh](https://github.com/shyleshragun/trading_app/blob/master/scripts/run_trading_app.sh)

-  Maven was used to build the application: [/pom.xml](https://github.com/shyleshragun/trading_app/blob/master/pom.xml)

- SpringBoot app was start by using bash.

  Command:

  > bash start_trading_app.sh SPRING_PROFILES_ACTIVE PSQL_HOST PSQL_USER PSQL_PASSWORD IEX_PUB_TOKEN

  The quick start file uses environment variables:

  - SPRING_PROFILES_ACTIVE - specifies the version of the application properties to use.  *dev* or *prod* are used, but currently, there is no difference in using either of them.
  - IEX_PUB_TOKEN - public token provided by IEX Cloud.
  - PSQL_HOST - name of PSQL database.
  - PSQL_USER - username to access database.
  - PSQL_PASSWORD - password to access database.
  - IEX_HOST - IEX host address (https://cloud.iexapis.com/v1)

- The application was designed to use Swagger-UI to interact with the application. The link used to access the Swagger-UI page whenever the application was properly executed was: http://localhost:5000/swagger-ui.html

  ![Swagger-UI image](https://github.com/shyleshragun/trading_app/blob/master/assets/swagger.JPG)

  

  The application can also be viewed using Postman. A controller web-address was inputted in the **GET** url box: http://localhost:5000/health or http://localhost:5000/quote/iex/ticker/fb

![Postman image 1](https://github.com/shyleshragun/trading_app/blob/master/assets/Postman.JPG) 

![Postman image 2](https://github.com/shyleshragun/trading_app/blob/master/assets/Postman1.JPG) 


---



## Rest API Usage

### Swagger

Swagger is a framework for describing an API using common language such that everyone could understand.  It is highly comprehensible to both developers and non-developers, machine readable and easily adjustable. Any API that adheres to Swagger specifications is easy to read, easy to iterate, and easy to consume.

 

### Quote Controller

Provides access to the Quote table within the database and allows adding, updating and accessing quotes. IEX market data was used to populate Quote table.

Endpoints:

- *GET '/quote/dailyList'* - list all securities that are available to trading in trading system

- *GET '/quote/iex/ticker/{ticker}'* - Shows IEX market data for {ticker}

- *POST '/quote/ticker/{ticker}'* - Adds new ticker to Quote database

- *PUT '/quote/'* - Update quote's Quote data

- *PUT '/quote/iexMarketData'* - Update all quotes from IEX Cloud

  

### Trader Controller

Provides access to the trader table within the database and allows users to create, delete and update trader information. Contains account balance to buy or sell stocks, that is, withdraw or sell funds.

Endpoints:

- *PUT '/trader/withdraaw/traderId/{traderId}/amount/{amount}'* - Removes {amount} from  {traderId}: traders' account.
- *PUT '/trader/deposit/traderId/{traderId}/amount/{amount}'* -  Adds {amount} to {traderId}: traders' account.
- *POST '/trader/'* - Creates trader with the details provided.

- *DELETE '/trader/traderId/{traderID}'* - Deletes {traderId} trader as long as there was no account balance or stocks.

  

### Order Controller

Allows users to buy or sell stock.

Endpoints:

- *POST '/order/marketOrder'* - Buy or sell stock with validation on the basis of trader's balance.

  

### App Controller

Controller with no function to the application. However, it was used to check whether the application was running by using url: http://localhost:8080/health.

Endpoints:

- *GET '/health'* - Check if application could access database.

  

### Dashboard Controller

Assists in allowing users to view their positions for every quote and view trader account details.

Endpoints:

- *GET '/dashboard/portfolio/traderId/{traderId}'* - Gets the portfolio of {traderId} trader, that is, list of  position and quote data of every stock involved with trader.
- *GET '/dashboard/profile/traderId/{traderId}'* - Gets the profile of {traderId} trader, that is, shows the trader and account data.

---



## Architecture

![Architecture Diagram](https://github.com/shyleshragun/trading_app/blob/master/assets/architecture.JPG)

### Logical Layers:

- **Controller** 

  All interaction between the user and the program happens within this layer. Users send requests to perform particular operations and the controller forwards the requests to Service layer. Also, all endpoints are described within the Controller layer.

  

- **Service**

  All requests sent from the Controller Layer are executed. This layer performs the requests using business logic, PSQL database and IEX Cloud. This layer interacts with Dao layer when needed.

  

- **Dao**

  This layer performs the CRUD actions on data within the database. It also provides access to IEX cloud in retrieving data. 

  

- **SpringBoot**

  A framework that was used to deploy and manage the application to Swagger-UI. Apache TomCat, an webservlet application that maps Http requests from Swagger-UI to application, was used.

  

- **PSQL** 

  A database that contains tables and views used by trader, account, quote and security order. 

  

- **IEX**

  An API that provides data on stock information.

---



## Improvements

1. Design program so that it could auto-populate new market data in an interval.
2. Provide more features to assist market trading such as graphical charts, percentage charts etc.
3. Design update to create short updates.
4. Provide a security protocol like three-handshake whenever buy or sell stock. This should be done to ensures that users understands and confirms on transfer.
5. Implement application onto the net and check if multi-user functions work.  

---

