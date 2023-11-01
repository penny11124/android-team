# Ureka Module
#coding:utf-8
import key_serialization

# ECC
from cryptography.hazmat.primitives.asymmetric import ec
from cryptography.hazmat.backends import default_backend
from cryptography.hazmat.primitives import serialization
from cryptography.hazmat.primitives.serialization import PrivateFormat
from cryptography.hazmat.primitives.serialization import PublicFormat
from cryptography.hazmat.primitives.serialization import Encoding

# ECDSA
from cryptography.hazmat.primitives import hashes
from cryptography.exceptions import InvalidSignature

######################################################
# ECC Key Factory
######################################################
def generate_key_pair():

    private_key = ec.generate_private_key(ec.SECP256K1(), default_backend())
    private_key_byte = key_serialization.key_to_byte(private_key, key_type = 'ecc-private-key')

    public_key = private_key.public_key()
    public_key_byte = key_serialization.key_to_byte(public_key, key_type = 'ecc-public-key')    


    return (private_key_byte, public_key_byte)


######################################################
# Sign ECC Signature
#   messageIn: byte
#   privateKey: _EllipticCurvePrivateKey
#
#   return: byte
######################################################
def sign_signature(messageIn, privateKey):

    return privateKey.sign(messageIn, ec.ECDSA(hashes.SHA256()))




######################################################
# Verify ECC Signature
#   messageIn: byte
#   signatureIn: byte
#   publicKey: _EllipticCurvePublicKey
#
#   return: True/False
######################################################
def verify_signature(signatureIn, messageIn, publicKey):

    try:
        publicKey.verify(signatureIn, messageIn, ec.ECDSA(hashes.SHA256()))
        # print ("Valid Signature.")
        return True
    except InvalidSignature:
        # print ("Invalid Signature.")
        return False



######################################################
# Testing
#
#  ECC_Key_obj
#      |
#      |
#      v
#   DER_byte
#      |
#      |
#      v
#   JSON_str
######################################################
# # Generate Key
# (private_key_byte, public_key_byte) = generate_key_pair()   # (135, 88)

# # Test key_serialization
# private_key = key_serialization.byte_backto_key(private_key_byte, key_type = 'ecc-private-key')
# public_key = key_serialization.byte_backto_key(public_key_byte, key_type = 'ecc-public-key')

# # Test key_serialization
# print('readable_private_key_str: ' + key_serialization.byte_to_str(private_key_byte))
# print('readable_public_key_str: ' + key_serialization.byte_to_str(public_key_byte))

# # Test key_serialization
# print(key_serialization.key_to_byte(private_key, key_type = 'ecc-private-key') == private_key_byte)
# print(key_serialization.key_to_byte(public_key, key_type = 'ecc-public-key') == public_key_byte)
# print()


######################################################
# Testing
#
#     Message_byte -> Signature_byte
#         ^                 
#         |                | (BASE64)
#         | (UTF-8)        | (UTF-8)
#                          v
#     Message_str     Signature_str
#
######################################################
# message_str = 'message to be signed'
# message_byte = key_serialization.str_to_byte(message_str)
# print('message_byte: ' + str(message_byte))

# # Test key_serialization
# print(key_serialization.byte_backto_str(message_byte) == message_str)
# print()

# # Sign message 
# signature_byte = sign_signature(message_byte, private_key)
# print('signature_byte: ' + str(signature_byte))
# print('readable_signature_str: ' + key_serialization.byte_to_str(signature_byte))

# # Verify signature on message
# if(verify_signature(signature_byte, message_byte, public_key)):
#     print('Valid Signature for ' + str(message_byte))
# else:
#     print('Invalid Signature')

# # Test key_serialization
# print(key_serialization.str_backto_byte(key_serialization.byte_to_str(signature_byte)) == signature_byte)


