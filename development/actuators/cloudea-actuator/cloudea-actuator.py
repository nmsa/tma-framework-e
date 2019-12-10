from flask import Flask
from flask import request
import json
import os
import logging
import logging.config
from tmalibrary.actuator import *
import requests

cloudeaActuator = Flask(__name__)

loginUrl = "https://159.122.129.190/api/login"
createCaseUrl = "https://159.122.129.190/api/messaging/cases/create"

initialIncidentId = 1

def login():

  payload = "user=admin%40instance1-starling.com&password=1Q2w3e4r!&undefined="
  headers = {
      'Content-Type': "application/x-www-form-urlencoded",
      'cache-control': "no-cache",
      'Postman-Token': "d9d1f9c9-6ff8-43d6-80da-743716f62531"
      }

  response = requests.request("POST", loginUrl, data=payload, headers=headers, verify = False)

  return response.cookies

cookies = login()

logger = logging.getLogger(__name__)
logger.info('Starting CloudEA Actuator')

@cloudeaActuator.route('/ActuatorAPI/act', methods=['POST'])
def process_message():
  # load json file
  input = request.get_data()
  message = HandleRequest()
  payload = message.processRequest(input)
  if payload['action'] == "createCase":
    data = createPayload(payload['configuration'])
    response = sendMessage(data, cookies)
    return message.generateResponse(str(response))
  else: 
    return logger.info('Not defined action')

def sendMessage(message, cookies):
  headers = {'content-Type':'application/json'}
  req = requests.post(createCaseUrl, data=json.dumps(message), headers=headers, verify=False, cookies=cookies)
  return req.status_code

def createPayload(config):

  global initialIncidentId

  #for conf in config:
  #  if conf['keyName'] == "incidentId":
  #    incidentId = conf['value']
  #  elif conf['keyName'] == "incidentSource":
  #    incidentSource = conf['value']
  #  elif conf['keyName'] == "subject":
  #    subject = conf['value']
  #  elif conf['keyName'] == "summary":
  #    summary = conf['value']
  #  elif conf['keyName'] == "is_public":
  #    is_public = conf['value']
  #  elif conf['keyName'] == "caseType":
  #    caseType = conf['value']
  #  elif conf['keyName'] == "aggregatedDomain":
  #    aggregatedDomain = conf['value']
  #  else : 
  #    return logger.error('Invalid configurations')
  
  incidentId = initialIncidentId
  initialIncidentId += 1
  incidentSource = "CloudEA" 
  subject = "Insufficient Security Control / Policy Coverage"
  summary = "The CloudEA system detected insufficient McAfee ePO Security Control /Policy coverage for the production environment. That might risk the organization and allow security breach. Make sure you review the ePO settings and policies."
  is_public = "true"
  caseType = "INCIDENT"
  aggregatedDomain = "Starling"

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
     logger.info('Initializing CloudEA Actuator')
     cloudeaActuator.run(debug='True', host='0.0.0.0', port=8080)
