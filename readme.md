This is a simple project for me to understand REST API, containerization and microservice design.

"Wallets" is an application that allows users to have different accounts(wallets) in different currencies and transfer money between them with ease.

You can start all services using docker compose up. Then, go to your localhost:80 in order to interact with the user interface where you can transfer money between wallets of yourself and others with different currencies and request deposits from or withdrawals to outside banks using sample users test1, test2 and test3 with password "123" or sign up a new user. You can also open http://localhost:8080/swagger-ui.html that acts as the authority to accept or reject out-of-Wallets deposits and withdrawals.

You can change your host ports that listen on the service containers or transfer and exchange fee in the docker compose file. Furthermore, uncomment lines 70 and 71 if you prefer to work with swagger(http://localhost:9090/swagger-ui.html) rather than the UI frontend.

Currency exchange rates are updated every 10 seconds by the rate updater service and are random with no basis in reality.

