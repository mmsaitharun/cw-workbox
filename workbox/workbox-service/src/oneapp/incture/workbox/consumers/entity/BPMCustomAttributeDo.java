package oneapp.incture.workbox.consumers.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import oneapp.incture.workbox.pmc.entity.BaseDo;

/**
 * Entity implementation class for Entity: TaskCustomAttributeDo
 *
 */
@Entity
@Table(name = "BPM_CUST_ATTR")
public class BPMCustomAttributeDo implements BaseDo, Serializable {

	
	private static final long serialVersionUID = 1L;

	public BPMCustomAttributeDo() {
		super();
	}

	@Id
	@Column(name = "CUSTOM_ID", length = 50)
	private String customId;

	@Column(name = "TASK_ID", length = 64 )
	private String instanceId;
	
	@Column(name = "PRC_INST_ID", length = 64 )
	private String processInstanceId;

	@Column(name = "VENDOR_ID", length = 100)
	private String vendorId;

	@Column(name = "INVOICE_NO", length = 100)
	private String invoiceNo;
	
	@Column(name = "PLANT", length = 100)
	private String plant;
	
	@Column(name = "MATERIAL", length = 100)
	private String material;
	
	@Override
	public String toString() {
		return "BPMCustomAttributeDo [customId=" + customId + ", instanceId=" + instanceId + ", processInstanceId="
				+ processInstanceId + ", vendorId=" + vendorId + ", invoiceNo=" + invoiceNo + ", palnt=" + plant
				+ ", material=" + material + "]";
	}

	public String getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
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

	@Override
	public Object getPrimaryKey() {
		// TODO Auto-generated method stub
		return customId;
	}



}
