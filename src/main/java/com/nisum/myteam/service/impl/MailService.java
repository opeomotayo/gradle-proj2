/**
 * 
 */
package com.nisum.myteam.service.impl;

import com.nisum.myteam.model.EmailDomain;
import com.nisum.myteam.model.Mail;
import com.nisum.myteam.model.vo.LoginDetailsVO;
import com.nisum.myteam.service.IMailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.activation.DataHandler;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.servlet.ServletContext;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author nisum
 *
 */
@Service
@Slf4j
public class MailService implements IMailService {

	@Autowired
	JavaMailSender emailSender;

	@Autowired
	ServletContext servletContext;
	
	@Autowired
	ResourceLoader resourceLoader;


	@Autowired
	private Environment environment;


	@Override
	public String sendEmailWithAttachment(EmailDomain emailObj) {
		String response = "Success";
		MimeMessage msg = emailSender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(msg, true);
			helper.setTo(emailObj.getToEmail());
			helper.setCc(emailObj.getCcEmail());
			String fromDate = emailObj.getFromDate();
			String toDate = emailObj.getToDate();
			String subject = "";
			String mailContent = "";
			String empId = emailObj.getEmpId();
			if("".equals(empId)){
				empId = "0";
				subject = "All employees - Login hours Report for the period: "+fromDate+" to "+toDate;
				mailContent = "Hi,\n PFA for All employees login hours report for the period: " + fromDate + " to "+ toDate;
			}else{
				subject = empId+ " - Login hours Report for the period: "+fromDate+" to "+toDate;
				mailContent = "Hi,\n PFA for Employee ID: "+empId+" login hours report for the period: " + fromDate + " to "+ toDate;
			}
			helper.setSubject(subject);
			helper.setText(mailContent);
			String fileName = empId + "_" + fromDate + "_" + toDate+".pdf";
			File file = resourceLoader.getResource("/WEB-INF/reports/" + fileName).getFile();
			FileSystemResource fileSystem = new FileSystemResource(file);
			helper.addAttachment(fileSystem.getFilename(), fileSystem);
			helper.setSentDate(new Date());
			emailSender.send(msg);
		} catch (MessagingException e) {
			response = "Mail sending failed due to " + e.getMessage();
			log.error("Mail sending failed due to: ", e);
		} catch (IOException e) {
			response = "Mail sending failed due to " + e.getMessage();
			log.error("Mail sending failed due to: ", e);
		}
		return response;
	}


	public void sendLeaveNotification(Mail mail) throws MessagingException, IOException {
		MimeMessage message = emailSender.createMimeMessage();
		//MimeMessageHelper helper = new MimeMessageHelper(message,MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,StandardCharsets.UTF_8.name());
		//Context context = new Context();
		//context.setVariables(mail.getModel());
		//String html = templateEngine.process(environment.getProperty("email.leave.notification.template.name") , context);


		MimeMessageHelper helper = new MimeMessageHelper(message);
		File file = ResourceUtils.getFile("classpath:" + environment.getProperty("email.leave.notification.template.file.path"));

		//Read File Content
		String content = new String(Files.readAllBytes(file.toPath()));
		content = content.replaceAll("employeeName", mail.getEmpName());


		helper.setTo(mail.getTo());
		helper.setText(content, true);
		helper.setSubject(mail.getSubject());
		helper.setFrom(mail.getFrom());
		if (mail.getCc() != null) {
			helper.setCc(mail.getCc());//helper.setBcc(mail.getBcc());//log.info("Mail Content::"+content);//log.info("The Mail Object::"+mail);
		}

		emailSender.send(message);
	}

	public void sendProjectNotification(Mail mail) throws MessagingException, IOException
	{
		MimeMessage message = emailSender.createMimeMessage();
		//MimeMessageHelper helper = new MimeMessageHelper(message,MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,StandardCharsets.UTF_8.name());
		//Context context = new Context();
		//context.setVariables(mail.getModel());
		//String html = templateEngine.process(environment.getProperty("email.leave.notification.template.name") , context);


		MimeMessageHelper helper = new MimeMessageHelper(message);
		File file = ResourceUtils.getFile("classpath:"+environment.getProperty("email.project.notification.template.file.path"));

		//Read File Content
		String content = new String(Files.readAllBytes(file.toPath()));
		log.info("EmployeeName:::::"+mail.getEmpName());
		content=content.replaceAll("employeeName",mail.getEmpName());
		log.info(content);

		helper.setTo(mail.getTo());
		helper.setText(content, true);
		helper.setSubject(mail.getSubject());
		helper.setFrom(mail.getFrom());
		helper.setCc(mail.getCc());
		//helper.setBcc(mail.getBcc());

		emailSender.send(message);
	}
	
	public void sendWorkAnniversaryNotification(Mail mail) throws IOException, MessagingException {
		
		MimeMessage message = emailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message,true); 
		MimeMultipart multipart = new MimeMultipart("related");
		MimeBodyPart messageBodyPart = new MimeBodyPart();
		String htmlText = "<img src=\"cid:HappyWorkAnniversary.png\">";
        messageBodyPart.setContent(htmlText, "text/html");
        multipart.addBodyPart(messageBodyPart);
        messageBodyPart = new MimeBodyPart();
       
		InputStream targetStream = servletContext.getResourceAsStream("/WEB-INF/images/HappyWorkAnniversary.png");
		ByteArrayDataSource ds = new ByteArrayDataSource(targetStream, "image/png");
		messageBodyPart.setDataHandler(new DataHandler(ds));
		messageBodyPart.setHeader("Content-ID", "<HappyWorkAnniversary.png>");
		messageBodyPart.setDisposition(MimeBodyPart.INLINE);
		messageBodyPart.setFileName("HappyWorkAnniversary.png");
		multipart.addBodyPart(messageBodyPart);
		message.setContent(multipart);
         
		helper.setTo(mail.getTo());
		helper.setSubject(mail.getSubject());
		helper.setFrom(mail.getFrom());
		helper.setCc(mail.getCc());
		//helper.setBcc(mail.getBcc());

		emailSender.send(message);
	}

	@Override
	public void sendExemptHoursEmployeDetailsToLeads(Mail mail, List<LoginDetailsVO> hoursExemptEmployeeList) throws MessagingException, IOException {
		if(Optional.ofNullable(hoursExemptEmployeeList).isPresent() ) {
			MimeMessage message = emailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			StringBuilder exemptEmployeeRowsData = new StringBuilder();
			hoursExemptEmployeeList.stream().forEach(e -> {
				exemptEmployeeRowsData.append("<tr><td style = \"padding: 10px;\">");
				exemptEmployeeRowsData.append(e.getEmployeeId());
				exemptEmployeeRowsData.append("</td><td style = \"padding: 10px;\">");
				exemptEmployeeRowsData.append(e.getEmployeeName());
				exemptEmployeeRowsData.append("</td><td style = \"padding: 10px;\">");
				exemptEmployeeRowsData.append(e.getAvgHours());
				exemptEmployeeRowsData.append("</td><td style = \"padding: 10px;\">");
				exemptEmployeeRowsData.append(e.getFunctionalGroup());
				exemptEmployeeRowsData.append("</td><td style = \"padding: 10px;\">");
				exemptEmployeeRowsData.append(e.getDeliverManager());
				exemptEmployeeRowsData.append("</td><td style = \"padding: 10px;\">");
				exemptEmployeeRowsData.append(e.getOrphanLogin());
				exemptEmployeeRowsData.append("</td>");
			});
			File file = ResourceUtils.getFile("classpath:" + environment.getProperty("email.exempt.hours.employeelist.template.file.path"));

			//Read File Content
			String content = new String(Files.readAllBytes(file.toPath()));
			content = content.replaceAll("employeeList", exemptEmployeeRowsData.toString());
			helper.setTo(mail.getTo());
			helper.setText(content, true);
			helper.setSubject(mail.getSubject());
			helper.setFrom(mail.getFrom());
			helper.setCc(mail.getCc());
			helper.setBcc(mail.getBcc());
			//helper.setBcc(mail.getBcc());
			if (exemptEmployeeRowsData.toString() != null && !exemptEmployeeRowsData.toString().isEmpty()) {
				emailSender.send(message);
			}
		}
	}

}
