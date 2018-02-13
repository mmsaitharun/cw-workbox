package oneapp.incture.workbox.consumers.dao;

import javax.persistence.EntityManager;

import oneapp.incture.workbox.consumers.dto.BPMCustomAttributeDto;
import oneapp.incture.workbox.consumers.entity.BPMCustomAttributeDo;
import oneapp.incture.workbox.poadapter.dao.BaseDao;
import oneapp.incture.workbox.util.ExecutionFault;
import oneapp.incture.workbox.util.InvalidInputFault;
import oneapp.incture.workbox.util.NoResultFault;
import oneapp.incture.workbox.util.ServicesUtil;

/**
 * The <code>TaskOwnersDao</code> converts Do to Dto and vice-versa <code>Data
 * Access Objects <code>
 * 
 * @author INC00609
 * @version 1.0
 * @since 2017-22-09
 */
public class BPMCustomAttributeDao extends BaseDao<BPMCustomAttributeDo, BPMCustomAttributeDto> {

	public BPMCustomAttributeDao(EntityManager entityManager) {
		super(entityManager);
	}

	@Override
	public BPMCustomAttributeDto exportDto(BPMCustomAttributeDo fromDo) {
		
		BPMCustomAttributeDto outputDto = new BPMCustomAttributeDto();

		if (!ServicesUtil.isEmpty(fromDo.getCustomId()))
			outputDto.setCustomId(fromDo.getCustomId().trim());
		if (!ServicesUtil.isEmpty(fromDo.getInstanceId()))
			outputDto.setInstanceId(fromDo.getInstanceId().trim());
		if (!ServicesUtil.isEmpty(fromDo.getProcessInstanceId()))
			outputDto.setProcessInstanceId(fromDo.getProcessInstanceId().trim());
		if (!ServicesUtil.isEmpty(fromDo.getMaterial()))
			outputDto.setMaterial(fromDo.getMaterial().trim());
		if (!ServicesUtil.isEmpty(fromDo.getInvoiceNo()))
			outputDto.setInvoiceNo(fromDo.getInvoiceNo().trim());
		if (!ServicesUtil.isEmpty(fromDo.getPlant()))
			outputDto.setPlant(fromDo.getPlant().trim());
		if (!ServicesUtil.isEmpty(fromDo.getVendorId()))
			outputDto.setVendorId(fromDo.getVendorId().trim());

		return outputDto;
	}

	@Override
	protected BPMCustomAttributeDo importDto(BPMCustomAttributeDto fromDto)
			throws InvalidInputFault, ExecutionFault, NoResultFault {
		
		BPMCustomAttributeDo outputDo = new BPMCustomAttributeDo();

		if (!ServicesUtil.isEmpty(fromDto.getCustomId()))
			outputDo.setCustomId(fromDto.getCustomId().trim());
		if (!ServicesUtil.isEmpty(fromDto.getInstanceId()))
			outputDo.setInstanceId(fromDto.getInstanceId().trim());
		if (!ServicesUtil.isEmpty(fromDto.getProcessInstanceId()))
			outputDo.setProcessInstanceId(fromDto.getProcessInstanceId().trim());
		if (!ServicesUtil.isEmpty(fromDto.getMaterial()))
			outputDo.setMaterial(fromDto.getMaterial().trim());
		if (!ServicesUtil.isEmpty(fromDto.getInvoiceNo()))
			outputDo.setInvoiceNo(fromDto.getInvoiceNo().trim());
		if (!ServicesUtil.isEmpty(fromDto.getPlant()))
			outputDo.setPlant(fromDto.getPlant().trim());
		if (!ServicesUtil.isEmpty(fromDto.getVendorId()))
			outputDo.setVendorId(fromDto.getVendorId().trim());

		return outputDo;
	}

	public String createAttrInstance(BPMCustomAttributeDto dto) {
		//	System.err.println("[PMC][TaskCustomAttributeDao][createAttrInstance]initiated with " + dto);
		try {
			create(dto);
			return "SUCCESS";
		} catch (Exception e) {
			System.err.println("[PMC][TaskCustomAttributeDao][createAttrInstance][error] " + e.getMessage());
		}
		return "FAILURE";
	}

	public String updateAttrInstance(BPMCustomAttributeDto dto) {
		//	System.err.println("[PMC][TaskCustomAttributeDao][updateAttrInstance]initiated with " + dto);
		try {
			update(dto);
			return "SUCCESS";
		} catch (Exception e) {
			System.err.println("[PMC][TaskCustomAttributeDao][updateAttrInstance][error] " + e.getMessage());
		}
		return "FAILURE";

	}



	/*	@SuppressWarnings("unchecked")
	public String allAttrInstance() {
		//	System.err.println("[PMC][TaskCustomAttributeDao][allAttrInstance]initiated");
		Query query = this.getEntityManager().createQuery("select te from TaskCustomAttributeDo te");
		List<BPMCustomAttributeDo> processDos = (List<BPMCustomAttributeDo>) query.getResultList();
		int i = 0;
		try {
			for (BPMCustomAttributeDo entity : processDos) {
				System.err.println("[PMC][TaskCustomAttributeDao][allAttrInstance][i]"+i+"[entity]" +entity);
				//	delete(exportDto(entity));
				i++;
			}
			return "SUCCESS";
		} catch (Exception e) {
			System.err.println("[PMC][TaskCustomAttributeDao][allAttrInstance][error] " + e.getMessage());
		}
		return "FAILURE";
	}*/

	/*@SuppressWarnings("unchecked")
	public BPMCustomAttributeDo getAttributeInstance(String instanceId) {
		Query query = this.getEntityManager()
				.createQuery("select te from TaskCustomAttributeDo te where te.instanceId =:instanceId");
		query.setParameter("instanceId", instanceId);
		List<BPMCustomAttributeDo> attributeDos = (List<BPMCustomAttributeDo>) query.getResultList();

		if (attributeDos.size() > 0) {
			for(BPMCustomAttributeDo entity : attributeDos){
				return entity;
			}
		} else {
			return null;
		}
		return null;
	}*/

	/*	public String updateAttributeInstance(String instanceId,String actions) {
		try{
			Query query = this.getEntityManager()
					.createQuery("update TaskCustomAttributeDo te set te.actions = '"+actions+"' where te.instanceId =:instanceId");
			query.setParameter("instanceId", instanceId);

			if (query.executeUpdate()> 0) {
				return "SUCCESS";
			}
		}catch(Exception e ){
			System.err.println("[PMC][ConsumeODataFacade][updateAttributeInstance] failed because"+e.getMessage());
		}
		return "FAILURE";
	}*/

}
