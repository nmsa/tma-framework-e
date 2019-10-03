from flask import Flask
from flask import request
import json
import os
import logging
import logging.config
from tmalibrary.actuator import *
import requests

cloudeaActuator = Flask(__name__)

logger = logging.getLogger(__name__)
logger.info('Starting CloudEA Actuator')

@cloudeaActuator.route('/ActuatorAPI/act', methods=['POST'])
def process_message():
  # load json file
  input = request.get_data()
  message = HandleRequest()
  payload = message.processRequest(input)
  if payload.action == "createCase":
    data = createPayload(payload.configuration)
    response = sendMessage(data)
    return message.generateResponse(str(response))
  else: 
    return logger.info('Not defined action')

def sendMessage(message):
  url = "https://159.122.129.190/api/messaging/cases/create"
  headers = {'content-Type':'application/json'}
  cookies = {'comilion-fw': 'DA05AA890DD8096BDDCC3D6E1A7C3EF9'}
  req = requests.post(url, data=json.dumps(message), headers=headers, verify=False, cookies=cookies)
  return req.status_code

def createPayload(config):

  for conf in config:
    if conf['keyName'] == "incidentId":
      incidentId = conf['value']
    elif conf['keyName'] == "incidentSource":
      incidentSource = conf['value']
    elif conf['keyName'] == "subject":
      subject = conf['value']
    elif conf['keyName'] == "summary":
      summary = conf['value']
    elif conf['keyName'] == "is_public":
      is_public = conf['value']
    elif conf['keyName'] == "caseType":
      caseType = conf['value']
    elif conf['keyName'] == "aggregatedDomain":
      aggregatedDomain = conf['value']
    else : 
      return logger.error('Invalid configurations')

  newPayload = {
  "incidentId": incidentId, 
  "incidentSource": incidentSource, 
  "subject": subject,
  "summary": summary,
  "is_public": is_public,
  "caseType": caseType,
  "aggregatedDomain": aggregatedDomain
  }
  return newPayload

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
     logger.info('Initializing Demo Actuator')
     cloudeaActuator.run(debug='True', host='0.0.0.0', port=8080)
