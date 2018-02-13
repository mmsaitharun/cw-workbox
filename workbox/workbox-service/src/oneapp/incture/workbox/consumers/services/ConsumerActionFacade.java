package oneapp.incture.workbox.consumers.services;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.ejb.Stateless;

import oneapp.incture.workbox.consumers.dto.ActionsRequestDto;
import oneapp.incture.workbox.consumers.dto.WorkboxRequestDto;
import oneapp.incture.workbox.consumers.util.ConnectionsUtil;
import oneapp.incture.workbox.consumers.util.ResponseDto;
import oneapp.incture.workbox.util.ServicesUtil;

/**
 * Session Bean implementation class ConsumeODataFacade
 */
@Stateless
public class ConsumerActionFacade implements ConsumerActionFacadeLocal {
	
	
	public ConsumerActionFacade() {
	}
	
	
	
	@Override
	public ResponseDto executeAction(WorkboxRequestDto requestDto){
		
		System.err.println("[PMC][ConsumerActionFacade][actions][executeAction][init] " +requestDto);
		ResponseDto returnMessage = new ResponseDto();
		if( requestDto.getTaskType().equals("AddComment")){
			returnMessage.setMessage("Failed to Add Comment");	
		}else{
			returnMessage.setMessage("Failed to "+ requestDto.getTaskType());
		}
		returnMessage.setStatus("FAILURE");
		returnMessage.setStatusCode("1");
		if(!ServicesUtil.isEmpty(requestDto.getUserId()) && !ServicesUtil.isEmpty(requestDto.getScode()))
		{
			if(!ServicesUtil.isEmpty(requestDto.getSapOrigin()) && !ServicesUtil.isEmpty(requestDto.getInstanceId())&& !ServicesUtil.isEmpty(requestDto.getTaskType())){

				String serviceBase = "http://sthcigwdq1.kaust.edu.sa:8005/sap/opu/odata/IWPGW/TASKPROCESSING;mo;v=2/"+requestDto.getTaskType()+"?";
				String	serviceUri = "SAP__Origin='"+requestDto.getSapOrigin()+"'&InstanceID='"+requestDto.getInstanceId()+"'";
				try {
					if(!ServicesUtil.isEmpty(requestDto.getDecisionKey())){
						serviceUri=serviceUri+	"&DecisionKey='"+requestDto.getDecisionKey()+"'";
					}
					if(!ServicesUtil.isEmpty(requestDto.getForwardTo())){
						serviceUri=serviceUri+	"&ForwardTo='"+requestDto.getForwardTo()+"'";
					}
					if(!ServicesUtil.isEmpty(requestDto.getText())){
						serviceUri=serviceUri+	"&Text='"+ URLEncoder.encode(requestDto.getText() , "UTF-8")+"'";
					}

					if(!ServicesUtil.isEmpty(requestDto.getActions())){
						serviceUri = serviceUri+"&sap-client=260&Comments='";
						if(!ServicesUtil.isEmpty(requestDto.getComments())){
							serviceUri = serviceUri+ URLEncoder.encode(requestDto.getComments() , "UTF-8"); 
						}
						serviceUri = serviceUri+"%20*AllItems*%20";
						for(ActionsRequestDto dto : requestDto.getActions()){
							serviceUri = serviceUri+"ItemNo%20eq%20"+dto.getItemNo()+"%20and%20Accept/Reject%20eq%20"; 
							if(dto.getAction().equals("APPROVE")){
								serviceUri = serviceUri+"TRUE";	
							}
							else{
								serviceUri = serviceUri+"FALSE";
							}
							serviceUri = serviceUri+",";
						}
						serviceUri = serviceUri.substring(0,serviceUri.length()-1);
						serviceUri = serviceUri+"'";
					}
					if(!ServicesUtil.isEmpty(requestDto.getComments()) && ServicesUtil.isEmpty(requestDto.getActions())){
						serviceUri=serviceUri+	"&Comments='"+URLEncoder.encode(requestDto.getComments() , "UTF-8")+"'";
					}

				} catch (UnsupportedEncodingException e1) {
					System.err.println("[PMC][ODataServicesUtil][actions][executeAction][error][URLEncoder] " +e1.getMessage());
					returnMessage.setMessage("URL Encoding Failed");
					return returnMessage;
				}
				serviceUri = serviceBase +serviceUri;
				System.err.println("[PMC][ODataServicesUtil][Xpath][actions][username]"+requestDto.getUserId()+"[serviceUri] "+serviceUri);

				try {
					returnMessage = ConnectionsUtil.executeActionHttp(serviceUri,requestDto.getUserId(),requestDto.getScode());
					if( requestDto.getTaskType().equals("AddComment")){
						requestDto.setTaskType("Add Comment");
					}
					if(returnMessage.getStatus().equals("SUCCESS")){
						returnMessage.setMessage( requestDto.getTaskType() + " Successful");
					}
					else if(returnMessage.getStatus().equals("FAILURE") && ServicesUtil.isEmpty(returnMessage.getMessage()) ){
						returnMessage.setMessage( requestDto.getTaskType() + " Failed");
					}
				} catch (IOException e) {
					returnMessage.setMessage(e.getMessage());
				}
			}
			else{

				returnMessage.setMessage("BAD REQUEST");
				System.err.println("[PMC][ODataServicesUtil][actions][executeAction][exit] " +returnMessage);
				return returnMessage;
			}
		}
		else {
			returnMessage.setMessage("AUTHORISATION FAILED");
			System.err.println("[PMC][ODataServicesUtil][actions][executeAction][exit] " +returnMessage);
			return returnMessage;
		}

		System.err.println("[PMC][ODataServicesUtil][actions][executeAction][exit] " +returnMessage);
		return returnMessage;
	}
	
}
