# Actuators
 
## Communication with `Executor`

The communication between the executor and the actuator is done according to the following steps:

1. `Executor` component wants to send an action to the `Actuator`;
2. The public key from the corresponding `Actuator` is obtained from the database;
3. `Executor` encrypts the message with the public key of the `Actuator`;
4. `Executor` performs a REST request to the `Actuator`, whose payload is the encrypted message from the previous step;
5. `Actuator` receives the request and decrypts the message using its own private key;
6. `Actuator` performs the adapation according to the definition of the payload;
7. `Actuator` encrypts the response using the public key from the `Executor`;
8. `Actuator` signs the message using its own private key;
9. `Actuator` sends the response to the `Executor` as a stream of two lines encoded in Base64: (1) response message and (2) signature;
10. `Executor` verifies the signature using the public key of the actuator. If the message is valid, it decrypts the response with its own private key and the request is completed.

Both the public and private key for the Actuator can be generated using the `Admin` component. For more details about how to generate the keys as well as the algorithms used, check the [documentation](https://github.com/eubr-atmosphere/tma-framework-k/tree/master/development/tma-admin-console).

## Authors
* Jose Alexandre D'Abruzzo
