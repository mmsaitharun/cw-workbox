package oneapp.incture.workbox.inbox.services;

import javax.ejb.Local;

import com.incture.pmc.poadapter.services.ResponseDto;
import com.incture.pmc.poadapter.services.SubstitutionRuleDto;

import oneapp.incture.workbox.pmc.dto.responses.SubstitutionResponseDto;
import oneapp.incture.workbox.pmc.dto.responses.UserDtoResponse;

@Local
public interface SubstitutionRuleFacadeLocal {

	ResponseDto createRule(SubstitutionRuleDto ruleDto);

	ResponseDto deleteRule(SubstitutionRuleDto ruleDto);

	ResponseDto updateRule(SubstitutionRuleDto ruleDto);

	SubstitutionResponseDto getActiveRulesBySubstitute(String user);

	SubstitutionResponseDto getActiveRulesBySubstitutedUser(String substitutedUser);

	SubstitutionResponseDto getInactiveRulesBySubstitute(String substitutingUser);

	SubstitutionResponseDto getInactiveRulesBySubstitutedUser(String substitutedUser);

	UserDtoResponse getSubstituteUsers(String substitutedUser);

	UserDtoResponse getSubstitutedUsers(String substitutingUserString);

	SubstitutionResponseDto getRulesBySubstitute(String user);

	SubstitutionResponseDto getRulesBySubstitutedUser(String user);

	SubstitutionResponseDto getAllRulesByUser(String user);

	ResponseDto deleteAndCreate(SubstitutionRuleDto ruleDto);

}
