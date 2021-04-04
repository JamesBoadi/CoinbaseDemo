# CoinBase Demo Application

A simple java application that subscribes to CoinBase Pro WebSocket Feed

## Installation

Extract the zip to a directory of your choice

Create a new workspace or open an existing one and go to file, and click Import -> General -> Existing Projects Into Workspace

Select the CoinBase folder then the demo folder from the directory you extracted it to CoinBase -> demo
and click finish

Make sure that the existing packages do not conflict with the packages inside the project

Go to Run Configurations by right clicking on the project or the run button,
then go to Java application by double clicking, creating a new configuration.

Ensure that the default project is demo and the Main class is Client


## Run

Once imported, you can run the application from the terminal, the Main class is Client.
You can enter an instrument and the feed will display bids and asks if a price change occurs.

Currently you can only subscribe to one instrument per session, if you want to subscribe to a different instrument, terminate the current session by pressing Ctrl+C or the stop debugging button on the terminal and start again.

## Requirements

To run this application, the following prerequisites should already be present

  * Spring Boot v2.4.4
  * JDK 8
  * Eclipse IDE

## License
No License
