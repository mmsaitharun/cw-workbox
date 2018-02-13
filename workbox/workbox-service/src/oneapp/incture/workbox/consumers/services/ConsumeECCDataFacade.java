package oneapp.incture.workbox.consumers.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import oneapp.incture.workbox.consumers.dao.ECCCustomAttributeDao;
import oneapp.incture.workbox.consumers.dto.ECCCustomAttributeDto;
import oneapp.incture.workbox.consumers.dto.UserDetailDto;
import oneapp.incture.workbox.consumers.dto.WorKBoxDetailDto;
import oneapp.incture.workbox.consumers.dto.WorkboxRequestDto;
import oneapp.incture.workbox.consumers.entity.ECCCustomAttributeDo;
import oneapp.incture.workbox.consumers.util.ODataServicesUtil;
import oneapp.incture.workbox.pmc.services.EntityManagerProviderLocal;
import oneapp.incture.workbox.poadapter.dao.ProcessEventsDao;
import oneapp.incture.workbox.poadapter.dao.TaskEventsDao;
import oneapp.incture.workbox.poadapter.dao.TaskOwnersDao;
import oneapp.incture.workbox.poadapter.dto.ProcessEventsDto;
import oneapp.incture.workbox.poadapter.dto.ResponseMessage;
import oneapp.incture.workbox.poadapter.dto.TaskEventsDto;
import oneapp.incture.workbox.poadapter.dto.TaskOwnersDto;
import oneapp.incture.workbox.poadapter.entity.TaskEventsDo;
import oneapp.incture.workbox.poadapter.entity.TaskOwnersDo;
import oneapp.incture.workbox.util.PMCConstant;
import oneapp.incture.workbox.util.ServicesUtil;

/**
 * Session Bean implementation class ConsumeODataFacade
 */
@Stateless
public class ConsumeECCDataFacade implements ConsumeECCDataFacadeLocal {

	@EJB
	EntityManagerProviderLocal em;


	public ConsumeECCDataFacade() {
	}

	@Override
	public ResponseMessage getDataFromECC(String processor,String scode) {
		//	System.err.println("[PMC][ConsumeODataFacade][Xpath][Xpath][getDataFromECC] method invoked with [processor]" + processor);
		ResponseMessage responseMessage = new ResponseMessage();
		responseMessage.setMessage("Data Consumed Successfully");
		responseMessage.setStatus("SUCCESS");
		responseMessage.setStatusCode("0");
		try{
			//	final long startTime = System.nanoTime();
			
			
			NodeList nodeList = ODataServicesUtil.xPathOdata(PMCConstant.ECC_URL+"TaskCollection", PMCConstant.APPLICATION_XML, PMCConstant.HTTP_METHOD_GET, "/feed /entry ",processor,scode);
			if(!ServicesUtil.isEmpty(nodeList)){
				if(nodeList.getLength()>0){
					List<String> instanceList =  new ArrayList<String>();
					int i;
					for (i = 0; i < nodeList.getLength(); i++) {
						Node nNode = nodeList.item(i);
						if (nNode.getNodeType() == Node.ELEMENT_NODE) {
							String returnedValue = convertToDto(nNode ,processor ,scode);
							if (returnedValue.equals("FAILURE")) {
								responseMessage.setMessage("Data consumption failed");
								responseMessage.setStatus("FAILURE");
								responseMessage.setStatusCode("1");
								break;
							}
							else if(!returnedValue.equals("FAILURE") && !returnedValue.equals("SUCCESS")){
								instanceList.add(returnedValue);	
							}
						}
					}
					/*if (new TaskOwnersDao(em.getEntityManager()).deleteNonExistingTasks(instanceList,processor).equals("FAILURE")) {
						responseMessage.setMessage("Data consumption failed as it failed to delete owners");
						responseMessage.setStatus("FAILURE");
						responseMessage.setStatusCode("1");
						return responseMessage;
					}*/
					//	final long duration = System.nanoTime() - startTime;
					//	System.err.println("[PMC][ConsumeODataFacade][Xpath][getDataFromECC][no of entries]" + i+ "[timeTaken]"+ duration);
				}
			}
		}
		catch (Exception e) {
			System.err.println("[PMC][ConsumeODataFacade][Xpath][getDataFromECC][error] " + e.getMessage());
			responseMessage.setMessage("Data Consumption failed because - " + e.getMessage());
			responseMessage.setStatus("FAILURE");
			responseMessage.setStatusCode("1");
		}

		return responseMessage;
	}

