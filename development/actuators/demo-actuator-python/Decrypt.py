from KeyManager import KeyManager
import base64

class Decrypt:

	def encryptResponse(self, response, privateKey, plainResponse):

		keymanager = KeyManager()
		signedResponse = keymanager.sign(response,privateKey)
		signedResponseEncoded = base64.b64encode(signedResponse)

		publicKeyExecutorPath = "keys/pub-key-executor.pem"
		publicKeyExecutor = keymanager.getPublicKey(publicKeyExecutorPath)
		encryptedMessage = keymanager.encrypt(message,publicKeyExecutor)
		response = base64.b64encode(str(encryptedMessage))
		response = response + "\n"
		response = response + signedResponseEncoded

	def handleRequest(self, request, response):

		# TODO: Handle Requests where the key is not valid
		privateKeyPath = "keys/private_key.pem" 
		keymanager = KeyManager()
		privateKey = keymanager.getPrivateKey(privateKeyPath)
		decryptData = keymanager.decrypt(request, privateKey)
		encryptResponse(response, privateKey, plainResponse)