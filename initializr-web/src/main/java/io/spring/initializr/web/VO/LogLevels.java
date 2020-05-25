package io.spring.initializr.web.VO;

public class LogLevels {

	private String contextPath;
	private String logLevel;
	
	public String getContextPath() {
		return contextPath;
	}
	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}
	public String getLogLevel() {
		return logLevel;
	}
	public void setLogLevel(String logLevel) {
		this.logLevel = logLevel;
	}
	@Override
	public String toString() {
		return "LogLevels [contextPath=" + contextPath + ", logLevel=" + logLevel + "]";
	}

}