	private String convertToDto(Node nNode, String processor ,String scode) {
		Element eElement = (Element) nNode;
		Element mproperties = (Element) eElement.getElementsByTagName("m:properties").item(0);
		String sapOrigin = mproperties.getElementsByTagName("d:SAP__Origin").item(0).getTextContent();

		TaskEventsDto taskDto = new TaskEventsDto();
		TaskOwnersDto ownersDto = new TaskOwnersDto();
		ProcessEventsDto processDto = new ProcessEventsDto();
		ECCCustomAttributeDto attributeDto = new ECCCustomAttributeDto();

		String eventId =  mproperties.getElementsByTagName("d:InstanceID").item(0).getTextContent();
		taskDto.setEventId(eventId);
		ownersDto.setEventId(eventId);
		attributeDto.setInstanceId(eventId);
		attributeDto.setSapOrigin(sapOrigin);
		taskDto.setProcessId(processDto.getProcessId());
		attributeDto.setProcessInstanceId(mproperties.getElementsByTagName("d:TaskDefinitionID").item(0).getTextContent());
		processDto.setStartedByDisplayName( mproperties.getElementsByTagName("d:CreatedByName").item(0).getTextContent());
		processDto.setStartedByDisplayName(mproperties.getElementsByTagName("d:CreatedBy").item(0).getTextContent());
		taskDto.setDescription(mproperties.getElementsByTagName("d:Description").item(0).getTextContent());
		//			ownersDto.setTaskOwnerDisplayName(mproperties.getElementsByTagName("d:ForwardingUserName").item(0).getTextContent());
		//			ownersDto.setTaskOwner(mproperties.getElementsByTagName("d:ForwardingUser").item(0).getTextContent());
		taskDto.setCurrentProcessorDisplayName(mproperties.getElementsByTagName("d:ProcessorName").item(0).getTextContent());
		taskDto.setPriority(mproperties.getElementsByTagName("d:Priority").item(0).getTextContent());
		processDto.setStartedBy(mproperties.getElementsByTagName("d:CreatedBy").item(0).getTextContent());
		processDto.setName(mproperties.getElementsByTagName("d:TaskDefinitionName").item(0).getTextContent());
		taskDto.setProcessName(mproperties.getElementsByTagName("d:TaskDefinitionName").item(0).getTextContent());
		taskDto.setForwardedBy(mproperties.getElementsByTagName("d:ForwardedUser").item(0).getTextContent());
		taskDto.setCurrentProcessor(mproperties.getElementsByTagName("d:Processor").item(0).getTextContent());
		attributeDto.setAttribute(mproperties.getElementsByTagName("d:TaskTitle").item(0).getTextContent());
		taskDto.setForwardedAt(dateParser(mproperties, "d:ForwardedOn"));
		attributeDto.setStartDeadLine(dateParser(mproperties, "d:StartDeadLine"));
		processDto.setStartedAt(dateParser(mproperties, "d:CreatedOn"));
		taskDto.setCreatedAt(dateParser(mproperties, "d:CreatedOn"));
		taskDto.setCompletionDeadLine(dateParser(mproperties, "d:CompletionDeadLine"));
		attributeDto.setExpiryDate(dateParser(mproperties, "d:ExpiryDate"));
		taskDto.setCompletedAt(dateParser(mproperties, "d:CompletedOn"));
		processDto.setCompletedAt(dateParser(mproperties, "d:CompletedOn"));


		if (mproperties.getElementsByTagName("d:IsEscalated").item(0).getTextContent().equals("true")) {
			attributeDto.setEscalated(true);
		} else {
			attributeDto.setEscalated(false);
		}


		String status = mproperties.getElementsByTagName("d:Status").item(0).getTextContent();
		if (!(status.equals("COMPLETED"))) {
			processDto.setStatus("INPROGRESS");
		} else {
			processDto.setStatus(status);
		}
		taskDto.setStatus(status);

		Element dTaskSupports = (Element) mproperties.getElementsByTagName("d:TaskSupports").item(0);	
		if(dTaskSupports.getElementsByTagName("d:Release").item(0).getTextContent().equals("true")){
			attributeDto.setActionList("Release");	
		}
		if(dTaskSupports.getElementsByTagName("d:Claim").item(0).getTextContent().equals("true")){
			if(!ServicesUtil.isEmpty(attributeDto.getActionList())){
				attributeDto.setActionList(attributeDto.getActionList()+",Claim");
			}
			else{
				attributeDto.setActionList("Claim");	
			}

		}
		if(dTaskSupports.getElementsByTagName("d:Forward").item(0).getTextContent().equals("true")){
			if(!ServicesUtil.isEmpty(attributeDto.getActionList())){
				attributeDto.setActionList(attributeDto.getActionList()+",Forward");
			}
			else{
				attributeDto.setActionList("Forward");	
			}

		}
		if(dTaskSupports.getElementsByTagName("d:Comments").item(0).getTextContent().equals("true")){
			if(!ServicesUtil.isEmpty(attributeDto.getActionList())){
				attributeDto.setActionList(attributeDto.getActionList()+",Comments");
			}
			else{
				attributeDto.setActionList("Comments");	
			}

		}
		/*
		 * UNCOMMENT TO DISPLAY ATTACHMENTS ALSO IN ACTIONS
		 * 
		 * if(dTaskSupports.getElementsByTagName("d:Attachments").item(0).getTextContent().equals("true")){
				if(!ServicesUtil.isEmpty(attributeDto.getActionList())){
					attributeDto.setActionList(attributeDto.getActionList()+",Attachments");
				}
				else{
					attributeDto.setActionList("Attachments");	
				}

			}
		 */
		ownersDto.setTaskOwnerDisplayName(processor);
		ownersDto.setTaskOwner(processor);
		if (!ServicesUtil.isEmpty(taskDto.getStatus()) && taskDto.getStatus().equals("RESERVED")) {
			ownersDto.setIsProcessed(true);
		} else {
			ownersDto.setIsProcessed(false);
		}
		List<TaskEventsDo> taskEventDos = new TaskEventsDao(em.getEntityManager()).checkIfTaskInstanceExists(taskDto.getEventId());
		if (!ServicesUtil.isEmpty(taskEventDos)) {
			for(TaskEventsDo taskEventDo : taskEventDos){
				taskDto.setProcessId(taskEventDo.getTaskEventsDoPK().getProcessId());
				processDto.setProcessId(taskEventDo.getTaskEventsDoPK().getProcessId());
			}
			//	System.err.println("[PMC][ConsumeODataFacade][Xpath][convertToDto][inUpdateInstance]");
			if(!updateInstance(processDto, taskDto, attributeDto).equals("SUCCESS")){
				return "FAILURE"; 
			}
		} else {
			//	System.err.println("[PMC][ConsumeODataFacade][Xpath][convertToDto][inCreateInstance]");
			if(!createInstance(processDto, taskDto,attributeDto).equals("SUCCESS")){
				return "FAILURE";
			}
		}
		if(!saveAndUpdateTaskOwners(ownersDto,taskDto.getStatus()).equals("SUCCESS")){
			return "FAILURE";
		}
		return taskDto.getEventId();
	}


