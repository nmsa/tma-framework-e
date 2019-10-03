import requests
import json

def createPayload():
	payload = {"incidentId":38, 
	"incidentSource": "CloudEA", 
	"subject": "Non-complient policy discovered",
	"summary": "Non-complient policy discovered by Boris Giterman. Please check your McAffee policy distribution",
	"is_public": "true",
	"caseType": "INCIDENT",
	"aggregatedDomain": "Strarling"
}
	return payload

url = "https://159.122.129.190/api/messaging/cases/create"
payload = createPayload()
headers = {'content-Type':'application/json'}
cookies = {'comilion-fw': 'DC8FC0E97E63D3C1232A789F0337F6D1'}
req = requests.post(url, data=json.dumps(payload), headers=headers, verify=False, cookies=cookies)

print(req.status_code)