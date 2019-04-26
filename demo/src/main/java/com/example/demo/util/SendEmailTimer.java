package com.example.demo.util;

import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.Timer;
import org.jboss.netty.util.TimerTask;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 发送邮件延迟重试
 * 
 * @author wangbo
 * @date 2018年1月17日
 * @version 1.0
 */
public class SendEmailTimer {

	@Resource
	private EmailSendQueue emailsendQueue;

	private static class WheelTimer {
		private static SendEmailTimer instance = new SendEmailTimer();
	}

	static final Timer timer = new HashedWheelTimer();

	private SendEmailTimer() {
	}

	public static SendEmailTimer getInstance() {
		return WheelTimer.instance;
	}
	
	public void retrySendEmail(final Map<String,Object> map,final String email,final String templatePath,final String title,final int num,final int delayTime){
		if(num > 0){
		    timer.newTimeout(new TimerTask() {
		        public void run(Timeout timeout){
//					System.out.println(map.toString()+"-----"+email+"-----"+templatePath+"======"+title+"===="+num+"===="+delayTime+"====");
//					EmailSendQueue  emailSendQueue = new EmailSendQueue();
//                    EmailSendQueue.Entity entity1 = emailsendQueue.new Entity(map, email, templatePath, title, num, delayTime);

                    EmailSendQueue.Entity entity = emailsendQueue.new Entity(map,email,templatePath,title,num,delayTime);
					EmailSendQueue.addEntity(entity);
		        }
		    }, delayTime, TimeUnit.SECONDS);
		}
	}



}