	private Date dateParser(Element mproperties,String key){

		if(!ServicesUtil.isEmpty(mproperties.getElementsByTagName(key)))
			return ServicesUtil.resultTAsDate(mproperties.getElementsByTagName(key).item(0).getTextContent());
		return null;
	}

	private String createInstance(ProcessEventsDto processDto,TaskEventsDto taskDto,ECCCustomAttributeDto attributeDto){
		System.err.println("[PMC][ConsumeODataFacade][Xpath][createInstance][createProcessInstance] " + processDto
				+ "[createTaskInstance]" + taskDto 
				+ "[createTaskAttributeInstance]" + attributeDto+"[status]"+taskDto.getStatus());
		if (new ProcessEventsDao(em.getEntityManager()).createProcessInstance(processDto).equals("FAILURE")) {
			return "FAILURE";
		}
		if (new TaskEventsDao(em.getEntityManager()).createTaskInstance(taskDto).equals("FAILURE")) {
			return "FAILURE";
		}

		if(!(taskDto.getStatus().equals("COMPLETED"))){
			String actions = getDecisionOptions(attributeDto.getSapOrigin(),taskDto.getEventId());
			if(!actions.equals("FAILURE")){
				attributeDto.setActions(actions);
			}
			else{
				return "FAILURE";
			}
		}
		if (new ECCCustomAttributeDao(em.getEntityManager()).createAttrInstance(attributeDto).equals("FAILURE")) {
			return "FAILURE";
		}
		return "SUCCESS";
	}

