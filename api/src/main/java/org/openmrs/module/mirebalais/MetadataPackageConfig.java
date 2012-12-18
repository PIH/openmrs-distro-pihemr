package org.openmrs.module.mirebalais;

import org.openmrs.module.metadatasharing.ImportMode;

public class MetadataPackageConfig {
	
	private String filenameBase;
	
	private String groupUuid;
	
	private Integer version;
	
	private ImportMode importMode;
	
	public MetadataPackageConfig(String filenameBase, String groupUuid, Integer version, ImportMode importMode) {
		this.filenameBase = filenameBase;
		this.groupUuid = groupUuid;
		this.version = version;
		this.importMode = importMode;
	}
	
	public String getFilenameBase() {
		return filenameBase;
	}
	
	public void setFilenameBase(String filenameBase) {
		this.filenameBase = filenameBase;
	}
	
	public String getGroupUuid() {
		return groupUuid;
	}
	
	public void setGroupUuid(String groupUuid) {
		this.groupUuid = groupUuid;
	}
	
	public ImportMode getImportMode() {
		return importMode;
	}
	
	public void setImportMode(ImportMode importMode) {
		this.importMode = importMode;
	}
	
	public Integer getVersion() {
		return version;
	}
	
	public void setVersion(Integer version) {
		this.version = version;
	}
	
}
