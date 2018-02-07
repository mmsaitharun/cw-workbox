package oneapp.incture.workbox.pmc.dto;

import java.util.List;

public class RuleManagementRequestDto {

	private List<RuleManagementDto> dtoList;

	public List<RuleManagementDto> getDtoList() {
		return dtoList;
	}

	public void setDtoList(List<RuleManagementDto> dtoList) {
		this.dtoList = dtoList;
	}

	@Override
	public String toString() {
		return "RuleManagementRequestDto [dtoList=" + dtoList + "]";
	}

}