	private String getDecisionOptions(String sapOrigin,String instanceId){

		String serviceUrl = PMCConstant.ECC_URL+"DecisionOptions?SAP__Origin='"+sapOrigin+"'&InstanceID='"+instanceId+"'";
		System.err.println("[PMC][ConsumeODataFacade][Xpath][getDecisionOptions] method invoked with [instanceId]" + instanceId+"[sapOrigin]"+sapOrigin+" [url]"+serviceUrl);

		try {
			String  actions = null;
			//ODataServicesUtil.readActions(serviceUrl, PMCConstant.APPLICATION_XML);
			return actions;

		} catch (Exception e) {
			System.err.println("[PMC][ConsumeODataFacade][getDecisionOptions][error]" + e.getMessage());
		}
		return "FAILURE";
	}

	/*   
	 *   This method  updates the instances of the ProcessEventsDto,  TaskEventsDto, ECCCustomAttributeDto
	 *   because they already exists in the db with the particular event id
	 */

	private String updateInstance(ProcessEventsDto processDto, TaskEventsDto taskDto,
			ECCCustomAttributeDto attributeDto) {

		if (new ProcessEventsDao(em.getEntityManager()).updateProcessInstance(processDto).equals("FAILURE")) {
			return "FAILURE";
		}
		if (new TaskEventsDao(em.getEntityManager()).updateTaskInstance(taskDto).equals("FAILURE")) {
			return "FAILURE";
		}

		/*   
		 *   Uncomment to update attributes also while calling the list service 


		ECCCustomAttributeDao attrdao =  new ECCCustomAttributeDao(em.getEntityManager());
		ECCCustomAttributeDo attrEntity =attrdao.getAttributeInstance(attributeDto.getInstanceId());
		//	System.err.println("[PMC][ConsumeODataFacade][Xpath][convertToDto][ECCCustomAttributeDo]" + attrEntity+"[attributeDto.getProcessInstanceId()]"+attributeDto.getProcessInstanceId());
		if(!ServicesUtil.isEmpty(attrEntity)){
			attributeDto.setCustomId(attrEntity.getCustomId());
			attributeDto.setActions(attrEntity.getActions());
			//System.err.println("update Dto : " + attributeDto);
			if (attrdao.updateAttrInstance(attributeDto).equals("FAILURE")) {
				return "FAILURE";
			}
		}
		 */
		return "SUCCESS";
	}

	/*   
	 *   This method  removes any records for which the event id is same but not the task owner  
	 *   updates record for which the event id and the task owner  are same
	 *   else it creates if the record doesnt exists 
	 *   in the Task Owners table 
	 */


