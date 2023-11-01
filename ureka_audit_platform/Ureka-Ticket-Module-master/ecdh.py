# Random Generator
#coding:utf-8
import os

# ECDH
from cryptography.hazmat.primitives.asymmetric import ec
from cryptography.hazmat.primitives.kdf.hkdf import HKDF
from cryptography.hazmat.primitives import hashes
from cryptography.hazmat.backends import default_backend

# AES
from cryptography.hazmat.primitives.ciphers import (
    Cipher, algorithms, modes
)
from cryptography.hazmat.primitives import padding
from cryptography.exceptions import InvalidTag

######################################################
# Random Number Generation
# (pyca/cryptography recommends using operating system’s provided random number generator)
######################################################
def generate_random_byte(bytes_num):
    return os.urandom(bytes_num)

######################################################
# ECDH Key Factory 
# (Further apply ECDHE (ECDH, ephemeral) if consider Forward Secrecy)
#   server_private_key: _EllipticCurvePrivateKey
#   salt: bytes
#   info: bytes
#   peer_public_key: _EllipticCurvePublicKey
#
#   return: True/False
######################################################
def generate_ecdh_key(server_private_key, salt, info, peer_public_key):
    
    # Perform ECDH
    shared_key = server_private_key.exchange(ec.ECDH(), peer_public_key)

    # Perform Key Derivation [HKDF, HMAC-based Extract-and-Expand KDF]
    #
    #     ( Example in pyca/cryptography/HKDF doc, key_material=b"fixed input key", length=32, salt=generate_random_byte(16), info=b"hkdf-example" )
    #     ( Example in pyca/cryptography/ECDH doc, key_material=b"ephemeral shared key" (~= random), length=32, salt=None, info=b'handshake data' )    
    #     ( Example in pyca/cryptography/HKDFExpand doc, key_material=generate_random_byte(16), length=32, info = b"hkdf-example" )
    #        ( When the key material is already cryptographically strong (i.e. ephemeral), HKDFExpand (expand only version, no salt) can be used )
    #
    #     ( Because our shared key is fixed due to fixed private keys, maybe using HKDF (rather than HKDFExpand) 
    #       with (salt = generate_random_byte(16)) + (fixed info) 
    #       will be more secure!? ) 
    #
    derived_key = HKDF(
        algorithm=hashes.SHA256(),
        length=32,
        salt=salt,  # Randomizes the KDF’s output. Optional, but highly recommended. 
        info=info,  # Application specific context information. 
        backend=default_backend()
    ).derive(shared_key)
    
    return derived_key

######################################################
# ECDH AES Encryption (CBC Mode)
######################################################
def encrypt(plaintext, key):

    # IV must be the same number of bytes as the block_size of the cipher. 
    # IV must be random bytes. 
    # IV do not need to be kept secret and they can be included in a transmitted message.
    # Each time something is encrypted a new IV should be generated.
    # Unfortunately, there are attacks against CBC when IV is not implemented alongside a set of strong integrity and authenticity checks.
    iv = b"1234567890abcdef" # Dangerous?
    # iv = generate_random_byte(16) # recommanded, but need be transmited & perform authenticity check everytime?

    # Initailize AES (CBC mode, how about other stream cipher AEAD mode, like GCM mode?)
    cipher = Cipher(algorithms.AES(key), modes.CBC(iv), backend=default_backend())
    encryptor = cipher.encryptor()
    
    # Padding using PKCS7 (CBC is block cipher mode, which need padding)
    padder = padding.PKCS7(128).padder()
    padded_message = padder.update(plaintext) + padder.finalize()
    
    # Encrypt message
    encrypted_message = encryptor.update(padded_message) + encryptor.finalize()

    return (iv, encrypted_message)

######################################################
# ECDH AES Decryption (CBC Mode)
######################################################
def decrypt(ciphertext, key, iv):

    # Initailize AES (CBC mode, how about other stream cipher AEAD mode, like GCM mode?)
    cipher = Cipher(algorithms.AES(key), modes.CBC(iv), backend=default_backend())
    decryptor = cipher.decryptor()
    
    # Decrypt message
    decrypted_message = decryptor.update(ciphertext) + decryptor.finalize()

    # Unpadding using PKCS7 (CBC is block cipher mode, which need padding)
    unpadder = padding.PKCS7(128).unpadder()
    unpadded_message = unpadder.update(decrypted_message) + unpadder.finalize()

    return (unpadded_message)


######################################################
# Testing
# (Because command ticket & data ticket both have signature on it, so we can omit HMAC, is it?)
# (However, if we encrypt command ticket & data ticket only by AES, HMAC will be still needed, is it?)
# (GCM (Galois Counter Mode) is a new kind of stream cipher AEAD mode, which need not HMAC
######################################################
# private_key1 = ec.generate_private_key(ec.SECP256K1(), default_backend())
# public_key1 = private_key1.public_key()

