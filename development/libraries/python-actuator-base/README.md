# Actuator `Python` Library

Main dependency to use during the development of actuators developed in `Python`.


## Build

To use the library, you need to run the following command.

```sh
pip install tmalibrary
```

## Usage

To use the library in the development of your actuator, you just need to import tmalibrary package in your actuator main file. To do that, you need to add the following line in your code.

```python
from tmalibrary.actuator import *
```

Note: check the [demo-actuator-python](../../actuators/demo-actuator-python) for mode detailed demonstration of the usage.

## Main Features

- **ActuatorPayload**: class to be used as parameter of the `act` operation according to the definition in [here](https://github.com/eubr-atmosphere/tma-framework-e/tree/master#actuators-definition);
- **HandleRequest**: class with methods to process the request from the `Executor` component to the Actuator.
- **KeyManager**: class with encryption and signature methods.

## Author
* Rui Silva
