from .KeyManager import KeyManager
import base64
import json
from .ActuatorPayload import ActuatorPayload

class HandleRequest:
    def generateResponse(self, plainResponse):
        keymanager = KeyManager()
        privateKeyPath = "keys/priv-key-actuator"
        privateKey = keymanager.getPrivateKey(privateKeyPath)
        
        signedResponseBytes = keymanager.sign(plainResponse,privateKey)
        signedResponseBase64 = base64.b64encode(signedResponseBytes)
        
        publicKeyExecutorPath = "keys/pub-key-executor"
        publicKeyExecutor = keymanager.getPublicKey(publicKeyExecutorPath)
        encryptedMessageBytes = keymanager.encrypt(plainResponse,publicKeyExecutor)
        encryptedMessageBase64 = base64.b64encode(encryptedMessageBytes)
        responseBytes = encryptedMessageBase64 + '\n'.encode() + signedResponseBase64
        
        return responseBytes
    
    def processRequest(self, request):
        # TODO: Handle Requests where the key is not valid
        privateKeyPath = "keys/priv-key-actuator"
        keymanager = KeyManager()
        privateKey = keymanager.getPrivateKey(privateKeyPath)
        decryptData = keymanager.decrypt(request, privateKey)
        input = json.loads(decryptData)
        payload = ActuatorPayload(input)
        return payload
