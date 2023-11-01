import sys
import ticket_module
import ticket
import key_serialization

######################################################
# Setup
######################################################

# 2 ticket modules, seperately in Private Autenticator & IoT Device
print()
private_authenticator_1 = ticket_module.TicketModule(module_type = ticket.PRIVATE_ANTHENTICATOR, module_name = "private_authenticator_1", db_path = '/secure_db/private_authenticator_1')
private_authenticator_2 = ticket_module.TicketModule(module_type = ticket.PRIVATE_ANTHENTICATOR, module_name = "private_authenticator_2", db_path = '/secure_db/private_authenticator_2')
iot_device_1 = ticket_module.TicketModule(module_type = ticket.IOT_DEVICE, module_name = "iot_device_1", db_path = '/secure_db/iot_device_1')
# iot_device_2 = ticket_module.TicketModule(module_type = ticket.IOT_DEVICE, module_name = "iot_device_2", db_path = '/secure_db/iot_device_2')
print()

# To-Do: Different features in initialization_mode & ticket_mode
if(not(private_authenticator_1.ticket_mode and private_authenticator_2.ticket_mode and iot_device_1.ticket_mode)):
	sys.exit("All devices should be initiziled 1st...")



######################################################
# After Initialization...
######################################################
pub_private_authenticator_1 = private_authenticator_1.device_pub_key_str
priv_private_authenticator_1 = private_authenticator_1.device_priv_key

pub_private_authenticator_2 = private_authenticator_2.device_pub_key_str
priv_private_authenticator_2 = private_authenticator_2.device_priv_key

pub_iot_device_1 = iot_device_1.device_pub_key_str
priv_iot_device_1 = iot_device_1.device_priv_key





