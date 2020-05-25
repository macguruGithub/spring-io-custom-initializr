package io.spring.initializr.web.VO;

import java.util.Map;

public class EnvironmentTypeRequest {
	// private List<String> envTypeList;
	private Map<String, LogLevels> envTypeList;
	private String applicationName;
	private String port;

	public EnvironmentTypeRequest setEnvTypeList(Map<String, LogLevels> envTypeList) {

		this.envTypeList = envTypeList;
		return this;

	}

	public Map<String, LogLevels> getEnvTypeList() {
		return this.envTypeList;
	}

	public EnvironmentTypeRequest setApplicationName(String applicationName) {
		this.applicationName = applicationName;
		return this;
	}

	public EnvironmentTypeRequest setPort(String port) {
		this.port = port;
		return this;
	}

	public String getApplicationName() {
		return this.applicationName;
	}

	public String getPort() {
		return this.port;
	}

	@Override
	public String toString() {
		return "EnvironmentTypeRequest [envTypeList=" + envTypeList + ", applicationName=" + applicationName + ", port="
				+ port + "]";
	}

}
