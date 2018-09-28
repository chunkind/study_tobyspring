package springbook.user.service;

import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

//new : 발송 테스트용 스텁 오브잭트
public class DummyMailSender implements MailSender{

	@Override
	public void send(SimpleMailMessage arg0) throws MailException {
		
	}

	@Override
	public void send(SimpleMailMessage[] arg0) throws MailException {
		
	}
	
}
