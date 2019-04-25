package com.example.demo.util;

import com.sun.mail.util.MailSSLSocketFactory;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.mail.Authenticator;
import javax.mail.Message.RecipientType;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class EmailSendQueue {
	private static LinkedBlockingQueue<Entity> queue = new LinkedBlockingQueue();;
	private static ExecutorService es = Executors.newCachedThreadPool();
	@Value("${mail.smtp.host}")
	private String host;
	@Value("${mail.smtp.port}")
	private String port;
	@Value("${mail.user}")
	private String user;
	@Value("${mail.password}")
	private String password;
	/**
	 * 
	 */
	private EmailSendQueue() {
		super();
	}

	public static void addEntity(Entity entity) {
		try {
			queue.put(entity);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	static {
		queue = new LinkedBlockingQueue();
	}

	void initQueue() throws InterruptedException {
		generateThreadPool();
	}

	void destroyQueue() throws InterruptedException {
		es.shutdown();
	}

	void generateThreadPool() {
		Consumer[] consumers = new Consumer[3];
		for (int i = 0; i < consumers.length; i++) {
			consumers[i] = new Consumer();
			es.submit(consumers[i]);
		}
	}

	class Consumer implements Runnable {

		@Override
		public void run() {
			while (true) {
				Entity entity = null;
				try {

					entity = queue.take();
//					System.out.println(1/0);
					sendEmail(entity.map, entity.mail, entity.templatePath, entity.title);

				} catch (Exception e) {
					SendEmailTimer.getInstance().retrySendEmail(entity.map, entity.mail, entity.templatePath, entity.title,entity.num-1,entity.delayTime);
					e.printStackTrace();
				}
			}
		}

	}

	public class Entity {
		@Value("${email.send.retry.num}")
		int num ;
		@Value("${email.send.retry.delayTime}")
		int delayTime;
		final Map<String, Object> map;
		final String mail;
		final String templatePath;
		final String title;
		/**
		 * @param map
		 * @param mail
		 * @param templatePath
		 * @param title
		 * @param num
		 * @param delayTime
		 */
		public Entity( Map<String, Object> map, String mail,
				String templatePath, String title, int num, int delayTime) {
			super();
			this.map = map;
			this.mail = mail;
			this.templatePath = templatePath;
			this.title = title;
			this.num = num;
			this.delayTime = delayTime;
		}
		public Entity( Map<String, Object> map, String mail,
				String templatePath, String title){
			this.map = map;
			this.mail = mail;
			this.templatePath = templatePath;
			this.title = title;
		}

	}

//	static ResourceBundle rb = ResourceBundle.getBundle("config");

	private static Configuration configuration = new Configuration(
			Configuration.VERSION_2_3_23);

	public void sendEmail(Map<String, Object> map, String email,String templatePath, String title,
			int num, int delayTime) throws Exception {
			sendEmail(map, email, templatePath, title);
	}

	/**
	 * 发送邮件模板
	 * 
	 * @param map
	 *            发送的数据
	 * @param email
	 *            接收人
	 * @param templatePath
	 *            freemarker模板id
	 * @throws UnsupportedEncodingException 
	 * @throws AddressException 
	 * @throws Exception
	 * @title title 动态标题
	 */
	public void sendEmail(Map<String, Object> map, String email,
			String templatePath, String title) throws Exception {
		final Properties props = new Properties();
		// 表示SMTP发送邮件，需要进行身份验证
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.host", host);
		// 端口
		props.put("mail.smtp.port", port);
		// 发件人的账号
		props.put("mail.user", user);
		// 访问SMTP服务时需要提供的密码
		props.put("mail.password", password);
		// 开启安全协议
		MailSSLSocketFactory sf = null;
		try {
			sf = new MailSSLSocketFactory();
			sf.setTrustAllHosts(true);
		} catch (GeneralSecurityException e1) {
			e1.printStackTrace();
		}

		props.put("mail.smtp.ssl.enable", "true");
		props.put("mail.smtp.ssl.socketFactory", sf);

		// 构建授权信息，用于进行SMTP进行身份验证
		Authenticator authenticator = new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				// 用户名、密码
				String userName = props.getProperty("mail.user");
				String password = props.getProperty("mail.password");
				return new PasswordAuthentication(userName, password);
			}
		};
		// 使用环境属性和授权信息，创建邮件会话
		Session mailSession = Session.getInstance(props, authenticator);
		// 创建邮件消息
		MimeMessage msg = new MimeMessage(mailSession);
		// 设置发件人
		InternetAddress form = new InternetAddress(
				props.getProperty("mail.user"), "票加加");
		msg.setFrom(form);
		// 设置收件人
		InternetAddress to = new InternetAddress(email);
		msg.setRecipient(RecipientType.TO, to);
		// 设置邮件标题
		msg.setSubject(title);
		map.put("title", title);
		configuration.setClassForTemplateLoading(this.getClass(), "/template/mail/");
		Template tpl = configuration.getTemplate(templatePath);

		// Template tpl =
		// freemarkerConfigurer.getConfiguration().getTemplate(templatePath);
		String htmlText = FreeMarkerTemplateUtils.processTemplateIntoString(
				tpl, map);
		// 设置邮件的内容体
		msg.setContent(htmlText, "text/html;charset=UTF-8");
		// 发送邮件
		Transport.send(msg);
	}

}
