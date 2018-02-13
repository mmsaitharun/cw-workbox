package oneapp.incture.workbox.pmc.services;

import javax.ejb.Local;

import oneapp.incture.workbox.pmc.dto.CollaborationDto;
import oneapp.incture.workbox.pmc.dto.responses.CollaborationResponseDto;
import oneapp.incture.workbox.pmc.dto.responses.NotificationResponseDto;
import oneapp.incture.workbox.poadapter.dto.ResponseMessage;

@Local
public interface CollaborationFacadeLocal {

	public ResponseMessage createCollaboration(CollaborationDto dto);

	public CollaborationResponseDto getMessageDetails(String processId, String taskId);

	public CollaborationResponseDto getProcessLevelWithTaskLevelMessage(String processId);

	public CollaborationResponseDto getMessageUsingOwnerId();
	
	public NotificationResponseDto getNotification();
}
