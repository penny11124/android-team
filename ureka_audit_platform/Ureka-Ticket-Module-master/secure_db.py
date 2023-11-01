# Ureka Module
#coding:utf-8
import key_serialization

# File Path
import os

class SecureDB:

    def __init__(self, db_path = ''):
        
        # File I/O
        self.current_path = os.path.abspath(os.path.dirname(__file__)) + db_path

        self.path_device_priv = "/DeviceKey/PrivateKey.key"
        self.path_device_pub = "/DeviceKey/PublicKey.key"

        self.path_owner_pub = "/OwnerKey/PublicKey.key"


        # Device Id
        self.device_priv_key = None
        self.device_priv_key_byte = b''
        self.device_priv_key_str = ''
        
        self.device_pub_key = None
        self.device_pub_key_byte = b''
        self.device_pub_key_str = ''


        # Permission Table (Owner, manager...)
        self.owner_pub_key = None
        self.owner_pub_key_byte = b''
        self.owner_pub_key_str = ''


    ######################################################
    # Mode: 
    #   - Boot Mode
    #   - Ticket Mode
    ######################################################
    def loadSecureDB(self, debug_mode = False):
    
        ticket_mode = False


        if(self.checkFileExist(self.path_device_priv)):
            if(self.checkFileExist(self.path_device_pub)):
                    
                self.device_priv_key_byte = self.loadFile(self.path_device_priv)
                self.device_priv_key = key_serialization.byte_backto_key(self.device_priv_key_byte, key_type = 'ecc-private-key')
                self.device_priv_key_str = key_serialization.byte_to_str(self.device_priv_key_byte)

                self.device_pub_key_byte = self.loadFile(self.path_device_pub)
                self.device_pub_key = key_serialization.byte_backto_key(self.device_pub_key_byte, key_type = 'ecc-public-key')
                self.device_pub_key_str = key_serialization.byte_to_str(self.device_pub_key_byte)

                if(debug_mode):                            

                    print('device_priv_key_str: %s' % self.device_priv_key_str)
                    print('device_pub_key_str: %s' % self.device_pub_key_str)
                    print()

                ticket_mode = True
        
        if(self.checkFileExist(self.path_owner_pub)):
            
            self.owner_pub_key_byte = self.loadFile(self.path_owner_pub)
            self.owner_pub_key = key_serialization.byte_backto_key(self.owner_pub_key_byte, key_type = 'ecc-public-key')
            self.owner_pub_key_str = key_serialization.byte_to_str(self.owner_pub_key_byte)

            if(debug_mode):

                print('owner_pub_key_str: %s' % self.owner_pub_key_str)
                print()

            ticket_mode = True

        return (ticket_mode, 
                    self.device_priv_key, self.device_priv_key_str, 
                            self.device_pub_key, self.device_pub_key_str, 
                                    self.owner_pub_key, self.owner_pub_key_str)

    # Initialization
    def initDeviceId(self, device_priv_key_byte, device_pub_key_byte, debug_mode = False):

        self.storeFile(self.path_device_priv, device_priv_key_byte)
        self.storeFile(self.path_device_pub, device_pub_key_byte)

    # Initialization / Owner-transfer
    def storeOwnerKey(self, owner_pub_key_byte, debug_mode = False):
        
        self.storeFile(self.path_owner_pub, owner_pub_key_byte)



    ######################################################
    # File I/O (byte)
    ######################################################
    def storeFile(self, relative_path, data, debug_mode = False):
        
        # Get abs file path
        abs_path = self.current_path + relative_path
        
        if(debug_mode):
            print("Data : %s" % data) 

        # Create directory if not exist
        if not os.path.exists(os.path.dirname(abs_path)):
            try:
                os.makedirs(os.path.dirname(abs_path))
            except OSError as exc: # Guard against race condition
                if exc.errno != errno.EEXIST:
                    if(debug_mode):
                        print("Directory not exist.")
                    raise
        
        # Open and write file
        with open(abs_path, 'wb') as f:
            f.write(data)

        if(debug_mode):
            print("Store Data in : %s" % abs_path)

    def loadFile(self, relative_path, debug_mode = False):
        
        # Get abs file path
        abs_path = self.current_path + relative_path

        if(debug_mode):
            print("Load Data from : %s" % abs_path)
        
        if self.checkFileExist(relative_path):
            
            # Open and read file
            with open(abs_path, 'rb') as f:
                data = f.read()

            if(debug_mode):
                print("Data : %s" % data)

            return data

        if(debug_mode):
            print("File not exist.")
        
    def checkFileExist(self, relative_path):
        
        # Get abs file path
        abs_path = self.current_path + relative_path
        
        if not os.path.exists(os.path.dirname(abs_path)):
            return False
        else:
            return True



######################################################
# Testing
######################################################

# mSecureDB = SecureDB(db_path = '')
# print('+++ Load SecureDB +++ \n')


# path = '/hello_file.txt'
# data = b'abcd\n'
# mSecureDB.storeFile(path, data, debug_mode = True)
# print()

# path = '/hello_file.txt'
# mSecureDB.loadFile(path, debug_mode = True)
# print()


# mSecureDB.loadSecureDB(debug_mode = True)

