package com.dachlab.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Sim card and gateway configuration bean. Get the parameters from
 * application-xxx.properties
 * 
 * @author dcharles
 */
@Component("simProperties")
@ConfigurationProperties(prefix = "sim")
public class SimProperties implements ISimProperties {
	private String gatewayManufacturer;
	private String gatewayName;
	private String gatewayPort;
	private int gatewayBaudRate;
	private String smscNumber;
	private String simPin;
	private String forwardToNumber;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.picontroller.home.properties.ISimProperties#getGatewayName()
	 */
	@Override
	public String getGatewayName() {
		return gatewayName;
	}

	public void setGatewayName(String gatewayName) {
		this.gatewayName = gatewayName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.picontroller.home.properties.ISimProperties#getGatewayPort()
	 */
	@Override
	public String getGatewayPort() {
		return gatewayPort;
	}

	public void setGatewayPort(String gatewayPort) {
		this.gatewayPort = gatewayPort;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.picontroller.home.properties.ISimProperties#getGatewayBaudRate()
	 */
	@Override
	public int getGatewayBaudRate() {
		return gatewayBaudRate;
	}

	public void setGatewayBaudRate(int gatewayBaudRate) {
		this.gatewayBaudRate = gatewayBaudRate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.picontroller.home.properties.ISimProperties#getSmscNumber()
	 */
	@Override
	public String getSmscNumber() {
		return smscNumber;
	}

	public void setSmscNumber(String smscNumber) {
		this.smscNumber = smscNumber;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.picontroller.home.properties.ISimProperties#getGatewayManufacturer()
	 */
	@Override
	public String getGatewayManufacturer() {
		return gatewayManufacturer;
	}

	public void setGatewayManufacturer(String gatewayManufacturer) {
		this.gatewayManufacturer = gatewayManufacturer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.picontroller.home.properties.ISimProperties#getSimPin()
	 */
	@Override
	public String getSimPin() {
		return simPin;
	}

	public void setSimPin(String simPin) {
		this.simPin = simPin;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.picontroller.home.properties.ISimProperties#getForwardToNumber()
	 */
	@Override
	public String getForwardToNumber() {
		return forwardToNumber;
	}

	public void setForwardToNumber(String forwardToNumber) {
		this.forwardToNumber = forwardToNumber;
	}

}
