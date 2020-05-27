package io.spring.initializr.web.VO;

public class KubernetesRequest {

	private String name;
	private String namespace;
	private String noOfReplicas;
	private String imageUrl;
	private String port;
	private String targetPort;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNamespace() {
		return namespace;
	}
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	public String getNoOfReplicas() {
		return noOfReplicas;
	}
	public void setNoOfReplicas(String noOfReplicas) {
		this.noOfReplicas = noOfReplicas;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getTargetPort() {
		return targetPort;
	}
	public void setTargetPort(String targetPort) {
		this.targetPort = targetPort;
	}
	
	

}
