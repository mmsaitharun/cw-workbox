package oneapp.incture.workbox.pmc.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class RuleManagementResponseDto {
	List<RuleManagementDto> ruleManagementDtos;
	List<ActionDto> actionDto;
	List<TaskNameDto> taskList;
	ResponseMessage message;
	
	public List<TaskNameDto> getTaskList() {
		return taskList;
	}

	public void setTaskList(List<TaskNameDto> taskList) {
		this.taskList = taskList;
	}

	public List<ActionDto> getActionDto() {
		return actionDto;
	}

	public void setActionDto(List<ActionDto> actionDto) {
		this.actionDto = actionDto;
	}

	public List<RuleManagementDto> getRuleManagementDtos() {
		return ruleManagementDtos;
	}

	public void setRuleManagementDtos(List<RuleManagementDto> ruleManagementDtos) {
		this.ruleManagementDtos = ruleManagementDtos;
	}

	public ResponseMessage getMessage() {
		return message;
	}

	public void setMessage(ResponseMessage message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "RuleManagementResponseDto [ruleManagementDtos=" + ruleManagementDtos + ", actionDto=" + actionDto
				+ ", taskList=" + taskList + ", message=" + message + "]";
	}


}
