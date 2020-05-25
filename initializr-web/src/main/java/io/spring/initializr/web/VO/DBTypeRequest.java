package io.spring.initializr.web.VO;

public class DBTypeRequest {

	private String dbType;
	private String hostName;
	private String dbName;
	private String username;
	private String password;
	private String showsql;
	private String ddlauto;
	private String dialect;
	private String isHibernate;
	
	public String getDbType() {
		return dbType;
	}
	public void setDbType(String dbType) {
		this.dbType = dbType;
	}
	public String getHostName() {
		return hostName;
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	public String getDbName() {
		return dbName;
	}
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getShowsql() {
		return showsql;
	}
	public void setShowsql(String showsql) {
		this.showsql = showsql;
	}
	public String getDdlauto() {
		return ddlauto;
	}
	public void setDdlauto(String ddlauto) {
		this.ddlauto = ddlauto;
	}
	public String getDialect() {
		return dialect;
	}
	public void setDialect(String dialect) {
		this.dialect = dialect;
	}
	public String getIsHibernate() {
		return isHibernate;
	}
	public void setIsHibernate(String isHibernate) {
		this.isHibernate = isHibernate;
	}
	
	@Override
	public String toString() {
		return "DBTypeRequest [dbType=" + dbType + ", hostName=" + hostName + ", dbName=" + dbName + ", username="
				+ username + ", password=" + password + ", showsql=" + showsql + ", ddlauto=" + ddlauto + ", dialect="
				+ dialect + ", isHibernate=" + isHibernate + "]";
	}
	
}
