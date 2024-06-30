# If this library is not installed, don't install ntcore but pyntcore.
import ntcore
import sys
import time

CONNECTION_TIMEOUT_SECONDS = 30
LOOPS_COOLDOWN_SECONDS = 0.1


def get_network_table(ip: str, client_name: str):
    network_table_instance = ntcore.NetworkTableInstance.getDefault()

    print("Setting up NetworkTables client")
    network_table_instance.startClient4(client_name)
    network_table_instance.setServer(ip)
    network_table_instance.startDSClient()

    print("Waiting for connection to NetworkTables server...")
    started_time = time.time()
    while not network_table_instance.isConnected():
        # terminate client and program if it takes to long to connect
        if time.time() - started_time > CONNECTION_TIMEOUT_SECONDS:
            print("Didn't connect to network tables. Terminating...")
            terminate_network_table_instance(network_table_instance, client_name)
            sys.exit()
        time.sleep(LOOPS_COOLDOWN_SECONDS)

    print("Connected {} to NetworkTables server".format(client_name))
    return network_table_instance


def terminate_network_table_instance(network_table_instance: ntcore.NetworkTableInstance, client_name: str):
    print("Terminating client named: " + client_name)
    ntcore.NetworkTableInstance.destroy(network_table_instance)