# private_key2 = ec.generate_private_key(ec.SECP256K1(), default_backend())
# public_key2 = private_key2.public_key()

# pre_shared_info = b''

# salt = generate_random_byte(32)

# session_key1 = generate_ecdh_key(private_key1, salt, pre_shared_info, public_key2)
# print('session_key1: ' + str(session_key1))

# session_key2 = generate_ecdh_key(private_key2, salt, pre_shared_info, public_key1) 
# print('session_key2: ' + str(session_key2))

# if(session_key1 == session_key2):
#     print("Session Key is shared.")
# else:
#     print("Some error.")

# print()


# message = b'message to be encrypted'

# (iv, encrypted_message) = encrypt(message, session_key1)
# print('encrypted_message: ' + str(encrypted_message))

# decrypted_message = decrypt(encrypted_message, session_key2, iv)
# print('decrypted_message: ' + str(decrypted_message))

# if(message == decrypted_message):
#     print("Message is shared.")
# else:
#     print("Some error.")

# print()


######################################################
# ECDH AES Encryption (GCM Mode)
######################################################
def gcm_encrypt(key, plaintext, associated_data):
    # Generate a random 96-bit IV.
    iv = generate_random_byte(12)

    # Construct an AES-GCM Cipher object with the given key and a
    # randomly generated IV.
    encryptor = Cipher(
        algorithms.AES(key),
        modes.GCM(iv),
        backend=default_backend()
    ).encryptor()

    # associated_data will be authenticated but not encrypted,
    # it must also be passed in on decryption.
    encryptor.authenticate_additional_data(associated_data)

    # Encrypt the plaintext and get the associated ciphertext.
    # GCM does not require padding.
    ciphertext = encryptor.update(plaintext) + encryptor.finalize()

    return (iv, ciphertext, encryptor.tag)

######################################################
# ECDH AES Decryption (GCM Mode)
######################################################
def gcm_decrypt(key, associated_data, iv, ciphertext, tag):
    # Construct a Cipher object, with the key, iv, and additionally the
    # GCM tag used for authenticating the message.
    decryptor = Cipher(
        algorithms.AES(key),
        modes.GCM(iv, tag),
        backend=default_backend()
    ).decryptor()

    # We put associated_data back in or the tag will fail to verify
    # when we finalize the decryptor.
    decryptor.authenticate_additional_data(associated_data)

    # Decryption gets us the authenticated plaintext.
    # If the tag does not match an InvalidTag exception will be raised.
    return decryptor.update(ciphertext) + decryptor.finalize()


######################################################
# Testing
# (GCM (Galois Counter Mode) is a new kind of stream cipher AEAD mode, which need not HMAC
######################################################
# private_key1 = ec.generate_private_key(ec.SECP256K1(), default_backend())
# public_key1 = private_key1.public_key()

# private_key2 = ec.generate_private_key(ec.SECP256K1(), default_backend())
# public_key2 = private_key2.public_key()


# pre_shared_info = b''

# import os
# salt = generate_random_byte(32)

# session_key1 = generate_ecdh_key(private_key1, salt, pre_shared_info, public_key2)  # 32
# print('session_key1: ' + str(session_key1))

# session_key2 = generate_ecdh_key(private_key2, salt, pre_shared_info, public_key1)  # 32
# print('session_key2: ' + str(session_key2))

# if(session_key1 == session_key2):
#     print("Session Key is shared.")
# else:
#     print("Some error.")

# print()


# message = b'message to be encrypted'
# associated_plain_message = b"authenticated but not encrypted payload"

# (iv, encrypted_message, tag) = gcm_encrypt(
#     session_key1,
#     message,
#     associated_plain_message
# )
# print('encrypted_message: ' + str(encrypted_message))

# try:
#     decrypted_message = gcm_decrypt(
#         session_key2,
#         associated_plain_message,
#         iv,
#         encrypted_message,
#         tag
#     )
#     print ("Valid Tag.")
#     print('decrypted_message: ' + str(decrypted_message))
# except InvalidTag:
#     print ("Invalid Tag.")


# if(message == decrypted_message):
#     print("Message is shared.")
# else:
#     print("Some error.")

# print()

# try:
#     decrypted_message = gcm_decrypt(
#         session_key2,
#         associated_plain_message,
#         iv,
#         encrypted_message,
#         b'wrong_tagggggggg'
#     )
#     print ("Valid Tag.")
# except InvalidTag:
#     print ("Invalid Tag.")

# print()
