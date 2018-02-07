package oneapp.incture.workbox.pmc.services;

import javax.ejb.Local;

import oneapp.incture.workbox.pmc.dto.CollaborationDto;
import oneapp.incture.workbox.pmc.dto.CollaborationResponseDto;
import oneapp.incture.workbox.pmc.dto.NotificationResponseDto;
import oneapp.incture.workbox.pmc.dto.ResponseMessage;

@Local
public interface CollaborationFacadeLocal {

	public ResponseMessage createCollaboration(CollaborationDto dto);

	public CollaborationResponseDto getMessageDetails(String processId, String taskId);

	public CollaborationResponseDto getProcessLevelWithTaskLevelMessage(String processId);

	public CollaborationResponseDto getMessageUsingOwnerId();
	
	public NotificationResponseDto getNotification();
}
