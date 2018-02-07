package oneapp.incture.workbox.pmc.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CollaborationResponseDto {
	
	private ResponseMessage responseMessage;
	private List<CollaborationMessagesDto> responseDtos;
	
	public ResponseMessage getResponseMessage() {
		return responseMessage;
	}
	public void setResponseMessage(ResponseMessage responseMessage) {
		this.responseMessage = responseMessage;
	}
	
	public List<CollaborationMessagesDto> getResponseDtos() {
		return responseDtos;
	}
	public void setResponseDtos(List<CollaborationMessagesDto> responseDtos) {
		this.responseDtos = responseDtos;
	}
	@Override
	public String toString() {
		return "CollaborationMessageDto [responseMessage=" + responseMessage + ", responseDtos=" + responseDtos + "]";
	}
	
	
	
	
	
}
