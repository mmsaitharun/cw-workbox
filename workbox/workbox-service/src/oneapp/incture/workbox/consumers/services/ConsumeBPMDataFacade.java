package oneapp.incture.workbox.consumers.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import oneapp.incture.workbox.consumers.dao.BPMCustomAttributeDao;
import oneapp.incture.workbox.consumers.dto.BPMCustomAttributeDto;
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
public class ConsumeBPMDataFacade implements ConsumeBPMDataFacadeLocal {

	@EJB
	EntityManagerProviderLocal em;


	public static String USERNAME;
	public static String PASSWORD; 

	public ConsumeBPMDataFacade() {
	}

	@Override
	public ResponseMessage getDataFromOdata(String processor,String scode,String processorDisplay,String status) {
		USERNAME = processor;
		PASSWORD = scode;
		//	System.err.println("[PMC][ConsumeODataFacade][Xpath][Xpath][getDataFromECC] method invoked with [processor]" + processor);
		ResponseMessage responseMessage = new ResponseMessage();
		responseMessage.setMessage("Data Consumed Successfully");
		responseMessage.setStatus("SUCCESS");
		responseMessage.setStatusCode("0");
		try{
			String count =  ODataServicesUtil.getCountInString(processor, scode,PMCConstant.BPM_URL+"TaskCollection/$count?$filter=Status%20ne%20%27"+status+"%27",PMCConstant.APPLICATION_XML);
			if(!ServicesUtil.isEmpty(count)){

				NodeList nodeList = ODataServicesUtil.xPathOdata(PMCConstant.BPM_URL+"TaskCollection?$skip=0&$top="+count+"&$orderby=CreatedOn%20desc&$filter=Status%20ne%20%27"+status+"%27&$expand=Description,CustomAttributeData,UIExecutionLink", PMCConstant.APPLICATION_ATOM_XML, PMCConstant.HTTP_METHOD_GET, "/feed /entry ",USERNAME,PASSWORD);
				if(!ServicesUtil.isEmpty(nodeList)){
					if(nodeList.getLength()>0){
						List<String> instanceList =  new ArrayList<String>();
						int i;
						for (i = 0; i < nodeList.getLength(); i++) {
							Node nNode = nodeList.item(i);
							if (nNode.getNodeType() == Node.ELEMENT_NODE) {
								String returnedValue = convertToDto(nNode,processorDisplay);
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

						if(new TaskOwnersDao(em.getEntityManager()).deleteSubstitutedTasks(processor,instanceList).equals("FAILURE")){
							System.err.println("[PMC][TaskOwnersDao][deleteSubstitutedTasks][failed] ");
						}
					}
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

	private String convertToDto(Node nNode,String processorDisplay) {

		NodeList childNodes = nNode.getChildNodes();

		TaskEventsDto taskDto = new TaskEventsDto();
		TaskOwnersDto ownersDto = new TaskOwnersDto();
		BPMCustomAttributeDto attributeDto = new BPMCustomAttributeDto();
		ProcessEventsDto processDto = new ProcessEventsDto();

		processDto.setProcessId(UUID.randomUUID().toString().replaceAll("-", ""));

		for (int i = 0 ; i < childNodes.getLength() ; i++) {

			Element eElement = (Element)  childNodes.item(i);
			NamedNodeMap mp = eElement.getAttributes();
			Element mproperties = null;

			
			if( !ServicesUtil.isEmpty(mp.getNamedItem("title")) ){

				String title = mp.getNamedItem("title").getTextContent();
				if(title.equals("UIExecutionLink") ||  title.equals("CustomAttributeData") || title.equals("Description") ){
					if(title.equals("UIExecutionLink")){
						mproperties = (Element) eElement.getElementsByTagName("m:properties").item(0);
						taskDto.setDetailUrl(mproperties.getElementsByTagName("d:GUI_Link").item(0).getTextContent());
					}
					else if(title.equals("Description")){
						mproperties = (Element) eElement.getElementsByTagName("m:properties").item(0);
						taskDto.setDescription(mproperties.getElementsByTagName("d:Description").item(0).getTextContent());
					} 
					else if(title.equals("CustomAttributeData")){
						System.err.println("feed = "+eElement.getElementsByTagName("feed") +"-- ");
						if(!ServicesUtil.isEmpty(eElement.getElementsByTagName("feed"))){
						NodeList customList =  eElement.getElementsByTagName("feed").item(0).getChildNodes();
							for (int j = 0 ; j < customList.getLength() ; j++) {
								//System.out.println("nlist.item(j).getNodeName() -- "+nlist.item(j).getNodeName()+"---"+nlist.item(j).getAttributes().getNamedItem("title"));
								if(customList.item(j).getNodeName().equals("entry")){
									Element eEle = (Element) customList.item(j);
									mproperties = (Element) eEle.getElementsByTagName("m:properties").item(0);
									String name = mproperties.getElementsByTagName("d:Name").item(0).getTextContent();
									if(name.equals("invoiceNumber")){
										attributeDto.setInvoiceNo(mproperties.getElementsByTagName("d:Value").item(0).getTextContent());	 
									}
									else if(name.equals("vendorId")){
										attributeDto.setVendorId(mproperties.getElementsByTagName("d:Value").item(0).getTextContent());	 
									}
									else if(name.equals("plant")){
										attributeDto.setPlant(mproperties.getElementsByTagName("d:Value").item(0).getTextContent());	 
									}
									else if(name.equals("material")){
										attributeDto.setMaterial(mproperties.getElementsByTagName("d:Value").item(0).getTextContent());	 
									}
								}
							}
						}
					}
				}
			}
			else if(eElement.getNodeName().equals("content")){

				mproperties = (Element) eElement.getElementsByTagName("m:properties").item(0);
				String eventId =  mproperties.getElementsByTagName("d:InstanceID").item(0).getTextContent();
				taskDto.setEventId(eventId);
				ownersDto.setEventId(eventId);
				attributeDto.setInstanceId(eventId);
				taskDto.setProcessId(processDto.getProcessId());
				attributeDto.setProcessInstanceId(mproperties.getElementsByTagName("d:TaskDefinitionID") == null ? null :mproperties.getElementsByTagName("d:TaskDefinitionID").item(0).getTextContent());
				taskDto.setPriority(mproperties.getElementsByTagName("d:Priority") == null ? null :mproperties.getElementsByTagName("d:Priority").item(0).getTextContent());
				processDto.setName(mproperties.getElementsByTagName("d:TaskDefinitionName") == null ? null :mproperties.getElementsByTagName("d:TaskDefinitionName").item(0).getTextContent());
				taskDto.setName(mproperties.getElementsByTagName("d:TaskDefinitionName") == null ? null :mproperties.getElementsByTagName("d:TaskDefinitionName").item(0).getTextContent());
				taskDto.setSubject(mproperties.getElementsByTagName("d:TaskTitle") == null ? null :mproperties.getElementsByTagName("d:TaskTitle").item(0).getTextContent());
				ownersDto.setIsSubstituted( Boolean.valueOf(mproperties.getElementsByTagName("d:IsSubstituted") == null ? null :mproperties.getElementsByTagName("d:IsSubstituted").item(0).getTextContent()));
				taskDto.setCreatedAt(dateParser(mproperties, "d:CreatedOn"));
				taskDto.setCompletionDeadLine(dateParser(mproperties, "d:CompletionDeadLine"));
				taskDto.setCompletedAt(dateParser(mproperties, "d:CompletedOn"));
				taskDto.setTaskType("Human");
				taskDto.setOrigin("BPM");

			}

		}

		ownersDto.setTaskOwnerDisplayName(processorDisplay);
		ownersDto.setTaskOwner(USERNAME);

		if (!ServicesUtil.isEmpty(taskDto.getStatus()) && taskDto.getStatus().equals("RESERVED")) {
			taskDto.setCurrentProcessor(USERNAME);
			taskDto.setCurrentProcessorDisplayName(processorDisplay);
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
			if(!updateInstance(processDto, taskDto).equals("SUCCESS")){
				return "FAILURE"; 
			}
		} else {
			if(!createInstance(processDto, taskDto,attributeDto).equals("SUCCESS")){
				return "FAILURE";
			}
		}
		if(!saveAndUpdateTaskOwners(ownersDto,taskDto.getStatus()).equals("SUCCESS")){
			return "FAILURE";
		}
		if(ownersDto.getIsSubstituted())
			return taskDto.getEventId();
		else
			return "SUCCESS";

	}

	private Date dateParser(Element mproperties,String key){

		if(!ServicesUtil.isEmpty(mproperties.getElementsByTagName(key)))
			return ServicesUtil.resultTAsDate(mproperties.getElementsByTagName(key).item(0).getTextContent());
		return null;
	}

	private String createInstance(ProcessEventsDto processDto, TaskEventsDto taskDto,BPMCustomAttributeDto attributeDto) {

		if (new ProcessEventsDao(em.getEntityManager()).createProcessInstance(processDto).equals("FAILURE")) {
			return "FAILURE";
		}
		if (new TaskEventsDao(em.getEntityManager()).createTaskInstance(taskDto).equals("FAILURE")) {
			return "FAILURE";
		}

		if (new BPMCustomAttributeDao(em.getEntityManager()).createAttrInstance(attributeDto).equals("FAILURE")) {
			return "FAILURE";
		}
		return "SUCCESS";
	}


	/*   
	 *   This method  updates the instances of the ProcessEventsDto,  TaskEventsDto, TaskCustomAttributeDto
	 *   because they already exists in the db with the particular event id
	 */

	private String updateInstance(ProcessEventsDto processDto, TaskEventsDto taskDto) {

		if (new ProcessEventsDao(em.getEntityManager()).updateProcessInstance(processDto).equals("FAILURE")) {
			return "FAILURE";
		}
		if (new TaskEventsDao(em.getEntityManager()).updateTaskInstance(taskDto).equals("FAILURE")) {
			return "FAILURE";
		}

		return "SUCCESS";
	}

	/*   
	 *   This method  removes any records for which the event id is same but not the task owner  
	 *   updates record for which the event id and the task owner  are same
	 *   else it creates if the record doesnt exists 
	 *   in the Task Owners table 
	 */


	private String saveAndUpdateTaskOwners(TaskOwnersDto dto ,String status ){
		//System.err.println("[PMC][ConsumeODataFacade][Xpath][saveAndUpdateTaskOwners][TaskOwnersDto]"+dto+"[status]"+status);

		TaskOwnersDao dao = new TaskOwnersDao(em.getEntityManager());

		//check Status isSubstituted - add to list 
		//check status reserved - make reserved to ready - make ready to reserved
		// check status ready - make reserved  to ready 

		List<TaskOwnersDo> entities = dao.getOwnerInstances(dto.getEventId());
		boolean isExists = false;
		if(!ServicesUtil.isEmpty(entities)){
			for(TaskOwnersDo entity : entities){
				if(entity.getTaskOwnersDoPK().getEventId().equals(dto.getEventId())&&entity.getTaskOwnersDoPK().getTaskOwner().equals(dto.getTaskOwner())){
					dao.updateTaskOwnerInstance(dto);
					isExists = true;
				}
				else{
					/*if(dao.deleteInstance(entity).equals("FAILURE")){
						return "FAILURE";	
					}*/
					if(status.equals("READY") && dto.getIsProcessed()){
						dto.setIsProcessed(false);
						dao.updateTaskOwnerInstance(dto);
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

	/*@Override
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
	 */

	/*   
	 *   This method  converts the nodes recieved from service which has the details of the user to UserDetailDto  
	 */

	/*	private UserDetailDto convertToUserDto(Node nNode) {
		Element eElement = (Element) nNode;
		Element mproperties = (Element) eElement.getElementsByTagName("m:properties").item(0);
		UserDetailDto userDto = new UserDetailDto();
		userDto.setDepartment(mproperties.getElementsByTagName("d:Department").item(0).getTextContent());
		userDto.setDisplayName(mproperties.getElementsByTagName("d:DisplayName").item(0).getTextContent());
		userDto.setEmail(mproperties.getElementsByTagName("d:Email").item(0).getTextContent());
		userDto.setUniqueName(mproperties.getElementsByTagName("d:UniqueName").item(0).getTextContent());

		return userDto;
	}
	 */

}
