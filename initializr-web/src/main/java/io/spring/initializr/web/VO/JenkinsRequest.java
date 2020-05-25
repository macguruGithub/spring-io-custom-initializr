package io.spring.initializr.web.VO;

public class JenkinsRequest {

	private String apiName;
	private String ocirPassword;
	private String ocirUsername;
	private String ocirRepository;
	private String ocirRegistry;
	private String buildTag;
	
	
	public String getApiName() {
		return apiName;
	}
	public void setApiName(String apiName) {
		this.apiName = apiName;
	}
	public String getOcirPassword() {
		return ocirPassword;
	}
	public void setOcirPassword(String ocirPassword) {
		this.ocirPassword = ocirPassword;
	}
	public String getOcirUsername() {
		return ocirUsername;
	}
	public void setOcirUsername(String ocirUsername) {
		this.ocirUsername = ocirUsername;
	}
	public String getOcirRepository() {
		return ocirRepository;
	}
	public void setOcirRepository(String ocirRepository) {
		this.ocirRepository = ocirRepository;
	}
	public String getOcirRegistry() {
		return ocirRegistry;
	}
	public void setOcirRegistry(String ocirRegistry) {
		this.ocirRegistry = ocirRegistry;
	}
	public String getBuildTag() {
		return buildTag;
	}
	public void setBuildTag(String buildTag) {
		this.buildTag = buildTag;
	}
	
	
	
}
