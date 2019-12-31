# Copyright 2018 Wirepas Ltd. All Rights Reserved.
#
# See file LICENSE.txt for full license details.
#
import argparse

from wirepas_gateway.dbus.dbus_client import BusClient



class SinkConfigurator(BusClient):
    """
    Simple class to configure a sink
    """
    def __init__(self, **kwargs):
        super(SinkConfigurator, self).__init__(**kwargs)


    def configure(self, sink_name, node_address, node_role, network_address, network_channel, start):
        sink = self.sink_manager.get_sink(sink_name)
        if sink is None:
            print("Cannot retrieve sink object")
            return

        # Do the actual configuration
        config = {}
        if node_address is not None:
            config['node_address'] = node_address
        if node_role is not None:
            config['node_role'] = 17
        if network_address is not None:
            config['network_address'] = network_address
        if network_channel is not None:
            config['network_channel'] = network_channel
        if start is not None:
            config["started"] = start

        ret = sink.write_config(config)
        print("Configuration done with result = {}".format(ret))


def main(log_name='configure_node'):
    parser = argparse.ArgumentParser(fromfile_prefix_chars='@')
    parser.add_argument('-n',
                        '--node_address',
                        type=int,
                        help="Node address")

    parser.add_argument('-r',
                        '--node_role',
                        default=17, # LL Sink
                        type=int,
                        help="Node role")

    parser.add_argument('-N',
                        '--network_address',
                        type=int,
                        help="Network address")

    parser.add_argument('-c',
                        '--network_channel',
                        type=int,
                        help="Network channel")

    parser.add_argument('-s',
                        '--sink_name',
                        type=str,
                        help="Sink name")

    parser.add_argument('-S',
                        '--start',
                        type=bool,
                        help="Start the sink after configuration")

    args = parser.parse_args()

    sink_configurator = SinkConfigurator()

    sink_configurator.configure(
        node_address=args.node_address,
        node_role=args.node_role,
        network_address=args.network_address,
        network_channel=args.network_channel,
        sink_name=args.sink_name,
        start=args.start)


if __name__ == "__main__":
    main()