	private String saveAndUpdateTaskOwners(TaskOwnersDto dto ,String status){
		System.err.println("[PMC][ConsumeODataFacade][Xpath][saveAndUpdateTaskOwners][TaskOwnersDto]"+dto+"[status]"+status);

		TaskOwnersDao dao = new TaskOwnersDao(em.getEntityManager());
		List<TaskOwnersDo> entities = dao.getOwnerInstances(dto.getEventId());
		boolean isExists = false;
		if(!ServicesUtil.isEmpty(entities)){
			for(TaskOwnersDo entity : entities){
				if(entity.getTaskOwnersDoPK().getEventId().equals(dto.getEventId())&&entity.getTaskOwnersDoPK().getTaskOwner().equals(dto.getTaskOwner())){
					dao.updateTaskOwnerInstance(dto);
					isExists = true;
				}
				else{
					if(dao.deleteInstance(entity).equals("FAILURE")){
						return "FAILURE";	
					}
				}
			}
		}
		if(!isExists){
			if (dao.createTaskOwnerInstance(dto).equals("FAILURE")) {
				return "FAILURE";
			}
		}
		return "SUCCESS";
	}

	/*   
	 *   This method  gets all the users to which a task can be forwarded to ( using xpath services )
	 */

	@Override
	public List<UserDetailDto> getUsers(WorkboxRequestDto requestDto) {
		List<UserDetailDto> instanceList= null;
		try{
			String url = PMCConstant.ECC_URL+"SearchUsers?SAP__Origin=%27"+requestDto.getSapOrigin()+"%27&SearchPattern=%27"+requestDto.getText()+"%27&MaxResults=100";
			NodeList nodeList = ODataServicesUtil.xPathOdata(url, PMCConstant.APPLICATION_XML, PMCConstant.HTTP_METHOD_GET, "/feed /entry ",requestDto.getUserId(),requestDto.getScode());
			if(nodeList.getLength()>0){
				instanceList =  new ArrayList<UserDetailDto>();
				int i;
				for (i = 0; i < nodeList.getLength(); i++) {
					Node nNode = nodeList.item(i);
					if (nNode.getNodeType() == Node.ELEMENT_NODE) {
						UserDetailDto userDetail = convertToUserDto(nNode);
						instanceList.add(userDetail);	
					}
				}
			}
		}
		catch (Exception e) {
			System.err.println("[PMC][ConsumeODataFacade][Xpath][getUsers][error] " + e.getMessage());
		}
		return instanceList;
	}

	/*   
	 *   This method  converts the nodes recieved from service which has the details of the user to UserDetailDto  
	 */

	private UserDetailDto convertToUserDto(Node nNode) {
		Element eElement = (Element) nNode;
		Element mproperties = (Element) eElement.getElementsByTagName("m:properties").item(0);
		UserDetailDto userDto = new UserDetailDto();
		userDto.setDepartment(mproperties.getElementsByTagName("d:Department").item(0).getTextContent());
		userDto.setDisplayName(mproperties.getElementsByTagName("d:DisplayName").item(0).getTextContent());
		userDto.setEmail(mproperties.getElementsByTagName("d:Email").item(0).getTextContent());
		userDto.setUniqueName(mproperties.getElementsByTagName("d:UniqueName").item(0).getTextContent());

		return userDto;
	}


