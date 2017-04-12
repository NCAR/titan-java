///////////////////////////////////////////////////////////////////////
//
// RdasCommParams
//
// RDAS Communication parameters
//
// Mike Dixon
//
// Feb 2003
//
////////////////////////////////////////////////////////////////////////

package edu.ucar.rap.titan.RdasControl;

import edu.ucar.rap.jrp.*;

public class RdasCommParams extends CollectionParameter
	    
{

    public BooleanParameter connectAtStartup;
    public StringParameter host;
    public IntegerParameter port;
    public StringParameter relayHost;
    public IntegerParameter relayPort;
    public BooleanParameter viaRelay;
    public DoubleParameter timeBetweenRelayBeams;
    public BooleanParameter acceptRemoteCommands;
    public IntegerParameter maxDataQueueSize;
    public IntegerParameter maxCommandQueueSize;
    public IntegerParameter connectDelay;
    public IntegerParameter commandDelay;
    public BooleanParameter bigEndian;
    
    // constructor
    
    public RdasCommParams(String name, String label, int depth)
    {
	super(name, label, depth);
	
	connectAtStartup = new BooleanParameter("connectAtStartup");
	connectAtStartup.setLabel("Connect at startup");
	connectAtStartup.setDescription("Connect to RDAS or relay at startup.");
	connectAtStartup.setInfo("If TRUE, the socket connection to either RDAS or RELAY will be set up at startup. The connection can be open or closed using the COMMS menu.");
	connectAtStartup.setValue(true);

	host = new StringParameter("host");
	host.setLabel("RDAS host");
	host.setDescription("IP name of RDAS host");
	host.setInfo("This can be either a hostname or IP address.");
	host.setValue("localhost");
	
	port = new IntegerParameter("port");
	port.setLabel("RDAS port");
	port.setDescription("TCP/IP port for RDAS server");
 	port.setValue(10000);
	
	relayHost = new StringParameter("relayHost");
	relayHost.setLabel("RDAS relay host");
	relayHost.setDescription("IP name of host of RDAS relay service");
	relayHost.setInfo("This can be either a hostname or IP address.");
	relayHost.setValue("localhost");
	
	relayPort = new IntegerParameter("relayPort");
	relayPort.setLabel("RDAS relay port");
	relayPort.setDescription("TCP/IP port for RDAS relay service");
 	relayPort.setValue(11000);

	viaRelay = new BooleanParameter("viaRelay");
	viaRelay.setLabel("Contact relay server?");
	viaRelay.setDescription("Option to contact relay host.");
	viaRelay.setInfo("If FALSE, contacts RDAS directly. If true, contacts relay host");
	viaRelay.setValue(false);

	timeBetweenRelayBeams = new DoubleParameter("timeBetweenRelayBeams");
	timeBetweenRelayBeams.setLabel("Time between beams (secs)");
	timeBetweenRelayBeams.setDescription("Time between beams when getting data via relay");
	timeBetweenRelayBeams.setInfo("Normally, every available beam is sent from the relay server to the client. However, if network speed is limited this may not be practical. If you set this parameter to a value greater than 0, in seconds, beams will only be sent at this interval. Note that the value does not need to be an integer - a value of 0.5 means a beam will be sent every half second. The default is 0, which means every beam will be sent.");
 	timeBetweenRelayBeams.setValue(0.0);

	acceptRemoteCommands = new BooleanParameter("acceptRemoteCommands");
	acceptRemoteCommands.setLabel("Accept remote commands?");
	acceptRemoteCommands.setDescription("Accept commands in relay mode.");
	acceptRemoteCommands.setInfo("Only applies to relay mode. If TRUE, will accept commands from a remote instance of RdasControl. If FALSE, will not accept remote commands. If FALSE, the only way to shut down the radar is to terminate the relay application.");
	acceptRemoteCommands.setValue(true);

	maxDataQueueSize = new IntegerParameter("maxDataQueueSize");
	maxDataQueueSize.setLabel("Max data queue size");
	maxDataQueueSize.setDescription("Max size of incoming radar data queue");
	maxDataQueueSize.setInfo
	    ("This is the maximum number of messages which" +
	     " can be waiting for attention at any time." +
	     " Limiting the size prevents memory usage from" +
	     " growing in an uncontrolled manner if an error" +
	     " occurs within the program.");
 	maxDataQueueSize.setValue(1000);
	
	maxCommandQueueSize = new IntegerParameter("maxCommandQueueSize");
	maxCommandQueueSize.setLabel("Max command queue size");
	maxCommandQueueSize.setDescription("Max size of command queue");
	maxCommandQueueSize.setInfo
	    ("This is the maximum number of commands which" +
	     " can be waiting to be sent at any time.");
 	maxCommandQueueSize.setValue(100);
	
	connectDelay = new IntegerParameter("connectDelay");
	connectDelay.setLabel("Connect delay (msecs)");
	connectDelay.setDescription("Delay after connection established - msecs");
	connectDelay.setInfo("This is the delay after the connection is established" +
			     " before any commands are sent to RDAS.");
 	connectDelay.setValue(500);

	commandDelay = new IntegerParameter("commandDelay");
	commandDelay.setLabel("Command delay (msecs)");
	commandDelay.setDescription("Delay before message is sent - msecs");
	commandDelay.setInfo("For debugging. You can set this to a value greater" +
			     " than 0 to cause a delay before each command is sent" +
			     " to RDAS.");
 	commandDelay.setValue(100);

	bigEndian = new BooleanParameter("bigEndian");
	bigEndian.setLabel("Big-endian data?");
	bigEndian.setDescription("Is data in BE - network byte order - format?");
	bigEndian.setInfo("If FALSE, data is in little-endian format.");
	bigEndian.setValue(true);

	// add the parameters to the list
	
	add(connectAtStartup);
	add(host);
	add(port);
	add(relayHost);
	add(relayPort);
	add(viaRelay);
	add(timeBetweenRelayBeams);
	add(acceptRemoteCommands);
	add(maxDataQueueSize);
	add(maxCommandQueueSize);
	add(connectDelay);
	add(commandDelay);
	add(bigEndian);
	
	// copy the values to the defaults
	
	setDefaultFromValue();

    }

}
