# Ureka Module
#coding:utf-8
import ticket
import secure_db
import ecc
import ecdh
import key_serialization


class TicketModule:
    
    def __init__(self, module_type = '', module_name = '', db_path = ''):

        # Module Type
        self.module_type = module_type

        # Module Name
        self.module_name = module_name

        # Boot_mode / Ticket_mode
        self.ticket_mode = False

        # Device Id (Device can be Private Autenticator or IoT Device...)
        self.device_priv_key = None
        self.device_priv_key_str = ''
        self.device_pub_key = None
        self.device_pub_key_str = ''
        
        # Permission Table (Owner, manager...)
        self.owner_pub_key = None
        self.owner_pub_key_str = ''

        # +++ Load SecureDB +++
        self.mSecureDB = secure_db.SecureDB(db_path = db_path)
        (self.ticket_mode, 
            self.device_priv_key, self.device_priv_key_str, 
                self.device_pub_key, self.device_pub_key_str, 
                    self.owner_pub_key, self.owner_pub_key_str) = self.mSecureDB.loadSecureDB(debug_mode = False)

        if(self.ticket_mode):
            print('\n+++ Ticket module <%s> has been initailized +++' % module_name)
            self.display_state()
        else:
            print('\n+++ Ticket module <%s> need to be initailized... +++' % module_name)


    ######################################################
    # Display (Debug/Test)
    ######################################################
    def display_state(self):

        if(self.module_type == ticket.PRIVATE_ANTHENTICATOR):
            print('####################################################################################################################################################')
            print('module_type: PRIVATE_ANTHENTICATOR')
            print()
            print('device_priv_key_str: %s' % self.device_priv_key_str[0:64])
            print('device_pub_key_str: %s' % self.device_pub_key_str[0:64])
            # print('device_priv_key_str: %s' % self.device_priv_key_str)
            # print('device_pub_key_str: %s' % self.device_pub_key_str)
            print('####################################################################################################################################################')

        if(self.module_type == ticket.IOT_DEVICE):
            print('####################################################################################################################################################')
            print('module_type: IOT_DEVICE')
            print()
            print('device_priv_key_str: %s' % self.device_priv_key_str[0:64])
            print('device_pub_key_str: %s' % self.device_pub_key_str[0:64])
            print()
            print('owner_pub_key_str: %s' % self.owner_pub_key_str[0:64])
            # print('device_priv_key_str: %s' % self.device_priv_key_str)
            # print('device_pub_key_str: %s' % self.device_pub_key_str)
            # print()
            # print('owner_pub_key_str: %s' % self.owner_pub_key_str)
            print('####################################################################################################################################################')

    def display_module_name(self):
        print()
        print('------------------------------------')
        print(self.module_name)
        print('------------------------------------')



    ######################################################
    # Generate Different Ticket Types
    ######################################################

    def generate_boostrapping_ticket(self, holder_id):

        new_ticket = ticket.Ticket()

        new_ticket.ticket_type = ticket.TYPE_INITIALIZATION_TICKET
        # no device_id
        new_ticket.holder_id = holder_id

        return new_ticket

    def generate_query_ticket(self):

        new_ticket = ticket.Ticket()

        new_ticket.ticket_type = ticket.TYPE_QUERY_TICKET

        return new_ticket

    def generate_management_ticket(self, device_id, holder_id, request_body):

        new_ticket = ticket.Ticket()

        new_ticket.ticket_type = ticket.TYPE_MANAGEMENT_TICKET
        new_ticket.device_id = device_id
        new_ticket.holder_id = holder_id
        new_ticket.request_body = request_body

        new_ticket = self.add_issuer_signature_on_ticket(new_ticket, self.device_priv_key)

        return new_ticket

    # Similar format with management_ticket
    def generate_access_permission_ticket(self, device_id, holder_id, request_body):

        new_ticket = ticket.Ticket()

        new_ticket.ticket_type = ticket.TYPE_ACCESS_PERMISSION_TICKET
        new_ticket.device_id = device_id
        new_ticket.holder_id = holder_id
        new_ticket.request_body = request_body

        new_ticket = self.add_issuer_signature_on_ticket(new_ticket, self.device_priv_key)

        return new_ticket

    # Similar format with access_permission_ticket
    def generate_challenge_ticket(self, device_id, holder_id):

        new_ticket = ticket.Ticket()

        new_ticket.ticket_type = ticket.TYPE_CHALLENGE_TICKET
        new_ticket.device_id = device_id
        new_ticket.holder_id = holder_id

        # Random Challenge
        random_challenge = ecdh.generate_random_byte(32)
        new_ticket.request_body = key_serialization.byte_to_str(random_challenge)

        new_ticket = self.add_issuer_signature_on_ticket(new_ticket, self.device_priv_key)

        return new_ticket

    # Similar format with access_permission_ticket
    def generate_response_ticket(self, device_id, holder_id):

        new_ticket = ticket.Ticket()

        new_ticket.ticket_type = ticket.TYPE_RESONSE_TICKET
        new_ticket.device_id = device_id
        new_ticket.holder_id = holder_id

        new_ticket = self.add_issuer_signature_on_ticket(new_ticket, self.device_priv_key)

        return new_ticket

    # Similar format with access_permission_ticket
    def generate_key_exchange_ticket(self, device_id, holder_id):

        new_ticket = ticket.Ticket()

        new_ticket.ticket_type = ticket.TYPE_KEY_EXCHANGE_TICKET
        new_ticket.device_id = device_id
        new_ticket.holder_id = holder_id

        # Random Salt
        random_salt = ecdh.generate_random_byte(32)
        new_ticket.request_body = key_serialization.byte_to_str(random_salt)

        new_ticket = self.add_issuer_signature_on_ticket(new_ticket, self.device_priv_key)        

        # Generate (temp) session_key
        session_key = self.generate_session_key(
                            server_private_key_obj = self.device_priv_key, 
                                salt_byte = random_salt, 
                                    info_byte = b'', 
                                        peer_public_key_obj = key_serialization.str_backto_key(holder_id, key_type = 'ecc-public-key'))
        print ("session_key: " + str(session_key))

        return new_ticket
    
    # Similar format with access_permission_ticket
    def generate_command_ticket(self, device_id, holder_id, request_body):

        pass

    def generate_return_ticket(self):
        
        # Query Ticket: Return Device_ID
        # Initialization Ticket: Return Device_ID

        # Command Ticket: Return Data

        pass



    ######################################################
    # Verify Different Ticket Types
    ######################################################
    def verify_xxx_ticket(self, ticket_in):


        # (Z-1) Verify TICKET_PROTOCOL_VERSION
        if (ticket_in.ticket_protocol_verision == ticket.TICKET_PROTOCOL_VERSION):
            print('(Z-1) PASS: TICKET_PROTOCOL_VERSION' + ' (' + ticket.TICKET_PROTOCOL_VERSION + ') ')
        else:
            print('(Z-1) ERROR: TICKET_PROTOCOL_VERSION')
            return


        # (Z-2) Classify TICKET_TYPE 
        if (ticket_in.ticket_type == ticket.TYPE_INITIALIZATION_TICKET):
            print('(Z-2) PASS: TYPE_INITIALIZATION_TICKET')

        elif (ticket_in.ticket_type == ticket.TYPE_QUERY_TICKET):
            print('(Z-2) PASS: TYPE_QUERY_TICKET')

        elif (ticket_in.ticket_type == ticket.TYPE_MANAGEMENT_TICKET):
            print('(Z-2) PASS: TYPE_MANAGEMENT_TICKET')
        
        elif (ticket_in.ticket_type == ticket.TYPE_ACCESS_PERMISSION_TICKET):
            print('(Z-2) PASS: TYPE_ACCESS_PERMISSION_TICKET')
        elif (ticket_in.ticket_type == ticket.TYPE_CHALLENGE_TICKET):
            print('(Z-2) PASS: TYPE_CHALLENGE_TICKET')
        elif (ticket_in.ticket_type == ticket.TYPE_RESONSE_TICKET):
            print('(Z-2) PASS: TYPE_RESONSE_TICKET')
        elif (ticket_in.ticket_type == ticket.TYPE_KEY_EXCHANGE_TICKET):
            print('(Z-2) PASS: TYPE_KEY_EXCHANGE_TICKET')
        # elif (ticket_in.ticket_type == ticket.TYPE_COMMAND_TICKET):
        #     print('(Z-2) PASS: TYPE_COMMAND_TICKET')

        # elif (ticket_in.ticket_type == ticket.TYPE_RETURN_TICKET):
        #     print('(Z-2) PASS: TYPE_RETURN_TICKET')
        
        else:
            print('(Z-2) ERROR: WRONG_TICKET_TYPE')
            return


        # (Z-3) Classify DEVICE_ID 
        if (ticket_in.ticket_type == ticket.TYPE_CHALLENGE_TICKET or ticket_in.ticket_type == ticket.TYPE_KEY_EXCHANGE_TICKET):
            # To-Do: Return Ticket - to get DEVICE_ID after initialization
            # if (ticket_in.device_id == self.slave_pub_key_str):
            print('(Z-3) PASS: DEVICE_ID')
        elif (ticket_in.ticket_type != ticket.TYPE_INITIALIZATION_TICKET and ticket_in.ticket_type != ticket.TYPE_QUERY_TICKET):
            if (ticket_in.device_id == self.device_pub_key_str):
                print('(Z-3) PASS: DEVICE_ID')
            else:
                print('(Z-3) ERROR: DEVICE_ID')
                return


        # (Z-4) Verify ISSUER_SIGNATURE
        if (ticket_in.ticket_type == ticket.TYPE_MANAGEMENT_TICKET):
            if self.verify_issuer_signature_on_ticket(ticket_in, self.owner_pub_key):
                print('(Z-4) PASS: ISSUER_SIGNATURE on MANAGEMENT_TICKET')
            else:
                print('(Z-4) ERROR: ISSUER_SIGNATURE on MANAGEMENT_TICKET')
                return

        elif (ticket_in.ticket_type == ticket.TYPE_ACCESS_PERMISSION_TICKET):
            if self.verify_issuer_signature_on_ticket(ticket_in, self.owner_pub_key):
                print('(Z-4) PASS: ISSUER_SIGNATURE on ACCESS_PERMISSION_TICKET')
            else:
                print('(Z-4) ERROR: ISSUER_SIGNATURE on ACCESS_PERMISSION_TICKET')
                return


        # (N) Verify ISSUER_SIGNATURE
        if (ticket_in.ticket_type == ticket.TYPE_CHALLENGE_TICKET):
            
            # To-Do: Return Ticket - to get DEVICE_ID after initialization
            # if self.verify_issuer_signature_on_ticket(ticket_in, self.slave_pub_key_str):
            print('(N-2) PASS: ISSUER_SIGNATURE on CHALLENGE_TICKET')

        elif (ticket_in.ticket_type == ticket.TYPE_RESONSE_TICKET):

            # To-Do: Need to check whether the CHALLENGE in the RESONSE_TICKET is correct

            if self.verify_issuer_signature_on_ticket(ticket_in, self.owner_pub_key):
                print('(N-3) PASS: ISSUER_SIGNATURE on RESONSE_TICKET')
            else:
                print('(N-3) ERROR: ISSUER_SIGNATURE on RESONSE_TICKET')
                return

        elif (ticket_in.ticket_type == ticket.TYPE_KEY_EXCHANGE_TICKET):
            
            # To-Do: Return Ticket - to get DEVICE_ID after initialization
            # if self.verify_issuer_signature_on_ticket(ticket_in, self.slave_pub_key_str):
            print('(N-4) PASS: ISSUER_SIGNATURE on KEY_EXCHANGE_TICKET')


        # (E-Z) Execute TICKET        
        if (ticket_in.ticket_type == ticket.TYPE_INITIALIZATION_TICKET):
            print('(E-Z) EXECUTE: INITIALIZATION_TICKET')
            
            print ("initialize_iot_device( )...")

            self.initialize_iot_device(ticket_in)

        
        elif (ticket_in.ticket_type == ticket.TYPE_QUERY_TICKET):
            print('(E-Z) EXECUTE: QUERY_TICKET')
            self.query() 

        elif (ticket_in.ticket_type == ticket.TYPE_MANAGEMENT_TICKET):
            print('(E-Z) EXECUTE: MANAGEMENT_TICKET')
            
            print ("ownership_transfer( )...")
            
            self.ownership_transfer(ticket_in)
        
        elif (ticket_in.ticket_type == ticket.TYPE_ACCESS_PERMISSION_TICKET):
            print('(E-Z) EXECUTE: ACCESS_PERMISSION_TICKET')

            # To-Do: Session
            # print ("set_session_permission( )...")

            print ("generate_challenge_ticket( )...")
        

        # (E-N) Execute TICKET
        if (ticket_in.ticket_type == ticket.TYPE_CHALLENGE_TICKET):
            print('(E-N) EXECUTE: CHALLENGE_TICKET')

            print ("generate_response_ticket( )...")

        elif (ticket_in.ticket_type == ticket.TYPE_RESONSE_TICKET):
            print('(E-N) EXECUTE: RESONSE_TICKET')

            print ("generate_key_exchange_ticket( )...")

        elif (ticket_in.ticket_type == ticket.TYPE_KEY_EXCHANGE_TICKET):
            print('(E-N) EXECUTE: KEY_EXCHANGE_TICKET')

            # Generate (temp) session_key
            session_key = self.generate_session_key(
                                server_private_key_obj = self.device_priv_key, 
                                    salt_byte = key_serialization.str_backto_byte(ticket_in.request_body), 
                                        info_byte = b'', 
                                            peer_public_key_obj = key_serialization.str_backto_key(ticket_in.device_id, key_type = 'ecc-public-key'))
            print ("session_key: " + str(session_key))

            print ("generate_command_ticket( )...")


            # To-Do: Session
            # print ("check_session_permission( )...")
            # print ("get_session_command( )...")


        # (AC) Return ticket



    ######################################################
    # Add ECC Signature on Ticket
    #   ticket_in: Ticket
    #
    #   return: Ticket
    ######################################################
    def add_issuer_signature_on_ticket(self, ticket_in, private_key):

        # Message
        message_str = key_serialization.ticket_to_jsonstr(ticket_in)
        message_byte = key_serialization.str_to_byte(message_str)

        # Sign Signature
        signature_byte = ecc.sign_signature(message_byte, private_key)

        # Add Signature on Ticket
        ticket_with_signature = ticket_in
        ticket_with_signature.issuer_signature = key_serialization.byte_to_str(signature_byte)

        return ticket_with_signature


    ######################################################
    # Sign ECC Signature on Ticket
    #   ticket_in: Ticket
    #
    #   return: True/False
    ######################################################
    def verify_issuer_signature_on_ticket(self, ticket_in, public_key):

        try:
            # Get Signature on Ticket    
            signature_byte = key_serialization.str_backto_byte(ticket_in.issuer_signature)

            # Message = Remove Signature on Ticket
            # Notice that we cannot simply set signature = '', but need to 'delete' the signature variable in object
            del ticket_in.issuer_signature    # == ticket_in.__dict__.pop('issuer_signature')
            
            message_str = key_serialization.ticket_to_jsonstr(ticket_in)
            #print('message_str = ' + message_str)
            message_byte = key_serialization.str_to_byte(message_str)

            # Verify Signature
            return ecc.verify_signature(signature_byte, message_byte, public_key)
        
        except AttributeError:
            print('ERROR: NO SIGNATURE')
            return False



    ######################################################
    # Ticket Handshake
    #   +++ Execute xxxTicket (E-Z) +++
    ######################################################
    def initialize_iot_device(self, new_ticket):

        if(self.module_type != ticket.IOT_DEVICE):
            print('ERROR: ONLY IOT_DEVICE CAN DO THIS OPERATION')
            return

        if(self.ticket_mode):
            print('ERROR: NOT INITIALIZATION_MODE')
            return

        ######################################################
        # Initialize Device Id
        ######################################################
        # Randomly generate device Id
        device_private_key_byte = b''
        device_public_key_byte = b''
        (device_private_key_byte, device_public_key_byte) = ecc.generate_key_pair()

        self.mSecureDB.initDeviceId(device_private_key_byte, device_public_key_byte)


        ######################################################
        # Update Permission Table (only for IoT Device)
        ######################################################
        owner_public_key_byte =  key_serialization.str_backto_byte(new_ticket.holder_id)

        self.mSecureDB.storeOwnerKey(owner_public_key_byte)


        ######################################################
        # Read-after-write Consistency
        ######################################################
        (self.ticket_mode, 
            self.device_priv_key, self.device_priv_key_str, 
                self.device_pub_key, self.device_pub_key_str, 
                    self.owner_pub_key, self.owner_pub_key_str) = self.mSecureDB.loadSecureDB()

        self.display_state()

    def query(self):
        print('device_pub_key_str: %s' % self.device_pub_key_str[0:64])
        print('owner_pub_key_str: %s' % self.owner_pub_key_str[0:64])

    def ownership_transfer(self, new_ticket):
        
        ######################################################
        # Decode Request Body
        ######################################################        
        request_body_dict = key_serialization.jsonstr_to_dict(new_ticket.request_body)    # sort_keys = True
        print('request_body_dict = ' + str(request_body_dict))

        ######################################################
        # Update Permission Table (MANAGEMENT_OWNER)
        ######################################################
        if(request_body_dict[ticket.REQUEST_BODY_MANAGEMENT_MANAGEMENT_TYPE] == ticket.MANAGEMENT_OWNER):

            owner_public_key_byte =  key_serialization.str_backto_byte(new_ticket.holder_id)

            self.mSecureDB.storeOwnerKey(owner_public_key_byte)

        ######################################################
        # Read-after-write Consistency
        ######################################################
        (self.ticket_mode, 
            self.device_priv_key, self.device_priv_key_str, 
                self.device_pub_key, self.device_pub_key_str, 
                    self.owner_pub_key, self.owner_pub_key_str) = self.mSecureDB.loadSecureDB()

        self.display_state()

    def set_session_permission(self, new_ticket):
        pass


    ######################################################
    # Ticket Handshake
    #   +++ Execute Command Ticket (E-N) +++
    ######################################################
    def generate_session_key(self, server_private_key_obj, salt_byte, info_byte, peer_public_key_obj):
        return ecdh.generate_ecdh_key(
                        server_private_key = server_private_key_obj, 
                            salt = salt_byte, 
                                info = info_byte, 
                                    peer_public_key = peer_public_key_obj)

    def check_session_permission(self):
        pass

    def get_session_command(self):
        pass   



    ######################################################
    # Initilize Private Authenticator (without using Ticket)
    ######################################################
    def initialize_private_authenticator(self):

        if(self.module_type != ticket.PRIVATE_ANTHENTICATOR):
            print('ERROR: ONLY PRIVATE_ANTHENTICATOR CAN DO THIS OPERATION')
            return

        if(self.ticket_mode):
            print('ERROR: NOT INITIALIZATION_MODE')
            return

        ######################################################
        # Initialize Device Id
        ######################################################
        # Randomly generate device Id
        device_private_key_byte = b''
        device_public_key_byte = b''
        (device_private_key_byte, device_public_key_byte) = ecc.generate_key_pair()

        self.mSecureDB.initDeviceId(device_private_key_byte, device_public_key_byte)

        ######################################################
        # Read-after-write Consistency
        ######################################################
        (self.ticket_mode, 
            self.device_priv_key, self.device_priv_key_str, 
                self.device_pub_key, self.device_pub_key_str, 
                    self.owner_pub_key, self.owner_pub_key_str) = self.mSecureDB.loadSecureDB()

        self.display_state()