	@Override
	public WorKBoxDetailDto getTaskDetails(WorkboxRequestDto requestDto) {

		WorKBoxDetailDto detailErrorDto = new WorKBoxDetailDto();
		detailErrorDto.setMessage(new ResponseMessage());
		detailErrorDto.getMessage().setStatus("FAILURE");
		detailErrorDto.getMessage().setStatusCode("1");

		try {
			NodeList nodeList = ODataServicesUtil.xPathOdata(PMCConstant.ECC_URL+"TaskCollection(SAP__Origin='"+ requestDto.getSapOrigin()+"',InstanceID='"+requestDto.getInstanceId()+"')/?$expand=Description,CustomAttributeData", PMCConstant.APPLICATION_XML, PMCConstant.HTTP_METHOD_GET, "/entry ",requestDto.getUserId(),requestDto.getScode());
			WorKBoxDetailDto detailDto = convertToDetailDto(nodeList);
			detailDto.setMessage(new ResponseMessage());
			detailDto.getMessage().setMessage("Details Fetched succesfully");
			detailDto.getMessage().setStatus("SUCCESS");
			detailDto.getMessage().setStatusCode("0");
			if (!ServicesUtil.isEmpty(detailDto)) {
				detailDto.setActionURL(PMCConstant.ECC_URL);
				ECCCustomAttributeDao attrdao =  new ECCCustomAttributeDao(em.getEntityManager());
				ECCCustomAttributeDo attrEntity =attrdao.getAttributeInstance(requestDto.getInstanceId());
				if (!ServicesUtil.isEmpty(attrEntity)&&!ServicesUtil.isEmpty(attrEntity.getActionList())){
					if(!ServicesUtil.isEmpty(attrEntity.getActions())){
						detailDto.setActionList(attrEntity.getActions()+","+detailDto.getActionList());
					}
					else{
						String actions = getDecisionOptions(requestDto.getSapOrigin(),requestDto.getInstanceId());
						if(!actions.equals("FAILURE") && !ServicesUtil.isEmpty(actions)){
							if (!new ECCCustomAttributeDao(em.getEntityManager()).updateAttributeInstance(requestDto.getInstanceId(),actions).equals("FAILURE")) {
								detailDto.setActionList(actions+","+detailDto.getActionList());
							}
						}
					}
				}

				return detailDto;
			}
		} catch (Exception e) {
			System.err.println("[PMC][ConsumeODataFacade][getTaskDetails][error] " + e.getMessage());
		}
		detailErrorDto.getMessage().setMessage("Failed to get the Details of this instance");	
		return detailErrorDto;


	}

	

	/*private static WorkBoxCommentsDto convertToCommentsDto(ODataEntry createdEntry) {
		Map<String, Object> properties = createdEntry.getProperties();
		Set<Entry<String, Object>> entries = properties.entrySet();
		WorkBoxCommentsDto dto = new WorkBoxCommentsDto();
		final DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for (Entry<String, Object> entry : entries) {
			String key = entry.getKey();
			if(!(ServicesUtil.isEmpty(key) && ServicesUtil.isEmpty(entry.getValue()))){
				if(key.equals("CreatedAt")){
					try {
						dto.setCommentedOn(dateFormatter.parse(ServicesUtil.calendarFormat((GregorianCalendar) entry.getValue())));
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
				else if(key.equals("Text")){
					dto.setCommentText((String) entry.getValue());
				}
				else if (key.equals("CreatedBy")) {
					dto.setCommentBy((String) entry.getValue());
				} 
				else if (key.equals("CreatedByName")) {
					dto.setCommentByName((String) entry.getValue());
				} 
				else if (key.equals("ID")) {
					dto.setCommentId((String) entry.getValue());
				} 
			}
		}
		return dto ;
	}*/

	/*private static WorkBoxAttachmentsDto convertToAttachmentsDto(ODataEntry createdEntry) {
		Set<Entry<String, Object>> entries = properties.entrySet();
		WorkBoxAttachmentsDto dto = new WorkBoxAttachmentsDto();
		final DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for (Entry<String, Object> entry : entries) {
			String key = entry.getKey();
			if(!(ServicesUtil.isEmpty(key) && ServicesUtil.isEmpty(entry.getValue()))){
				if(key.equals("CreatedAt")){
					try {
						dto.setAttachedOn(dateFormatter.parse(ServicesUtil.calendarFormat((GregorianCalendar) entry.getValue())));
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
				else if(key.equals("FileName")){
					dto.setFileName((String) entry.getValue());
				}
				else if (key.equals("CreatedBy")) {
					dto.setAttachBy((String) entry.getValue());
				} 
				else if (key.equals("CreatedByName")) {
					dto.setAttachByName((String) entry.getValue());
				} 
				else if (key.equals("ID")) {
					dto.setAttachId((String) entry.getValue());
				} 
				else if(key.equals("mime_type")){
					dto.setMimeType((String) entry.getValue());
				}
				else if(key.equals("FileSize")){
					dto.setFileSize((Integer) entry.getValue());
				}
				else if(key.equals("FileDisplayName")){
					dto.setFileDisplayName((String) entry.getValue());
				}
			}
		}
		return dto ;
	}*/

