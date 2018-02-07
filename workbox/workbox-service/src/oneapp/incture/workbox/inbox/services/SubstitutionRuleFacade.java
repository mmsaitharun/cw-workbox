package oneapp.incture.workbox.inbox.services;

import javax.ejb.Stateless;

import com.incture.pmc.poadapter.services.ResponseDto;
import com.incture.pmc.poadapter.services.SubstitutionRuleDto;

import oneapp.incture.workbox.pmc.dto.responses.SubstitutionResponseDto;
import oneapp.incture.workbox.pmc.dto.responses.UserDtoResponse;
import oneapp.incture.workbox.pmc.wsdlconsumers.SubstitutionManagementConsumer;

/**
 * Session Bean implementation class SubstitutionRuleFacadeWsdlConsumer
 */
@Stateless
public class SubstitutionRuleFacade implements SubstitutionRuleFacadeLocal {


	public SubstitutionRuleFacade() {
	}

	SubstitutionManagementConsumer consumer = new SubstitutionManagementConsumer();

	@Override
	public ResponseDto createRule(SubstitutionRuleDto ruleDto){
		return consumer.createRule(ruleDto);
	}

	@Override
	public ResponseDto deleteRule(SubstitutionRuleDto ruleDto){
		return consumer.deleteRule(ruleDto);
	}

	@Override
	public ResponseDto updateRule(SubstitutionRuleDto ruleDto){
		return consumer.updateRule(ruleDto);
	}

	@Override
	public SubstitutionResponseDto getActiveRulesBySubstitute(String  substitutingUser){
		return consumer.getActiveRulesBySubstitute(substitutingUser);
	}

	@Override
	public SubstitutionResponseDto getActiveRulesBySubstitutedUser(String  substitutedUser){
		return consumer.getActiveRulesBySubstitutedUser(substitutedUser);
	}
	@Override
	public SubstitutionResponseDto getInactiveRulesBySubstitute(String  substitutingUser){
		return consumer.getInactiveRulesBySubstitute(substitutingUser);
	}

	@Override
	public SubstitutionResponseDto getInactiveRulesBySubstitutedUser(String  substitutedUser){
		return consumer.getInactiveRulesBySubstitutedUser(substitutedUser);
	}

	@Override
	public SubstitutionResponseDto getRulesBySubstitute(String  user){
		return consumer.getRulesBySubstitute(user);
	}

	@Override
	public SubstitutionResponseDto getRulesBySubstitutedUser(String  user){
		return consumer.getRulesBySubstitutedUser(user);
	}

	@Override
	public UserDtoResponse getSubstituteUsers(String  substitutedUser){
		return consumer.getSubstituteUsers(substitutedUser);
	}

	@Override
	public UserDtoResponse getSubstitutedUsers(String  substitutingUserString){
		return consumer.getSubstitutedUsers(substitutingUserString);
	}

	@Override
	public SubstitutionResponseDto getAllRulesByUser(String  user){
		return consumer.getAllRulesByUser(user);
	}

	@Override
	public ResponseDto deleteAndCreate(SubstitutionRuleDto ruleDto){
		return consumer.deleteAndCreate(ruleDto);
	}

}
