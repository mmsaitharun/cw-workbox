package oneapp.incture.workbox.consumers.dto;

import java.util.UUID;

import javax.xml.bind.annotation.XmlRootElement;

import oneapp.incture.workbox.pmc.dto.BaseDto;
import oneapp.incture.workbox.util.EnOperation;
import oneapp.incture.workbox.util.InvalidInputFault;

/**
 * <h1>TaskCustomAttributeDto Class Implementation</h1> No validation parameters are
 * enforced
 * 
 * @author INC00609
 * @version 1.0
 * @since 2017-22-09
 */
@XmlRootElement
public class BPMCustomAttributeDto extends BaseDto {

	private String customId= UUID.randomUUID().toString().replaceAll("-", "");
	private String processInstanceId;
	private String instanceId;
	private String vendorId;
	private String invoiceNo;
	private String plant;
	private String material;
	
	
	
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	public String getVendorId() {
		return vendorId;
	}
	public void setVendorId(String vendorId) {
		this.vendorId = vendorId;
	}
	public String getInvoiceNo() {
		return invoiceNo;
	}
	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}
	public String getPlant() {
		return plant;
	}
	public void setPlant(String palnt) {
		this.plant = palnt;
	}
	public String getMaterial() {
		return material;
	}
	public void setMaterial(String material) {
		this.material = material;
	}
	

	public String getCustomId() {
		return customId;
	}
	public void setCustomId(String customId) {
		this.customId = customId;
	}
	
	public String getInstanceId() {
		return instanceId;
	}
	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}
	


	@Override
	public String toString() {
		return "BPMCustomAttributeDto [customId=" + customId + ", processInstanceId=" + processInstanceId
				+ ", instanceId=" + instanceId + ", vendorId=" + vendorId + ", invoiceNo=" + invoiceNo + ", palnt="
				+ plant + ", material=" + material + "]";
	}
	
	@Override
	public Boolean getValidForUsage() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void validate(EnOperation enOperation) throws InvalidInputFault {
		// TODO Auto-generated method stub

	}
}