	private static WorKBoxDetailDto convertToDetailDto(NodeList nodeList) {
		
		WorKBoxDetailDto detailDto = new  WorKBoxDetailDto();
		for (int x = 0; x < nodeList.getLength(); x++) {
			Element eElement = (Element)  nodeList.item(x);
			NamedNodeMap mp = eElement.getAttributes();
			if( mp.getNamedItem("title") != null ){
				String title = mp.getNamedItem("title").getTextContent();
				if(title.equals("Description")){
					Element mproperties = (Element) eElement.getElementsByTagName("m:properties").item(0);
					detailDto.setDescription(mproperties.getElementsByTagName("d:Description").item(0).getTextContent());
					detailDto.setDescriptionAsHtml(mproperties.getElementsByTagName("d:DescriptionAsHtml").item(0).getTextContent());
				}
				else if(title.equals("CustomAttributeData")){
					NodeList customList =  eElement.getElementsByTagName("feed").item(0).getChildNodes();
					detailDto = convertToCustomAttributeDto( customList,  detailDto) ;
				}
			}
			else if(eElement.getNodeName().equals("m:properties")){
				Element taskSupports = (Element) eElement.getElementsByTagName("d:TaskSupports").item(0);
				detailDto = setAction(taskSupports,detailDto,"Confirm");
				detailDto = setAction(taskSupports,detailDto,"Forward");
				detailDto = setAction(taskSupports,detailDto,"Claim");
				detailDto = setAction(taskSupports,detailDto,"Release");
				detailDto = setAction(taskSupports,detailDto,"Comments");
			}
		}
		return detailDto;
	}

	private static WorKBoxDetailDto setAction(Element taskSupports,WorKBoxDetailDto detailDto,String action){

		if(taskSupports.getElementsByTagName("d:"+action).item(0).getTextContent().equals("true")){

			if(!ServicesUtil.isEmpty(detailDto.getActionList()))
				detailDto.setActionList(detailDto.getActionList()+","+action);
			else{
				detailDto.setActionList(action);	
			}
		}
		return detailDto;
	}
	
	private static WorKBoxDetailDto convertToCustomAttributeDto(NodeList customList, WorKBoxDetailDto detailDto){
		
		for (int j = 0 ; j < customList.getLength() ; j++) {
			if(customList.item(j).getNodeName().equals("entry")){
				Element eEle = (Element) customList.item(j);
				Element mproperties = (Element) eEle.getElementsByTagName("m:properties").item(0);

				String name = mproperties.getElementsByTagName("d:Name").item(0).getTextContent();
				String value = mproperties.getElementsByTagName("d:Value").item(0).getTextContent();

				if(!ServicesUtil.isEmpty(name) && !ServicesUtil.isEmpty(value)){
					if(name.equals("MasterDataCollection")){
						detailDto.setMasterDataCollection(value);
					}
					else if(name.equals("HeaderCollection")){
						detailDto.setHeaderCollection(value);
					}
					else if(name.equals("HeaderInformationDetail")){
						detailDto.setHeaderInformationDetail(value);
					}
					else if(name.equals("MoreDetailInformation")){
						detailDto.setMoreDetailInformation(value);
					}
					else if(name.equals("ItemAttachmentCollection")){
						detailDto.setItemAttachmentCollection(value);
					}
					else if(name.equals("ItemCollectionData")){
						detailDto.getItemCollectionData().add(value);
					}
					else if(name.equals("ItemCollectionHeader")){
						detailDto.setItemCollectionHeader(value);
					}
					else if(name.equals("AdditionalTabHeaderNote")){
						detailDto.setAdditionalTabHeaderNote(value);
					}
					else if(name.equals("informationTabContentTop")){
						detailDto.setInformationTabContentTop(value);
					}
				}
			}
		}
		return detailDto;
	}




}
