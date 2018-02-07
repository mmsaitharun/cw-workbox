package oneapp.incture.workbox.pmc.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TrackingResponse {
	
	private List<TasksCountDTO> tasksCount;
	private ResponseMessage responseMessage;
	
	public List<TasksCountDTO> getTasksCount() {
		return tasksCount;
	}
	public void setTasksCount(List<TasksCountDTO> tasksCount) {
		this.tasksCount = tasksCount;
	}
	public ResponseMessage getResponseMessage() {
		return responseMessage;
	}
	public void setResponseMessage(ResponseMessage responseMessage) {
		this.responseMessage = responseMessage;
	}
	
	
	
}
