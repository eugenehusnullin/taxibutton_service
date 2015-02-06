package tb2014.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import tb2014.domain.SmsMethod;
import tb2014.utils.Sms48;
import tb2014.utils.SmsSelf;

@Service
public class SmsService {
	@Autowired
	private Sms48 sms48;
	@Autowired
	private SmsSelf smsSelf;

	@Value("#{mainSettings['device.registration.smsmethod']}")
	private String smsMethodSetting;

	public void sendMessage(String phoneNumber, String message) {
		SmsMethod smsMethod = SmsMethod.valueOf(smsMethodSetting);

		switch (smsMethod) {
		case self:
			smsSelf.send(phoneNumber, message);
			break;

		case sms48:
			sms48.send(phoneNumber, message);
			break;

		default:
			break;
		}
	}

}
