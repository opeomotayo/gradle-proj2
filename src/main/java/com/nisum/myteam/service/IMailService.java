/**
 * 
 */
package com.nisum.myteam.service;

import com.nisum.myteam.model.EmailDomain;
import com.nisum.myteam.model.Mail;
import com.nisum.myteam.model.vo.LoginDetailsVO;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

/**
 * @author nisum
 *
 */
public interface IMailService {
	
	public String sendEmailWithAttachment(EmailDomain emailObj);

	public void sendLeaveNotification(Mail mail)throws MessagingException, IOException;

	public void sendProjectNotification(Mail mail)throws MessagingException, IOException;
	
	public void sendWorkAnniversaryNotification(Mail mail)throws MessagingException, IOException;

	public void sendExemptHoursEmployeDetailsToLeads(Mail mail, List<LoginDetailsVO> hoursExemptEmployeeList) throws MessagingException, IOException;

}
