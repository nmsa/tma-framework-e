from flask import Flask
from flask import request
import json
import os
import logging
import logging.config
from HandleRequest import HandleRequest

demoActuator = Flask(__name__)

logger = logging.getLogger(__name__)
logger.info('Starting Demo Actuator')

@demoActuator.route('/ActuatorAPI/act/', methods=['POST'])
def process_message():
  # load json file
  input = request.get_data()
  message = HandleRequest()
  payload = message.processRequest(input)
  operation = executeaction(payload.action)
  return message.generateResponse(str(operation))

# Execute the action
def executeaction(action):
  logger.info('Action: %s', action)
  switcher = {
    "scale": "action " + action,
    "email": "action " + action
  }
  return switcher.get(action, "Not defined action: " + action)
  

 # load logging configuration file
def setup_logging(default_path='logging.json', env_key='LOG_CFG'):
  path = default_path
  value = os.getenv(env_key, None)
  if value:
     path = value
  if os.path.exists(path):
     with open(path, 'rt') as f:
         config = json.load(f)
     logging.config.dictConfig(config)
  else:
     logging.basicConfig(level=logging.DEBUG)


if __name__ == '__main__':
     setup_logging()
     logger = logging.getLogger(__name__)
<<<<<<< HEAD
     logger.info('Initializing  Demo Actuator')
     k8sActuator.run(debug='True', host='0.0.0.0', port=8080)
=======
     logger.info('Initializing Demo Actuator')
     demoActuator.run(debug='True', host='0.0.0.0', port=8080)
>>>>>>> 7ef476129470ab2749c31087df7d71d56960dbdf
