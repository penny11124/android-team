# Ureka-Ticket-Module

[GitHub (private) link](https://github.com/Uternet1984/Ureka-Ticket-Module) 

<br/>


## Get Started

(1st-time) Initialize Python Virtual Environment & PIP Modules
- `$ python3 -m venv ureka-venv`
- `$ source ./ureka-venv/bin/activate`
- `$ pip3 install -r requirements.txt`
- `$ deactivate`

Afterward, always run your program in Python Virtual Environment
- `$ source ./ureka-venv/bin/activate`
- `$ python3 ticket_module_test_xxx.py`

<br/>


## Document

| Program                     | Description   |
|:--------------------------- |:------------- |
| `ticket_module_test_xxx.py` | Test Programs |
| `ticket_module.py`          | Main Program  |


|   Program   | Description   |
|:-----------:|:------------- |
| `ticket.py` | Ticket format |


| Program                | Description             |
|:---------------------- |:----------------------- |
| `ecc.py`               | [:link:][ECDSA]         |
| `ecdh.py`              | [:link:][ECDH & KDF]    |
| `key_serialization.py` | [:link:][Serialization] |


| Program        | Description                      |
|:-------------- |:-------------------------------- |
| `secure_db.py` | Use Unix File System as Keystore |


| Folder                               | Description  |
|:------------------------------------ | ------------ |
| `secure_db/private_authenticator_1/` | Smartphone-1 |
| `secure_db/private_authenticator_2/` | Smartphone-2 |
| `secure_db/iot_device_1/`            | IoT-Device-1 |

[ECDSA]: https://cryptography.io/en/latest/hazmat/primitives/asymmetric/ec/#elliptic-curve-signature-algorithms
[ECDH & KDF]:https://cryptography.io/en/latest/hazmat/primitives/asymmetric/ec/#elliptic-curve-key-exchange-algorithm
[Serialization]:https://cryptography.io/en/latest/hazmat/primitives/asymmetric/serialization/

<br/>


## Integration Test 

Scenario: Two users (with their smartphones), access an IoT device by creating or exchanging Ureka Tickets
1. Initialize Private Authenticator (i.e. Create private key in smartphone)
2. INITIALIZATION_TICKET: Initialize IoT Device (i.e. Create private key in iot device, & set 1st owner)
3. QUERY_TICKET: Query IoT Device (i.e. Query the device id or owner id of iot device, so that user can request a ticket from the owner  on transaction platform)
4. MANAGEMENT_TICKET: Manage IoT Device (i.e. Let authorized user become new owner)
5. ACCESS_PERMISSION_TICKET: Access IoT Device (i.e. Do not change owner, but let owner or authorized user access iot device with specific permissions)

<br/>


## Unit Test

- Under `ecc.py`
- Under `ecdh.py`
- Under `key_serialization.py`
- Under `secure_db.py`

<br/>


## To-Do List

- [x] Testing Module
    - [x] Intuitive Testing Module
    - [ ] Interative Integration Testing Mode: Nodes & Network Observer
    - [ ] Official Testing Module
    - [ ] Acceptance Testing

+ [x] [Cryptography Modules](https://cryptography.io/en/latest/)

- [x] Ticket Module (Embedded in different type of devices)
    - [x] Device Type: Private Authenticator & User Client
    - [x] Device Type: IoT Device
    - [ ] Device Type: Decouple Private Authenticator & User Client
    - [ ] Different features between Private Authenticator & IoT Device
    - [x] Initialization (for Private Authenticator)
    - [x] Initialization Ticket (for IoT Device)
    - [x] Query Ticket
    - [x] Management Ticket
    - [x] Access Permission Ticket
    - [ ] Access Permission Ticket + Session (Session Key + Command Tickets)
    - [ ] Return Ticket

+ [ ] Transaction Platform (Store Tickets & Return Tickets)
    + [ ] Device Table in TX Platform (or in the local cache in User Client)


<br/>
